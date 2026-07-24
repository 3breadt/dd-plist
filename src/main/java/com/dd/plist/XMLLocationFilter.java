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
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    AttributesImpl enhancedAttributes = new AttributesImpl(attributes);
    enhancedAttributes.addAttribute(
        NS,
        LINE_NUMBER,
        "dd:" + LINE_NUMBER,
        "CDATA",
        String.valueOf(this.locator.getLineNumber()));
    enhancedAttributes.addAttribute(
        NS,
        COLUMN_NUMBER,
        "dd:" + COLUMN_NUMBER,
        "CDATA",
        String.valueOf(this.locator.getColumnNumber()));
    super.startElement(uri, localName, qName, enhancedAttributes);
  }
}
