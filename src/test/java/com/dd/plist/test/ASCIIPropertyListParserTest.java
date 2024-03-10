package com.dd.plist.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dd.plist.ASCIILocationInformation;
import com.dd.plist.ASCIIPropertyListParser;
import com.dd.plist.NSArray;
import com.dd.plist.NSData;
import com.dd.plist.NSDate;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ASCIIPropertyListParser} class.
 * @author Daniel Dreibrodt
 */
public class ASCIIPropertyListParserTest {
  @Test
  public void parse_canParseAppleFormat() throws Exception {
    NSObject x = PropertyListParser.parse(new File("test-files/test1-ascii.plist"));
    NSDictionary d = assertInstanceOf(NSDictionary.class, x);
    assertEquals(5, d.count());
    assertEquals("valueA", d.objectForKey("keyA").toString());
    assertEquals("value&B", d.objectForKey("key&B").toString());
    assertEquals(((NSDate) d.objectForKey("date")).getDate(), new Date(1322472090000L));
    assertArrayEquals(((NSData) d.objectForKey("data")).bytes(),
        new byte[]{0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82});
    NSArray a = (NSArray) d.objectForKey("array");
    assertEquals(4, a.count());
    assertEquals(a.objectAtIndex(0), new NSString("YES"));
    assertEquals(a.objectAtIndex(1), new NSString("NO"));
    assertEquals(a.objectAtIndex(2), new NSString("87"));
    assertEquals(a.objectAtIndex(3), new NSString("3.14159"));
  }

  @Test
  public void parse_providesCorrectObjectLocationsForAppleFormat() throws Exception {
    File file = new File("test-files/test1-ascii.plist");
    String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.US_ASCII);
    List<String> lines = Files.readAllLines(file.toPath());

    BiConsumer<NSObject, String> locationChecker = (NSObject object, String token) -> {
      ASCIILocationInformation location = assertInstanceOf(ASCIILocationInformation.class,
          object.getLocationInformation());
      assertEquals(text.indexOf(token), location.getOffset());
      assertEquals(lines.get(location.getLineNumber() - 1).indexOf(token),
          location.getColumnNumber() - 1);
    };

