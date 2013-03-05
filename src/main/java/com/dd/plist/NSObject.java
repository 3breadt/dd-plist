/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2012 Daniel Dreibrodt
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Abstract interface for any object contained in a property list.
 * The names and functions of the various objects orient themselves
 * towards Apple's Cocoa API.
 *
 * @author Daniel Dreibrodt
 */
public abstract class NSObject {

    /**
     * The newline character used for generating the XML output.
     * This constant will be different depending on the operating system on
     * which you use this library.
     */
    final static String NEWLINE = System.getProperty("line.separator");

    /**
     * The identation character used for generating the XML output. This is the
     * tabulator character.
     */
    final static String INDENT = "\t";

    /**
     * The maximum length of the text lines to be used when generating
     * ASCII property lists. But this number is only a guideline it is not
     * guaranteed that it will not be overstepped.
     */
    final static int ASCII_LINE_LENGTH = 80;

    /**
     * Generates the XML representation of the object (without XML headers or enclosing plist-tags).
     *
     * @param xml   The StringBuilder onto which the XML representation is appended.
     * @param level The indentation level of the object.
     */
    abstract void toXML(StringBuilder xml, int level);

    /**
     * Assigns IDs to all the objects in this NSObject subtree.
     *
     * @param out The writer object that handles the binary serialization.
     */
    void assignIDs(BinaryPropertyListWriter out) {
        out.assignID(this);
    }

    /**
     * Generates the binary representation of the object.
     *
     * @param out The output stream to serialize the object to.
     */
    abstract void toBinary(BinaryPropertyListWriter out) throws IOException;

    /**
     * Generates a valid XML property list including headers using this object as root.
     *
     * @return The XML representation of the property list including XML header and doctype information.
     */
    public String toXMLPropertyList() {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append(NSObject.NEWLINE);
        xml.append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
        xml.append(NSObject.NEWLINE);
        xml.append("<plist version=\"1.0\">");
        xml.append(NSObject.NEWLINE);
        toXML(xml, 0);
        xml.append(NSObject.NEWLINE);
        xml.append("</plist>");
        return xml.toString();
    }

    /**
     * Generates the ASCII representation of this object.
     * The generated ASCII representation does not end with a newline.
     * Complies with https://developer.apple.com/library/mac/#documentation/Cocoa/Conceptual/PropertyLists/OldStylePlists/OldStylePLists.html
     *
     * @param ascii The StringBuilder onto which the ASCII representation is appended.
     * @param level The indentation level of the object.
     */
    protected abstract void toASCII(StringBuilder ascii, int level);

    /**
     * Generates the ASCII representation of this object in the GnuStep format.
     * The generated ASCII representation does not end with a newline.
     *
     * @param ascii The StringBuilder onto which the ASCII representation is appended.
     * @param level The indentation level of the object.
     */
    protected abstract void toASCIIGnuStep(StringBuilder ascii, int level);

    /**
     * Helper method that adds correct identation to the xml output.
     * Calling this method will add <code>level</code> number of tab characters
     * to the <code>xml</code> string.
     *
     * @param xml   The string builder for the XML document.
     * @param level The level of identation.
     */
    void indent(StringBuilder xml, int level) {
        for (int i = 0; i < level; i++)
            xml.append(INDENT);
    }

    /**
     * Wraps the given value inside a NSObject.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     */
    public static NSString wrap(String value) {
        return new NSString(value);
    }

    /**
     * Wraps the given value inside a NSObject.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     */
    public static NSNumber wrap(long value) {
        return new NSNumber(value);
    }

    /**
     * Wraps the given value inside a NSObject.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     */
    public static NSNumber wrap(double value) {
        return new NSNumber(value);
    }

    /**
     * Wraps the given value inside a NSObject.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     */
    public static NSNumber wrap(boolean value) {
        return new NSNumber(value);
    }

