package com.dd.plist;

import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * XML Filter that stores the location of nodes in custom attributes.
 *
 * @author Daniel Dreibrodt
 */
class XMLLocationFilter extends XMLFilterImpl {
    public static final String NS = "https://github.com/3breadt/dd-plist/";
    public static final String LINE_NUMBER = "LINE_NUMBER";
    public static final String COLUMN_NUMBER = "COLUMN_NUMBER";

    private Locator locator = null;

    XMLLocationFilter(XMLReader xmlReader) {
        super(xmlReader);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        AttributesImpl enhancedAttributes = new AttributesImpl(attributes);
        enhancedAttributes.addAttribute(
                NS, LINE_NUMBER, "dd:" + LINE_NUMBER, "CDATA", String.valueOf(this.locator.getLineNumber()));
        enhancedAttributes.addAttribute(
                NS, COLUMN_NUMBER, "dd:" + COLUMN_NUMBER, "CDATA", String.valueOf(this.locator.getColumnNumber()));
        super.startElement(uri, localName, qName, enhancedAttributes);
    }

}
