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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * A NSString contains a string.
 * @author Daniel Dreibrodt
 */
public class NSString extends NSObject {

    private String content;

    /**
     * Creates an NSString from its binary representation.
     * @param bytes The binary representation.
     * @param encoding The encoding of the binary representation, the name of a supported charset.
     * @see java.lang.String
     * @throws UnsupportedEncodingException
     */
    public NSString(byte[] bytes, String encoding) throws UnsupportedEncodingException {
        content = new String(bytes, encoding);
    }

    /**
     * Creates a NSString from a string.
     * @param string The string that will be contained in the NSString.
     */
    public NSString(String string) {
        try {
            content = new String(string.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof NSString)) return false;
	return content.equals(((NSString)obj).content);
    }
    
    @Override
    public int hashCode() {
	return content.hashCode();
    }

    /**
     * The textual representation of this NSString.
     * @return The NSString's contents.
     */
    @Override
    public String toString() {
        return content;
    }

    public void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        xml.append("<string><![CDATA[");
        xml.append(content.replaceAll("]]>", "]]]]><![CDATA[>"));
        xml.append("]]></string>");
    }
    
    private static CharsetEncoder asciiEncoder = Charset.forName("ASCII").newEncoder();
    private static CharsetEncoder utf16beEncoder = Charset.forName("UTF-16BE").newEncoder();
    public void toBinary(BinaryPropertyListWriter out) throws IOException {
	CharBuffer charBuf = CharBuffer.wrap(content);
	int kind;
	ByteBuffer byteBuf;
	if (asciiEncoder.canEncode(charBuf)) {
	    kind = 0x5; // standard ASCII
	    byteBuf = asciiEncoder.encode(charBuf);
	} else {
	    kind = 0x6; // UTF-16-BE
	    byteBuf = utf16beEncoder.encode(charBuf);
	}
	byte[] bytes = new byte[byteBuf.remaining()];
	byteBuf.get(bytes);
	out.writeIntHeader(kind, content.length());
	out.write(bytes);
    }
}
