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
import java.nio.ByteOrder;

/**
 * The UID class holds a unique identifier.
 * Only found in binary property lists that are keyed archives.
 *
 * @author Daniel Dreibrodt
 */
public class UID extends NSObject {

    private long value;

    /**
     * Creates a new UID instance.
     * @param bytes The UID value.
     */
    public UID(byte[] bytes) throws UnsupportedEncodingException {
        if (bytes.length != 1 && bytes.length != 2 && bytes.length != 4 && bytes.length != 8) {
            throw new UnsupportedEncodingException("Invalid Length of UID");
        }
        value = BinaryPropertyListParser.parseLong(bytes);
    }

    public UID(byte aByte) {
        value = aByte;
    }

    public UID(short number)
    {
        value = number;
    }

    public UID(int number)
    {
        value = number;
    }

    public UID(long number)
    {
        value = number;
    }

    public int getByteCount()
    {
            if(value <= 0xFF) return 1;
            if(value <= 0xFFFF) return 2;
            if(value <= 0xFFFFFFFF) return 4;
            return 8;
    }

    /**
     * Gets this instance's value.
     * @return The UID's value.
     */
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(getByteCount());
        buffer.order(ByteOrder.BIG_ENDIAN);
        switch (getByteCount()) {
            case 1:
                buffer.put((byte)value);
                break;
            case 2:
                buffer.putShort((short)value);
                break;
            case 4:
                buffer.putInt((int)value);
                break;
            case 8:
            case 16:
                buffer.putLong(value);
                break;
        }
        return buffer.array();
    }

    @Override
    public UID clone() {
        return new UID(this.value);
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
        xml.append("<dict>");
        xml.append(NSObject.NEWLINE);

        this.indent(xml, level + 1);
        xml.append("<key>CF$UID</key>");
        xml.append(NSObject.NEWLINE);

        this.indent(xml, level + 1);
        xml.append("<integer>" + value +  "</integer>");
        xml.append(NSObject.NEWLINE);

        this.indent(xml, level);
        xml.append("</dict>");

    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        out.write(0x80 + getByteCount() - 1);
        out.write(getBytes());
    }

    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        this.indent(ascii, level);
        ascii.append('"');
        byte[] bytes = getBytes();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
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
