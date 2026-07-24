/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2024 Daniel Dreibrodt
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
 * Information about the location of an NSObject within a binary property list file.
 *
 * @author Daniel Dreibrodt
 */
public class BinaryLocationInformation extends LocationInformation {

  private final int id;
  private final int offset;

  BinaryLocationInformation(int id, int offset) {
    this.id = id;
    this.offset = offset;
  }

  /**
   * Gets the ID of the NSObject.
   *
   * @return The ID of the NSObject.
   */
  public int getId() {
    return this.id;
  }

  /**
   * Gets the offset of the NSObject inside the file.
   *
   * @return The offset of the NSObject.
   */
  public int getOffset() {
    return this.offset;
  }

  @Override
  public String getDescription() {
    return "Object ID: " + this.id + ", Offset: " + this.offset;
  }
}
