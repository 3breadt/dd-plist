package com.dd.plist.test.model;

import com.dd.plist.annotations.PlistInclude;
import com.dd.plist.annotations.PlistOptions;

@PlistOptions
@PlistInclude(PlistInclude.Include.NON_NULL)
public class TestAnnotationsClass2 {

    private String nullText = null;

    private String emptyText = "";

    private String textIncluded = "textIncluded";

    private byte[] emptyArray = new byte[]{};

    private byte[] arrayIncluded = new byte[]{17, 56, 0};

    private byte[] nullArray = null;
}
