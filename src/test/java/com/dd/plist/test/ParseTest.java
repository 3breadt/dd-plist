package com.dd.plist.test;

import com.dd.plist.*;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ParseTest {
    @Test
    public void testXml() throws Exception {
        // parse an example plist file
        NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));

        // check the data in it
        NSDictionary d = (NSDictionary) x;
        assertEquals(5, d.count());
        assertEquals("valueA", d.objectForKey("keyA").toString());
        assertEquals("value&B", d.objectForKey("key&B").toString());
        assertEquals(((NSDate) d.objectForKey("date")).getDate(), new Date(1322472090000L));
        assertArrayEquals(((NSData) d.objectForKey("data")).bytes(), new byte[]{0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82});
        NSArray a = (NSArray) d.objectForKey("array");
        assertEquals(4, a.count());
        assertEquals(a.objectAtIndex(0), new NSNumber(true));
        assertEquals(a.objectAtIndex(1), new NSNumber(false));
        assertEquals(a.objectAtIndex(2), new NSNumber(87));
        assertEquals(a.objectAtIndex(3), new NSNumber(3.14159));

        // read/write it, make sure we get the same thing
        XMLPropertyListWriter.write(x, new File("test-files/out-testXml.plist"));
        NSObject y = PropertyListParser.parse(new File("test-files/out-testXml.plist"));
        assertEquals(x, y);
    }

    @Test
    public void testXmlLocations() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));
        NSDictionary d = (NSDictionary) x;
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, d.getLocationInformation()).getXPath(),
            "/plist/dict");
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, d.get("keyA").getLocationInformation()).getXPath(),
            "/plist/dict/*[2]");
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, d.get("key&B").getLocationInformation()).getXPath(),
            "/plist/dict/*[4]");
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, d.get("date").getLocationInformation()).getXPath(),
            "/plist/dict/*[6]");
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, d.get("data").getLocationInformation()).getXPath(),
            "/plist/dict/*[8]");
        NSArray array = assertInstanceOf(NSArray.class, d.get("array"));
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, array.getLocationInformation()).getXPath(),
            "/plist/dict/*[10]");
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, array.objectAtIndex(0).getLocationInformation()).getXPath(),
            "/plist/dict/*[10]/*[1]");
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, array.objectAtIndex(1).getLocationInformation()).getXPath(),
            "/plist/dict/*[10]/*[2]");
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, array.objectAtIndex(2).getLocationInformation()).getXPath(),
            "/plist/dict/*[10]/*[3]");
        assertEquals(
            assertInstanceOf(XMLLocationInformation.class, array.objectAtIndex(3).getLocationInformation()).getXPath(),
            "/plist/dict/*[10]/*[4]");
    }

    /**
     * Test parsing of an XML property list in UTF-16BE.
     */
    @Test
    public void testXmlUtf16BeWithBom() throws Exception {
        this.testXmlEncoding("UTF-16BE-BOM");
    }

    /**
     * Test parsing of an XML property list in UTF-16BE, but without the BOM.
     */
    @Test
    public void testXmlUtf16BeWithoutBom() throws Exception {
        this.testXmlEncoding("UTF-16BE");
    }

    /**
     * Test parsing of an XML property list in UTF-16LE.
     */
    @Test
    public void testXmlUtf16LeWithBom() throws Exception {
        this.testXmlEncoding("UTF-16LE-BOM");
    }

    /**
     * Test parsing of an XML property list in UTF-16LE, but without the BOM.
     */
    @Test
    public void testXmlUtf16LeWithoutBom() throws Exception {
        this.testXmlEncoding("UTF-16LE");
    }

    /**
     * Test parsing of an XML property list in UTF-32BE.
     */
    @Test
    public void testXmlUtf32BeWithBom() throws Exception {
        this.testXmlEncoding("UTF-32BE-BOM");
    }

    /**
     * Test parsing of an XML property list in UTF-32BE, but without the BOM.
     */
    @Test
    public void testXmlUtf32BeWithoutBom() throws Exception {
        this.testXmlEncoding("UTF-32BE");
    }

    /**
     * Test parsing of an XML property list in UTF-32LE.
     */
    @Test
    public void testXmlUtf32LeWithBom() throws Exception {
        this.testXmlEncoding("UTF-32LE-BOM");
    }

    /**
     * Test parsing of an XML property list in UTF-32LE, but without the BOM.
     */
    @Test
    public void testXmlUtf32LeWithoutBom() throws Exception {
        this.testXmlEncoding("UTF-32LE");
    }

    private void testXmlEncoding(String encoding) throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test-xml-" + encoding.toLowerCase() + ".plist"));

        // check the data in it
        NSDictionary d = (NSDictionary) x;
        assertEquals(5, d.count());
        assertEquals("valueA", d.objectForKey("keyA").toString());
        assertEquals("value&B \u2705", d.objectForKey("key&B").toString());
        assertEquals(((NSDate) d.objectForKey("date")).getDate(), new Date(1322472090000L));
        assertArrayEquals(((NSData) d.objectForKey("data")).bytes(), new byte[]{0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82});
        NSArray a = (NSArray) d.objectForKey("array");
        assertEquals(4, a.count());
        assertEquals(a.objectAtIndex(0), new NSNumber(true));
        assertEquals(a.objectAtIndex(1), new NSNumber(false));
        assertEquals(a.objectAtIndex(2), new NSNumber(87));
        assertEquals(a.objectAtIndex(3), new NSNumber(3.14159));
    }

    @Test
    public void testXmlWithInfinityValues() throws Exception {
        // See https://github.com/3breadt/dd-plist/issues/83
        NSDictionary dictFromXml = (NSDictionary) XMLPropertyListParser.parse(new File("test-files/infinity-xml.plist"));
        assertEquals(Double.POSITIVE_INFINITY, ((NSNumber)dictFromXml.get("a")).doubleValue());
        assertEquals(Double.NEGATIVE_INFINITY, ((NSNumber)dictFromXml.get("b")).doubleValue());
    }

    @Test
    public void testBinary() throws Exception {
        NSObject x = XMLPropertyListParser.parse(new File("test-files/test1.plist"));

        // save and load as binary
        BinaryPropertyListWriter.write(x, new File("test-files/out-testBinary.plist"));
        NSObject y = PropertyListParser.parse(new File("test-files/out-testBinary.plist"));
        assertEquals(x, y);
    }

    @Test
    public void testBinaryLocations() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test1-binary.plist"));

        NSDictionary d = (NSDictionary) x;
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, d.getLocationInformation()).getId(),
            0);
        // each dictionary key is serialized as an NSObject, as we have 5 keys, the next value object has ID 6
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, d.get("keyA").getLocationInformation()).getId(),
            6);
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, d.get("key&B").getLocationInformation()).getId(),
            7);
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, d.get("date").getLocationInformation()).getId(),
            8);
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, d.get("data").getLocationInformation()).getId(),
            9);
        NSArray array = assertInstanceOf(NSArray.class, d.get("array"));
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, array.getLocationInformation()).getId(),
            10);
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, array.objectAtIndex(0).getLocationInformation()).getId(),
            11);
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, array.objectAtIndex(1).getLocationInformation()).getId(),
            12);
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, array.objectAtIndex(2).getLocationInformation()).getId(),
            13);
        assertEquals(
            assertInstanceOf(BinaryLocationInformation.class, array.objectAtIndex(3).getLocationInformation()).getId(),
            14);
    }

    @Test
    public void testBinaryWithInfinityValues() throws Exception {
        NSDictionary dictFromXml = (NSDictionary) BinaryPropertyListParser.parse(new File("test-files/infinity-binary.plist"));
        assertEquals(Double.POSITIVE_INFINITY, ((NSNumber)dictFromXml.get("a")).doubleValue());
        assertEquals(Double.NEGATIVE_INFINITY, ((NSNumber)dictFromXml.get("b")).doubleValue());
    }

    /**
     * NSSet only occurs in binary property lists, so we have to test it separately.
     * NSSets are not yet supported in reading/writing, as binary property list format v1+ is required.
     */
    /*public void testSet() throws Exception {
        NSSet s = new NSSet();
        s.addObject(new NSNumber(1));
        s.addObject(new NSNumber(3));
        s.addObject(new NSNumber(2));

        NSSet orderedSet = new NSSet(true);
        s.addObject(new NSNumber(1));
        s.addObject(new NSNumber(3));
        s.addObject(new NSNumber(2));

        NSDictionary dict = new NSDictionary();
        dict.put("set1", s);
        dict.put("set2", orderedSet);

        PropertyListParser.saveAsBinary(dict, new File("test-files/out-testSet.plist"));
        NSObject parsedRoot = PropertyListParser.parse(new File("test-files/out-testSet.plist"));
        assertTrue(parsedRoot.equals(dict));
    }*/

    @Test
    public void testASCII() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test1-ascii.plist"));
        NSDictionary d = (NSDictionary) x;
        assertEquals(5, d.count());
        assertEquals("valueA", d.objectForKey("keyA").toString());
        assertEquals("value&B", d.objectForKey("key&B").toString());
        assertEquals(((NSDate) d.objectForKey("date")).getDate(), new Date(1322472090000L));
        assertArrayEquals(((NSData) d.objectForKey("data")).bytes(), new byte[]{0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82});
        NSArray a = (NSArray) d.objectForKey("array");
        assertEquals(4, a.count());
        assertEquals(a.objectAtIndex(0), new NSString("YES"));
        assertEquals(a.objectAtIndex(1), new NSString("NO"));
        assertEquals(a.objectAtIndex(2), new NSString("87"));
        assertEquals(a.objectAtIndex(3), new NSString("3.14159"));
    }

    @Test
    public void testASCIILocations() throws Exception {
        File file = new File("test-files/test1-ascii.plist");
        String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.US_ASCII);
        NSObject x = PropertyListParser.parse(file);
        NSDictionary d = (NSDictionary) x;
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, d.getLocationInformation()).getOffset(),
            text.indexOf("{"));
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, d.get("keyA").getLocationInformation()).getOffset(),
            text.indexOf("valueA"));
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, d.get("key&B").getLocationInformation()).getOffset(),
            text.indexOf("\"value&\\U0042\""));
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, d.get("date").getLocationInformation()).getOffset(),
            text.indexOf("\"2011-11-28T09:21:30Z\""));
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, d.get("data").getLocationInformation()).getOffset(),
            text.indexOf("<00000004 10410820 82>"));
        NSArray array = assertInstanceOf(NSArray.class, d.get("array"));
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, array.getLocationInformation()).getOffset(),
            text.indexOf("("));
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, array.objectAtIndex(0).getLocationInformation()).getOffset(),
            text.indexOf("YES"));
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, array.objectAtIndex(1).getLocationInformation()).getOffset(),
            text.indexOf("NO"));
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, array.objectAtIndex(2).getLocationInformation()).getOffset(),
            text.indexOf("87"));
        assertEquals(
            assertInstanceOf(ASCIILocationInformation.class, array.objectAtIndex(3).getLocationInformation()).getOffset(),
            text.indexOf("3.14159"));
    }

    @Test
    public void testGnuStepASCII() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test1-ascii-gnustep.plist"));
        NSDictionary d = (NSDictionary) x;
        assertEquals(5, d.count());
        assertEquals("valueA", d.objectForKey("keyA").toString());
        assertEquals("value&B", d.objectForKey("key&B").toString());
        assertEquals(((NSDate) d.objectForKey("date")).getDate(), new Date(1322472090000L));
        assertArrayEquals(((NSData) d.objectForKey("data")).bytes(), new byte[]{0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82});
        NSArray a = (NSArray) d.objectForKey("array");
        assertEquals(4, a.count());
        assertEquals(a.objectAtIndex(0), new NSNumber(true));
        assertEquals(a.objectAtIndex(1), new NSNumber(false));
        assertEquals(a.objectAtIndex(2), new NSNumber(87));
        assertEquals(a.objectAtIndex(3), new NSNumber(3.14159));
    }

    @Test
    public void testGnuStepASCIIWithBase64() throws Exception {
        byte[] expectedData = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xBB, (byte) 0xBB, (byte) 0xCC, (byte) 0xCC};

        NSObject x = PropertyListParser.parse(new File("test-files/test1-ascii-gnustep-base64.plist"));
        NSDictionary d = (NSDictionary) x;
        assertEquals(5, d.count());
        assertEquals("valueA", d.objectForKey("keyA").toString());
        assertEquals("value&B", d.objectForKey("key&B").toString());
        assertEquals(((NSDate) d.objectForKey("date")).getDate(), new Date(1322472090000L));
        assertArrayEquals(expectedData, ((NSData) d.objectForKey("data")).bytes());
        NSArray a = (NSArray) d.objectForKey("array");
        assertEquals(4, a.count());
        assertEquals(a.objectAtIndex(0), new NSNumber(true));
        assertEquals(a.objectAtIndex(1), new NSNumber(false));
        assertEquals(a.objectAtIndex(2), new NSNumber(87));
        assertEquals(a.objectAtIndex(3), new NSNumber(3.14159));
    }

    @Test
    public void testASCIIWriting() throws Exception {
        File in = new File("test-files/test1.plist");
        File out = new File("test-files/out-test1-ascii.plist");
        File in2 = new File("test-files/test1-ascii.plist");
        NSDictionary x = (NSDictionary) PropertyListParser.parse(in);
        ASCIIPropertyListWriter.write(x, out);

        //Information gets lost when saving into the ASCII format (NSNumbers are converted to NSStrings)

        NSDictionary y = (NSDictionary) PropertyListParser.parse(out);
        NSDictionary z = (NSDictionary) PropertyListParser.parse(in2);
        assertEquals(y, z);
    }

    @Test
    public void testGnuStepASCIIWriting() throws Exception {
        File in = new File("test-files/test1.plist");
        File out = new File("test-files/out-test1-ascii-gnustep.plist");
        NSDictionary x = (NSDictionary) PropertyListParser.parse(in);
        ASCIIPropertyListWriter.writeGnuStep(x, out);
        NSObject y = PropertyListParser.parse(out);
        assertEquals(x, y);
    }

    @Test
    public void testAsciiNullCharactersInString() {
        assertThrows(ParseException.class, () -> PropertyListParser.parse(new File("test-files/test2-ascii-null-char-in-string.plist")));
    }

    @Test
    public void testAsciiPropertyListEncodedWithUtf8() throws Exception {
        this.testAsciiUnicode("test-ascii-utf-8.plist");
    }

    @Test
    public void testAsciiPropertyListEncodedWithUtf16Be() throws Exception {
        this.testAsciiUnicode("test-ascii-utf-16be.plist");
    }

    @Test
    public void testAsciiPropertyListEncodedWithUtf16Le() throws Exception {
        this.testAsciiUnicode("test-ascii-utf-16le.plist");
    }

    @Test
    public void testAsciiPropertyListEncodedWithUtf32Be() throws Exception {
        this.testAsciiUnicode("test-ascii-utf-32be.plist");
    }

    @Test
    public void testAsciiPropertyListEncodedWithUtf32Le() throws Exception {
        this.testAsciiUnicode("test-ascii-utf-32le.plist");
    }

    private void testAsciiUnicode(String filename) throws Exception {
        // contains BOM, encoding shall be automatically detected
        NSDictionary dict = (NSDictionary) ASCIIPropertyListParser.parse(new File("test-files/" + filename));
        assertEquals(6, dict.count());
        assertEquals("JÔÖú@2x.jpg", dict.objectForKey("path").toString());
        assertEquals("QÔÖú@2x 啕.jpg", dict.objectForKey("Key QÔÖª@2x 䌡").toString());
        assertEquals("もじれつ", dict.get("quoted").toString());
        assertEquals("クオート無し", dict.get("not_quoted").toString());
        assertEquals("\"\\\":\n拡張文字ｷﾀｱｱｱ", dict.get("with_escapes").toString());
        assertEquals("\u0020\u5e78", dict.get("with_u_escapes").toString());
    }

    @Test
    public void testAsciiCommentsAreNotIncludedInStrings() throws Exception {
        String stringFileContentStr = "/* Menu item to make the current document plain text */\n" +
                "\"Make Plain Text\" = \"In reinen Text umwandeln\";\n" +
                "/* Menu item to make the current document rich text */\n" +
                "\"Make Rich Text\" = \"In formatierten Text umwandeln\";\n";
        byte[] stringFileContentRaw = stringFileContentStr.getBytes();

        String stringFileContent = new String(stringFileContentRaw, StandardCharsets.UTF_8);
        String asciiPropertyList = "{" + stringFileContent + "}";
        NSDictionary dict = (NSDictionary) ASCIIPropertyListParser.parse(asciiPropertyList.getBytes(StandardCharsets.UTF_8));
        assertTrue(dict.containsKey("Make Plain Text"));
        assertEquals("In reinen Text umwandeln", dict.get("Make Plain Text").toString());
    }

    @Test
    public void testAsciiEscapeCharacters() throws Exception {
        String asciiPropertyList = "{\n" +
                "a = \"abc \\n def\";\n" +
                "b = \"\\r\";\n" +
                "c = \"xyz\\b\";\n" +
                "d = \"\\tasdf\";\n" +
                "e = \"\\\\ \\\"\";\n" +
                "f = \"a \\' b\";\n" +
                "}";
        NSDictionary dict = (NSDictionary) ASCIIPropertyListParser.parse(asciiPropertyList.getBytes(StandardCharsets.UTF_8));
        assertEquals("abc \n def", dict.get("a").toString());
        assertEquals("\r", dict.get("b").toString());
        assertEquals("xyz\b", dict.get("c").toString());
        assertEquals("\tasdf", dict.get("d").toString());
        assertEquals("\\ \"", dict.get("e").toString());
        assertEquals("a ' b", dict.get("f").toString());
    }
}
