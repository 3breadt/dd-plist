package com.dd.plist;

/**
 * Information about the location of an NSObject within an XML property list file.
 * @author Daniel Dreibrodt
 */
public class XMLLocationInformation extends LocationInformation {

  private final String xpath;

  XMLLocationInformation(String xpath) {
    this.xpath = xpath;
  }

  /**
   * Gets the XPath of the XML node that is the source for the NSObject.
   * @return The XPath.
   */
  public String getXPath() {
    return this.xpath;
  }

  @Override
  public String getDescription() {
    return "XPath: " + this.xpath;
  }
}
