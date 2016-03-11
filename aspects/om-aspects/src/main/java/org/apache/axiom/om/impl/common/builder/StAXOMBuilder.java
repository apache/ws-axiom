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

package org.apache.axiom.om.impl.common.builder;

import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlReader;
import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilderSupport;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder.Selector;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

import javax.xml.stream.XMLStreamReader;

import java.io.Closeable;

public class StAXOMBuilder extends AbstractBuilder implements CustomBuilderSupport {
    private final XmlReader reader;

    private final Detachable detachable;
    
    private final CustomBuilderManager customBuilderManager = new CustomBuilderManager();
    
    protected StAXOMBuilder(NodeFactory nodeFactory, XMLStreamReader parser,
            boolean autoClose, Detachable detachable, Closeable closeable, Model model,
            AxiomSourcedElement root) {
        // TODO: disable namespace repairing for XMLStreamReader created from a parser
        super(nodeFactory, model, root, true);
        if (parser.getEventType() != XMLStreamReader.START_DOCUMENT) {
            throw new IllegalStateException("The XMLStreamReader must be positioned on a START_DOCUMENT event");
        }
        reader = new StAXPullReader(parser, handler, closeable, autoClose);
        this.detachable = detachable;
        builderHandler.addListener(customBuilderManager);
    }
    
    public StAXOMBuilder(NodeFactory nodeFactory, XMLStreamReader parser, boolean autoClose,
            Detachable detachable, Closeable closeable) {
        this(nodeFactory, parser, autoClose, detachable, closeable, PlainXMLModel.INSTANCE, null);
    }
    
    public StAXOMBuilder(NodeFactory nodeFactory,
                         XMLStreamReader parser, 
                         AxiomSourcedElement element) {
        this(nodeFactory, parser, true, null, null, PlainXMLModel.INSTANCE, element);
    }
    
    @Override
    public void registerCustomBuilder(Selector selector, CustomBuilder customBuilder) {
        customBuilderManager.register(selector, customBuilder);
    }
    
    public final void close() {
        reader.dispose();
    }

    public final void detach() throws OMException {
        if (detachable != null) {
            detachable.detach();
        } else {
            while (!builderHandler.done) {
                next();
            }
        }
    }
    
    /**
     * Forwards the parser one step further, if parser is not completed yet. If this is called after
     * parser is done, then throw an OMException. If the cache is set to false, then returns the
     * event, *without* building the OM tree. If the cache is set to true, then handles all the
     * events within this, and builds the object structure appropriately and returns the event.
     *
     * @return Returns int.
     * @throws OMException
     */
    public void next() throws OMException {
        if (builderHandler.done) {
            throw new OMException();
        }
        try {
            reader.proceed();
        } catch (StreamException ex) {
            throw new DeferredParsingException(ex);
        }
        builderHandler.executeDeferredActions();
    }
}
