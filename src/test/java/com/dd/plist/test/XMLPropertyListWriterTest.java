package com.dd.plist.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.dd.plist.XMLPropertyListWriter;
import java.io.File;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

public class XMLPropertyListWriterTest {
  @Test
  public void write_canWriteXmlPropertyList() throws Exception {
    // parse an example plist file
    NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));

    // read/write it, make sure we get the same thing
    XMLPropertyListWriter.write(x, new File("test-files/out-testXml.plist"));
    NSObject y = PropertyListParser.parse(new File("test-files/out-testXml.plist"));
    assertEquals(x, y);
  }

  /** A bare file name has no parent directory; writing must not fail trying to create one. */
  @Test
  public void write_acceptsFileWithoutParentDirectory() throws Exception {
    NSObject x = PropertyListParser.parse(new File("test-files/test1.plist"));
    File out = new File("out-test-bare-filename.plist");
    try {
      XMLPropertyListWriter.write(x, out);
      assertEquals(x, PropertyListParser.parse(out));
    } finally {
      Files.deleteIfExists(out.toPath());
    }
  }
}
