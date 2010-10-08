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

    public String toXML(String indent) {
        String xml = indent + "<array>" + System.getProperty("line.separator");
        for (NSObject o : array) {
            xml += o.toXML(indent + "  ") + System.getProperty("line.separator");
        }
        xml += indent + "</array>";
        return xml;
    }
}
