/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2011 Daniel Dreibrodt, Keith Randall
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
     * Creates a NSDate from a Java Date
     * @param d The date
     */
    public NSDate(Date d) {
        if(d==null)
            throw new IllegalArgumentException("Date cannot be null");
        date = d;
    }

    /**
     * Gets the date.
     * @return The date.
     */
    public Date getDate() {
        return date;
    }

    public void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        xml.append("<date>");
        xml.append(sdf.format(date));
        xml.append("</date>");
    }

    public void toBinary(BinaryPropertyListWriter out) throws IOException {
	out.write(0x33);
	out.writeDouble((date.getTime() - EPOCH) / 1000.0);
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
