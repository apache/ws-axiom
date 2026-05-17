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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.StringReader;
import junit.framework.TestCase;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.dimension.ElementContext;
import org.apache.axiom.ts.dimension.ExpansionStrategy;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;
import org.apache.axiom.ts.dimension.serialization.XML;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;
import org.xml.sax.InputSource;

/** Tests various ways to serialize an {@link OMSourcedElement}. */
public class TestSerialize extends TestCase {
    @Inject
    private OMFactory factory;

    /**
     * Specifies whether the data source to be used extends {@link AbstractPullOMDataSource} (<code>
     * false</code>) or {@link AbstractPushOMDataSource} (<code>true</code>).
     */
    @Inject
    @Named("push")
    private boolean push;

    /** Specifies if the {@link OMDataSource} is destructive or not. */
    @Inject
    @Named("destructive")
    private boolean destructive;

    /** Specifies if and how the {@link OMSourcedElement} is to be placed inside an {@link OMContainer}. */
    @Inject
    private ElementContext elementContext;

    @Inject
    private ExpansionStrategy expansionStrategy;

    /** The serialization strategy to test. */
    @Inject
    private SerializationStrategy serializationStrategy;

    /** Specifies if the parent of the {@link OMSourcedElement} should be serialized instead of the {@link OMSourcedElement} itself. */
    @Inject
    @Named("serializeParent")
    private boolean serializeParent;

    /** The number of times the {@link OMSourcedElement} will be serialized; the only meaningful values are 1 and 2. */
    @Inject
    @Named("count")
    private int count;

    @Override
    protected void runTest() throws Throwable {
        OMSourcedElement element = TestDocument.DOCUMENT1.createOMSourcedElement(factory, push, destructive);
        OMDataSource ds = element.getDataSource();
        OMContainer parent = elementContext.wrap(element);
        boolean parentComplete = parent != null && parent.isComplete();
        expansionStrategy.apply(element);
        boolean consuming = expansionStrategy.isConsumedAfterSerialization(push, destructive, serializationStrategy);
        for (int iteration = 0; iteration < count; iteration++) {
            boolean expectException = iteration != 0
                    && (consuming || serializeParent && !serializationStrategy.isCaching() && !parentComplete);
            XML result;
            if (expectException) {
                assertThatThrownBy(() -> serializationStrategy.serialize(serializeParent ? parent : element))
                        .isInstanceOf(Exception.class);
                continue;
            } else {
                result = serializationStrategy.serialize(serializeParent ? parent : element);
            }
            InputSource expectedXML = new InputSource(new StringReader(TestDocument.DOCUMENT1.getContent()));
            if (serializeParent) {
                expectedXML = elementContext.getControl(expectedXML);
            }
            assertAbout(xml()).that(result.getInputSource()).hasSameContentAs(expectedXML);
            // If the underlying OMDataSource is non destructive, the expansion status should not
            // have been changed during serialization. If it is destructive and caching is enabled,
            // then the sourced element should be expanded.
            if (expansionStrategy.isExpandedAfterSerialization(push, destructive, serializationStrategy)) {
                assertThat(element.isExpanded()).isTrue();
                assertThat(element.isComplete()).isEqualTo(!consuming);
            } else {
                assertThat(element.isExpanded()).isFalse();
            }
            if (parent != null && !serializeParent) {
                // Operations on the OMSourcedElement should have no impact on the parent
                assertThat(parent.isComplete()).isEqualTo(parentComplete);
            }
        }
        if (ds instanceof PullOMDataSource pullOmDataSource) {
            assertThat(pullOmDataSource.hasUnclosedReaders()).isFalse();
        }
    }
}
