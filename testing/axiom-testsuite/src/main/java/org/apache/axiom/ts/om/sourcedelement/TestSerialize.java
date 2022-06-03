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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.AbstractPullOMDataSource;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.ElementContext;
import org.apache.axiom.ts.dimension.ExpansionStrategy;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;
import org.apache.axiom.ts.dimension.serialization.XML;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;
import org.xml.sax.InputSource;

/** Tests various ways to serialize an {@link OMSourcedElement}. */
public class TestSerialize extends AxiomTestCase {
    private final boolean push;
    private final boolean destructive;
    private final ElementContext elementContext;
    private final ExpansionStrategy expansionStrategy;
    private final SerializationStrategy serializationStrategy;
    private final boolean serializeParent;
    private final int count;

    /**
     * Constructor.
     *
     * @param metaFactory the meta factory for the implementation to be tested
     * @param push specifies whether the data source to be used extends {@link
     *     AbstractPullOMDataSource} (<code>false</code>) or {@link AbstractPushOMDataSource} (
     *     <code>true</code>)
     * @param destructive specifies if the {@link OMDataSource} is destructive or not
     * @param elementContext specifies if an how the {@link OMSourcedElement} is to be placed inside
     *     an {@link OMContainer}
     * @param serializationStrategy the serialization strategy to test
     * @param serializeParent specifies if the parent of the {@link OMSourcedElement} should be
     *     serialized instead of the {@link OMSourcedElement} itself
     * @param count the number of times the {@link OMSourcedElement} will be serialized; the only
     *     meaningful values are 1 and 2
     */
    public TestSerialize(
            OMMetaFactory metaFactory,
            boolean push,
            boolean destructive,
            ElementContext elementContext,
            ExpansionStrategy expansionStrategy,
            SerializationStrategy serializationStrategy,
            boolean serializeParent,
            int count) {
        super(metaFactory);
        this.push = push;
        this.destructive = destructive;
        this.elementContext = elementContext;
        this.expansionStrategy = expansionStrategy;
        this.serializationStrategy = serializationStrategy;
        this.serializeParent = serializeParent;
        this.count = count;
        addTestParameter("push", push);
        addTestParameter("destructive", destructive);
        elementContext.addTestParameters(this);
        expansionStrategy.addTestParameters(this);
        serializationStrategy.addTestParameters(this);
        addTestParameter("serializeParent", serializeParent);
        addTestParameter("count", count);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element =
                TestDocument.DOCUMENT1.createOMSourcedElement(factory, push, destructive);
        OMDataSource ds = element.getDataSource();
        OMContainer parent = elementContext.wrap(element);
        boolean parentComplete = parent != null && parent.isComplete();
        expansionStrategy.apply(element);
        boolean consuming =
                expansionStrategy.isConsumedAfterSerialization(
                        push, destructive, serializationStrategy);
        for (int iteration = 0; iteration < count; iteration++) {
            boolean expectException =
                    iteration != 0
                            && (consuming
                                    || serializeParent
                                            && !serializationStrategy.isCaching()
                                            && !parentComplete);
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
            InputSource expectedXML =
                    new InputSource(new StringReader(TestDocument.DOCUMENT1.getContent()));
            if (serializeParent) {
                expectedXML = elementContext.getControl(expectedXML);
            }
            assertAbout(xml()).that(result.getInputSource()).hasSameContentAs(expectedXML);
            // If the underlying OMDataSource is non destructive, the expansion status should not
            // have been changed during serialization. If it is destructive and caching is enabled,
            // then the sourced element should be expanded.
            if (expansionStrategy.isExpandedAfterSerialization(
                    push, destructive, serializationStrategy)) {
                assertTrue(element.isExpanded());
                assertEquals(
                        "OMSourcedElement completion status", !consuming, element.isComplete());
            } else {
                assertFalse(element.isExpanded());
            }
            if (parent != null && !serializeParent) {
                // Operations on the OMSourcedElement should have no impact on the parent
                assertEquals("Parent completion status", parentComplete, parent.isComplete());
            }
        }
        if (ds instanceof PullOMDataSource) {
            assertFalse(((PullOMDataSource) ds).hasUnclosedReaders());
        }
    }
}
