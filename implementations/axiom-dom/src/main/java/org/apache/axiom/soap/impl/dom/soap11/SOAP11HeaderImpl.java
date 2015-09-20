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

package org.apache.axiom.soap.impl.dom.soap11;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.impl.traverse.OMChildrenWithSpecificAttributeIterator;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.impl.dom.SOAPHeaderImpl;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Header;

public class SOAP11HeaderImpl extends SOAPHeaderImpl implements AxiomSOAP11Header {
    public Iterator extractHeaderBlocks(String role) {
        return new OMChildrenWithSpecificAttributeIterator(getFirstOMChild(),
                                                           new QName(
                                                                   SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                                                                   SOAP11Constants.ATTR_ACTOR),
                                                           role,
                                                           true);

    }
}
