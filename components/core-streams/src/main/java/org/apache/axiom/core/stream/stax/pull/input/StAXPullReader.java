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

package org.apache.axiom.core.stream.stax.pull.input;

import org.apache.axiom.core.stream.CharacterData;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.Closeable;

/* Implementation note about error handling
 * ----------------------------------------
 *
 * Usually, code that uses StAX directly just stops processing of an XML document
 * once the first parsing error has been reported. However, since Axiom
 * uses deferred parsing, and client code accesses the XML infoset using
 * an object model, things are more complicated. Indeed, if the XML
 * document is not well formed, the corresponding error might be reported
 * as a runtime exception by any call to a method of an OM node.
 *
 * Typically the client code will have some error handling that will intercept
 * runtime exceptions and take appropriate action. Very often this error handling
 * code might want to access the object model again, for example to log the request that caused the
 * failure. This causes no problem except if the runtime exception was caused by a
 * parsing error, in which case Axiom would again try to pull events from the parser.
 *
 * This would lead to a situation where Axiom accesses a parser that has reported a parsing
 * error before. While one would expect that after a first error reported by the parser, all
 * subsequent invocations of the parser will fail, this is not the case for all parsers
 * (at least not in all situations). Instead, the parser might be left in an inconsistent
 * state after the error. E.g. AXIOM-34 describes a case where Woodstox
 * encounters an error in XMLStreamReader#getText() but continues to return
 * (incorrect) events afterwards. The explanation for this behaviour might be that
 * the situation described here is quite uncommon when StAX is used directly (i.e. not through
 * Axiom).
 *
 * To avoid this, the builder remembers exceptions thrown by the parser and rethrows
 * them during a call to next().
 */
final class StAXPullReader implements XmlReader {
    private static final Log log = LogFactory.getLog(StAXPullReader.class);

    /** Field parser */
    private XMLStreamReader reader;

    private XMLStreamReaderHelper helper;

    private final XmlHandler handler;
    private final Closeable closeable;

    /**
     * Specifies whether the builder/parser should be automatically closed when the {@link
     * XMLStreamConstants#END_DOCUMENT} event is reached.
     */
    private final boolean autoClose;

    private boolean isClosed = false; // Indicate if parser is closed

    /**
     * Stores exceptions thrown by the parser. Used to avoid accessing the parser again after is has
     * thrown a parse exception.
     */
    private Exception parserException;

    private boolean start = true;

    StAXPullReader(
            XMLStreamReader reader,
            XMLStreamReaderHelper helper,
            XmlHandler handler,
            Closeable closeable,
            boolean autoClose) {
        if (reader.getEventType() != XMLStreamReader.START_DOCUMENT) {
            throw new IllegalStateException(
                    "The XMLStreamReader must be positioned on a START_DOCUMENT event");
        }
        this.reader = reader;
        this.helper = helper;
        this.handler = handler;
        this.closeable = closeable;
        this.autoClose = autoClose;
    }

    private static String normalize(String s) {
        return s == null ? "" : s;
    }

    private void processText(int textType) throws StreamException {
        if (textType == XMLStreamConstants.CHARACTERS) {
            CharacterData data = helper.getCharacterData();
            if (data != null) {
                handler.processCharacterData(data, false);
                return;
            }
        }
        // Some parsers (like Woodstox) parse text nodes lazily and may throw a
        // RuntimeException in getText()
        String text;
        try {
            text = reader.getText();
        } catch (RuntimeException ex) {
            parserException = ex;
            throw ex;
        }
        switch (textType) {
            case XMLStreamConstants.CHARACTERS -> handler.processCharacterData(text, false);
            case XMLStreamConstants.SPACE -> handler.processCharacterData(text, true);
            case XMLStreamConstants.CDATA -> {
                handler.startCDATASection();
                handler.processCharacterData(text, false);
                handler.endCDATASection();
            }
            default -> throw new IllegalStateException();
        }
    }

