/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2011-2017 Daniel Dreibrodt
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The NSData class is a wrapper for a byte buffer.
 * @see <a href="https://developer.apple.com/reference/foundation/nsdata" target="_blank">Foundation NSData documentation</a>
 * @author Daniel Dreibrodt
 */
public class NSData extends NSObject {

    private final byte[] bytes;

    /**
     * Creates a new NSData instance with the specified content.
     *
     * @param bytes The data content.
     */
    public NSData(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Creates a new NSData instance with the specified Base64 encoded content.
     *
     * @param base64 The Base64 encoded data content.
     * @throws IOException When the given string is not a proper Base64 formatted string.
     */
    public NSData(String base64) throws IOException {
        //Remove all white spaces from the string so that it is parsed completely
        //and not just until the first white space occurs.
        String data = base64.replaceAll("\\s+", "");
        this.bytes = Base64.decode(data, Base64.DONT_GUNZIP);
    }

    /**
     * Creates a new NSData instance with the specified file as content.
     *
     * @param file The file containing the data.
     * @throws FileNotFoundException If the file could not be found.
     * @throws IOException           If the file could not be read.
     */
    public NSData(File file) throws IOException {
        this.bytes = new byte[(int) file.length()];
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.read(this.bytes);
        raf.close();
    }

    /**
     * Returns the bytes contained in this instance.
     *
     * @return The data as bytes
     */
    public byte[] bytes() {
        return this.bytes;
    }

    /**
     * Returns the number of bytes stored in this instance.
     *
     * @return The number of bytes contained in this object.
     */
    public int length() {
        return this.bytes.length;
    }

    /**
     * Copies data from this instance into the specified buffer.
     *
     * @param buf    The byte buffer which will contain the data.
     * @param length The number of bytes to copy.
     */
    public void getBytes(ByteBuffer buf, int length) {
        buf.put(this.bytes, 0, Math.min(this.bytes.length, length));
    }

    /**
     * Copies data from this instance into the specified buffer.
     *
     * @param buf        The byte buffer which will contain the data.
     * @param rangeStart The index from which to start copying.
     * @param rangeStop  The index at which to stop copying.
     */
    public void getBytes(ByteBuffer buf, int rangeStart, int rangeStop) {
        buf.put(this.bytes, rangeStart, Math.min(this.bytes.length, rangeStop));
    }

    /**
     * Gets the Base64 encoded data contained in this instance.
     *
     * @return The data as a Base64 encoded <code>String</code>.
     */
    public String getBase64EncodedData() {
        return Base64.encodeBytes(this.bytes);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass()) && Arrays.equals(((NSData) obj).bytes, this.bytes);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Arrays.hashCode(this.bytes);
        return hash;
    }

    @Override
    public NSData clone() {
        return new NSData(this.bytes.clone());
    }

    @Override
    void toXML(StringBuilder xml, int level) {
        this.indent(xml, level);
        xml.append("<data>");
        xml.append(NSObject.NEWLINE);
        String base64 = this.getBase64EncodedData();
        for (String line : base64.split("\n")) {
            this.indent(xml, level + 1);
            xml.append(line);
            xml.append(NSObject.NEWLINE);
        }
        this.indent(xml, level);
        xml.append("</data>");
    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        out.writeIntHeader(0x4, this.bytes.length);
        out.write(this.bytes);
    }

    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        this.indent(ascii, level);
        ascii.append(ASCIIPropertyListParser.DATA_BEGIN_TOKEN);
        int indexOfLastNewLine = ascii.lastIndexOf(NEWLINE);
        for (int i = 0; i < this.bytes.length; i++) {
            int b = this.bytes[i] & 0xFF;
            if (b < 16)
                ascii.append('0');
            ascii.append(Integer.toHexString(b));
            if (ascii.length() - indexOfLastNewLine > ASCII_LINE_LENGTH) {
                ascii.append(NEWLINE);
                indexOfLastNewLine = ascii.length();
            } else if ((i + 1) % 2 == 0 && i != this.bytes.length - 1) {
                ascii.append(' ');
            }
        }
        ascii.append(ASCIIPropertyListParser.DATA_END_TOKEN);
    }

    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        this.toASCII(ascii, level);
    }
}
