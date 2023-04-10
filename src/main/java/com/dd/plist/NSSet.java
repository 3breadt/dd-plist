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
import java.util.stream.Collectors;

/**
 * The NSSet class is an unordered collection of NSObject instances.
 * This implementation uses a <code>LinkedHashSet</code> or <code>TreeSet</code>as the underlying
 * data structure.
 *
 * @author Daniel Dreibrodt
 * @see LinkedHashSet
 */
public class NSSet extends NSObject {

    private final Set<NSObject> set;

    private boolean ordered = false;

    /**
     * Creates a new NSSet instance. The created set is unordered.
     *
     * @see java.util.LinkedHashSet
     */
    public NSSet() {
        this.set = new LinkedHashSet<>();
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
        this.set = ordered ? new TreeSet<>() : new LinkedHashSet<>();
    }

    /**
     * Creates a new NSSet instance with the specified content. The created set is unordered.
     *
     * @param objects The objects to populate the set.
     * @see java.util.LinkedHashSet
     */
    public NSSet(NSObject... objects) {
        this.set = new LinkedHashSet<>();
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
        this(ordered);
        this.set.addAll(Arrays.stream(objects).map(NSNull::wrap).collect(Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Adds an object to the set.
     *
     * @param obj The object to add.
     */
    public synchronized void addObject(NSObject obj) {
        this.set.add(NSNull.wrap(obj));
    }

    /**
     * Removes an object from the set.
     *
     * @param obj The object to remove.
     */
    public synchronized void removeObject(NSObject obj) {
        this.set.remove(NSNull.wrap(obj));
    }

    /**
     * Returns all objects contained in the set.
     *
     * @return An array of all objects in the set.
     */
    public synchronized NSObject[] allObjects() {
        return this.set.stream().map(NSNull::unwrap).toArray(NSObject[]::new);
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
            return NSNull.unwrap(this.set.iterator().next());
    }

    /**
     * Finds out whether the given object is contained in the set.
     *
     * @param obj The object to look for.
     * @return <code>true</code>, when the object was found, <code>false</code> otherwise.
     */
    public boolean containsObject(NSObject obj) {
        return this.set.contains(NSNull.wrap(obj));
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
            if (o.equals(NSNull.wrap(obj)))
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
        return this.set.stream().map(NSNull::unwrap).iterator();
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

        return this.compareTo((NSObject) obj) == 0;
    }

    @Override
    public int compareTo(NSObject o) {
        Objects.requireNonNull(o);
        if (o == this) {
            return 0;
        } else if (o instanceof NSSet) {
            NSSet other = (NSSet) o;
            if (other.count() != this.count()) {
                return Integer.compare(this.count(), other.count());
            }

            NSObject[] thisObjects = this.allObjects();
            NSObject[] otherObjects = other.allObjects();

            for (int i = 0; i < this.count(); i++) {
                int itemDiff = NSNull.wrap(thisObjects[i]).compareTo(NSNull.wrap((otherObjects[i])));
                if (itemDiff != 0) {
                    return itemDiff;
                }
            }

            return 0;
        } else {
            return this.getClass().getName().compareTo(o.getClass().getName());
        }
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
        for (NSObject element : this.set) {
            clonedSet[i++] = element != null ? element.clone() : null;
        }

        return new NSSet(this.ordered, clonedSet);
    }

    @Override
    public Object toJavaObject() {
        Set<Object> clonedSet = this.ordered ? new TreeSet<>() : new LinkedHashSet<>(this.set.size());
        for (NSObject o : this.set) {
            clonedSet.add(o.toJavaObject());
        }
        return clonedSet;
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
        new NSArray(this.allObjects()).toXML(xml, level);
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
        new NSArray(this.allObjects()).toASCII(ascii, level);
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
        new NSArray(this.allObjects()).toASCIIGnuStep(ascii, level);
    }
}
