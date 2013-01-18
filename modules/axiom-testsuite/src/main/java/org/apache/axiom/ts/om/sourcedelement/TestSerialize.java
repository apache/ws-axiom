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

import java.io.StringReader;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.strategy.ExpansionStrategy;
import org.apache.axiom.ts.strategy.serialization.SerializationStrategy;
import org.apache.axiom.ts.strategy.serialization.XML;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.InputSource;

/**
 * Tests various ways to serialize an {@link OMSourcedElement}.
 */
public class TestSerialize extends AxiomTestCase {
    private final boolean destructive;
    private final boolean orphan;
    private final ExpansionStrategy expansionStrategy;
    private final SerializationStrategy serializationStrategy;
    private final boolean serializeParent;
    private final int count;
    
    /**
     * Constructor.
     * 
     * @param metaFactory
     *            the meta factory for the implementation to be tested
     * @param destructive
     *            specifies if the {@link OMDataSource} is destructive or not
     * @param orphan
     *            specifies if the test is to be executed on an {@link OMSourcedElement} that has a
     *            parent or not
     * @param serializationStrategy
     *            the serialization strategy to test
     * @param serializeParent
     *            specifies if the parent of the {@link OMSourcedElement} should be serialized
     *            instead of the {@link OMSourcedElement} itself
     * @param count
     *            the number of times the {@link OMSourcedElement} will be serialized; the only
     *            meaningful values are 1 and 2
     */
    public TestSerialize(OMMetaFactory metaFactory, boolean destructive, boolean orphan,
            ExpansionStrategy expansionStrategy, SerializationStrategy serializationStrategy,
            boolean serializeParent, int count) {
        super(metaFactory);
        this.destructive = destructive;
        this.orphan = orphan;
        this.expansionStrategy = expansionStrategy;
        this.serializationStrategy = serializationStrategy;
        this.serializeParent = serializeParent;
        this.count = count;
        addTestProperty("destructive", String.valueOf(destructive));
        addTestProperty("orphan", String.valueOf(orphan));
        expansionStrategy.addTestProperties(this);
        serializationStrategy.addTestProperties(this);
        addTestProperty("serializeParent", String.valueOf(serializeParent));
        addTestProperty("count", String.valueOf(count));
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element = TestDocument.DOCUMENT1.createOMSourcedElement(factory, destructive);
        TestDataSource ds = (TestDataSource)element.getDataSource();
        OMElement parent;
        if (orphan) {
            parent = null;
        } else {
            parent = factory.createOMElement("parent", null);
            parent.addChild(element);
        }
        expansionStrategy.apply(element);
        boolean cache = serializationStrategy.isCaching();
        boolean consuming = !cache && expansionStrategy.isConsumedAfterSerializationWithoutCaching(destructive);
        String expectedXML = TestDocument.DOCUMENT1.getContent();
        if (serializeParent) {
            OMElement expected = factory.createOMElement("parent", null);
            expected.addChild(OMXMLBuilderFactory.createOMBuilder(factory, new StringReader(expectedXML)).getDocumentElement());
            expectedXML = expected.toString();
        }
        for (int iteration=0; iteration<count; iteration++) {
            boolean expectException = iteration != 0 && consuming;
            XML result;
            try {
                result = serializationStrategy.serialize(serializeParent ? parent : element);
                if (expectException) {
                    fail("Expected exception");
                }
            } catch (Exception ex) {
                if (!expectException) {
                    throw ex;
                } else {
                    continue;
                }
            }
            XMLAssert.assertXMLIdentical(XMLUnit.compareXML(new InputSource(new StringReader(expectedXML)), result.getInputSource()), true);
            // If the underlying OMDataSource is non destructive, the expansion status should not have been
            // changed during serialization. If it is destructive and caching is enabled, then
            // the sourced element should be expanded.
            if (expansionStrategy.isExpandedAfterSerialization(destructive, cache)) {
                assertTrue(element.isExpanded());
                assertEquals(!consuming, element.isComplete());
            } else {
                assertFalse(element.isExpanded());
            }
            if (parent != null) {
                // Operations on the OMSourcedElement should have no impact on the parent
                assertTrue(parent.isComplete());
            }
        }
        assertFalse(ds.hasUnclosedReaders());
    }
}
