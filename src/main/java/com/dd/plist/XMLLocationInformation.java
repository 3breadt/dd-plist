package com.dd.plist;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

/**
 * Information about the location of an NSObject within an XML property list file.
 * <p>
 * Line and column number are only available if the {@code withLineInformation} parameter in the call to one of the
 * {@link XMLPropertyListParser}'s parse methods was set to {@code true}. The line information is taken from the
 * {@link Locator} class and thus is only an approximation of the actual location that can only be used
 * for diagnostic purposes.
 * </p>
 * @author Daniel Dreibrodt
 */
public class XMLLocationInformation extends LocationInformation {

    private final String xpath;
    private int lineNo = -1;
    private int column = -1;

    XMLLocationInformation(Node n, String xpath) {
        this.xpath = xpath;

        if (n.hasAttributes()) {
            NamedNodeMap attrs = n.getAttributes();
            Node lineNumberNode = attrs.getNamedItemNS(XMLLocationFilter.NS, XMLLocationFilter.LINE_NUMBER);
            if (lineNumberNode != null) {
                try {
                    this.lineNo = Integer.parseInt(lineNumberNode.getNodeValue());
                } catch (NumberFormatException ignored) {
                    // Invalid location information should not abort parsing
                }
            }

            Node colNumberNode = attrs.getNamedItemNS(XMLLocationFilter.NS, XMLLocationFilter.COLUMN_NUMBER);
            if (colNumberNode != null) {
                try {
                    this.column = Integer.parseInt(colNumberNode.getNodeValue());
                } catch (NumberFormatException ignored) {
                    // Invalid location information should not abort parsing
                }
            }
        }
    }

    /**
     * Gets the XPath of the XML node that is the source for the NSObject.
     *
     * @return The XPath.
     */
    public String getXPath() {
        return this.xpath;
    }

    /**
     * Gets a value indicating whether line and column number are available.
     *
     * @return {@code true}, if both line and column number are available; {@code false}, otherwise.
     */
    public boolean hasLineInformation() {
        return this.lineNo > 0 && this.column > 0;
    }

    /**
     * Gets the line number of the end of the XML node's start tag, if available.
     *
     * @return The line number (starting at 1), or -1 if the line number is not available.
     * @see Locator#getLineNumber()
     * @see XMLLocationInformation#hasLineInformation()
     */
    public int getLineNumber() {
        return this.lineNo;
    }

    /**
     * Gets the column number of the end of the XML node's start tag, if available.
     *
     * @return The column (starting at 1), or -1 if the column is not available.
     * @see Locator#getColumnNumber()
     * @see XMLLocationInformation#hasLineInformation()
     */
    public int getColumnNumber() {
        return this.column;
    }

    @Override
    public String getDescription() {
        if (this.hasLineInformation()) {
            return "Line: " + this.lineNo + ", Column: " + this.column + ", XPath: " + this.xpath;
        }

        return "XPath: " + this.xpath;
    }
}
