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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

/**
 * The NSString class is a wrapper for a string.
 *
 * @author Daniel Dreibrodt
 * @see <a href="https://developer.apple.com/reference/foundation/nsstring"
 *     target="_blank">Foundation NSString documentation</a>
 */
public class NSString extends NSObject {
  private static CharsetEncoder asciiEncoder, utf16beEncoder;

  private String content;

  /**
   * Creates a new NSString instance from its binary representation.
   *
   * @param bytes The binary representation.
   * @param encoding The string encoding (name of the charset).
   * @throws UnsupportedEncodingException When the given encoding is not supported by the JRE.
   * @see java.lang.String#String(byte[], String)
   */
  public NSString(byte[] bytes, String encoding) throws UnsupportedEncodingException {
    this(bytes, 0, bytes.length, encoding);
  }

  /**
   * Creates a new NSString instance from its binary representation.
   *
   * @param bytes An array containing the binary representation of the string.
   * @param startIndex The offset inside the array at which the string data starts.
   * @param endIndex The offset inside the array at which the string data ends.
   * @param encoding The string encoding (name of the charset).
   * @throws UnsupportedEncodingException When the given encoding is not supported by the JRE.
   * @see java.lang.String#String(byte[], int, int, String)
   */
  public NSString(byte[] bytes, final int startIndex, final int endIndex, String encoding)
      throws UnsupportedEncodingException {
    this.content = new String(bytes, startIndex, endIndex - startIndex, encoding);
  }

  /**
   * Creates a new NSString instance with the specified content.
   *
   * @param string The string that will be contained in the NSString.
   */
  public NSString(String string) {
    this.content = string;
  }

  /**
   * Gets the integer value of this string.
   *
   * @return The integer value of this string, assuming a decimal representation and skipping
   *     whitespace at the beginning of the string. If the string does not contain a valid decimal
   *     representation of a number, 0 is returned. If the string contains an integer larger than
   *     Integer.MAX_VALUE, Integer.MAX_VALUE is returned. If the string contains an integer less
   *     than Integer.MIN_VALUE, Integer.MIN_VALUE is returned.
   */
  public int intValue() {
    double d = this.doubleValue();

    if (d > Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }

    if (d < Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }

    return (int) d;
  }

  /**
   * Gets the floating-point value of this string.
   *
   * @return The floating-point value of this string, assuming a decimal representation and skipping
   *     whitespace at the beginning of the string. If the string does not contain a valid decimal
   *     representation of a number, 0 is returned. If the string contains an integer larger than
   *     Float.MAX_VALUE, Float.MAX_VALUE is returned. If the string contains an integer less than
   *     -Float.MAX_VALUE, -Float.MAX_VALUE is returned.
   */
  public float floatValue() {
    double d = this.doubleValue();

    if (d > Float.MAX_VALUE) {
      return Float.MAX_VALUE;
    }

    if (d < -Float.MAX_VALUE) {
      return -Float.MAX_VALUE;
    }

    return (float) d;
  }

  /**
   * Gets the floating-point value (double precision) of this string.
   *
   * @return The floating-point value of this string, assuming a decimal representation and skipping
   *     whitespace at the beginning of the string. If the string does not contain a valid decimal
   *     representation of a floating-point number, 0 is returned.
   */
  public double doubleValue() {
    Scanner s = new Scanner(this.content.trim()).useLocale(Locale.ROOT).useDelimiter("[^\\d.+-]+");
    if (s.hasNextDouble()) {
      return s.nextDouble();
    } else {
      return 0d;
    }
  }

  /**
   * Gets the boolean value of this string.
   *
   * @return The boolean value of this string. Leading whitespaces are ignored. Any + or - sign and
   *     leading zeroes are ignored. If the remaining string starts with 'Y', 'y', 'T', 't' or a
   *     positive digit (1-9), true is returned. Otherwise, false is returned.
   *     <p>Examples: "YES" is true "true" is true " YES" is true "+1" is true "-9" is true " +01"
   *     is true "0" is false "false" is false "no" is false "1FALSE" is true "0TRUE" is true
   *     "FALSE1" is false
   */
  public boolean boolValue() {
    Scanner s = new Scanner(this.content.trim()).useLocale(Locale.ROOT);
    return s.hasNext("([+-]?0*)?[YyTt1-9].*");
  }

  /**
   * Gets the string content of this instance.
   *
   * @return This string contained in this instance.
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Sets the string content of this instance.
   *
   * @param c The new content of this string object.
   */
  public void setContent(String c) {
    this.content = c;
  }

  /**
   * Appends a string to this string.
   *
   * @param s The string to append.
   */
  public void append(NSString s) {
    this.append(s.getContent());
  }

  /**
   * Appends a string to this string.
   *
   * @param s The string to append.
   */
  public void append(String s) {
    this.content += s;
  }

  /**
   * Prepends a string to this string.
   *
   * @param s The string to prepend.
   */
  public void prepend(String s) {
    this.content = s + this.content;
  }

  /**
   * Prepends a string to this string.
   *
   * @param s The string to prepend.
   */
  public void prepend(NSString s) {
    this.prepend(s.getContent());
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null
        && this.getClass() == obj.getClass()
        && this.content.equals(((NSString) obj).content);
  }

  @Override
  public int hashCode() {
    return this.content.hashCode();
  }

  @Override
  public NSString clone() {
    return new NSString(this.content);
  }

  @Override
  public String toString() {
    return this.content;
  }

  @Override
  public Object toJavaObject() {
    return this.content;
  }

