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
 * The UID class holds a unique identifier.
 * Only found in binary property lists that are keyed archives.
 *
 * @author Daniel Dreibrodt
 */
public class UID extends NSObject {

    private final byte[] bytes;
    private final String name;

    /**
     * Creates a new UID instance.
     * @param name The UID name.
     * @param bytes The UID value.
     */
    public UID(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    /**
     * Gets this instance's value.
     * @return The UID's value.
     */
    public byte[] getBytes() {
        return this.bytes;
    }

    /**
     * Gets this instance's name.
     * @return The UID's name.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public UID clone() {
        return new UID(this.name, this.bytes.clone());
    }

    /**
     * There is no XML representation specified for UIDs.
     * In this implementation UIDs are represented as hexadecimal strings in the XML output.
     *
     * @param xml   The XML StringBuilder
     * @param level The indentation level
     */
    @Override
    void toXML(StringBuilder xml, int level) {
        this.indent(xml, level);
        xml.append("<string>");
        for (int i = 0; i < this.bytes.length; i++) {
            byte b = this.bytes[i];
            if (b < 16)
                xml.append('0');
            xml.append(Integer.toHexString(b));
        }
        xml.append("</string>");
    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        out.write(0x80 + this.bytes.length - 1);
        out.write(this.bytes);
    }

    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        this.indent(ascii, level);
        ascii.append('"');
        for (int i = 0; i < this.bytes.length; i++) {
            byte b = this.bytes[i];
            if (b < 16)
                ascii.append('0');
            ascii.append(Integer.toHexString(b));
        }
        ascii.append('"');
    }

    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        this.toASCII(ascii, level);
    }
}
