package com.dd.plist.test;

import com.dd.plist.*;
import junit.framework.TestCase;

import javax.swing.*;
import java.io.File;
import java.util.*;

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
        assertTrue(((NSString)d.objectForKey("keyA")).toString().equals("valueA"));
        assertTrue(((NSString)d.objectForKey("key&B")).toString().equals("value&B"));
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
        assertTrue(((NSString)d.objectForKey("keyA")).toString().equals("valueA"));
        assertTrue(((NSString)d.objectForKey("key&B")).toString().equals("value&B"));
        assertTrue(((NSDate)d.objectForKey("date")).getDate().equals(new Date(1322472090000L)));
        assertTrue(Arrays.equals(((NSData)d.objectForKey("data")).bytes(),
                                 new byte[]{0x00,0x00,0x00,0x04,0x10,0x41,0x08,0x20,(byte)0x82}));
        NSArray a = (NSArray)d.objectForKey("array");
        assertTrue(a.count() == 4);
        assertTrue(a.objectAtIndex(0).equals(new NSNumber(true)));
        assertTrue(a.objectAtIndex(1).equals(new NSNumber(false)));
        assertTrue(a.objectAtIndex(2).equals(new NSNumber(87)));
        assertTrue(a.objectAtIndex(3).equals(new NSNumber(3.14159)));
        NSObject y = PropertyListParser.parse(new File("test-files/test1-ascii-gnustep.plist"));
        assertTrue(x.equals(y));
    }

    public static void testASCIIWriting() throws Exception {
    	File in = new File("test-files/test1.plist");
    	File out = new File("test-files/out-test1-ascii.plist");
    	NSDictionary x = (NSDictionary)PropertyListParser.parse(in);
    	PropertyListParser.saveAsASCII(x, out);
    	NSDictionary y = (NSDictionary)PropertyListParser.parse(out);
    	assertTrue(x.equals(y));
    }

    public static void testGnuStepASCIIWriting() throws Exception {
    	File in = new File("test-files/test1.plist");
    	File out = new File("test-files/out-test1-ascii-gnustep.plist");
    	NSDictionary x = (NSDictionary)PropertyListParser.parse(in);
    	PropertyListParser.saveAsGnuStepASCII(x, out);
    	NSObject y = PropertyListParser.parse(out);
    	assertTrue(x.equals(y));
    }

    public static void testWrap() throws Exception {
        boolean bool = true;
        byte byt = 24;
        short shrt = 12;
        int i = 42;
        long lng = 30000000000l;
        float flt = 124.3f;
        double dbl = 32.0;
        Date date = new java.util.Date();
        String string = "Hello World";
        byte[] bytes = new byte[] {(byte)0x00, (byte)0xAF, (byte)0xAF};
        JFrame frame = new JFrame();
        Object[] array = new Object[] {bool, byt, shrt, i, lng, flt, dbl, date, string, bytes};
        LinkedList<Object> list = new LinkedList<Object>();
        for(Object o:array)
            list.add(o);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("int", i);
        map.put("long", lng);
        map.put("date", date);

        NSObject wrappedO = NSObject.wrap((Object)bool);
        assertTrue(wrappedO.getClass().equals(NSNumber.class));
        assertTrue(wrappedO.toJavaObject().equals(bool));

        wrappedO = NSObject.wrap((Object)byt);
        assertTrue(wrappedO.getClass().equals(NSNumber.class));
        assertTrue((Integer)wrappedO.toJavaObject() == byt);

        wrappedO = NSObject.wrap((Object)shrt);
        assertTrue(wrappedO.getClass().equals(NSNumber.class));
        assertTrue((Integer)wrappedO.toJavaObject() == shrt);

        wrappedO = NSObject.wrap((Object)i);
        assertTrue(wrappedO.getClass().equals(NSNumber.class));
        assertTrue((Integer)wrappedO.toJavaObject() == i);

        wrappedO = NSObject.wrap((Object)lng);
        assertTrue(wrappedO.getClass().equals(NSNumber.class));
        assertTrue((Long)wrappedO.toJavaObject() == lng);

        wrappedO = NSObject.wrap((Object)flt);
        assertTrue(wrappedO.getClass().equals(NSNumber.class));
        assertTrue((Double)wrappedO.toJavaObject() == flt);

        wrappedO = NSObject.wrap((Object)dbl);
        assertTrue(wrappedO.getClass().equals(NSNumber.class));
        assertTrue((Double)wrappedO.toJavaObject() == dbl);

        wrappedO = NSObject.wrap((Object)date);
        assertTrue(wrappedO.getClass().equals(NSDate.class));
        assertTrue(((Date)wrappedO.toJavaObject()).equals(date));

        wrappedO = NSObject.wrap((Object)string);
        assertTrue(wrappedO.getClass().equals(NSString.class));
        assertTrue(((String)wrappedO.toJavaObject()).equals(string));

        wrappedO = NSObject.wrap((Object)bytes);
        assertTrue(wrappedO.getClass().equals(NSData.class));
        byte[] data = (byte[])wrappedO.toJavaObject();
        assertTrue(data.length == bytes.length);
        for(int x=0; x<bytes.length;x++)
            assertTrue(data[x] == bytes[x]);

        wrappedO = NSObject.wrap((Object)array);
        assertTrue(wrappedO.getClass().equals(NSArray.class));
        Object[] objArray = (Object[])wrappedO.toJavaObject();
        assertTrue(objArray.length == array.length);

        wrappedO = NSObject.wrap((Object)list);
        assertTrue(wrappedO.getClass().equals(NSArray.class));
        objArray = (Object[])wrappedO.toJavaObject();
        assertTrue(objArray.length == array.length);

        assertTrue(NSObject.wrap((Object)frame).getClass().equals(NSData.class));

        wrappedO = NSObject.wrap((Object)map);
        assertTrue(wrappedO.getClass().equals(NSDictionary.class));
        NSDictionary dict = (NSDictionary)wrappedO;
        assertTrue(((NSNumber)dict.objectForKey("int")).longValue() == i);
        assertTrue(((NSNumber)dict.objectForKey("long")).longValue() == lng);
        assertTrue(((NSDate)dict.objectForKey("date")).getDate().equals(date));
        Object unwrappedO = wrappedO.toJavaObject();
        Map map2 = (Map)unwrappedO;
        assertTrue(((Integer)map.get("int")) == i);
        assertTrue(((Long)map.get("long")) == lng);
        assertTrue(((Date)map.get("date")).equals(date));
    }
}
