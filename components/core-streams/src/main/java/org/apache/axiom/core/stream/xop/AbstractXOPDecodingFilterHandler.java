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
package org.apache.axiom.core.stream.xop;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.xml.namespace.QName;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlHandlerWrapper;

public abstract class AbstractXOPDecodingFilterHandler extends XmlHandlerWrapper {
    private static final String SOLE_CHILD_MSG =
            "Expected xop:Include as the sole child of an element information item (see section "
                    + "3.2 of http://www.w3.org/TR/xop10/)";

    private enum State {
        AFTER_START_ELEMENT,
        CONTENT_SEEN,
        IN_XOP_INCLUDE,
        AFTER_XOP_INCLUDE
    }

    private State state = State.CONTENT_SEEN;
    private String contentID;

    public AbstractXOPDecodingFilterHandler(XmlHandler parent) {
        super(parent);
    }

    protected abstract Object buildCharacterData(String contentID) throws StreamException;

    private void inContent() throws StreamException {
        switch (state) {
            case IN_XOP_INCLUDE:
                throw new StreamException(
                        "Expected xop:Include element information item to be empty");
            case AFTER_XOP_INCLUDE:
                throw new StreamException(SOLE_CHILD_MSG);
            default:
                state = State.CONTENT_SEEN;
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        if (localName.equals(XOPConstants.INCLUDE)
                && namespaceURI.equals(XOPConstants.NAMESPACE_URI)) {
            if (state == State.AFTER_START_ELEMENT) {
                state = State.IN_XOP_INCLUDE;
            } else {
                throw new StreamException(SOLE_CHILD_MSG);
            }
        } else {
            inContent();
            super.startElement(namespaceURI, localName, prefix);
        }
    }

    @Override
    public void endElement() throws StreamException {
        if (state == State.IN_XOP_INCLUDE) {
            if (contentID == null) {
                throw new StreamException("No href attribute found on xop:Include element");
            }
            super.processCharacterData(buildCharacterData(contentID), false);
            contentID = null;
            state = State.AFTER_XOP_INCLUDE;
        } else {
            state = State.CONTENT_SEEN;
            super.endElement();
        }
    }

    @Override
    public void processAttribute(
            String namespaceURI,
            String localName,
            String prefix,
            String value,
            String type,
            boolean specified)
            throws StreamException {
        if (state == State.IN_XOP_INCLUDE) {
            if (namespaceURI.isEmpty() && localName.equals(XOPConstants.HREF)) {
                if (!value.startsWith("cid:")) {
                    throw new StreamException(
                            "Expected href attribute containing a URL in the cid scheme");
                }
                try {
                    // URIs should always be decoded using UTF-8. On the other hand, since non ASCII
                    // characters are not allowed in content IDs, we can simply decode using ASCII
                    // (which is a subset of UTF-8)
                    contentID = URLDecoder.decode(value.substring(4), "ascii");
                } catch (UnsupportedEncodingException ex) {
                    // We should never get here
                    throw new StreamException(ex);
                }
            } else {
                throw new StreamException(
                        "Encountered unexpected attribute "
                                + new QName(namespaceURI, localName)
                                + " on xop:Include element");
            }
        } else {
            super.processAttribute(namespaceURI, localName, prefix, value, type, specified);
        }
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI)
            throws StreamException {
        if (state != State.IN_XOP_INCLUDE) {
            super.processNamespaceDeclaration(prefix, namespaceURI);
        }
    }

    @Override
    public void attributesCompleted() throws StreamException {
        if (state != State.IN_XOP_INCLUDE) {
            super.attributesCompleted();
            state = State.AFTER_START_ELEMENT;
        }
    }

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        inContent();
        super.processCharacterData(data, ignorable);
    }

    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        inContent();
        super.startProcessingInstruction(target);
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        inContent();
        super.endProcessingInstruction();
    }

    @Override
    public void startComment() throws StreamException {
        inContent();
        super.startComment();
    }

    @Override
    public void endComment() throws StreamException {
        inContent();
        super.endComment();
    }

    @Override
    public void startCDATASection() throws StreamException {
        inContent();
        super.startCDATASection();
    }

    @Override
    public void endCDATASection() throws StreamException {
        inContent();
        super.endCDATASection();
    }

    @Override
    public void processEntityReference(String name, String replacementText) throws StreamException {
        inContent();
        super.processEntityReference(name, replacementText);
    }
}
