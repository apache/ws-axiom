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
package org.apache.axiom.datatype;

import java.text.ParseException;

import org.w3c.dom.Element;

public final class DOMHelper {
    private DOMHelper() {}
    
    public static <T> T getValue(Element element, Type<T> type) throws ParseException {
        // TODO: using getTextContent here is actually incorrect because it extracts text recursively
        return type.parse(element.getTextContent(), DOMContextAccessor.INSTANCE, element, null);
    }
    
    public static <T> void setValue(Element element, Type<T> type, T value) {
        element.setTextContent(type.format(value, DOMContextAccessor.INSTANCE, element, null));
    }
}
