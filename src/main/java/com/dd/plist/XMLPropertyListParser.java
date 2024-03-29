/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2014 Daniel Dreibrodt
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Parses XML property lists.
 *
 * @author Daniel Dreibrodt
 */
public class XMLPropertyListParser {
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

    static {
        //
        // Attempt to disable parser features that can lead to XXE exploits; see:
        // https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Java
        //
        try {
            FACTORY.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException ignored) {
        }

        try {
            FACTORY.setFeature("http://xml.org/sax/features/external-general-entities", false);
        } catch (ParserConfigurationException ignored) {
        }

        try {
            FACTORY.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (ParserConfigurationException ignored) {
        }

        try {
            FACTORY.setXIncludeAware(false);
        } catch (UnsupportedOperationException ignored) {
        }

        FACTORY.setExpandEntityReferences(false);
        FACTORY.setNamespaceAware(false);
        FACTORY.setIgnoringComments(true);
        FACTORY.setCoalescing(true);
        FACTORY.setValidating(false);
    }

    /**
     * Gets a {@link DocumentBuilder} to parse an XML property list.
     * As {@link DocumentBuilder} instance are not thread-safe a new {@link DocumentBuilder} is generated for each request.
     *
     * @return A new {@link DocumentBuilder} that can parse property lists without an internet connection.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing a XML property list
     *                                                        could not be created. This should not occur.
     */
    public static DocumentBuilder getDocBuilder() throws ParserConfigurationException {
        DocumentBuilder builder = FACTORY.newDocumentBuilder();
        builder.setEntityResolver(new PlistDtdResolver());
        return builder;
    }

    /**
     * Parses an XML property list file.
     *
     * @param f The XML property list file.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.File)
     */
    public static NSObject parse(File f)
            throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException {
        return parse(f.toPath());
    }

    /**
     * Parses an XML property list file.
     *
     * @param path The XML property list file path.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.File)
     */
    public static NSObject parse(Path path)
            throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException {
        try (InputStream fileInputStream = Files.newInputStream(path)) {
            return parse(fileInputStream);
        }
    }

    /**
     * Parses an XML property list from a byte array.
     *
     * @param bytes The byte array containing the property list's data.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     */
    public static NSObject parse(final byte[] bytes)
            throws ParserConfigurationException, SAXException, PropertyListFormatException, IOException {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            return parse(inputStream);
        }
    }

    /**
     * Parses an XML property list from an input stream.
     * This method does not close the specified input stream.
     *
     * @param is The input stream pointing to the property list's data.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.InputStream)
     */
    public static NSObject parse(InputStream is)
            throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException {
        // Do not pass BOM to XML parser because it can't handle it
        InputStream filteredInputStream = new ByteOrderMarkFilterInputStream(is, false);
        return parse(parseXml(new InputSource(filteredInputStream), false));
    }

    /**
     * Parses an XML property list from a {@link Reader}.
     * This method does not close the specified reader.
     *
     * @param reader The reader providing the property list's data.
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.InputStream)
     */
    public static NSObject parse(Reader reader)
            throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException {
        return parse(parseXml(new InputSource(reader), false));
    }


    /**
     * Parses an XML property list file.
     *
     * @param f                   The XML property list file.
     * @param withLineInformation If set to {@code true}, the parser will try to collect line information and store it
     *                            in the parsed object's location information
     *                            (See {@link NSObject#getLocationInformation()}).
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.File)
     */
    public static NSObject parse(File f, boolean withLineInformation)
            throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException {
        return parse(f.toPath(), withLineInformation);
    }

    /**
     * Parses an XML property list file.
     *
     * @param path The XML property list file path.
     * @param withLineInformation If set to {@code true}, the parser will try to collect line information and store it
     *                            in the parsed object's location information
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.File)
     */
    public static NSObject parse(Path path, boolean withLineInformation)
            throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException {
        try (InputStream fileInputStream = Files.newInputStream(path)) {
            return parse(fileInputStream, withLineInformation);
        }
    }

