/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2011 Daniel Dreibrodt
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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
        dict = new HashMap<String, NSObject>();
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
            xml.append("<key><![CDATA[");
            xml.append(key.replaceAll("]]>", "]]]]><![CDATA[>"));
            xml.append("]]></key>");
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
