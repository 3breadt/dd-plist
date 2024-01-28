package com.dd.plist.test;

import com.dd.plist.NSArray;
import com.dd.plist.NSData;
import com.dd.plist.NSDate;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.dd.plist.UID;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author hiran
 */
public class NavigationTest {
    
    /**
     * Ensures we have non-null parent relationships on levels lower than the root
     * @param p the NSObject to test
     * @param level the nesting level
     */
    private static void testParents(NSObject p, int level) {
        if (level > 0 && p.getParent() == null) {
            fail("Parent not set in NSObject " + p + "(" + p.getClass().getName() + ") on level " + level);
        }
        
        if (p instanceof NSDictionary) {
            NSDictionary d = (NSDictionary)p;
            for (Map.Entry<String, NSObject> entry: d.entrySet()) {
                testParents(entry.getValue(), level+1);
            }
        } else if (p instanceof NSArray) {
            NSArray a = (NSArray)p;
            
            for (NSObject o: a.getArray()) {
                testParents(o, level+1);
            }
        } else if (p instanceof NSDate) {
            // this is a leaf. No further check needed
        } else if (p instanceof NSData) {
            // this is a leaf. No further check needed
        } else if (p instanceof NSString) {
            // this is a leaf. No further check needed
        } else if (p instanceof NSNumber) {
            // this is a leaf. No further check needed
        } else {
            fail("Unknown node " + p + "(" + p.getClass().getName() + ") on level " + level);
        }
    }

    @Test
    public void testParents() throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException
    {
        NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));

        testParents(x, 0);
    }

}
