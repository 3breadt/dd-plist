package com.dd.plist.test;

import static org.junit.jupiter.api.Assertions.*;

import com.dd.plist.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.Test;

public class XMLPropertyListParserTest {

  @Test
  public void parse_canParseXmlPropertyList() throws Exception {
    // parse an example plist file
    NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));

    // check the data in it
    NSDictionary d = assertInstanceOf(NSDictionary.class, x);
    assertEquals(5, d.count());
    assertEquals("valueA", d.objectForKey("keyA").toString());
    assertEquals("value&B", d.objectForKey("key&B").toString());
    assertEquals(((NSDate) d.objectForKey("date")).getDate(), new Date(1322472090000L));
    assertArrayEquals(
        ((NSData) d.objectForKey("data")).bytes(),
        new byte[] {0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82});
    NSArray a = (NSArray) d.objectForKey("array");
    assertEquals(4, a.count());
    assertEquals(a.objectAtIndex(0), new NSNumber(true));
    assertEquals(a.objectAtIndex(1), new NSNumber(false));
    assertEquals(a.objectAtIndex(2), new NSNumber(87));
    assertEquals(a.objectAtIndex(3), new NSNumber(3.14159));
  }

  @Test
  public void parse_providesCorrectObjectLocations() throws Exception {
    BiConsumer<NSObject, String> locationChecker =
        (NSObject object, String expectedLocation) -> {
          XMLLocationInformation location =
              assertInstanceOf(XMLLocationInformation.class, object.getLocationInformation());
          assertEquals(
              expectedLocation,
              location.getXPath()
                  + ";"
                  + location.getLineNumber()
                  + ":"
                  + location.getColumnNumber());
        };

    NSObject x = XMLPropertyListParser.parse(new File("test-files/test1.plist"), true);
    NSDictionary d = (NSDictionary) x;

    locationChecker.accept(d, "/plist/dict;4:7");
    locationChecker.accept(d.get("keyA"), "/plist/dict/*[2];6:11");
    locationChecker.accept(d.get("key&B"), "/plist/dict/*[4];8:11");
    locationChecker.accept(d.get("date"), "/plist/dict/*[6];10:9");
    locationChecker.accept(d.get("data"), "/plist/dict/*[8];12:9");
    NSArray array = assertInstanceOf(NSArray.class, d.get("array"));
    locationChecker.accept(array, "/plist/dict/*[10];14:10");
    locationChecker.accept(array.objectAtIndex(0), "/plist/dict/*[10]/*[1];15:12");
    locationChecker.accept(array.objectAtIndex(1), "/plist/dict/*[10]/*[2];16:13");
    locationChecker.accept(array.objectAtIndex(2), "/plist/dict/*[10]/*[3];17:14");
    locationChecker.accept(array.objectAtIndex(3), "/plist/dict/*[10]/*[4];18:11");
  }

  /** Test parsing of an XML property list in UTF-16BE. */
  @Test
  public void parse_canHandleUtf16BeEncoding() throws Exception {
    this.testXmlEncoding("UTF-16BE-BOM");
  }

  /** Test parsing of an XML property list in UTF-16BE, but without the BOM. */
  @Test
  public void parse_canHandleUtf16BeEncodingWithoutBom() throws Exception {
    this.testXmlEncoding("UTF-16BE");
  }

  /** Test parsing of an XML property list in UTF-16LE. */
  @Test
  public void parse_canHandleUtf16LeEncoding() throws Exception {
    this.testXmlEncoding("UTF-16LE-BOM");
  }

  /** Test parsing of an XML property list in UTF-16LE, but without the BOM. */
  @Test
  public void parse_canHandleUtf16LeEncodingWithoutBom() throws Exception {
    this.testXmlEncoding("UTF-16LE");
  }

  /** Test parsing of an XML property list in UTF-32BE. */
  @Test
  public void parse_canHandleUtf32BeEncoding() throws Exception {
    this.testXmlEncoding("UTF-32BE-BOM");
  }

  /** Test parsing of an XML property list in UTF-32BE, but without the BOM. */
  @Test
  public void parse_canHandleUtf32BeEncodingWithoutBom() throws Exception {
    this.testXmlEncoding("UTF-32BE");
  }

  /** Test parsing of an XML property list in UTF-32LE. */
  @Test
  public void parse_canHandleUtf32LeEncoding() throws Exception {
    this.testXmlEncoding("UTF-32LE-BOM");
  }

  /** Test parsing of an XML property list in UTF-32LE, but without the BOM. */
  @Test
  public void parse_canHandleUtf32LeEncodingWithoutBom() throws Exception {
    this.testXmlEncoding("UTF-32LE");
  }

  @Test
  public void parse_canHandleNumbersWithInfinityValue() throws Exception {
    // See https://github.com/3breadt/dd-plist/issues/83
    NSDictionary dictFromXml =
        (NSDictionary) XMLPropertyListParser.parse(new File("test-files/infinity-xml.plist"));
    assertEquals(Double.POSITIVE_INFINITY, ((NSNumber) dictFromXml.get("a")).doubleValue());
    assertEquals(Double.NEGATIVE_INFINITY, ((NSNumber) dictFromXml.get("b")).doubleValue());
  }

  /**
   * Verifies that an excessively (or maliciously) nested XML property list is rejected with a
   * {@link PropertyListFormatException} instead of causing a {@link StackOverflowError}.
   */
  @Test
  public void parse_rejectsExcessivelyNestedStructures() {
    byte[] plist = buildDeeplyNestedArrayPlist(50000).getBytes(StandardCharsets.UTF_8);
    PropertyListFormatException ex =
        assertThrows(PropertyListFormatException.class, () -> XMLPropertyListParser.parse(plist));
    assertTrue(
        ex.getMessage().contains("nesting depth"),
        "Unexpected exception message: " + ex.getMessage());
  }

  /**
   * Verifies that a legitimately deeply nested XML property list within the supported depth limit
   * can still be parsed.
   */
  @Test
  public void parse_allowsDeeplyNestedStructuresWithinLimit() throws Exception {
    byte[] plist = buildDeeplyNestedArrayPlist(400).getBytes(StandardCharsets.UTF_8);
    NSObject parsed = XMLPropertyListParser.parse(plist);

    int depth = 0;
    NSObject current = parsed;
    while (current instanceof NSArray) {
      NSArray array = (NSArray) current;
      assertEquals(1, array.count());
      current = array.objectAtIndex(0);
      depth++;
    }

    assertEquals(400, depth);
    assertEquals(new NSString("leaf"), current);
  }

  /**
   * Builds an XML property list consisting of {@code depth} nested arrays, each containing a single
   * child, with an innermost {@code <string>} leaf element.
   *
   * @param depth The number of nested array elements.
   * @return The XML property list as a string.
   */
  private static String buildDeeplyNestedArrayPlist(int depth) {
    StringBuilder builder = new StringBuilder();
    builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    builder.append("<plist version=\"1.0\">");
    for (int i = 0; i < depth; i++) {
      builder.append("<array>");
    }
    builder.append("<string>leaf</string>");
    for (int i = 0; i < depth; i++) {
      builder.append("</array>");
    }
    builder.append("</plist>");
    return builder.toString();
  }

  private void testXmlEncoding(String encoding) throws Exception {
    NSObject x =
        PropertyListParser.parse(
            new File("test-files/test-xml-" + encoding.toLowerCase() + ".plist"));

    // check the data in it
    NSDictionary d = assertInstanceOf(NSDictionary.class, x);
    assertEquals(5, d.count());
    assertEquals("valueA", d.objectForKey("keyA").toString());
    assertEquals("value&B \u2705", d.objectForKey("key&B").toString());
    assertEquals(((NSDate) d.objectForKey("date")).getDate(), new Date(1322472090000L));
    assertArrayEquals(
        ((NSData) d.objectForKey("data")).bytes(),
        new byte[] {0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82});
    NSArray a = (NSArray) d.objectForKey("array");
    assertEquals(4, a.count());
    assertEquals(a.objectAtIndex(0), new NSNumber(true));
    assertEquals(a.objectAtIndex(1), new NSNumber(false));
    assertEquals(a.objectAtIndex(2), new NSNumber(87));
    assertEquals(a.objectAtIndex(3), new NSNumber(3.14159));
  }
}
