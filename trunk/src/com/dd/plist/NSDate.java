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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;

/**
 * Represents a date
 * @author Daniel Dreibrodt
 */
public class NSDate extends NSObject {

    private Date date;

    // EPOCH = new SimpleDateFormat("yyyy MM dd zzz").parse("2001 01 01 GMT").getTime();
    // ...but that's annoying in a static initializer because it can throw exceptions, ick.
    // So we just hardcode the correct value.
    private final static long EPOCH = 978307200000L;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static { sdf.setTimeZone(TimeZone.getTimeZone("GMT")); }
    
    /**
     * Creates a date from its binary representation.
     * @param bytes The date bytes
     */
    public NSDate(byte[] bytes) {
        //dates are 8 byte big-endian double, seconds since the epoch
        date = new Date(EPOCH + (long)(1000 * BinaryPropertyListParser.parseDouble(bytes)));
    }
    
    /**
     * Parses a date from its textual representation.
     * That representation has the following pattern: <code>yyyy-MM-dd'T'HH:mm:ss'Z'</code>
     * @param textRepresentation The textual representation of the date (ISO 8601 format)
     * @throws ParseException When the date could not be parsed, i.e. it does not match the expected pattern.
     */
    public NSDate(String textRepresentation) throws ParseException {
        date = sdf.parse(textRepresentation);
    }

    /**
     * Gets the date.
     * @return The date.
     */
    public Date getDate() {
        return date;
    }

    public String toXML(String indent) {
        String xml = indent + "<date>";
        if (date != null) {
            xml += sdf.format(date);
        }
        xml += "</date>";
        return xml;
    }

    /**
     * Generates a string representation of the date.
     * @see java.util.Date#toString()
     * @return A string representation of the date.
     */
    @Override
    public String toString() {
        return date.toString();
    }
}
