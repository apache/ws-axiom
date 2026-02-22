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
package org.apache.axiom.ts.dimension;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.AbstractPullOMDataSource;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.Dimension;
import org.apache.axiom.testutils.suite.TestParameterTarget;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;
import org.junit.Assert;

/**
 * Defines if and how an {@link OMContainer} is to be built or expanded during the execution of a
 * test case.
 */
public abstract class ExpansionStrategy extends Multiton implements Dimension {
    /** Don't build the {@link OMContainer}. */
    public static final ExpansionStrategy DONT_EXPAND =
            new ExpansionStrategy() {
                @Override
                public void addTestParameters(TestParameterTarget testCase) {
                    testCase.addTestParameter("expand", "no");
                }

                @Override
                public void apply(OMContainer container) {
                    if (container instanceof OMSourcedElement sourcedElement) {
                        // Do nothing, but check that the element isn't expanded already
                        Assert.assertFalse(sourcedElement.isExpanded());
                    } else {
                        Assert.assertFalse(container.isComplete());
                    }
                }

                @Override
                public boolean isConsumedAfterSerialization(
                        boolean pushDS,
                        boolean destructiveDS,
                        SerializationStrategy serializationStrategy) {
                    return !(pushDS && !serializationStrategy.isPush())
                            && destructiveDS
                            && !serializationStrategy.isCaching();
                }

                @Override
                public boolean isExpandedAfterSerialization(
                        boolean pushDS,
                        boolean destructiveDS,
                        SerializationStrategy serializationStrategy) {
                    return pushDS && !serializationStrategy.isPush()
                            || destructiveDS && serializationStrategy.isCaching();
                }
            };

    /** Partially build the {@link OMContainer}. */
    public static final ExpansionStrategy PARTIAL =
            new ExpansionStrategy() {
                @Override
                public void addTestParameters(TestParameterTarget testCase) {
                    testCase.addTestParameter("expand", "partially");
                }

                @Override
                public void apply(OMContainer container) {
                    container.getFirstOMChild();
                    if (container instanceof OMSourcedElement sourcedElement) {
                        Assert.assertTrue(sourcedElement.isExpanded());
                    }
                    Assert.assertFalse(container.isComplete());
                }

                @Override
                public boolean isConsumedAfterSerialization(
                        boolean pushDS,
                        boolean destructiveDS,
                        SerializationStrategy serializationStrategy) {
                    return !serializationStrategy.isCaching();
                }

                @Override
                public boolean isExpandedAfterSerialization(
                        boolean pushDS,
                        boolean destructiveDS,
                        SerializationStrategy serializationStrategy) {
                    return true;
                }
            };

    /** Fully build the {@link OMContainer}. */
    public static final ExpansionStrategy FULL =
            new ExpansionStrategy() {
                @Override
                public void addTestParameters(TestParameterTarget testCase) {
                    testCase.addTestParameter("expand", "fully");
                }

                @Override
                public void apply(OMContainer container) {
                    container.getFirstOMChild();
                    container.build();
                    if (container instanceof OMSourcedElement sourcedElement) {
                        Assert.assertTrue(sourcedElement.isExpanded());
                    }
                    Assert.assertTrue(container.isComplete());
                }

                @Override
                public boolean isConsumedAfterSerialization(
                        boolean pushDS,
                        boolean destructiveDS,
                        SerializationStrategy serializationStrategy) {
                    return false;
                }

                @Override
                public boolean isExpandedAfterSerialization(
                        boolean pushDS,
                        boolean destructiveDS,
                        SerializationStrategy serializationStrategy) {
                    return true;
                }
            };

    private ExpansionStrategy() {}

    /**
     * Apply the expansion strategy to the given {@link OMContainer}.
     *
     * @param container
     */
    public abstract void apply(OMContainer container);

    /**
     * Determines if serializing the {@link OMSourcedElement} after applying this expansion strategy
     * will consume it. If the element is consumed, it cannot be serialized twice.
     *
     * @param pushDS specifies whether the {@link OMDataSource} of the {@link OMSourcedElement}
     *     extends {@link AbstractPullOMDataSource} (<code>false</code>) or {@link
     *     AbstractPushOMDataSource} (<code>true</code>)
     * @param destructiveDS specifies whether the {@link OMDataSource} of the {@link
     *     OMSourcedElement} is destructive
     * @param serializationStrategy the serialization strategy
     * @return <code>true</code> if serializing the {@link OMSourcedElement} will consume it, <code>
     *     false</code> if the {@link OMSourcedElement} can be serialized multiple times
     */
    public abstract boolean isConsumedAfterSerialization(
            boolean pushDS, boolean destructiveDS, SerializationStrategy serializationStrategy);

    /**
     * Determines if the {@link OMSourcedElement} to which this expansion strategy has been applied
     * will be expanded after serialization.
     *
     * @param pushDS specifies whether the {@link OMDataSource} of the {@link OMSourcedElement}
     *     extends {@link AbstractPullOMDataSource} (<code>false</code>) or {@link
     *     AbstractPushOMDataSource} (<code>true</code>)
     * @param destructiveDS specifies whether the {@link OMDataSource} of the {@link
     *     OMSourcedElement} is destructive
     * @param serializationStrategy the serialization strategy
     * @return the expected value of {@link OMSourcedElement#isExpanded()} after serialization
     */
    public abstract boolean isExpandedAfterSerialization(
            boolean pushDS, boolean destructiveDS, SerializationStrategy serializationStrategy);
}
