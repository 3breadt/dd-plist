/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2023 Daniel Dreibrodt
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

import java.io.IOException;

/**
 * Internally used representation of the null value for storing it inside dictionaries and sets.
 * @author Daniel Dreibrodt
 */
public final class NSNull extends NSObject {

    private static final NSNull NULL = new NSNull();

    private NSNull() {
    }

    /**
     * Returns the specified NSObject if it is not null, or a NSNull instance otherwise.
     * @param o The object.
     * @return The non-null object, or a NSNull instance.
     */
    protected static NSObject wrap(NSObject o) {
        return o == null ? NULL : o;
    }

    /**
     * Returns the specified NSObject if it is not a NSNull instance, or null otherwise.
     * @param o The object.
     * @return The non-null object, or null.
     */
    protected static NSObject unwrap(NSObject o) {
        return o == NULL ? null : o;
    }

    @Override
    public NSObject clone() {
        return this;
    }

    @Override
    public Object toJavaObject() {
        return null;
    }

    @Override
    void toXML(StringBuilder xml, int level)  {
        throw new NullPointerException("A null value cannot be represented in an XML property list.");
    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        out.write(0x00);
    }

    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        throw new NullPointerException("A null value cannot be represented in an ASCII property list.");
    }

    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        throw new NullPointerException("A null value cannot be represented in an ASCII property list.");
    }

    @Override
    public boolean equals(Object obj) {
        return obj == NULL;
    }

    @Override
    public int compareTo(NSObject o) {
        if (o == NULL)
        {
            return 0;
        }

        return o == null ? 1 : -1;
    }
}
