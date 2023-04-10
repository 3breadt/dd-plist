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
import java.math.BigInteger;
import java.util.Objects;

/**
 * The UID class holds a unique identifier.
 * Only found in binary property lists that are keyed archives.
 *
 * @author Daniel Dreibrodt
 */
public class UID extends NSObject {
    private final BigInteger uid;
    private final String name;

    /**
     * Creates a new UID instance.
     *
     * @param name  The UID name.
     * @param value The UID value.
     * @throws NullPointerException     If value is null.
     * @throws IllegalArgumentException If value is negative or has a length of more than 128-bit.
     */
    public UID(String name, BigInteger value) {
        this.name = name;
        this.uid = Objects.requireNonNull(value);
        if (this.uid.bitLength() > 128) {
            throw new IllegalArgumentException("The specified UID exceeds the maximum length of 128-bit.");
        }
        if (this.uid.signum() < 0) {
            throw new IllegalArgumentException("The specified value is negative.");
        }
    }

    /**
     * Creates a new UID instance.
     *
     * @param name  The UID name.
     * @param bytes The UID value.
     * @throws NullPointerException     If bytes is null.
     * @throws IllegalArgumentException If bytes represents a UID with a length of more than 128-bit (leading zero bytes are ignored).
     */
    public UID(String name, byte[] bytes) {
        this(name, new BigInteger(1, Objects.requireNonNull(bytes)));
    }

    /**
     * Gets this instance's value.
     *
     * @return The UID's value in big-endian representation, encoded on 1, 2, 4, 8 or 16 bytes.
     */
    public byte[] getBytes() {
        byte[] data = this.uid.toByteArray();
        if (data.length == 3) {
            byte[] paddedData = new byte[4];
            System.arraycopy(data, 0, paddedData, 1, 3);
            data = paddedData;
        } else if (data.length > 4 && data.length < 8) {
            byte[] paddedData = new byte[8];
            System.arraycopy(data, 0, paddedData, 8 - data.length, data.length);
            data = paddedData;
        } else if (data.length > 8 && data.length < 16) {
            byte[] paddedData = new byte[16];
            System.arraycopy(data, 0, paddedData, 16 - data.length, data.length);
            data = paddedData;
        }

        return data;
    }

    /**
     * Gets this instance's name.
     *
     * @return The UID's name.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public UID clone() {
        return new UID(this.name, this.uid.toByteArray());
    }

    @Override
    public Object toJavaObject() {
        return this.getBytes();
    }

    /**
     * The UID type has no dedicated representation in an XML property list.
     *
     * Typically, it is represented in XML as a dictionary with only one entry,
     * where the key is "CF$UID" and the value is the integer representation
     * of the UID.
     * @param xml   The XML StringBuilder
     * @param level The indentation level
     */
    @Override
    void toXML(StringBuilder xml, int level) {
        NSDictionary uidDict = new NSDictionary();
        uidDict.put("CF$UID", new NSNumber(this.uid.longValue()));
        uidDict.toXML(xml, level);
    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        byte[] bytes = this.getBytes();
        out.write(0x80 + bytes.length - 1);
        out.write(bytes);
    }

    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        new NSString(this.uid.toString(16)).toASCII(ascii, level);
    }

    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        new NSString(this.uid.toString(16)).toASCIIGnuStep(ascii, level);
    }

    @Override
    public int compareTo(NSObject o) {
        Objects.requireNonNull(o);
        if (o == this) {
            return 0;
        } else if (o instanceof UID) {
            UID other = (UID)o;
            int diff = this.uid.compareTo(other.uid);
            if (diff == 0) {
                if (this.name == null) {
                    return other.name != null ? 1 : 0;
                } else if (other.name == null) {
                    return -1;
                }

                return this.name.compareTo(other.name);
            }

            return diff;
        } else {
            return this.getClass().getName().compareTo(o.getClass().getName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        UID uid = (UID) o;
        return this.uid.equals(((UID) o).uid) && Objects.equals(this.name, uid.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(this.name);
        result = 31 * result + this.uid.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.uid.toString(16) + (this.name != null ? " (" + this.name + ")" : "");
    }
}
