package com.dd.plist.test;

import com.dd.plist.NSString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the NSString class.
 * @author Daniel Dreibrodt
 */
public class NSStringTest {
    @Test
    public void testIntValuePositive() {
        NSString s = new NSString("42");
        assertEquals(42, s.intValue());
    }

    @Test
    public void testIntValueNegative() {
        NSString s = new NSString("-42");
        assertEquals(-42, s.intValue());
    }

    @Test
    public void testIntValueFloat() {
        NSString s = new NSString("42.87654");
        assertEquals(42, s.intValue());
    }

    @Test
    public void testIntValueSkipsWhitespace() {
        NSString s = new NSString("  42");
        assertEquals(42, s.intValue());
    }

    @Test
    public void testIntValueIgnoresSubsequentCharacters() {
        NSString s = new NSString("  42 is the meaning of life. 12345678.");
        assertEquals(42, s.intValue());

        NSString s2 = new NSString("1234five");
        assertEquals(1234, s2.intValue());
    }

    @Test
    public void testIntValueMaxValue() {
        NSString s = new NSString("2147483647");
        assertEquals(Integer.MAX_VALUE, s.intValue());
    }

    @Test
    public void testIntValueMoreThanMaxValue() {
        NSString s = new NSString("2147483648");
        assertEquals(Integer.MAX_VALUE, s.intValue());
    }

    @Test
    public void testIntValueMinValue() {
        NSString s = new NSString("-2147483648");
        assertEquals(Integer.MIN_VALUE, s.intValue());
    }

    @Test
    public void testIntValueLessThanMinValue() {
        NSString s = new NSString("-2147483649");
        assertEquals(Integer.MIN_VALUE, s.intValue());
    }

    @Test
    public void testFloatValuePositive() {
        NSString s = new NSString("42");
        assertEquals(42, s.floatValue(), 0);
    }

    @Test
    public void testFloatValueNegative() {
        NSString s = new NSString("-42");
        assertEquals(-42, s.floatValue(), 0);
    }

    @Test
    public void testFloatValueFloat() {
        NSString s = new NSString("42.87654");
        assertEquals(42.87654, s.floatValue(), 1E-5);
    }

    @Test
    public void testFloatValueSkipsWhitespace() {
        NSString s = new NSString("  42.1234");
        assertEquals(42.1234, s.floatValue(), 1E-5);
    }

    @Test
    public void testFloatValueIgnoresSubsequentCharacters() {
        NSString s = new NSString("  42.5 is the meaning of life. 12345678.");
        assertEquals(42.5, s.floatValue(), 0);

        NSString s2 = new NSString("1234five");
        assertEquals(1234, s2.floatValue(), 0);
    }

    @Test
    public void testFloatValueMaxValue() {
        NSString s = new NSString("340282350000000000000000000000000000000");
        assertEquals(Float.MAX_VALUE, s.floatValue(), 0);
    }

    @Test
    public void testFloatValueMoreThanMaxValue() {
        NSString s = new NSString("340282350000000000000000000000000000000.1");
        assertEquals(Float.MAX_VALUE, s.floatValue(), 0);
    }

    @Test
    public void testFloatValueMinValue() {
        NSString s = new NSString("-340282350000000000000000000000000000000");
        assertEquals(-Float.MAX_VALUE, s.floatValue(), 0);
    }

    @Test
    public void testFloatValueLessThanMinValue() {
        NSString s = new NSString("-340282350000000000000000000000000000000.1");
        assertEquals(-Float.MAX_VALUE, s.floatValue(), 0);
    }

    @Test
    public void testDoubleValuePositive() {
        NSString s = new NSString("42");
        assertEquals(42, s.doubleValue(), 0);
    }

    @Test
    public void testDoubleValueNegative() {
        NSString s = new NSString("-42");
        assertEquals(-42, s.doubleValue(), 0);
    }

    @Test
    public void testDoubleValueFloat() {
        NSString s = new NSString("42.87654");
        assertEquals(42.87654, s.doubleValue(), 1E-5);
    }

