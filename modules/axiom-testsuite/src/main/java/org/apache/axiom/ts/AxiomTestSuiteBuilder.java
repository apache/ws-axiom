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
package org.apache.axiom.ts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestSuite;

import org.apache.axiom.om.OMMetaFactory;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public abstract class AxiomTestSuiteBuilder {
    private static class Exclude {
        private final Class testClass;
        private final Filter filter;
        
        public Exclude(Class testClass, Filter filter) {
            this.testClass = testClass;
            this.filter = filter;
        }
        
        public boolean accept(AxiomTestCase test) {
            return (testClass == null || test.getClass().equals(testClass))
                    && (filter == null || filter.match(test.getTestProperties()));
        }
    }
    
    protected final OMMetaFactory metaFactory;
    private final List/*<Exclude>*/ excludes = new ArrayList();
    private TestSuite suite;
    
    public AxiomTestSuiteBuilder(OMMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
    }
    
    public final void exclude(Class testClass, String filter) {
        try {
            excludes.add(new Exclude(testClass, filter == null ? null : FrameworkUtil.createFilter(filter)));
        } catch (InvalidSyntaxException ex) {
            throw new IllegalArgumentException("Invalid filter expression", ex);
        }
    }
    
    public final void exclude(Class testClass) {
        exclude(testClass, null);
    }
    
    public final void exclude(String filter) {
        exclude(null, filter);
    }
    
    protected abstract void addTests();
    
    public final TestSuite build() {
        suite = new TestSuite();
        addTests();
        return suite;
    }
    
    protected final void addTest(AxiomTestCase test) {
        for (Iterator it = excludes.iterator(); it.hasNext(); ) {
            if (((Exclude)it.next()).accept(test)) {
                return;
            }
        }
        suite.addTest(test);
    }
}
