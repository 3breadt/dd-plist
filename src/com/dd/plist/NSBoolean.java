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
 * Represents a Boolean
 * @author Daniel Dreibrodt
 */
public class NSBoolean extends NSObject {

  private boolean value;

  public NSBoolean(boolean value) {
    this.value = value;
  }

  public boolean isTrue() {
    return value;
  }

  public String toXML(String indent) {
    String xml = indent;
    if(value) xml+="<true/>";
    else xml+="<false/>";
    return xml;
  }

}
