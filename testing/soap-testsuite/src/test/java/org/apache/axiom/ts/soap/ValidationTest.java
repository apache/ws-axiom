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
package org.apache.axiom.ts.soap;

import static org.apache.axiom.testing.multiton.Multiton.getInstances;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValidationTest extends TestCase {
    private final SOAPSample message;

    public ValidationTest(SOAPSample message) {
        super(message.getName());
        this.message = message;
    }

    @Override
    protected void runTest() throws Throwable {
        message.getSOAPSpec()
                .getSchema()
                .newValidator()
                .validate(new StreamSource(message.getInputStream()));
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        for (SOAPSample message : getInstances(SOAPSample.class)) {
            if (message != SOAPSampleSet.XSI_TYPE.getMessage(SOAPSpec.SOAP11)
                    && message != SOAPSampleSet.XSI_TYPE.getMessage(SOAPSpec.SOAP12)) {
                suite.addTest(new ValidationTest(message));
            }
        }
        return suite;
    }
}
