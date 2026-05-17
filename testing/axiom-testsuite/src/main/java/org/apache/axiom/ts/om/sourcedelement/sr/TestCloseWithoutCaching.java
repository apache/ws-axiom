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
package org.apache.axiom.ts.om.sourcedelement.sr;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.ds.AbstractPullOMDataSource;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

/**
 * Tests that calling {@link XMLStreamReader#close()} on an {@link XMLStreamReader} returned by
 * {@link OMContainer#getXMLStreamReaderWithoutCaching()} for an OM tree containing an {@link
 * AbstractPullOMDataSource} closes all readers requested from the data source.
 */
public class TestCloseWithoutCaching implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("events")
    private int events;

    @Override
    public void runTest() throws Throwable {
        OMElement root = factory.createOMElement("root", null);
        PullOMDataSource ds = new PullOMDataSource("<child>content</child>");
        root.addChild(factory.createOMElement(ds));
        XMLStreamReader reader = root.getXMLStreamReaderWithoutCaching();
        for (int i = 0; i < events; i++) {
            reader.next();
        }
        reader.close();
        assertThat(ds.hasUnclosedReaders()).isFalse();
    }
}
