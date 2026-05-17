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

import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class TestSetPrefixScope extends DialectTestCase {
    @Override
    public void runTest() throws Throwable {
        XMLOutputFactory factory = staxImpl.newNormalizedXMLOutputFactory();
        XMLStreamWriter writer = factory.createXMLStreamWriter(new ByteArrayOutputStream());
        writer.writeStartDocument();
        writer.writeStartElement("root");
        writer.setPrefix("p", "urn:ns");
        writer.writeStartElement("child");
        assertThat(writer.getPrefix("urn:ns")).isEqualTo("p");
        writer.writeEndElement();
        assertThat(writer.getPrefix("urn:ns")).isEqualTo("p");
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
    }
}
