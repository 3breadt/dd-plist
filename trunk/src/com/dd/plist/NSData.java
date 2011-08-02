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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * NSData objects are wrappers for byte buffers.
 * @author Daniel Dreibrodt
 */
public class NSData extends NSObject {

    private byte[] bytes;

    /**
     * Creates the NSData object from the binary representation of it.
     * @param bytes The raw data contained in the NSData object.
     */
    public NSData(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Creates a NSData object from its textual representation, which is a Base64 encoded amount of bytes.
     * @param base64 The Base64 encoded contents of the NSData object.
     */
    public NSData(String base64) {
        String data = "";
        for (String line : base64.split("\n")) {
            data += line.trim();
        }
        bytes = Base64.decode(data);
    }

    /**
     * Creates a NSData object from a file. Using the files contents as the contents of this NSData object.
     * @param file The file containing the data.
     * @throws FileNotFoundException If the file could not be found.
     * @throws IOException If the file could not be read.
     */
    public NSData(File file) throws FileNotFoundException, IOException {
        bytes = new byte[(int) file.length()];
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.read(bytes);
        raf.close();
    }

    /**
     * The bytes contained in this NSData object.
     * @return The data as bytes
     */
    public byte[] bytes() {
        return bytes;
    }

    /**
     * Gets the amount of data stored in this object.
     * @return The number of bytes contained in this object.
     */
    public int length() {
        return bytes.length;
    }

    /**
     * Loads the bytes from this NSData object into a byte buffer
     * @param buf The byte buffer which will contain the data
     * @param length The amount of data to copy
     */
    public void getBytes(ByteBuffer buf, int length) {
        buf.put(bytes, 0, Math.min(bytes.length, length));
    }

    /**
     * Loads the bytes from this NSData object into a byte buffer
     * @param buf The byte buffer which will contain the data
     * @param rangeStart The start index
     * @param rangeStop The stop index
     */
    public void getBytes(ByteBuffer buf, int rangeStart, int rangeStop) {
        buf.put(bytes, rangeStart, Math.min(bytes.length, rangeStop));
    }

    /**
     * Gets the Base64 encoded data contained in this NSData object.
     * @return The Base64 encoded data as a <code>String</code>.
     */
    public String getBase64EncodedData() {
        return Base64.encodeBytes(bytes);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass()) && Arrays.equals(((NSData) obj).bytes, bytes);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Arrays.hashCode(this.bytes);
        return hash;
    }

    public void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        xml.append("<data>");
        xml.append(NSObject.NEWLINE);
        String base64 = getBase64EncodedData();
        for (String line : base64.split("\n")) {
            indent(xml, level+1);
            xml.append(line);
            xml.append(NSObject.NEWLINE);
        }
        indent(xml, level);
        xml.append("</data>");
    }

    void toBinary(BinaryPropertyListWriter out) throws IOException {
	out.writeIntHeader(0x4, bytes.length);
	out.write(bytes);
    }
}
