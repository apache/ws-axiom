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

import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.Constants;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.fom.AbderaNode;
import org.apache.axiom.fom.impl.FOMNodeFactory;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.builder.BuilderImpl;
import org.apache.axiom.om.impl.common.builder.BuilderListener;
import org.apache.axiom.om.impl.stream.stax.StAXPullInput;

@SuppressWarnings("unchecked")
public class FOMBuilder extends BuilderImpl implements Constants {

    private final FOMFactory fomfactory;
    private final ParserOptions parserOptions;

    public FOMBuilder(final FOMFactory factory, XMLStreamReader parser, ParserOptions parserOptions) {
        super(new StAXPullInput(new FOMStAXFilter(parser, parserOptions), false, null), FOMNodeFactory.INSTANCE,
                factory, null, true, null); // TODO: probably we can use repairNamespaces=false here
        this.parserOptions = parserOptions;
        this.fomfactory = factory;
        addListener(new BuilderListener() {
            @Override
            public Runnable nodeAdded(CoreNode node, int depth) {
                ((AbderaNode)node).setFactory(factory);
                return null;
            }
        });
    }

    public ParserOptions getParserOptions() {
        return parserOptions;
    }

    /**
     * Method next.
     * 
     * @return Returns int.
     * @throws OMException
     */
    public void next() throws OMException {
        try {
            super.next();
        } catch (OMException e) {
            // TODO: transforming the OMException here is not ideal!
            throw new ParseException(e);
        }
    }

    public <T extends Element> Document<T> getFomDocument() {
        // For compatibility with earlier Abdera versions, force creation of the document element.
        // Note that the only known case where this has a visible effect is when the document is
        // not well formed. At least one unit test depends on this behavior.
        getDocumentElement();
        return (Document<T>)getDocument();
    }

    public FOMFactory getFactory() {
        return fomfactory;
    }
}
