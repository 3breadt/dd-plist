/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2011 Daniel Dreibrodt
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
     * @param indent The StringBuilder onto which the XML representation is appended.
     * @param level The indentation level of the object.
     */
    public abstract void toXML(StringBuilder xml, int level);

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

        xml.append("</plist>");
        return xml.toString();
    }

    protected void indent(StringBuilder xml, int level) {
        for(int i=0;i<level;i++)
            xml.append(INDENT);
    }
}
