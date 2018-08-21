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
import java.util.*;

/**
 * The NSSet class is an unordered collection of NSObject instances.
 * This implementation uses a <code>LinkedHashSet</code> or <code>TreeSet</code>as the underlying
 * data structure.
 *
 * @author Daniel Dreibrodt
 * @see LinkedHashSet
 */
public class NSSet extends NSObject {

    private Set<NSObject> set;

    private boolean ordered = false;

    /**
     * Creates a new NSSet instance. The created set is unordered.
     *
     * @see java.util.LinkedHashSet
     */
    public NSSet() {
        this.set = new LinkedHashSet<NSObject>();
    }

    /**
     * Creates a new NSSet instance.
     *
     * @param ordered Indicates whether the created set should be ordered or unordered.
     * @see java.util.LinkedHashSet
     * @see java.util.TreeSet
     */
    public NSSet(boolean ordered) {
        this.ordered = ordered;
        if (!ordered)
            this.set = new LinkedHashSet<NSObject>();
        else
            this.set = new TreeSet<NSObject>();
    }

    /**
     * Creates a new NSSet instance with the specified content. The created set is unordered.
     *
     * @param objects The objects to populate the set.
     * @see java.util.LinkedHashSet
     */
    public NSSet(NSObject... objects) {
        this.set = new LinkedHashSet<NSObject>();
        this.set.addAll(Arrays.asList(objects));
    }

    /**
     * Create a new NSSet instance with the specified content.
     *
     * @param ordered Indicates whether the created set should be ordered or unordered.
     * @param objects The objects to populate the set.
     * @see java.util.LinkedHashSet
     * @see java.util.TreeSet
     */
    public NSSet(boolean ordered, NSObject... objects) {
        this.ordered = ordered;
        if (!ordered)
            this.set = new LinkedHashSet<NSObject>();
        else
            this.set = new TreeSet<NSObject>();
        this.set.addAll(Arrays.asList(objects));
    }

    /**
     * Adds an object to the set.
     *
     * @param obj The object to add.
     */
    public synchronized void addObject(NSObject obj) {
        this.set.add(obj);
    }

    /**
     * Removes an object from the set.
     *
     * @param obj The object to remove.
     */
    public synchronized void removeObject(NSObject obj) {
        this.set.remove(obj);
    }

    /**
     * Returns all objects contained in the set.
     *
     * @return An array of all objects in the set.
     */
    public synchronized NSObject[] allObjects() {
        return this.set.toArray(new NSObject[this.count()]);
    }

    /**
     * Returns one of the objects in the set, or <code>null</code>
     * if the set contains no objects.
     *
     * @return The first object in the set, or <code>null</code> if the set is empty.
     */
    public synchronized NSObject anyObject() {
        if (this.set.isEmpty())
            return null;
        else
            return this.set.iterator().next();
    }

    /**
     * Finds out whether the given object is contained in the set.
     *
     * @param obj The object to look for.
     * @return <code>true</code>, when the object was found, <code>false</code> otherwise.
     */
    public boolean containsObject(NSObject obj) {
        return this.set.contains(obj);
    }

    /**
     * Determines whether the set contains an object equal to the given object
     * and returns that object if it is present.
     *
     * @param obj The object to look for.
     * @return The object if it is present, <code>null</code> otherwise.
     */
    public synchronized NSObject member(NSObject obj) {
        for (NSObject o : this.set) {
            if (o.equals(obj))
                return o;
        }
        return null;
    }

    /**
     * Finds out whether at least one object is present in both sets.
     *
     * @param otherSet The other set.
     * @return <code>false</code> if the intersection of both sets is empty, <code>true</code> otherwise.
     */
    public synchronized boolean intersectsSet(NSSet otherSet) {
        for (NSObject o : this.set) {
            if (otherSet.containsObject(o))
                return true;
        }
        return false;
    }

    /**
     * Finds out if this set is a subset of the given set.
     *
     * @param otherSet The other set.
     * @return <code>true</code> if all elements in this set are also present in the other set, <code>false</code> otherwise.
     */
    public synchronized boolean isSubsetOfSet(NSSet otherSet) {
        for (NSObject o : this.set) {
            if (!otherSet.containsObject(o))
                return false;
        }
        return true;
    }

    /**
     * Returns an iterator object that lets you iterate over all elements of the set.
     * This is the equivalent to <code>objectEnumerator</code> in the Cocoa implementation
     * of NSSet.
     *
     * @return The iterator for the set.
     */
    public synchronized Iterator<NSObject> objectIterator() {
        return this.set.iterator();
    }

