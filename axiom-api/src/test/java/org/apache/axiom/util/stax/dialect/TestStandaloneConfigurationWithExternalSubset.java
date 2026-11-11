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
package org.apache.axiom.util.stax.dialect;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.util.StAXParserConfiguration;

/**
 * Tests that a parser configured with {@link StAXParserConfiguration#STANDALONE} processes a
 * document with an external DTD subset without attempting to load that subset.
 */
public class TestStandaloneConfigurationWithExternalSubset extends DialectTestCase {
    @Override
    public void execute() throws Throwable {
        XMLInputFactory factory = StAXParserConfiguration.STANDALONE.configure(
                staxImpl.newNormalizedXMLInputFactory(), staxImpl.getDialect());
        DummyHTTPServer server = new DummyHTTPServer();
        server.start();
        try {
            XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(
                    "<?xml version='1.0'?><!DOCTYPE root SYSTEM '" + server.getBaseURL() + "dummy.dtd'><root/>"));
            boolean reachedDocumentElement = false;
            try {
                while (reader.hasNext()) {
                    if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                        reachedDocumentElement = true;
                    }
                }
            } finally {
                reader.close();
            }
            assertThat(reachedDocumentElement).isTrue();
            // Assert that the parser didn't try to load the external DTD subset
            assertThat(server.isRequestReceived()).isFalse();
        } finally {
            server.stop();
        }
    }
}