    /**
     * Wraps the given value inside a NSObject.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     */
    public static NSDate wrap(Date value) {
        return new NSDate(value);
    }

    /**
     * Wraps the given value inside a NSObject.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     */
    public static NSData wrap(byte[] value) {
        return new NSData(value);
    }

    /**
     * Wraps the given value inside a NSObject.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     */
    public static NSData wrap(Serializable value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(value);
        oos.close();

        return wrap(baos.toByteArray());
    }

    /**
     * Creates a NSArray with the contents of the given array.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     * @throws RuntimeException When one of the objects contained in the array cannot be represented by a NSObject.
     */
    public static NSArray wrap(Object[] value) {
        NSArray arr = new NSArray(value.length);
        for (int i = 0; i < value.length; i++) {
            arr.setValue(i, wrap(value[i]));
        }
        return arr;
    }

    /**
     * Creates a NSDictionary with the contents of the given map.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     * @throws RuntimeException When one of the values contained in the map cannot be represented by a NSObject.
     */
    public static NSDictionary wrap(Map<String, Object> value) {
        NSDictionary dict = new NSDictionary();
        for (String key : value.keySet())
            dict.put(key, wrap(value.get(key)));
        return dict;
    }

    /**
     * Creates a NSSet with the contents of this set.
     *
     * @param value The value to represent as a NSObject.
     * @return A NSObject representing the given value.
     * @throws RuntimeException When one of the values contained in the set cannot be represented by a NSObject.
     */
    public static NSSet wrap(Set<Object> value) {
        NSSet set = new NSSet();
        for (Object o : value.toArray())
            set.addObject(wrap(o));
        return set;
    }

    /**
     * Creates a NSObject representing the given Java Object.
     * Numerics of type bool, int, long, short, byte, float or double are wrapped as NSNumber objects.
     * Strings are wrapped as NSString objects abd byte arrays as NSData objects.
     * Serializable classes are serialized and their data is stored in NSData objects.
     * Arrays are converted to NSArrays where each array member is converted to a NSObject by the above rules.
     *
     * @param o The object to represent.
     * @return A NSObject equivalent to the given object.
     */
    public static NSObject wrap(Object o) {
        Class<? extends Object> c = o.getClass();
        if (Boolean.class.isAssignableFrom(c)) {
            return wrap((boolean) (Boolean) o);
        }
        if (Integer.class.isAssignableFrom(c)) {
            return wrap((int) (Integer) o);
        }
        if (Long.class.isAssignableFrom(c)) {
            return wrap((long) (Long) o);
        }
        if (Short.class.isAssignableFrom(c)) {
            return wrap((int) (Short) o);
        }
        if (Byte.class.isAssignableFrom(c)) {
            return wrap((int) (Byte) o);
        }
        if (Float.class.isAssignableFrom(c)) {
            return wrap((double) (Float) o);
        }
        if (Double.class.isAssignableFrom(c)) {
            return wrap((double) (Double) o);
        }
        if (String.class.equals(c)) {
            return wrap((String) o);
        }
        if (byte[].class.equals(c)) {
            return wrap((byte[]) o);
        }
        if (Object[].class.isAssignableFrom(c)) {
            return wrap((Object[]) o);
        }
        if (c.isAssignableFrom(Serializable.class)) {
            try {
                return wrap((Serializable) o);
            } catch (IOException ex) {
                return wrapSerialized(o);
            }
        }
        return wrapSerialized(o);
    }

    /**
     * Serializes the given object using Java's default object serialization
     * and wraps the serialized object in a NSData object.
     *
     * @param o The object to serialize and wrap.
     * @return A NSData object
     * @throws RuntimeException When the object could not be serialized.
     */
    private static NSData wrapSerialized(Object o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            return new NSData(baos.toByteArray());
        } catch (IOException ex) {
            throw new RuntimeException("The given object of class " + o.getClass().toString() + " could not be serialized and stored in a NSData object.");
        }
    }
}
