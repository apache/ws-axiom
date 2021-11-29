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
package org.apache.axiom.om.impl.mixin;

import static org.apache.axiom.om.impl.common.factory.meta.BuilderFactory.OM;
import static org.apache.axiom.om.impl.common.factory.meta.BuilderFactory.SOAP;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.factory.OMFactoryImpl;
import org.apache.axiom.om.impl.common.factory.meta.BuilderSpec;
import org.apache.axiom.om.impl.intf.factory.AxiomNodeFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.impl.factory.SOAP11Factory;
import org.apache.axiom.soap.impl.factory.SOAP12Factory;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

@Mixin
public abstract class AxiomNodeFactoryMixin implements AxiomNodeFactory {
    private final OMFactory omFactory;
    private final SOAPFactory soap11Factory;
    private final SOAPFactory soap12Factory;
    
    public AxiomNodeFactoryMixin() {
        omFactory = new OMFactoryImpl(this);
        soap11Factory = new SOAP11Factory(this);
        soap12Factory = new SOAP12Factory(this);
    }
    
    @Override
    public final OMFactory getOMFactory() {
        return omFactory;
    }

    @Override
    public final SOAPFactory getSOAP11Factory() {
        return soap11Factory;
    }

    @Override
    public final SOAPFactory getSOAP12Factory() {
        return soap12Factory;
    }
    
    @Override
    public final OMXMLParserWrapper createStAXOMBuilder(XMLStreamReader parser) {
        return OM.createBuilder(this, BuilderSpec.from(parser));
    }

    @Override
    public final OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, InputSource is) {
        return OM.createBuilder(this, BuilderSpec.from(configuration, is));
    }
    
    @Override
    public final OMXMLParserWrapper createOMBuilder(Source source) {
        return OM.createBuilder(this, BuilderSpec.from(StAXParserConfiguration.DEFAULT, source));
    }

    @Override
    public final OMXMLParserWrapper createOMBuilder(Node node, boolean expandEntityReferences) {
        return OM.createBuilder(this, BuilderSpec.from(node, expandEntityReferences));
    }

    @Override
    public final OMXMLParserWrapper createOMBuilder(SAXSource source, boolean expandEntityReferences) {
        return OM.createBuilder(this, BuilderSpec.from(source, expandEntityReferences));
    }

    @Override
    public final OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, MultipartBody message) {
        return OM.createBuilder(this, BuilderSpec.from(configuration, message));
    }

    @Override
    public final OMXMLParserWrapper createOMBuilder(Source rootPart, OMAttachmentAccessor attachmentAccessor) {
        return OM.createBuilder(this, BuilderSpec.from(StAXParserConfiguration.DEFAULT, rootPart, attachmentAccessor));
    }

    @Override
    public final SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser) {
        return SOAP.createBuilder(this, BuilderSpec.from(parser));
    }

    @Override
    public final SOAPModelBuilder createSOAPModelBuilder(InputSource is) {
        return SOAP.createBuilder(this, BuilderSpec.from(StAXParserConfiguration.SOAP, is));
    }

    @Override
    public final SOAPModelBuilder createSOAPModelBuilder(Source source) {
        return SOAP.createBuilder(this, BuilderSpec.from(StAXParserConfiguration.SOAP, source));
    }

    @Override
    public final SOAPModelBuilder createSOAPModelBuilder(MultipartBody message) {
        return SOAP.createBuilder(this, BuilderSpec.from(StAXParserConfiguration.SOAP, message));
    }

    @Override
    public final SOAPModelBuilder createSOAPModelBuilder(Source rootPart, OMAttachmentAccessor attachmentAccessor) {
        return SOAP.createBuilder(this, BuilderSpec.from(StAXParserConfiguration.SOAP, rootPart, attachmentAccessor));
    }
}
