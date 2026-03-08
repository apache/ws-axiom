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
package org.apache.axiom.ts.saaj;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;

import org.apache.axiom.ts.soap.SOAPSpec;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import junit.framework.TestCase;

public abstract class SAAJTestCase extends TestCase {
    @Inject protected SAAJImplementation saajImplementation;

    @Inject
    @Named("spec")
    protected SOAPSpec spec;

    protected final MessageFactory newMessageFactory() throws SOAPException {
        return spec.getAdapter(FactorySelector.class).newMessageFactory(saajImplementation);
    }

    protected final SOAPFactory newSOAPFactory() throws SOAPException {
        return spec.getAdapter(FactorySelector.class).newSOAPFactory(saajImplementation);
    }
}
