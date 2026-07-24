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
 * Information about the location of an NSObject within an ASCII property list file.
 *
 * @author Daniel Dreibrodt
 */
public class ASCIILocationInformation extends LocationInformation {
  private final int offset;
  private final int lineNo;
  private final int column;

  ASCIILocationInformation(int offset, int lineNo, int column) {
    this.offset = offset;
    this.lineNo = lineNo;
    this.column = column;
  }

  /**
   * Gets the offset of the NSObject inside the file.
   *
   * @return The offset of the NSObject.
   */
  public int getOffset() {
    return this.offset;
  }

  /**
   * Gets the line number.
   *
   * @return The line number, starting at 1.
   */
  public int getLineNumber() {
    return this.lineNo;
  }

  /**
   * Gets the column number.
   *
   * @return The column, starting at 1.
   */
  public int getColumnNumber() {
    return this.column;
  }

  @Override
  public String getDescription() {
    return "Line: " + this.lineNo + ", Column: " + this.column + ", Offset: " + this.offset;
  }
}
