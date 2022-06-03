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
package org.apache.axiom.core.stream.util;

import org.apache.axiom.core.stream.CharacterData;

public class CharacterDataAccumulator {
    private Object content;

    public void append(Object data) {
        if (content == null) {
            content = data;
        } else {
            StringBuilder buffer;
            if (content instanceof StringBuilder) {
                buffer = (StringBuilder) content;
            } else {
                if (content instanceof CharacterData) {
                    buffer = new StringBuilder();
                    ((CharacterData) content).appendTo(buffer);
                } else {
                    buffer = new StringBuilder(content.toString());
                }
                content = buffer;
            }
            if (data instanceof CharacterData) {
                ((CharacterData) data).appendTo(buffer);
            } else {
                buffer.append(data);
            }
        }
    }

    public boolean isEmpty() {
        return content == null;
    }

    @Override
    public String toString() {
        return content == null ? "" : content.toString();
    }

    public void clear() {
        content = null;
    }
}
