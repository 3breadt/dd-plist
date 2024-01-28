/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2011 Daniel Dreibrodt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.dd.plist;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

/**
 * Parses property lists. The parser can handle files, input streams and byte arrays.
 * All known property list formats are supported.
 *
 * @author Daniel Dreibrodt
 */
public class PropertyListParser {

    private static final int TYPE_XML = 0;
    private static final int TYPE_BINARY = 1;
    private static final int TYPE_ASCII = 2;
    private static final int TYPE_ERROR_BLANK = 10;
    private static final int TYPE_ERROR_UNKNOWN = 11;

    private static final int READ_BUFFER_LENGTH = 4096;

    /**
     * Prevent instantiation.
     */
    protected PropertyListParser() {
        /* empty */
    }

    /**
     * Determines the property list type by means of the first bytes of its data.
     *
     * @param dataBeginning The very first bytes of data of the property list (minus any whitespace) as a string.
     * @return The type of the property list.
     */
    private static int determineType(String dataBeginning) {
        dataBeginning = dataBeginning.trim();
        if (dataBeginning.length() == 0) {
            return TYPE_ERROR_BLANK;
        }

        if (dataBeginning.startsWith("bplist")) {
            return TYPE_BINARY;
        }

        if (dataBeginning.startsWith("(") || dataBeginning.startsWith("{") || dataBeginning.startsWith("/")) {
            return TYPE_ASCII;
        }

        if (dataBeginning.startsWith("<")) {
            return TYPE_XML;
        }
        return TYPE_ERROR_UNKNOWN;
    }

    /**
     * Determines the property list type by means of the first bytes of its data.
     *
     * @param is     An input stream pointing to the beginning of the property list data.
     *               If the stream supports marking it will be reset to the beginning of the property
     *               list data after the type has been determined.
     * @param offset The number of bytes to skip in the stream.
     * @return The type of the property list.
     */
    private static int determineType(InputStream is, int offset) throws IOException {
        int index = offset;
        int readLimit = index + 1024;
        if (is.markSupported()) {
            is.mark(readLimit);
        }

        is.skip(offset);
        int b;
        ByteOrderMarkReader bomReader = new ByteOrderMarkReader();
        boolean bom = true;
        //Skip any possible whitespace at the beginning of the file
        do {
            if (++index > readLimit) {
                is.reset();
                return determineType(is, readLimit);
            }
            b = is.read();
            //Check if we are reading the Unicode byte order mark (BOM) and skip it
            bom &= bomReader.readByte(b);
        } while (b != -1 && (b == ' ' || b == '\t' || b == '\r' || b == '\n' || b == '\f' || bom));

        if (b == -1) {
            return TYPE_ERROR_BLANK;
        }

        String charset = bomReader.getDetectedCharset();
        if (charset == null) {
            charset = "UTF-8";
        }

        byte[] magicBytes = new byte[8];
        magicBytes[0] = (byte) b;
        int read = is.read(magicBytes, 1, 7);
        if (read == -1) {
            return TYPE_ERROR_UNKNOWN;
        }

        int type = determineType(new String(magicBytes, 0, read, Charset.forName(charset)));
        if (is.markSupported()) {
            is.reset();
        }

        return type;
    }

    /**
     * Reads all bytes from an InputStream and stores them in an array, up to
     * a maximum count.
     *
     * @param in The InputStream pointing to the data that should be stored in the array.
     * @return An array containing all bytes that were read from the input stream.
     * @throws java.io.IOException If an I/O error occurs while reading from the input stream.
     */
    protected static byte[] readAll(InputStream in) throws IOException {
        Objects.requireNonNull(in, "The specified input stream is null");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[READ_BUFFER_LENGTH];
        int read;
        while ((read = in.read(buf, 0, READ_BUFFER_LENGTH)) != -1) {
            outputStream.write(buf, 0, read);
        }

        return outputStream.toByteArray();
    }

