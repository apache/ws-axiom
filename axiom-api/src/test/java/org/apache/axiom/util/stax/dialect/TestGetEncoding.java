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

import static org.junit.Assert.assertThrows;

import java.io.ByteArrayInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class TestGetEncoding extends DialectTestCase {
    public TestGetEncoding(StAXImplementationAdapter staxImpl) {
        super(staxImpl);
    }

    @Override
    protected void runTest() throws Throwable {
        XMLInputFactory factory = staxImpl.newNormalizedXMLInputFactory();
        XMLStreamReader reader =
                factory.createXMLStreamReader(
                        new ByteArrayInputStream(
                                "<?xml version='1.0' encoding='iso-8859-15'?><root/>"
                                        .getBytes("iso-8859-15")));
        assertEquals("iso-8859-15", reader.getEncoding());
        reader.next();
        assertThrows(IllegalStateException.class, reader::getEncoding);
        reader.close();
    }
}
