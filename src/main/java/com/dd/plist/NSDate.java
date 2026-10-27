/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2011 Daniel Dreibrodt, Keith Randall
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * The NSDate class wraps a date.
 *
 * <p>Like Foundation's NSDate, an instance stores its value as a time interval in seconds since the
 * reference date (2001-01-01 00:00:00 UTC), held as a {@code double}. That value is what binary
 * property lists contain, so a date read from a binary property list is written back unchanged. The
 * {@link Date} returned by {@link #getDate()} is that interval rounded to the nearest millisecond.
 *
 * @author Daniel Dreibrodt
 * @see <a href="https://developer.apple.com/reference/foundation/nsdate" target="_blank">Foundation
 *     NSDate documentation</a>
 */
public class NSDate extends NSObject {

  private final Date date;
  private final double timeIntervalSinceReferenceDate;
  private final long dateMillis;

  // EPOCH = new SimpleDateFormat("yyyy MM dd zzz").parse("2001 01 01 GMT").getTime();
  // ...but that's annoying in a static initializer because it can throw exceptions, ick.
  // So we just hardcode the correct value.
  private static final long EPOCH = 978307200000L;

  private static final SimpleDateFormat sdfDefault =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private static final SimpleDateFormat sdfGnuStep = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

  /**
   * Creates new NSDate instance from its binary representation.
   *
   * @param bytes The binary date representation.
   */
  public NSDate(byte[] bytes) {
    this(bytes, 0, bytes.length);
  }

  /**
   * Creates a new NSDate instance from its binary representation.
   *
   * @param bytes The byte array containing the date data.
   * @param startIndex The index within the array at which the date data begins.
   * @param endIndex The index within the array at which the data date ends.
   */
  public NSDate(byte[] bytes, final int startIndex, final int endIndex) {
    // dates are 8 byte big-endian double, seconds since the epoch
    this(BinaryPropertyListParser.parseDouble(bytes, startIndex, endIndex));
  }

  /**
   * Creates a new NSDate instance from a time interval relative to the reference date (2001-01-01
   * 00:00:00 UTC). The time interval is stored exactly. The {@link Date} returned by {@link
   * #getDate()} is the time interval rounded to the nearest millisecond, limited to the range of
   * {@link Date}. A time interval that is not a number is represented by the reference date.
   *
   * @param timeIntervalSinceReferenceDate The number of seconds since the reference date. Negative
   *     values denote dates before the reference date.
   * @see <a
   *     href="https://developer.apple.com/documentation/foundation/nsdate/init(timeintervalsincereferencedate:)"
   *     target="_blank">Foundation NSDate init(timeIntervalSinceReferenceDate:)</a>
   */
  public NSDate(double timeIntervalSinceReferenceDate) {
    this.timeIntervalSinceReferenceDate = timeIntervalSinceReferenceDate;
    this.dateMillis = toMillis(timeIntervalSinceReferenceDate);
    this.date = new Date(this.dateMillis);
  }

  /**
   * Creates a new NSDate instance from is textual representation. The textual representation must
   * adhere to one of the following patterns. For XML property lists: <code>yyyy-MM-dd'T'HH:mm:ss'Z'
   * </code> For ASCII property lists: <code>yyyy-MM-dd HH:mm:ss Z</code>
   *
   * @param textRepresentation The textual representation of the date.
   * @throws ParseException If the date could not be parsed, i.e. it does not match the expected
   *     pattern.
   */
  public NSDate(String textRepresentation) throws ParseException {
    this.date = parseDateString(textRepresentation);
    this.dateMillis = this.date.getTime();
    this.timeIntervalSinceReferenceDate = toTimeInterval(this.dateMillis);
  }

  /**
   * Creates a new NSDate instance.
   *
   * @param d The date.
   */
  public NSDate(Date d) {
    if (d == null) throw new IllegalArgumentException("Date cannot be null");
    this.date = d;
    this.dateMillis = d.getTime();
    this.timeIntervalSinceReferenceDate = toTimeInterval(this.dateMillis);
  }

  private NSDate(Date d, double timeIntervalSinceReferenceDate) {
    this.date = d;
    this.dateMillis = d.getTime();
    this.timeIntervalSinceReferenceDate = timeIntervalSinceReferenceDate;
  }

  static {
    sdfDefault.setTimeZone(TimeZone.getTimeZone("GMT"));
    sdfGnuStep.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  /**
   * Converts a time interval since the reference date to milliseconds since the Unix epoch,
   * rounding to the nearest millisecond. Values beyond the range of a long are clamped and NaN is
   * mapped to the reference date.
   *
   * @param timeIntervalSinceReferenceDate The number of seconds since the reference date.
   * @return The number of milliseconds since the Unix epoch.
   */
  private static long toMillis(double timeIntervalSinceReferenceDate) {
    // Math.round saturates at Long.MIN_VALUE/Long.MAX_VALUE, only the addition can overflow
    long offset = Math.round(1000 * timeIntervalSinceReferenceDate);
    return offset > Long.MAX_VALUE - EPOCH ? Long.MAX_VALUE : EPOCH + offset;
  }

  private static double toTimeInterval(long millis) {
    return (millis - EPOCH) / 1000.0;
  }

  /**
   * Parses a date string and creates a Java Date object from it. This function is synchronized
   * because the underlying SimpleDateFormat instances are not thread-safe.
   *
   * @param textRepresentation The date string as found in an ASCII or XML property list
   * @return The parsed Date
   * @throws ParseException If the given string cannot be parsed.
   * @see SimpleDateFormat#parse(java.lang.String)
   */
  private static synchronized Date parseDateString(String textRepresentation)
      throws ParseException {
    try {
      return sdfDefault.parse(textRepresentation);
    } catch (ParseException ex) {
      return sdfGnuStep.parse(textRepresentation);
    }
  }

  /**
   * Generates a string representation of a Java Date object. The string is formatted according to
   * the specification for XML property list dates.
   *
   * @param date The date which should be represented.
   * @return The string representation of the date.
   */
  private static synchronized String makeDateString(Date date) {
    return sdfDefault.format(date);
  }

  /**
   * Generates a string representation of a Java Date object. The string is formatted according to
   * the specification for GnuStep ASCII property list dates.
   *
   * @param date The date which should be represented.
   * @return The string representation of the date.
   */
  private static synchronized String makeDateStringGnuStep(Date date) {
    return sdfGnuStep.format(date);
  }

  /**
   * Gets the date. Its precision is limited to milliseconds, see {@link
   * #getTimeIntervalSinceReferenceDate()} for the exact value. Changes to the returned date are
   * reflected by this NSDate.
   *
   * @return The date.
   */
  public Date getDate() {
    return this.date;
  }

  /**
   * Gets the exact time interval between this date and the reference date (2001-01-01 00:00:00
   * UTC). If the {@link Date} returned by {@link #getDate()} has been modified, the time interval
   * is derived from its new value.
   *
   * @return The number of seconds since the reference date. Negative values denote dates before the
   *     reference date.
   * @see <a
   *     href="https://developer.apple.com/documentation/foundation/nsdate/timeintervalsincereferencedate"
   *     target="_blank">Foundation NSDate timeIntervalSinceReferenceDate</a>
   */
  public double getTimeIntervalSinceReferenceDate() {
    long millis = this.date.getTime();
    return millis == this.dateMillis ? this.timeIntervalSinceReferenceDate : toTimeInterval(millis);
  }

  /**
   * Checks whether this NSDate is equal to another object. Two NSDate instances are equal when
   * their time intervals since the reference date are identical, so dates that only differ below
   * one millisecond are not equal even though their {@link #getDate()} values are.
   *
   * @param obj The object to compare to.
   * @return Whether the object is an NSDate with the same time interval.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    return obj.getClass().equals(this.getClass())
        && Double.doubleToRawLongBits(this.getTimeIntervalSinceReferenceDate())
            == Double.doubleToRawLongBits(((NSDate) obj).getTimeIntervalSinceReferenceDate());
  }

  @Override
  public int hashCode() {
    return Long.hashCode(Double.doubleToRawLongBits(this.getTimeIntervalSinceReferenceDate()));
  }

  @Override
  public NSDate clone() {
    return new NSDate((Date) this.getDate().clone(), this.getTimeIntervalSinceReferenceDate());
  }

  @Override
  void toXML(StringBuilder xml, int level) {
    this.indent(xml, level);
    xml.append("<date>");
    xml.append(makeDateString(this.date));
    xml.append("</date>");
  }

  @Override
  void toBinary(BinaryPropertyListWriter out) throws IOException {
    out.write(0x33);
    out.writeDouble(this.getTimeIntervalSinceReferenceDate());
  }

  /**
   * Generates a string representation of the date.
   *
   * @return A string representation of the date.
   * @see java.util.Date#toString()
   */
  @Override
  public String toString() {
    return this.date.toString();
  }

  @Override
  public Object toJavaObject() {
    return this.date;
  }

  @Override
  public int compareTo(NSObject o) {
    Objects.requireNonNull(o);
    if (o == this) {
      return 0;
    } else if (o instanceof NSDate) {
      double thisInterval = this.getTimeIntervalSinceReferenceDate();
      double otherInterval = ((NSDate) o).getTimeIntervalSinceReferenceDate();
      int result = Double.compare(thisInterval, otherInterval);
      // Double.compare treats all NaN values as equal, distinguish them as equals() does
      return result != 0
          ? result
          : Long.compare(
              Double.doubleToRawLongBits(thisInterval), Double.doubleToRawLongBits(otherInterval));
    } else {
      return this.getClass().getName().compareTo(o.getClass().getName());
    }
  }

  @Override
  void toASCII(StringBuilder ascii, int level, boolean gnuStep) {
    this.indent(ascii, level);
    if (gnuStep) {
      ascii.append("<*D");
      ascii.append(makeDateStringGnuStep(this.date));
      ascii.append('>');
    } else {
      ascii.append('"');
      ascii.append(makeDateString(this.date));
      ascii.append('"');
    }
  }
}
