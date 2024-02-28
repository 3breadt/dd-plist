package com.dd.plist;

/**
 * Information about the location of an NSObject within an ASCII property list file.
 * @author Daniel Dreibrodt
 */
public class ASCIILocationInformation extends LocationInformation {

  private final int offset;

  ASCIILocationInformation(int offset) {
    this.offset = offset;
  }

  /**
   * Gets the offset of the NSObject inside the file.
   * @return The offset of the NSObject.
   */
  public int getOffset() {
    return this.offset;
  }

  @Override
  public String getDescription() {
    return "Offset: " + this.offset;
  }
}
