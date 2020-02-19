package com.dd.plist.test;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.dd.plist.test.model.TestAppleSCEP;
import com.sun.tools.javac.util.List;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class PlistAnnotationsTest {

    public static InputStream getResource(String filename) throws IOException {
        final String fullname = "/" + filename;
        final InputStream input = PlistAnnotationsTest.class.getResourceAsStream(fullname);
        if (input == null)
            throw new IOException(fullname + " cannot be loaded!");
        return input;
    }

    public TestAppleSCEP getTestSCEP1() {
        TestAppleSCEP testAppleSCEP = new TestAppleSCEP();
        testAppleSCEP.setDescription("This is description");
        testAppleSCEP.setDisplayName("Apple SCEP");
        testAppleSCEP.setIdentifier("com.apple.scep.test");
        testAppleSCEP.setUuid("test123");
        testAppleSCEP.setVersion(1);
        TestAppleSCEP.TestAppleSCEPContent content = testAppleSCEP.getPayloadContent();
        content.setAllowAllAppsAccess(true);
        content.setChallenge("Challenge123");
        content.setKeyType("RSA");
        content.setKeyUsage(4);
        content.setRetries(10);
        content.setSubject(List.of(List.of(List.of("C", "US")), List.of(List.of("O", "DEV"))));
        content.setCaFingerprint(new byte[]{23, 31, 53, 89, 99, -23, -12, 42, 120, -42, -78, 21, 23, 31, 53, 89, 99, -23, -12, 42, 120, -42, -78, 21, 23, 31, 53, 89, 99, -23, -12, 42, 120, -42, -78, 21, 23, 31, 53, 89, 99, -23, -12, 42, 120, -42, -78, 21, 2, 1, 0, 42, -12, 0, 2});
        return testAppleSCEP;
    }

    public TestAppleSCEP getTestSCEP2() {
        TestAppleSCEP testAppleSCEP = new TestAppleSCEP();
        testAppleSCEP.setDescription("Configures SCEP settings");
        testAppleSCEP.setDisplayName("SCEP");
        testAppleSCEP.setIdentifier("com.apple.security.scep.3B61BAE9-B049-4B89-8373-545A2DAD5136");
        testAppleSCEP.setUuid("3B61BAE9-B049-4B89-8373-545A2DAD5136");
        testAppleSCEP.setVersion(1);
        TestAppleSCEP.TestAppleSCEPContent content = testAppleSCEP.getPayloadContent();
        content.setChallenge("123");
        content.setKeyType("RSA");
        content.setKeySize(2048);
        content.setRetries(3);
        content.setSubject(List.of(List.of(List.of("Test Sung"))));
        return testAppleSCEP;
    }

    @Test
    public void testSerializePojo() {
        String xml = NSObject.fromJavaObject(getTestSCEP1()).toXMLPropertyList();

        assertTrue(xml.contains("<key>PayloadContent</key>"), "UpperCamelCase Test - 'PayloadContent'");
        assertFalse(xml.contains("<key>Ignored</key>"), "Ignored Test 'Ignored'");
        assertTrue(xml.contains("<key>AllowAllAppsAccess</key>"), "UpperCamelCase Test Subclass - 'AllowAllAppsAccess'");
        assertTrue(xml.contains("<key>CAFingerprint</key>"), "Alias Test Subclass - 'CAFingerprint'");
        assertTrue(xml.contains("<key>PayloadDisplayName</key>"), "Alias + Subclass Test - 'PayloadDisplayName'");
    }

    @Test
    public void testDeserializePojo_apple_scep_1() throws Exception {
        NSObject root = PropertyListParser.parse(new File("test-files/test-pojo-apple-scep-1.plist"));

        NSDictionary dict1 = (NSDictionary) root;
        assertEquals(7, dict1.count());

        NSDictionary dict2 = (NSDictionary) dict1.objectForKey("PayloadContent");
        assertEquals(8, dict2.count());

        assertTrue(dict2.containsKey("AllowAllAppsAccess"), "contains 'AllowAllAppsAccess'");
        assertTrue(dict2.containsKey("CAFingerprint"), "contains 'CAFingerprint'");
        assertTrue(dict2.containsKey("Subject"), "contains 'Subject'");

        TestAppleSCEP deserialized = root.toJavaObject(TestAppleSCEP.class);
        assertEquals(getTestSCEP1(), deserialized, "deserialized plist file");
    }

    @Test
    public void testDeserializePojo_apple_scep_2() throws Exception {
        // test a payload generated with Apple Configurator
        NSObject root = PropertyListParser.parse(new File("test-files/test-pojo-apple-scep-2.plist"));

        NSDictionary dict1 = (NSDictionary) root;
        assertEquals(7, dict1.count());

        NSDictionary dict2 = (NSDictionary) dict1.objectForKey("PayloadContent");
        assertEquals(9, dict2.count());

        assertTrue(dict2.containsKey("URL"), "contains 'URL'");
        assertTrue(dict2.containsKey("SubjectAltName"), "contains 'SubjectAltName'");
        assertTrue(dict2.containsKey("Subject"), "contains 'Subject'");

        TestAppleSCEP deserialized = root.toJavaObject(TestAppleSCEP.class);
        assertEquals(getTestSCEP2(), deserialized, "deserialized plist file");
    }
}
