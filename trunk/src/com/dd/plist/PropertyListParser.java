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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Parses any given property list
 * @author Daniel Dreibrodt
 */
public class PropertyListParser {

    /**
     * Parses a property list from a file. It can either be in XML or binary format.
     * @param f The property list file
     * @return The root object in the property list
     * @throws Exception If an error occurred while parsing
     */
    public static NSObject parse(File f) throws Exception {
        FileInputStream fis = new FileInputStream(f);
        byte[] magic = new byte[8];
        fis.read(magic);
        String magic_string = new String(magic);
        fis.close();
        if (magic_string.startsWith("bplist00")) {
            return BinaryPropertyListParser.parse(f);
        } else if (magic_string.startsWith("<?xml")) {
            return XMLPropertyListParser.parse(f);
        } else {
            throw new UnsupportedOperationException("The given file is neither a binary nor a XML property list. ASCII property lists are not supported.");
        }
    }

    /**
     * Parses a property list from a byte array. It can either be in XML or binary format.
     * @param bytes The property list data
     * @return The root object in the property list
     * @throws Exception If an error occurred while parsing.
     */
    public static NSObject parse(byte[] bytes) throws Exception {
        String magic_string = new String(bytes, 0, 8);
        if (magic_string.startsWith("bplist00")) {
            return BinaryPropertyListParser.parse(bytes);
        } else if (magic_string.startsWith("<?xml")) {
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
            byte[] magic = new byte[8];
            is.read(magic);
            is.reset();
            String magic_string = new String(magic);
            if (magic_string.startsWith("bplist00")) {
                return BinaryPropertyListParser.parse(is);
            } else if(magic_string.startsWith("<?xml")) {
                return XMLPropertyListParser.parse(is);
            } else {
                throw new UnsupportedOperationException("The given data is neither a binary nor a XML property list. ASCII property lists are not supported.");
            }
        } else {
            if (is.available() > Runtime.getRuntime().freeMemory()) {
                throw new Exception("To little heap space available! Wanted to read " + is.available() + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
            }
            byte[] buf = new byte[is.available()];
            is.read(buf);
            is.close();
            return parse(buf);
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