    @Test
    public void testDoubleValueSkipsWhitespace() {
        NSString s = new NSString("  42.1234");
        assertEquals(42.1234, s.doubleValue(), 1E-5);
    }

    @Test
    public void testDoubleValueIgnoresSubsequentCharacters() {
        NSString s = new NSString("  42.5 is the meaning of life. 12345678.");
        assertEquals(42.5, s.doubleValue(), 0);

        NSString s2 = new NSString("123.4five678");
        assertEquals(123.4, s2.doubleValue(), 0);
    }

    @Test
    public void testBoolValueRegularCases() {
        assertTrue(new NSString("YES").boolValue());
        assertTrue(new NSString("yes").boolValue());
        assertTrue(new NSString("TRUE").boolValue());
        assertTrue(new NSString("true").boolValue());
        assertTrue(new NSString("1").boolValue());

        assertFalse(new NSString("NO").boolValue());
        assertFalse(new NSString("no").boolValue());
        assertFalse(new NSString("FALSE").boolValue());
        assertFalse(new NSString("false").boolValue());
        assertFalse(new NSString("0").boolValue());
    }

    @Test
    public void testBoolValueLeadingWhitespace() {
        assertTrue(new NSString(" YES").boolValue());
        assertTrue(new NSString(" yes").boolValue());
        assertTrue(new NSString(" TRUE").boolValue());
        assertTrue(new NSString(" true").boolValue());
        assertTrue(new NSString(" 1").boolValue());

        assertFalse(new NSString(" NO").boolValue());
        assertFalse(new NSString(" no").boolValue());
        assertFalse(new NSString(" FALSE").boolValue());
        assertFalse(new NSString(" false").boolValue());
        assertFalse(new NSString(" 0").boolValue());
    }

    @Test
    public void testBoolValueLeadingZeroes() {
        assertTrue(new NSString("0YES").boolValue());
        assertTrue(new NSString("0yes").boolValue());
        assertTrue(new NSString("0TRUE").boolValue());
        assertTrue(new NSString("0true").boolValue());
        assertTrue(new NSString("01").boolValue());

        assertFalse(new NSString("0NO").boolValue());
        assertFalse(new NSString("0no").boolValue());
        assertFalse(new NSString("0FALSE").boolValue());
        assertFalse(new NSString("0false").boolValue());
        assertFalse(new NSString("00").boolValue());
    }

    @Test
    public void testBoolValueSign() {
        assertTrue(new NSString("+YES").boolValue());
        assertTrue(new NSString("-yes").boolValue());
        assertTrue(new NSString("-TRUE").boolValue());
        assertTrue(new NSString("+true").boolValue());
        assertTrue(new NSString("+1").boolValue());

        assertFalse(new NSString("-NO").boolValue());
        assertFalse(new NSString("+no").boolValue());
        assertFalse(new NSString("+FALSE").boolValue());
        assertFalse(new NSString("+false").boolValue());
        assertFalse(new NSString("-0").boolValue());
    }

    @Test
    public void testBoolValueIntegers() {
        assertTrue(new NSString("002").boolValue());
        assertTrue(new NSString("+03").boolValue());
        assertTrue(new NSString("  04").boolValue());
        assertTrue(new NSString("5").boolValue());
        assertTrue(new NSString("   +00000000006").boolValue());
        assertTrue(new NSString("7NO").boolValue());
        assertTrue(new NSString("-80000").boolValue());
        assertTrue(new NSString("9FALSE").boolValue());
    }

    @Test
    public void testToXmlWithInvalidChars() {
        String xml = new NSString("\2Hello\0World\3\r\n\tHow are you?\uFFFF\uFFFEI am a \ud83d\udc3b.").toXMLPropertyList();
        assertEquals("HelloWorld\r\n\tHow are you?I am a \ud83d\udc3b.", getStringFromXml(xml));
    }

    private static String getStringFromXml(String xml) {
        int index = xml.indexOf("<string>");
        int endIndex = xml.indexOf("</string>", index);
        return xml.substring(index + 8, endIndex);
    }
}
