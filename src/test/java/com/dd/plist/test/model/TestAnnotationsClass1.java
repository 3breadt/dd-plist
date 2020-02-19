package com.dd.plist.test.model;

import com.dd.plist.annotations.PlistAlias;
import com.dd.plist.annotations.PlistIgnore;
import com.dd.plist.annotations.PlistInclude;
import com.dd.plist.annotations.PlistOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@PlistOptions
@PlistInclude(PlistInclude.Include.NON_EMPTY)
public class TestAnnotationsClass1 {

    private String emptyText = "";

    private String textIncluded = "textIncluded";

    private byte[] emptyArray = new byte[]{};

    private byte[] arrayIncluded = new byte[]{17, 56, 0};

    private Integer nullInt = null;

}