  @Override
  void toXML(StringBuilder xml, int level) {
    this.indent(xml, level);
    xml.append("<string>");
    appendXmlText(xml, this.content);
    xml.append("</string>");
  }

  @Override
  public void toBinary(BinaryPropertyListWriter out) throws IOException {
    CharBuffer charBuf = CharBuffer.wrap(this.content);
    int kind;
    ByteBuffer byteBuf;
    synchronized (NSString.class) {
      if (asciiEncoder == null) asciiEncoder = StandardCharsets.US_ASCII.newEncoder();
      else asciiEncoder.reset();

      if (asciiEncoder.canEncode(charBuf)) {
        kind = 0x5; // standard ASCII
        byteBuf = asciiEncoder.encode(charBuf);
      } else {
        if (utf16beEncoder == null) utf16beEncoder = StandardCharsets.UTF_16BE.newEncoder();
        else utf16beEncoder.reset();

        kind = 0x6; // UTF-16-BE
        byteBuf = utf16beEncoder.encode(charBuf);
      }
    }
    byte[] bytes = new byte[byteBuf.remaining()];
    byteBuf.get(bytes);
    out.writeIntHeader(kind, this.content.length());
    out.write(bytes);
  }

  @Override
  void toASCII(StringBuilder ascii, int level, boolean gnuStep) {
    this.indent(ascii, level);
    ascii.append('"');
    // According to
    // https://developer.apple.com/library/mac/#documentation/Cocoa/Conceptual/PropertyLists/OldStylePlists/OldStylePLists.html
    // non-ASCII characters are not escaped but simply written into the
    // file, thus actually violating the ASCII plain text format.
    // We will escape the string anyway because current Xcode project files (ASCII property lists)
    // also escape their strings.
    escapeStringForASCII(ascii, this.content);
    ascii.append('"');
  }

  @Override
  public int compareTo(NSObject o) {
    Objects.requireNonNull(o);
    if (o == this) {
      return 0;
    } else if (o instanceof NSString) {
      return this.getContent().compareTo(((NSString) o).getContent());
    } else {
      return this.getClass().getName().compareTo(o.getClass().getName());
    }
  }

  /**
   * Appends a string to an ASCII property list, escaping it as required.
   *
   * @param out The output buffer.
   * @param s The unescaped string.
   */
  static void escapeStringForASCII(StringBuilder out, String s) {
    int len = s.length();
    int i = 0;
    while (i < len && !needsAsciiEscape(s.charAt(i))) i++;
    out.append(s, 0, i);
    for (; i < len; i++) {
      char c = s.charAt(i);
      if (c > 127) {
        // non-ASCII Unicode
        out.append("\\U");
        String hex = Integer.toHexString(c);
        for (int pad = hex.length(); pad < 4; pad++) out.append('0');
        out.append(hex);
      } else if (c == '\\') {
        out.append("\\\\");
      } else if (c == '\"') {
        out.append("\\\"");
      } else if (c == '\b') {
        out.append("\\b");
      } else if (c == '\n') {
        out.append("\\n");
      } else if (c == '\r') {
        out.append("\\r");
      } else if (c == '\t') {
        out.append("\\t");
      } else {
        out.append(c);
      }
    }
  }

  private static boolean needsAsciiEscape(char c) {
    return c > 127 || c == '\\' || c == '"' || c == '\b' || c == '\n' || c == '\r' || c == '\t';
  }

  /**
   * Appends a string to an XML property list as character data. Characters that are not allowed in
   * XML 1.0 (see https://www.w3.org/TR/xml/#charsets) are dropped, and the text is wrapped in a
   * CDATA section if it contains markup characters.
   *
   * @param xml The output buffer.
   * @param s The string.
   */
  static void appendXmlText(StringBuilder xml, String s) {
    int len = s.length();
    int i = 0;
    while (i < len && isPlainXmlChar(s.charAt(i))) i++;
    if (i == len) {
      xml.append(s);
      return;
    }
    // According to http://www.w3.org/TR/REC-xml/#syntax node values must not
    // contain the characters < or &. Also the > character should be escaped.
    boolean cdata = s.indexOf('&', i) >= 0 || s.indexOf('<', i) >= 0 || s.indexOf('>', i) >= 0;
    if (cdata) xml.append("<![CDATA[");
    xml.append(s, 0, i);
    for (; i < len; i++) {
      int codePoint = s.codePointAt(i);
      if (codePoint > 0xFFFF) {
        i++;
      }

      if ((codePoint == 0x9)
          || (codePoint == 0xA)
          || (codePoint == 0xD)
          || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
          || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
          || (codePoint >= 0x10000)) {
        if (codePoint == '>' && cdata && endsWithCdataTerminatorStart(xml)) {
          // "]]>" must be split across two CDATA sections
          xml.append("]]><![CDATA[");
        }
        xml.appendCodePoint(codePoint);
      }
    }
    if (cdata) xml.append("]]>");
  }

  /** Whether the character can be appended unchanged, without CDATA wrapping. */
  private static boolean isPlainXmlChar(char c) {
    return (c >= 0x20 && c < 0xD800 && c != '&' && c != '<' && c != '>')
        || c == '\t'
        || c == '\n'
        || c == '\r';
  }

  private static boolean endsWithCdataTerminatorStart(StringBuilder sb) {
    int n = sb.length();
    return n >= 2 && sb.charAt(n - 1) == ']' && sb.charAt(n - 2) == ']';
  }
}
