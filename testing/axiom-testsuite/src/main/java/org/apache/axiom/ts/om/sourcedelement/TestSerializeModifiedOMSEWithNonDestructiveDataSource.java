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
package org.apache.axiom.ts.om.sourcedelement;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.ds.StringOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;

public class TestSerializeModifiedOMSEWithNonDestructiveDataSource extends AxiomTestCase {
    public TestSerializeModifiedOMSEWithNonDestructiveDataSource(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMDataSourceExt ds = new StringOMDataSource("<element><child/></element>");
        assertFalse(ds.isDestructiveWrite());

        OMFactory f = metaFactory.getOMFactory();
        OMElement element = f.createOMElement(ds, "element", null);

        element.getFirstElement().setText("TEST");

        StringWriter sw = new StringWriter();
        element.serialize(sw);
        assertTrue(sw.toString().indexOf("TEST") != -1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        element.serialize(baos);
        assertTrue(new String(baos.toByteArray(), StandardCharsets.UTF_8).indexOf("TEST") != -1);

        assertTrue(element.toString().indexOf("TEST") != -1);
    }
}
