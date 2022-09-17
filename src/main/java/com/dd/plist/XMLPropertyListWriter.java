/*
 * plist - An open source library to parse and generate property lists
 * Copyright (C) 2022 Daniel Dreibrodt
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Writes property lists in the XML format.
 *
 * @author Daniel Dreibrodt
 */
public class XMLPropertyListWriter {
    /**
     * Saves a property list with the given object as root into an XML file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void write(NSObject root, File out) throws IOException {
        Objects.requireNonNull(root, "The root object is null.");

        File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }

        write(root, out.toPath());
    }

    /**
     * Saves a property list with the given object as root into an XML file.
     *
     * @param root The root object.
     * @param path  The output file path.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void write(NSObject root, Path path) throws IOException {
        try (OutputStream fileOutputStream = Files.newOutputStream(path)) {
            write(root, fileOutputStream);
        }
    }

    /**
     * Saves a property list with the given object as root in XML format into an output stream.
     * This method does not close the specified output stream.
     *
     * @param root The root object.
     * @param out  The output stream.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void write(NSObject root, OutputStream out) throws IOException {
        Objects.requireNonNull(root, "The root object is null.");

        OutputStreamWriter w = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        w.write(root.toXMLPropertyList());
        w.flush();
    }
}
