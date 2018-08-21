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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * Parser for ASCII property lists. Supports Apple OS X/iOS and GnuStep/NeXTSTEP format.
 * This parser is based on the recursive descent paradigm, but the underlying grammar
 * is not explicitly defined.
 * </p>
 * <p>
 * Resources on ASCII property list format:
 * </p>
 * <ul>
 * <li><a href="https://developer.apple.com/library/mac/#documentation/Cocoa/Conceptual/PropertyLists/OldStylePlists/OldStylePLists.html">
 * Property List Programming Guide - Old-Style ASCII Property Lists
 * </a></li>
 * <li><a href="http://www.gnustep.org/resources/documentation/Developer/Base/Reference/NSPropertyList.html">
 * GnuStep - NSPropertyListSerialization class documentation
 * </a></li>
 * </ul>
 * @author Daniel Dreibrodt
 */
public final class ASCIIPropertyListParser {

    public static final char WHITESPACE_SPACE = ' ';
    public static final char WHITESPACE_TAB = '\t';
    public static final char WHITESPACE_NEWLINE = '\n';
    public static final char WHITESPACE_CARRIAGE_RETURN = '\r';

    public static final char ARRAY_BEGIN_TOKEN = '(';
    public static final char ARRAY_END_TOKEN = ')';
    public static final char ARRAY_ITEM_DELIMITER_TOKEN = ',';

    public static final char DICTIONARY_BEGIN_TOKEN = '{';
    public static final char DICTIONARY_END_TOKEN = '}';
    public static final char DICTIONARY_ASSIGN_TOKEN = '=';
    public static final char DICTIONARY_ITEM_DELIMITER_TOKEN = ';';

    public static final char QUOTEDSTRING_BEGIN_TOKEN = '"';
    public static final char QUOTEDSTRING_END_TOKEN = '"';
    public static final char QUOTEDSTRING_ESCAPE_TOKEN = '\\';

    public static final char DATA_BEGIN_TOKEN = '<';
    public static final char DATA_END_TOKEN = '>';

    public static final char DATA_GSOBJECT_BEGIN_TOKEN = '*';
    public static final char DATA_GSDATE_BEGIN_TOKEN = 'D';
    public static final char DATA_GSBOOL_BEGIN_TOKEN = 'B';
    public static final char DATA_GSBOOL_TRUE_TOKEN = 'Y';
    public static final char DATA_GSBOOL_FALSE_TOKEN = 'N';
    public static final char DATA_GSINT_BEGIN_TOKEN = 'I';
    public static final char DATA_GSREAL_BEGIN_TOKEN = 'R';

    public static final char DATE_DATE_FIELD_DELIMITER = '-';
    public static final char DATE_TIME_FIELD_DELIMITER = ':';
    public static final char DATE_GS_DATE_TIME_DELIMITER = ' ';
    public static final char DATE_APPLE_DATE_TIME_DELIMITER = 'T';
    public static final char DATE_APPLE_END_TOKEN = 'Z';

    public static final char COMMENT_BEGIN_TOKEN = '/';
    public static final char MULTILINE_COMMENT_SECOND_TOKEN = '*';
    public static final char SINGLELINE_COMMENT_SECOND_TOKEN = '/';
    public static final char MULTILINE_COMMENT_END_TOKEN = '/';

    /**
     * Property list source data
     */
    private final char[] data;
    /**
     * Current parsing index
     */
    private int index;

    /**
     * Creates a new parser for the given property list content.
     *
     * @param propertyListContent The content of the property list that is to be parsed.
     * @param encoding The name of a supported {@link java.nio.charset.Charset charset} to decode the property list.
     * @throws java.io.UnsupportedEncodingException If no support for the named charset is available in this instance of the Java virtual machine.
     */
    private ASCIIPropertyListParser(byte[] propertyListContent, String encoding) throws UnsupportedEncodingException {
        this.data = new String(propertyListContent, encoding).toCharArray();
    }

