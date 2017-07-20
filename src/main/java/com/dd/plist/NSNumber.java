/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2011-2017 Daniel Dreibrodt, Keith Randall
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
 * The NSNumber class wraps a numeric value. The value can be an integer a floating point number or a boolean value.
 * @see <a href="https://developer.apple.com/reference/foundation/nsnumber" target="_blank">Foundation NSNumber documentation</a>
 * @author Daniel Dreibrodt
 */
public class NSNumber extends NSObject implements Comparable<Object> {

    /**
     * Indicates that the number's value is an integer.
     * The number is stored as a Java <code>long</code>.
     * Its original value could have been char, short, int, long or even long long.
     */
    public static final int INTEGER = 0;

    /**
     * Indicates that the number's value is a real number.
     * The number is stored as a Java <code>double</code>.
     * Its original value could have been float or double.
     */
    public static final int REAL = 1;

    /**
     * Indicates that the number's value is boolean.
     */
    public static final int BOOLEAN = 2;

    /** Holds the current type of this number */
    private int type;

    private long longValue;
    private double doubleValue;
    private boolean boolValue;

    /**
     * Creates a new NSNumber instance from its binary representation.
     *
     * @param bytes The binary representation of this number.
     * @param type  The type of number.
     * @see #INTEGER
     * @see #REAL
     * @see #BOOLEAN
     */
    public NSNumber(byte[] bytes, int type){
        this(bytes, 0, bytes.length, type);
    }

    /**
     * Creates a new NSNumber instance from its binary representation.
     *
     * @param bytes An array of bytes containing the binary representation of the number.
     * @param startIndex The position in the array at which the number is stored.
     * @param endIndex The position in the array at which the number's data ends.
     * @param type The type of number
     * @see #INTEGER
     * @see #REAL
     * @see #BOOLEAN
     */
    public NSNumber(byte[] bytes, final int startIndex, final int endIndex, final int type){
        switch (type) {
            case INTEGER: {
                doubleValue = longValue = BinaryPropertyListParser.parseLong(bytes, startIndex, endIndex);
                break;
            }
            case REAL: {
                doubleValue = BinaryPropertyListParser.parseDouble(bytes, startIndex, endIndex);
                longValue = Math.round(doubleValue);
                break;
            }
            default: {
                throw new IllegalArgumentException("Type argument is not valid.");
            }
        }
        this.type = type;
    }

    /**
     * Create a NSNumber instance from its textual representation.
     *
     * @param text The textual representation of the number.
     * @throws IllegalArgumentException If the text does not represent an integer, real number or boolean value.
     * @see Boolean#parseBoolean(java.lang.String)
     * @see Long#parseLong(java.lang.String)
     * @see Double#parseDouble(java.lang.String)
     */
    public NSNumber(String text) {
        if (text == null)
            throw new IllegalArgumentException("The given string is null and cannot be parsed as number.");
        try {
            long l;
            if (text.startsWith("0x")) {
                l  = Long.parseLong(text.substring(2), 16);
            } else {
                l  = Long.parseLong(text);
            }
            this.doubleValue = this.longValue = l;
            this.type = INTEGER;
        } catch (Exception ex) {
            try {
                this.doubleValue = Double.parseDouble(text);
                this.longValue = Math.round(doubleValue);
                this.type = REAL;
            } catch (Exception ex2) {
                try {
                    this.boolValue = text.equalsIgnoreCase("true") || text.equalsIgnoreCase("yes");
                    if(!boolValue && !(text.equalsIgnoreCase("false") || text.equalsIgnoreCase("no"))) {
                        throw new Exception("not a boolean");
                    }
                    this.type = BOOLEAN;
                    this.doubleValue = this.longValue = this.boolValue ? 1 : 0;
                } catch (Exception ex3) {
                    throw new IllegalArgumentException("The given string neither represents a double, an int nor a boolean value.");
                }
            }
        }
    }

    /**
     * Creates a new NSNumber instance with the specified value.
     *
     * @param i The integer value.
     */
    public NSNumber(int i) {
        this.doubleValue = this.longValue = i;
        this.type = INTEGER;
    }

    /**
     * Creates a new NSNumber instance with the specified value.
     *
     * @param l The long integer value.
     */
    public NSNumber(long l) {
        this.doubleValue = this.longValue = l;
        this.type = INTEGER;
    }

    /**
     * Creates a new NSNumber instance with the specified value.
     *
     * @param d The real value.
     */
    public NSNumber(double d) {
        this.longValue = (long) (this.doubleValue = d);
        this.type = REAL;
    }

    /**
     * Creates a new NSNumber instance with the specified value.
     *
     * @param b The boolean value.
     */
    public NSNumber(boolean b) {
        this.boolValue = b;
        this.doubleValue = this.longValue = b ? 1 : 0;
        this.type = BOOLEAN;
    }

    /**
     * Gets the type of this instance's value.
     *
     * @return The type flag.
     * @see #BOOLEAN
     * @see #INTEGER
     * @see #REAL
     */
    public int type() {
        return this.type;
    }

    /**
     * Gets a value indicating whether the value of this NSNumber is a boolean.
     *
     * @return Whether the number's value is a boolean.
     */
    public boolean isBoolean() {
        return this.type == BOOLEAN;
    }

