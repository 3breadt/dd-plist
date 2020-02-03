/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2012 Keith Randall
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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A BinaryPropertyListWriter is a helper class for writing out
 * binary property list files.  It contains an output stream and
 * various structures for keeping track of which NSObjects have
 * already been serialized, and where they were put in the file.
 *
 * @author Keith Randall
 */
public final class BinaryPropertyListWriter {

    private static final int VERSION_00 = 0;
    private static final int VERSION_10 = 10;
    private static final int VERSION_15 = 15;
    private static final int VERSION_20 = 20;

    private int version = VERSION_00;

    // raw output stream to result file
    private final OutputStream out;

    // # of bytes written so far
    private long count;

    // map from object to its ID
    private final Map<NSObject, Integer> idMap = new LinkedHashMap<NSObject, Integer>();
    private int idSizeInBytes;

    /**
     * Creates a new binary property list writer
     *
     * @param outStr The output stream into which the binary property list will be written
     * @throws IOException If an I/O error occurs while writing to the stream or the object structure contains
     *                     data that cannot be saved.
     */
    BinaryPropertyListWriter(OutputStream outStr) throws IOException {
        this.out = new BufferedOutputStream(outStr);
    }

    BinaryPropertyListWriter(OutputStream outStr, int version) throws IOException {
        this.version = version;
        this.out = new BufferedOutputStream(outStr);
    }

    /**
     * Finds out the minimum binary property list format version that
     * can be used to save the given NSObject tree.
     *
     * @param root Object root
     * @return Version code
     */
    private static int getMinimumRequiredVersion(NSObject root) {
        int minVersion = VERSION_00;
        if (root == null) {
            minVersion = VERSION_10;
        }
        if (root instanceof NSDictionary) {
            NSDictionary dict = (NSDictionary) root;
            for (NSObject o : dict.getHashMap().values()) {
                int v = getMinimumRequiredVersion(o);
                if (v > minVersion)
                    minVersion = v;
            }
        } else if (root instanceof NSArray) {
            NSArray array = (NSArray) root;
            for (NSObject o : array.getArray()) {
                int v = getMinimumRequiredVersion(o);
                if (v > minVersion)
                    minVersion = v;
            }
        } else if (root instanceof NSSet) {
            //Sets are only allowed in property lists v1+
            minVersion = VERSION_10;
            NSSet set = (NSSet) root;
            for (NSObject o : set.allObjects()) {
                int v = getMinimumRequiredVersion(o);
                if (v > minVersion)
                    minVersion = v;
            }
        }
        return minVersion;
    }

    /**
     * Writes a binary plist file with the given object as the root.
     *
     * @param file the file to write to
     * @param root the source of the data to write to the file
     * @throws IOException If an I/O error occurs while writing to the file or the object structure contains
     *                     data that cannot be saved.
     */
    public static void write(File file, NSObject root) throws IOException {
        OutputStream fileOutputStream = new FileOutputStream(file);
        try {
            write(fileOutputStream, root);
        }
        finally {
            try {
                fileOutputStream.close();
            }
            catch (IOException ex) {
                // ignore
            }
        }
    }

    /**
     * Writes a binary plist serialization of the given object as the root.
     * This method does not close the output stream.
     *
     * @param out  the stream to write to
     * @param root the source of the data to write to the stream
     * @throws IOException If an I/O error occurs while writing to the stream or the object structure contains
     *                     data that cannot be saved.
     */
    public static void write(OutputStream out, NSObject root) throws IOException {
        int minVersion = getMinimumRequiredVersion(root);
        if (minVersion > VERSION_00) {
            String versionString = minVersion == VERSION_10 ? "v1.0" : (minVersion == VERSION_15 ? "v1.5" : (minVersion == VERSION_20 ? "v2.0" : "v0.0"));
            throw new IOException("The given property list structure cannot be saved. " +
                    "The required version of the binary format (" + versionString + ") is not yet supported.");
        }

        BinaryPropertyListWriter w = new BinaryPropertyListWriter(out, minVersion);
        w.write(root);
    }

