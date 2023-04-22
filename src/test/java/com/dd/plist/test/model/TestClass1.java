package com.dd.plist.test.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestClass1 {

    private Map<String, List<String>> map;

    public Map<String, List<String>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<String>> map) {
        this.map = map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestClass1 testClass = (TestClass1) o;

        return Objects.equals(map, testClass.map);

    }

    @Override
    public int hashCode() {
        return map != null ? map.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TestClassMap{" + "map=" + map + '}';
    }
}
