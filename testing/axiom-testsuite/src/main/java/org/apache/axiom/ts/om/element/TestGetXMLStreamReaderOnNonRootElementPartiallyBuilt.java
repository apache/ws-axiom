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
package org.apache.axiom.ts.om.element;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Iterator;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.util.stax.debug.XMLStreamReaderValidator;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests that {@link OMContainer#getXMLStreamReader(boolean)} produces the correct sequence of
 * events when called on an {@link OMElement} that is not the root element and that may be partially
 * built.
 */
public class TestGetXMLStreamReaderOnNonRootElementPartiallyBuilt implements Executable {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("cache")
    private boolean cache;

    @Inject
    @Named("build")
    private int build;

    @Override
    public void execute() throws Throwable {
        OMElement root =
                AXIOMUtil.stringToOM(factory, "<root><child><emptyElement/><element>content</element></child></root>");
        OMElement child = (OMElement) root.getFirstOMChild();

        // Partially build the tree
        if (build > 0) {
            Iterator<OMNode> it = root.getDescendants(false);
            for (int i = 0; i < build; i++) {
                it.next();
            }
        }

        XMLStreamReader reader = new XMLStreamReaderValidator(child.getXMLStreamReader(cache), true);
        while (reader.hasNext()) {
            reader.next();
        }
    }
}
