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

import java.nio.charset.StandardCharsets;

import javax.xml.namespace.QName;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.blob.Blobs;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.StringOMDataSource;
import org.apache.axiom.om.ds.WrappedTextNodeOMDataSource;
import org.apache.axiom.om.ds.WrappedTextNodeOMDataSourceFromBlob;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetObject extends AxiomTestCase {
    public TestGetObject(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        Blob blob = Blobs.createBlob("test".getBytes(StandardCharsets.UTF_8));
        OMSourcedElement element =
                factory.createOMElement(
                        new WrappedTextNodeOMDataSourceFromBlob(
                                new QName("wrapper"), blob, StandardCharsets.UTF_8));
        // getObject returns null if the data source is not of the expected type
        assertNull(element.getObject(StringOMDataSource.class));
        // Test with the right data source type
        assertSame(blob, element.getObject(WrappedTextNodeOMDataSourceFromBlob.class));
        assertSame(blob, element.getObject(WrappedTextNodeOMDataSource.class));
        // Now modify the content of the element
        factory.createOMComment(element, "comment");
        assertNull(element.getObject(WrappedTextNodeOMDataSourceFromBlob.class));
    }
}
