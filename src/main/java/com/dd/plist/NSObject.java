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

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

/**
 * Abstract interface for an object contained in a property list.
 * The names and functions of the various objects orient themselves towards Apple's Cocoa API.
 *
 * @author Daniel Dreibrodt
 */
public abstract class NSObject implements Cloneable {

    /**
     * The newline character used for generating the XML output.
     * This constant will be different depending on the operating system on
     * which you use this library.
     */
    final static String NEWLINE = System.getProperty("line.separator");

    /**
     * The maximum length of the text lines to be used when generating
     * ASCII property lists. But this number is only a guideline it is not
     * guaranteed that it will not be overstepped.
     */
    final static int ASCII_LINE_LENGTH = 80;

    /**
     * The indentation character used for generating the XML output. This is the
     * tabulator character.
     */
    private final static String INDENT = "\t";

    /**
     * Creates and returns a deep copy of this instance.
     * @return A clone of this instance.
     */
    @Override
    public abstract NSObject clone();

    /**
     * Generates the XML representation of the object (without XML headers or enclosing plist-tags).
     *
     * @param xml   The {@link StringBuilder} onto which the XML representation is appended.
     * @param level The indentation level of the object.
     */
    abstract void toXML(StringBuilder xml, int level);

    /**
     * Assigns IDs to all the objects in this NSObject subtree.
     *
     * @param out The writer object that handles the binary serialization.
     */
    void assignIDs(BinaryPropertyListWriter out) {
        out.assignID(this);
    }

    /**
     * Generates the binary representation of the object.
     *
     * @param out The output stream to serialize the object to.
     * @throws java.io.IOException If an I/O error occurs while writing to the stream or the object structure contains
     *                             data that cannot be saved.
     */
    abstract void toBinary(BinaryPropertyListWriter out) throws IOException;

