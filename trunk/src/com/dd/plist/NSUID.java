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

/**
 * A NSUID contains a UID. Only found in binary property lists.
 * @author Daniel Dreibrodt
 */
public class NSUID extends NSObject {

    private byte[] bytes;
    private String name;

    public NSUID(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getName() {
        return name;
    }

    public void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        xml.append("<string>");
        xml.append(new String(bytes));
        xml.append("</string>");
    }
    
    void toBinary(BinaryPropertyListWriter out) throws IOException {
	out.write(0x80 + bytes.length - 1);
	out.write(bytes);
    }
}
