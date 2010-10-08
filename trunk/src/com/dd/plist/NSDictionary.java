/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2010 Daniel Dreibrodt
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

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A NSDictionary is a collection of keys and values, essentially a Hashtable.
 * The keys are simple Strings whereas the values can be any kind of NSObject.
 * @see java.util.Hashtable
 * @see com.dd.plist.NSObject
 * @author Daniel Dreibrodt
 */
public class NSDictionary extends NSObject {

    private Hashtable<String, NSObject> dict;

    /**
     * Creates a new empty NSDictionary.
     */
    public NSDictionary() {
        dict = new Hashtable<String, NSObject>();
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

    public String toXML(String indent) {
        String xml = indent + "<dict>" + System.getProperty("line.separator");
        Enumeration<String> keys = dict.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            NSObject val = objectForKey(key);
            xml += indent + "  <key>" + key + "</key>" + System.getProperty("line.separator");
            xml += val.toXML(indent + "  ") + System.getProperty("line.separator");
        }
        xml += indent + "</dict>";
        return xml;
    }
}