    /**
     * Gets the underlying data structure in which this NSSets stores its content.
     * @return A Set object.
     */
    Set<NSObject> getSet() {
        return this.set;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.set != null ? this.set.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final NSSet other = (NSSet) obj;
        return !(this.set != other.set && (this.set == null || !this.set.equals(other.set)));
    }

    /**
     * Gets the number of elements in the set.
     *
     * @return The number of elements in the set.
     * @see Set#size()
     */
    public synchronized int count() {
        return this.set.size();
    }

    @Override
    public NSSet clone() {
        NSObject[] clonedSet = new NSObject[this.set.size()];
        int i = 0;
        for(NSObject element : this.set) {
            clonedSet[i++] = element != null ? element.clone() : null;
        }

        return new NSSet(this.ordered, clonedSet);
    }

    /**
     * Returns the XML representation for this set.
     * There is no official XML representation specified for sets.
     * In this implementation it is represented by an array.
     *
     * @param xml   The XML StringBuilder
     * @param level The indentation level
     */
    @Override
    void toXML(StringBuilder xml, int level) {
        this.indent(xml, level);
        xml.append("<array>");
        xml.append(NSObject.NEWLINE);
        for (NSObject o : this.set) {
            o.toXML(xml, level + 1);
            xml.append(NSObject.NEWLINE);
        }
        this.indent(xml, level);
        xml.append("</array>");
    }

    @Override
    void assignIDs(BinaryPropertyListWriter out) {
        super.assignIDs(out);
        for (NSObject obj : this.set) {
            obj.assignIDs(out);
        }
    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        if (this.ordered) {
            out.writeIntHeader(0xB, this.set.size());
        } else {
            out.writeIntHeader(0xC, this.set.size());
        }
        for (NSObject obj : this.set) {
            out.writeID(out.getID(obj));
        }
    }

    /**
     * Returns the ASCII representation of this set.
     * There is no official ASCII representation for sets.
     * In this implementation sets are represented as arrays.
     *
     * @param ascii The ASCII file string builder
     * @param level The indentation level
     */
    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        this.indent(ascii, level);
        NSObject[] array = this.allObjects();
        ascii.append(ASCIIPropertyListParser.ARRAY_BEGIN_TOKEN);
        int indexOfLastNewLine = ascii.lastIndexOf(NEWLINE);
        for (int i = 0; i < array.length; i++) {
            Class<?> objClass = array[i].getClass();
            if ((objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class))
                    && indexOfLastNewLine != ascii.length()) {
                ascii.append(NEWLINE);
                indexOfLastNewLine = ascii.length();
                array[i].toASCII(ascii, level + 1);
            } else {
                if (i != 0)
                    ascii.append(' ');
                array[i].toASCII(ascii, 0);
            }

            if (i != array.length - 1)
                ascii.append(ASCIIPropertyListParser.ARRAY_ITEM_DELIMITER_TOKEN);

            if (ascii.length() - indexOfLastNewLine > ASCII_LINE_LENGTH) {
                ascii.append(NEWLINE);
                indexOfLastNewLine = ascii.length();
            }
        }
        ascii.append(ASCIIPropertyListParser.ARRAY_END_TOKEN);
    }

    /**
     * Returns the ASCII representation of this set according to the GnuStep format.
     * There is no official ASCII representation for sets.
     * In this implementation sets are represented as arrays.
     *
     * @param ascii The ASCII file string builder
     * @param level The indentation level
     */
    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        this.indent(ascii, level);
        NSObject[] array = this.allObjects();
        ascii.append(ASCIIPropertyListParser.ARRAY_BEGIN_TOKEN);
        int indexOfLastNewLine = ascii.lastIndexOf(NEWLINE);
        for (int i = 0; i < array.length; i++) {
            Class<?> objClass = array[i].getClass();
            if ((objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class))
                    && indexOfLastNewLine != ascii.length()) {
                ascii.append(NEWLINE);
                indexOfLastNewLine = ascii.length();
                array[i].toASCIIGnuStep(ascii, level + 1);
            } else {
                if (i != 0)
                    ascii.append(' ');
                array[i].toASCIIGnuStep(ascii, 0);
            }

            if (i != array.length - 1)
                ascii.append(ASCIIPropertyListParser.ARRAY_ITEM_DELIMITER_TOKEN);

            if (ascii.length() - indexOfLastNewLine > ASCII_LINE_LENGTH) {
                ascii.append(NEWLINE);
                indexOfLastNewLine = ascii.length();
            }
        }
        ascii.append(ASCIIPropertyListParser.ARRAY_END_TOKEN);
    }

}
