package com.dd.plist.test;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the NSString class.
 *
 * @author Daniel Dreibrodt
 */
public class NSNumberTest {
    @Test
    public void testInitializeFromNanString() {
        NSNumber nan = new NSNumber("nan");
        assertTrue(Double.isNaN(nan.doubleValue()));
    }

    @Test
    public void testInitializeFromDoubleNaN() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertTrue(Double.isNaN(nan.doubleValue()));
    }

    @Test
    public void testInitializeFromFloatNaN() {
        NSNumber nan = new NSNumber(Float.NaN);
        assertTrue(Double.isNaN(nan.doubleValue()));
    }

    @Test
    public void testNanIntValue() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertThrows(IllegalStateException.class, () -> nan.intValue());
    }

    @Test
    public void testNanLongValue() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertThrows(IllegalStateException.class, () -> nan.longValue());
    }

    @Test
    public void testNanFloatValue() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertTrue(Float.isNaN(nan.floatValue()));
    }

    @Test
    public void testNanStringValue() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertEquals("NaN", nan.stringValue());
    }

    @Test
    public void testNanToXml() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertTrue(nan.toXMLPropertyList().contains("<real>nan</real>"));
    }

    @Test
    public void testNanToString() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertEquals("NaN", nan.toString());
    }

    @Test
    public void testNanToBinaryAndBack() throws Exception {
        NSDictionary dict = new NSDictionary();
        dict.put("NaN", new NSNumber(Double.NaN));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            BinaryPropertyListWriter.write(out, dict);
        }
        finally {
            out.close();
        }

        NSDictionary parsedDict = (NSDictionary)BinaryPropertyListParser.parse(out.toByteArray());
        NSNumber parsedNan = (NSNumber)parsedDict.get("NaN");
        assertTrue(Double.isNaN(parsedNan.doubleValue()));
    }
}
