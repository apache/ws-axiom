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

public interface Normalizer<T> {
    Normalizer<String> LOWER_CASE =
            new Normalizer<String>() {
                @Override
                public String normalize(String value) {
                    return value == null ? null : value.toLowerCase(Locale.ENGLISH);
                }
            };

    Normalizer<String> DTD =
            new Normalizer<String>() {
                @Override
                public String normalize(String content) throws Exception {
                    if (content == null || content.trim().length() == 0) {
                        return null;
                    } else {
                        return DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder()
                                .parse(
                                        new InputSource(
                                                new StringReader(
                                                        "<!DOCTYPE root ["
                                                                + content
                                                                + "]><root/>")))
                                .getDoctype()
                                .getInternalSubset();
                    }
                }
            };

    Normalizer<String> EMPTY_STRING_TO_NULL =
            new Normalizer<String>() {
                @Override
                public String normalize(String value) {
                    return "".equals(value) ? null : value;
                }
            };

    T normalize(T value) throws Exception;
}
