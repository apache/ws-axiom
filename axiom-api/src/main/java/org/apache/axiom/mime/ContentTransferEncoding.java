/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axiom.mime;

import java.io.OutputStream;

import org.apache.axiom.util.base64.Base64EncodingOutputStream;

/**
 * Represents a MIME content transfer encoding.
 */
public abstract class ContentTransferEncoding {
    private static class Identity extends ContentTransferEncoding {
        Identity(String name) {
            super(name);
        }

        @Override
        public OutputStream encode(OutputStream out) {
            return out;
        }
    }

    /**
     * The {@code 8bit} content transfer encoding.
     */
    public static final ContentTransferEncoding EIGHT_BIT = new Identity("8bit");

    /**
     * The {@code binary} content transfer encoding.
     */
    public static final ContentTransferEncoding BINARY = new Identity("binary");

    /**
     * The {@code base64} content transfer encoding.
     */
    public static final ContentTransferEncoding BASE64 = new ContentTransferEncoding("base64") {
        @Override
        public OutputStream encode(OutputStream out) {
            return new Base64EncodingOutputStream(out);
        }
    };

    private final String name;

    public ContentTransferEncoding(String name) {
        this.name = name;
    }

    @Override
    public final String toString() {
        return name;
    }

    /**
     * Wrap the given output stream to apply the content transfer encoding.
     * 
     * @param out the output stream to wrap
     * @return the wrapped output stream
     */
    public abstract OutputStream encode(OutputStream out);
}
