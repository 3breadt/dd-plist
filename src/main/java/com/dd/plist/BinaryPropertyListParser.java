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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Parses property lists that are in Apple's binary format.
 * Use this class when you are sure about the format of the property list.
 * Otherwise, use the PropertyListParser class.
 * <p>
 * Parsing is done by calling the static <code>parse</code> methods.
 *
 * @author Daniel Dreibrodt
 */
public final class BinaryPropertyListParser {
    private static final int SIMPLE_TYPE = 0x00;
    private static final int INT_TYPE = 0x01;
    private static final int REAL_TYPE = 0x02;
    private static final int DATE_TYPE = 0x03;
    private static final int DATA_TYPE = 0x04;
    private static final int ASCII_STRING_TYPE = 0x05;
    private static final int UTF16_STRING_TYPE = 0x06;
    private static final int UTF8_STRING_TYPE = 0x07;
    private static final int UID_TYPE = 0x08;
    private static final int ARRAY_TYPE = 0x0A;
    private static final int ORDERED_SET_TYPE = 0x0B;
    private static final int SET_TYPE = 0x0C;
    private static final int DICTIONARY_TYPE = 0x0D;

    /**
     * Major version of the property list format
     */
    @SuppressWarnings("FieldCanBeLocal") //Useful when the features of different format versions are implemented
    private int majorVersion;

    /**
     * Minor version of the property list format
     */
    @SuppressWarnings("FieldCanBeLocal") //Useful when the features of different format versions are implemented
    private int minorVersion;

    /**
     * The property list data.
     */
    private byte[] bytes;

    /**
     * Length of an object reference in bytes
     */
    private int objectRefSize;
    private int offsetSize;
    private int numObjects;
    private int offsetTableOffset;

    /**
     * Protected constructor so that instantiation is fully controlled by the
     * static parse methods.
     *
     * @see BinaryPropertyListParser#parse(byte[])
     */
    private BinaryPropertyListParser() {
        /* empty */
    }

    /**
     * Parses a binary property list file.
     *
     * @param f The binary property list file
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws PropertyListFormatException When the property list's format could not be parsed.
     * @throws java.io.IOException         If a {@link NSString} object could not be decoded or an I/O error occurs on the input stream.
     */
    public static NSObject parse(File f) throws IOException, PropertyListFormatException {
        return parse(f.toPath());
    }

    /**
     * Parses a binary property list file.
     *
     * @param path The path to the binary property list file
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws PropertyListFormatException When the property list's format could not be parsed.
     * @throws java.io.IOException         If a {@link NSString} object could not be decoded or an I/O error occurs on the input stream.
     */
    public static NSObject parse(Path path) throws IOException, PropertyListFormatException {
        try (InputStream fileInputStream = Files.newInputStream(path)) {
            return parse(fileInputStream);
        }
    }

    /**
     * Parses a binary property list from an input stream.
     * This method does not close the specified input stream.
     *
     * @param is The input stream that points to the property list's data.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws PropertyListFormatException When the property list's format could not be parsed.
     * @throws java.io.IOException         If a {@link NSString} object could not be decoded or an I/O error occurs on the input stream.
     */
    public static NSObject parse(InputStream is) throws IOException, PropertyListFormatException {
        return parse(PropertyListParser.readAll(is));
    }

    /**
     * Parses a binary property list from a byte array.
     *
     * @param data The binary property list's data.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws PropertyListFormatException          When the property list's format could not be parsed.
     * @throws java.io.UnsupportedEncodingException If a {@link NSString} object could not be decoded.
     */
    public static NSObject parse(byte[] data) throws PropertyListFormatException, UnsupportedEncodingException {
        BinaryPropertyListParser parser = new BinaryPropertyListParser();
        return parser.doParse(data);
    }

    /**
     * Parses an unsigned integers from a byte array.
     *
     * @param bytes The byte array containing the unsigned integer.
     * @return The unsigned integer represented by the given bytes.
     */
    @SuppressWarnings("unused")
    public static long parseUnsignedInt(byte[] bytes) {
        return parseUnsignedInt(bytes, 0, bytes.length);
    }

