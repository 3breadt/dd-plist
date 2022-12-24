package com.dd.plist.test;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
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
        assertThrows(IllegalStateException.class, nan::intValue);
    }

    @Test
    public void testNanLongValue() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertThrows(IllegalStateException.class, nan::longValue);
    }

    @Test
    public void testNanFloatValue() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertTrue(Float.isNaN(nan.floatValue()));
    }

    @Test
    public void testNanStringValue() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertEquals("nan", nan.stringValue());
    }

    @Test
    public void testNanToXml() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertThat(nan.toXMLPropertyList(), containsString("<real>nan</real>"));
    }

    @Test
    public void testNanToString() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertEquals("nan", nan.toString());
    }

    @Test
    public void testNanToBinaryAndBack() throws Exception {
        NSDictionary dict = new NSDictionary();
        dict.put("NaN", new NSNumber(Double.NaN));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            BinaryPropertyListWriter.write(dict, out);
        }
        finally {
            out.close();
        }

        NSDictionary parsedDict = (NSDictionary)BinaryPropertyListParser.parse(out.toByteArray());
        NSNumber parsedNan = (NSNumber)parsedDict.get("NaN");
        assertTrue(Double.isNaN(parsedNan.doubleValue()));
    }

    @Test
    public void testInitializeFromPositiveInfinityString() {
        NSNumber inf = new NSNumber("+infinity");
        assertEquals(Double.POSITIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void testInitializeFromDoublePositiveInfinity() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void testInitializeFromFloatPositiveInfinity() {
        NSNumber inf = new NSNumber(Float.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void testPositiveInfinityIntValue() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertThrows(IllegalStateException.class, inf::intValue);
    }

    @Test
    public void testPositiveInfinityLongValue() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertThrows(IllegalStateException.class, inf::longValue);
    }

    @Test
    public void testPositiveInfinityFloatValue() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertEquals(Float.POSITIVE_INFINITY, inf.floatValue());
    }

    @Test
    public void testPositiveInfinityStringValue() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertEquals("+infinity", inf.stringValue());
    }

    @Test
    public void testPositiveInfinityToXml() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertThat(inf.toXMLPropertyList(), containsString("<real>+infinity</real>"));
    }

    @Test
    public void testPositiveInfinityToString() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertEquals("+infinity", inf.toString());
    }

    @Test
    public void testPositiveInfinityToBinaryAndBack() throws Exception {
        NSDictionary dict = new NSDictionary();
        dict.put("inf", new NSNumber(Double.POSITIVE_INFINITY));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            BinaryPropertyListWriter.write(dict, out);
        }
        finally {
            out.close();
        }

        NSDictionary parsedDict = (NSDictionary)BinaryPropertyListParser.parse(out.toByteArray());
        NSNumber parsedPositiveInfinity = (NSNumber)parsedDict.get("inf");
        assertEquals(Double.POSITIVE_INFINITY, parsedPositiveInfinity.doubleValue());
    }

    @Test
    public void testInitializeFromNegativeInfinityString() {
        NSNumber inf = new NSNumber("-infinity");
        assertEquals(Double.NEGATIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void testInitializeFromDoubleNegativeInfinity() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertEquals(Double.NEGATIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void testInitializeFromFloatNegativeInfinity() {
        NSNumber inf = new NSNumber(Float.NEGATIVE_INFINITY);
        assertEquals(Double.NEGATIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void testNegativeInfinityIntValue() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertThrows(IllegalStateException.class, inf::intValue);
    }

    @Test
    public void testNegativeInfinityLongValue() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertThrows(IllegalStateException.class, inf::longValue);
    }

    @Test
    public void testNegativeInfinityFloatValue() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertEquals(Float.NEGATIVE_INFINITY, inf.floatValue());
    }

    @Test
    public void testNegativeInfinityStringValue() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertEquals("-infinity", inf.stringValue());
    }

    @Test
    public void testNegativeInfinityToXml() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertThat(inf.toXMLPropertyList(), containsString("<real>-infinity</real>"));
    }

    @Test
    public void testNegativeInfinityToString() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertEquals("-infinity", inf.toString());
    }

    @Test
    public void testNegativeInfinityToBinaryAndBack() throws Exception {
        NSDictionary dict = new NSDictionary();
        dict.put("inf", new NSNumber(Double.NEGATIVE_INFINITY));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            BinaryPropertyListWriter.write(dict, out);
        }
        finally {
            out.close();
        }

        NSDictionary parsedDict = (NSDictionary)BinaryPropertyListParser.parse(out.toByteArray());
        NSNumber parsedNegativeInfinity = (NSNumber)parsedDict.get("inf");
        assertEquals(Double.NEGATIVE_INFINITY, parsedNegativeInfinity.doubleValue());
    }
}
