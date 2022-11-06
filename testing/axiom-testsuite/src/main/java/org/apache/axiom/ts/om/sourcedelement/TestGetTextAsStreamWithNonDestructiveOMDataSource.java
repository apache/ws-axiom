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

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.activation.DataSource;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.activation.WrappedTextNodeOMDataSourceFromDataSource;
import org.apache.axiom.testutils.activation.RandomDataSource;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetTextAsStreamWithNonDestructiveOMDataSource extends AxiomTestCase {
    public TestGetTextAsStreamWithNonDestructiveOMDataSource(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        DataSource ds = new RandomDataSource(445566, 32, 128, 20000000);
        QName qname = new QName("a");
        Charset cs = Charset.forName("ascii");
        OMSourcedElement element =
                factory.createOMElement(
                        new WrappedTextNodeOMDataSourceFromDataSource(qname, ds, cs), qname);
        Reader in = element.getTextAsStream(true);
        assertFalse(in instanceof StringReader);
        IOTestUtils.compareStreams(new InputStreamReader(ds.getInputStream(), cs), in);
        assertFalse(element.isExpanded());
    }
}