    /**
     * Parses an unsigned integer from a byte array.
     *
     * @param bytes      The byte array containing the unsigned integer.
     * @param startIndex Beginning of the unsigned int in the byte array.
     * @param endIndex   End of the unsigned int in the byte array.
     * @return The unsigned integer represented by the given bytes.
     */
    public static long parseUnsignedInt(byte[] bytes, int startIndex, int endIndex) {
        long l = 0;
        for (int i = startIndex; i < endIndex; i++) {
            l <<= 8;
            l |= bytes[i] & 0xFF;
        }
        l &= 0xFFFFFFFFL;
        return l;
    }

    /**
     * Parses a long from a (big-endian) byte array.
     *
     * @param bytes The bytes representing the long integer.
     * @return The long integer represented by the given bytes.
     */
    @SuppressWarnings("unused")
    public static long parseLong(byte[] bytes) {
        return parseLong(bytes, 0, bytes.length);
    }

    /**
     * Parses a long from a (big-endian) byte array.
     *
     * @param bytes      The bytes representing the long integer.
     * @param startIndex Beginning of the long in the byte array.
     * @param endIndex   End of the long in the byte array.
     * @return The long integer represented by the given bytes.
     */
    public static long parseLong(byte[] bytes, int startIndex, int endIndex) {
        long l = 0;
        for (int i = startIndex; i < endIndex; i++) {
            l <<= 8;
            l |= bytes[i] & 0xFF;
        }
        return l;
    }

    /**
     * Parses a double from a (big-endian) byte array.
     *
     * @param bytes The bytes representing the double.
     * @return The double represented by the given bytes.
     */
    @SuppressWarnings("unused")
    public static double parseDouble(byte[] bytes) {
        return parseDouble(bytes, 0, bytes.length);
    }

    /**
     * Parses a double from a (big-endian) byte array.
     *
     * @param bytes      The bytes representing the double.
     * @param startIndex Beginning of the double in the byte array.
     * @param endIndex   End of the double in the byte array.
     * @return The double represented by the given bytes.
     */
    public static double parseDouble(byte[] bytes, int startIndex, int endIndex) {
        if (endIndex - startIndex == 8) {
            return Double.longBitsToDouble(parseLong(bytes, startIndex, endIndex));
        } else if (endIndex - startIndex == 4) {
            return Float.intBitsToFloat((int) parseLong(bytes, startIndex, endIndex));
        } else {
            throw new IllegalArgumentException("endIndex (" + endIndex + ") - startIndex (" + startIndex + ") != 4 or 8");
        }
    }

    /**
     * Copies a part of a byte array into a new array.
     *
     * @param src        The source array.
     * @param startIndex The index from which to start copying.
     * @param endIndex   The index until which to copy.
     * @return The copied array.
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

    /**
     * Parses a binary property list from a byte array.
     *
     * @param data The binary property list's data.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws PropertyListFormatException          When the property list's format could not be parsed.
     * @throws java.io.UnsupportedEncodingException If a {@link NSString} object could not be decoded.
     */
    private NSObject doParse(byte[] data) throws PropertyListFormatException, UnsupportedEncodingException {
        Objects.requireNonNull(data);
        if (data.length < 8) {
            throw new PropertyListFormatException("The available binary property list data is too short.");
        }

        this.bytes = data;
        String magic = new String(copyOfRange(this.bytes, 0, 8), StandardCharsets.US_ASCII);
        if (!magic.startsWith("bplist") || magic.length() < 8 || !Character.isDigit(magic.charAt(6)) || !Character.isDigit(magic.charAt(7))) {
            throw new PropertyListFormatException("The binary property list has an invalid file header: " + magic);
        }

        this.majorVersion = magic.charAt(6) - 0x30; //ASCII number
        this.minorVersion = magic.charAt(7) - 0x30; //ASCII number

        // 0.0 - OS X Tiger and earlier
        // 0.1 - Leopard
        // 0.? - Snow Leopard
        // 1.5 - Lion
        // 2.0 - Snow Lion

        if (this.majorVersion > 0) {
            throw new PropertyListFormatException("Unsupported binary property list format: v" + this.majorVersion + "." + this.minorVersion + ". " +
                    "Version 1.0 and later are not yet supported.");
            //Version 1.0+ is not even supported by OS X's own parser
        }

        if (this.bytes.length < 40 /* header + trailer length */) {
            throw new PropertyListFormatException("The binary property list does not contain a complete object offset table.");
        }

        // Parse trailer, last 32 bytes of the file
        byte[] trailer = copyOfRange(this.bytes, this.bytes.length - 32, this.bytes.length);

        // Trailer starts with 6 null bytes (index 0 to 5)
        this.offsetSize = (int) parseUnsignedInt(trailer, 6, 7);
        this.objectRefSize = (int) parseUnsignedInt(trailer, 7, 8);
        this.numObjects = (int) parseUnsignedInt(trailer, 8, 16);
        int topObject = (int) parseUnsignedInt(trailer, 16, 24);
        this.offsetTableOffset = (int) parseUnsignedInt(trailer, 24, 32);

        // Validate consistency of the trailer
        if (this.offsetTableOffset + (this.numObjects + 1) * this.offsetSize > this.bytes.length || topObject >= this.bytes.length - 32) {
            throw new PropertyListFormatException("The binary property list contains a corrupted object offset table.");
        }

        return this.parseObject(ParsedObjectStack.empty(), topObject);
    }

