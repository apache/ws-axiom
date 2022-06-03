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
package org.apache.axiom.core.stream;

import org.junit.Test;

public class NamespaceRepairingFilterTest {
    @Test(expected = ConflictingNamespaceDeclarationException.class)
    public void testNamespaceDeclarationConflictingWithElementName() throws StreamException {
        XmlHandler handler =
                new NamespaceRepairingFilter(null, false)
                        .createFilterHandler(NullXmlHandler.INSTANCE);
        handler.startFragment();
        handler.startElement("urn:ns1", "test", "p");
        handler.processNamespaceDeclaration("p", "urn:ns2");
        handler.attributesCompleted();
        handler.endElement();
        handler.completed();
    }
}
