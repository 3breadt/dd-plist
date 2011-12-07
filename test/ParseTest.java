import com.dd.plist.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import junit.framework.TestCase;

public class ParseTest extends TestCase {

    /**
     * Test the xml reader/writer
     */
    public static void testXml() throws Exception {
        // parse an example plist file
        NSObject x = PropertyListParser.parse(new File("test/test1.plist"));
        
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
        PropertyListParser.saveAsXML(x, new File("test/temp/testXml.plist"));
        NSObject y = PropertyListParser.parse(new File("test/temp/testXml.plist"));
        assertTrue(x.equals(y));
    }

    /**
     *  Test the binary reader/writer.
     */
    public static void testBinary() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test/test1.plist"));
        
        // save and load as binary
        PropertyListParser.saveAsBinary(x, new File("test/temp/testBinary.plist"));
        NSObject y = PropertyListParser.parse(new File("test/temp/testBinary.plist"));
        assertTrue(x.equals(y));
    }

    /**
     *  NSSet only occurs in binary property lists, so we have to test it separately.
     */
    public static void testSet() throws Exception {
        NSSet s = new NSSet();
        s.addObject(new NSNumber(1));
        s.addObject(new NSNumber(2));
        s.addObject(new NSNumber(3));
        
        PropertyListParser.saveAsBinary(s, new File("test/temp/testSet.plist"));
        NSObject t = PropertyListParser.parse(new File("test/temp/testSet.plist"));
        assertTrue(s.equals(t));
    }
    
    public static void testASCII() throws Exception {
        NSObject x = PropertyListParser.parse(new File("test/test1-ascii.plist"));        
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
        NSObject y = PropertyListParser.parse(new File("test/test1-ascii-gnustep.plist"));
        assertTrue(x.equals(y));
    }
}
