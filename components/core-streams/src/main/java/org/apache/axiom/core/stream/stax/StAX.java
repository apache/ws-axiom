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
package org.apache.axiom.core.stream.stax;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.stream.NullXmlHandler;
import org.apache.axiom.core.stream.serializer.Serializer;
import org.apache.axiom.core.stream.stax.push.input.XmlHandlerStreamWriter;

public final class StAX {
    private StAX() {}

    public static XMLStreamWriter createNullXMLStreamWriter() {
        return new XmlHandlerStreamWriter(NullXmlHandler.INSTANCE, null, null);
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding) {
        Serializer serializer = new Serializer(out, encoding);
        return new XmlHandlerStreamWriter(serializer, serializer, null);
    }
}
