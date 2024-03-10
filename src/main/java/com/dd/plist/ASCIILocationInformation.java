package com.dd.plist;

/**
 * Information about the location of an NSObject within an ASCII property list file.
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
   * @return The offset of the NSObject.
   */
  public int getOffset() {
    return this.offset;
  }

  /**
   * Gets the line number.
   * @return The line number, starting at 1.
   */
  public int getLineNumber() {
    return this.lineNo;
  }

  /**
   * Gets the column number.
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
