/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.parser.stax;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.common.factory.AbstractOMMetaFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;

public final class FOMMetaFactory extends AbstractOMMetaFactory {
    public static final FOMMetaFactory INSTANCE = new FOMMetaFactory();
    
    private final OMFactory omFactory = new FOMFactory();
    
    private FOMMetaFactory() {}
    
    public OMFactory getOMFactory() {
        return omFactory;
    }

    public SOAPFactory getSOAP11Factory() {
        throw new UnsupportedOperationException();
    }

    public SOAPFactory getSOAP12Factory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AxiomSOAPMessage createSOAPMessage() {
        throw new UnsupportedOperationException();
    }
}