    /**
     * Gets a value indicating whether the value of this NSNumber is an integer.
     *
     * @return Whether the number's value is an integer.
     */
    public boolean isInteger() {
        return this.type == INTEGER;
    }

    /**
     * Gets a value indicating whether the value of this NSNumber is a real number.
     *
     * @return Whether the number's value is a real number.
     */
    public boolean isReal() {
        return this.type == REAL;
    }

    /**
     * Gets this instance's boolean value.
     *
     * @return <code>true</code> if the value is true or non-zero, <code>false</code> otherwise.
     */
    public boolean boolValue() {
        if (this.type == BOOLEAN)
            return this.boolValue;
        else
            return this.doubleValue() != 0;
    }

    /**
     * Gets this instance's long integer value.
     *
     * @return The value of the number as a <code>long</code>.
     */
    public long longValue() {
        return this.longValue;
    }

    /**
     * Gets this instance's integer value.
     * <i>Note: Even though the number's type might be INTEGER it can be larger than a Java int.
     * Use intValue() only if you are certain that it contains a number from the int range.
     * Otherwise the value might be inaccurate.</i>
     *
     * @return The value of the number as an <code>int</code>.
     */
    public int intValue() {
        return (int) this.longValue;
    }

    /**
     * Gets this instance's <code>double</code> value.
     *
     * @return The value of the number as a <code>double</code>.
     */
    public double doubleValue() {
        return this.doubleValue;
    }

    /**
     * Gets this instance's <code>float</code> value.
     * WARNING: Possible loss of precision if the value is outside the float range.
     *
     * @return The value of the number as a <code>float</code>.
     */
    public float floatValue() {
        return (float) this.doubleValue;
    }

    /**
     * Gets this instance's value expressed as a human-readable string.
     * @return The human-readable string representation of this number.
     */
    public String stringValue() {
        switch (this.type()) {
            case INTEGER: {
                return String.valueOf(this.longValue());
            }
            case REAL: {
                return String.valueOf(this.doubleValue());
            }
            case BOOLEAN: {
                return String.valueOf(this.boolValue());
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * Checks whether the other object is a NSNumber of the same value.
     *
     * @param obj The object to compare to.
     * @return Whether the objects are equal in terms of numeric value and type.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        NSNumber n = (NSNumber) obj;
        return this.type == n.type && this.longValue == n.longValue && this.doubleValue == n.doubleValue && this.boolValue == n.boolValue;
    }

    @Override
    public int hashCode() {
        int hash = type;
        hash = 37 * hash + (int) (this.longValue ^ (this.longValue >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.doubleValue) ^ (Double.doubleToLongBits(this.doubleValue) >>> 32));
        hash = 37 * hash + (boolValue() ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        switch (this.type()) {
            case INTEGER: {
                return String.valueOf(this.longValue());
            }
            case REAL: {
                return String.valueOf(this.doubleValue());
            }
            case BOOLEAN: {
                return String.valueOf(this.boolValue());
            }
            default: {
                return super.toString();
            }
        }
    }

    @Override
    void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        switch (this.type()) {
            case INTEGER: {
                xml.append("<integer>");
                xml.append(this.longValue());
                xml.append("</integer>");
                break;
            }
            case REAL: {
                xml.append("<real>");
                xml.append(this.doubleValue());
                xml.append("</real>");
                break;
            }
            case BOOLEAN: {
                if (boolValue())
                    xml.append("<true/>");
                else
                    xml.append("<false/>");
                break;
            }
            default:
                break;
        }
    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        switch (this.type()) {
            case INTEGER: {
                if (longValue() < 0) {
                    out.write(0x13);
                    out.writeBytes(this.longValue(), 8);
                } else if (this.longValue() <= 0xff) {
                    out.write(0x10);
                    out.writeBytes(this.longValue(), 1);
                } else if (this.longValue() <= 0xffff) {
                    out.write(0x11);
                    out.writeBytes(this.longValue(), 2);
                } else if (this.longValue() <= 0xffffffffL) {
                    out.write(0x12);
                    out.writeBytes(this.longValue(), 4);
                } else {
                    out.write(0x13);
                    out.writeBytes(this.longValue(), 8);
                }
                break;
            }
            case REAL: {
                out.write(0x23);
                out.writeDouble(this.doubleValue());
                break;
            }
            case BOOLEAN: {
                out.write(this.boolValue() ? 0x09 : 0x08);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        indent(ascii, level);
        if (this.isBoolean()) {
            ascii.append(this.boolValue() ? "YES" : "NO");
        } else {
            ascii.append(toString());
        }
    }

    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        indent(ascii, level);
        switch (this.type()) {
            case INTEGER: {
                ascii.append("<*I");
                ascii.append(this.toString());
                ascii.append('>');
                break;
            }
            case REAL: {
                ascii.append("<*R");
                ascii.append(this.toString());
                ascii.append('>');
                break;
            }
            case BOOLEAN: {
                if (this.boolValue()) {
                    ascii.append("<*BY>");
                } else {
                    ascii.append("<*BN>");
                }
            }
            default:
                break;
        }
    }

    public int compareTo(Object o) {
        double x = this.doubleValue();
        double y;
        if (o instanceof NSNumber) {
            NSNumber num = (NSNumber) o;
            y = num.doubleValue();
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        } else if (o instanceof Number) {
            y = ((Number) o).doubleValue();
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        } else {
            return -1;
        }
    }
}
