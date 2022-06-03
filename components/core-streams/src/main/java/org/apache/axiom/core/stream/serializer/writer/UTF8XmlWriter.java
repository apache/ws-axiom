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
package org.apache.axiom.core.stream.serializer.writer;

import java.io.IOException;
import java.io.OutputStream;

final class UTF8XmlWriter extends ASCIICompatibleXmlWriter {
    UTF8XmlWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void setUnmappableCharacterHandler(
            UnmappableCharacterHandler unmappableCharacterHandler) {
        // There are no unmappable characters in UTF-8
    }

    @Override
    protected void writeNonASCIICharacter(int codePoint) throws IOException {
        if (codePoint < 0x800) {
            writeByte((byte) (0xc0 + (codePoint >> 6)));
            writeByte((byte) (0x80 + (codePoint & 0x3f)));
        } else if (codePoint < 0x10000) {
            writeByte((byte) (0xe0 + (codePoint >> 12)));
            writeByte((byte) (0x80 + ((codePoint >> 6) & 0x3f)));
            writeByte((byte) (0x80 + (codePoint & 0x3f)));
        } else {
            writeByte((byte) (0xf0 + (codePoint >> 18)));
            writeByte((byte) (0x80 + ((codePoint >> 12) & 0x3f)));
            writeByte((byte) (0x80 + ((codePoint >> 6) & 0x3f)));
            writeByte((byte) (0x80 + (codePoint & 0x3f)));
        }
    }
}
