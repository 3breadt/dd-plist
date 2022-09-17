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
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Writes property lists in the ASCII format. Supports Apple OS X/iOS and GnuStep/NeXTSTEP format.
 *
 * @author Daniel Dreibrodt
 */
public final class ASCIIPropertyListWriter {
    /**
     * Prevents instantiation.
     */
    private ASCIIPropertyListWriter() {
        /* empty */
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void write(NSDictionary root, File out) throws IOException {
        File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }

        write(root, out.toPath());
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param path The output file path.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void write(NSDictionary root, Path path) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.US_ASCII)) {
            w.write(root.toASCIIPropertyList());
        }
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void write(NSArray root, File out) throws IOException {
        Objects.requireNonNull(root, "The root object is null.");

        File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }

        write(root, out.toPath());
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param path The output file path.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void write(NSArray root, Path path) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.US_ASCII)) {
            w.write(root.toASCIIPropertyList());
        }
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void writeGnuStep(NSDictionary root, File out) throws IOException {
        Objects.requireNonNull(root, "The root object is null.");

        File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }

        writeGnuStep(root, out.toPath());
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param path The output file path.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void writeGnuStep(NSDictionary root, Path path) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.US_ASCII)) {
            w.write(root.toGnuStepASCIIPropertyList());
        }
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param out  The output file. If the output file's parent directory does not exist, it will be created.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void writeGnuStep(NSArray root, File out) throws IOException {
        Objects.requireNonNull(root, "The root object is null.");

        File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }

        writeGnuStep(root, out.toPath());
    }

    /**
     * Saves a property list with the given object as root into an ASCII file.
     *
     * @param root The root object.
     * @param path The output file path.
     * @throws IOException If an error occurs during the writing process.
     */
    public static void writeGnuStep(NSArray root, Path path) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.US_ASCII)) {
            w.write(root.toGnuStepASCIIPropertyList());
        }
    }
}
