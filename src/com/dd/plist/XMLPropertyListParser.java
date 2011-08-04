/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2011 Daniel Dreibrodt
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses XML property lists
 * @author Daniel Dreibrodt
 */
public class XMLPropertyListParser {

    /**
     * Parses a XML property list file.
     * @param f The XML plist file.
     * @return The root object of the property list.
     * @throws Exception
     */
    public static NSObject parse(File f) throws Exception {
        FileInputStream fis = new FileInputStream(f);
        return parse(fis);
    }

    /**
     * Parses a XML property list from a byte array.
     * @param bytes The byte array
     * @return The root object of the property list
     * @throws Exception
     */
    public static NSObject parse(final byte[] bytes) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return parse(bis);
    }

    /**
     * Parses a XML property list from an input stream.
     * @param is The input stream.
     * @return The root object of the property list
     * @throws Exception
     */
    public static NSObject parse(InputStream is) throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        //Enable offline parsing
        docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        docBuilderFactory.setIgnoringComments(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        Document doc = docBuilder.parse(is);

        if (!doc.getDoctype().getName().equals("plist")) {
            throw new UnsupportedOperationException("The given XML document is not a property list.");
        }

        //Skip all #TEXT nodes and take the first element node we find as root
        NodeList rootNodes = doc.getDocumentElement().getChildNodes();
        int rootIndex = getNextElementNode(rootNodes, 0);
        if(rootIndex!=-1)
            return parseObject(rootNodes.item(rootIndex));
        else
            throw new Exception("No root node found!");
    }

    /**
     * Parses a node in the XML structure and returns the corresponding NSObject
     * @param n The XML node
     * @return The corresponding NSObject
     * @throws Exception
     */
    private static NSObject parseObject(Node n) throws Exception {
        String type = n.getNodeName();
        if (type.equals("dict")) {
            NSDictionary dict = new NSDictionary();
            NodeList children = n.getChildNodes();
            for (int i = getNextElementNode(children, 0); i != -1; i = getNextElementNode(children, i)) {
                Node key = children.item(i);
                i = getNextElementNode(children, i);
                Node val = children.item(i);

                dict.put(key.getChildNodes().item(0).getNodeValue(), parseObject(val));
            }
            return dict;
        } else if (type.equals("array")) {
            NodeList children = n.getChildNodes();
            NSArray array = new NSArray(children.getLength());
            for (int i = getNextElementNode(children, 0); i != -1; i = getNextElementNode(children,i)) {
                array.setValue(i, parseObject(children.item(i)));
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
                return new NSString(children.item(0).getNodeValue());
            }
        } else if (type.equals("data")) {
            return new NSData(n.getChildNodes().item(0).getNodeValue());
        } else if (type.equals("date")) {
            return new NSDate(n.getChildNodes().item(0).getNodeValue());
        }
        return null;
    }

    /**
     * Finds the next element node, starting from the given index.
     * @param list The list of nodes to search
     * @param startIndex The index from where to start searching
     * @return The next index of an element node or -1 if none is found.
     */
    private static int getNextElementNode(NodeList list, int startIndex) {
        for(int i=startIndex;i<list.getLength();i++) {
            if(list.item(i).getNodeType()==Node.ELEMENT_NODE)
                return i;
        }
        return -1;
    }
}
