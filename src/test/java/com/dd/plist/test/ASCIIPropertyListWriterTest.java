package com.dd.plist.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dd.plist.ASCIIPropertyListWriter;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import java.io.File;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ASCIIPropertyListWriter} class.
 * @author Daniel Dreibrodt
 */
public class ASCIIPropertyListWriterTest {
  @Test
  public void write_canWriteAppleFormat() throws Exception {
    File in = new File("test-files/test1.plist");
    File out = new File("test-files/out-test1-ascii.plist");
    File in2 = new File("test-files/test1-ascii.plist");
    NSDictionary x = (NSDictionary) PropertyListParser.parse(in);
    ASCIIPropertyListWriter.write(x, out);

    //Information gets lost when saving into the ASCII format (NSNumbers are converted to NSStrings)

    NSDictionary y = (NSDictionary) PropertyListParser.parse(out);
    NSDictionary z = (NSDictionary) PropertyListParser.parse(in2);
    assertEquals(y, z);
  }

  @Test
  public void writeGnuStep_canWriteGnuStepFormat() throws Exception {
    File in = new File("test-files/test1.plist");
    File out = new File("test-files/out-test1-ascii-gnustep.plist");
    NSDictionary x = (NSDictionary) PropertyListParser.parse(in);
    ASCIIPropertyListWriter.writeGnuStep(x, out);
    NSObject y = PropertyListParser.parse(out);
    assertEquals(x, y);
  }
}
