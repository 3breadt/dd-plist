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
 * Keeps track of the stack of parsed objects in a binary property list.
 * @author Daniel Dreibrodt
 */
class ParsedObjectStack {

    private ParsedObjectStack parent;
    private int object;

    private ParsedObjectStack(ParsedObjectStack parent, int object) {
        this.parent = parent;
        this.object = object;
    }

    /**
     * Creates a new stack containing only the specified object identifier.
     * @return The stack.
     */
    public static ParsedObjectStack empty() {
        return new ParsedObjectStack(null, -1);
    }

    /**
     * Tries to push the specified object identifier onto the stack, checking that it is not already on the stack.
     * @param obj The object identifier.
     * @return The new stack with the added object identifier.
     * @throws PropertyListFormatException The stack already contained that object identifier,
     *         indicating a cyclic reference in the property list.
     */
    public ParsedObjectStack push(int obj) throws PropertyListFormatException {
        this.throwIfOnStack(obj);
        return new ParsedObjectStack(this, obj);
    }

    private void throwIfOnStack(int obj) throws PropertyListFormatException {
        if (this.parent != null) {
            if (this.object == obj) {
                throw new PropertyListFormatException("The given binary property list contains a cyclic reference.");
            }

            this.parent.throwIfOnStack(obj);
        }
    }
}