    /**
     * Parses a property list from a file.
     *
     * @param filePath The path to the property list file.
     * @return The root object in the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If the given property list has an invalid format.
     */
    public static NSObject parse(String filePath) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        return parse(FileSystems.getDefault().getPath(filePath));
    }

    /**
     * Parses a property list from a file.
     *
     * @param f The property list file.
     * @return The root object in the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If the given property list has an invalid format.
     */
    public static NSObject parse(File f) throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
        return parse(f.toPath());
    }

    /**
     * Parses a property list from a file.
     *
     * @param path The path to the property list file.
     * @return The root object in the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If the given property list has an invalid format.
     */
    public static NSObject parse(Path path) throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
        try (InputStream fileInputStream = Files.newInputStream(path)) {
            return parse(fileInputStream);
        }
    }

    /**
     * Parses a property list from a byte array.
     *
     * @param bytes The property list data as a byte array.
     * @return The root object in the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the byte array.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If the given property list has an invalid format.
     */
    public static NSObject parse(byte[] bytes) throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
        return parse(new ByteArrayInputStream(bytes));
    }
    
    /**
     * Traverse hierarchy and set parents.
     */
    private static void setParents(NSObject parent) {
        if (parent instanceof NSArray) {
            NSArray a = (NSArray)parent;
            
            for (NSObject o: a.getArray()) {
                o.setParent(parent);
                setParents(o);
            }
        } else if (parent instanceof NSDictionary) {
            NSDictionary d = (NSDictionary)parent;
            
            for (Map.Entry<String, NSObject> entry: d.entrySet()) {
                entry.getValue().setParent(parent);
                setParents(entry.getValue());
            }
        } else if (parent instanceof NSSet) {
            NSSet s = (NSSet)parent;
            
            for (NSObject o: s.allObjects()) {
                o.setParent(parent);
                setParents(o);
            }
        } else if (parent instanceof NSString) {
            // this parent is a leaf. Nothing else to traverse.
        } else if (parent instanceof NSDate) {
            // this parent is a leaf. Nothing else to traverse.
        } else if (parent instanceof NSData) {
            // this parent is a leaf. Nothing else to traverse.
        } else if (parent instanceof NSNumber) {
            // this parent is a leaf. Nothing else to traverse.
        } else if (parent instanceof NSNull) {
            // this parent is a leaf. Nothing else to traverse.
        } else {
            throw new IllegalArgumentException("Unknown node " + parent + "(" + parent.getClass().getName() + ")");
        }
    }

    /**
     * Parses a property list from an InputStream.
     * This method does not close the specified input stream.
     *
     * @param is The InputStream delivering the property list data.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input stream.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If the given property list has an invalid format.
     */
    public static NSObject parse(InputStream is) throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }

        NSObject result = null;
        
        switch (determineType(is, 0)) {
            case TYPE_BINARY:
                result = BinaryPropertyListParser.parse(is);
                break;
            case TYPE_XML:
                result = XMLPropertyListParser.parse(is);
                break;
            case TYPE_ASCII:
                result = ASCIIPropertyListParser.parse(is);
                break;
            case TYPE_ERROR_BLANK:
                result = null;
                break;
            default:
                throw new PropertyListFormatException("The given data is not a property list of a supported format.");
        }
        
        if (result != null) {
            setParents(result);
        }
        
        return result;
    }

    /**
     * Saves a property list with the given object as root into an XML file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     * @deprecated Use {@link XMLPropertyListWriter} instead.
     */
    @Deprecated()
    public static void saveAsXML(NSObject root, File out) throws IOException {
        XMLPropertyListWriter.write(root, out);
    }

    /**
     * Saves a property list with the given object as root in XML format into an output stream.
     * This method does not close the specified output stream.
     *
     * @param root The root object.
     * @param out  The output stream.
     * @throws IOException If an error occurs during the writing process.
     * @deprecated Use {@link XMLPropertyListWriter} instead.
     */
    @Deprecated()
    public static void saveAsXML(NSObject root, OutputStream out) throws IOException {
        XMLPropertyListWriter.write(root, out);
    }

    /**
     * Converts a given property list file into the OS X and iOS XML format.
     *
     * @param in  The source file.
     * @param out The target file. If the output file's parent directory does not exist, it will be created.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     * @deprecated Use {@link PropertyListConverter} instead.
     */
    @Deprecated
    public static void convertToXml(File in, File out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        PropertyListConverter.convertToXml(in, out);
    }

    /**
     * Saves a property list with the given object as root into a binary file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     * @deprecated Use {@link BinaryPropertyListWriter} instead.
     */
    public static void saveAsBinary(NSObject root, File out) throws IOException {
        BinaryPropertyListWriter.write(root, out, true);
    }

    /**
     * Saves a property list with the given object as root in binary format into an output stream.
     * This method does not close the specified input stream.
     *
     * @param root The root object.
     * @param out  The output stream.
     * @throws IOException If an error occurs during the writing process.
     * @deprecated Use {@link BinaryPropertyListWriter} instead.
     */
    @Deprecated
    public static void saveAsBinary(NSObject root, OutputStream out) throws IOException {
        BinaryPropertyListWriter.write(root, out);
    }

    /**
     * Converts a given property list file into the OS X and iOS binary format.
     *
     * @param in  The source file.
     * @param out The target file. If the output file's parent directory does not exist, it will be created.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing a XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     * @deprecated Use {@link PropertyListConverter} instead.
     */
    @Deprecated
    public static void convertToBinary(File in, File out) throws IOException, ParserConfigurationException, ParseException, SAXException, PropertyListFormatException {
        PropertyListConverter.convertToBinary(in, out);
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     * @deprecated Use {@link ASCIIPropertyListWriter} instead.
     */
    @Deprecated
    public static void saveAsASCII(NSDictionary root, File out) throws IOException {
        ASCIIPropertyListWriter.write(root, out);
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     * @deprecated Use {@link ASCIIPropertyListWriter} instead.
     */
    @Deprecated
    public static void saveAsASCII(NSArray root, File out) throws IOException {
        ASCIIPropertyListWriter.write(root, out);
    }

    /**
     * Converts a given property list file into ASCII format.
     *
     * @param in  The source file.
     * @param out The target file. If the file's parent directory does not exist, it will be created.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing a XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     * @deprecated Use {@link PropertyListConverter} instead.
     */
    @Deprecated
    public static void convertToASCII(File in, File out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        PropertyListConverter.convertToASCII(in, out);
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     * @deprecated Use {@link ASCIIPropertyListWriter} instead.
     */
    @Deprecated
    public static void saveAsGnuStepASCII(NSDictionary root, File out) throws IOException {
        ASCIIPropertyListWriter.writeGnuStep(root, out);
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     * @deprecated Use {@link ASCIIPropertyListWriter} instead.
     */
    @Deprecated
    public static void saveAsGnuStepASCII(NSArray root, File out) throws IOException {
        ASCIIPropertyListWriter.writeGnuStep(root, out);
    }

    /**
     * Converts a given property list file into ASCII format.
     *
     * @param in  The source file.
     * @param out The target file.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing a XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     * @deprecated Use {@link PropertyListConverter} instead.
     */
    @Deprecated
    public static void convertToGnuStepASCII(File in, File out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        PropertyListConverter.convertToGnuStepASCII(in, out);
    }
}
