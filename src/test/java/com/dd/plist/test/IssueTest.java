package com.dd.plist.test;

import com.dd.plist.NSData;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import org.junit.Test;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Regression tests for various issues and bugs that have been encountered
 */
public class IssueTest  {
    @Test
    public void testGzipInputStream() throws Exception {
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
        assertEquals(zippedObject, normalObject);
    }

    @Test
    public void testIssue31_FalsePositiveForGZipInsideDataElement() throws Exception {
        File plistFile = new File("test-files/github-issue31.plist");

        NSDictionary dict = (NSDictionary)PropertyListParser.parse(plistFile);
        NSDictionary files = (NSDictionary)dict.get("files2");
        NSData hash = (NSData)((NSDictionary)files.get("Base.lproj/Main.storyboardc/MainController.nib")).get("hash");
        assertEquals("1f8b2ef69414fa70ff578a697cfc0919235c8eff", HexConverter.toHex(hash.bytes()));
    }
}
