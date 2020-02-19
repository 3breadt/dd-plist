package com.dd.plist.test.model;

import com.dd.plist.annotations.PlistAlias;
import com.dd.plist.annotations.PlistIgnore;
import com.dd.plist.annotations.PlistInclude;
import com.dd.plist.annotations.PlistOptions;

import java.util.*;

@PlistOptions
@PlistInclude(PlistInclude.Include.NON_EMPTY)
public class TestAnnotationsClass1 {

    private String emptyText = "";

    private String textIncluded = "textIncluded";

    private byte[] emptyArray = new byte[]{};

    private byte[] arrayIncluded = new byte[]{17, 56, 0};

    private Integer nullInt = null;

    private List<String> emptyList = new ArrayList<>();

    private Set<String> emptySet = new HashSet<>();

    private Map<String, String> emptyMap = new HashMap<>();

    private Collection<String> col = Collections.singletonList("TEST");

}
