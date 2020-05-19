package com.dd.plist.test;

import com.dd.plist.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


public class ParseTest {
    /**
     * Test the xml reader/writer
     */
    @Test
    public void testXml() throws Exception {
        // parse an example plist file
        NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));

        // check the data in it
        NSDictionary d = (NSDictionary) x;
        assertTrue(d.count() == 5);
        assertTrue(d.objectForKey("keyA").toString().equals("valueA"));
        assertTrue(d.objectForKey("key&B").toString().equals("value&B"));
        assertTrue(((NSDate) d.objectForKey("date")).getDate().equals(new Date(1322472090000L)));
        assertTrue(Arrays.equals(((NSData) d.objectForKey("data")).bytes(),
                new byte[] {0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82}));
        NSArray a = (NSArray) d.objectForKey("array");
        assertTrue(a.count() == 4);
        assertTrue(a.objectAtIndex(0).equals(new NSNumber(true)));
        assertTrue(a.objectAtIndex(1).equals(new NSNumber(false)));
        assertTrue(a.objectAtIndex(2).equals(new NSNumber(87)));
        assertTrue(a.objectAtIndex(3).equals(new NSNumber(3.14159)));

        // read/write it, make sure we get the same thing
        PropertyListParser.saveAsXML(x, new File("test-files/out-testXml.plist"));
        NSObject y = PropertyListParser.parse(new File("test-files/out-testXml.plist"));
        assertTrue(x.equals(y));
    }

    /**
     * Test the binary reader/writer.
     */
    @Test
    public void testBinary() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));

        // save and load as binary
        PropertyListParser.saveAsBinary(x, new File("test-files/out-testBinary.plist"));
        NSObject y = PropertyListParser.parse(new File("test-files/out-testBinary.plist"));
        assertTrue(x.equals(y));
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
        assertTrue(d.count() == 5);
        assertTrue(d.objectForKey("keyA").toString().equals("valueA"));
        assertTrue(d.objectForKey("key&B").toString().equals("value&B"));
        assertTrue(((NSDate) d.objectForKey("date")).getDate().equals(new Date(1322472090000L)));
        assertTrue(Arrays.equals(((NSData) d.objectForKey("data")).bytes(),
                new byte[] {0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82}));
        NSArray a = (NSArray) d.objectForKey("array");
        assertTrue(a.count() == 4);
        assertTrue(a.objectAtIndex(0).equals(new NSString("YES")));
        assertTrue(a.objectAtIndex(1).equals(new NSString("NO")));
        assertTrue(a.objectAtIndex(2).equals(new NSString("87")));
        assertTrue(a.objectAtIndex(3).equals(new NSString("3.14159")));
    }

    @Test
    public void testGnuStepASCII() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test1-ascii-gnustep.plist"));
        NSDictionary d = (NSDictionary) x;
        assertTrue(d.count() == 5);
        assertTrue(d.objectForKey("keyA").toString().equals("valueA"));
        assertTrue(d.objectForKey("key&B").toString().equals("value&B"));
        assertTrue(((NSDate) d.objectForKey("date")).getDate().equals(new Date(1322472090000L)));
        assertTrue(Arrays.equals(((NSData) d.objectForKey("data")).bytes(),
                new byte[] {0x00, 0x00, 0x00, 0x04, 0x10, 0x41, 0x08, 0x20, (byte) 0x82}));
        NSArray a = (NSArray) d.objectForKey("array");
        assertTrue(a.count() == 4);
        assertTrue(a.objectAtIndex(0).equals(new NSNumber(true)));
        assertTrue(a.objectAtIndex(1).equals(new NSNumber(false)));
        assertTrue(a.objectAtIndex(2).equals(new NSNumber(87)));
        assertTrue(a.objectAtIndex(3).equals(new NSNumber(3.14159)));
    }

    @Test
    public void testASCIIWriting() throws Exception {
        File in = new File("test-files/test1.plist");
        File out = new File("test-files/out-test1-ascii.plist");
        File in2 = new File("test-files/test1-ascii.plist");
        NSDictionary x = (NSDictionary) PropertyListParser.parse(in);
        PropertyListParser.saveAsASCII(x, out);

        //Information gets lost when saving into the ASCII format (NSNumbers are converted to NSStrings)

        NSDictionary y = (NSDictionary) PropertyListParser.parse(out);
        NSDictionary z = (NSDictionary) PropertyListParser.parse(in2);
        assertTrue(y.equals(z));
    }

    @Test
    public void testGnuStepASCIIWriting() throws Exception {
        File in = new File("test-files/test1.plist");
        File out = new File("test-files/out-test1-ascii-gnustep.plist");
        NSDictionary x = (NSDictionary) PropertyListParser.parse(in);
        PropertyListParser.saveAsGnuStepASCII(x, out);
        NSObject y = PropertyListParser.parse(out);
        assertTrue(x.equals(y));
    }

    @Test
    public void testAsciiNullCharactersInString() throws Exception {
        assertThrows(ParseException.class, () -> PropertyListParser.parse(new File("test-files/test2-ascii-null-char-in-string.plist")));
    }

    public void testAsciiPropertyListEncodedWithUtf8() throws Exception {
        testAsciiUnicode("test-ascii-utf8.plist");
    }

    public void testAsciiPropertyListEncodedWithUtf16Be() throws Exception {
        testAsciiUnicode("test-ascii-utf16-be.plist");
    }

    public void testAsciiPropertyListEncodedWithUtf16Le() throws Exception {
        testAsciiUnicode("test-ascii-utf16-le.plist");
    }

    public void testAsciiPropertyListEncodedWithUtf32Be() throws Exception {
        testAsciiUnicode("test-ascii-utf32-be.plist");
    }

    public void testAsciiPropertyListEncodedWithUtf32Le() throws Exception {
        testAsciiUnicode("test-ascii-utf32-le.plist");
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

    public void testAsciiCommentsAreNotIncludedInStrings() throws Exception {
        String stringFileContentStr = "/* Menu item to make the current document plain text */\n" +
                "\"Make Plain Text\" = \"In reinen Text umwandeln\";\n" +
                "/* Menu item to make the current document rich text */\n" +
                "\"Make Rich Text\" = \"In formatierten Text umwandeln\";\n";
        byte[] stringFileContentRaw = stringFileContentStr.getBytes();

        String stringFileContent = new String(stringFileContentRaw, Charset.forName("UTF-8"));
        String asciiPropertyList = "{" + stringFileContent + "}";
        NSDictionary dict = (NSDictionary)ASCIIPropertyListParser.parse(asciiPropertyList.getBytes(Charset.forName("UTF-8")));
        assertTrue(dict.containsKey("Make Plain Text"));
        assertEquals("In reinen Text umwandeln", dict.get("Make Plain Text").toString());
    }

    public void testAsciiEscapeCharacters() throws Exception {
        String asciiPropertyList = "{\n" +
                "a = \"abc \\n def\";\n" +
                "b = \"\\r\";\n" +
                "c = \"xyz\\b\";\n" +
                "d = \"\\tasdf\";\n" +
                "e = \"\\\\ \\\"\";\n" +
                "f = \"a \\' b\";\n" +
                "}";
        NSDictionary dict = (NSDictionary)ASCIIPropertyListParser.parse(asciiPropertyList.getBytes(Charset.forName("UTF-8")));
        assertEquals("abc \n def", dict.get("a").toString());
        assertEquals("\r", dict.get("b").toString());
        assertEquals("xyz\b", dict.get("c").toString());
        assertEquals("\tasdf", dict.get("d").toString());
        assertEquals("\\ \"", dict.get("e").toString());
        assertEquals("a ' b", dict.get("f").toString());
    }
}
