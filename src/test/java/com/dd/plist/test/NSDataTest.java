package com.dd.plist.test;

import com.dd.plist.NSData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NSDataTest {

    @Test
    public void stringConstructor_decodesBase64() throws IOException {
        NSData data = new NSData("SGVsbG8sIHdvcmxkIQ==");

        assertArrayEquals("Hello, world!".getBytes(StandardCharsets.UTF_8), data.bytes());
    }

    @Test
    public void stringConstructor_ignoresWhitespace() throws IOException {
        NSData data = new NSData("SGVs\n bG8s\tIHdvcmxkIQ==");

        assertArrayEquals("Hello, world!".getBytes(StandardCharsets.UTF_8), data.bytes());
    }

    @Test
    public void stringConstructor_rejectsInvalidBase64WithIOException() {
        assertThrows(IOException.class, () -> new NSData("not base64!"));
    }

    @Test
    public void getBase64EncodedData_encodesBytes() {
        NSData data = new NSData("Hello, world!".getBytes(StandardCharsets.UTF_8));

        assertEquals("SGVsbG8sIHdvcmxkIQ==", data.getBase64EncodedData());
    }
}
