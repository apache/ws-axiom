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
package org.apache.axiom.testutils.stax;

import java.io.StringReader;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

public interface Normalizer {
    Normalizer IDENTITY = new Normalizer() {
        public Object normalize(Object value) {
            return value;
        }
    };

    Normalizer LOWER_CASE = new Normalizer() {
        public Object normalize(Object value) {
            return value == null ? null : ((String)value).toLowerCase(Locale.ENGLISH);
        }
    };
    
    Normalizer DTD = new Normalizer() {
        public Object normalize(Object value) throws Exception {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new InputSource(new StringReader("<!DOCTYPE root [" + value + "]><root/>"))).getDoctype().getInternalSubset();
        }
    };
    
    Normalizer EMPTY_STRING_TO_NULL = new Normalizer() {
        public Object normalize(Object value) {
            return "".equals(value) ? null : value;
        }
    };

    Object normalize(Object value) throws Exception;
}
