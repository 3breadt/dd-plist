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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Parses binary property lists
 * @author Daniel Dreibrodt
 */
public class BinaryPropertyListParser {

    /** property list in bytes **/
    private static byte[] bytes;
    /** Length of an offset definition in bytes **/
    private static int offsetSize;
    /** Length of an object reference in bytes **/
    private static int objectRefSize;
    /** Number of objects stored in this property list **/
    private static int numObjects;
    /** Reference to the top object of the property list **/
    private static int topObject;
    /** Offset of the offset table from the beginning of the file **/
    private static int offsetTableOffset;
    /** The table holding the information at which offset each object is found **/
    private static int[] offsetTable;

    /**
     * Parses a binary property list from a byte array
     * @param data binary property list content
     * @return The top PropertyListObject, typically a Dictionary
     * @throws java.lang.Exception
     */
    public static NSObject parse(byte[] data) throws Exception {
        bytes = data;
        String magic = new String(copyOfRange(bytes, 0, 8));
        if (!magic.equals("bplist00")) {
            throw new Exception("The given data is no binary property list. Wrong magic bytes: " + magic);
        }

        /*
         * Handle trailer, last 32 bits of the file
         */
        byte[] trailer = copyOfRange(bytes, bytes.length - 32, bytes.length);
        //6 null bytes (index 0 to 5)
        offsetSize = (int) parseUnsignedInt(copyOfRange(trailer, 6, 7));
        //System.out.println("offsetSize: "+offsetSize);
        objectRefSize = (int) parseUnsignedInt(copyOfRange(trailer, 7, 8));
        //System.out.println("objectRefSize: "+objectRefSize);
        numObjects = (int) parseUnsignedInt(copyOfRange(trailer, 8, 16));
        //System.out.println("numObjects: "+numObjects);
        topObject = (int) parseUnsignedInt(copyOfRange(trailer, 16, 24));
        //System.out.println("topObject: "+topObject);
        offsetTableOffset = (int) parseUnsignedInt(copyOfRange(trailer, 24, 32));
        //System.out.println("offsetTableOffset: "+offsetTableOffset);

        /*
         * Handle offset table
         */
        offsetTable = new int[numObjects];

        for (int i = 0; i < numObjects; i++) {
            byte[] offsetBytes = copyOfRange(bytes, offsetTableOffset + i * offsetSize, offsetTableOffset + (i + 1) * offsetSize);
            offsetTable[i] = (int) parseUnsignedInt(offsetBytes);
            /*System.out.print("Offset for Object #"+i+" is "+offsetTable[i]+" [");
            for(byte b:offsetBytes) System.out.print(Integer.toHexString(b)+" ");
            System.out.println("]");*/
        }

        return parseObject(topObject);
    }

