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

import java.math.BigInteger;

/**
 * A NSInteger contains an integer.
 * @author Daniel Dreibrodt
 */
public class NSInteger extends NSObject {

    private BigInteger value;

    /**
     * Creates a NSInteger from its binary representation.
     * This will create an unsigned integer.
     * @param bytes The binary representation.
     */
    public NSInteger(byte[] bytes) {
        if (bytes.length < 8) {
            value = BigInteger.valueOf(BinaryPropertyListParser.parseUnsignedInt(bytes));
        }
        value = new BigInteger(bytes);
    }

    /**
     * Creates a NSInteger from its textual representation.
     * @param textRepresentation The textual representation.
     */
    public NSInteger(String textRepresentation) {
        value = new BigInteger(textRepresentation);
    }

    /**
     * Gets the NSInteger's value as a Java long value.
     * @return The long value of the NSInteger.
     */
    public long longValue() {
        return value.longValue();
    }

    /**
     * Gets the NSInteger's value as a Java int value. Note that the result
     * may be inaccurate as a NSInteger can store integers that exceed the
     * Java int range.
     * @return The int value of the NSInteger.
     */
    public int intValue() {
        return value.intValue();
    }

    /**
     * Gets the BigInteger which stores the NSInteger's value.
     * By manipulating the returned BigInteger you manipulate
     * this NSInteger's value.
     * @return The NSInteger's value.
     */
    public BigInteger bigIntegerValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass().equals(this.getClass()) && ((NSInteger)obj).bigIntegerValue().equals(value));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    public String toXML(String indent) {
        String xml = indent + "<integer>";
        xml += value.toString();
        xml += "</integer>";
        return xml;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
