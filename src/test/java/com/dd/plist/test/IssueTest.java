package com.dd.plist.test;

import com.dd.plist.*;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for various issues and bugs that have been encountered
 */
public class IssueTest  {
    @Test
    public void testGzipInputStream() throws Exception {
        File plistFile = new File("test-files/test-gzipinputstream-issue.plist");

        //Get the file input stream
        InputStream fileInputStream = new FileInputStream(plistFile);

        //GZIP that file
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

        byte[] buffer = new byte[1024];
        for (int length; (length = fileInputStream.read(buffer)) != -1; ) {
            gzipOutputStream.write(buffer, 0, length);
        }

        fileInputStream.close();
        gzipOutputStream.close();

        //Create an GZIP input stream from the zipped byte array
        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        InputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

        //Parse the property list from that stream
        NSObject zippedObject = PropertyListParser.parse(gzipInputStream);
        NSObject normalObject = PropertyListParser.parse(plistFile);
        assertEquals(zippedObject, normalObject);
    }

    @Test
    public void testIssue31_FalsePositiveForGZipInsideDataElement() throws Exception {
        File plistFile = new File("test-files/github-issue31.plist");

        NSDictionary dict = (NSDictionary)PropertyListParser.parse(plistFile);
        NSDictionary files = (NSDictionary)dict.get("files2");
        NSData hash = (NSData)((NSDictionary)files.get("Base.lproj/Main.storyboardc/MainController.nib")).get("hash");
        assertEquals("1f8b2ef69414fa70ff578a697cfc0919235c8eff", HexConverter.toHex(hash.bytes()));
    }

    @Test
    public void testIssue42_OutOfMemoryErrorWhenBinaryPropertyListTrailerIsCorrupt() throws Exception {
        File plistFile = new File("test-files/github-issue42.plist");
        assertThrows(PropertyListFormatException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue49_NSNumberToFloat() throws Exception {
        NSNumber number = new NSNumber(1);
        assertEquals(1.0, number.toJavaObject(Float.class), 0.0);
    }

    @Test
    public void testIssue51_BillionLaughsAttack() throws Exception {
        String plist = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE lolz [\n" +
                " <!ENTITY lol \"lol\">\n" +
                " <!ELEMENT lolz (#PCDATA)>\n" +
                " <!ENTITY lol1 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">\n" +
                " <!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">\n" +
                " <!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">\n" +
                " <!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">\n" +
                " <!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">\n" +
                " <!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">\n" +
                " <!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">\n" +
                " <!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">\n" +
                " <!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">\n" +
                "]>\n" +
                "<lolz>&lol9;</lolz>";
        InputStream is = new ByteArrayInputStream(plist.getBytes());

        try {
            XMLPropertyListParser.parse(is);
        }
        catch (SAXParseException ex) {
            // Expected exception for older runtimes
        }
        catch (UnsupportedOperationException ex) {
            // Expected exception for newer runtimes, then the parser will complain about the invalid DOCTYPE
        }
        catch (Exception ex) {
            fail("Unexpected exception of type " + ex.getClass().getName() + " was thrown, with the message: " + ex.getMessage());
        }
    }
}
