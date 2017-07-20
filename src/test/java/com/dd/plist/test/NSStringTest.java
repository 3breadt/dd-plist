package com.dd.plist.test;

import com.dd.plist.NSString;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Tests for the NSString class.
 * @author Daniel Dreibrodt
 */
public class NSStringTest {
    @Test
    public void intValuePositive() {
        NSString s = new NSString("42");
        assertEquals(42, s.intValue());
    }

    @Test
    public void intValueNegative() {
        NSString s = new NSString("-42");
        assertEquals(-42, s.intValue());
    }

    @Test
    public void intValueFloat() {
        NSString s = new NSString("42.87654");
        assertEquals(42, s.intValue());
    }

    @Test
    public void intValueSkipsWhitespace() {
        NSString s = new NSString("  42");
        assertEquals(42, s.intValue());
    }

    @Test
    public void intValueIgnoresSubsequentCharacters() {
        NSString s = new NSString("  42 is the meaning of life. 12345678.");
        assertEquals(42, s.intValue());

        NSString s2 = new NSString("1234five");
        assertEquals(1234, s2.intValue());
    }

    @Test
    public void intValueMaxValue() {
        NSString s = new NSString("2147483647");
        assertEquals(Integer.MAX_VALUE, s.intValue());
    }

    @Test
    public void intValueMoreThanMaxValue() {
        NSString s = new NSString("2147483648");
        assertEquals(Integer.MAX_VALUE, s.intValue());
    }

    @Test
    public void intValueMinValue() {
        NSString s = new NSString("-2147483648");
        assertEquals(Integer.MIN_VALUE, s.intValue());
    }

    @Test
    public void intValueLessThanMinValue() {
        NSString s = new NSString("-2147483649");
        assertEquals(Integer.MIN_VALUE, s.intValue());
    }

    @Test
    public void floatValuePositive() {
        NSString s = new NSString("42");
        assertEquals(42, s.floatValue(), 0);
    }

    @Test
    public void floatValueNegative() {
        NSString s = new NSString("-42");
        assertEquals(-42, s.floatValue(), 0);
    }

    @Test
    public void floatValueFloat() {
        NSString s = new NSString("42.87654");
        assertEquals(42.87654, s.floatValue(), 1E-5);
    }

    @Test
    public void floatValueSkipsWhitespace() {
        NSString s = new NSString("  42.1234");
        assertEquals(42.1234, s.floatValue(), 1E-5);
    }

    @Test
    public void floatValueIgnoresSubsequentCharacters() {
        NSString s = new NSString("  42.5 is the meaning of life. 12345678.");
        assertEquals(42.5, s.floatValue(), 0);

        NSString s2 = new NSString("1234five");
        assertEquals(1234, s2.floatValue(), 0);
    }

    @Test
    public void floatValueMaxValue() {
        NSString s = new NSString("340282350000000000000000000000000000000");
        assertEquals(Float.MAX_VALUE, s.floatValue(), 0);
    }

    @Test
    public void floatValueMoreThanMaxValue() {
        NSString s = new NSString("340282350000000000000000000000000000000.1");
        assertEquals(Float.MAX_VALUE, s.floatValue(), 0);
    }

    @Test
    public void floatValueMinValue() {
        NSString s = new NSString("-340282350000000000000000000000000000000");
        assertEquals(-Float.MAX_VALUE, s.floatValue(), 0);
    }

    @Test
    public void floatValueLessThanMinValue() {
        NSString s = new NSString("-340282350000000000000000000000000000000.1");
        assertEquals(-Float.MAX_VALUE, s.floatValue(), 0);
    }

    @Test
    public void doubleValuePositive() {
        NSString s = new NSString("42");
        assertEquals(42, s.doubleValue(), 0);
    }

    @Test
    public void doubleValueNegative() {
        NSString s = new NSString("-42");
        assertEquals(-42, s.doubleValue(), 0);
    }

    @Test
    public void doubleValueFloat() {
        NSString s = new NSString("42.87654");
        assertEquals(42.87654, s.doubleValue(), 1E-5);
    }

    @Test
    public void doubleValueSkipsWhitespace() {
        NSString s = new NSString("  42.1234");
        assertEquals(42.1234, s.doubleValue(), 1E-5);
    }

    @Test
    public void doubleValueIgnoresSubsequentCharacters() {
        NSString s = new NSString("  42.5 is the meaning of life. 12345678.");
        assertEquals(42.5, s.doubleValue(), 0);

        NSString s2 = new NSString("123.4five678");
        assertEquals(123.4, s2.doubleValue(), 0);
    }

    @Test
    public void boolValueRegularCases() {
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
    public void boolValueLeadingWhitespace() {
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
    public void boolValueLeadingZeroes() {
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
    public void boolValueSign() {
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
    public void boolValueIntegers() {
        assertTrue(new NSString("002").boolValue());
        assertTrue(new NSString("+03").boolValue());
        assertTrue(new NSString("  04").boolValue());
        assertTrue(new NSString("5").boolValue());
        assertTrue(new NSString("   +00000000006").boolValue());
        assertTrue(new NSString("7NO").boolValue());
        assertTrue(new NSString("-80000").boolValue());
        assertTrue(new NSString("9FALSE").boolValue());
    }
}
