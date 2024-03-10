package com.dd.plist.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.dd.plist.XMLPropertyListParser;
import java.io.File;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link BinaryPropertyListWriter} class.
 * @author Daniel Dreibrodt
 */
public class BinaryPropertyListWriterTest {
  @Test
  public void write_canWriteBinaryPropertyList() throws Exception {
    NSObject x = XMLPropertyListParser.parse(new File("test-files/test1.plist"));

    // save and load as binary
    BinaryPropertyListWriter.write(x, new File("test-files/out-testBinary.plist"));
    NSObject y = PropertyListParser.parse(new File("test-files/out-testBinary.plist"));
    assertEquals(x, y);
  }

}
