/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2022 Daniel Dreibrodt
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

/**
 * Converts files between different property list formats.
 *
 * @author Daniel Dreibrodt
 */
public final class PropertyListConverter {
    /**
     * Prevents instantiation.
     */
    private PropertyListConverter() {
        /* empty */
    }

    /**
     * Converts a given property list file into ASCII format.
     *
     * @param in  The source file.
     * @param out The target file. If the file's parent directory does not exist, it will be created.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     */
    public static void convertToASCII(File in, File out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        NSObject root = PropertyListParser.parse(in);
        if (root instanceof NSDictionary) {
            ASCIIPropertyListWriter.write((NSDictionary) root, out);
        } else if (root instanceof NSArray) {
            ASCIIPropertyListWriter.write((NSArray) root, out);
        } else {
            throw new PropertyListFormatException("The root of the given input property list is neither a Dictionary nor an Array.");
        }
    }

    /**
     * Converts a given property list file into ASCII format.
     *
     * @param in  The source file path.
     * @param out The target file path.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     */
    public static void convertToASCII(Path in, Path out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        NSObject root = PropertyListParser.parse(in);
        if (root instanceof NSDictionary) {
            ASCIIPropertyListWriter.write((NSDictionary) root, out);
        } else if (root instanceof NSArray) {
            ASCIIPropertyListWriter.write((NSArray) root, out);
        } else {
            throw new PropertyListFormatException("The root of the given input property list is neither a Dictionary nor an Array.");
        }
    }

    /**
     * Converts a given property list file into ASCII format.
     *
     * @param in  The source file.
     * @param out The target file. If the file's parent directory does not exist, it will be created.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     */
    public static void convertToGnuStepASCII(File in, File out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        NSObject root = PropertyListParser.parse(in);
        if (root instanceof NSDictionary) {
            ASCIIPropertyListWriter.writeGnuStep((NSDictionary) root, out);
        } else if (root instanceof NSArray) {
            ASCIIPropertyListWriter.writeGnuStep((NSArray) root, out);
        } else {
            throw new PropertyListFormatException("The root of the given input property list is neither a Dictionary nor an Array.");
        }
    }

    /**
     * Converts a given property list file into the OS X and iOS binary format.
     *
     * @param in  The source file.
     * @param out The target file. If the file's parent directory does not exist, it will be created.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     */
    public static void convertToBinary(File in, File out) throws IOException, ParserConfigurationException, ParseException, SAXException, PropertyListFormatException {
        NSObject root = PropertyListParser.parse(in);
        BinaryPropertyListWriter.write(root, out, true);
    }

    /**
     * Converts a given property list file into the OS X and iOS binary format.
     *
     * @param in  The source file path.
     * @param out The target file path.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     */
    public static void convertToBinary(Path in, Path out) throws IOException, ParserConfigurationException, ParseException, SAXException, PropertyListFormatException {
        NSObject root = PropertyListParser.parse(in);
        BinaryPropertyListWriter.write(root, out);
    }

    /**
     * Converts a given property list file into the OS X and iOS XML format.
     *
     * @param in  The source file.
     * @param out The target file. If the file's parent directory does not exist, it will be created.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     */
    public static void convertToXml(File in, File out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        NSObject root = PropertyListParser.parse(in);
        XMLPropertyListWriter.write(root, out);
    }

    /**
     * Converts a given property list file into the OS X and iOS XML format.
     *
     * @param in  The source file path.
     * @param out The target file path.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the input file or writing the output file.
     * @throws org.xml.sax.SAXException                       If any parse error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @throws java.text.ParseException                       If a date string could not be parsed.
     */
    public static void convertToXml(Path in, Path out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        NSObject root = PropertyListParser.parse(in);
        XMLPropertyListWriter.write(root, out);
    }
}
