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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests that {@link OMSourcedElement} calls {@link XMLStreamReader#close()} on the {@link
 * XMLStreamReader} returned by {@link OMDataSource#getReader()} when the element is completely
 * built.
 */
public class TestCloseOnComplete implements Executable {
    @Inject
    private OMFactory factory;

    @Override
    public void execute() throws Throwable {
        PullOMDataSource ds = new PullOMDataSource("<root><a/></root>");
        OMSourcedElement element = factory.createOMElement(ds);
        OMNode child = element.getFirstOMChild();
        assertThat(element.isComplete()).isFalse();
        assertThat(ds.hasUnclosedReaders()).isTrue();
        child.getNextOMSibling();
        assertThat(element.isComplete()).isTrue();
        assertThat(ds.hasUnclosedReaders()).isFalse();
    }
}
