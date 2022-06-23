/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2022 Daniel Dreibrodt
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

package com.dd.plist;

/**
 * Reads Byte Order Marks for various Unicode encodings.
 * @author Daniel Dreibrodt
 */
class ByteOrderMarkReader {
    private static final int[][] BOMs = {
        { 0xEF, 0xBB, 0xBF },
        { 0xFE, 0xFF },
        { 0xFF, 0xFE},
        { 0x00, 0x00, 0xFE, 0xFF },
        { 0xFF, 0xFE, 0x00, 0x00 },
    };

    private static final String Charsets[] = {
        "UTF-8",
        "UTF-16BE",
        "UTF-16LE",
        "UTF-32BE",
        "UTF-32LE"
    };

    private boolean[] charsetPossible = { true, true, true, true, true};
    private int offset;
    private String charset;

    /**
     * Gets the charset that was detected.
     * @return The name of the detected charset, or <c>null</c> if no charset was detected.
     */
    public String getDetectedCharset() {
        return this.charset;
    }

    /**
     * Processes a byte that was read from the input.
     * @param b The byte to process.
     * @return <c>true</c> if the input so far could potentially be a BOM; otherwise, <c>false</c>.
     */
    public boolean readByte(int b) {
        boolean matchingCharset = false;
        for (int c = 0; c < Charsets.length; c++) {
            if (this.charsetPossible[c]) {
                int[] bom = BOMs[c];
                boolean match = this.offset < bom.length && bom[this.offset] == b;
                if (match) {
                    matchingCharset = true;
                    if (this.offset + 1 == bom.length) {
                        this.charset = Charsets[c];
                    }
                }
                else {
                    this.charsetPossible[c] = false;
                }
            }
        }

        this.offset++;
        return matchingCharset;
    }

    /**
     * Detects the encoding of input data that is available as a complete byte array.
     * @param bytes The input data.
     * @return The name of the detected charset, or <c>null</c> if no BOM was detected.
     */
    public static String detect(byte[] bytes) {
        // Check for byte order marks
        if (bytes.length > 2) {
            if (bytes[0] == (byte)0xFE && bytes[1] == (byte)0xFF) {
                return "UTF-16";
            }
            else if (bytes[0] == (byte)0xFF && bytes[1] == (byte)0xFE) {
                if (bytes.length > 4 && bytes[2] == (byte)0x00 && bytes[3] == (byte)0x00) {
                    return "UTF-32";
                }
                return "UTF-16";
            }
            else if (bytes.length > 3) {
                if (bytes[0] == (byte)0xEF && bytes[1] == (byte)0xBB && bytes[2] == (byte)0xBF) {
                    return "UTF-8";
                }
                else if (bytes.length > 4 && bytes[0] == (byte)0x00 && bytes[1] == (byte)0x00 && bytes[2] == (byte)0xFE && bytes[3] == (byte)0xFF) {
                    return "UTF-32";
                }
            }
        }

        return null;
    }
}
