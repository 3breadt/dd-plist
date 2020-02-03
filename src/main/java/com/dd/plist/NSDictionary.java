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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The NSDictionary class is a collection of NSObject instances that are identified by strings.
 * It represents a hash map where the keys are strings and the values NSObject instances.
 * The implementation is based on a linked hash map, so that the order of the elements in the dictionary is preserved.
 *
 * You can access the keys through the function <code>allKeys()</code>.
 * Access to the objects stored for each key is given through the function <code>objectForKey(String key)</code>.
 *
 * @author Daniel Dreibrodt
 * @see <a href="https://developer.apple.com/reference/foundation/nsdictionary" target="_blank">Foundation NSDictionary documentation</a>
 * @see java.util.LinkedHashMap
 */
public class NSDictionary extends NSObject  implements Map<String, NSObject> {

    private final HashMap<String, NSObject> dict;

    /**
     * Creates a new NSDictionary instance.
     */
    public NSDictionary() {
        this.dict = new LinkedHashMap<String, NSObject>();
    }

    /**
     * Gets the hash map which stores the keys and values of this dictionary.
     * Changes to the hash map are directly reflected in this dictionary.
     *
     * @return The hash map which is used by this dictionary to store its contents.
     */
    public HashMap<String, NSObject> getHashMap() {
        return this.dict;
    }

    /**
     * Gets the NSObject stored for the given key.
     *
     * @param key The key.
     * @return The object.
     */
    public NSObject objectForKey(String key) {
        return this.dict.get(key);
    }

    public int size() {
        return this.dict.size();
    }

    public boolean isEmpty() {
        return this.dict.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.dict.containsKey(key);
    }

    public boolean containsValue(Object value) {
        if(value == null)
            return false;
        NSObject wrap = NSObject.fromJavaObject(value);
        return this.dict.containsValue(wrap);
    }

    public NSObject get(Object key) {
        return this.dict.get(key);
    }

