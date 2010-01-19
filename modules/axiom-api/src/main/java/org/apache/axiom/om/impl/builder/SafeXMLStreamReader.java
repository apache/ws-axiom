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

package org.apache.axiom.om.impl.builder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import org.apache.axiom.util.stax.wrapper.XMLStreamReaderContainer;

/**
 * XMLStreamReader wrapper that prevents access to the underlying parser
 * after the first error occurs.
 * <p>
 * Usually, code that uses StAX directly just stops processing of an XML document
 * once the first parsing error has been reported. However, since Axiom
 * uses deferred parsing, and client code accesses the XML infoset using
 * an object model, things are more complicated. Indeed, if the XML
 * document is not well formed, the corresponding error might be reported
 * as a runtime exception by any call to a method of an OM node.
 * <p>
 * Typically the client code will have some error handling that will intercept
 * runtime exceptions and take appropriate action. Very often this error handling
 * code might want to access the object model again, for example to log the request that caused the
 * failure. This causes no problem except if the runtime exception was caused by a
 * parsing error, in which case Axiom would again try to pull events from the parser.
 * <p>
 * This would lead to a situation where Axiom accesses a parser that has reported a parsing
 * error before. While one would expect that after a first error reported by the parser, all
 * subsequent invocations of the parser will fail, this is not the case for all parsers
 * (at least not in all situations). Instead, the parser might be left in an inconsistent
 * state after the error. E.g. WSCOMMONS-372 describes a case where Woodstox
 * encounters an error in {@link XMLStreamReader#getText()} but continues to return
 * (incorrect) events afterwards. The explanation for this behaviour might be that
 * the situation described here is quite uncommon when StAX is used directly (i.e. not through
 * Axiom).
 * <p>
 * This class provides a simple way to prevent this type of issue by wrapping the underlying
 * parser implementation. After the first parsing error occurs, the wrapper prevents any call
 * to {@link XMLStreamReader#next()} and similar methods on the underlying parser.
 * Any attempt to do so will immediately result in an error.
 */
public class SafeXMLStreamReader extends StreamReaderDelegate implements XMLStreamReaderContainer {
    private boolean parserError;

    public SafeXMLStreamReader(XMLStreamReader reader) {
        super(reader);
    }

    private void checkError() throws XMLStreamException {
        if (parserError) {
            throw new XMLStreamException(
                    "Trying to read events from a parser that already reported an error before");
        }
    }

    public String getElementText() throws XMLStreamException {
        try {
            return super.getElementText();
        } catch (XMLStreamException ex) {
            parserError = true;
            throw ex;
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }

    public String getPIData() {
        try {
            return super.getPIData();
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }

    public String getText() {
        try {
            return super.getText();
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }

    public char[] getTextCharacters() {
        try {
            return super.getTextCharacters();
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        checkError();
        try {
            return super.getTextCharacters(sourceStart, target, targetStart, length);
        } catch (XMLStreamException ex) {
            parserError = true;
            throw ex;
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }

    public int getTextLength() {
        try {
            return super.getTextLength();
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }

    public int getTextStart() {
        try {
            return super.getTextStart();
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }

    public boolean hasNext() throws XMLStreamException {
        checkError();
        try {
            return super.hasNext();
        } catch (XMLStreamException ex) {
            parserError = true;
            throw ex;
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }

    public int next() throws XMLStreamException {
        checkError();
        try {
            return super.next();
        } catch (XMLStreamException ex) {
            parserError = true;
            throw ex;
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }

    public int nextTag() throws XMLStreamException {
        checkError();
        try {
            return super.nextTag();
        } catch (XMLStreamException ex) {
            parserError = true;
            throw ex;
        } catch (RuntimeException ex) {
            parserError = true;
            throw ex;
        } catch (Error ex) {
            parserError = true;
            throw ex;
        }
    }
}
