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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.util.StAXParserConfiguration;

/**
 * Tests that a parser configured with {@link StAXParserConfiguration#STANDALONE} never attempts to
 * load an external parsed entity. Depending on the parser, the entity reference is either replaced
 * by empty content or rejected with an exception (Woodstox); both outcomes are acceptable.
 */
public class TestStandaloneConfigurationWithExternalEntity extends DialectTestCase {
    @Override
    public void execute() throws Throwable {
        XMLInputFactory factory = StAXParserConfiguration.STANDALONE.configure(
                staxImpl.newNormalizedXMLInputFactory(), staxImpl.getDialect());
        DummyHTTPServer server = new DummyHTTPServer();
        server.start();
        try {
            StringBuilder text = new StringBuilder();
            boolean gotException = false;
            try {
                XMLStreamReader reader = factory.createXMLStreamReader(new StringReader("<?xml version='1.0'?>"
                        + "<!DOCTYPE root [<!ENTITY ext SYSTEM '" + server.getBaseURL() + "entity.xml'>]>"
                        + "<root>a&ext;b</root>"));
                try {
                    while (reader.hasNext()) {
                        if (reader.next() == XMLStreamConstants.CHARACTERS) {
                            text.append(reader.getText());
                        }
                    }
                } finally {
                    reader.close();
                }
            } catch (XMLStreamException | RuntimeException ex) {
                gotException = true;
            }
            // Assert that the parser didn't try to load the external entity
            assertThat(server.isRequestReceived()).isFalse();
            if (!gotException) {
                // The reference to the external entity must have been replaced by empty content
                assertThat(text.toString()).isEqualTo("ab");
            }
        } finally {
            server.stop();
        }
    }
}