    /**
     * Parses an object inside the currently parsed binary property list.
     * For the format specification check
     * <a href="http://www.opensource.apple.com/source/CF/CF-855.17/CFBinaryPList.c">
     * Apple's binary property list parser implementation</a>.
     *
     * @param stack The stack to keep track of parsed objects and detect cyclic references.
     * @param obj   The object ID.
     * @return The parsed object.
     * @throws PropertyListFormatException          When the property list's format could not be parsed.
     * @throws java.io.UnsupportedEncodingException If a {@link NSString} object could not be decoded.
     */
    private NSObject parseObject(ParsedObjectStack stack, int obj) throws PropertyListFormatException, UnsupportedEncodingException {
        stack = stack.push(obj);
        int offset = this.getObjectOffset(obj);
        byte type = this.bytes[offset];
        int objType = (type & 0xF0) >> 4;
        int objInfo = type & 0x0F;
        switch (objType) {
            case SIMPLE_TYPE:
                return this.parseSimpleObject(offset, objInfo, objType, obj);
            case INT_TYPE:
                return this.parseNumber(offset, objInfo, NSNumber.INTEGER);
            case REAL_TYPE:
                return this.parseNumber(offset, objInfo, NSNumber.REAL);
            case DATE_TYPE:
                return this.parseDate(offset, objInfo);
            case DATA_TYPE:
                return this.parseData(offset, objInfo);
            case ASCII_STRING_TYPE:
                return this.parseString(offset, objInfo, (o, l) -> l, StandardCharsets.US_ASCII.name());
            case UTF16_STRING_TYPE:
                // UTF-16 characters can have variable length, but the Core Foundation reference implementation
                // assumes 2 byte characters, thus only covering the Basic Multilingual Plane
                return this.parseString(offset, objInfo, (o, l) -> 2 * l, StandardCharsets.UTF_16BE.name());
            case UTF8_STRING_TYPE:
                // UTF-8 characters can have variable length, so we need to calculate the byte length dynamically
                // by reading the UTF-8 characters one by one
                return this.parseString(offset, objInfo, this::calculateUtf8StringLength, StandardCharsets.UTF_8.name());
            case UID_TYPE:
                return this.parseUid(obj, offset, objInfo + 1);
            case ARRAY_TYPE:
                return this.parseArray(offset, objInfo, stack);
            case ORDERED_SET_TYPE:
                return this.parseSet(offset, objInfo, true, stack);
            case SET_TYPE:
                return this.parseSet(offset, objInfo, false, stack);
            case DICTIONARY_TYPE:
                return this.parseDictionary(offset, objInfo, stack);
            default:
                throw new PropertyListFormatException("The given binary property list contains an object of unknown type (" + objType + ")");
        }
    }

