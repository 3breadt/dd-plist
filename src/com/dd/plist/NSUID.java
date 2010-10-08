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
 * A NSUID contains a UID. Only found in binary property lists.
 * @author Daniel Dreibrodt
 */
public class NSUID extends NSObject {

  private byte[] bytes;
  private String name;

  public NSUID(String name, byte[] bytes) {
    this.name = name;
    this.bytes = bytes;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public String getName() {
    return name;
  }

  public String toXML(String indent) {
    String xml=indent+"<string>";
    xml+=new String(bytes);
    xml+="</string>";
    return xml;
  }

}
