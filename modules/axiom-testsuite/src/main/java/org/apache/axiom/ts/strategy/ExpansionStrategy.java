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
package org.apache.axiom.ts.strategy;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;
import org.junit.Assert;

/**
 * Defines if and how an {@link OMSourcedElement} is to be expanded during the execution of a test
 * case.
 */
public interface ExpansionStrategy extends Strategy {
    /**
     * Don't expand the {@link OMSourcedElement}.
     */
    ExpansionStrategy DONT_EXPAND = new ExpansionStrategy() {
        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("expand", "no");
        }

        public void apply(OMSourcedElement element) {
            // Do nothing, but check that the element isn't expanded already
            Assert.assertFalse(element.isExpanded());
        }

        public boolean isConsumedAfterSerializationWithoutCaching(boolean destructiveDS) {
            return destructiveDS;
        }

        public boolean isExpandedAfterSerialization(boolean destructiveDS, boolean cache) {
            return destructiveDS && cache;
        }
    };
    
    /**
     * Partially expand the {@link OMSourcedElement}.
     */
    ExpansionStrategy PARTIAL = new ExpansionStrategy() {
        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("expand", "partially");
        }
        
        public void apply(OMSourcedElement element) {
            element.getFirstOMChild();
            Assert.assertTrue(element.isExpanded());
            Assert.assertFalse(element.isComplete());
        }

        public boolean isConsumedAfterSerializationWithoutCaching(boolean destructiveDS) {
            return true;
        }

        public boolean isExpandedAfterSerialization(boolean destructiveDS, boolean cache) {
            return true;
        }
    };

    /**
     * Fully expand the {@link OMSourcedElement}.
     */
    ExpansionStrategy FULL = new ExpansionStrategy() {
        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("expand", "fully");
        }
        
        public void apply(OMSourcedElement element) {
            element.getFirstOMChild();
            element.build();
            Assert.assertTrue(element.isExpanded());
            Assert.assertTrue(element.isComplete());
        }

        public boolean isConsumedAfterSerializationWithoutCaching(boolean destructiveDS) {
            return false;
        }

        public boolean isExpandedAfterSerialization(boolean destructiveDS, boolean cache) {
            return true;
        }
    };

    /**
     * Apply the expansion strategy to the given {@link OMSourcedElement}.
     * 
     * @param element
     */
    void apply(OMSourcedElement element);
    
    /**
     * Determines if serializing the {@link OMSourcedElement} (with caching turned off) after
     * applying this expansion strategy will consume it. If the element is consumed, it cannot be
     * serialized twice.
     * 
     * @param destructiveDS
     *            specifies whether the {@link OMDataSource} of the {@link OMSourcedElement} is
     *            destructive
     * @return <code>true</code> if serializing the {@link OMSourcedElement} will consume it,
     *         <code>false</code> if the {@link OMSourcedElement} can be serialized multiple times
     */
    boolean isConsumedAfterSerializationWithoutCaching(boolean destructiveDS);
    
    /**
     * Determines if the {@link OMSourcedElement} to which this expansion strategy has been applied
     * will be expanded after serialization.
     * 
     * @param destructiveDS
     *            specifies whether the {@link OMDataSource} of the {@link OMSourcedElement} is
     *            destructive
     * @param cache
     *            specifies whether caching is enabled during serialization
     * @return the expected value of {@link OMSourcedElement#isExpanded()} after serialization
     */
    boolean isExpandedAfterSerialization(boolean destructiveDS, boolean cache);
}
