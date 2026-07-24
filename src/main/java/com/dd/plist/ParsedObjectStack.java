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
 *
 * @author Daniel Dreibrodt
 */
class ParsedObjectStack {

  /**
   * The maximum number of nested objects that will be parsed. This protects against {@link
   * StackOverflowError}s caused by excessively (or maliciously) nested structures, while still
   * allowing very deeply nested legitimate property lists to be parsed.
   */
  static final int MAX_NESTING_DEPTH = 512;

  private final ParsedObjectStack parent;
  private final int object;
  private final int depth;

  private ParsedObjectStack(ParsedObjectStack parent, int object) {
    this.parent = parent;
    this.object = object;
    this.depth = parent == null ? 0 : parent.depth + 1;
  }

  /**
   * Creates a new stack containing only the specified object identifier.
   *
   * @return The stack.
   */
  public static ParsedObjectStack empty() {
    return new ParsedObjectStack(null, -1);
  }

  /**
   * Tries to push the specified object identifier onto the stack, checking that it is not already
   * on the stack and that the maximum nesting depth has not been exceeded.
   *
   * @param obj The object identifier.
   * @return The new stack with the added object identifier.
   * @throws PropertyListFormatException The stack already contained that object identifier
   *     (indicating a cyclic reference), or the maximum nesting depth was exceeded.
   */
  public ParsedObjectStack push(int obj) throws PropertyListFormatException {
    if (this.depth >= MAX_NESTING_DEPTH) {
      throw new PropertyListFormatException(
          "The nesting depth of the property list exceeds the maximum supported depth of "
              + MAX_NESTING_DEPTH
              + ".");
    }

    this.throwIfOnStack(obj);
    return new ParsedObjectStack(this, obj);
  }

  private void throwIfOnStack(int obj) throws PropertyListFormatException {
    for (ParsedObjectStack current = this; current.parent != null; current = current.parent) {
      if (current.object == obj) {
        throw new PropertyListFormatException(
            "The given binary property list contains a cyclic reference.");
      }
    }
  }
}