    public void putAll(Map<? extends String, ? extends NSObject> values) {
        for (Object object : values.entrySet()) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, NSObject> entry = (Map.Entry<String, NSObject>) object;
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * If the value is null, no operation will be performed on the dictionary.
     *
     * @param key The key.
     * @param obj The value.
     * @return The value previously associated to the given key,
     *         or null, if no value was associated to it.
     */
    public NSObject put(String key, NSObject obj) {
        if(key == null)
            return null;
        if(obj == null)
            return this.dict.get(key);
        return this.dict.put(key, obj);
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * If key or value are null, no operation will be performed on the dictionary.
     *
     * @param key The key.
     * @param obj The value. Supported object types are numbers, byte-arrays, dates, strings and arrays or sets of those.
     * @return The value previously associated to the given key,
     *         or null, if no value was associated to it.
     */
    public NSObject put(String key, Object obj) {
        return this.put(key, NSObject.fromJavaObject(obj));
    }

    /**
     * Removes a key-value pair from this dictionary.
     *
     * @param key The key
     * @return the value previously associated to the given key.
     */
    public NSObject remove(String key) {
        return this.dict.remove(key);
    }

    public NSObject remove(Object key) {
        return this.dict.remove(key);
    }

    /**
     * Removes all key-value pairs from this dictionary.
     * @see java.util.Map#clear()
     */
    public void clear() {
        this.dict.clear();
    }

    public Set<String> keySet() {
        return this.dict.keySet();
    }

    public Collection<NSObject> values() {
        return this.dict.values();
    }

    public Set<Entry<String, NSObject>> entrySet() {
        return this.dict.entrySet();
    }

    /**
     * Checks whether a given key is contained in this dictionary.
     *
     * @param key The key that will be searched for.
     * @return Whether the key is contained in this dictionary.
     */
    public boolean containsKey(String key) {
        return this.dict.containsKey(key);
    }

    /**
     * Checks whether a given value is contained in this dictionary.
     *
     * @param val The value that will be searched for.
     * @return Whether the key is contained in this dictionary.
     */
    public boolean containsValue(NSObject val) {
        return val != null && this.dict.containsValue(val);
    }

    /**
     * Checks whether a given value is contained in this dictionary.
     *
     * @param val The value that will be searched for.
     * @return Whether the key is contained in this dictionary.
     */
    public boolean containsValue(String val) {
        for (NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSString.class)) {
                NSString str = (NSString) o;
                if (str.getContent().equals(val))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given value is contained in this dictionary.
     *
     * @param val The value that will be searched for.
     * @return Whether the key is contained in this dictionary.
     */
    public boolean containsValue(long val) {
        for (NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSNumber.class)) {
                NSNumber num = (NSNumber) o;
                if (num.isInteger() && num.intValue() == val)
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given value is contained in this dictionary.
     *
     * @param val The value that will be searched for.
     * @return Whether the key is contained in this dictionary.
     */
    public boolean containsValue(double val) {
        for (NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSNumber.class)) {
                NSNumber num = (NSNumber) o;
                if (num.isReal() && num.doubleValue() == val)
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given value is contained in this dictionary.
     *
     * @param val The value that will be searched for.
     * @return Whether the key is contained in this dictionary.
     */
    public boolean containsValue(boolean val) {
        for (NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSNumber.class)) {
                NSNumber num = (NSNumber) o;
                if (num.isBoolean() && num.boolValue() == val)
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given value is contained in this dictionary.
     *
     * @param val The value that will be searched for.
     * @return Whether the key is contained in this dictionary.
     */
    public boolean containsValue(Date val) {
        for (NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSDate.class)) {
                NSDate dat = (NSDate) o;
                if (dat.getDate().equals(val))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given value is contained in this dictionary.
     *
     * @param val The value that will be searched for.
     * @return Whether the key is contained in this dictionary.
     */
    public boolean containsValue(byte[] val) {
        for (NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSData.class)) {
                NSData dat = (NSData) o;
                if (Arrays.equals(dat.bytes(), val))
                    return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of contained key-value pairs.
     *
     * @return The size of this NSDictionary.
     */
    public int count() {
        return this.dict.size();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass()) && ((NSDictionary) obj).dict.equals(this.dict);
    }

    /**
     * Gets a list of all keys used in this NSDictionary.
     *
     * @return The list of all keys used in this NSDictionary.
     */
    public String[] allKeys() {
        return this.dict.keySet().toArray(new String[this.count()]);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.dict.hashCode();
        return hash;
    }

    @Override
    public NSDictionary clone() {
        NSDictionary clone = new NSDictionary();
        for(Entry<String, NSObject> entry : this.dict.entrySet()) {
            clone.dict.put(entry.getKey(), entry.getValue() != null ? entry.getValue().clone() : null);
        }

        return clone;
    }

    @Override
    void toXML(StringBuilder xml, int level) {
        this.indent(xml, level);
        xml.append("<dict>");
        xml.append(NSObject.NEWLINE);
        for (String key : this.dict.keySet()) {
            NSObject val = this.objectForKey(key);
            this.indent(xml, level + 1);
            xml.append("<key>");
            //According to http://www.w3.org/TR/REC-xml/#syntax node values must not
            //contain the characters < or &. Also the > character should be escaped.
            if (key.contains("&") || key.contains("<") || key.contains(">")) {
                xml.append("<![CDATA[");
                xml.append(key.replaceAll("]]>", "]]]]><![CDATA[>"));
                xml.append("]]>");
            } else {
                xml.append(key);
            }
            xml.append("</key>");
            xml.append(NSObject.NEWLINE);
            val.toXML(xml, level + 1);
            xml.append(NSObject.NEWLINE);
        }
        this.indent(xml, level);
        xml.append("</dict>");
    }

    @Override
    void assignIDs(BinaryPropertyListWriter out) {
        super.assignIDs(out);

        for (Map.Entry<String, NSObject> entry : this.dict.entrySet()) {
            new NSString(entry.getKey()).assignIDs(out);
        }

        for (Map.Entry<String, NSObject> entry : this.dict.entrySet()) {
            entry.getValue().assignIDs(out);
        }
    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        out.writeIntHeader(0xD, this.dict.size());
        Set<Map.Entry<String, NSObject>> entries = this.dict.entrySet();
        for (Map.Entry<String, NSObject> entry : entries) {
            out.writeID(out.getID(new NSString(entry.getKey())));
        }
        for (Map.Entry<String, NSObject> entry : entries) {
            out.writeID(out.getID(entry.getValue()));
        }
    }

    /**
     * Generates a valid ASCII property list which has this NSDictionary as its
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
     * NSDictionary as its root object. The generated property list complies with
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
        ascii.append(ASCIIPropertyListParser.DICTIONARY_BEGIN_TOKEN);
        ascii.append(NEWLINE);
        String[] keys = this.allKeys();
        for (String key : keys) {
            NSObject val = this.objectForKey(key);
            this.indent(ascii, level + 1);
            ascii.append('"');
            ascii.append(NSString.escapeStringForASCII(key));
            ascii.append("\" =");
            Class<?> objClass = val.getClass();
            if (objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) {
                ascii.append(NEWLINE);
                val.toASCII(ascii, level + 2);
            } else {
                ascii.append(' ');
                val.toASCII(ascii, 0);
            }
            ascii.append(ASCIIPropertyListParser.DICTIONARY_ITEM_DELIMITER_TOKEN);
            ascii.append(NEWLINE);
        }
        this.indent(ascii, level);
        ascii.append(ASCIIPropertyListParser.DICTIONARY_END_TOKEN);
    }

    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        this.indent(ascii, level);
        ascii.append(ASCIIPropertyListParser.DICTIONARY_BEGIN_TOKEN);
        ascii.append(NEWLINE);
        String[] keys = this.dict.keySet().toArray(new String[this.dict.size()]);
        for (String key : keys) {
            NSObject val = this.objectForKey(key);
            this.indent(ascii, level + 1);
            ascii.append('"');
            ascii.append(NSString.escapeStringForASCII(key));
            ascii.append("\" =");
            Class<?> objClass = val.getClass();
            if (objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) {
                ascii.append(NEWLINE);
                val.toASCIIGnuStep(ascii, level + 2);
            } else {
                ascii.append(' ');
                val.toASCIIGnuStep(ascii, 0);
            }
            ascii.append(ASCIIPropertyListParser.DICTIONARY_ITEM_DELIMITER_TOKEN);
            ascii.append(NEWLINE);
        }
        this.indent(ascii, level);
        ascii.append(ASCIIPropertyListParser.DICTIONARY_END_TOKEN);
    }
}
