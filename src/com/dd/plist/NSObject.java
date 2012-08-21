/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2012 Daniel Dreibrodt
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

import java.io.IOException;

/**
 * Abstract interface for any object contained in a property list.
 * The names and functions of the various objects orient themselves
 * towards Apple's Cocoa API.
 * @author Daniel Dreibrodt
 */
public abstract class NSObject {

    /**
     * The newline character used for generating the XML output.
     * This constant will be different depending on the operating system on
     * which you use this library.
     */
    final static String NEWLINE = System.getProperty("line.separator");
    
    /**
     * The identation character used for generating the XML output. This is the
     * tabulator character.
     */
    final static String INDENT = "\t";
    
    /**
     * The maximum length of the text lines to be used when generating
     * ASCII property lists. But this number is only a guideline it is not
     * guaranteed that it will not be overstepped.
     */
    final static int ASCII_LINE_LENGTH = 80;

    /**
     * Generates the XML representation of the object (without XML headers or enclosing plist-tags).
     * @param xml The StringBuilder onto which the XML representation is appended.
     * @param level The indentation level of the object.
     */
    abstract void toXML(StringBuilder xml, int level);

    /**
     * Assigns IDs to all the objects in this NSObject subtree.
     * @param out The writer object that handles the binary serialization.
     */
    void assignIDs(BinaryPropertyListWriter out) {
	out.assignID(this);
    }

    /**
     * Generates the binary representation of the object.
     * @param out The output stream to serialize the object to.
     */
    abstract void toBinary(BinaryPropertyListWriter out) throws IOException;

    /**
     * Generates a valid XML property list including headers using this object as root.
     * @return The XML representation of the property list including XML header and doctype information.
     */
    public String toXMLPropertyList() {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append(NSObject.NEWLINE);
        xml.append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
        xml.append(NSObject.NEWLINE);
        xml.append("<plist version=\"1.0\">");
        xml.append(NSObject.NEWLINE);
	toXML(xml, 0);
        xml.append(NSObject.NEWLINE);
        xml.append("</plist>");
        return xml.toString();
    }
    
    /**
     * Generates the ASCII representation of this object.
     * The generated ASCII representation does not end with a newline.
     * Complies with https://developer.apple.com/library/mac/#documentation/Cocoa/Conceptual/PropertyLists/OldStylePlists/OldStylePLists.html
     * @param ascii The StringBuilder onto which the ASCII representation is appended.
     * @param level The indentation level of the object.
     */
    protected abstract void toASCII(StringBuilder ascii, int level);
    
    /**
     * Generates the ASCII representation of this object in the GnuStep format.
     * The generated ASCII representation does not end with a newline.
     * @param ascii The StringBuilder onto which the ASCII representation is appended.
     * @param level The indentation level of the object.
     */
    protected abstract void toASCIIGnuStep(StringBuilder ascii, int level);

    /**
     * Helper method that adds correct identation to the xml output.
     * Calling this method will add <code>level</code> number of tab characters
     * to the <code>xml</code> string.
     * @param xml The string builder for the XML document.
     * @param level The level of identation.
     */
    void indent(StringBuilder xml, int level) {
        for(int i=0;i<level;i++)
            xml.append(INDENT);
    }
}