    /**
     * Parses a binary property list from an InputStream
     * @param is The input stream
     * @return The top PropertyListObject, typically a Dictionary
     * @throws java.lang.Exception
     */
    public static NSObject parse(InputStream is) throws Exception {
        //Read all bytes into a list
        if (is.available() > Runtime.getRuntime().freeMemory()) {
            throw new Exception("To little heap space available! Wanted to read " + is.available() + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
        }
        byte[] buf = new byte[is.available()];
        is.read(buf);
        is.close();
        return parse(buf);
    }

    /**
     * Parses a binary property list file
     * @param f The binary property list file
     * @return The top PropertyListObject, typically a Dictionary
     * @throws java.lang.Exception
     */
    public static NSObject parse(File f) throws Exception {
        if (f.length() > Runtime.getRuntime().freeMemory()) {
            throw new Exception("To little heap space available! Wanted to read " + f.length() + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
        }
        return parse(new FileInputStream(f));
    }

    /**
     * Parses an object inside the currently parsed binary property list
     * @param obj The object id
     * @return The parsed object
     * @throws java.lang.Exception
     */
    private static NSObject parseObject(int obj) throws Exception {
        int offset = offsetTable[obj];
        byte type = bytes[offset];
        int objType = (type & 0xF0) >> 4; //First  4 bits
        int objInfo = (type & 0x0F);      //Second 4 bits
        //System.out.println("Parsing object #"+obj+" @ ["+offset+"]: "+objType+" ("+objInfo+")");
        switch (objType) {
            case 0x0: {
                //Simple
                switch (objInfo) {
                    case 0x0: {
                        //null
                        return null;
                    }
                    case 0x8: {
                        //false
                        return new NSBoolean(false);
                    }
                    case 0x9: {
                        //true
                        return new NSBoolean(true);
                    }
                    case 0xF: {
                        //filler byte
                        return null;
                    }
                }
                break;
            }
            case 0x1: {
                //integer
                int length = (int) Math.pow(2, objInfo);
                if (length < Runtime.getRuntime().freeMemory()) {
                    return new NSInteger(copyOfRange(bytes, offset + 1, offset + 1 + length));
                } else {
                    throw new Exception("To little heap space available! Wanted to read " + length + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
                }
            }
            case 0x2: {
                //real
                int length = (int) Math.pow(2, objInfo);
                if (length < Runtime.getRuntime().freeMemory()) {
                    return new NSReal(copyOfRange(bytes, offset + 1, offset + 1 + length));
                } else {
                    throw new Exception("To little heap space available! Wanted to read " + length + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
                }
            }
            case 0x3: {
                //Date
                if (objInfo != 0x3) {
                    System.err.println("Unknown date type :" + objInfo + ". Parsing anyway...");
                }
                return new NSDate(copyOfRange(bytes, offset + 1, offset + 9));
            }
            case 0x4: {
                //Data
                int dataoffset = 1;
                int length = objInfo;
                if (objInfo == 0xF) {
                    int int_type = bytes[offset + 1];
                    int intType = (int_type & 0xF0) / 0xF;
                    if (intType != 0x1) {
                        System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType);
                    }
                    int intInfo = int_type & 0x0F;
                    int intLength = (int) Math.pow(2, intInfo);
                    dataoffset = 2 + intLength;
                    if (intLength < 3) {
                        length = (int) parseUnsignedInt(copyOfRange(bytes, offset + 2, offset + 2 + intLength));
                    } else {
                        length = new BigInteger(copyOfRange(bytes, offset + 2, offset + 2 + intLength)).intValue();
                    }
                }
                if (length < Runtime.getRuntime().freeMemory()) {
                    return new NSData(copyOfRange(bytes, offset + dataoffset, offset + dataoffset + length));
                } else {
                    throw new Exception("To little heap space available! Wanted to read " + length + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
                }
            }
            case 0x5: {
                //ASCII String
                int length = objInfo;
                int stroffset = 1;
                if (objInfo == 0xF) {
                    int int_type = bytes[offset + 1];
                    int intType = (int_type & 0xF0) / 0xF;
                    if (intType != 0x1) {
                        System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType);
                    }
                    int intInfo = int_type & 0x0F;
                    int intLength = (int) Math.pow(2, intInfo);
                    stroffset = 2 + intLength;
                    if (intLength < 3) {
                        length = (int) parseUnsignedInt(copyOfRange(bytes, offset + 2, offset + 2 + intLength));
                    } else {
                        length = new BigInteger(copyOfRange(bytes, offset + 2, offset + 2 + intLength)).intValue();
                    }
                }
                if (length < Runtime.getRuntime().freeMemory()) {
                    return new NSString(copyOfRange(bytes, offset + stroffset, offset + stroffset + length), "ASCII");
                } else {
                    throw new Exception("To little heap space available! Wanted to read " + length + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
                }
            }
            case 0x6: {
                //UTF-16-BE String
                int length = objInfo;
                int stroffset = 1;
                if (objInfo == 0xF) {
                    int int_type = bytes[offset + 1];
                    int intType = (int_type & 0xF0) / 0xF;
                    if (intType != 0x1) {
                        System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType);
                    }
                    int intInfo = int_type & 0x0F;
                    int intLength = (int) Math.pow(2, intInfo);
                    stroffset = 2 + intLength;
                    if (intLength < 3) {
                        length = (int) parseUnsignedInt(copyOfRange(bytes, offset + 2, offset + 2 + intLength));
                    } else {
                        length = new BigInteger(copyOfRange(bytes, offset + 2, offset + 2 + intLength)).intValue();
                    }
                }
                if (length < Runtime.getRuntime().freeMemory()) {
                    return new NSString(copyOfRange(bytes, offset + stroffset, offset + stroffset + length), "UTF-16BE");
                } else {
                    throw new Exception("To little heap space available! Wanted to read " + length + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
                }
            }
            case 0x8: {
                //UID
                int length = objInfo + 1;
                if (length < Runtime.getRuntime().freeMemory()) {
                    return new NSUID(String.valueOf(obj), copyOfRange(bytes, offset + 1, offset + 1 + length));
                } else {
                    throw new Exception("To little heap space available! Wanted to read " + length + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
                }
            }
            case 0xA: {
                //Array
                int length = objInfo;
                int arrayoffset = 1;
                if (objInfo == 0xF) {
                    int int_type = bytes[offset + 1];
                    int intType = (int_type & 0xF0) / 0xF;
                    if (intType != 0x1) {
                        System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType);
                    }
                    int intInfo = int_type & 0x0F;
                    int intLength = (int) Math.pow(2, intInfo);
                    arrayoffset = 2 + intLength;
                    if (intLength < 3) {
                        length = (int) parseUnsignedInt(copyOfRange(bytes, offset + 2, offset + 2 + intLength));
                    } else {
                        length = new BigInteger(copyOfRange(bytes, offset + 2, offset + 2 + intLength)).intValue();
                    }
                }
                if (length * objectRefSize > Runtime.getRuntime().freeMemory()) {
                    throw new Exception("To little heap space available!");
                }
                NSArray array = new NSArray(length);
                for (int i = 0; i < length; i++) {
                    int objRef = (int) parseUnsignedInt(copyOfRange(bytes,
                            offset + arrayoffset + i * objectRefSize,
                            offset + arrayoffset + (i + 1) * objectRefSize));
                    array.setValue(i, parseObject(objRef));
                    //System.out.println("Element #"+i+" of the Array "+obj+" is: "+array[i].toString());
                }
                return array;

            }
            case 0xD: {
                //Dictionary
                int length = objInfo;
                int dictoffset = 1;
                if (objInfo == 0xF) {
                    int int_type = bytes[offset + 1];
                    int intType = (int_type & 0xF0) / 0xF;
                    if (intType != 0x1) {
                        System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType);
                    }
                    int intInfo = int_type & 0x0F;
                    int intLength = (int) Math.pow(2, intInfo);
                    dictoffset = 2 + intLength;
                    if (intLength < 3) {
                        length = (int) parseUnsignedInt(copyOfRange(bytes, offset + 2, offset + 2 + intLength));
                    } else {
                        length = new BigInteger(copyOfRange(bytes, offset + 2, offset + 2 + intLength)).intValue();
                    }
                }
                if (length * 2 * objectRefSize > Runtime.getRuntime().freeMemory()) {
                    throw new Exception("To little heap space available!");
                }
                //System.out.println("Parsing dictionary #"+obj);
                NSDictionary dict = new NSDictionary();
                for (int i = 0; i < length; i++) {
                    int keyRef = (int) parseUnsignedInt(copyOfRange(bytes,
                            offset + dictoffset + i * objectRefSize,
                            offset + dictoffset + (i + 1) * objectRefSize));
                    int valRef = (int) parseUnsignedInt(copyOfRange(bytes,
                            offset + dictoffset + (length * objectRefSize) + i * objectRefSize,
                            offset + dictoffset + (length * objectRefSize) + (i + 1) * objectRefSize));
                    NSObject key = parseObject(keyRef);
                    NSObject val = parseObject(valRef);
                    //System.out.println("  DICT #"+obj+": Mapped "+key.toString()+" to "+val.toString());
                    dict.put(key.toString(), val);
                }
                return dict;
            }
            default: {
                System.err.println("Unknown object type: " + objType);
            }
        }
        return null;
    }

    /**
     * Parses unsigned integers from a byte array
     * @param bytes
     * @return the parsed integer
     */
    public static final long parseUnsignedInt(byte[] bytes) {
        long l = 0;
        for (byte b : bytes) {
            l <<= 8;
            l |= b & 0xFF;
        }
        l &= 0xFFFFFFFFL;
        return l;
    }

    /**
     * Copies a part of a byte array into a new array
     * @param src The source array
     * @param startIndex The index from which to start copying
     * @param endIndex The index until which to copy
     * @return The copied array
     */
    public static byte[] copyOfRange(byte[] src, int startIndex, int endIndex) {
        int length = endIndex - startIndex;
        if (length < 0) {
            throw new IllegalArgumentException("startIndex (" + startIndex + ")" + " > endIndex (" + endIndex + ")");
        }
        byte[] dest = new byte[length];
        System.arraycopy(src, startIndex, dest, 0, length);
        return dest;
    }
}

