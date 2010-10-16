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
 * A number either integer or real
 * @author Daniel Dreibrodt
 */
public class NSNumber extends NSObject {

    public static final int INTEGER = 0;
    public static final int REAL = 1;

    private int type;

    private long longValue;
    private double doubleValue;

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
                throw new IllegalArgumentException("Given string is neither a real or integer number.");
            }
        }
    }

    public NSNumber(int i) {
        type = INTEGER;
        doubleValue = longValue = i;
    }

    public NSNumber(double d) {
        longValue = (long) (doubleValue = d);
        type = REAL;
    }

    public int type() {
        return type;
    }

    public long longValue() {
        return longValue;
    }

    public int intValue() {
        return (int)longValue;
    }

    public double doubleValue() {
        return doubleValue;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(NSNumber.class) && obj.hashCode()==hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.type;
        hash = 79 * hash + (int) (this.longValue ^ (this.longValue >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.doubleValue) ^ (Double.doubleToLongBits(this.doubleValue) >>> 32));
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
            }
            default : {
                return "";
            }
        }
    }

}
