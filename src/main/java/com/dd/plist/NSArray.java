/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2014 Daniel Dreibrodt
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
import java.lang.reflect.Array;
import java.util.*;

/**
 * The NSArray class is a wrapper for an array of NSObject instances.
 *
 * @author Daniel Dreibrodt
 * @see <a href="https://developer.apple.com/reference/foundation/nsarray" target="_blank">Foundation NSArray documentation</a>
 */
public class NSArray extends NSObject implements List {

    private ArrayList<NSObject> array;

    /**
     * Creates a new NSArray instance of the specified size.
     *
     * @param length The number of elements the NSArray instance will be able to hold.
     */
    public NSArray(int length) {
        this.array = new ArrayList<NSObject>();
    }

    /**
     * Creates a new NSArray instance containing the specified elements.
     *
     * @param a The elements to be contained by the NSArray instance.
     */
    public NSArray(NSObject... a) {
        this.array = new ArrayList<NSObject>();
        for (int i = 0; i < a.length; i++) {
            this.array.add(a[i]);
        }
    }

    /**
     * Returns the object stored at the given index.
     * Equivalent to <code>getArray()[i]</code>.
     *
     * @param i The index of the object.
     * @return The object at the given index.
     */
    public NSObject objectAtIndex(int i) {
        return this.array.get(i);
    }

