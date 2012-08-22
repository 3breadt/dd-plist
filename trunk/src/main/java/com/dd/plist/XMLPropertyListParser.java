/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2012 Daniel Dreibrodt
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Parses XML property lists
 * @author Daniel Dreibrodt
 */
public class XMLPropertyListParser {
    
    /**
     * Objects are unneccesary.
     */
    private XMLPropertyListParser() {
        /** empty **/
    }

    private static DocumentBuilderFactory docBuilderFactory = null;

    /**
     * Initialize the document builder factory so that it can be reuused and does not need to
     * be reinitialized for each new parsing.
     * @throws ParserConfigurationException If the parser configuration is not supported on your system.
     */
    private static synchronized void initDocBuilderFactory() throws ParserConfigurationException {
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringComments(true);
	docBuilderFactory.setCoalescing(true);
    }

    /**
     * Gets a DocumentBuilder to parse a XML property list.
     * As DocumentBuilders are not thread-safe a new DocBuilder is generated for each request.
     * @return A new DocBuilder that can parse property lists w/o an internet connection.
     */
    private static synchronized DocumentBuilder getDocBuilder() throws ParserConfigurationException {
        if(docBuilderFactory==null)
            initDocBuilderFactory();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	docBuilder.setEntityResolver(new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) {
		    if ("-//Apple Computer//DTD PLIST 1.0//EN".equals(publicId) || // older publicId
			"-//Apple//DTD PLIST 1.0//EN".equals(publicId)) { // newer publicId
			// return a dummy, zero length DTD so we don't have to fetch
			// it from the network.
			return new InputSource(new ByteArrayInputStream(new byte[0]));
		    }
		    return null;
		}
	    });
        return docBuilder;
    }

    /**
     * Parses a XML property list file.
     * @param f The XML property list file.
     * @return The root object of the property list. This is usally a NSDictionary but can also be a NSArray.
     * @throws Exception When an error occurs during parsing.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.File)
     */
    public static NSObject parse(File f) throws Exception {
        DocumentBuilder docBuilder = getDocBuilder();

        Document doc = docBuilder.parse(f);

        return parseDocument(doc);
    }

    /**
     * Parses a XML property list from a byte array.
     * @param bytes The byte array containing the property list's data.
     * @return The root object of the property list. This is usally a NSDictionary but can also be a NSArray.
     * @throws Exception When an error occurs during parsing.
     */
    public static NSObject parse(final byte[] bytes) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return parse(bis);
    }

    /**
     * Parses a XML property list from an input stream.
     * @param is The input stream pointing to the property list's data.
     * @return The root object of the property list. This is usally a NSDictionary but can also be a NSArray.
     * @throws Exception When an error occurs during parsing.
     * @see javax.xml.parsers.DocumentBuilder#parse(java.io.InputStream)
     */
    public static NSObject parse(InputStream is) throws Exception {
        DocumentBuilder docBuilder = getDocBuilder();

        Document doc = docBuilder.parse(is);

        return parseDocument(doc);
    }

    /**
     * Parses the XML document by generating the appropriate NSObjects for each XML node.
     * @param doc The XML document.
     * @return The root NSObject of the property list contained in the XML document.
     * @throws Exception If an error occured during parsing.
     */
    private static NSObject parseDocument(Document doc) throws Exception {
        DocumentType docType = doc.getDoctype();
        if(docType==null) {
            if(!doc.getDocumentElement().getNodeName().equals("plist")) {
                throw new UnsupportedOperationException("The given XML document is not a property list.");
            }
        }
        else if (!docType.getName().equals("plist")) {
            throw new UnsupportedOperationException("The given XML document is not a property list.");
        }

        //Skip all #TEXT nodes and take the first element node we find as root
        List<Node> rootNodes = filterElementNodes(doc.getDocumentElement().getChildNodes());
        if(rootNodes.size() > 0)
            return parseObject(rootNodes.get(0));
        else
            throw new Exception("No root node found!");
    }

    /**
     * Parses a node in the XML structure and returns the corresponding NSObject
     * @param n The XML node.
     * @return The corresponding NSObject.
     * @throws Exception If an error occured during parsing the node.
     */
    private static NSObject parseObject(Node n) throws Exception {
        String type = n.getNodeName();
        if (type.equals("dict")) {
            NSDictionary dict = new NSDictionary();
            List<Node> children = filterElementNodes(n.getChildNodes());
	    for (int i = 0; i < children.size(); i += 2) {
		Node key = children.get(i);
		Node val = children.get(i+1);
                
                String keyString = key.getChildNodes().item(0).getNodeValue();
                
                //Workaround for buggy behavior of the Android XML parser, which
                //separates certain Strings into several nodes
                for(int j=1;j<key.getChildNodes().getLength();j++)
                    keyString += key.getChildNodes().item(j).getNodeValue();
                
		dict.put(keyString, parseObject(val));
            }
            return dict;
        } else if (type.equals("array")) {
            List<Node> children = filterElementNodes(n.getChildNodes());
	    NSArray array = new NSArray(children.size());
	    for (int i = 0; i < children.size(); i++) {
		array.setValue(i, parseObject(children.get(i)));
	    }
	    return array;
        } else if (type.equals("true")) {
            return new NSNumber(true);
        } else if (type.equals("false")) {
            return new NSNumber(false);
        } else if (type.equals("integer")) {
            return new NSNumber(n.getChildNodes().item(0).getNodeValue());
        } else if (type.equals("real")) {
            return new NSNumber(n.getChildNodes().item(0).getNodeValue());
        } else if (type.equals("string")) {
            NodeList children = n.getChildNodes();
            if (children.getLength() == 0) {
                return new NSString(""); //Empty string
            } else {
                String string = children.item(0).getNodeValue();
                //Workaround for buggy behavior of the Android XML parser, which
                //separates certain Strings into several nodes
                for(int i=1;i<children.getLength();i++)
                    string += children.item(i).getNodeValue();
                return new NSString(string);
            }
        } else if (type.equals("data")) {
            return new NSData(n.getChildNodes().item(0).getNodeValue());
        } else if (type.equals("date")) {
            return new NSDate(n.getChildNodes().item(0).getNodeValue());
        }
        return null;
    }

    /**
     * Returns all element nodes that are contained in a list of nodes.
     * @param list The list of nodes to search.
     * @return The sublist containing only nodes representing actual elements.
     */
    private static List<Node> filterElementNodes(NodeList list) {
	List<Node> result = new ArrayList<Node>(list.getLength());
	for (int i=0; i<list.getLength(); i++) {
	    if (list.item(i).getNodeType()==Node.ELEMENT_NODE) {
		result.add(list.item(i));
	    }
	}
	return result;
    }
}
