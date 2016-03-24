package com.dd.plist.test.model;

import java.util.Map;

public class TestClassMap {
    private Map<String, String> stringMap;
    private Map<String, Integer> integerMap;
    private Map<String, Long> longMap;
    private Map<String, Double> doubleMap;
    private Map<String, Float> floatMap;
    private Map<String, Byte> byteMap;
    private Map<String, Short> shortMap;
    private Map<String, TestClass1> test1Map;
    private Map<String, TestClass2> test2Map;
    private Map<String, TestClass3> test3Map;

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<String, String> stringMap) {
        this.stringMap = stringMap;
    }

    public Map<String, Integer> getIntegerMap() {
        return integerMap;
    }

    public void setIntegerMap(Map<String, Integer> integerMap) {
        this.integerMap = integerMap;
    }

    public Map<String, Long> getLongMap() {
        return longMap;
    }

    public void setLongMap(Map<String, Long> longMap) {
        this.longMap = longMap;
    }

    public Map<String, Double> getDoubleMap() {
        return doubleMap;
    }

    public void setDoubleMap(Map<String, Double> doubleMap) {
        this.doubleMap = doubleMap;
    }

    public Map<String, Float> getFloatMap() {
        return floatMap;
    }

    public void setFloatMap(Map<String, Float> floatMap) {
        this.floatMap = floatMap;
    }

    public Map<String, Byte> getByteMap() {
        return byteMap;
    }

    public void setByteMap(Map<String, Byte> byteMap) {
        this.byteMap = byteMap;
    }

    public Map<String, Short> getShortMap() {
        return shortMap;
    }

    public void setShortMap(Map<String, Short> shortMap) {
        this.shortMap = shortMap;
    }

    public Map<String, TestClass1> getTest1Map() {
        return test1Map;
    }

    public void setTest1Map(Map<String, TestClass1> test1Map) {
        this.test1Map = test1Map;
    }

    public Map<String, TestClass2> getTest2Map() {
        return test2Map;
    }

    public void setTest2Map(Map<String, TestClass2> test2Map) {
        this.test2Map = test2Map;
    }

    public Map<String, TestClass3> getTest3Map() {
        return test3Map;
    }

    public void setTest3Map(Map<String, TestClass3> test3Map) {
        this.test3Map = test3Map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestClassMap testClass = (TestClassMap) o;

        if (stringMap != null ? !stringMap.equals(testClass.stringMap) : testClass.stringMap != null) return false;
        if (integerMap != null ? !integerMap.equals(testClass.integerMap) : testClass.integerMap != null) return false;
        if (longMap != null ? !longMap.equals(testClass.longMap) : testClass.longMap != null) return false;
        if (doubleMap != null ? !doubleMap.equals(testClass.doubleMap) : testClass.doubleMap != null) return false;
        if (floatMap != null ? !floatMap.equals(testClass.floatMap) : testClass.floatMap != null) return false;
        if (byteMap != null ? !byteMap.equals(testClass.byteMap) : testClass.byteMap != null) return false;
        if (shortMap != null ? !shortMap.equals(testClass.shortMap) : testClass.shortMap != null) return false;
        if (test1Map != null ? !test1Map.equals(testClass.test1Map) : testClass.test1Map != null) return false;
        if (test2Map != null ? !test2Map.equals(testClass.test2Map) : testClass.test2Map != null) return false;
        return test3Map != null ? test3Map.equals(testClass.test3Map) : testClass.test3Map == null;

    }

    @Override
    public int hashCode() {
        int result = stringMap != null ? stringMap.hashCode() : 0;
        result = 31 * result + (integerMap != null ? integerMap.hashCode() : 0);
        result = 31 * result + (longMap != null ? longMap.hashCode() : 0);
        result = 31 * result + (doubleMap != null ? doubleMap.hashCode() : 0);
        result = 31 * result + (floatMap != null ? floatMap.hashCode() : 0);
        result = 31 * result + (byteMap != null ? byteMap.hashCode() : 0);
        result = 31 * result + (shortMap != null ? shortMap.hashCode() : 0);
        result = 31 * result + (test1Map != null ? test1Map.hashCode() : 0);
        result = 31 * result + (test2Map != null ? test2Map.hashCode() : 0);
        result = 31 * result + (test3Map != null ? test3Map.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestClassMap{" +
                "stringMap=" + stringMap +
                ", integerMap=" + integerMap +
                ", longMap=" + longMap +
                ", doubleMap=" + doubleMap +
                ", floatMap=" + floatMap +
                ", byteMap=" + byteMap +
                ", shortMap=" + shortMap +
                ", test1Map=" + test1Map +
                ", test2Map=" + test2Map +
                ", test3Map=" + test3Map +
                '}';
    }
}
