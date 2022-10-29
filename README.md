# com.dd.plist - A Java library for working with property lists

[![Build Status](https://travis-ci.org/3breadt/dd-plist.svg?branch=master)](https://travis-ci.org/3breadt/dd-plist)

This library enables your Java application to handle property lists of various formats. It is licensed under the terms of the MIT license.

Property lists are files used to store user settings and serialized objects.
They originate from the NeXSTEP programming environment and are now a basic part of the Cocoa framework (OS X and iOS) as well as the GNUstep framework.

## Features

 * Read / write property lists from / to files, streams or byte arrays
 * Convert between property list formats
 * Property list contents are provided as objects from the NeXTSTEP environment (NSDictionary, NSArray, NSString, etc.)
 * Serialize native java data structures to property list objects
 * Deserialize from property list objects to native java data structures

## Supported formats

 * Cocoa XML
 * Cocoa Binary (v0)
 * Cocoa / NeXSTSTEP / GNUstep ASCII

## Maven support

If you use Maven and want to include the library into your project you can use the following dependency.

    <dependency>
      <groupId>com.googlecode.plist</groupId>
      <artifactId>dd-plist</artifactId>
      <version>1.26</version>
    </dependency>

## Help

The API documentation is included in the download but can also be browsed online: [JavaDoc for com.dd.plist](https://3breadt.github.io/dd-plist/).

If you have further questions please post them on the [GitHub issue tracker](https://github.com/3breadt/dd-plist/issues) or in the Discussion forum plist-discuss on [Google Groups](http://groups.google.com/group/plist-discuss).

## Usage examples

### Reading

Parsing can be done with the PropertyListParser class. You can feed the `PropertyListParser` with a `File`, an `InputStream` or a `byte` array.
The `parse` method of the `PropertyListParser` will parse the input and give you a `NSObject` as result. Generally this is a `NSDictionary` but it can also be a `NSArray`.

_Note: Property lists created by `NSKeyedArchiver` are not yet supported_

You can then navigate the contents of the property lists using the various classes extending `NSObject`. These are modeled in such a way as to closely resemble the respective Cocoa classes.

You can also directly convert the contained `NSObject` objects into native Java objects with the `NSObject.toJavaObject()` method. Using this method you can avoid working with `NSObject` instances altogether.

### Writing

You can create your own property list using the various constructors of the different `NSObject` classes. Or you can wrap existing native Java structures with the method `NSObject.wrap(Object o)`. Just make sure that the root object of the property list is either a `NSDictionary` (can be created from objects of the type `Map<String, Object>`) or a `NSArray` (can be created from object arrays).

For building an XML property list you can then call the `toXMLPropertyList` method on the root object of your property list. It will give you a UTF-8 `String` containing the property list in XML format.

If you want to have the property list in binary format use the `BinaryPropertyListWriter` class. It can write the binary property list directly to a file or to an `OutputStream`.

When you directly want to save your property list to a file, you can also use the `saveAsXML` or `saveAsBinary` methods of the `PropertyListParser` class.

### Converting

For converting a file into another format there exist convenience methods in the `PropertyListParser` class: `convertToXML`, `convertToBinary`,  `convertToASCII` and `convertToGnuStepASCII`.

## Code snippets

### Reading

    try {
      File file = new File("Info.plist");
      NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(file);
      String name = rootDict.objectForKey("Name").toString();
      NSObject[] parameters = ((NSArray)rootDict.objectForKey("Parameters")).getArray();
      for(NSObject param:parameters) {
        if(param.getClass().equals(NSNumber.class)) {
          NSNumber num = (NSNumber)param;
          switch(num.type()) {
            case NSNumber.BOOLEAN : {
              boolean bool = num.boolValue();
              //...
              break;
            }
            case NSNumber.INTEGER : {
              long l = num.longValue();
              //or int i = num.intValue();
              //...
              break;
            }
            case NSNumber.REAL : {
              double d = num.doubleValue();
              //...
              break;
            }
          }
        }
        // else...
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }


#### On Android

Put your property list files into the project folder _res/raw_ to mark them as resource files. Then you can create an `InputStream` for that resource and pass it to the `PropertyListParser`.

In this example your property list file is called _properties.plist_.

    try {
      InputStream is = getResources().openRawResource(R.raw.properties);
      NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(is);
      //Continue parsing...
    } catch(Exception ex) {
      //Handle exceptions...
    }

### Writing

    //Creating the root object
    NSDictionary root = new NSDictionary();

    //Creation of an array of the length 2
    NSArray people = new NSArray(2);

    //Creation of the first object to be stored in the array
    NSDictionary person1 = new NSDictionary();
    //The NSDictionary will automatically wrap strings, numbers and dates in the respective NSObject subclasses
    person1.put("Name", "Peter"); //This will become a NSString
    //Use the Java Calendar class to get a Date object
    Calendar cal = Calendar.getInstance();
    cal.set(2011, 1, 13, 9, 28);
    person1.put("RegistrationDate", cal.getTime()); //This will become a NSDate
    person1.put("Age", 23); //This will become a NSNumber
    person1.put("Photo", new NSData(new File("peter.jpg")));

    //Creation of the second object to be stored in the array
    NSDictionary person2 = new NSDictionary();
    person2.put("Name", "Lisa");
    person2.put("Age", 42);
    person2.put("RegistrationDate", new NSDate("2010-09-23T12:32:42Z"));
    person2.put("Photo", new NSData(new File("lisa.jpg")));

    //Put the objects into the array
    people.setValue(0, person1);
    people.setValue(1, person2);

    //Put the array into the property list
    root.put("People", people);

    //Save the propery list
    XMLPropertyListWriter.write(root, new File("people.plist"));
