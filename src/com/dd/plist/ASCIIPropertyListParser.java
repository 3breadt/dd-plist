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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Parser for ASCII property list. Supports Apple OS X/iOS and GnuStep/NeXTSTEP format.
 * @author Daniel Dreibrodt
 */
public class ASCIIPropertyListParser {
    
    private final static Pattern scannerDelimiterPattern = Pattern.compile("(\\s*\\=\\s*)|(\\s*\\;\\s+)|(\\s*\\,\\s+)|(\\s+)");    
    
    private final static Pattern arrayBeginToken = Pattern.compile("\\(");
    private final static Pattern arrayEndToken = Pattern.compile("\\)");
    
    private final static Pattern dictionaryBeginToken = Pattern.compile("\\{");
    private final static Pattern dictionaryEndToken = Pattern.compile("\\}");
    
    //ASCII string without spaces, quotes or other tokens
    private final static Pattern simpleStringPattern = Pattern.compile("[\\x00-\\x7F&&[^\" ,;\\(\\)\\{\\}\\<\\>]]+");
    //ASCII string within double quotes
    private final static Pattern quotedStringPattern = Pattern.compile("\"[\\x00-\\x7F]+\"");
    
    private final static Pattern dataBeginToken = Pattern.compile("<[0-9A-Fa-f ]*");
    private final static Pattern dataContentPattern = Pattern.compile("[0-9A-Fa-f ]+");
    private final static Pattern dataEndToken = Pattern.compile("[0-9A-Fa-f ]*>");
    
    private final static Pattern realPattern = Pattern.compile("[0-9]+.[0-9]+");
    
    //YYYY-MM-DD HH:MM:SS +/-ZZZZ
    private final static Pattern gnuStepDateBeginPattern = Pattern.compile("<\\*D[0-9]{4}-[0-1][0-9]-[0-3][0-9]");
    //yyyy-MM-ddTHH:mm:ssZ
    private final static Pattern appleDatePattern = Pattern.compile("\"[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z\"");
    
    private final static Pattern appleBooleanPattern = Pattern.compile("(YES)|(NO)");
    private final static Pattern gnuStepBooleanPattern = Pattern.compile("(\\<\\*BY\\>)|(\\<\\*BN\\>)");
    
    private final static Pattern gnuStepIntPattern = Pattern.compile("\\<\\*I[0-9]+\\>");
    private final static Pattern gnuStepRealPattern = Pattern.compile("\\<\\*R[0-9]+(.[0-9]+)?\\>");
    
    
    /**
     * Parses an ASCII property list file.
     * @param f The ASCII property list file.
     * @return The root object of the property list. This is usally a NSDictionary but can also be a NSArray.
     * @throws Exception When an error occurs during parsing.
     */
    public static NSObject parse(File f) throws Exception {
        return parse(new Scanner(f));
    }
    
    /**
     * Parses an ASCII property list from an input stream.
     * @param in The input stream that points to the property list's data.
     * @return The root object of the property list. This is usally a NSDictionary but can also be a NSArray.
     * @throws Exception When an error occurs during parsing.
     */
    public static NSObject parse(InputStream in) throws Exception {
        return parse(new Scanner(in));
    }
    
