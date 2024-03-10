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
 * Tests for the {@link NSNumber} class.
 * @author Daniel Dreibrodt
 */
public class NSNumberTest {
    @Test
    public void init_canHandleNaNString() {
        NSNumber nan = new NSNumber("nan");
        assertTrue(Double.isNaN(nan.doubleValue()));
    }

    @Test
    public void init_canHandleNaNDouble() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertTrue(Double.isNaN(nan.doubleValue()));
    }

    @Test
    public void init_canHandleNaNFloat() {
        NSNumber nan = new NSNumber(Float.NaN);
        assertTrue(Double.isNaN(nan.doubleValue()));
    }

    @Test
    public void intValue_throwsIllegalStateExceptionForNaN() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertThrows(IllegalStateException.class, nan::intValue);
    }

    @Test
    public void longValue_throwsIllegalStateExceptionForNaN() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertThrows(IllegalStateException.class, nan::longValue);
    }

    @Test
    public void floatValue_canHandleNaN() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertTrue(Float.isNaN(nan.floatValue()));
    }

    @Test
    public void stringValue_canHandleNaN() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertEquals("nan", nan.stringValue());
    }

    @Test
    public void toXML_canHandleNaN() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertThat(nan.toXMLPropertyList(), containsString("<real>nan</real>"));
    }

    @Test
    public void toString_canHandleNaN() {
        NSNumber nan = new NSNumber(Double.NaN);
        assertEquals("nan", nan.toString());
    }

    @Test
    public void toBinary_canHandleNaN() throws Exception {
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
    public void init_canHandlePositiveInfinityString() {
        NSNumber inf = new NSNumber("+infinity");
        assertEquals(Double.POSITIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void init_canHandlePositiveInfinityDouble() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void init_canHandlePositiveInfinityFloat() {
        NSNumber inf = new NSNumber(Float.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void intValue_throwsIllegalStateExceptionForPositiveInfinity() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertThrows(IllegalStateException.class, inf::intValue);
    }

    @Test
    public void longValue_throwsIllegalStateExceptionForPositiveInfinity() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertThrows(IllegalStateException.class, inf::longValue);
    }

    @Test
    public void floatValue_canHandlePositiveInfinity() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertEquals(Float.POSITIVE_INFINITY, inf.floatValue());
    }

    @Test
    public void stringValue_canHandlePositiveInfinity() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertEquals("+infinity", inf.stringValue());
    }

    @Test
    public void toXml_canHandlePositiveInfinity() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertThat(inf.toXMLPropertyList(), containsString("<real>+infinity</real>"));
    }

    @Test
    public void toString_canHandlePositiveInfinity() {
        NSNumber inf = new NSNumber(Double.POSITIVE_INFINITY);
        assertEquals("+infinity", inf.toString());
    }

    @Test
    public void toBinary_canHandlePositiveInfinity() throws Exception {
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
    public void init_canHandleNegativeInfinityString() {
        NSNumber inf = new NSNumber("-infinity");
        assertEquals(Double.NEGATIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void init_canHandleNegativeInfinityDouble() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertEquals(Double.NEGATIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void init_canHandleNegativeInfinityFloat() {
        NSNumber inf = new NSNumber(Float.NEGATIVE_INFINITY);
        assertEquals(Double.NEGATIVE_INFINITY, inf.doubleValue());
    }

    @Test
    public void intValue_throwsIllegalStateExceptionForNegativeInfinity() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertThrows(IllegalStateException.class, inf::intValue);
    }

    @Test
    public void longValue_throwsIllegalStateExceptionForNegativeInfinity() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertThrows(IllegalStateException.class, inf::longValue);
    }

    @Test
    public void floatValue_canHandleNegativeInfinity() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertEquals(Float.NEGATIVE_INFINITY, inf.floatValue());
    }

    @Test
    public void stringValue_canHandleNegativeInfinity() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertEquals("-infinity", inf.stringValue());
    }

    @Test
    public void toXml_canHandleNegativeInfinity() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertThat(inf.toXMLPropertyList(), containsString("<real>-infinity</real>"));
    }

    @Test
    public void toString_canHandleNegativeInfinity() {
        NSNumber inf = new NSNumber(Double.NEGATIVE_INFINITY);
        assertEquals("-infinity", inf.toString());
    }

    @Test
    public void toBinary_canHandleNegativeInfinity() throws Exception {
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