    /**
     * Generates a valid XML property list including headers using this object as root.
     *
     * @return The XML representation of the property list including XML header and doctype information.
     */
    public String toXMLPropertyList() {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append(NSObject.NEWLINE)
                .append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">")
                .append(NSObject.NEWLINE)
                .append("<plist version=\"1.0\">")
                .append(NSObject.NEWLINE);
        this.toXML(xml, 0);
        xml.append(NSObject.NEWLINE).append("</plist>");
        return xml.toString();
    }

    /**
     * Generates the ASCII representation of this object.
     * The generated ASCII representation does not end with a newline.
     * Complies with the <a href="https://developer.apple.com/library/content/documentation/Cocoa/Conceptual/PropertyLists/OldStylePlists/OldStylePLists.html" target="_blank">Old-Style ASCII Property Lists definition</a>.
     *
     * @param ascii The {@link StringBuilder} onto which the ASCII representation is appended.
     * @param level The indentation level of the object.
     */
    protected abstract void toASCII(StringBuilder ascii, int level);

    /**
     * Generates the ASCII representation of this object in the GnuStep format.
     * The generated ASCII representation does not end with a newline.
     *
     * @param ascii The {@link StringBuilder} onto which the ASCII representation is appended.
     * @param level The indentation level of the object.
     */
    protected abstract void toASCIIGnuStep(StringBuilder ascii, int level);

    /**
     * Helper method that adds correct indentation to the xml output.
     * Calling this method will add <code>level</code> number of tab characters
     * to the <code>xml</code> string.
     *
     * @param xml   The {@link StringBuilder} onto which the XML representation is appended.
     * @param level The level of indentation.
     */
    void indent(StringBuilder xml, int level) {
        for (int i = 0; i < level; i++)
            xml.append(INDENT);
    }

    /**
     * Converts this NSObject into an equivalent object of the Java Runtime Environment.
     * <ul>
     * <li>{@link NSArray} objects are converted to arrays.</li>
     * <li>{@link NSDictionary} objects are converted to objects extending the {@link java.util.Map} class.</li>
     * <li>{@link NSSet} objects are converted to objects extending the {@link java.util.Set} class.</li>
     * <li>{@link NSNumber} objects are converted to primitive number values (int, long, double or boolean).</li>
     * <li>{@link NSString} objects are converted to {@link String} objects.</li>
     * <li>{@link NSData} objects are converted to byte arrays.</li>
     * <li>{@link NSDate} objects are converted to {@link java.util.Date} objects.</li>
     * <li>{@link UID} objects are converted to byte arrays.</li>
     * </ul>
     * @return A native java object representing this NSObject's value.
     */
    public Object toJavaObject() {
        if(this instanceof NSArray) {
            return this.deserializeArray();
        } else if (this instanceof NSDictionary) {
            return this.deserializeMap();
        } else if(this instanceof NSSet) {
            return this.deserializeSet();
        } else if(this instanceof NSNumber) {
            return this.deserializeNumber();
        } else if(this instanceof NSString) {
            return ((NSString)this).getContent();
        } else if(this instanceof NSData) {
            return ((NSData)this).bytes();
        } else if(this instanceof NSDate) {
            return ((NSDate)this).getDate();
        } else if(this instanceof UID) {
            return ((UID)this).getBytes();
        } else {
            return this;
        }
    }

    /**
     * Converts this NSObject into an object of the specified class.
     * @param <T> The target object type.
     * @param clazz The target class.
     * @return A new instance of the specified class, deserialized from this NSObject.
     * @throws IllegalArgumentException If the specified class cannot be deserialized from this NSObject.
     */
    @SuppressWarnings("unchecked")
    public <T> T toJavaObject(Class<T> clazz) {
        return (T) this.toJavaObject(this, clazz, null);
    }

    /**
     * Serializes the specified object into an NSObject.
     * Objects which do not have a direct type correspondence to an NSObject type will be serialized as a {@link NSDictionary}.
     * The dictionary will contain the values of all publicly accessible fields and properties.
     * @param object The object to serialize.
     * @return A NSObject instance.
     * @throws IllegalArgumentException If the specified object throws an exception while getting its properties.
     */
    public static NSObject fromJavaObject(Object object) {
        if (object == null) {
            return null;
        }

        if(object instanceof NSObject) {
            return (NSObject)object;
        }

        Class<?> objClass = object.getClass();
        if (objClass.isArray()) {
            //process []
            return fromArray(object, objClass);
        }

        if (isSimple(objClass)) {
            //process simple types
            return fromSimple(object, objClass);
        }

        if (Set.class.isAssignableFrom(objClass)) {
            //process set
            return fromSet((Set<?>) object);
        }

        if (Map.class.isAssignableFrom(objClass)) {
            //process Map
            return fromMap((Map<?, ?>) object);
        }

        if (Collection.class.isAssignableFrom(objClass)) {
            //process collection
            return fromCollection((Collection<?>) object);
        }

        //process pojo
        return fromPojo(object, objClass);
    }

    private static boolean isSimple(Class<?> clazz) {
        return clazz.isPrimitive() ||
                Number.class.isAssignableFrom(clazz) ||
                Boolean.class.isAssignableFrom(clazz) ||
                clazz == String.class ||
                Date.class.isAssignableFrom(clazz);
    }

    private static Object getInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Could not instantiate class " + clazz.getSimpleName());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not instantiate class " + clazz.getSimpleName());
        }
    }

    private static Class<?> getClassForName(String className) {
        int spaceIndex = className.indexOf(' ');
        if(spaceIndex != -1) {
            className = className.substring(spaceIndex + 1);
        }

        if ("double".equals(className)) {
            return double.class;
        }
        if ("float".equals(className)) {
            return float.class;
        }
        if ("int".equals(className)) {
            return int.class;
        }
        if ("long".equals(className)) {
            return long.class;
        }
        if ("short".equals(className)) {
            return short.class;
        }
        if ("boolean".equals(className)) {
            return boolean.class;
        }
        if ("byte".equals(className)) {
            return byte.class;
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not load class " + className, e);
        }
    }

    private static String makeFirstCharLowercase(String input) {
        char[] chars = input.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private Object toJavaObject(NSObject payload, Class<?> clazz, Type[] types) {
        if (clazz.isArray()) {
            //generics and arrays do not mix
            return this.deserializeArray(payload, clazz);
        }

        if (isSimple(clazz)) {
            return deserializeSimple(payload, clazz);
        }

        if (clazz == Object.class && !(payload instanceof NSSet || payload instanceof NSArray)) {
            return deserializeSimple(payload, clazz);
        }

        if (payload instanceof NSSet && Collection.class.isAssignableFrom(clazz)) {
            return this.deserializeCollection(payload, clazz, types);
        }

        if (payload instanceof NSArray && Collection.class.isAssignableFrom(clazz)) {
            return this.deserializeCollection(payload, clazz, types);
        }

        if (payload instanceof NSDictionary) {
            return this.deserializeObject((NSDictionary) payload, clazz, types);
        }

        if (payload instanceof NSData && Collection.class.isAssignableFrom(clazz)) {
            return this.deserializeCollection(payload, clazz, types);
        }

        throw new IllegalArgumentException("Cannot process " + clazz.getSimpleName());
    }

    private Object deserializeObject(NSDictionary payload, Class<?> clazz, Type[] types) {
        Map<String, NSObject> map = payload.getHashMap();

        if (Map.class.isAssignableFrom(clazz)) {
            return this.deserializeMap(clazz, types, map);
        }

        Object result = getInstance(clazz);

        Map<String, Method> getters = new HashMap<String, Method>();
        Map<String, Method> setters = new HashMap<String, Method>();
        for (Method method : clazz.getMethods()) {
            String name = method.getName();
            if (name.startsWith("get")) {
                getters.put(makeFirstCharLowercase(name.substring(3)), method);
            } else if (name.startsWith("set")) {
                setters.put(makeFirstCharLowercase(name.substring(3)), method);
            } else if (name.startsWith("is")) {
                getters.put(makeFirstCharLowercase(name.substring(2)), method);
            }
        }

        for (Map.Entry<String, NSObject> entry : map.entrySet()) {
            Method setter = setters.get(makeFirstCharLowercase(entry.getKey()));
            Method getter = getters.get(makeFirstCharLowercase(entry.getKey()));
            if (setter != null && getter != null) {
                Class<?> elemClass = getter.getReturnType();
                Type[] elemTypes = null;
                Type type = getter.getGenericReturnType();
                if (type instanceof ParameterizedType) {
                    elemTypes = ((ParameterizedType) type).getActualTypeArguments();
                }

                try {
                    setter.invoke(result, this.toJavaObject(entry.getValue(), elemClass, elemTypes));
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Could not access setter " + setter);
                } catch (InvocationTargetException e) {
                    throw new IllegalArgumentException("Could not invoke setter " + setter);
                }

            }
        }
        return result;
    }

    private HashMap<String, Object> deserializeMap() {
        HashMap<String, NSObject> originalMap = ((NSDictionary)this).getHashMap();
        HashMap<String, Object> clonedMap = new HashMap<String, Object>(originalMap.size());
        for(String key:originalMap.keySet()) {
            clonedMap.put(key, originalMap.get(key).toJavaObject());
        }

        return clonedMap;
    }

    private Object deserializeMap(Class<?> clazz, Type[] types, Map<String, NSObject> map) {
        final Map<String, Object> result;

        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            //fallback
            result = new HashMap<String, Object>();
        } else {
            @SuppressWarnings("unchecked")
            Map<String, Object> temp = (Map<String, Object>) getInstance(clazz);
            result = temp;
        }

        Class<?> elemClass = Object.class;
        Type[] elemParams = null;
        if (types != null && types.length > 1) {
            Type elemType = types[1];
            if (elemType instanceof ParameterizedType) {
                elemClass = getClassForName(((ParameterizedType) elemType).getRawType().toString());
                elemParams = ((ParameterizedType) elemType).getActualTypeArguments();
            } else {
                elemClass = getClassForName(elemType.toString());
            }
        }
        for (Map.Entry<String, NSObject> entry : map.entrySet()) {
            result.put(entry.getKey(), this.toJavaObject(entry.getValue(), elemClass, elemParams));
        }

        return result;
    }

    private Object deserializeCollection(NSObject payload, Class<?> clazz, Type[] types) {
        final Collection<Object> result;
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            //try fallback
            if (List.class.isAssignableFrom(clazz)) {
                result = new ArrayList<Object>();
            } else if (Set.class.isAssignableFrom(clazz)) {
                result = new HashSet<Object>();
            } else {
                //we fail
                throw new IllegalArgumentException("Could not find a proper implementation for " + clazz.getSimpleName());
            }
        } else {
            @SuppressWarnings("unchecked")
            Collection<Object> temp = (Collection<Object>) getInstance(clazz);
            result = temp;
        }

        Class<?> elemClass = Object.class;
        Type[] elemTypes = null;
        if (types != null && types.length > 0) {
            if (types[0] instanceof ParameterizedType) {
                elemClass = getClassForName(((ParameterizedType) types[0]).getRawType().toString());
                elemTypes = ((ParameterizedType) types[0]).getActualTypeArguments();
            } else {
                elemClass = getClassForName(types[0].toString());
            }
        }

        if (payload instanceof NSArray) {
            for (NSObject nsObject : ((NSArray) payload).getArray()) {
                result.add(this.toJavaObject(nsObject, elemClass, elemTypes));
            }

            return result;
        }

        if (payload instanceof NSData) {
            if (elemClass != null && elemClass.isAssignableFrom(Byte.class)) {
                for (Byte b : ((NSData) payload).bytes()) {
                    result.add(b);
                }

                return result;
            }
            else {
                throw new IllegalArgumentException("NSData cannot be converted to " + clazz.getName());
            }
        }

        if (payload instanceof NSSet) {
            for(NSObject nsObject : ((NSSet) payload).getSet()) {
                result.add(this.toJavaObject(nsObject, elemClass, elemTypes));
            }

            return result;
        }
        throw new IllegalArgumentException("Unknown NS* type " + payload.getClass().getSimpleName());
    }

    private Object[] deserializeArray() {
        NSObject[] originalArray = ((NSArray)this).getArray();
        Object[] clonedArray = new Object[originalArray.length];
        for(int i = 0; i < originalArray.length; i++) {
            clonedArray[i] = originalArray[i].toJavaObject();
        }

        return clonedArray;
    }

    private Object deserializeArray(NSObject payload, Class<?> clazz) {
        Class<?> elementClass = getClassForName(clazz.getComponentType().getName());

        if (payload instanceof NSArray) {
            NSObject[] array = ((NSArray) payload).getArray();
            Object result = Array.newInstance(elementClass, array.length);
            for (int i = 0; i < array.length; i++) {
                Array.set(result, i, this.toJavaObject(array[i], elementClass, null));
            }
            return result;
        }

        if (payload instanceof NSSet) {
            Set<NSObject> set = ((NSSet) payload).getSet();
            Object result = Array.newInstance(elementClass, set.size());
            int i = 0;
            for (NSObject aSet : set) {
                Array.set(result, i, this.toJavaObject(aSet, elementClass, null));
                i++;
            }
            return result;
        }

        if (payload instanceof NSData) {
            return deserializeData((NSData) payload, elementClass);
        }

        throw new IllegalArgumentException("Unable to map " + payload.getClass().getSimpleName() + " to " + clazz.getName());
    }

    private Set<Object> deserializeSet() {
        Set<NSObject> originalSet = ((NSSet)this).getSet();
        Set<Object> clonedSet;
        if(originalSet instanceof LinkedHashSet) {
            clonedSet = new LinkedHashSet<Object>(originalSet.size());
        } else {
            clonedSet = new TreeSet<Object>();
        }
        for(NSObject o : originalSet) {
            clonedSet.add(o.toJavaObject());
        }
        return clonedSet;
    }

    private static Object deserializeData(NSData payload, Class<?> elementClass) {
        if (elementClass == byte.class) {
            return payload.bytes();
        }

        if (elementClass == Byte.class) {
            byte[] bytes = payload.bytes();
            Object result = Array.newInstance(elementClass, bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                Array.set(result, i, bytes[i]);
            }
            return result;
        }

        throw new IllegalArgumentException("NSData can only be mapped to byte[] or Byte[].");
    }

    private static Object deserializeSimple(NSObject payload, Class<?> clazz) {
        if (payload instanceof NSNumber) {
            return deserializeNumber((NSNumber) payload, clazz);
        }

        if (payload instanceof NSDate) {
            return deserializeDate((NSDate) payload, clazz);
        }

        if (payload instanceof NSString) {
            return ((NSString) payload).getContent();
        }

        throw new IllegalArgumentException("Cannot map " + payload.getClass().getSimpleName() + " to " + clazz.getSimpleName());
    }

    private static Date deserializeDate(NSDate date, Class<?> clazz) {
        if (clazz == Date.class) {
            //short circuit
            return date.getDate();
        }

        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            //fallback
            return date.getDate();
        }

        Date result = (Date) getInstance(clazz);
        result.setTime(date.getDate().getTime());
        return result;
    }

    private Object deserializeNumber() {
        NSNumber num = (NSNumber)this;
        switch(num.type()) {
            case NSNumber.INTEGER : {
                long longVal = num.longValue();
                if(longVal > Integer.MAX_VALUE || longVal < Integer.MIN_VALUE) {
                    return longVal;
                } else {
                    return num.intValue();
                }
            }
            case NSNumber.REAL : {
                return num.doubleValue();
            }
            case NSNumber.BOOLEAN : {
                return num.boolValue();
            }
            default : {
                return num.doubleValue();
            }
        }
    }

    private static Object deserializeNumber(final NSNumber number, Class<?> clazz) {
        if (number.isInteger()) {
            if (clazz == long.class || clazz == Long.class) {
                return number.longValue();
            }

            if (clazz == int.class || clazz == Integer.class) {
                //XXX possible overflow
                return number.intValue();
            }

            if (clazz == short.class || clazz == Short.class) {
                //XXX possible overflow
                return (short) number.intValue();
            }

            if (clazz == byte.class || clazz == Byte.class) {
                //XXX possible overflow
                return (byte) number.intValue();
            }
        }

        if (number.isInteger() || number.isReal()) {
            if (clazz == double.class || clazz == Double.class) {
                return number.doubleValue();
            }

            if (clazz == float.class || clazz == Float.class) {
                //XXX possible overflow
                return number.floatValue();
            }
        }

        if (number.isBoolean()) {
            if (clazz == boolean.class || clazz == Boolean.class) {
                return number.boolValue();
            }
        }

        throw new IllegalArgumentException("Cannot map NSNumber to " + clazz.getSimpleName());
    }

    @SuppressWarnings("ConstantConditions")
    private static NSObject fromSimple(Object object, Class<?> objClass) {
        if (object instanceof Long || objClass == long.class) {
            return new NSNumber((Long) object);
        }

        if (object instanceof Integer || objClass == int.class) {
            return new NSNumber((Integer) object);
        }

        if (object instanceof Short || objClass == short.class) {
            return new NSNumber((Short) object);
        }

        if (object instanceof Byte || objClass == byte.class) {
            return new NSNumber((Byte) object);
        }

        if (object instanceof Double || objClass == double.class) {
            return new NSNumber((Double) object);
        }

        if (object instanceof Float || objClass == float.class) {
            return new NSNumber((Float) object);
        }

        if (object instanceof Boolean || objClass == boolean.class) {
            return new NSNumber((Boolean) object);
        }

        if (object instanceof Date) {
            return new NSDate((Date) object);
        }

        if (objClass == String.class) {
            return new NSString((String) object);
        }

        throw new IllegalArgumentException("Cannot map " + objClass.getSimpleName() + " as a simple type.");
    }

    private static NSDictionary fromPojo(Object object, Class<?> objClass) {
        NSDictionary result = new NSDictionary();

        for (Method method : objClass.getMethods()) {
            if (Modifier.isNative(method.getModifiers()) ||
                    Modifier.isStatic(method.getModifiers()) ||
                    method.getParameterTypes().length != 0) {
                continue;
            }
            String name = method.getName();
            if (name.startsWith("get")) {
                name = makeFirstCharLowercase(name.substring(3));
            } else if (name.startsWith("is")) {
                name = makeFirstCharLowercase(name.substring(2));
            } else {
                ///not a getter
                continue;
            }

            try {
                result.put(name, fromJavaObject(method.invoke(object)));
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Could not access getter " + method.getName());
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException("Could not invoke getter " + method.getName());
            }
        }

        for(Field field : objClass.getFields()) {
            if(Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                result.put(field.getName(), fromJavaObject(field.get(object)));
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Could not access field " + field.getName());
            }
        }

        return result;
    }

    private static NSDictionary fromMap(Map<?, ?> map) {
        NSDictionary result = new NSDictionary();
        for (Map.Entry entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new IllegalArgumentException("Maps need a String key for mapping to NSDictionary.");
            }
            result.put((String) entry.getKey(), fromJavaObject(entry.getValue()));
        }

        return result;
    }

    private static NSObject fromArray(Object object, Class<?> objClass) {
        Class<?> elementClass = objClass.getComponentType();
        if(elementClass == byte.class || elementClass == Byte.class) {
            return fromData(object);
        }

        int size = Array.getLength(object);
        NSObject[] array = new NSObject[size];
        for (int i = 0; i < size; i++) {
            array[i] = fromJavaObject(Array.get(object, i));
        }

        return new NSArray(array);
    }

    private static NSData fromData(Object object) {
        int size = Array.getLength(object);
        byte[] array = new byte[size];
        for (int i = 0; i < size; i++) {
            array[i] = (Byte)(Array.get(object, i));
        }

        return new NSData(array);
    }

    private static NSArray fromCollection(Collection<?> collection) {
        List<NSObject> payload = new ArrayList<NSObject>(collection.size());
        for (Object elem : collection) {
            payload.add(fromJavaObject(elem));
        }

        return new NSArray(payload.toArray(new NSObject[payload.size()]));
    }

    private static NSSet fromSet(Set<?> set) {
        NSSet result = new NSSet();
        for (Object elem : set) {
            result.addObject(fromJavaObject(elem));
        }

        return result;
    }

}
