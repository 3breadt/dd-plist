package com.dd.plist.test;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Regression tests for various issues and bugs that have been encountered
 */
public class IssueTest extends TestCase {
    public static void testGzipInputStream() {
        try {
            File plistFile = new File("test-files/test-gzipinputstream-issue.plist");

            //Get the file input stream
            InputStream fileInputStream = new FileInputStream(plistFile);

            //GZIP that file
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

            byte[] buffer = new byte[1024];
            for (int length; (length = fileInputStream.read(buffer)) != -1; ) {
                gzipOutputStream.write(buffer, 0, length);
            }

            fileInputStream.close();
            gzipOutputStream.close();

            //Create an GZIP input stream from the zipped byte array
            InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            InputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

            //Parse the property list from that stream
            NSObject zippedObject = PropertyListParser.parse(gzipInputStream);
            NSObject normalObject = PropertyListParser.parse(plistFile);
            Assert.assertEquals(zippedObject, normalObject);
        } catch(Throwable t) {
            Assert.fail("Could not parse property list from GZIP input stream. "
                    + t.getClass().getName() + " was thrown: " + t.getMessage());
        }
    }
}
