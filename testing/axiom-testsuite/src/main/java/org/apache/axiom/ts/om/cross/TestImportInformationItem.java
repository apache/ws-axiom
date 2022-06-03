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
package org.apache.axiom.ts.om.cross;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.om.XMLSampleAdapter;
import org.apache.axiom.ts.xml.XMLSample;

public class TestImportInformationItem extends CrossOMTestCase {
    private final XMLSample file;

    public TestImportInformationItem(
            OMMetaFactory metaFactory, OMMetaFactory altMetaFactory, XMLSample file) {
        super(metaFactory, altMetaFactory);
        this.file = file;
        addTestParameter("file", file.getName());
    }

    @Override
    protected void runTest() throws Throwable {
        OMDocument original = file.getAdapter(XMLSampleAdapter.class).getDocument(metaFactory);
        assertAbout(xml())
                .that(
                        xml(
                                OMDocument.class,
                                (OMDocument)
                                        metaFactory.getOMFactory().importInformationItem(original)))
                .hasSameContentAs(xml(OMDocument.class, original));
    }
}
