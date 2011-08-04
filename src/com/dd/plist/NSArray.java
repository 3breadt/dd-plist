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

    /**
     * Creates an empty array of the given length.
     * @param length The number of elements this array will be able to hold.
     */
    public NSArray(int length) {
        array = new NSObject[length];
    }

    /**
     * Creates a array from an existing one
     * @param a The array which should be wrapped by the NSArray
     */
    public NSArray(NSObject... a) {
        array = a;
    }

    /**
     * Returns the object stored at the given index.
     * Equivalent to <code>getArray()[i]</code>.
     * @param i The index of the object.
     * @return The object at the given index.
     */
    public NSObject objectAtIndex(int i) {
        return array[i];
    }

    /**
     * Stores an object at the specified index.
     * If there was another object stored at that index it will be replaced.
     * Equivalent to <code>getArray()[key] = value</code>.
     * @param key The index where to store the object.
     * @param value The object.
     */
    public void setValue(int key, NSObject value) {
        array[key] = value;
    }

    /**
     * Returns the array of NSObjects represented by this NSArray.
     * Any changes to the values of this array will also affect the NSArray.
     * @return The actual array represented by this NSArray.
     */
    public NSObject[] getArray() {
        return array;
    }

    /**
     * Returns the size of the array.
     * @return The number of elements that this array can store.
     */
    public int count() {
        return array.length;
    }

    /**
     * Checks whether an object is present in the array or whether it is equal
     * to any of the objects in the array.
     * @see Object#equals(java.lang.Object)
     * @param obj The object to look for.
     * @return <code>true</code>, when the object could be found. <code>false</code> otherwise.
     */
    public boolean containsObject(NSObject obj) {
        for (NSObject o : array) {
            if (o.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Searches for an object in the array. If it is found its index will be
     * returned. This method also returns an index if the object is not the same
     * as the one stored in the array but has equal contents.
     * @see Object#equals(java.lang.Object)
     * @see #indexOfIdenticalObject(com.dd.plist.NSObject)
     * @param obj The object to look for.
     * @return The index of the object, if it was found. -1 otherwise.
     */
    public int indexOfObject(NSObject obj) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Searches for an object in the array. If it is found its index will be
     * returned. This method only returns the index of an object that is
     * <b>identical</b> to the given one. Thus objects that might contain the
     * same value as the given one will not be considered.
     * @see #indexOfObject(com.dd.plist.NSObject)
     * @param obj The object to look for.
     * @return The index of the object, if it was found. -1 otherwise.
     */
    public int indexOfIdenticalObject(NSObject obj) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == obj) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last object contained in this array.
     * Equivalent to <code>getArray()[getArray().length-1]</code>.
     * @return The value of the highest index in the array.
     */
    public NSObject lastObject() {
        return array[array.length-1];
    }

    /**
     * Returns a new array containing only the values stored at the given
     * indices. The values are sorted by their index.
     * @param indexes The indices of the objects.
     * @return The new array containing the objects stored at the given indices.
     */
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
