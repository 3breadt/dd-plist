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
import java.util.Arrays;

/**
 * Represents an Array
 * @author Daniel Dreibrodt
 */
public class NSArray extends NSObject {

    private NSObject[] array;

    public NSArray(int length) {
        array = new NSObject[length];
    }

    public NSObject objectAtIndex(int i) {
        return array[i];
    }

    public void setValue(int key, NSObject value) {
        array[key] = value;
    }

    public NSObject[] getArray() {
        return array;
    }

    public int count() {
        return array.length;
    }

    public boolean containsObject(NSObject obj) {
        for (NSObject o : array) {
            if (o.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    public int indexOfObject(NSObject obj) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfIdenticalObject(NSObject obj) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == obj) {
                return i;
            }
        }
        return -1;
    }

    public NSObject lastObject() {
        return array[array.length-1];
    }

    public NSObject[] objectsAtIndexes(int... indexes) {
        NSObject[] result = new NSObject[indexes.length];
        Arrays.sort(indexes);
        for(int i=0;i<indexes.length;i++) result[i]=array[indexes[0]];
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass()) && Arrays.equals(((NSArray) obj).getArray(),this.array);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Arrays.deepHashCode(this.array);
        return hash;
    }

    public void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        xml.append("<array>");
        xml.append(NSObject.NEWLINE);
        for (NSObject o : array) {
            o.toXML(xml, level+1);
            xml.append(NSObject.NEWLINE);
        }
        indent(xml, level);
        xml.append("</array>");
    }

    @Override
    void assignIDs(BinaryPropertyListWriter out) {
	super.assignIDs(out);
	for (NSObject obj : array) {
	    obj.assignIDs(out);
	}
    }

    void toBinary(BinaryPropertyListWriter out) throws IOException {
	out.writeIntHeader(0xA, array.length);
	for (NSObject obj : array) {
	    out.writeID(out.getID(obj));
	}
    }
}
