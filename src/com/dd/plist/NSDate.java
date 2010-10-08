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
import java.util.Date;

/**
 * Represents a date
 * @author Daniel Dreibrodt
 */
public class NSDate extends NSObject {

    private Date date;

    /**
     * Creates a date from its binary representation. <br/>
     * <b><i>NOT SUPPORTED YET.</i></b>
     * @param bytes The date bytes
     */
    public NSDate(byte[] bytes) {
        //TODO bytes are 8 byte float
        System.err.println("WARNING: Binary encoded date objects are not yet supported");
        date = new Date();
    }

    /**
     * Parses a date from its textual representation.
     * That representation has the following pattern: <code>yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'</code>
     * @param textRepresentation The textual representation of the date
     * @throws ParseException When the date could not be parsed, i.e. it does not match the expected pattern.
     */
    public NSDate(String textRepresentation) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
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
