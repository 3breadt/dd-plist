package com.dd.plist.test;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.dd.plist.test.model.TestAnnotationsClass1;
import com.dd.plist.test.model.TestAnnotationsClass2;
import com.dd.plist.test.model.TestAppleSCEP;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlistAnnotationsTest {

    public TestAppleSCEP getTestSCEP1() {
        TestAppleSCEP testAppleSCEP = new TestAppleSCEP();
        testAppleSCEP.setDescription("This is description");
        testAppleSCEP.setDisplayName("Apple SCEP");
        testAppleSCEP.setIdentifier("com.apple.scep.test");
        testAppleSCEP.setUuid("test123");
        testAppleSCEP.setVersion(1);
        testAppleSCEP.setPresent(true);
        TestAppleSCEP.TestAppleSCEPContent content = testAppleSCEP.getPayloadContent();
        content.setAllowAllAppsAccess(true);
        content.setChallenge("Challenge123");
        content.setKeyType("RSA");
        content.setKeyUsage(4);
        content.setRetries(10);
        content.setSubject(Collections.singletonList(Collections.singletonList(Collections.singletonList("Test Sung"))));
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
        List<String> part1 = new ArrayList<String>();
        part1.add("C");
        part1.add("US");
        List<String> part2 = new ArrayList<String>();
        part2.add("O");
        part2.add("DEV");
        List<List<String>> root1 = new ArrayList<>();
        root1.add(part1);
        List<List<String>> root2 = new ArrayList<>();
        root2.add(part2);
        List<List<List<String>>> root3 = new ArrayList<>();
        root3.add(root1);
        root3.add(root2);
        content.setSubject(root3);
        return testAppleSCEP;
    }

    @Test
    public void testSerializeIncludeAnnotatedPojo() {
        String xml = NSObject.fromJavaObject(new TestAnnotationsClass1()).toXMLPropertyList();

        assertTrue(xml.contains("<key>textIncluded</key>"), "Must be present - 'textIncluded'");
        assertTrue(xml.contains("<key>arrayIncluded</key>"), "Must be present - 'arrayIncluded'");
        assertFalse(xml.contains("<key>emptyText</key>"), "Must NOT BE present - 'emptyText'");
        assertFalse(xml.contains("<key>emptyArray</key>"), "Must NOT BE present - 'emptyArray'");
        assertFalse(xml.contains("<key>nullInt</key>"), "Must NOT BE present - 'nullInt'");
        assertFalse(xml.contains("<key>emptyList</key>"), "Must NOT BE present - 'emptyList'");
        assertFalse(xml.contains("<key>emptySet</key>"), "Must NOT BE present - 'emptySet'");
        assertFalse(xml.contains("<key>emptyMap</key>"), "Must NOT BE present - 'emptyMap'");
        assertTrue(xml.contains("<key>col</key>"), "Must be present - 'col'");

        xml = NSObject.fromJavaObject(new TestAnnotationsClass2()).toXMLPropertyList();

        assertFalse(xml.contains("<key>nullText</key>"), "Must NOT BE present - 'nullText'");
        assertTrue(xml.contains("<key>emptyText</key>"), "Must be present - 'emptyText'");
        assertTrue(xml.contains("<key>textIncluded</key>"), "Must be present - 'textIncluded'");
        assertTrue(xml.contains("<key>emptyArray</key>"), "Must be present - 'emptyArray'");
        assertTrue(xml.contains("<key>arrayIncluded</key>"), "Must be present - 'arrayIncluded'");
        assertFalse(xml.contains("<key>nullArray</key>"), "Must NOT BE present - 'nullArray'");
    }

    @Test
    public void testSerializePojo() {
        String xml = NSObject.fromJavaObject(getTestSCEP1()).toXMLPropertyList();

        assertTrue(xml.contains("<key>PayloadContent</key>"), "Must be present - 'PayloadContent'");
        assertTrue(xml.contains("<key>AllowAllAppsAccess</key>"), "Must be present - 'AllowAllAppsAccess'");
        assertTrue(xml.contains("<key>CAFingerprint</key>"), "Must be present - 'CAFingerprint'");
        assertTrue(xml.contains("<key>PayloadDisplayName</key>"), "Must be present - 'PayloadDisplayName'");
        assertFalse(xml.contains("<key>Ignored</key>"), "Must NOT BE present - 'Ignored'");
        assertFalse(xml.contains("<key>IgnoredTransient</key>"), "Must NOT BE present  - 'IgnoredTransient'");
        assertFalse(xml.contains("<key>IgnoredStatic</key>"), "Must NOT BE present - 'IgnoredStatic'");
        assertTrue(xml.contains("<key>EmptyTextIncluded</key>"), "Must be present - 'EmptyTextIncluded'");
        assertFalse(xml.contains("<key>EmptyText</key>"), "Must NOT BE present - 'EmptyText'");
        assertFalse(xml.contains("<key>EmptyArray</key>"), "Must NOT BE present - 'EmptyArray'");
        assertTrue(xml.contains("<key>EmptyArrayIncluded</key>"), "Must be present - 'EmptyArrayIncluded'");
        assertFalse(xml.contains("<key>NullInt</key>"), "Must NOT BE present - 'NullInt'");
        assertTrue(xml.contains("<key>IsPresent</key>"), "Must be present - 'IsPresent'");
        assertTrue(xml.contains("<key>PresentIs</key>"), "Must be present - 'PresentIs'");
    }

    @Test
    public void testDeserializePojo_apple_scep_1() throws Exception {
        NSObject root = PropertyListParser.parse(new File("test-files/test-pojo-apple-scep-1.plist"));

        NSDictionary dict1 = (NSDictionary) root;
        assertEquals(9, dict1.count());

        NSDictionary dict2 = (NSDictionary) dict1.objectForKey("PayloadContent");
        assertEquals(8, dict2.count());

        assertTrue(dict2.containsKey("AllowAllAppsAccess"), "Must be present - 'AllowAllAppsAccess'");
        assertTrue(dict2.containsKey("CAFingerprint"), "Must be present - 'CAFingerprint'");
        assertTrue(dict2.containsKey("Subject"), "Must be present - 'Subject'");

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

        assertTrue(dict2.containsKey("URL"), "Must be present - 'URL'");
        assertTrue(dict2.containsKey("SubjectAltName"), "Must be present - 'SubjectAltName'");
        assertTrue(dict2.containsKey("Subject"), "Must be present - 'Subject'");

        TestAppleSCEP deserialized = root.toJavaObject(TestAppleSCEP.class);
        assertEquals(getTestSCEP2(), deserialized, "deserialized plist file");
    }
}
