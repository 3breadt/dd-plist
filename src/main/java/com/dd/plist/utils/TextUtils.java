/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2014 Daniel Dreibrodt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.dd.plist.utils;

public final class TextUtils {

    /**
     * A bit mask which selects the bit encoding ASCII character case.
     */
    private static final char CASE_MASK = 0x20;

    private static char toUpperCase(char c) {
        return (char) (c ^ CASE_MASK);
    }

    private static char toLowerCase(char c) {
        return (char) (c ^ CASE_MASK);
    }

    public static boolean isUpperCase(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    public static boolean isLowerCase(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    public static String makeFirstCharLowerCase(String input) {
        if (!isLowerCase(input.charAt(0))) {
            char[] chars = input.toCharArray();
            chars[0] = toLowerCase(chars[0]);
            return new String(chars);
        }
        return input;
    }

    public static String makeFirstCharUpperCase(String input) {
        if (!isUpperCase(input.charAt(0))) {
            char[] chars = input.toCharArray();
            chars[0] = toUpperCase(chars[0]);
            return new String(chars);
        }
        return input;
    }
}