    private NSDate parseDate(int offset, int objInfo) throws PropertyListFormatException {
        if (objInfo != 0x3) {
            throw new PropertyListFormatException("The given binary property list contains a date object of an unknown type (" + objInfo + ")");
        }

        if (offset + 9 > this.bytes.length) {
            throw new PropertyListFormatException("The given binary property list contains a date object longer than the amount of available data.");
        }

        return new NSDate(this.bytes, offset + 1, offset + 9);
    }

    private NSData parseData(int offset, int objInfo) throws PropertyListFormatException {
        int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
        int length = lengthAndOffset[0];
        int dataOffset = offset + lengthAndOffset[1];
        if (dataOffset + length > this.bytes.length) {
            throw new PropertyListFormatException("The given binary property list contains a data object longer than the amount of available data.");
        }

        return new NSData(copyOfRange(this.bytes, dataOffset, dataOffset + length));
    }

    private NSObject parseSimpleObject(int offset, int objInfo, int objType, int obj) throws PropertyListFormatException {
        switch (objInfo) {
            case 0x0: //null object (v1.0 and later)
                return null;
            case 0x8: // false
                return new NSNumber(false);
            case 0x9: // true
                return new NSNumber(true);
            case 0xC: // URL with no base URL (v1.0 and later)
            case 0xD: // URL with base URL (v1.0 and later)
                //TODO Implement binary URL parsing (not implemented in Core Foundation)
                throw new PropertyListFormatException("The given binary property list contains a URL object. This object type is not supported.");
            case 0xE: //16-byte UUID (v1.0 and later)
                return this.parseUid(obj, offset, 16);
            default:
                throw new PropertyListFormatException("The given binary property list contains an object of unknown type (" + objType + ")");
        }
    }

    private UID parseUid(int obj, int offset, int length) throws PropertyListFormatException {
        if (offset + 1 + length >= this.bytes.length) {
            throw new PropertyListFormatException("The given property list contains an UID larger than the amount of available data.");
        }

        return new UID(String.valueOf(obj), copyOfRange(this.bytes, offset + 1, offset + 1 + length));
    }

    private NSNumber parseNumber(int offset, int objInfo, int integer) throws PropertyListFormatException {
        // integer
        int length = (int) Math.pow(2, objInfo);
        try {
            return new NSNumber(this.bytes, offset + 1, offset + 1 + length, integer);
        } catch (IndexOutOfBoundsException ex) {
            throw new PropertyListFormatException("The given property list contains an NSNumber with a length larger than the amount of available data.", ex);
        }
    }

    private NSString parseString(int offset, int objInfo, BiFunction<Integer, Integer, Integer> stringLengthCalculator, String charsetName) throws PropertyListFormatException, UnsupportedEncodingException {
        int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
        int strOffset = offset + lengthAndOffset[1];
        int length = stringLengthCalculator.apply(strOffset, lengthAndOffset[0]);
        if (strOffset + length > this.bytes.length) {
            throw new PropertyListFormatException("The given binary property list contains an NSString that is larger than the amount of available data.");
        }

        return new NSString(this.bytes, strOffset, strOffset + length, charsetName);
    }

    private NSArray parseArray(int offset, int objInfo, ParsedObjectStack stack) throws PropertyListFormatException, UnsupportedEncodingException {
        int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
        int length = lengthAndOffset[0];
        int arrayOffset = offset + lengthAndOffset[1];

        NSArray array = new NSArray(length);
        for (int i = 0; i < length; i++) {
            int objRef = this.parseObjectReferenceFromList(arrayOffset, i);
            array.setValue(i, this.parseObject(stack, objRef));
        }
        return array;
    }

    private NSSet parseSet(int offset, int objInfo, boolean ordered, ParsedObjectStack stack) throws PropertyListFormatException, UnsupportedEncodingException {
        int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
        int length = lengthAndOffset[0];
        int setOffset = offset + lengthAndOffset[1];

        NSSet set = new NSSet(ordered);
        for (int i = 0; i < length; i++) {
            int objRef = this.parseObjectReferenceFromList(setOffset, i);
            set.addObject(this.parseObject(stack, objRef));
        }

        return set;
    }

