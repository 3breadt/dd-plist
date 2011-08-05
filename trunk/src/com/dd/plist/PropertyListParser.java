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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.IOException;

/**
 * Parses any given property list
 * @author Daniel Dreibrodt
 */
public class PropertyListParser {

    /**
     * Reads all bytes from an InputStream and stores them in an array, up to
     * a maximum count.
     * @param in The InputStream
     * @param max The maximum number of bytes to read.
     **/
    static byte[] readAll(InputStream in, int max) throws IOException {
	ByteArrayOutputStream buf = new ByteArrayOutputStream();
	while (max > 0) {
	    int n = in.read();
	    if (n == -1) break; // EOF
	    buf.write(n);
	    max--;
	}
	return buf.toByteArray();
    }

    /**
     * Parses a property list from a file. It can either be in XML or binary format.
     * @param f The property list file
     * @return The root object in the property list
     * @throws Exception If an error occurred while parsing
     */
    public static NSObject parse(File f) throws Exception {
        FileInputStream fis = new FileInputStream(f);
        String magicString = new String(readAll(fis, 8), 0, 8);
        fis.close();
        if (magicString.startsWith("bplist00")) {
            return BinaryPropertyListParser.parse(f);
        } else if (magicString.startsWith("<?xml")) {
            return XMLPropertyListParser.parse(f);
        } else {
            throw new UnsupportedOperationException("The given data is neither a binary nor a XML property list. ASCII property lists are not supported.");
        }
    }

    /**
     * Parses a property list from a byte array. It can either be in XML or binary format.
     * @param bytes The property list data
     * @return The root object in the property list
     * @throws Exception If an error occurred while parsing.
     */
    public static NSObject parse(byte[] bytes) throws Exception {
        String magicString = new String(bytes, 0, 8);
        if (magicString.startsWith("bplist00")) {
            return BinaryPropertyListParser.parse(bytes);
        } else if (magicString.startsWith("<?xml")) {
            return XMLPropertyListParser.parse(bytes);
        } else {
            throw new UnsupportedOperationException("The given data is neither a binary nor a XML property list. ASCII property lists are not supported.");
        }
    }

    /**
     * Parses a property list from an InputStream. It can either be in XML or binary format.
     * @param is The InputStream delivering the property list data
     * @return The root object of the property list
     * @throws Exception If an error occurred while parsing.
     */
    public static NSObject parse(InputStream is) throws Exception {
        if(is.markSupported()) {
            is.mark(10);
            String magicString = new String(readAll(is, 8), 0, 8);
            is.reset();
            if (magicString.startsWith("bplist00")) {
                return BinaryPropertyListParser.parse(is);
            } else if (magicString.startsWith("<?xml")) {
                return XMLPropertyListParser.parse(is);
            } else {
                throw new UnsupportedOperationException("The given data is neither a binary nor a XML property list. ASCII property lists are not supported.");
            }
        } else {
            //Now we have to read everything, because if one parsing method fails
            //the whole InputStream is lost as we can't reset it
            return parse(readAll(is, Integer.MAX_VALUE));
        }
    }

    /**
     * Saves a property list with the given object as root into a XML file
     * @param root the root object
     * @param out the output file
     * @throws IOException if an error occurs during writing
     */
    public static void saveAsXML(NSObject root, File out) throws IOException {
	OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(out), "UTF-8");
        w.write(root.toXMLPropertyList());
        w.close();
    }

    /**
     * Converts a given property list file into the xml format
     * @param in the source file
     * @param out the target file
     * @throws Exception if an error occurs during parsing
     * @throws IOException if an error occurs during writing
     */
    public static void convertToXml(File in, File out) throws Exception {
        NSObject root = parse(in);
        saveAsXML(root, out);
    }
    
    /**
     * Saves a property list with the given object as root into a binary file
     * @param root the root object
     * @param out the output file
     * @throws IOException if an error occurs during writing
     */
    public static void saveAsBinary(NSObject root, File out) throws IOException {
	BinaryPropertyListWriter.write(out, root);
    }
    
    /**
     * Converts a given property list file into the binary format
     * @param in the source file
     * @param out the target file
     * @throws Exception if an error occurs during parsing
     * @throws IOException if an error occurs during writing
     */
    public static void convertToBinary(File in, File out) throws Exception {
	NSObject root = parse(in);
	saveAsBinary(root, out);
    }
}
