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
package org.apache.axiom.om.impl.common.factory;

import static org.apache.axiom.om.impl.common.factory.BuilderFactory.OM;
import static org.apache.axiom.om.impl.common.factory.BuilderFactory.SOAP;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMMetaFactorySPI;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Base class for {@link OMMetaFactory} implementations that make use of the standard builders
 * ({@link org.apache.axiom.core.impl.builder.BuilderImpl} and its subclasses).
 */
public abstract class AbstractOMMetaFactory implements OMMetaFactorySPI {
    private final NodeFactory nodeFactory;
    
    public AbstractOMMetaFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }
    
    @Override
    public OMXMLParserWrapper createStAXOMBuilder(XMLStreamReader parser) {
        return OM.createBuilder(nodeFactory, BuilderSpec.from(parser));
    }

    @Override
    public OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, InputSource is) {
        return OM.createBuilder(nodeFactory, BuilderSpec.from(configuration, is));
    }
    
    @Override
    public OMXMLParserWrapper createOMBuilder(Source source) {
        return OM.createBuilder(nodeFactory, BuilderSpec.from(StAXParserConfiguration.DEFAULT, source));
    }

    @Override
    public OMXMLParserWrapper createOMBuilder(Node node, boolean expandEntityReferences) {
        return OM.createBuilder(nodeFactory, BuilderSpec.from(node, expandEntityReferences));
    }

    @Override
    public OMXMLParserWrapper createOMBuilder(SAXSource source, boolean expandEntityReferences) {
        return OM.createBuilder(nodeFactory, BuilderSpec.from(source, expandEntityReferences));
    }

    @Override
    public OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, MultipartBody message) {
        return OM.createBuilder(nodeFactory, BuilderSpec.from(configuration, message));
    }

    @Override
    public OMXMLParserWrapper createOMBuilder(Source rootPart, OMAttachmentAccessor attachmentAccessor) {
        return OM.createBuilder(nodeFactory, BuilderSpec.from(StAXParserConfiguration.DEFAULT, rootPart, attachmentAccessor));
    }

    @Override
    public SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser) {
        return SOAP.createBuilder(nodeFactory, BuilderSpec.from(parser));
    }

    @Override
    public SOAPModelBuilder createSOAPModelBuilder(InputSource is) {
        return SOAP.createBuilder(nodeFactory, BuilderSpec.from(StAXParserConfiguration.SOAP, is));
    }

    @Override
    public SOAPModelBuilder createSOAPModelBuilder(Source source) {
        return SOAP.createBuilder(nodeFactory, BuilderSpec.from(StAXParserConfiguration.SOAP, source));
    }

    @Override
    public SOAPModelBuilder createSOAPModelBuilder(MultipartBody message) {
        return SOAP.createBuilder(nodeFactory, BuilderSpec.from(StAXParserConfiguration.SOAP, message));
    }

    @Override
    public SOAPModelBuilder createSOAPModelBuilder(Source rootPart, OMAttachmentAccessor attachmentAccessor) {
        return SOAP.createBuilder(nodeFactory, BuilderSpec.from(StAXParserConfiguration.SOAP, rootPart, attachmentAccessor));
    }
}
