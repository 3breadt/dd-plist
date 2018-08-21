package com.dd.plist.test;

import com.dd.plist.*;

import junit.framework.TestCase;

import java.io.File;

/**
 * @author Daniel Dreibrodt
 */
public class CloneTest extends TestCase {
    public void testCloneEquals() throws Exception {
        NSDictionary root = (NSDictionary)PropertyListParser.parse(new File("test-files/test1.plist"));
        NSDictionary clonedDictionary = root.clone();

        assertEquals(clonedDictionary, root);
    }

    public void testCloneIsDeep() throws Exception {
        NSDictionary root = (NSDictionary)PropertyListParser.parse(new File("test-files/test1.plist"));
        NSDictionary clonedDictionary = root.clone();

        ((NSString)root.get("keyA")).append("modified");
        assertEquals("valueA", ((NSString)clonedDictionary.get("keyA")).getContent());

        ((NSDate)root.get("date")).getDate().setTime(42);
        assertNotSame(42, ((NSDate)clonedDictionary.get("date")).getDate().getTime());

        ((NSData)root.get("data")).bytes()[0] = 0x42;
        assertEquals(0x00, ((NSData)clonedDictionary.get("data")).bytes()[0]);

        NSArray originalArray = ((NSArray)root.get("array"));
        NSArray clonedArray = ((NSArray)clonedDictionary.get("array"));

        originalArray.getArray()[0] = new NSNumber(false);
        assertEquals(true, ((NSNumber)clonedArray.getArray()[0]).boolValue());
    }
}
