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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * An input stream that filters the Byte Order Mark from the input.
 */
class ByteOrderMarkFilterInputStream extends FilterInputStream {
    private boolean closeStream;
    private boolean readingBom = true;
    private final Queue<Integer> consumedBytes = new LinkedList<>();

    /**
     * Creates a {@code ByteOrderMarkFilterInputStream} instance.
     *
     * @param in the underlying input stream.
     * @param closeStream If set to {@code false} the original input stream is not closed when the filtered stream is closed.
     */
    public ByteOrderMarkFilterInputStream(InputStream in, boolean closeStream) {
        super(in);
        this.closeStream = closeStream;
    }

    @Override
    public int read() throws IOException {
        if (this.readingBom) {
            int b;
            ByteOrderMarkReader bomReader = new ByteOrderMarkReader();
            do {
                b = super.read();
                this.consumedBytes.add(b);
                this.readingBom = bomReader.readByte(b);
            }
            while (this.readingBom);

            if (bomReader.getDetectedCharset() != null) {
                this.consumedBytes.clear();
                return b;
            }
        }

        if (this.consumedBytes.size() > 0) {
            return this.consumedBytes.poll();
        }

        return super.read();
    }

    @Override public void close() throws IOException {
        if (this.closeStream) {
            super.close();
        }
    }
}
