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
package org.apache.axiom.ts.om.builder;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.dom.DOMSource;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.ConformanceTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.xml.XMLSample;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TestCreateOMBuilderFromDOM extends ConformanceTestCase {
    @Inject
    private OMFactory factory;

    private final DOMImplementation implementation;
    private final Boolean expandEntityReferences;

    @Inject
    public TestCreateOMBuilderFromDOM(
            XMLSample file,
            DOMImplementation implementation,
            @Named("expandEntityReferences") @Nullable Boolean expandEntityReferences) {
        super(file);
        this.implementation = implementation;
        this.expandEntityReferences = expandEntityReferences;
    }

    private Document loadDocument(boolean expandEntityReferences) throws Exception {
        return implementation.parse(new InputSource(file.getUrl().toString()), expandEntityReferences);
    }

    @Override
    public void execute() throws Throwable {
        // We never expand entity references during parsing, but we may do this later when
        // converting DOM to OM.
        Document document = loadDocument(false);
        OMXMLParserWrapper builder;
        if (expandEntityReferences == null) {
            builder = OMXMLBuilderFactory.createOMBuilder(factory, new DOMSource(document));
        } else {
            builder = OMXMLBuilderFactory.createOMBuilder(factory, document, expandEntityReferences.booleanValue());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        builder.getDocument().serialize(baos);
        InputSource actual = new InputSource();
        actual.setByteStream(new ByteArrayInputStream(baos.toByteArray()));
        actual.setSystemId(file.getUrl().toString());
        assertAbout(xml())
                .that(actual)
                .ignoringWhitespaceInPrologAndEpilog()
                .hasSameContentAs(loadDocument(expandEntityReferences == null || expandEntityReferences));
    }
}
