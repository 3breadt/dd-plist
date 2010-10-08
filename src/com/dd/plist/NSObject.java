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

/**
 * Abstract interface for any object contained in a property list.
 * The names and functions of the various objects orient themselves
 * towards Apple's Cocoa API.
 * @author Daniel
 */
public abstract class NSObject {

 /**
  * Generates the XML representation of the object (without XML headers or enclosing plist-tags).
  * @param indent The indentation with which to generate the XML.
  * @return The XML representation of the object.
  */
 public abstract String toXML(String indent);

 /**
  * Generates the XML representation of the object (without XML headers or enclosing plist-tags).
  * @return The XML representation of the object.
  */
 public String toXML() {
     return toXML("");
 }

 /**
  * Generates a valid XML property list including headers.
  * @return The XML property list.
  */
 public String toXMLPropertyList() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+System.getProperty("line.separator");
    xml += "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"+System.getProperty("line.separator");
    xml += "<plist version=\"1.0\">"+System.getProperty("line.separator");
    xml += toXML("");
    xml += "</plist>";
    return xml;
 }
  
}