    /**
     * Parses an ASCII property list file.
     *
     * @param f The ASCII property list file.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws java.text.ParseException If an error occurs during parsing.
     * @throws java.io.IOException If an error occurs while reading from the input stream.
     */
    public static NSObject parse(File f) throws IOException, ParseException {
        InputStream fileInputStream = new FileInputStream(f);
        try {
            return parse(fileInputStream);
        }
        finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Parses an ASCII property list file.
     *
     * @param f The ASCII property list file.
     * @param encoding The name of a supported {@link java.nio.charset.Charset charset} to decode the property list.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws java.text.ParseException If an error occurs during parsing.
     * @throws java.io.IOException If an error occurs while reading from the input stream.
     * @throws java.io.UnsupportedEncodingException If no support for the named charset is available in this instance of the Java virtual machine.
     */
    public static NSObject parse(File f, String encoding) throws IOException, ParseException {
        InputStream fileInputStream = new FileInputStream(f);
        try {
            return parse(fileInputStream, encoding);
        }
        finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Parses an ASCII property list from an input stream.
     * This method does not close the specified input stream.
     *
     * @param in The input stream that points to the property list's data.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws java.text.ParseException If an error occurs during parsing.
     * @throws java.io.IOException If an error occurs while reading from the input stream.
     */
    public static NSObject parse(InputStream in) throws ParseException, IOException {
        return parse(PropertyListParser.readAll(in));
    }

    /**
     * Parses an ASCII property list from an input stream.
     * This method does not close the specified input stream.
     *
     * @param in The input stream that points to the property list's data.
     * @param encoding The name of a supported {@link java.nio.charset.Charset charset} to decode the property list.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws java.text.ParseException If an error occurs during parsing.
     * @throws java.io.IOException If an error occurs while reading from the input stream.
     * @throws java.io.UnsupportedEncodingException If no support for the named charset is available in this instance of the Java virtual machine.
     */
    public static NSObject parse(InputStream in, String encoding) throws ParseException, IOException {
        return parse(PropertyListParser.readAll(in), encoding);
    }

    /**
     * Parses an ASCII property list from a byte array.
     *
     * @param bytes The ASCII property list data.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws ParseException If an error occurs during parsing.
     */
    public static NSObject parse(byte[] bytes) throws ParseException {
        try {
            // Check for byte order marks
            if (bytes.length > 2) {
                if (bytes[0] == (byte)0xFE && bytes[1] == (byte)0xFF) {
                    return parse(bytes, "UTF-16");
                }
                else if (bytes[0] == (byte)0xFF && bytes[1] == (byte)0xFE) {
                    if (bytes.length > 4 && bytes[2] == (byte)0x00 && bytes[3] == (byte)0x00) {
                        return parse(bytes, "UTF-32");
                    }
                    return parse(bytes, "UTF-16");
                }
                else if (bytes.length > 3) {
                    if (bytes[0] == (byte)0xEF && bytes[1] == (byte)0xBB && bytes[2] == (byte)0xBF) {
                        return parse(bytes, "UTF-8");
                    }
                    else if (bytes.length > 4 && bytes[0] == (byte)0x00 && bytes[1] == (byte)0x00 && bytes[2] == (byte)0xFE && bytes[3] == (byte)0xFF) {
                        return parse(bytes, "UTF-32");
                    }
                }
            }

            return parse(bytes, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            // Very unlikely to happen
            throw new RuntimeException("Unsupported property list encoding: " + e.getMessage());
        }
    }

    /**
     * Parses an ASCII property list from a byte array.
     *
     * @param bytes The ASCII property list data.
     * @param encoding The name of a supported {@link java.nio.charset.Charset} charset to decode the property list.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws ParseException If an error occurs during parsing.
     * @throws java.io.UnsupportedEncodingException If no support for the named charset is available in this instance of the Java virtual machine.
     */
    public static NSObject parse(byte[] bytes, String encoding) throws ParseException, UnsupportedEncodingException {
        ASCIIPropertyListParser parser = new ASCIIPropertyListParser(bytes, encoding);
        return parser.parse();
    }

    /**
     * Checks whether the given sequence of symbols can be accepted.
     *
     * @param sequence The sequence of tokens to look for.
     * @return Whether the given tokens occur at the current parsing position.
     */
    private boolean acceptSequence(char... sequence) {
        for (int i = 0; i < sequence.length; i++) {
            if (this.data[this.index + i] != sequence[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether the given symbols can be accepted, that is, if one
     * of the given symbols is found at the current parsing position.
     *
     * @param acceptableSymbols The symbols to check.
     * @return Whether one of the symbols can be accepted or not.
     */
    private boolean accept(char... acceptableSymbols) {
        boolean symbolPresent = false;
        for (char c : acceptableSymbols) {
            if (this.data[this.index] == c) {
                symbolPresent = true;
            }
        }

        return symbolPresent;
    }

    /**
     * Checks whether the given symbol can be accepted, that is, if
     * the given symbols is found at the current parsing position.
     *
     * @param acceptableSymbol The symbol to check.
     * @return Whether the symbol can be accepted or not.
     */
    private boolean accept(char acceptableSymbol) {
        return this.data[this.index] == acceptableSymbol;
    }

    /**
     * Expects the input to have one of the given symbols at the current parsing position.
     *
     * @param expectedSymbols The expected symbols.
     * @throws ParseException If none of the expected symbols could be found.
     */
    private void expect(char... expectedSymbols) throws ParseException {
        if (!this.accept(expectedSymbols)) {
            StringBuilder excString = new StringBuilder();
            excString.append("Expected '").append(expectedSymbols[0]).append("'");
            for (int i = 1; i < expectedSymbols.length; i++) {
                excString.append(" or '").append(expectedSymbols[i]).append("'");
            }

            excString.append(" but found '").append(this.data[this.index]).append("'");
            throw new ParseException(excString.toString(), this.index);
        }
    }

    /**
     * Expects the input to have the given symbol at the current parsing position.
     *
     * @param expectedSymbol The expected symbol.
     * @throws ParseException If the expected symbol could be found.
     */
    private void expect(char expectedSymbol) throws ParseException {
        if (!this.accept(expectedSymbol)) {
            throw new ParseException("Expected '" + expectedSymbol + "' but found '" + this.data[this.index] + "'", this.index);
        }
    }

    /**
     * Reads an expected symbol.
     *
     * @param symbol The symbol to read.
     * @throws ParseException If the expected symbol could not be read.
     */
    private void read(char symbol) throws ParseException {
        this.expect(symbol);
        this.index++;
    }

    /**
     * Skips the current symbol.
     */
    private void skip() {
        this.index++;
    }

    /**
     * Skips several symbols
     *
     * @param numSymbols The amount of symbols to skip.
     */
    private void skip(int numSymbols) {
        this.index += numSymbols;
    }

    /**
     * Skips all whitespaces and comments from the current parsing position onward.
     */
    private void skipWhitespacesAndComments() {
        boolean commentSkipped;
        do {
            commentSkipped = false;

            //Skip whitespaces
            while (this.accept(WHITESPACE_CARRIAGE_RETURN, WHITESPACE_NEWLINE, WHITESPACE_SPACE, WHITESPACE_TAB)) {
                this.skip();
            }

            //Skip single line comments "//..."
            if (this.acceptSequence(COMMENT_BEGIN_TOKEN, SINGLELINE_COMMENT_SECOND_TOKEN)) {
                this.skip(2);
                this.readInputUntil(WHITESPACE_CARRIAGE_RETURN, WHITESPACE_NEWLINE);
                commentSkipped = true;
            }

            //Skip multi line comments "/* ... */"
            else if (this.acceptSequence(COMMENT_BEGIN_TOKEN, MULTILINE_COMMENT_SECOND_TOKEN)) {
                this.skip(2);
                while (true) {
                    if (this.acceptSequence(MULTILINE_COMMENT_SECOND_TOKEN, MULTILINE_COMMENT_END_TOKEN)) {
                        this.skip(2);
                        break;
                    }

                    this.skip();
                }
                commentSkipped = true;
            }
        }
        while (commentSkipped); //if a comment was skipped more whitespace or another comment can follow, so skip again
    }

    /**
     * Reads input until one of the given symbols is found.
     *
     * @param symbols The symbols that can occur after the string to read.
     * @return The input until one the given symbols.
     */
    private String readInputUntil(char... symbols) {
        StringBuilder strBuf = new StringBuilder();
        while (!this.accept(symbols)) {
        	  strBuf.append(this.data[this.index]);
            this.skip();
        }

        return strBuf.toString();
    }

    /**
     * Reads input until the given symbol is found.
     *
     * @param symbol The symbol that can occur after the string to read.
     * @return The input until the given symbol.
     */
    private String readInputUntil(char symbol) {
        StringBuilder strBuf = new StringBuilder();
        while (!this.accept(symbol)) {
        	  strBuf.append(this.data[this.index]);
            this.skip();
        }
        return strBuf.toString();
    }

    /**
     * Parses the property list from the beginning and returns the root object
     * of the property list.
     *
     * @return The root object of the property list. This can either be a NSDictionary or a NSArray.
     * @throws ParseException If an error occurred during parsing
     */
    public NSObject parse() throws ParseException {
        this.index = 0;
        if (this.data.length == 0) {
           throw new ParseException("The property list is empty.", 0);
        }

        //Skip Unicode byte order mark (BOM)
        if (this.data[0] == '\uFEFF') {
            this.skip(1);
        }

        this.skipWhitespacesAndComments();
        this.expect(DICTIONARY_BEGIN_TOKEN, ARRAY_BEGIN_TOKEN, COMMENT_BEGIN_TOKEN);
        try {
            return this.parseObject();
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new ParseException("Reached end of input unexpectedly.", this.index);
        }
    }

    /**
     * Parses the NSObject found at the current position in the property list
     * data stream.
     *
     * @return The parsed NSObject.
     * @see ASCIIPropertyListParser#index
     */
    private NSObject parseObject() throws ParseException {
        switch (this.data[this.index]) {
            case ARRAY_BEGIN_TOKEN: {
                return this.parseArray();
            }
            case DICTIONARY_BEGIN_TOKEN: {
                return this.parseDictionary();
            }
            case DATA_BEGIN_TOKEN: {
                return this.parseData();
            }
            case QUOTEDSTRING_BEGIN_TOKEN: {
                String quotedString = this.parseQuotedString();
                //apple dates are quoted strings of length 20 and after the 4 year digits a dash is found
                if (quotedString.length() == 20 && quotedString.charAt(4) == DATE_DATE_FIELD_DELIMITER) {
                    try {
                        return new NSDate(quotedString);
                    } catch (Exception ex) {
                        //not a date? --> return string
                        return new NSString(quotedString);
                    }
                } else {
                    return new NSString(quotedString);
                }
            }
            default: {
                //0-9
                if (this.data[this.index] >= '0' && this.data[this.index] <= '9') {
                    //could be a date or just a string
                    return this.parseDateString();
                } else {
                    //non-numerical -> string or boolean
                    return new NSString(this.parseString());
                }
            }
        }
    }

    /**
     * Parses an array from the current parsing position.
     * The prerequisite for calling this method is, that an array begin token has been read.
     *
     * @return The array found at the parsing position.
     */
    private NSArray parseArray() throws ParseException {
        //Skip begin token
        this.skip();
        this.skipWhitespacesAndComments();
        List<NSObject> objects = new LinkedList<NSObject>();
        while (!this.accept(ARRAY_END_TOKEN)) {
            objects.add(this.parseObject());
            this.skipWhitespacesAndComments();
            if (this.accept(ARRAY_ITEM_DELIMITER_TOKEN)) {
                this.skip();
            } else {
                break; //must have reached end of array
            }

            this.skipWhitespacesAndComments();
        }

        //parse end token
        this.read(ARRAY_END_TOKEN);
        return new NSArray(objects.toArray(new NSObject[objects.size()]));
    }

    /**
     * Parses a dictionary from the current parsing position.
     * The prerequisite for calling this method is, that a dictionary begin token has been read.
     *
     * @return The dictionary found at the parsing position.
     */
    private NSDictionary parseDictionary() throws ParseException {
        //Skip begin token
        this.skip();
        this.skipWhitespacesAndComments();
        NSDictionary dict = new NSDictionary();
        while (!this.accept(DICTIONARY_END_TOKEN)) {
            //Parse key
            String keyString;
            if (this.accept(QUOTEDSTRING_BEGIN_TOKEN)) {
                keyString = this.parseQuotedString();
            } else {
                keyString = this.parseString();
            }

            this.skipWhitespacesAndComments();

            //Parse assign token
            this.read(DICTIONARY_ASSIGN_TOKEN);
            this.skipWhitespacesAndComments();

            NSObject object = this.parseObject();
            dict.put(keyString, object);
            this.skipWhitespacesAndComments();
            this.read(DICTIONARY_ITEM_DELIMITER_TOKEN);
            this.skipWhitespacesAndComments();
        }

        //skip end token
        this.skip();

        return dict;
    }

    /**
     * Parses a data object from the current parsing position.
     * This can either be a NSData object or a GnuStep NSNumber or NSDate.
     * The prerequisite for calling this method is, that a data begin token has been read.
     *
     * @return The data object found at the parsing position.
     */
    private NSObject parseData() throws ParseException {
        NSObject obj = null;
        //Skip begin token
        this.skip();
        if (this.accept(DATA_GSOBJECT_BEGIN_TOKEN)) {
            this.skip();
            this.expect(DATA_GSBOOL_BEGIN_TOKEN, DATA_GSDATE_BEGIN_TOKEN, DATA_GSINT_BEGIN_TOKEN, DATA_GSREAL_BEGIN_TOKEN);
            if (this.accept(DATA_GSBOOL_BEGIN_TOKEN)) {
                //Boolean
                this.skip();
                this.expect(DATA_GSBOOL_TRUE_TOKEN, DATA_GSBOOL_FALSE_TOKEN);
                if (this.accept(DATA_GSBOOL_TRUE_TOKEN)) {
                    obj = new NSNumber(true);
                } else {
                    obj = new NSNumber(false);
                }

                //Skip the parsed boolean token
                this.skip();
            } else if (this.accept(DATA_GSDATE_BEGIN_TOKEN)) {
                //Date
                this.skip();
                String dateString = this.readInputUntil(DATA_END_TOKEN);
                obj = new NSDate(dateString);
            } else if (this.accept(DATA_GSINT_BEGIN_TOKEN, DATA_GSREAL_BEGIN_TOKEN)) {
                //Number
                this.skip();
                String numberString = this.readInputUntil(DATA_END_TOKEN);
                obj = new NSNumber(numberString);
            }

            //parse data end token
            this.read(DATA_END_TOKEN);
        } else {
            String dataString = this.readInputUntil(DATA_END_TOKEN);
            dataString = dataString.replaceAll("\\s+", "");

            int numBytes = dataString.length() / 2;
            byte[] bytes = new byte[numBytes];
            for (int i = 0; i < bytes.length; i++) {
                String byteString = dataString.substring(i * 2, i * 2 + 2);
                int byteValue = Integer.parseInt(byteString, 16);
                bytes[i] = (byte) byteValue;
            }

            obj = new NSData(bytes);

            //skip end token
            this.skip();
        }

        return obj;
    }

    /**
     * Attempts to parse a plain string as a date if possible.
     *
     * @return A NSDate if the string represents such an object. Otherwise a NSString is returned.
     */
    private NSObject parseDateString() {
        String numericalString = this.parseString();
        if (numericalString.length() > 4 && numericalString.charAt(4) == DATE_DATE_FIELD_DELIMITER) {
            try {
                return new NSDate(numericalString);
            } catch(Exception ex) {
                //An exception occurs if the string is not a date but just a string
            }
        }

        return new NSString(numericalString);
    }

    /**
     * Parses a plain string from the current parsing position.
     * The string is made up of all characters to the next whitespace, delimiter token or assignment token.
     *
     * @return The string found at the current parsing position.
     */
    private String parseString() {
        return this.readInputUntil(WHITESPACE_SPACE, WHITESPACE_TAB, WHITESPACE_NEWLINE, WHITESPACE_CARRIAGE_RETURN,
                ARRAY_ITEM_DELIMITER_TOKEN, DICTIONARY_ITEM_DELIMITER_TOKEN, DICTIONARY_ASSIGN_TOKEN, ARRAY_END_TOKEN);
    }

    /**
     * Parses a quoted string from the current parsing position.
     * The prerequisite for calling this method is, that a quoted string begin token has been read.
     *
     * @return The quoted string found at the parsing method with all special characters unescaped.
     * @throws ParseException If an error occured during parsing.
     */
    private String parseQuotedString() throws ParseException {
        //Skip begin token
        this.skip();
        StringBuilder stringBuilder = new StringBuilder();
        boolean unescapedBackslash = true;
        //Read from opening quotation marks to closing quotation marks and skip escaped quotation marks
        while (this.data[this.index] != QUOTEDSTRING_END_TOKEN || (this.data[this.index - 1] == QUOTEDSTRING_ESCAPE_TOKEN && unescapedBackslash)) {
            stringBuilder.append(this.data[this.index]);
            if (this.accept(QUOTEDSTRING_ESCAPE_TOKEN)) {
                unescapedBackslash = !(this.data[this.index - 1] == QUOTEDSTRING_ESCAPE_TOKEN && unescapedBackslash);
            }

            this.skip();
        }

        String unescapedString;
        try {
            unescapedString = parseQuotedString(stringBuilder.toString());
        }
        catch (ParseException ex) {
            throw new ParseException(ex.getMessage(), this.index + ex.getErrorOffset());
        }
        catch (Exception ex) {
            throw new ParseException("A quoted string could not be parsed.", this.index);
        }

        //skip end token
        this.skip();

        return unescapedString;
    }

    /**
     * Parses a string according to the format specified for ASCII property lists.
     * Such strings can contain escape sequences which are unescaped in this method.
     *
     * @param s The escaped string according to the ASCII property list format, without leading and trailing quotation marks.
     * @return The unescaped string in UTF-8
     * @throws ParseException The string contains an invalid escape sequence.
     */
    private static synchronized String parseQuotedString(String s) throws ParseException {
        StringBuilder result = new StringBuilder();

        StringCharacterIterator iterator = new StringCharacterIterator(s);
        char c = iterator.current();

        while (iterator.getIndex() < iterator.getEndIndex()) {
            switch (c) {
                case '\\': { //An escaped sequence is following
                    result.append(parseEscapedSequence(iterator));
                    break;
                }
                default: { //a normal UTF-8 char
                    result.append(c);
                    break;
                }
            }
            c = iterator.next();
        }

        //Build string
        return result.toString();
    }

    /**
     * Unescapes an escaped character sequence, e.g. \\u00FC.
     *
     * @param iterator The string character iterator pointing to the first character after the backslash
     * @return The unescaped character.
     * @throws ParseException The string contains an invalid escape sequence.
     */
    private static char parseEscapedSequence(StringCharacterIterator iterator) throws ParseException {
        char c = iterator.next();
        switch (c)
        {
            case '\\':
            case '"':
            case '\'':
                return c;
            case 'b':
                return '\b';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';

            case 'U':
            case 'u':
            {
                //4 digit hex Unicode value
                String unicodeValue = new String(new char[] {iterator.next(), iterator.next(), iterator.next(), iterator.next()});
                try {
                    return (char) Integer.parseInt(unicodeValue, 16);
                }
                catch (NumberFormatException ex) {
                    throw new ParseException("The property list contains a string with an invalid escape sequence: \\" + c + unicodeValue, iterator.getIndex() - 4);
                }
            }

            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            {
                //3 digit octal ASCII value
                String num = new String(new char[] {c, iterator.next(), iterator.next()});
                try {
                    return (char) Integer.parseInt(num, 8);
                }
                catch (NumberFormatException ex) {
                    throw new ParseException("The property list contains a string with an invalid escape sequence: \\" + num, iterator.getIndex() - 2);
                }
            }

            default:
                throw new ParseException("The property list contains a string with an invalid escape sequence: \\" + c, iterator.getIndex());
        }
    }
}
