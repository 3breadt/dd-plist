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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A NSDictionary is a collection of keys and values, essentially a Hashtable.
 * The keys are simple Strings whereas the values can be any kind of NSObject.
 * @see java.util.Hashtable
 * @see com.dd.plist.NSObject
 * @author Daniel Dreibrodt
 */
public class NSDictionary extends NSObject {

    private HashMap<String, NSObject> dict;

    /**
     * Creates a new empty NSDictionary.
     */
    public NSDictionary() {
        //With a linked HashMap the order of elements in the dictionary is kept.
        dict = new LinkedHashMap<String, NSObject>();
    }

    /**
     * Gets the NSObject stored for the given key.
     * @param key The key.
     * @return The object.
     */
    public NSObject objectForKey(String key) {
        return dict.get(key);
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * @param key The key.
     * @param obj The value.
     */
    public void put(String key, NSObject obj) {
        dict.put(key, obj);
    }

    public void put(String key, String obj) {
        put(key, new NSString(obj));
    }

    public void put(String key, int obj) {
        put(key, new NSNumber(obj));
    }

    public void put(String key, long obj) {
        put(key, new NSNumber(obj));
    }

    public void put(String key, double obj) {
        put(key, new NSNumber(obj));
    }

    public void put(String key, boolean obj) {
        put(key, new NSNumber(obj));
    }

    public void put(String key, Date obj) {
        put(key, new NSDate(obj));
    }

    public void put(String key, byte[] obj) {
        put(key, new NSData(obj));
    }

    /**
     * Counts the number of contained key-value pairs.
     * @return The size of this NSDictionary.
     */
    public int count() {
        return dict.size();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass().equals(this.getClass()) && ((NSDictionary)obj).dict.equals(dict));
    }

    public String[] allKeys() {
        return dict.keySet().toArray(new String[0]);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.dict != null ? this.dict.hashCode() : 0);
        return hash;
    }

    public void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        xml.append("<dict>");
        xml.append(NSObject.NEWLINE);
        for (String key:dict.keySet()) {
            NSObject val = objectForKey(key);
            indent(xml, level+1);
            xml.append("<key>");
            //According to http://www.w3.org/TR/REC-xml/#syntax node values must not
            //contain the characters < or &. Also the > character should be escaped.
            if(key.contains("&") || key.contains("<") || key.contains(">")) {
                xml.append("<![CDATA[");
                xml.append(key.replaceAll("]]>", "]]]]><![CDATA[>"));
                xml.append("]]>");
            }
            else {
                xml.append(key);
            }
            xml.append("</key>");
            xml.append(NSObject.NEWLINE);
            val.toXML(xml, level+1);
            xml.append(NSObject.NEWLINE);
        }
        indent(xml, level);
        xml.append("</dict>");        
    }

    @Override
    void assignIDs(BinaryPropertyListWriter out) {
	super.assignIDs(out);
	for (Map.Entry<String,NSObject> entry : dict.entrySet()) {
	    new NSString(entry.getKey()).assignIDs(out);
	    entry.getValue().assignIDs(out);
	}
    }

    public void toBinary(BinaryPropertyListWriter out) throws IOException {
	out.writeIntHeader(0xD, dict.size());
	Set<Map.Entry<String,NSObject>> entries = dict.entrySet();
	for (Map.Entry<String,NSObject> entry : entries) {
	    out.writeID(out.getID(new NSString(entry.getKey())));
	}
	for (Map.Entry<String,NSObject> entry : entries) {
	    out.writeID(out.getID(entry.getValue()));
	}
    }
}