    /**
     * Removes the i-th element from the array.
     * The array will be resized.
     *
     * @param i The index of the object
     */
    public NSObject remove(int i) {
        return this.array.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return this.array.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.array.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return this.array.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return this.array.listIterator(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return this.array.subList(fromIndex, toIndex);
    }

    /**
     * Stores an object at the specified index.
     * If there was another object stored at that index it will be replaced.
     * Equivalent to <code>getArray()[key] = value</code>.
     *
     * @param key   The index where to store the object.
     * @param value The object.
     */
    public void setValue(int key, Object value) {
        this.array.set(key, NSObject.fromJavaObject(value));
    }

    /**
     * Returns the array of NSObjects represented by this NSArray.
     * Any changes to the values of this array will also affect the NSArray.
     *
     * @return The actual array represented by this NSArray.
     */
    public NSObject[] getArray() {
        NSObject[] results = new NSObject[array.size()];
        for(int i = 0; i < results.length; i++)
            results[i] = this.array.get(i);
        return results;
    }

    /**
     * Returns the size of the array.
     *
     * @return The number of elements that this array can store.
     */
    public int count() {
        return this.array.size();
    }

    /**
     * Checks whether an object is present in the array or whether it is equal
     * to any of the objects in the array.
     *
     * @param obj The object to look for.
     * @return <code>true</code>, when the object could be found. <code>false</code> otherwise.
     * @see Object#equals(java.lang.Object)
     */
    public boolean containsObject(Object obj) {
        NSObject nso = NSObject.fromJavaObject(obj);
        return this.array.contains(nso);
    }

    /**
     * Searches for an object in the array. If the specified object or an object equal to it is found,
     * its index is returned. Otherwise, -1 is returned.
     *
     * @param obj The object to look for.
     * @return The index of the object, if it was found. -1 otherwise.
     * @see Object#equals(java.lang.Object)
     * @see #indexOfIdenticalObject(Object)
     */
    public int indexOfObject(Object obj) {
        NSObject nso = NSObject.fromJavaObject(obj);
        return this.array.indexOf(nso);
    }

    /**
     * Searches for a specific object in the array. If the specified object is found (reference equality),
     * its index is returned. Otherwise, -1 is returned.
     *
     * @param obj The object to look for.
     * @return The index of the object, if it was found. -1 otherwise.
     * @see #indexOfObject(Object)
     */
    public int indexOfIdenticalObject(Object obj) {
        NSObject nso = NSObject.fromJavaObject(obj);
        for(int i = 0; i < this.array.size(); i++) {
            if(this.array.get(i) == nso) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last object contained in this array.
     * Equivalent to <code>getArray()[getArray().length-1]</code>.
     *
     * @return The value of the highest index in the array.
     */
    public NSObject lastObject() {
        return this.array.get(this.array.size() - 1);
    }

    /**
     * Returns a new array containing only the values stored at the given
     * indices. The values are sorted by their index.
     *
     * @param indexes The indices of the objects.
     * @return The new array containing the objects stored at the given indices.
     */
    public NSObject[] objectsAtIndexes(int... indexes) {
        NSObject[] result = new NSObject[indexes.length];
        Arrays.sort(indexes);
        for(int i = 0; i < indexes.length; i++)
            result[i] = this.array.get(indexes[i]);
        return result;
    }

    @Override
    public int size() {
        return array.size();
    }

    @Override
    public boolean isEmpty() {
        return array.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return containsObject(o);
    }

    @Override
    public Iterator iterator() {
        return array.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.array.toArray();
    }

    @Override
    public boolean add(Object o) {
        NSObject nso = NSObject.fromJavaObject(o);
        return this.array.add(nso);
    }

    @Override
    public boolean remove(Object o) {
        NSObject nso = NSObject.fromJavaObject(o);
        return this.array.remove(nso);
    }

    @Override
    public boolean addAll(Collection c) {
        return this.array.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return this.array.addAll(index, c);
    }

    @Override
    public void clear() {
        array.clear();
    }

    @Override
    public boolean retainAll(Collection c) {
        return this.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return array.removeAll(c);
    }

    @Override
    public boolean containsAll(Collection c) {
        return array.containsAll(c);
    }

    @Override
    public Object[] toArray(Object[] a) {
        return array.toArray(a);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(obj.getClass().equals(NSArray.class)) {
            return Arrays.equals(((NSArray) obj).getArray(), this.array.toArray());
        } else {
            NSObject nso = NSObject.fromJavaObject(obj);
            if(nso.getClass().equals(NSArray.class)) {
                return Arrays.equals(((NSArray) nso).getArray(), this.array.toArray());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Arrays.deepHashCode(this.array.toArray());
        return hash;
    }

    @Override
    public Object get(int index) {
        return array.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        NSObject nso = NSObject.fromJavaObject(element);
        return array.set(index, nso);
    }

    @Override
    public void add(int index, Object element) {
        NSObject nso = NSObject.fromJavaObject(element);
        this.array.add(index, nso);
    }

    @Override
    public NSArray clone() {
        NSObject[] clonedArray = new NSObject[this.array.size()];
        for(int i = 0; i < this.array.size(); i++) {
            clonedArray[i] = this.array.get(i) != null ? this.array.get(i).clone() : null;
        }

        return new NSArray(clonedArray);
    }

    @Override
    void toXML(StringBuilder xml, int level) {
        this.indent(xml, level);
        xml.append("<array>");
        xml.append(NSObject.NEWLINE);
        for(NSObject o : this.array) {
            o.toXML(xml, level + 1);
            xml.append(NSObject.NEWLINE);
        }
        this.indent(xml, level);
        xml.append("</array>");
    }

    @Override
    void assignIDs(BinaryPropertyListWriter out) {
        super.assignIDs(out);
        for(NSObject obj : this.array) {
            obj.assignIDs(out);
        }
    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        out.writeIntHeader(0xA, this.array.size());
        for(NSObject obj : this.array) {
            out.writeID(out.getID(obj));
        }
    }

    /**
     * Generates a valid ASCII property list which has this NSArray as its
     * root object. The generated property list complies with the format as
     * described in <a href="https://developer.apple.com/library/mac/#documentation/Cocoa/Conceptual/PropertyLists/OldStylePlists/OldStylePLists.html">
     * Property List Programming Guide - Old-Style ASCII Property Lists</a>.
     *
     * @return ASCII representation of this object.
     */
    public String toASCIIPropertyList() {
        StringBuilder ascii = new StringBuilder();
        this.toASCII(ascii, 0);
        ascii.append(NEWLINE);
        return ascii.toString();
    }

    /**
     * Generates a valid ASCII property list in GnuStep format which has this
     * NSArray as its root object. The generated property list complies with
     * the format as described in <a href="http://www.gnustep.org/resources/documentation/Developer/Base/Reference/NSPropertyList.html">
     * GnuStep - NSPropertyListSerialization class documentation
     * </a>
     *
     * @return GnuStep ASCII representation of this object.
     */
    public String toGnuStepASCIIPropertyList() {
        StringBuilder ascii = new StringBuilder();
        this.toASCIIGnuStep(ascii, 0);
        ascii.append(NEWLINE);
        return ascii.toString();
    }

    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        this.indent(ascii, level);
        ascii.append(ASCIIPropertyListParser.ARRAY_BEGIN_TOKEN);
        int indexOfLastNewLine = ascii.lastIndexOf(NEWLINE);
        for(int i = 0; i < this.array.size(); i++) {
            Class<?> objClass = this.array.get(i).getClass();
            if((objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class))
                    && indexOfLastNewLine != ascii.length()) {
                ascii.append(NEWLINE);
                indexOfLastNewLine = ascii.length();
                this.array.get(i).toASCII(ascii, level + 1);
            } else {
                if(i != 0)
                    ascii.append(' ');
                this.array.get(i).toASCII(ascii, 0);
            }

            if(i != this.array.size() - 1)
                ascii.append(ASCIIPropertyListParser.ARRAY_ITEM_DELIMITER_TOKEN);

            if(ascii.length() - indexOfLastNewLine > ASCII_LINE_LENGTH) {
                ascii.append(NEWLINE);
                indexOfLastNewLine = ascii.length();
            }
        }
        ascii.append(ASCIIPropertyListParser.ARRAY_END_TOKEN);
    }

    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        this.indent(ascii, level);
        ascii.append(ASCIIPropertyListParser.ARRAY_BEGIN_TOKEN);
        int indexOfLastNewLine = ascii.lastIndexOf(NEWLINE);
        for(int i = 0; i < this.array.size(); i++) {
            Class<?> objClass = this.array.get(i).getClass();
            if((objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class))
                    && indexOfLastNewLine != ascii.length()) {
                ascii.append(NEWLINE);
                indexOfLastNewLine = ascii.length();
                this.array.get(i).toASCIIGnuStep(ascii, level + 1);
            } else {
                if(i != 0)
                    ascii.append(' ');
                this.array.get(i).toASCIIGnuStep(ascii, 0);
            }

            if(i != this.array.size() - 1)
                ascii.append(ASCIIPropertyListParser.ARRAY_ITEM_DELIMITER_TOKEN);

            if(ascii.length() - indexOfLastNewLine > ASCII_LINE_LENGTH) {
                ascii.append(NEWLINE);
                indexOfLastNewLine = ascii.length();
            }
        }
        ascii.append(ASCIIPropertyListParser.ARRAY_END_TOKEN);
    }
}