    @Override
    public void dispose() {
        try {
            if (!isClosed) {
                reader.close();
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (Throwable e) {
            // Can't see a reason why we would want to surface an exception
            // while closing the parser.
            if (log.isDebugEnabled()) {
                log.debug(
                        "Exception occurred during parser close.  " + "Processing continues. " + e);
            }
        } finally {
            isClosed = true;
            //            builderHandler.done = true;
            // Release the parser so that it can be GC'd or reused. This is important because the
            // object model keeps a reference to the builder even after the builder is complete.
            reader = null;
            helper = null;
        }
    }

    @Override
    public boolean proceed() throws StreamException {
        int token = parserNext();

        // Note: if autoClose is enabled, then the parser may be null at this point

        switch (token) {
            case XMLStreamConstants.START_DOCUMENT ->
                    handler.startDocument(
                            reader.getEncoding(),
                            reader.getVersion(),
                            reader.getCharacterEncodingScheme(),
                            reader.standaloneSet() ? reader.isStandalone() : null);
            case XMLStreamConstants.START_ELEMENT -> {
                processElement();
            }
            case XMLStreamConstants.CHARACTERS,
                    XMLStreamConstants.CDATA,
                    XMLStreamConstants.SPACE ->
                    processText(token);
            case XMLStreamConstants.END_ELEMENT -> handler.endElement();
            case XMLStreamConstants.END_DOCUMENT -> handler.completed();
            case XMLStreamConstants.COMMENT -> {
                handler.startComment();
                handler.processCharacterData(reader.getText(), false);
                handler.endComment();
            }
            case XMLStreamConstants.DTD -> processDTD();
            case XMLStreamConstants.PROCESSING_INSTRUCTION -> {
                handler.startProcessingInstruction(reader.getPITarget());
                handler.processCharacterData(reader.getPIData(), false);
                handler.endProcessingInstruction();
            }
            case XMLStreamConstants.ENTITY_REFERENCE ->
                    handler.processEntityReference(reader.getLocalName(), reader.getText());
            default -> throw new IllegalStateException();
        }

        return token == XMLStreamReader.END_DOCUMENT;
    }

    private void processElement() throws StreamException {
        String namespaceURI = normalize(reader.getNamespaceURI());
        String localName = reader.getLocalName();
        String prefix = normalize(reader.getPrefix());
        handler.startElement(namespaceURI, localName, prefix);
        for (int i = 0, count = reader.getNamespaceCount(); i < count; i++) {
            handler.processNamespaceDeclaration(
                    normalize(reader.getNamespacePrefix(i)), normalize(reader.getNamespaceURI(i)));
        }
        for (int i = 0, count = reader.getAttributeCount(); i < count; i++) {
            handler.processAttribute(
                    normalize(reader.getAttributeNamespace(i)),
                    reader.getAttributeLocalName(i),
                    normalize(reader.getAttributePrefix(i)),
                    reader.getAttributeValue(i),
                    reader.getAttributeType(i),
                    reader.isAttributeSpecified(i));
        }
        handler.attributesCompleted();
    }

    private void processDTD() throws StreamException {
        DTDInfo dtdInfo = helper.getDTDInfo();
        String internalSubset = getDTDText();
        // Woodstox returns an empty string if there is no internal subset
        if (internalSubset != null && internalSubset.length() == 0) {
            internalSubset = null;
        }
        handler.processDocumentTypeDeclaration(
                dtdInfo.getRootName(),
                dtdInfo.getPublicId(),
                dtdInfo.getSystemId(),
                internalSubset);
    }

    /**
     * The getText() method for a DOCTYPE returns the subset of the DOCTYPE (not the direct
     * infoset). This may force the parser to get information from the network.
     *
     * @return doctype subset
     */
    private String getDTDText() {
        String text = null;
        try {
            text = reader.getText();
        } catch (RuntimeException e) {
            // Woodstox (and perhaps other parsers)
            // attempts to load the external subset even if
            // external enties is false.  So ignore this error
            // if external entity support is explicitly disabled.
            Boolean b =
                    (Boolean) reader.getProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES);
            if (b == null || b.booleanValue()) {
                throw e;
            }
            if (log.isDebugEnabled()) {
                log.debug(
                        "An exception occurred while calling getText() for a DOCTYPE.  "
                                + "The exception is ignored because external "
                                + "entites support is disabled.  "
                                + "The ignored exception is "
                                + e);
            }
        }
        return text;
    }

    /**
     * Pushes the virtual parser ahead one token. If a look ahead token was calculated it is
     * returned.
     *
     * @return next token
     * @throws StreamException
     */
    private int parserNext() throws StreamException {
        if (start) {
            start = false;
            return reader.getEventType();
        } else {
            try {
                if (parserException != null) {
                    log.warn(
                            "Attempt to access a parser that has thrown a parse exception before; "
                                    + "rethrowing the original exception.");
                    if (parserException instanceof XMLStreamException xmlStreamException) {
                        throw xmlStreamException;
                    } else {
                        throw (RuntimeException) parserException;
                    }
                }
                int event;
                try {
                    event = reader.next();
                } catch (XMLStreamException ex) {
                    parserException = ex;
                    throw ex;
                }
                if (autoClose && event == XMLStreamConstants.END_DOCUMENT) {
                    dispose();
                }
                return event;
            } catch (XMLStreamException ex) {
                throw new StreamException(ex);
            }
        }
    }
}
