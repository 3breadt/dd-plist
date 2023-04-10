package com.dd.plist.test;

import com.dd.plist.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXParseException;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Objects;
import java.util.stream.Stream;
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
        InputStream fileInputStream = Files.newInputStream(plistFile.toPath());

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
    public void testIssue42_OutOfMemoryErrorWhenBinaryPropertyListTrailerIsCorrupt() {
        File plistFile = new File("test-files/github-issue42.plist");
        assertThrows(PropertyListFormatException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue49_NSNumberToFloat() {
        NSNumber number = new NSNumber(1);
        assertEquals(1.0, number.toJavaObject(Float.class), 0.0);
    }

    @Test
    public void testIssue51_BillionLaughsAttack() {
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

    @Test
    public void testIssue67_EmptyAsciiPlist() {
        String plist = "/* Localized versions of Info.plist keys */";
        InputStream is = new ByteArrayInputStream(plist.getBytes());

        assertThrows(ParseException.class, () -> ASCIIPropertyListParser.parse(is));
    }

    @Test
    public void testIssue72_OutOfMemory_BinaryPlist() {
        File plistFile = new File("test-files/github-issue72.plist");
        assertDoesNotThrow(() -> BinaryPropertyListParser.parse(plistFile));
    }
    @Test
    public void testIssue73_AsciiPropertyListWithNonHexadecimalData() {
        File plistFile = new File("test-files/github-issue73-1.plist");
        assertThrows(ParseException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue73_BinaryPropertyListHeaderWithTwoByteUnicodeChar() {
        File plistFile = new File("test-files/github-issue73-2.plist");
        assertThrows(PropertyListFormatException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue73_BinaryPropertyListDataTooShort() {
        File plistFile = new File("test-files/github-issue73-3.plist");
        assertThrows(PropertyListFormatException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue73_FileTooShort() {
        File plistFile = new File("test-files/github-issue73-4.plist");
        assertThrows(PropertyListFormatException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue73_AsciiPropertyListWithNullBytesInComment() {
        File plistFile = new File("test-files/github-issue73-5.plist");
        assertThrows(ParseException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue73_InvalidBinaryPropertyListHeader() {
        File plistFile = new File("test-files/github-issue73-6.plist");
        assertThrows(PropertyListFormatException.class, () -> PropertyListParser.parse(plistFile));
    }

    @ParameterizedTest
    @MethodSource("provideIssue74ErrorFiles")
    public void testIssue74_UnsupportedOperationException(File file) {
        assertThrows(PropertyListFormatException.class, (() -> PropertyListParser.parse(file)));
    }

    @ParameterizedTest
    @MethodSource("provideIssue75ErrorFiles")
    public void testIssue75_CyclicReferencesInBinaryPropertyLists(File file) {
        assertThrows(PropertyListFormatException.class, (() -> PropertyListParser.parse(file)));
    }

    @Test
    public void testIssue76_UnexpectedIllegalArgumentExceptionForInvalidNumberInAsciiPropertyList() {
        File plistFile = new File("test-files/github-issue76.plist");
        assertThrows(ParseException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue76_UnexpectedIllegalArgumentExceptionForInvalidNumberInXmlPropertyList() {
        File plistFile = new File("test-files/github-issue76-xml.plist");
        assertThrows(ParseException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue78_NullReferenceExceptionForInvalidNSDictionaryKey() {
        File plistFile = new File("test-files/github-issue78.plist");
        assertThrows(PropertyListFormatException.class, () -> PropertyListParser.parse(plistFile));
    }

    @Test
    public void testIssue80_ClassCastExceptionForUidAddedToSet() {
        NSSet set = new NSSet(true);
        assertDoesNotThrow(() -> set.addObject(new UID(null, BigInteger.valueOf(42))));
    }

    @ParameterizedTest
    @MethodSource("provideIssue82ErrorFiles")
    public void testIssue82_IndexOutOfBoundsExceptions(File file) {
        assertThrows(PropertyListFormatException.class, (() -> PropertyListParser.parse(file)));
    }

    private static Stream<Arguments> provideIssue75ErrorFiles() {
        return Stream.of(Objects.requireNonNull(new File("test-files/github-issue75/").listFiles()))
                .filter(Objects::nonNull)
                .map(Arguments::of);
    }

    private static Stream<Arguments> provideIssue74ErrorFiles() {
        return Stream.of(Objects.requireNonNull(new File("test-files/github-issue74/").listFiles()))
                .filter(Objects::nonNull)
                .map(Arguments::of);
    }

    private static Stream<Arguments> provideIssue82ErrorFiles() {
        return Stream.of(Objects.requireNonNull(new File("test-files/github-issue82/").listFiles()))
                .filter(Objects::nonNull)
                .map(Arguments::of);
    }
}
