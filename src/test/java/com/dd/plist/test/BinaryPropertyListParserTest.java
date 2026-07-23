package com.dd.plist.test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import com.dd.plist.BinaryLocationInformation;
import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.NSArray;
import com.dd.plist.NSData;
import com.dd.plist.NSDate;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link BinaryPropertyListParser} class.
 * @author Daniel Dreibrodt
 */
public class BinaryPropertyListParserTest {

  private static final byte[] HEADER = new byte[] { 0x62, 0x70, 0x6C, 0x69, 0x73, 0x74, 0x30, 0x30 };
  private static final byte[] INT4 = new byte[]{0x12};
  private static final byte[] INT8 = new byte[]{0x13};
  private static final byte[] HUGE_POSITIVE_4 = new byte[]{0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xF0};
  private static final byte[] NEGATIVE_4 = new byte[]{(byte) 0x80, 0x00, 0x00, 0x00};
  private static final byte[] NEGATIVE_8 = new byte[]{
      (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
      (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

  @Test
  public void parse_canParseBinaryPropertyList() throws Exception {
    NSObject x = PropertyListParser.parse(new File("test-files/test1-binary.plist"));

    // check the data in it
    assertThat(x, instanceOf(NSDictionary.class));
    NSDictionary d = (NSDictionary) x;
    assertThat(d.count(), is(5));
    assertThat(d.objectForKey("keyA").toString(), is("valueA"));
    assertThat(d.objectForKey("key&B").toString(), is("value&B"));
    assertThat(((NSDate) d.objectForKey("date")).getDate(), is(new Date(1322472090000L)));
    assertThat(((NSData) d.objectForKey("data")).bytes(),
        is(new byte[]{0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82}));
    NSArray a = (NSArray) d.objectForKey("array");
    assertThat(a.count(), is(4));
    assertThat(a.objectAtIndex(0), is(new NSNumber(true)));
    assertThat(a.objectAtIndex(1), is(new NSNumber(false)));
    assertThat(a.objectAtIndex(2), is(new NSNumber(87)));
    assertThat(a.objectAtIndex(3), is(new NSNumber(3.14159)));
  }

  @Test
  public void parse_providesCorrectObjectLocations() throws Exception {
    NSObject x = PropertyListParser.parse(new File("test-files/test1-binary.plist"));

    NSDictionary d = (NSDictionary) x;
    assertThat(d.getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) d.getLocationInformation()).getId(), is(0));
    // each dictionary key is serialized as an NSObject, as we have 5 keys, the next value object has ID 6
    assertThat(d.get("keyA").getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) d.get("keyA").getLocationInformation()).getId(), is(6));
    assertThat(d.get("key&B").getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) d.get("key&B").getLocationInformation()).getId(), is(7));
    assertThat(d.get("date").getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) d.get("date").getLocationInformation()).getId(), is(8));
    assertThat(d.get("data").getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) d.get("data").getLocationInformation()).getId(), is(9));
    assertThat(d.get("array"), instanceOf(NSArray.class));
    NSArray array = (NSArray) d.get("array");
    assertThat(array.getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) array.getLocationInformation()).getId(), is(10));
    assertThat(array.objectAtIndex(0).getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) array.objectAtIndex(0).getLocationInformation()).getId(), is(11));
    assertThat(array.objectAtIndex(1).getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) array.objectAtIndex(1).getLocationInformation()).getId(), is(12));
    assertThat(array.objectAtIndex(2).getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) array.objectAtIndex(2).getLocationInformation()).getId(), is(13));
    assertThat(array.objectAtIndex(3).getLocationInformation(), instanceOf(BinaryLocationInformation.class));
    assertThat(((BinaryLocationInformation) array.objectAtIndex(3).getLocationInformation()).getId(), is(14));
  }

  @Test
  public void parse_canHandleNumbersWithInfinityValue() throws Exception {
    NSDictionary dict = (NSDictionary) BinaryPropertyListParser.parse(
        new File("test-files/infinity-binary.plist"));
    assertThat(((NSNumber) dict.get("a")).doubleValue(), is(Double.POSITIVE_INFINITY));
    assertThat(((NSNumber) dict.get("b")).doubleValue(), is(Double.NEGATIVE_INFINITY));
  }

  @Test
  public void parse_validatesArrayLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0xA, INT4, HUGE_POSITIVE_4);
    PropertyListFormatException ex = assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
    assertThat(ex.getMessage(), allOf(containsString("length"), containsString("NSArray")));
  }

  @Test
  public void parse_validatesDataLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0x4, INT4, HUGE_POSITIVE_4);
    PropertyListFormatException ex = assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
    assertThat(ex.getMessage(), allOf(containsString("length"), containsString("NSData")));
  }

  @Test
  public void parse_validatesAsciiStringLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0x5, INT4, HUGE_POSITIVE_4);
    PropertyListFormatException ex = assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
    assertThat(ex.getMessage(), allOf(containsString("length"), containsString("NSString")));
  }

  @Test
  public void parse_validatesUtf16StringLength() throws Exception {
    // The UTF-16 length is computed as 2 * declaredLength, which overflows to a negative value.
    // The bounds check must still reject it rather than reading out of bounds.
    byte[] plist = buildSingleObjectPlist(0x6, INT4, HUGE_POSITIVE_4);
    PropertyListFormatException ex = assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
    assertThat(ex.getMessage(), allOf(containsString("length"), containsString("NSString")));
  }

  @Test
  public void parse_validatesUtf8StringLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0x7, INT4, HUGE_POSITIVE_4);
    assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
  }

  @Test
  public void parse_validatesSetLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0xC, INT4, HUGE_POSITIVE_4);
    PropertyListFormatException ex = assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
    assertThat(ex.getMessage(), allOf(containsString("length"), containsString("NSSet")));
  }

  @Test
  public void parse_validatesOrderedSetLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0xB, INT4, HUGE_POSITIVE_4);
    PropertyListFormatException ex = assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
    assertThat(ex.getMessage(), allOf(containsString("length"), containsString("NSSet")));
  }

  @Test
  public void parse_validatesDictionaryLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0xD, INT4, HUGE_POSITIVE_4);
    PropertyListFormatException ex = assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
    assertThat(ex.getMessage(), allOf(containsString("length"), containsString("NSDictionary")));
  }

  @Test
  public void parse_rejectsNegativeArrayLength() throws Exception {
    // A 4-byte length of 0x80000000 is negative as a signed int. Without a guard this reaches
    // new NSObject[length] and throws an uncaught NegativeArraySizeException.
    byte[] plist = buildSingleObjectPlist(0xA, INT4, NEGATIVE_4);
    assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
  }

  @Test
  public void parse_rejectsNegativeSetLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0xC, INT4, NEGATIVE_4);
    assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
  }

  @Test
  public void parse_rejectsNegativeDictionaryLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0xD, INT4, NEGATIVE_4);
    assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
  }

  @Test
  public void parse_rejectsNegativeDataLength() throws Exception {
    // An 8-byte length of 0xFFFFFFFFFFFFFFFF is -1 as a signed int. Without a guard this reaches
    // copyOfRange and throws an uncaught IllegalArgumentException.
    byte[] plist = buildSingleObjectPlist(0x4, INT8, NEGATIVE_8);
    assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
  }

  @Test
  public void parse_rejectsNegativeStringLength() throws Exception {
    byte[] plist = buildSingleObjectPlist(0x5, INT8, NEGATIVE_8);
    assertThrows(
        PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
  }

  @Test
  public void parse_rejectsExcessivelyNestedStructures() {
    // https://github.com/3breadt/dd-plist/issues/104 (additional finding: unbounded recursion depth)
    byte[] plist = buildDeeplyNestedArrayPlist(500_000);
    assertThrows(PropertyListFormatException.class, () -> BinaryPropertyListParser.parse(plist));
  }

  @Test
  public void parse_allowsDeeplyNestedStructuresWithinLimit() throws Exception {
    // A legitimately (but deeply) nested structure well within the depth limit must still parse.
    byte[] plist = buildDeeplyNestedArrayPlist(400);
    NSObject root = BinaryPropertyListParser.parse(plist);

    int depth = 0;
    NSObject current = root;
    while (current instanceof NSArray) {
      depth++;
      current = ((NSArray) current).objectAtIndex(0);
    }

    assertThat(depth, is(400));
    assertThat(current, is(new NSNumber(false)));
  }

  private static byte[] buildSingleObjectPlist(int typeNibble, byte[] lengthMarker, byte[] lengthValue)
      throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(HEADER);                              // header (offsets 0..7)
    bos.write((typeNibble << 4) | 0x0F);            // object marker with extended length (offset 8)
    bos.write(lengthMarker);                        // integer type marker for the length
    bos.write(lengthValue);                         // the declared length
    bos.write(new byte[16]);                        // padding, so a plausible data region exists
    int offsetTableOffset = bos.size();
    bos.write(0x08);                              // offset table: object 0 lives at offset 8
    byte[] trailer = new byte[32];
    trailer[6] = 0x01;                              // offsetSize
    trailer[7] = 0x01;                              // objectRefSize
    trailer[15] = 0x01;                             // numObjects = 1
    // topObject stays 0 (bytes 16..23)
    trailer[31] = (byte) offsetTableOffset;         // offsetTableOffset (fits in a single byte here)
    bos.write(trailer);
    return bos.toByteArray();
  }

  private static byte[] buildDeeplyNestedArrayPlist(int depth) {
    final int objectRefSize = 4;
    final int offsetSize = 4;
    final int headerLength = 8;
    final int arrayLength = 1 + objectRefSize;                              // marker byte + one reference
    final int numObjects = depth + 1;                                       // depth arrays + terminal
    final int offsetTableOffset = headerLength + depth * arrayLength + 1;   // +1 for terminal object
    final int totalLength = offsetTableOffset + numObjects * offsetSize + 32;

    byte[] data = new byte[totalLength];
    System.arraycopy(HEADER, 0, data, 0, HEADER.length);

    int pos = headerLength;
    for (int i = 0; i < depth; i++) {
      data[pos] = (byte) 0xA1;                                             // NSArray with a single element
      writeInt32BE(data, pos + 1, i + 1);                                  // reference to the next object
      pos += arrayLength;
    }
    data[pos] = 0x08;                                                      // terminal 'false' boolean

    int tablePos = offsetTableOffset;
    for (int i = 0; i < depth; i++) {
      writeInt32BE(data, tablePos, headerLength + i * arrayLength);
      tablePos += offsetSize;
    }
    writeInt32BE(data, tablePos, headerLength + depth * arrayLength);      // offset of the terminal object

    int trailer = totalLength - 32;
    data[trailer + 6] = (byte) offsetSize;
    data[trailer + 7] = (byte) objectRefSize;
    writeInt32BE(data, trailer + 12, numObjects);                         // numObjects (low 32 bits)
    // topObject stays 0 (bytes 16..23)
    writeInt32BE(data, trailer + 28, offsetTableOffset);                  // offsetTableOffset (low 32 bits)
    return data;
  }

  private static void writeInt32BE(byte[] data, int offset, int value) {
    data[offset] = (byte) (value >>> 24);
    data[offset + 1] = (byte) (value >>> 16);
    data[offset + 2] = (byte) (value >>> 8);
    data[offset + 3] = (byte) value;
  }
}
