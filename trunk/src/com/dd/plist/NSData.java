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
     * @return
     */
    public byte[] bytes() {
        return bytes;
    }

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
     * @return
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

    public String toXML(String indent) {
        String xml = indent + "<data>" + System.getProperty("line.separator");
        String base64 = getBase64EncodedData();
        for (String line : base64.split("\n")) {
            xml += indent + "  " + line + System.getProperty("line.separator");
        }
        xml += indent + "</data>";
        return xml;
    }
}