    /**
     * Parses an ASCII property list from a byte array.
     * @param bytes The ASCII property list data.
     * @return The root object of the property list. This is usally a NSDictionary but can also be a NSArray.
     * @throws Exception When an error occurs during parsing.
     */
    public static NSObject parse(byte[] bytes) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return parse(bis);
    }
    
    /**
     * Performs the actual parsing of the ASCII property list.
     * @param s The scanner object wrapping the property list document.
     * @return The root object of the property list. This is usally a NSDictionary but can also be a NSArray.
     * @throws Exception When an error occurs during parsing. 
     */
    private static NSObject parse(Scanner s) throws Exception {
        s.useDelimiter(scannerDelimiterPattern);
        //A property list has to have a NSArray or NSDictionary as root
        if(s.hasNext(arrayBeginToken) || s.hasNext(dictionaryBeginToken)) {
            return parseObject(s);
        } else {
            throw new ParseException("Expected '"+arrayBeginToken+"' or '"+dictionaryBeginToken+"' but found "+s.next(), 0);
        }
    }
    
    /**
     * Parses an object from the current parsing position in the ASCII property list.
     * @param s The scanner object wrapping the property list document.
     * @return The root object of the property list. This is usally a NSDictionary but can also be a NSArray.
     * @throws Exception When an error occurs during parsing. 
     */
    private static NSObject parseObject(Scanner s) throws Exception {        
        if(s.hasNext(arrayBeginToken)) {
            //NSArray
            s.next();
            List<NSObject> arrayObjects = new LinkedList<NSObject>();
            int len = 0;
            while(!s.hasNext(arrayEndToken)) {
                NSObject o = parseObject(s);
                arrayObjects.add(o);
                len++;
            }
            if(!s.hasNext(arrayEndToken)) {
                throw new ParseException("Expected '"+arrayEndToken+"' but found "+s.next(), 0);
            }
            s.next();
            NSArray array = new NSArray(arrayObjects.toArray(new NSObject[len]));
            return array;
        } else if(s.hasNext(dictionaryBeginToken)) {
            //NSDictionary
            s.next();
            NSDictionary dict = new NSDictionary();
            while(!s.hasNext(dictionaryEndToken)) {
                String key = "";
                if(s.hasNext(simpleStringPattern)) {
                    key = s.next(simpleStringPattern);
                } else if(s.hasNext(quotedStringPattern)) {
                    key = parseQuotedString(s.next(quotedStringPattern));
                } else {
                    throw new ParseException("Expected String but found "+s.next(), 0);
                }                
                NSObject value = parseObject(s);                
                dict.put(key, value);                
            }
            if(!s.hasNext(dictionaryEndToken)) {
                throw new ParseException("Expected '"+dictionaryEndToken+"' but found "+s.next(), 0);
            }
            s.next();
            return dict;
        } else if(s.hasNext(gnuStepDateBeginPattern)) {
            //NSDate
            String dateString = s.next(); //<*DYYYY-MM-DD
            dateString += " "+s.next(); //HH:MM:SS
            dateString += " "+s.next(); //+/-ZZZZ>
            return new NSDate(dateString.substring(3, dateString.length()-1));
        } else if(s.hasNext(appleDatePattern)) {
            //NSDate
            return new NSDate(s.next().replaceAll("\"",""));
        } else if(s.hasNextInt()) {
            //NSNumber: int
            return new NSNumber(s.nextInt());
        } else if(s.hasNext(realPattern)) {
            //NSNumber: real
            return new NSNumber(Double.parseDouble(s.next()));
        } else if (s.hasNext(appleBooleanPattern)) {
            //NSNumber: bool
            return new NSNumber(s.next().equals("YES"));
        } else if (s.hasNext(gnuStepBooleanPattern)) {
            //NSNumber: bool
            return new NSNumber(s.next().equals("<*BY>"));
        } else if (s.hasNext(gnuStepIntPattern)) {
            //NSNumber: int
            String token = s.next();
            return new NSNumber(Integer.parseInt(token.substring(3,token.length()-1)));
        } else if (s.hasNext(gnuStepRealPattern)) {
            //NSNumber: real
            String token = s.next();
            return new NSNumber(Double.parseDouble(token.substring(3,token.length()-1)));
        } else if(s.hasNext(dataBeginToken)) {            
            //NSData
            String data = s.next().replaceFirst("<", "");
            while(!s.hasNext(dataEndToken))
                data += s.next(dataContentPattern);
            data += s.next().replaceAll(">", "");
            int numBytes = data.length()/2;
            byte[] bytes = new byte[numBytes];
            for(int i=0;i<bytes.length;i++) {
                String byteString = data.substring(i*2, i*2+2);
                int byteValue = Integer.parseInt(byteString, 16);
                bytes[i] = (byte)byteValue;
            }
            return new NSData(bytes);
        } else if(s.hasNext(quotedStringPattern)) {
            //NSString
            String str = parseQuotedString(s.next());            
            return new NSString(str);
        } else if(s.hasNext(simpleStringPattern)) {
            //NSString
            String str = s.next();
            return new NSString(str);
        }
        else {
            throw new ParseException("Expected a NSObject but found "+s.next(), 0);
        }
    }
    
    /**
     * Used to encode the parsed strings
     */
    private static CharsetEncoder asciiEncoder;
    
    /**
     * Parses a string according to the format specified for ASCII property lists.
     * Such strings can contain escape sequences which are unescaped in this method.
     * @param s The escaped string according to the ASCII property list format.
     * @return The unescaped string in UTF-8 or ASCII format, depending on the contained characters.
     * @throws Exception If the string could not be properly parsed.
     */
    public static synchronized String parseQuotedString(String s) throws Exception {
        s = s.substring(1,s.length()-1);
        List<Byte> strBytes = new LinkedList<Byte>();
        StringCharacterIterator iterator = new StringCharacterIterator(s);
        char c = iterator.current();
        
        while(iterator.getIndex() < iterator.getEndIndex()) {
            switch(c) {
                case '\\' : { //An escaped sequence is following
                    byte[] bts = parseEscapedSequence(iterator).getBytes("UTF-8");
                    for(byte b:bts)
                        strBytes.add(b);
                    break;
                }
                default: { //a normal ASCII char
                    strBytes.add((byte)0);
                    strBytes.add((byte)c);
                    break;
                }
            }
            c = iterator.next();
        }
        byte[] bytArr = new byte[strBytes.size()];
        int i = 0;
        for(Byte b:strBytes) {
            bytArr[i] = b.byteValue();
            i++;
        }
        //Build string
        String result = new String(bytArr, "UTF-8");
        CharBuffer charBuf = CharBuffer.wrap(result);
        
        //If the string can be represented in the ASCII codepage
        // --> use ASCII encoding
        if(asciiEncoder == null)
            asciiEncoder = Charset.forName("ASCII").newEncoder();
        if(asciiEncoder.canEncode(charBuf))
            return asciiEncoder.encode(charBuf).asCharBuffer().toString();
        
        //The string contains characters outside the ASCII codepage
        // --> use the UTF-8 encoded string
        return result;
    }
    
    /**
     * Unescapes an escaped character sequence, e.g. \\u00FC.
     * @param iterator The string character iterator pointing to the first character after the backslash
     * @return The unescaped character as a string.
     * @throws UnsupportedEncodingException If an invalid Unicode or ASCII escape sequence is found.
     */
    private static String parseEscapedSequence(StringCharacterIterator iterator) throws UnsupportedEncodingException {        
        char c = iterator.next();
        if(c == '\\') {
            return new String(new byte[]{0, '\\'}, "UTF-8");
        } else if(c == '"') {
            return new String(new byte[]{0, '\"'}, "UTF-8");
        } else if(c == 'b') {
            return new String(new byte[]{0, '\b'}, "UTF-8");
        } else if(c == 'n') {
            return new String(new byte[]{0, '\n'}, "UTF-8");
        } else if(c == 'r') {
            return new String(new byte[]{0, '\r'}, "UTF-8");
        } else if(c == 't') {
            return new String(new byte[]{0, '\t'}, "UTF-8");
        } else if(c == 'U' || c == 'u') {
            //4 digit hex Unicode value
            String byte1 = "";
            byte1 += iterator.next();
            byte1 += iterator.next();
            String byte2 = "";
            byte2 += iterator.next();
            byte2 += iterator.next();
            byte[] stringBytes = {(byte)Integer.parseInt(byte1, 16), (byte)Integer.parseInt(byte2, 16)};
            return new String(stringBytes, "UTF-8");
        } else {
            //3 digit octal ASCII value
            String num = "";
            num += c;
            num += iterator.next();
            num += iterator.next();
            int asciiCode = Integer.parseInt(num, 8);
            byte[] stringBytes = {0, (byte)asciiCode};
            return new String(stringBytes, "UTF-8");
        }
    }
    
}
