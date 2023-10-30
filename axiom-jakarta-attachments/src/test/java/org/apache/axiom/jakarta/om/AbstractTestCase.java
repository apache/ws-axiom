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

package org.apache.axiom.jakarta.om;

import java.io.InputStream;
import java.net.URL;

import jakarta.activation.DataSource;
import jakarta.activation.URLDataSource;

import junit.framework.TestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;

/** Abstract base class for test cases. */
public abstract class AbstractTestCase extends TestCase {
    public AbstractTestCase() {
        this(null);
    }
    
    /** @param testName  */
    public AbstractTestCase(String testName) {
        super(testName);
    }

    public DataSource getTestResourceDataSource(String relativePath) {
        URL url = AbstractTestCase.class.getClassLoader().getResource(relativePath);
        if (url == null) {
            fail("The test resource " + relativePath + " could not be found");
        }
        return new URLDataSource(url);
    }

    public static InputStream getTestResource(String relativePath) {
        InputStream in = AbstractTestCase.class.getClassLoader().getResourceAsStream(relativePath);
        if (in == null) {
            fail("The test resource " + relativePath + " could not be found");
        }
        return in;
    }
    
    public static OMElement getTestResourceAsElement(OMMetaFactory omMetaFactory, String relativePath) {
        return OMXMLBuilderFactory.createOMBuilder(omMetaFactory.getOMFactory(), getTestResource(relativePath)).getDocumentElement();
    }
}

