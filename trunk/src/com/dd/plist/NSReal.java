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
 * A NSReal object contains a real number.
 * @author Daniel Dreibrodt
 */
public class NSReal extends NSObject {

  private float value;

  /**
   * Creates a NSReal object from its binary representation. <br/>
   * <b><i>NOT SUPPORTED YET</i></b>
   * @param bytes The binary representation
   */
  public NSReal(byte[] bytes) {
    System.out.println("WARNING: parsing of binary NSReals is not yet suppoorted.");
    //TODO
  }

  /**
   * Creates a NSReal object from its textual representation.
   * @param textRepresentation The textual representation.
   */
  public NSReal(String textRepresentation) {
    value = Float.parseFloat(textRepresentation);
  }

  /**
   * Gets the value of the NSReal object as a Java float value.
   * @return The NSReal's value.
   */
  public float getValue() {
    return value;
  }

  public String toXML(String indent) {
    String xml=indent+"<real>";
    xml+=String.valueOf(value);
    xml+="</real>";
    return xml;
  }

}
