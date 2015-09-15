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

package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.traverse.OMChildrenWithSpecificAttributeIterator;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.impl.common.AxiomSOAP11Header;
import org.apache.axiom.soap.impl.llom.SOAPHeaderImpl;

import javax.xml.namespace.QName;

import java.util.Iterator;

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