    NSObject x = PropertyListParser.parse(file);
    NSDictionary d = (NSDictionary) x;
    locationChecker.accept(d, "{");
    locationChecker.accept(d.get("keyA"), "valueA");
    locationChecker.accept(d.get("key&B"), "\"value&\\U0042\"");
    locationChecker.accept(d.get("date"), "\"2011-11-28T09:21:30Z\"");
    locationChecker.accept(d.get("data"), "<00000004 10410820 82>");
    NSArray array = assertInstanceOf(NSArray.class, d.get("array"));
    locationChecker.accept(array, "(");
    locationChecker.accept(array.objectAtIndex(0), "YES");
    locationChecker.accept(array.objectAtIndex(1), "NO");
    locationChecker.accept(array.objectAtIndex(2), "87");
    locationChecker.accept(array.objectAtIndex(3), "3.14159");
  }

  @Test
  public void parse_providesCorrectObjectLocationsWhenAdditionalLineBreaksArePresent() throws Exception {
    File file = new File("test-files/test1-ascii-multiline-handling.plist");
    String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.US_ASCII);
    List<String> lines = Files.readAllLines(file.toPath());

    BiConsumer<NSObject, String> locationChecker = (NSObject object, String token) -> {
      ASCIILocationInformation location = assertInstanceOf(ASCIILocationInformation.class,
          object.getLocationInformation());
      assertEquals(
          text.indexOf(token),
          location.getOffset(),
          "Incorrect location of " + object + ": " + location);
      assertEquals(
          lines.get(location.getLineNumber() - 1).indexOf(token),
          location.getColumnNumber() - 1,
          "Incorrect location of " + object + ": " + location);
    };

    NSObject x = PropertyListParser.parse(file);
    NSDictionary d = (NSDictionary) x;
    locationChecker.accept(d, "{");
    locationChecker.accept(d.get("keyA"), "valueA");
    locationChecker.accept(d.get("key&B"), "\"Multi");
    locationChecker.accept(d.get("date"), "\"2011-11-28T09:21:30Z\"");
    locationChecker.accept(d.get("data"), "<00000004");
    NSArray array = assertInstanceOf(NSArray.class, d.get("array"));
    locationChecker.accept(array, "(");
    locationChecker.accept(array.objectAtIndex(0), "YES");
    locationChecker.accept(array.objectAtIndex(1), "NO");
    locationChecker.accept(array.objectAtIndex(2), "87");
    locationChecker.accept(array.objectAtIndex(3), "3.14159");
  }

  @Test
  public void parse_canParseGnuStepFormat() throws Exception {
    NSObject x = PropertyListParser.parse(new File("test-files/test1-ascii-gnustep.plist"));
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
  public void parse_providesCorrectObjectLocationsForGnuStepFormat() throws Exception {
    File file = new File("test-files/test1-ascii-gnustep.plist");
    String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.US_ASCII);
    List<String> lines = Files.readAllLines(file.toPath());

    BiConsumer<NSObject, String> locationChecker = (NSObject object, String token) -> {
      ASCIILocationInformation location = assertInstanceOf(ASCIILocationInformation.class,
          object.getLocationInformation());
      assertEquals(text.indexOf(token), location.getOffset());
      assertEquals(lines.get(location.getLineNumber() - 1).indexOf(token),
          location.getColumnNumber() - 1);
    };

    NSObject x = PropertyListParser.parse(file);
    NSDictionary d = (NSDictionary) x;
    locationChecker.accept(d, "{");
    locationChecker.accept(d.get("keyA"), "valueA");
    locationChecker.accept(d.get("key&B"), "\"value&\\U0042\"");
    locationChecker.accept(d.get("date"), "<*D2011-11-28 09:21:30 +0000>");
    locationChecker.accept(d.get("data"), "<00000004 10410820 82>");
    NSArray array = assertInstanceOf(NSArray.class, d.get("array"));
    locationChecker.accept(array, "(");
    locationChecker.accept(array.objectAtIndex(0), "<*BY>");
    locationChecker.accept(array.objectAtIndex(1), "<*BN>");
    locationChecker.accept(array.objectAtIndex(2), "<*I87>");
    locationChecker.accept(array.objectAtIndex(3), "<*R3.14159>");
  }

  @Test
  public void parse_canHandleGnuStepBase64Data() throws Exception {
    byte[] expectedData = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xBB, (byte) 0xBB,
        (byte) 0xCC, (byte) 0xCC};

    NSObject x = PropertyListParser.parse(new File("test-files/test1-ascii-gnustep-base64.plist"));
    NSDictionary d = assertInstanceOf(NSDictionary.class, x);
    assertArrayEquals(expectedData, ((NSData) d.objectForKey("data")).bytes());
  }

  @Test
  public void parse_rejectsAsciiNullCharactersInString() {
    assertThrows(ParseException.class, () -> PropertyListParser.parse(
        new File("test-files/test2-ascii-null-char-in-string.plist")));
  }

  @Test
  public void parse_canHandleUtf8Encoding() throws Exception {
    this.testAsciiUnicode("test-ascii-utf-8.plist");
  }

  @Test
  public void parse_canHandleUtf16BeEncoding() throws Exception {
    this.testAsciiUnicode("test-ascii-utf-16be.plist");
  }

  @Test
  public void parse_canHandleUtf16LeEncoding() throws Exception {
    this.testAsciiUnicode("test-ascii-utf-16le.plist");
  }

  @Test
  public void parse_canHandleUtf32BeEncoding() throws Exception {
    this.testAsciiUnicode("test-ascii-utf-32be.plist");
  }

  @Test
  public void parse_canHandleUtf32LeEncoding() throws Exception {
    this.testAsciiUnicode("test-ascii-utf-32le.plist");
  }

  @Test
  public void parse_canHandleComments() throws Exception {
    String stringFileContentStr = "/* Menu item to make the current document plain text */\n" +
        "\"Make Plain Text\" = \"In reinen Text umwandeln\";\n" +
        "/* Menu item to make the current document rich text */\n" +
        "\"Make Rich Text\" = \"In formatierten Text umwandeln\";\n";
    byte[] stringFileContentRaw = stringFileContentStr.getBytes();

    String stringFileContent = new String(stringFileContentRaw, StandardCharsets.UTF_8);
    String asciiPropertyList = "{" + stringFileContent + "}";
    NSDictionary dict = (NSDictionary) ASCIIPropertyListParser.parse(
        asciiPropertyList.getBytes(StandardCharsets.UTF_8));
    assertTrue(dict.containsKey("Make Plain Text"));
    assertEquals("In reinen Text umwandeln", dict.get("Make Plain Text").toString());
  }

  @Test
  public void parse_canHandleEscapedCharacters() throws Exception {
    String asciiPropertyList = "{\n" +
        "a = \"abc \\n def\";\n" +
        "b = \"\\r\";\n" +
        "c = \"xyz\\b\";\n" +
        "d = \"\\tasdf\";\n" +
        "e = \"\\\\ \\\"\";\n" +
        "f = \"a \\' b\";\n" +
        "g = \"\\u07F7\";" +
        "h = \"\\775\";" +
        "}";
    NSDictionary dict = (NSDictionary) ASCIIPropertyListParser.parse(
        asciiPropertyList.getBytes(StandardCharsets.US_ASCII));
    assertEquals("abc \n def", dict.get("a").toString());
    assertEquals("\r", dict.get("b").toString());
    assertEquals("xyz\b", dict.get("c").toString());
    assertEquals("\tasdf", dict.get("d").toString());
    assertEquals("\\ \"", dict.get("e").toString());
    assertEquals("a ' b", dict.get("f").toString());
    assertEquals("߷", dict.get("g").toString());
    assertEquals("ǽ", dict.get("h").toString());
  }

  @Test
  public void parse_canHandleIncompleteEscapeSequence() {
    String asciiPropertyList = "{\n" +
        "a = \"\\u123\";\n" +
        "}";

    ParseException ex = assertThrows(ParseException.class, () -> ASCIIPropertyListParser.parse(
        asciiPropertyList.getBytes(StandardCharsets.US_ASCII)));
    assertEquals(asciiPropertyList.indexOf('\\'), ex.getErrorOffset());
  }

  private void testAsciiUnicode(String filename) throws Exception {
    // contains BOM, encoding shall be automatically detected
    NSDictionary dict = (NSDictionary) ASCIIPropertyListParser.parse(
        new File("test-files/" + filename));
    assertEquals(6, dict.count());
    assertEquals("JÔÖú@2x.jpg", dict.objectForKey("path").toString());
    assertEquals("QÔÖú@2x 啕.jpg", dict.objectForKey("Key QÔÖª@2x 䌡").toString());
    assertEquals("もじれつ", dict.get("quoted").toString());
    assertEquals("クオート無し", dict.get("not_quoted").toString());
    assertEquals("\"\\\":\n拡張文字ｷﾀｱｱｱ", dict.get("with_escapes").toString());
    assertEquals(" 幸", dict.get("with_u_escapes").toString());
  }
}
