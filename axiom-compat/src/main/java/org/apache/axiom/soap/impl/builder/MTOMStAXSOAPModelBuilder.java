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
package org.apache.axiom.soap.impl.builder;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;

/**
 * @deprecated Please use the {@link OMXMLBuilderFactory} API to create builders.
 */
public class MTOMStAXSOAPModelBuilder extends StAXSOAPModelBuilder {
    public MTOMStAXSOAPModelBuilder(
            XMLStreamReader reader, Attachments attachments, String soapVersion) {
        super(
                OMXMLBuilderFactory.createSOAPModelBuilder(
                        OMAbstractFactory.getMetaFactory(), new StAXSource(reader), attachments));
        validateSOAPVersion(null, soapVersion);
    }
}
