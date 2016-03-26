package com.dd.plist.test.model;

import java.util.Set;
import java.util.Set;

public class TestClassSet {
    private Set<String> stringSet;
    private Set<Integer> integerSet;
    private Set<Long> longSet;
    private Set<Double> doubleSet;
    private Set<Float> floatSet;
    private Set<Byte> byteSet;
    private Set<Short> shortSet;
    private Set<TestClass1> test1Set;
    private Set<TestClass2> test2Set;
    private Set<TestClass3> test3Set;

    public Set<String> getStringSet() {
        return stringSet;
    }

    public void setStringSet(Set<String> stringSet) {
        this.stringSet = stringSet;
    }

    public Set<Integer> getIntegerSet() {
        return integerSet;
    }

    public void setIntegerSet(Set<Integer> integerSet) {
        this.integerSet = integerSet;
    }

    public Set<Long> getLongSet() {
        return longSet;
    }

    public void setLongSet(Set<Long> longSet) {
        this.longSet = longSet;
    }

    public Set<Double> getDoubleSet() {
        return doubleSet;
    }

    public void setDoubleSet(Set<Double> doubleSet) {
        this.doubleSet = doubleSet;
    }

    public Set<Float> getFloatSet() {
        return floatSet;
    }

    public void setFloatSet(Set<Float> floatSet) {
        this.floatSet = floatSet;
    }

    public Set<Byte> getByteSet() {
        return byteSet;
    }

    public void setByteSet(Set<Byte> byteSet) {
        this.byteSet = byteSet;
    }

    public Set<Short> getShortSet() {
        return shortSet;
    }

    public void setShortSet(Set<Short> shortSet) {
        this.shortSet = shortSet;
    }

    public Set<TestClass1> getTest1Set() {
        return test1Set;
    }

    public void setTest1Set(Set<TestClass1> test1Set) {
        this.test1Set = test1Set;
    }

    public Set<TestClass2> getTest2Set() {
        return test2Set;
    }

    public void setTest2Set(Set<TestClass2> test2Set) {
        this.test2Set = test2Set;
    }

    public Set<TestClass3> getTest3Set() {
        return test3Set;
    }

    public void setTest3Set(Set<TestClass3> test3Set) {
        this.test3Set = test3Set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestClassSet that = (TestClassSet) o;

        if (stringSet != null ? !stringSet.equals(that.stringSet) : that.stringSet != null) return false;
        if (integerSet != null ? !integerSet.equals(that.integerSet) : that.integerSet != null) return false;
        if (longSet != null ? !longSet.equals(that.longSet) : that.longSet != null) return false;
        if (doubleSet != null ? !doubleSet.equals(that.doubleSet) : that.doubleSet != null) return false;
        if (floatSet != null ? !floatSet.equals(that.floatSet) : that.floatSet != null) return false;
        if (byteSet != null ? !byteSet.equals(that.byteSet) : that.byteSet != null) return false;
        if (shortSet != null ? !shortSet.equals(that.shortSet) : that.shortSet != null) return false;
        if (test1Set != null ? !test1Set.equals(that.test1Set) : that.test1Set != null) return false;
        if (test2Set != null ? !test2Set.equals(that.test2Set) : that.test2Set != null) return false;
        return test3Set != null ? test3Set.equals(that.test3Set) : that.test3Set == null;

    }

    @Override
    public int hashCode() {
        int result = stringSet != null ? stringSet.hashCode() : 0;
        result = 31 * result + (integerSet != null ? integerSet.hashCode() : 0);
        result = 31 * result + (longSet != null ? longSet.hashCode() : 0);
        result = 31 * result + (doubleSet != null ? doubleSet.hashCode() : 0);
        result = 31 * result + (floatSet != null ? floatSet.hashCode() : 0);
        result = 31 * result + (byteSet != null ? byteSet.hashCode() : 0);
        result = 31 * result + (shortSet != null ? shortSet.hashCode() : 0);
        result = 31 * result + (test1Set != null ? test1Set.hashCode() : 0);
        result = 31 * result + (test2Set != null ? test2Set.hashCode() : 0);
        result = 31 * result + (test3Set != null ? test3Set.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestClassSet{" +
                "stringSet=" + stringSet +
                ", integerSet=" + integerSet +
                ", longSet=" + longSet +
                ", doubleSet=" + doubleSet +
                ", floatSet=" + floatSet +
                ", byteSet=" + byteSet +
                ", shortSet=" + shortSet +
                ", test1Set=" + test1Set +
                ", test2Set=" + test2Set +
                ", test3Set=" + test3Set +
                '}';
    }
}
