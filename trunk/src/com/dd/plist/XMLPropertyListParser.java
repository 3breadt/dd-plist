/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2010 Daniel Dreibrodt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.dd.plist;

import java.io.File;
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

  public static NSObject parse(File f) throws Exception {
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setIgnoringElementContentWhitespace(true);
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

    Document doc = docBuilder.parse(f);

    if(!doc.getDoctype().getName().equals("plist")) throw new UnsupportedOperationException("The given XML document is not a property list.");

    return parseObject(doc.getDocumentElement().getFirstChild());
  }

  public static NSObject parse(final byte[] bytes) throws Exception{
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setIgnoringElementContentWhitespace(true);
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

    InputStream is = new InputStream() {
      private int pos = 0;
      @Override
      public int read() throws IOException {
        if(pos>=bytes.length) return -1;
        return bytes[pos++];
      }
    };
    Document doc = docBuilder.parse(is);

    if(!doc.getDoctype().getName().equals("plist")) throw new UnsupportedOperationException("The given XML document is not a property list.");

    return parseObject(doc.getDocumentElement().getFirstChild());
  }

  private static NSObject parseObject(Node n) throws Exception {
    String type = n.getNodeName();
    if(type.equals("dict")) {
      NSDictionary dict = new NSDictionary();
      NodeList children = n.getChildNodes();
      for(int i=0;i<children.getLength();i+=2) {
        Node key = children.item(i);
        Node val = children.item(i+1);

        dict.put(key.getChildNodes().item(0).getNodeValue(), parseObject(val));
      }
      return dict;
    }
    else if(type.equals("array")) {
      NodeList children = n.getChildNodes();
      NSArray array = new NSArray(children.getLength());
      for(int i=0;i<children.getLength();i++)
        array.setValue(i, parseObject(children.item(i)));
      return array;
    }
    else if(type.equals("true")) {
      return new NSBoolean(true);
    }
    else if(type.equals("false")) {
      return new NSBoolean(false);
    }
    else if(type.equals("integer")) {
      return new NSInteger(n.getChildNodes().item(0).getNodeValue());
    }
    else if(type.equals("real")) {
      return new NSReal(n.getChildNodes().item(0).getNodeValue());
    }
    else if(type.equals("string")) {
      NodeList children = n.getChildNodes();
      if(children.getLength()==0) return new NSString(""); //Empty string
      else return new NSString(children.item(0).getNodeValue());
    }
    else if(type.equals("data")) {
      return new NSData(n.getChildNodes().item(0).getNodeValue());
    }
    else if(type.equals("date")) {
      return new NSDate(n.getChildNodes().item(0).getNodeValue());
    }
    return null;
  }
}
