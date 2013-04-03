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
package org.apache.axiom.testutils.suite;

import javax.xml.transform.TransformerFactory;

/**
 * Specifies an XSLT implementation for use in a {@link MatrixTestCase}.
 */
public interface XSLTImplementation extends Dimension {
    XSLTImplementation[] INSTANCES = new XSLTImplementation[] {
        new XSLTImplementation() {
            public void addTestParameters(MatrixTestCase testCase) {
                testCase.addTestParameter("xslt", "xalan");
            }
            
            public TransformerFactory newTransformerFactory() {
                return new org.apache.xalan.processor.TransformerFactoryImpl();
            }
        },
        new XSLTImplementation() {
            public void addTestParameters(MatrixTestCase testCase) {
                testCase.addTestParameter("xslt", "saxon");
            }
            
            public TransformerFactory newTransformerFactory() {
                return new net.sf.saxon.TransformerFactoryImpl();
            }
        },
    };
    
    TransformerFactory newTransformerFactory();
}
