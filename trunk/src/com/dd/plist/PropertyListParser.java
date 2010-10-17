/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2010 Daniel Dreibrodt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dd.plist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
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
        byte[] magic = Arrays.copyOf(bytes, 8);
        String magic_string = new String(magic);
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
     * @throws java.lang.Exception
     */
    public static void saveAsXML(NSObject root, File out) throws Exception {
        FileWriter fw = new FileWriter(out);
        fw.write(root.toXMLPropertyList());
        fw.close();
    }

    /**
     * Converts a given property list file into the xml format
     * @param in the source file
     * @param out the target file
     * @throws java.lang.Exception
     */
    public static void convertToXml(File in, File out) throws Exception {
        NSObject root = parse(in);
        saveAsXML(root, out);
    }
}
