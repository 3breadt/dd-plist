package com.dd.plist.test;

import com.dd.plist.*;
import com.dd.plist.test.model.*;
import junit.framework.TestCase;

import java.nio.charset.Charset;
import java.util.*;

public class DeSerializationTest extends TestCase {
    private static final Date date = new Date();

    public void testSimpleMap() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", "value");
        NSDictionary dic = new NSDictionary();
        dic.put("key", new NSString("value"));
        Object result = dic.toJavaObject(map.getClass());
        assertEquals(map, result);
    }

    public void testSimpleMapNS() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", "value");
        NSObject result = NSObject.fromJavaObject(map);

        NSDictionary test = new NSDictionary();
        test.put("key", new NSString("value"));

        assertEquals(test, result);
    }

    public void testPojoMap() throws Exception {
        TestClassMap test = new TestClassMap();
        test.setStringMap(mapFromArr("key", "value"));
        test.setByteMap(mapFromArr("key", (byte) 123));
        test.setIntegerMap(mapFromArr("key", 234));
        test.setLongMap(mapFromArr("key", 345L));
        test.setDoubleMap(mapFromArr("key", 4.56d));
        test.setShortMap(mapFromArr("key", (short) 512));
        test.setFloatMap(mapFromArr("key", 5.67f));
        test.setTest1Map(mapFromArr("key", genTest1Object()));
        test.setTest2Map(mapFromArr("key", genTest2Object()));
        test.setTest3Map(mapFromArr("key", genTest3Object()));

        NSDictionary dict = new NSDictionary();
        dict.put("stringMap", genDict("key", new NSString("value")));
        dict.put("byteMap", genDict("key", new NSNumber((byte) 123)));
        dict.put("integerMap", genDict("key", new NSNumber(234)));
        dict.put("longMap", genDict("key", new NSNumber(345L)));
        dict.put("doubleMap", genDict("key", new NSNumber(4.56d)));
        dict.put("shortMap", genDict("key", new NSNumber((short) 512)));
        dict.put("floatMap", genDict("key", new NSNumber(5.67f)));
        dict.put("test1Map", genDict("key", genTest1Dict()));
        dict.put("test2Map", genDict("key", genTest2Dict()));

        dict.put("test3Map", genDict("key", genTest3DictOfSets()));
        assertEquals(test, dict.toJavaObject(TestClassMap.class));

        dict.put("test3Map", genDict("key", genTest3DictOfArrays()));
        assertEquals(test, dict.toJavaObject(TestClassMap.class));
    }

    public void testDictSet() throws Exception {
        assertEquals(NSObject.fromJavaObject(genTestSetObject()), genTestSetDict());
    }

    public void testPojoSet() throws Exception {
        TestClassSet test = genTestSetObject();
        NSDictionary dict = genTestSetDict();
        assertEquals(test, dict.toJavaObject(TestClassSet.class));
        dict.put("test3Set", new NSSet((genTest3DictOfSets())));
        assertEquals(test, dict.toJavaObject(TestClassSet.class));
    }

    public void testUid() throws Exception {

    }

    private NSDictionary genTestSetDict() {
        NSDictionary dict = new NSDictionary();
        dict.put("stringSet", new NSSet(new NSString("value")));
        dict.put("byteSet", new NSSet((new NSNumber(123))));
        dict.put("integerSet", new NSSet((new NSNumber(234))));
        dict.put("longSet", new NSSet((new NSNumber(345L))));
        dict.put("doubleSet", new NSSet((new NSNumber(4.56d))));
        dict.put("shortSet", new NSSet((new NSNumber((short) 512))));
        dict.put("floatSet", new NSSet((new NSNumber(5.67f))));
        dict.put("test1Set", new NSSet((genTest1Dict())));
        dict.put("test2Set", new NSSet((genTest2Dict())));
        dict.put("test3Set", new NSSet((genTest3DictOfArrays())));
        return dict;
    }

    private TestClassSet genTestSetObject() {
        TestClassSet test = new TestClassSet();
        test.setStringSet(setFromArr("value"));
        test.setByteSet(setFromArr((byte) 123));
        test.setIntegerSet(setFromArr(234));
        test.setLongSet(setFromArr(345L));
        test.setDoubleSet(setFromArr(4.56d));
        test.setShortSet(setFromArr((short) 512));
        test.setFloatSet(setFromArr(5.67f));
        test.setTest1Set(setFromArr(genTest1Object()));
        test.setTest2Set(setFromArr(genTest2Object()));
        test.setTest3Set(setFromArr(genTest3Object()));
        return test;
    }

    private NSDictionary genDict(String key, NSObject value) {
        NSDictionary result = new NSDictionary();
        result.put(key, value);
        return result;
    }

    private <V> Map<String, V> mapFromArr(String key, V value) {
        Map<String, V> result = new HashMap<String, V>();
        result.put(key, value);
        return result;
    }

    private <V> Set<V> setFromArr(V... values) {
        Set<V> result = new HashSet<V>();
        for (V value : values) {
            result.add(value);
        }
        return result;
    }

    private TestClass1 genTest1Object() {
        TestClass1 test = new TestClass1();
        Map<String, List<String>> testMap = new HashMap<String, List<String>>();

        List<String> list = new ArrayList<String>();
        list.add("value1");
        list.add("value2");
        list.add("value3");
        testMap.put("key", list);
        test.setMap(testMap);
        return test;
    }

    private NSDictionary genTest2Dict() {
        NSDictionary dict = new NSDictionary();
        dict.put("double", new NSNumber(1.23d));
        dict.put("primitiveDouble", new NSNumber(3.21d));
        dict.put("float", new NSNumber(4.56f));

        dict.put("primitiveFloat", new NSNumber(6.54f));
        dict.put("integer", new NSNumber(9876));
        dict.put("primitiveInteger", new NSNumber(6789));
        dict.put("long", new NSNumber(123456789L));
        dict.put("primitiveLong", new NSNumber(987654321L));

        dict.put("short", new NSNumber((short) 123));
        dict.put("primitiveShort", new NSNumber((short) 81));
        dict.put("string", new NSString("Hello World"));

        dict.put("boolean", new NSNumber(Boolean.TRUE));
        dict.put("primitiveBoolean", new NSNumber(true));
        dict.put("date", new NSDate(date));

        return dict;
    }

    private TestClass2 genTest2Object() {
        TestClass2 test = new TestClass2();
        test.setDouble(1.23d);
        test.setPrimitiveDouble(3.21d);
        test.setFloat(4.56f);
        test.setPrimitiveFloat(6.54f);
        test.setInteger(9876);
        test.setPrimitiveInteger(6789);
        test.setLong(123456789L);
        test.setPrimitiveLong(987654321L);
        test.setShort((short) 123);
        test.setPrimitiveShort((short) 81);
        test.setString("Hello World");
        test.setBoolean(Boolean.TRUE);
        test.setPrimitiveBoolean(true);
        test.setDate(date);

        return test;
    }

    private TestClass3 genTest3Object() {
        TestClass3 test = new TestClass3();

        test.setBooleanArray(new Boolean[]{true, false});
        test.setPrimitiveBooleanArray(new boolean[]{true, false});
        test.setDoubleArray(new Double[]{1.23d});
        test.setPrimitiveDoubleArray(new double[]{3.21d});
        test.setFloatArray(new Float[]{4.56f});
        test.setPrimitiveFloatArray(new float[]{6.54f});
        test.setIntegerArray(new Integer[]{9876});
        test.setPrimitiveIntegerArray(new int[]{6789});
        test.setLongArray(new Long[]{123456789L});
        test.setPrimitiveLongArray(new long[]{987654321L});
        test.setShortArray(new Short[]{(short) 123});
        test.setPrimitiveShortArray(new short[]{(short) 81});
        test.setStringArray(new String[]{"Hello", "World"});
        test.setDateArray(new Date[]{date});

        test.setByteArray(new Byte[]{123});
        test.setPrimitiveByteArray(new byte[]{81});

        return test;
    }

    private NSDictionary genTest3DictOfArrays() {
        NSDictionary dict = new NSDictionary();
        dict.put("doubleArray", new NSArray(new NSNumber(1.23d)));
        dict.put("primitiveDoubleArray", new NSArray(new NSNumber(3.21d)));
        dict.put("floatArray", new NSArray(new NSNumber(4.56f)));

        dict.put("primitiveFloatArray", new NSArray(new NSNumber(6.54f)));
        dict.put("integerArray", new NSArray(new NSNumber(9876)));
        dict.put("primitiveIntegerArray", new NSArray(new NSNumber(6789)));
        dict.put("longArray", new NSArray(new NSNumber(123456789L)));
        dict.put("primitiveLongArray", new NSArray(new NSNumber(987654321L)));

        dict.put("shortArray", new NSArray(new NSNumber((short) 123)));
        dict.put("primitiveShortArray", new NSArray(new NSNumber((short) 81)));
        dict.put("stringArray", new NSArray(new NSString("Hello"), new NSString("World")));

        dict.put("booleanArray", new NSArray(new NSNumber(Boolean.TRUE), new NSNumber(Boolean.FALSE)));
        dict.put("primitiveBooleanArray", new NSArray(new NSNumber(true), new NSNumber(false)));
        dict.put("dateArray", new NSArray(new NSDate(date)));

        dict.put("byteArray", new NSData(new byte[] {(byte) 123}));
        dict.put("primitiveByteArray", new NSData(new byte[] {(byte) 81}));

        return dict;
    }

    private NSDictionary genTest3DictOfSets() {
        NSDictionary dict = new NSDictionary();
        dict.put("doubleArray", new NSSet(new NSNumber(1.23d)));
        dict.put("primitiveDoubleArray", new NSSet(new NSNumber(3.21d)));
        dict.put("floatArray", new NSSet(new NSNumber(4.56f)));

        dict.put("primitiveFloatArray", new NSSet(new NSNumber(6.54f)));
        dict.put("integerArray", new NSSet(new NSNumber(9876)));
        dict.put("primitiveIntegerArray", new NSSet(new NSNumber(6789)));
        dict.put("longArray", new NSSet(new NSNumber(123456789L)));
        dict.put("primitiveLongArray", new NSSet(new NSNumber(987654321L)));

        dict.put("shortArray", new NSSet(new NSNumber((short) 123)));
        dict.put("primitiveShortArray", new NSSet(new NSNumber((short) 81)));
        dict.put("stringArray", new NSSet(new NSString("Hello"), new NSString("World")));

        dict.put("booleanArray", new NSSet(new NSNumber(Boolean.TRUE), new NSNumber(Boolean.FALSE)));
        dict.put("primitiveBooleanArray", new NSSet(new NSNumber(true), new NSNumber(false)));
        dict.put("dateArray", new NSSet(new NSDate(date)));

        dict.put("byteArray", new NSData(new byte[]{(byte) 123}));
        dict.put("primitiveByteArray", new NSData(new byte[]{(byte) 81}));

        return dict;
    }

    private NSDictionary genTest1Dict() {
        NSDictionary dic = new NSDictionary();
        NSDictionary map = new NSDictionary();
        NSArray array = new NSArray(3);
        array.setValue(0, new NSString("value1"));
        array.setValue(1, new NSString("value2"));
        array.setValue(2, new NSString("value3"));
        map.put("key", array);
        dic.put("map", map);
        return dic;
    }

    public void testPojoMapNested() throws Exception {
        assertEquals(
                genTest1Object(),
                genTest1Dict()
                        .toJavaObject(TestClass1.class));
    }

    public void testArrayPojo() throws Exception {
        assertEquals(
                genTest3Object(),
                genTest3DictOfArrays()
                        .toJavaObject(TestClass3.class));

        assertEquals(
                genTest3Object(),
                genTest3DictOfSets()
                        .toJavaObject(TestClass3.class));
    }

    public void testSimplePojo() throws Exception {
        TestClass2 reference = genTest2Object();
        TestClass2 deserialized = genTest2Dict().toJavaObject(TestClass2.class);
        assertEquals(reference, deserialized);
    }

    public void testSimpleNS() throws Exception {
        assertEquals(
                NSObject.fromJavaObject(genTest2Object()),
                genTest2Dict());
    }

    public void testArrayNS() throws Exception {
        assertEquals(
                NSObject.fromJavaObject(genTest3Object()),
                genTest3DictOfArrays());
    }

    public void testNSMapNested() throws Exception {
        assertEquals(
                NSObject.fromJavaObject(genTest1Object()),
                genTest1Dict());
    }

    public void testStringsFile() throws Exception {
        String stringFileContentStr = "/* Menu item to make the current document plain text */\n" +
                "\"Make Plain Text\" = \"In reinen Text umwandeln\";\n" +
                "/* Menu item to make the current document rich text */\n" +
                "\"Make Rich Text\" = \"In formatierten Text umwandeln\";\n";
        byte[] stringFileContentRaw = stringFileContentStr.getBytes();

        String stringFileContent = new String(stringFileContentRaw, Charset.forName("UTF-8"));
        String asciiPropertyList = "{" + stringFileContent + "}";
        NSDictionary dict = (NSDictionary)ASCIIPropertyListParser.parse(asciiPropertyList.getBytes(Charset.forName("UTF-8")));
        assertTrue(dict.containsKey("Make Plain Text"));
        assertEquals("In reinen Text umwandeln", dict.get("Make Plain Text").toString());
    }

}
