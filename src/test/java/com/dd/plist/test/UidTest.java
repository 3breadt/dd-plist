package com.dd.plist.test;

import com.dd.plist.UID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the {@link UID} class.
 * @author Daniel Dreibrodt
 */
public class UidTest {

    @Test
    public void init_throwsIllegalArgumentExceptionForMoreThan16Bytes()
    {
        byte[] data = new byte[17];
        data[0] = 0x01;
        assertThrows(IllegalArgumentException.class, () -> new UID(null, data));
    }

    @Test
    public void getBytes_returnsMinimumPossibleLength() {
        assertEquals(1, new UID(null, new byte[]{0x01}).getBytes().length);
        assertEquals(2, new UID(null, new byte[]{0x01, 0x00}).getBytes().length);
        assertEquals(4, new UID(null, new byte[]{0x01, 0x00, 0x00}).getBytes().length);
        assertEquals(4, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(8, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(8, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(8, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(8, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(16, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(16, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(16, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(16, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(16, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(16, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(16, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
        assertEquals(16, new UID(null, new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}).getBytes().length);
    }

    @ParameterizedTest
    @MethodSource("provideUIDs")
    public void compareToAndEquals_worksCorrectly(UID a, UID b, int expectation) {
        assertEquals(expectation, a.compareTo(b), "compareTo returned unexpected value");
        assertEquals(expectation * -1, b.compareTo(a), "compareTo returned unexpected value");
        assertEquals(expectation == 0, a.equals(b), "equals returned unexpected value");
    }

    private static Stream<Arguments> provideUIDs() {
        return Stream.of(
                Arguments.of(
                        new UID("x", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
                        new UID("x", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
                        0),
                Arguments.of(
                        new UID(null, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
                        new UID("", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
                        1),
                Arguments.of(
                        new UID("a", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
                        new UID("b", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
                        -1),
                Arguments.of(
                        new UID("x", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01}),
                        new UID("x", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
                        1),
                Arguments.of(
                        new UID("x", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00}),
                        new UID("x", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01}),
                        1),
                Arguments.of(
                        new UID("x", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00}),
                        new UID("x", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00}),
                        1),
                Arguments.of(
                        new UID("x", new byte[]{0x12, 0x34, 0x56, 0x78, 0x09}),
                        new UID("x", new byte[]{0x12, 0x34, 0x55, 0x78, 0x09}),
                        1),
                Arguments.of(
                        new UID("x", new byte[]{0x12, 0x34, 0x56, 0x78, 0x09}),
                        new UID("x", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00}),
                        1));
    }
}