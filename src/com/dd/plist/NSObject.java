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

import java.io.IOException;

/**
 * Abstract interface for any object contained in a property list.
 * The names and functions of the various objects orient themselves
 * towards Apple's Cocoa API.
 * @author Daniel
 */
public abstract class NSObject {

    public final static String NEWLINE = System.getProperty("line.separator");
    public final static String INDENT = "\t";

    /**
     * Generates the XML representation of the object (without XML headers or enclosing plist-tags).
     * @param xml The StringBuilder onto which the XML representation is appended.
     * @param level The indentation level of the object.
     */
    public abstract void toXML(StringBuilder xml, int level);

    /**
     * Assigns IDs to all the objects in this NSObject subtree.
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
     * @return The XML representation of the property list
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

    protected void indent(StringBuilder xml, int level) {
        for(int i=0;i<level;i++)
            xml.append(INDENT);
    }
}
