/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2011 Keith Randall
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A BinaryPropertyListWriter is a helper class for writing out
 * binary property list files.  It contains an output stream and
 * various structures for keeping track of which NSObjects have
 * already been serialized, and where they were put in the file.
 * @author Keith Randall
 */
public class BinaryPropertyListWriter {
    /**
     * Writes a binary plist file with the given object as the root.
     * @param file the file to write to
     * @param root the source of the data to write to the file
     */
    public static void write(File file, NSObject root) throws IOException {
	OutputStream out = new FileOutputStream(file);
	write(out, root);
	out.close();
    }
    /**
     * Writes a binary plist serialization of the given object as the root.
     * @param out the stream to write to
     * @param root the source of the data to write to the stream
     */
    public static void write(OutputStream out, NSObject root) throws IOException {
	BinaryPropertyListWriter w = new BinaryPropertyListWriter(out);
	w.write(root);
    }
    
    // raw output stream to result file
    OutputStream out;
    
    // # of bytes written so far
    long count;
    
    // map from object to its ID
    Map<NSObject,Integer> idMap = new HashMap<NSObject,Integer>();
    int idSizeInBytes;
    
    BinaryPropertyListWriter(OutputStream outStr) throws IOException {
	out = new BufferedOutputStream(outStr);
    }
    void write(NSObject root) throws IOException {
	// magic number
	write("bplist00".getBytes());
	
	// assign IDs to all the objects.
	root.assignIDs(this);
	
	idSizeInBytes = computeIdSizeInBytes(idMap.size());
	
	// offsets of each object, indexed by ID
	long[] offsets = new long[idMap.size()];
	
	// write each object, save offset
	for (Map.Entry<NSObject,Integer> entry : idMap.entrySet()) {
	    NSObject obj = entry.getKey();
	    int id = entry.getValue();
	    offsets[id] = count;
	    if (obj == null) {
		write(0x00);
	    } else {
		obj.toBinary(this);
	    }
	}
	
	// write offset table
	long offsetTableOffset = count;
	int offsetSizeInBytes = computeOffsetSizeInBytes(count);
	for (long offset : offsets) {
	    writeBytes(offset, offsetSizeInBytes);
	}
	
	// write trailer
	// 6 null bytes
	write(new byte[6]);
	// size of an offset
	write(offsetSizeInBytes);
	// size of a ref
	write(idSizeInBytes);
	// number of objects
	writeLong(idMap.size());
	// top object
	writeLong(idMap.get(root));
	// offset table offset
	writeLong(offsetTableOffset);
	
	out.flush();
    }
    
    void assignID(NSObject obj) {
	if (!idMap.containsKey(obj)) {
	    idMap.put(obj, idMap.size());
	}
    }
    
    int getID(NSObject obj) {
	return idMap.get(obj);
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
	if (value < 15) {
	    write((kind << 4) + value);
	} else if (value < 256) {
	    write((kind << 4) + 15);
	    write(0x10);
	    writeBytes(value, 1);
	} else if (value < 65536) {
	    write((kind << 4) + 15);
	    write(0x11);
	    writeBytes(value, 2);
	} else {
	    write((kind << 4) + 15);
	    write(0x12);
	    writeBytes(value, 4);
	}
    }
    
    void write(int b) throws IOException {
	out.write(b);
	count++;
    }
    
    void write(byte[] bytes) throws IOException {
	out.write(bytes);
	count += bytes.length;
    }
    
    void writeBytes(long value, int bytes) throws IOException {
	// write low-order bytes big-endian style
	for (int i = bytes - 1; i >= 0; i--) {
	    write((int)(value >> (8 * i)));
	}
    }
    
    void writeID(int id) throws IOException {
	writeBytes(id, idSizeInBytes);
    }
    
    void writeLong(long value) throws IOException {
	writeBytes(value, 8);
    }
    
    void writeDouble(double value) throws IOException {
	writeLong(Double.doubleToRawLongBits(value));
    }
}