    /**
     * Writes a binary plist serialization of the given object as the root
     * into a byte array.
     *
     * @param root The root object of the property list
     * @return The byte array containing the serialized property list
     * @throws IOException If an I/O error occurs while writing to the stream or the object structure contains
     *                     data that cannot be saved.
     */
    public static byte[] writeToArray(NSObject root) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        write(bout, root);
        return bout.toByteArray();
    }

    void write(NSObject root) throws IOException {
        // magic bytes
        this.write(new byte[]{'b', 'p', 'l', 'i', 's', 't'});

        //version
        switch (this.version) {
            case VERSION_00: {
                this.write(new byte[]{'0', '0'});
                break;
            }
            case VERSION_10: {
                this.write(new byte[]{'1', '0'});
                break;
            }
            case VERSION_15: {
                this.write(new byte[]{'1', '5'});
                break;
            }
            case VERSION_20: {
                this.write(new byte[]{'2', '0'});
                break;
            }
            default:
                break;
        }

        // assign IDs to all the objects.
        root.assignIDs(this);

        this.idSizeInBytes = computeIdSizeInBytes(this.idMap.size());

        // offsets of each object, indexed by ID
        long[] offsets = new long[this.idMap.size()];

        // write each object, save offset
        for (Map.Entry<NSObject, Integer> entry : this.idMap.entrySet()) {
            NSObject obj = entry.getKey();
            int id = entry.getValue();
            offsets[id] = this.count;
            if (obj == null) {
                this.write(0x00);
            } else {
                obj.toBinary(this);
            }
        }

        // write offset table
        long offsetTableOffset = this.count;
        int offsetSizeInBytes = this.computeOffsetSizeInBytes(this.count);
        for (long offset : offsets) {
            this.writeBytes(offset, offsetSizeInBytes);
        }

        if (this.version != VERSION_15) {
            // write trailer
            // 6 null bytes
            this.write(new byte[6]);
            // size of an offset
            this.write(offsetSizeInBytes);
            // size of a ref
            this.write(this.idSizeInBytes);
            // number of objects
            this.writeLong(this.idMap.size());
            // top object
            this.writeLong(this.idMap.get(root));
            // offset table offset
            this.writeLong(offsetTableOffset);
        }

        this.out.flush();
    }

    void assignID(NSObject obj) {
        if (!this.idMap.containsKey(obj)) {
            this.idMap.put(obj, this.idMap.size());
        }
    }

    int getID(NSObject obj) {
        return this.idMap.get(obj);
    }

    private static int computeIdSizeInBytes(int numberOfIds) {
        if (numberOfIds < 256) return 1;
        if (numberOfIds < 65536) return 2;
        return 4;
    }

    private int computeOffsetSizeInBytes(long maxOffset) {
        if (maxOffset < 256) return 1;
        if (maxOffset < 65536) return 2;
        if (maxOffset < 4294967296L) return 4;
        return 8;
    }

    void writeIntHeader(int kind, int value) throws IOException {
        assert value >= 0;
        if (value < 15) {
            this.write((kind << 4) + value);
        } else if (value < 256) {
            this.write((kind << 4) + 15);
            this.write(0x10);
            this.writeBytes(value, 1);
        } else if (value < 65536) {
            this.write((kind << 4) + 15);
            this.write(0x11);
            this.writeBytes(value, 2);
        } else {
            this.write((kind << 4) + 15);
            this.write(0x12);
            this.writeBytes(value, 4);
        }
    }

    void write(int b) throws IOException {
        this.out.write(b);
        this.count++;
    }

    void write(byte[] bytes) throws IOException {
        this.out.write(bytes);
        this.count += bytes.length;
    }

    void writeBytes(long value, int bytes) throws IOException {
        // write low-order bytes big-endian style
        for (int i = bytes - 1; i >= 0; i--) {
            this.write((int) (value >> (8 * i)));
        }
    }

    void writeID(int id) throws IOException {
        this.writeBytes(id, this.idSizeInBytes);
    }

    void writeLong(long value) throws IOException {
        this.writeBytes(value, 8);
    }

    void writeDouble(double value) throws IOException {
        this.writeLong(Double.doubleToRawLongBits(value));
    }
}
