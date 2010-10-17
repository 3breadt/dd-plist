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
 * A number whose value is either an integer, a real number or boolean.
 * @author Daniel Dreibrodt
 */
public class NSNumber extends NSObject {

    /**
     * Indicates that the number's value is an integer.
     * This can either be a Java <code>long</code> or <code>int</code>.
     **/
    public static final int INTEGER = 0;
    /**
     * Indicates that the number's value is a real number.
     * This is a Java <code>double</code>. **/
    public static final int REAL = 1;
    /** 
     * Indicates that the number's value is boolean.
     **/
    public static final int BOOLEAN = 2;

    private int type;

    private long longValue;
    private double doubleValue;
    private boolean boolValue;

    /**
     * Parses integers and real numbers from their binary representation.
     * <i>Note: real numbers are not yet supported.</i>
     * @param bytes The binary representation
     * @param type The type of number
     * @see #INTEGER
     * @see #REAL
     */
    public NSNumber(byte[] bytes, int type) {
        switch(type) {
            case INTEGER : {
                //TODO enable long
                doubleValue = longValue = BinaryPropertyListParser.parseUnsignedInt(bytes);
                break;
            }
            case REAL : {
                //TODO
                break;
            }
            default : {
                throw new IllegalArgumentException("Type argument is not valid.");
            }
        }
        this.type = type;
    }

    /**
     * Creates a number from its textual representation.
     * @param text The textual representation of the number.
     * @see Boolean#parseBoolean(java.lang.String)
     * @see Long#parseLong(java.lang.String)
     * @see Double#parseDouble(java.lang.String)
     * @throws IllegalArgumentException If the text does not represent an integer, real number or boolean value.
     */
    public NSNumber(String text) {
        try {
            long l = Long.parseLong(text);
            doubleValue = longValue = l;
            type = INTEGER;
        } catch(Exception ex) {
            try {
                double d = Double.parseDouble(text);
                longValue = (long)(doubleValue = d);
                type = REAL;
            } catch(Exception ex2) {
                try {
                    boolValue = Boolean.parseBoolean(text);
                    doubleValue = longValue = boolValue?1:0;
                } catch(Exception ex3) {
                    throw new IllegalArgumentException("Given text neither represents a double, int or boolean value.");
                }
            }
        }
    }

    /**
     * Creates an integer number.
     * @param i The integer value.
     */
    public NSNumber(int i) {
        type = INTEGER;
        doubleValue = longValue = i;
    }

    /**
     * Creates a real number.
     * @param d The real value.
     */
    public NSNumber(double d) {
        longValue = (long) (doubleValue = d);
        type = REAL;
    }

    /**
     * Creates a boolean number.
     * @param b The boolean value.
     */
    public NSNumber(boolean b) {
        boolValue = b;
        doubleValue = longValue = b?1:0;
    }

    /**
     * Gets the type of this number's value.
     * @return The type flag.
     * @see #BOOLEAN
     * @see #INTEGER
     * @see #REAL
     */
    public int type() {
        return type;
    }

    /**
     * The number's boolean value.
     * @return <code>true</code> if the value is true or non-zero, false</code> otherwise.
     */
    public boolean boolValue() {
        if(type==BOOLEAN)
            return boolValue;
        else
            return doubleValue!=0;
    }

    /**
     * The number's long value.
     * @return The value of the number as long
     */
    public long longValue() {
        return longValue;
    }

    /**
     * The number's int value.
     * <i>Note: Even though the number's type might be INTEGER it can be larger than a Java int.
     * Use intValue() only if you are certain that it contains a number from the int range.
     * Otherwise the value might be innaccurate.</i>
     * @return The value of the number as int
     */
    public int intValue() {
        return (int)longValue;
    }

    /**
     * The number's double value.
     * @return The value of the number as double.
     */
    public double doubleValue() {
        return doubleValue;
    }

    /**
     * Checks whether the other object is a NSNumber of the same value.
     * The type is ignored, what matters is the numeric value.
     * @param obj The object to compare to.
     * @return Whether the objects are equal in terms of numeric value.
     */
    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(NSNumber.class) && obj.hashCode()==hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (int) (this.longValue ^ (this.longValue >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.doubleValue) ^ (Double.doubleToLongBits(this.doubleValue) >>> 32));
        hash = 37 * hash + (boolValue() ? 1 : 0);
        return hash;
    }


    @Override
    public String toString() {
        switch(type) {
            case INTEGER : {
                return String.valueOf(longValue());
            }
            case REAL : {
                return String.valueOf(doubleValue());
            }
            case BOOLEAN : {
                return String.valueOf(boolValue());
            }
            default : {
                return super.toString();
            }
        }
    }

    @Override
    public String toXML(String indent) {
        switch(type) {
            case INTEGER : {
                String xml = "<integer>"+String.valueOf(longValue)+"</integer>";
                return indent+xml;
            }
            case REAL : {
                String xml = "<real>"+String.valueOf(doubleValue)+"</real>";
                return indent+xml;
            } case BOOLEAN : {
                if(boolValue)
                    return indent+"<true/>";
                else
                    return indent+"<false/>";
            }
            default : {
                return "";
            }
        }
    }

}
