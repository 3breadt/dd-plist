package com.dd.plist;

/**
 * Information about the location of an NSObject within a binary property list file.
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
   * @return The ID of the NSObject.
   */
  public int getId() {
    return this.id;
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
    return "Object ID: " + this.id + ", Offset: " + this.offset;
  }
}