    /**
     * Parses an XML property list from a byte array.
     *
     * @param bytes The byte array containing the property list's data.
     * @param withLineInformation If set to {@code true}, the parser will try to collect line information and store it
     *                            in the parsed object's location information
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     */
    public static NSObject parse(final byte[] bytes, boolean withLineInformation)
            throws ParserConfigurationException, SAXException, PropertyListFormatException, IOException {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            return parse(inputStream, withLineInformation);
        }
    }

    /**
     * Parses an XML property list from an input stream.
     * This method does not close the specified input stream.
     *
     * @param is The input stream pointing to the property list's data.
     * @param withLineInformation If set to {@code true}, the parser will try to collect line information and store it
     *                            in the parsed object's location information
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.InputStream)
     */
    public static NSObject parse(InputStream is, boolean withLineInformation)
            throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException {
        // Do not pass BOM to XML parser because it can't handle it
        InputStream filteredInputStream = new ByteOrderMarkFilterInputStream(is, false);
        return parse(parseXml(new InputSource(filteredInputStream), withLineInformation));
    }

    /**
     * Parses an XML property list from a {@link Reader}.
     * This method does not close the specified reader.
     *
     * @param reader The reader providing the property list's data.
     * @param withLineInformation If set to {@code true}, the parser will try to collect line information and store it
     *                            in the parsed object's location information
     * @return The root object of the property list. This is usually a {@link NSDictionary} but can also be a {@link NSArray}.
     * @throws javax.xml.parsers.ParserConfigurationException If a document builder for parsing an XML property list
     *                                                        could not be created. This should not occur.
     * @throws java.io.IOException                            If any I/O error occurs while reading the file.
     * @throws org.xml.sax.SAXException                       If any XML parsing error occurs.
     * @throws com.dd.plist.PropertyListFormatException       If the given property list has an invalid format.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.InputStream)
     */
    public static NSObject parse(Reader reader, boolean withLineInformation)
            throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException {
        return parse(parseXml(new InputSource(reader), withLineInformation));
    }

    /**
     * Parses a property list from an XML document.
     *
     * @param doc The XML document.
     * @return The root NSObject of the property list contained in the XML document.
     * @throws java.io.IOException                      If any I/O error occurs while reading the file.
     * @throws com.dd.plist.PropertyListFormatException If the given property list has an invalid format.
     */
    public static NSObject parse(Document doc) throws PropertyListFormatException, IOException {
        DocumentType docType = doc.getDoctype();
        if (docType == null) {
            if (!doc.getDocumentElement().getNodeName().equals("plist")) {
                throw new PropertyListFormatException("The given XML document is not a property list.");
            }
        } else if (!docType.getName().equals("plist")) {
            throw new PropertyListFormatException("The given XML document is not a property list.");
        }

        String xpath;
        Node rootNode;

        if (doc.getDocumentElement().getNodeName().equals("plist")) {
            xpath = "/plist";
            //Root element wrapped in plist tag
            List<Node> rootNodes = filterElementNodes(doc.getDocumentElement().getChildNodes());
            if (rootNodes.isEmpty()) {
                throw new PropertyListFormatException("The given XML property list has no root element!");
            } else if (rootNodes.size() == 1) {
                rootNode = rootNodes.get(0);
            } else {
                throw new PropertyListFormatException("The given XML property list has more than one root element!");
            }
        } else {
            //Root NSObject not wrapped in plist-tag
            rootNode = doc.getDocumentElement();
            xpath = "";
        }

        return parseObject(rootNode, xpath + "/" + rootNode.getNodeName());
    }

    private static Document parseXml(InputSource inputSource, boolean withLineInformation) throws IOException, SAXException, ParserConfigurationException {
        if (withLineInformation) {
            XMLReader xmlReader = createSafeXmlReader();

            XMLLocationFilter locationFilter = new XMLLocationFilter(xmlReader);
            SAXSource saxSource = new SAXSource(locationFilter, inputSource);

            DOMResult domResult = new DOMResult();
            try {
                Transformer transformer = createSafeTransformer();
                transformer.transform(saxSource, domResult);
            } catch (TransformerException e) {
                throw new IOException(e.getMessage(), e);
            }

            return (Document) domResult.getNode();
        } else {
            return getDocBuilder().parse(inputSource);
        }
    }

    private static XMLReader createSafeXmlReader() throws SAXException, ParserConfigurationException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        parserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        parserFactory.setXIncludeAware(false);

        SAXParser parser = parserFactory.newSAXParser();
        return parser.getXMLReader();
    }

    private static Transformer createSafeTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        return transformerFactory.newTransformer();
    }

    /**
     * Parses a node in the XML structure and returns the corresponding NSObject
     *
     * @param n The XML node.
     * @return The corresponding NSObject.
     * @throws PropertyListFormatException A parsing error occurred.
     */
    private static NSObject parseObject(Node n, String xpath) throws PropertyListFormatException {
        String type = n.getNodeName();
        XMLLocationInformation loc = new XMLLocationInformation(n, xpath);
        NSObject parsedObject = null;
        try {
            switch (type) {
                case "dict": {
                    NSDictionary dict = new NSDictionary();
                    parsedObject = dict;

                    List<Node> children = filterElementNodes(n.getChildNodes());
                    for (int i = 0; i < children.size(); i += 2) {
                        Node key = children.get(i);
                        String keyString = getNodeTextContents(key);

                        Node value = children.get(i + 1);
                        String childPath = xpath + "/*[" + (1 + i + 1) + "]";
                        dict.put(keyString, parseObject(value, childPath));
                    }

                    break;
                }
                case "array": {
                    List<Node> children = filterElementNodes(n.getChildNodes());
                    NSArray array = new NSArray(children.size());
                    parsedObject = array;

                    for (int i = 0; i < children.size(); i++) {
                        String childPath = xpath + "/*[" + (i + 1) + "]";
                        array.setValue(i, parseObject(children.get(i), childPath));
                    }

                    break;
                }
                case "true":
                    parsedObject = new NSNumber(true);
                    break;
                case "false":
                    parsedObject = new NSNumber(false);
                    break;
                case "integer":
                case "real":
                    parsedObject = new NSNumber(getNodeTextContents(n));
                    break;
                case "string":
                    parsedObject = new NSString(getNodeTextContents(n));
                    break;
                case "data":
                    parsedObject = new NSData(getNodeTextContents(n));
                    break;
                case "date":
                    parsedObject = new NSDate(getNodeTextContents(n));
                    break;
            }
        } catch (PropertyListFormatException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PropertyListFormatException(
                    loc.hasLineInformation()
                            ? ("The " + n.getNodeName() + " node at line " + loc.getLineNumber() + " and column " + loc.getColumnNumber() + " could not be parsed.")
                            : ("The " + n.getNodeName() + " node at " + xpath + " could not be parsed."),
                    loc,
                    ex);
        }

        if (parsedObject != null) {
            parsedObject.setLocationInformation(loc);
        }

        return parsedObject;
    }

    /**
     * Returns all element nodes that are contained in a list of nodes.
     *
     * @param list The list of nodes to search.
     * @return The sub-list containing only nodes representing actual elements.
     */
    private static List<Node> filterElementNodes(NodeList list) {
        List<Node> result = new ArrayList<>(list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                result.add(list.item(i));
            }
        }
        return result;
    }

    /**
     * Returns a node's text content.
     * This method will return the text value represented by the node's direct children.
     * If the given node is a TEXT or CDATA node, then its value is returned.
     *
     * @param n The node.
     * @return The node's text content.
     */
    private static String getNodeTextContents(Node n) {
        if (n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.CDATA_SECTION_NODE) {
            Text txtNode = (Text) n;
            String content = txtNode.getWholeText(); //This concatenates any adjacent text/cdata/entity nodes
            if (content == null)
                return "";
            else
                return content;
        } else {
            if (n.hasChildNodes()) {
                NodeList children = n.getChildNodes();

                for (int i = 0; i < children.getLength(); i++) {
                    //Skip any non-text nodes, like comments or entities
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
                        Text txtNode = (Text) child;
                        String content = txtNode.getWholeText(); //This concatenates any adjacent text/cdata/entity nodes
                        if (content == null)
                            return "";
                        else
                            return content;
                    }
                }

            }

            return "";
        }
    }

    /**
     * Offline resolver for Apple's PLIST DTDs.
     */
    private static class PlistDtdResolver implements EntityResolver {
        private static final String PLIST_PUBLIC_ID_1 = "-//Apple Computer//DTD PLIST 1.0//EN";
        private static final String PLIST_PUBLIC_ID_2 = "-//Apple//DTD PLIST 1.0//EN";

        PlistDtdResolver() {
        }

        /**
         * Allow the application to resolve external entities.
         * This specific implementation returns an empty definition for Apple's PLIST DTDs
         * so that parsing can happen offline.
         *
         * @param publicId The public identifier of the external entity being referenced, or null if none was supplied.
         * @param systemId The system identifier of the external entity being referenced.
         * @return An empty input source for the PLIST DTDs. For all other DTDs null is returned.
         */
        public InputSource resolveEntity(String publicId, String systemId) {
            if (PLIST_PUBLIC_ID_1.equals(publicId) || PLIST_PUBLIC_ID_2.equals(publicId)) {
                return new InputSource(new ByteArrayInputStream(new byte[0]));
            }
            return null;
        }
    }
}
