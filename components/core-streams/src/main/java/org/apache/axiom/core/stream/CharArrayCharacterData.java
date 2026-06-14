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
package org.apache.axiom.core.stream;

import java.io.IOException;
import org.apache.axiom.checker.union.Union;

/**
 * A reusable {@link CharacterData} implementation backed by a {@code char[]} slice. Instances are
 * only valid for the duration of the {@link XmlHandler#processCharacterData} invocation in which
 * they are passed.
 */
@SuppressWarnings({"super.invocation", "inconsistent.constructor.type"})
public final class CharArrayCharacterData implements CharacterData {
    private char[] ch;
    private int start;
    private int length;

    /**
     * Updates the backing char array slice. Must be called before each {@link
     * XmlHandler#processCharacterData} invocation.
     */
    public void set(char[] ch, int start, int length) {
        this.ch = ch;
        this.start = start;
        this.length = length;
    }

    /**
     * Invalidates this instance by clearing the backing array reference. Any subsequent attempt to
     * use this instance will result in a {@link NullPointerException}, making it easier to detect
     * bugs where an {@link XmlHandler} retains a reference beyond the allowed lifetime.
     */
    public void invalidate() {
        this.ch = null;
    }

    @Override
    public String toString() {
        return new String(ch, start, length);
    }

    @Override
    public void writeTo(CharacterDataSink sink) throws IOException {
        sink.getWriter().write(ch, start, length);
    }

    @Override
    public void appendTo(StringBuilder buffer) {
        buffer.append(ch, start, length);
    }

    @Override
    public @Union(types = {String.class, CharacterData.class}) Object retain() {
        return new String(ch, start, length);
    }
}
