package com.dd.plist.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.dd.plist.BinaryLocationInformation;
import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.NSArray;
import com.dd.plist.NSData;
import com.dd.plist.NSDate;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import java.io.File;
import java.util.Date;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link BinaryPropertyListParser} class.
 * @author Daniel Dreibrodt
 */
public class BinaryPropertyListParserTest {
  @Test
  public void parse_canParseBinaryPropertyList() throws Exception {
    NSObject x = PropertyListParser.parse(new File("test-files/test1-binary.plist"));

    // check the data in it
    NSDictionary d = assertInstanceOf(NSDictionary.class, x);
    assertEquals(5, d.count());
    assertEquals("valueA", d.objectForKey("keyA").toString());
    assertEquals("value&B", d.objectForKey("key&B").toString());
    assertEquals(((NSDate) d.objectForKey("date")).getDate(), new Date(1322472090000L));
    assertArrayEquals(((NSData) d.objectForKey("data")).bytes(),
        new byte[]{0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82});
    NSArray a = (NSArray) d.objectForKey("array");
    assertEquals(4, a.count());
    assertEquals(a.objectAtIndex(0), new NSNumber(true));
    assertEquals(a.objectAtIndex(1), new NSNumber(false));
    assertEquals(a.objectAtIndex(2), new NSNumber(87));
    assertEquals(a.objectAtIndex(3), new NSNumber(3.14159));
  }

  @Test
  public void parse_providesCorrectObjectLocations() throws Exception {
    NSObject x = PropertyListParser.parse(new File("test-files/test1-binary.plist"));

    NSDictionary d = (NSDictionary) x;
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class, d.getLocationInformation()).getId(),
        0);
    // each dictionary key is serialized as an NSObject, as we have 5 keys, the next value object has ID 6
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class,
            d.get("keyA").getLocationInformation()).getId(),
        6);
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class,
            d.get("key&B").getLocationInformation()).getId(),
        7);
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class,
            d.get("date").getLocationInformation()).getId(),
        8);
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class,
            d.get("data").getLocationInformation()).getId(),
        9);
    NSArray array = assertInstanceOf(NSArray.class, d.get("array"));
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class, array.getLocationInformation()).getId(),
        10);
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class,
            array.objectAtIndex(0).getLocationInformation()).getId(),
        11);
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class,
            array.objectAtIndex(1).getLocationInformation()).getId(),
        12);
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class,
            array.objectAtIndex(2).getLocationInformation()).getId(),
        13);
    assertEquals(
        assertInstanceOf(BinaryLocationInformation.class,
            array.objectAtIndex(3).getLocationInformation()).getId(),
        14);
  }

  @Test
  public void parse_canHandleNumbersWithInfinityValue() throws Exception {
    NSDictionary dict = (NSDictionary) BinaryPropertyListParser.parse(
        new File("test-files/infinity-binary.plist"));
    assertEquals(Double.POSITIVE_INFINITY, ((NSNumber) dict.get("a")).doubleValue());
    assertEquals(Double.NEGATIVE_INFINITY, ((NSNumber) dict.get("b")).doubleValue());
  }
}
