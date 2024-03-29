package com.dd.plist.test;

import com.dd.plist.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains tests for issues that were managed with the Google Code issue tracking system
 */
public class GoogleCodeIssueTest {
    @Test
    public void testIssue4() throws Exception {
        NSDictionary d = (NSDictionary)PropertyListParser.parse(new File("test-files/issue4.plist"));
        assertEquals("Kid\u2019s iPhone", ((NSString) d.objectForKey("Device Name")).toString());
    }

    @Test
    public void testIssue7() throws Exception {
        // also a test for issue 12
        // the issue4 test has a UTF-16-BE string in its binary representation
        NSObject x = PropertyListParser.parse(new File("test-files/issue4.plist"));
        BinaryPropertyListWriter.write(x, new File("test-files/out-testIssue7.plist"));
        NSObject y = PropertyListParser.parse(new File("test-files/out-testIssue7.plist"));
        assertEquals(x, y);
    }

    @Test
    public void testIssue16() throws Exception {
        float x = ((NSNumber)PropertyListParser.parse(new File("test-files/issue16.plist"))).floatValue();
        assertEquals(x, (float) 2.71828);
    }

    @Test
    public void testIssue18() throws Exception {
        NSNumber x = new NSNumber(-999);
        BinaryPropertyListWriter.write(x, new File("test-files/out-testIssue18.plist"));
        NSObject y = PropertyListParser.parse(new File("test-files/out-testIssue18.plist"));
        assertEquals(x, y);
    }

    @Test
    public void testIssue21() throws Exception {
        String x = ((NSString)PropertyListParser.parse(new File("test-files/issue21.plist"))).toString();
        assertEquals("Lot&s of &persand&s and other escapable \"\'<>€ characters", x);
    }

    @Test
    public void testIssue22() throws Exception {
        NSDictionary x1 = ((NSDictionary)PropertyListParser.parse(new File("test-files/issue22-emoji.plist")));
        NSDictionary x2 = ((NSDictionary)PropertyListParser.parse(new File("test-files/issue22-emoji-xml.plist")));
        BinaryPropertyListWriter.write(x1, new File("test-files/out-testIssue22.plist"));
        NSDictionary y1 = ((NSDictionary)PropertyListParser.parse(new File("test-files/out-testIssue22.plist")));
        XMLPropertyListWriter.write(x2, new File("test-files/out-testIssue22-xml.plist"));
        NSDictionary y2 = ((NSDictionary)PropertyListParser.parse(new File("test-files/out-testIssue22-xml.plist")));
        assertEquals(x1, x2);
        assertEquals(x1, y1);
        assertEquals(x1, y2);
        assertEquals(x2, y1);
        assertEquals(x2, y2);

        String emojiString = "Test Test, \uD83D\uDE30\u2754\uD83D\uDC4D\uD83D\uDC4E\uD83D\uDD25";

        assertEquals(emojiString, x1.objectForKey("emojiString").toString());
        assertEquals(emojiString, x2.objectForKey("emojiString").toString());
        assertEquals(emojiString, y1.objectForKey("emojiString").toString());
        assertEquals(emojiString, y2.objectForKey("emojiString").toString());
    }

    @Test
    public void testIssue30() throws Exception {
        NSArray arr = (NSArray)PropertyListParser.parse(new File("test-files/issue30.plist"));
    }

    @Test
    public void testIssue33() throws Exception {
        NSDictionary dict = (NSDictionary)PropertyListParser.parse(new File("test-files/issue33.pbxproj"));
    }

    @Test
    public void testIssue38() throws Exception {
        NSDictionary dict = (NSDictionary)PropertyListParser.parse(new File("test-files/issue33.pbxproj"));
        NSObject fileRef = ((NSDictionary) ((NSDictionary)dict.get("objects")).get("65541A9C16D13B8C00A968D5")).get("fileRef");
        assertEquals(fileRef, new NSString("65541A9B16D13B8C00A968D5"));
    }

    /**
     * Test storing null values
     */
    @Test
    public void testIssue41() {
        //Dictionary
        Map<String, Object> nullMap = new HashMap<>();
        nullMap.put("key", null);
        assertFalse(nullMap.isEmpty());

        NSDictionary nullDict = (NSDictionary)NSObject.fromJavaObject(nullMap);
        assertTrue(nullDict.isEmpty());

        nullDict.put(null, "test");
        assertTrue(nullDict.isEmpty());

        nullDict.put("test", null);
        assertTrue(nullDict.isEmpty());

        try {
            assertTrue(((NSDictionary)PropertyListParser.parse(nullDict.toXMLPropertyList().getBytes())).isEmpty());
        } catch (Exception e) {
            throw new AssertionError("No exception should have occurred while parsing an empty dictionary", e);
        }

        //Array
        String[] strArr = new String[3];
        strArr[0] = "";
        strArr[1] = null;
        strArr[2] = null;
        NSArray nsArr = (NSArray)NSObject.fromJavaObject(strArr);
        assertTrue(nsArr.containsObject(null));
        assertNull(nsArr.objectAtIndex(1));
        assertNull(nsArr.objectAtIndex(2));

        try {
            nsArr.toXMLPropertyList();
            throw new AssertionError("Storing a NSArray containing a null value as a XML property list should throw an exception");
        } catch(NullPointerException ex) {
            //expected exception
        }

        try {
            nsArr.toASCIIPropertyList();
            throw new AssertionError("Storing a NSArray containing a null value as a ASCII property list should throw an exception");
        } catch(NullPointerException ex) {
            //expected exception
        }

        try {
            nsArr.toGnuStepASCIIPropertyList();
            throw new AssertionError("Storing a NSArray containing a null value as a GnuStep ASCII property list should throw an exception");
        } catch(NullPointerException ex) {
            //expected exception
        }

        try {
            byte[] bin = BinaryPropertyListWriter.writeToArray(nsArr);
            throw new AssertionError("Storing a NSArray containing a null value as a binary property list should throw an exception");
        } catch(IOException ex) {
            //expect IOException because binary v1.0 format (which could theoretically store null values) is not supported
            //But v1.0 format is not even supported by OS X 10.10, so there is no plan as of yet to implement it
        }
    }

    @Test
    public void testIssue49() throws Exception {
        NSDictionary dict = (NSDictionary)PropertyListParser.parse(new File("test-files/issue49.plist"));
        assertEquals(0, dict.count());
    }
}
