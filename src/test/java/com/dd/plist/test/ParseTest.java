package com.dd.plist.test;

import com.dd.plist.*;

import junit.framework.TestCase;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class ParseTest extends TestCase {

    /**
     * Test the xml reader/writer
     */
    public static void testXml() throws Exception {
        // parse an example plist file
        NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));

        // check the data in it
        NSDictionary d = (NSDictionary)x;
        assertTrue(d.count() == 5);
        assertTrue(d.objectForKey("keyA").toString().equals("valueA"));
        assertTrue(d.objectForKey("key&B").toString().equals("value&B"));
        assertTrue(((NSDate)d.objectForKey("date")).getDate().equals(new Date(1322472090000L)));
        assertTrue(Arrays.equals(((NSData)d.objectForKey("data")).bytes(),
                                 new byte[]{0x00,0x00,0x00,0x04,0x10,0x41,0x08,0x20,(byte)0x82}));
        NSArray a = (NSArray)d.objectForKey("array");
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
     *  Test the binary reader/writer.
     */
    public static void testBinary() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));

        // save and load as binary
        PropertyListParser.saveAsBinary(x, new File("test-files/out-testBinary.plist"));
        NSObject y = PropertyListParser.parse(new File("test-files/out-testBinary.plist"));
        assertTrue(x.equals(y));
    }

    /**
     *  NSSet only occurs in binary property lists, so we have to test it separately.
     *  NSSets are not yet supported in reading/writing, as binary property list format v1+ is required.
     */
    /*public static void testSet() throws Exception {
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

    public static void testASCII() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test1-ascii.plist"));
        NSDictionary d = (NSDictionary)x;
        assertTrue(d.count() == 5);
        assertTrue(d.objectForKey("keyA").toString().equals("valueA"));
        assertTrue(d.objectForKey("key&B").toString().equals("value&B"));
        assertTrue(((NSDate)d.objectForKey("date")).getDate().equals(new Date(1322472090000L)));
        assertTrue(Arrays.equals(((NSData)d.objectForKey("data")).bytes(),
                                 new byte[]{0x00,0x00,0x00,0x04,0x10,0x41,0x08,0x20,(byte)0x82}));
        NSArray a = (NSArray)d.objectForKey("array");
        assertTrue(a.count() == 4);
        assertTrue(a.objectAtIndex(0).equals(new NSString("YES")));
        assertTrue(a.objectAtIndex(1).equals(new NSString("NO")));
        assertTrue(a.objectAtIndex(2).equals(new NSString("87")));
        assertTrue(a.objectAtIndex(3).equals(new NSString("3.14159")));
    }

    public static void testGnuStepASCII() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test1-ascii-gnustep.plist"));
        NSDictionary d = (NSDictionary)x;
        assertTrue(d.count() == 5);
        assertTrue(d.objectForKey("keyA").toString().equals("valueA"));
        assertTrue(d.objectForKey("key&B").toString().equals("value&B"));
        assertTrue(((NSDate)d.objectForKey("date")).getDate().equals(new Date(1322472090000L)));
        assertTrue(Arrays.equals(((NSData)d.objectForKey("data")).bytes(),
                new byte[]{0x00,0x00,0x00,0x04,0x10,0x41,0x08,0x20,(byte)0x82}));
        NSArray a = (NSArray)d.objectForKey("array");
        assertTrue(a.count() == 4);
        assertTrue(a.objectAtIndex(0).equals(new NSNumber(true)));
        assertTrue(a.objectAtIndex(1).equals(new NSNumber(false)));
        assertTrue(a.objectAtIndex(2).equals(new NSNumber(87)));
        assertTrue(a.objectAtIndex(3).equals(new NSNumber(3.14159)));
    }

    public static void testASCIIWriting() throws Exception {
    	File in = new File("test-files/test1.plist");
    	File out = new File("test-files/out-test1-ascii.plist");
        File in2 = new File("test-files/test1-ascii.plist");
    	NSDictionary x = (NSDictionary)PropertyListParser.parse(in);
    	PropertyListParser.saveAsASCII(x, out);

        //Information gets lost when saving into the ASCII format (NSNumbers are converted to NSStrings)

    	NSDictionary y = (NSDictionary)PropertyListParser.parse(out);
        NSDictionary z = (NSDictionary)PropertyListParser.parse(in2);
    	assertTrue(y.equals(z));
    }

    public static void testGnuStepASCIIWriting() throws Exception {
    	File in = new File("test-files/test1.plist");
    	File out = new File("test-files/out-test1-ascii-gnustep.plist");
    	NSDictionary x = (NSDictionary)PropertyListParser.parse(in);
    	PropertyListParser.saveAsGnuStepASCII(x, out);
    	NSObject y = PropertyListParser.parse(out);
    	assertTrue(x.equals(y));
    }

    public static void testAsciiNullCharactersInString() throws Exception {
    	// create a runnable to catch time outs (we don't want the test to run forever)
    	ExecutorService executor = Executors.newCachedThreadPool();
    	Runnable task = new Runnable() {			
			public void run() {
    	        // parse an example plist file
    	    	try
    	    	{
    	    		PropertyListParser.parse(new File("test-files/test2-ascii-null-char-in-string.plist"));
    	    	}
    	    	catch (java.text.ParseException e) {
    			   return;
    	    	}
    	    	catch(Exception e)
    	    	{
        	    	fail("Unexpected exception " + e);
    	    	}

    	    	fail("Expected java.text.ParseException");
			}
		};

		// parsing the 200ko file should take way less than 5s 
		executor.submit(task).get(5, TimeUnit.SECONDS);
    }

    public static void testAsciiUtf8CharactersInQuotedString() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test-files/test-ascii-utf8.plist"));
        NSDictionary d = (NSDictionary)x;
        assertEquals(2, d.count());
        assertEquals("JÔÖú@2x.jpg", d.objectForKey("path").toString());
        assertEquals("QÔÖú@2x 啕.jpg", d.objectForKey("Key QÔÖª@2x 䌡").toString());
    }
}
