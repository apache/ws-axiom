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

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import org.apache.axiom.testing.multiton.AdapterType;
import org.apache.axiom.ts.soap.SOAPSpec;

/**
 * {@link SOAPSpec} adapter that allows to retrieve the {@link MessageFactory} or {@link
 * SOAPFactory} corresponding to the SOAP version from a {@link SAAJImplementation}.
 */
@AdapterType
public interface FactorySelector {
    MessageFactory newMessageFactory(SAAJImplementation saajImplementation) throws SOAPException;

    SOAPFactory newSOAPFactory(SAAJImplementation saajImplementation) throws SOAPException;
}