    private NSDictionary parseDictionary(int offset, int objInfo, ParsedObjectStack stack) throws PropertyListFormatException, UnsupportedEncodingException {
        int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
        int length = lengthAndOffset[0];
        int contentOffset = lengthAndOffset[1];
        int keyListOffset = offset + contentOffset;
        int valueListOffset = keyListOffset + (length * this.objectRefSize);

        NSDictionary dict = new NSDictionary();
        for (int i = 0; i < length; i++) {
            int keyRef = this.parseObjectReferenceFromList(keyListOffset, i);
            int valRef = this.parseObjectReferenceFromList(valueListOffset, i);
            NSObject key = this.parseObject(stack, keyRef);
            if (key == null) {
                throw new PropertyListFormatException("The given binary property list contains a dictionary with an invalid NULL key.");
            }

            NSObject val = this.parseObject(stack, valRef);

            dict.put(key.toString(), val);
        }
        return dict;
    }

    private int[] readLengthAndOffset(int objInfo, int offset) throws PropertyListFormatException {
        try {
            int lengthValue = objInfo;
            int offsetValue = 1;
            if (objInfo == 0xF) {
                int int_type = this.bytes[offset + 1];
                int intType = (int_type & 0xF0) >> 4;
                if (intType != 0x1) {
                    System.err.println("BinaryPropertyListParser: Length integer has an unexpected type (" + intType + "). Attempting to parse anyway...");
                }
                int intInfo = int_type & 0x0F;
                int intLength = (int) Math.pow(2, intInfo);
                offsetValue = 2 + intLength;
                if (intLength < 3) {
                    lengthValue = (int) parseUnsignedInt(this.bytes, offset + 2, offset + 2 + intLength);
                } else {
                    lengthValue = new BigInteger(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength)).intValue();
                }
            }

            return new int[]{lengthValue, offsetValue};
        } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
            throw new PropertyListFormatException("The given binary property list contains an invalid length/offset integer at offset " + offset, ex);
        }
    }

    private int calculateUtf8StringLength(int offset, int numCharacters) {
        int length = 0;
        for (int i = 0; i < numCharacters; i++) {
            int tempOffset = offset + length;
            if (this.bytes.length <= tempOffset) {
                // WARNING: Invalid UTF-8 string
                return numCharacters;
            }

            int currentByte = this.bytes[tempOffset];
            if ((currentByte & 0x80) != 0x80) {
                length++;
            } else {
                int n = 0;
                if ((currentByte & 0xC0) == 0x80) {
                    // Unexpected continuation mark, fall back
                    return numCharacters;
                } else if ((currentByte & 0xE0) == 0xC0) {
                    n = 1;
                } else if ((currentByte & 0xF0) == 0xE0) {
                    n = 2;
                } else if ((currentByte & 0xF8) == 0xF0) {
                    n = 3;
                }

                if (this.hastUtf8Sequence(tempOffset, n)) {
                    length += 2;
                } else {
                    // Invalid sequence, fall back
                    return numCharacters;
                }
            }
        }

        return length;
    }

    private boolean hastUtf8Sequence(int offset, int n) {
        for (int i = 1; i <= n; i++) {
            if (((offset + i) >= this.bytes.length)
                    || ((this.bytes[offset + i] & 0xC0) != 0x80)) {
                return false;
            }
        }

        return true;
    }

    private int parseObjectReferenceFromList(int baseOffset, int objectIndex) throws PropertyListFormatException {
        return this.parseObjectReference(baseOffset + objectIndex * this.objectRefSize);
    }

    private int parseObjectReference(int offset) throws PropertyListFormatException {
        if (offset + this.objectRefSize >= this.bytes.length) {
            throw new PropertyListFormatException("The given property list contains an incomplete object reference at offset " + offset + ".");
        }

        return (int) parseUnsignedInt(this.bytes, offset, offset + this.objectRefSize);
    }

    private int getObjectOffset(int obj) throws PropertyListFormatException {
        if (obj >= this.numObjects) {
            throw new PropertyListFormatException("The given binary property list contains an invalid object identifier.");
        }

        int startOffset = this.offsetTableOffset + obj * this.offsetSize;
        return (int) parseUnsignedInt(this.bytes, startOffset, startOffset + this.offsetSize);
    }
}

