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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
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
        content = string;
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
    
    private static CharsetEncoder asciiEncoder, utf16beEncoder, utf8Encoder;

    @Override
    void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        xml.append("<string>");
        
        //Make sure that the string is encoded in UTF-8 for the XML output
        synchronized (NSString.class) {
            if(utf8Encoder==null)
                utf8Encoder = Charset.forName("UTF-8").newEncoder();
            else
                utf8Encoder.reset();
            
            try {
                ByteBuffer byteBuf = utf8Encoder.encode(CharBuffer.wrap(content));
                byte[] bytes = new byte[byteBuf.remaining()];
                byteBuf.get(bytes);
                content = new String(bytes, "UTF-8");
            } catch (CharacterCodingException ex) {
                ex.printStackTrace();
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        
        //According to http://www.w3.org/TR/REC-xml/#syntax node values must not
        //contain the characters < or &. Also the > character should be escaped.
        if(content.contains("&") || content.contains("<") || content.contains(">")) {
            xml.append("<![CDATA[");
            xml.append(content.replaceAll("]]>", "]]]]><![CDATA[>"));
            xml.append("]]>");
        } else {
            xml.append(content);
        }
        xml.append("</string>");
    }
    
    
    @Override
    public void toBinary(BinaryPropertyListWriter out) throws IOException {
	CharBuffer charBuf = CharBuffer.wrap(content);
	int kind;
	ByteBuffer byteBuf;
        synchronized (NSString.class) {
            if(asciiEncoder==null)
                asciiEncoder = Charset.forName("ASCII").newEncoder();
            else
                asciiEncoder.reset();
            
            if (asciiEncoder.canEncode(charBuf)) {
	        kind = 0x5; // standard ASCII
	        byteBuf = asciiEncoder.encode(charBuf);
            } else {
                if(utf16beEncoder==null)
                    utf16beEncoder = Charset.forName("UTF-16BE").newEncoder();
                else
                    utf16beEncoder.reset();
                
	        kind = 0x6; // UTF-16-BE
	        byteBuf = utf16beEncoder.encode(charBuf);
            }
        }
	byte[] bytes = new byte[byteBuf.remaining()];
	byteBuf.get(bytes);
	out.writeIntHeader(kind, content.length());
	out.write(bytes);
    }

    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        indent(ascii, level);
        ascii.append("\"");
        //According to https://developer.apple.com/library/mac/#documentation/Cocoa/Conceptual/PropertyLists/OldStylePlists/OldStylePLists.html
        //non-ASCII characters are not escaped but simply written into the 
        //file, thus actually violating the ASCII plain text format
        ascii.append(content);
        ascii.append("\"");        
    }

    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        indent(ascii, level);
        ascii.append("\"");
        ascii.append(escapeStringForASCII(content));
        ascii.append("\"");
    }
    
    /**
     * Escapes a string for use in ASCII property lists.
     * @param s The unescaped string.
     * @return The escaped string.
     */
    static String escapeStringForASCII(String s) {
        String out = "";
        char[] cArray = s.toCharArray();
        for(int i=0;i<cArray.length;i++) {
            char c = cArray[i];
            if(c > 127) {
                //non-ASCII Unicode
                out += "\\U";
                String hex = Integer.toHexString(c);
                while(hex.length() < 4)
                    hex = "0"+hex;
                out += hex;
            } else if(c == '\\') {
                out += "\\\\";
            } else if(c == '\"') {
                out += "\\\"";
            } else if(c == '\b') {
                out += "\\b";
            } else if(c == '\n') {
                out += "\\n";
            } else if(c == '\r') {
                out += "\\r";
            } else if(c == '\t') {
                out += "\\t";
            } else {
                out += c;
            }
        }
        return out;
    }
}
