/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.axiom.core.stream.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.axiom.core.stream.CharacterData;
import org.apache.axiom.core.stream.CharacterDataSink;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.serializer.writer.UnmappableCharacterHandler;
import org.apache.axiom.core.stream.serializer.writer.WriterXmlWriter;
import org.apache.axiom.core.stream.serializer.writer.XmlWriter;
import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;

/**
 * This abstract class is a base class for other stream 
 * serializers (xml, html, text ...) that write output to a stream.
 * 
 * @xsl.usage internal
 */
public final class Serializer implements XmlHandler, CharacterDataSink {
    /**
     * The number of characters to process at once. Chosen small enough to leverage processor caches
     * and large enough to reduce method invocation overhead.
     */
    private static final int CHUNK_SIZE = 4096;
    
    private static final int MIXED_CONTENT = 0;
    private static final int TAG = 1;
    private static final int ATTRIBUTE_VALUE = 2;
    private static final int COMMENT = 3;
    private static final int PROCESSING_INSTRUCTION = 4;
    private static final int CDATA_SECTION = 5;

    private static final String[] illegalCharacterSequences = { null, null, null, "--", "?>", "]]>" };

    private static final UnmappableCharacterHandler[] unmappableCharacterHandlers = {
            UnmappableCharacterHandler.CONVERT_TO_CHARACTER_REFERENCE,
            UnmappableCharacterHandler.THROW_EXCEPTION,
            UnmappableCharacterHandler.CONVERT_TO_CHARACTER_REFERENCE,
            UnmappableCharacterHandler.THROW_EXCEPTION,
            UnmappableCharacterHandler.THROW_EXCEPTION,
            UnmappableCharacterHandler.THROW_EXCEPTION };
    
    private final XmlWriter writer;
    private final OutputStream outputStream;
    
    /**
     * Add space before '/>' for XHTML.
     */
    protected boolean spaceBeforeClose = false;

    /**
     * Tells if we're in an internal document type subset.
     */
    protected boolean inDoctype = false;

    private int context = MIXED_CONTENT;
    private int matchedIllegalCharacters;

    /**
     * Tracks the number of consecutive square brackets so that the '>' in ']]>' can be replaced by
     * a character reference.
     */
    private int squareBrackets;

    private String[] elementNameStack = new String[8];
    private int depth;
    private boolean startTagOpen;

    /**
     * A utility buffer for converting Strings passed to
     * character() methods to character arrays.
     * Reusing this buffer means not creating a new character array
     * everytime and it runs faster.
     */
    private final char[] charsBuff = new char[CHUNK_SIZE];

    public Serializer(Writer out) {
        writer = new WriterXmlWriter(out);
        outputStream = null;
    }

    public Serializer(OutputStream out, String encoding) {
        writer = XmlWriter.create(out, encoding);
        outputStream = out;
    }

    private void switchContext(int context) throws StreamException {
        this.context = context;
        try {
            writer.setUnmappableCharacterHandler(unmappableCharacterHandlers[context]);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        matchedIllegalCharacters = 0;
        squareBrackets = 0;
    }

    /**
     * Get the output stream events are serialized to. This method will close any open start tag and
     * flush all pending data before returning the output stream.
     *
     * @return the output stream, or {@code null} if this serializer is not writing to an output
     *         stream (but to a {@link Writer} e.g.)
     * @throws StreamException
     */
    public OutputStream getOutputStream() throws StreamException {
        if (outputStream != null) {
            closeStartTag();
            flushBuffer();
            return outputStream;
        } else {
            return null;
        }
    }

    /**
     *   Report an element type declaration.
     *  
     *   <p>The content model will consist of the string "EMPTY", the
     *   string "ANY", or a parenthesised group, optionally followed
     *   by an occurrence indicator.  The model will be normalized so
     *   that all whitespace is removed,and will include the enclosing
     *   parentheses.</p>
     *  
     *   @param name The element type name.
     *   @param model The content model as a normalized string.
     *   @exception StreamException The application may raise an exception.
     */
    public void elementDecl(String name, String model) throws StreamException {
        try {
            DTDprolog();
            writer.write("<!ELEMENT ");
            writer.write(name);
            writer.write(' ');
            writer.write(model);
            writer.write(">\n");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    /**
     * Report an internal entity declaration.
     *
     * <p>Only the effective (first) declaration for each entity
     * will be reported.</p>
     *
     * @param name The name of the entity.  If it is a parameter
     *        entity, the name will begin with '%'.
     * @param value The replacement text of the entity.
     * @exception StreamException The application may raise an exception.
     * @see #externalEntityDecl
     * @see org.xml.sax.DTDHandler#unparsedEntityDecl
     */
    public void internalEntityDecl(String name, String value) throws StreamException {
        try {
            DTDprolog();
            outputEntityDecl(name, value);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    /**
     * Output the doc type declaration.
     *
     * @param name non-null reference to document type name.
     * NEEDSDOC @param value
     *
     * @throws StreamException
     */
    private void outputEntityDecl(String name, String value) throws IOException {
        writer.write("<!ENTITY ");
        writer.write(name);
        writer.write(" \"");
        writer.write(value);
        writer.write("\">\n");
    }

    @Override
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            Boolean standalone) throws StreamException {
        switchContext(TAG);
        try {
            writer.write("<?xml version=\"");
            writer.write(xmlVersion == null ? "1.0" : xmlVersion);
            writer.write('"');
            if (xmlEncoding != null) {
                writer.write(" encoding=\"");
                writer.write(xmlEncoding);
                writer.write('"');
            }
            if (standalone != null) {
                writer.write(" standalone=\"");
                writer.write(standalone ? "yes" : "no");
                writer.write('"');
            }
            writer.write("?>");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(MIXED_CONTENT);
    }

    @Override
    public void startFragment() throws StreamException {
    }

    /**
     * Report an attribute type declaration.
     *
     * <p>Only the effective (first) declaration for an attribute will
     * be reported.  The type will be one of the strings "CDATA",
     * "ID", "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY",
     * "ENTITIES", or "NOTATION", or a parenthesized token group with
     * the separator "|" and all whitespace removed.</p>
     *
     * @param eName The name of the associated element.
     * @param aName The name of the attribute.
     * @param type A string representing the attribute type.
     * @param valueDefault A string representing the attribute default
     *        ("#IMPLIED", "#REQUIRED", or "#FIXED") or null if
     *        none of these applies.
     * @param value A string representing the attribute's default value,
     *        or null if there is none.
     * @exception StreamException The application may raise an exception.
     */
    public void attributeDecl(String eName, String aName, String type, String valueDefault,
            String value) throws StreamException {
        try {
            DTDprolog();
            writer.write("<!ATTLIST ");
            writer.write(eName);
            writer.write(' ');
            writer.write(aName);
            writer.write(' ');
            writer.write(type);
            if (valueDefault != null) {
                writer.write(' ');
                writer.write(valueDefault);
            }
            //writer.write(" ");
            //writer.write(value);
            writer.write(">\n");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    /**
     * Report a parsed external entity declaration.
     *
     * <p>Only the effective (first) declaration for each entity
     * will be reported.</p>
     *
     * @param name The name of the entity.  If it is a parameter
     *        entity, the name will begin with '%'.
     * @param publicId The declared public identifier of the entity, or
     *        null if none was declared.
     * @param systemId The declared system identifier of the entity.
     * @exception StreamException The application may raise an exception.
     * @see #internalEntityDecl
     * @see org.xml.sax.DTDHandler#unparsedEntityDecl
     */
    public void externalEntityDecl(String name, String publicId, String systemId) throws StreamException {
        try {
            DTDprolog();
            writer.write("<!ENTITY ");
            writer.write(name);
            if (publicId != null) {
                writer.write(" PUBLIC \"");
                writer.write(publicId);
            } else {
                writer.write(" SYSTEM \"");
                writer.write(systemId);
            }
            writer.write("\" >\n");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    /**
     * Receive notification of character data.
     *
     * <p>The Parser will call this method to report each chunk of
     * character data.  SAX parsers may return all contiguous character
     * data in a single chunk, or they may split it into several
     * chunks; however, all of the characters in any single event
     * must come from the same external entity, so that the Locator
     * provides useful information.</p>
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * <p>Note that some parsers will report whitespace using the
     * ignorableWhitespace() method rather than this one (validating
     * parsers must do so).</p>
     *
     * @param chars The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     * @throws StreamException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #ignorableWhitespace
     * @see org.xml.sax.Locator
     *
     * @throws StreamException
     */
    public void characters(char chars[], int start, int length) throws StreamException {
        // It does not make sense to continue with rest of the method if the number of 
        // characters to read from array is 0.
        // Section 7.6.1 of XSLT 1.0 (http://www.w3.org/TR/xslt#value-of) suggest no text node
        // is created if string is empty.
        if (length == 0)
            return;
        
        final XmlWriter writer = this.writer;
        final int context = this.context;
        final String illegalCharacterSequence = illegalCharacterSequences[context];
        
        try {
            int i;
            
            final int end = start + length;
            int lastDirtyCharProcessed = start - 1; // last non-clean character that was processed
                                                    // that was processed
            int matchedIllegalCharacters = this.matchedIllegalCharacters;
            int squareBrackets = this.squareBrackets;
            for (i = start; i < end; i++) {
                char ch = chars[i];
                
                if (illegalCharacterSequence != null) {
                    while (true) {
                        if (ch == illegalCharacterSequence.charAt(matchedIllegalCharacters)) {
                            if (++matchedIllegalCharacters == illegalCharacterSequence.length()) {
                                throw new IllegalCharacterSequenceException("Illegal character sequence \"" + illegalCharacterSequence + "\"");
                            }
                            break;
                        } else if (matchedIllegalCharacters > 0) {
                            int offset = 1;
                            loop: while (offset < matchedIllegalCharacters) {
                                for (int j = 0; j < matchedIllegalCharacters - offset; j++) {
                                    if (illegalCharacterSequence.charAt(j) != illegalCharacterSequence.charAt(j+offset)) {
                                        offset++;
                                        continue loop;
                                    }
                                }
                                break;
                            }
                            matchedIllegalCharacters -= offset;
                        } else {
                            break;
                        }
                    }
                }
                
                String replacement = null;
                boolean generateCharacterReference = false;
                
                if (context == MIXED_CONTENT || context == ATTRIBUTE_VALUE) {
                    if (ch <= 0x1F) {
                        // Range 0x00 through 0x1F inclusive
                        //
                        // This covers the non-whitespace control characters
                        // in the range 0x1 to 0x1F inclusive.
                        // It also covers the whitespace control characters in the same way:
                        // 0x9   TAB
                        // 0xA   NEW LINE
                        // 0xD   CARRIAGE RETURN
                        //
                        // We also cover 0x0 ... It isn't valid
                        // but we will output "&#0;" 
                        
                        // The default will handle this just fine, but this
                        // is a little performance boost to handle the more
                        // common TAB, NEW-LINE, CARRIAGE-RETURN
                        switch (ch) {
                            case 0x09:
                                if (context == ATTRIBUTE_VALUE) {
                                    replacement = "&#9;";
                                }
                                break;
                            case 0x0A:
                                if (context == ATTRIBUTE_VALUE) {
                                    replacement = "&#10;";
                                }
                                break;
                            case 0x0D:
                                replacement = "&#13;";
                                // Leave whitespace carriage return as a real character
                                break;
                            default:
                                generateCharacterReference = true;
                                break;
                        }
                    } else if (ch < 0x7F) {
                        switch (ch) {
                            case '<':
                                replacement = "&lt;";
                                break;
                            case '>':
                                if (context == MIXED_CONTENT && squareBrackets >= 2) {
                                    replacement = "&gt;";
                                }
                                break;
                            case '&':
                                replacement = "&amp;";
                                break;
                            case '"':
                                if (context == ATTRIBUTE_VALUE) {
                                    replacement = "&quot;";
                                }
                        }
                    } else if (ch <= 0x9F) {
                        // Range 0x7F through 0x9F inclusive
                        // More control characters, including NEL (0x85)
                        generateCharacterReference = true;
                    } else if (ch == 0x2028) {
                        // LINE SEPARATOR
                        replacement = "&#8232;";
                    }
                    
                    if (ch == ']') {
                        squareBrackets++;
                    } else {
                        squareBrackets = 0;
                    }
                }
                
                int startClean = lastDirtyCharProcessed + 1;
                int lengthClean = i - startClean;
                if (replacement != null || generateCharacterReference || lengthClean == CHUNK_SIZE) {
                    if (startClean < i) {
                        writer.write(chars, startClean, lengthClean);
                    }
                    if (replacement != null) {
                        writer.write(replacement);
                    } else if (generateCharacterReference) {
                        writer.writeCharacterReference(ch);
                    }
                    lastDirtyCharProcessed = i;
                }
            }
            
            // we've reached the end. Any clean characters at the
            // end of the array than need to be written out?
            int startClean = lastDirtyCharProcessed + 1;
            if (i > startClean) {
                int lengthClean = i - startClean;
                writer.write(chars, startClean, lengthClean);
            }
            this.matchedIllegalCharacters = matchedIllegalCharacters;
            this.squareBrackets = squareBrackets;
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    private void characters(String s) throws StreamException {
        characters(s, 0, s.length());
    }

    void characters(String s, int start, int length) throws StreamException {
        while (length > 0) {
            int count = Math.min(length, CHUNK_SIZE);
            s.getChars(start, start+count, charsBuff, 0);
            characters(charsBuff, 0, count);
            start += count;
            length -= count;
        }
    }

    @Override
    public Writer getWriter() {
        return new SerializerWriter(this);
    }

    @Override
    public AbstractBase64EncodingOutputStream getBase64EncodingOutputStream() {
        return writer.getBase64EncodingOutputStream();
    }

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        closeStartTag();
        if (data instanceof CharacterData) {
            try {
                ((CharacterData)data).writeTo(this);
            } catch (IOException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof StreamException) {
                    throw (StreamException)cause;
                } else {
                    throw new StreamException(ex);
                }
            }
        } else {
            characters(data.toString());
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        closeStartTag();
        try
        {
            switchContext(TAG);
            writer.write('<');
            if (!prefix.isEmpty()) {
                writer.write(prefix);
                writer.write(':');
            }
            writer.write(localName);
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }
        if (2*(depth+1) > elementNameStack.length) {
            String[] newElementNameStack = new String[elementNameStack.length*2];
            System.arraycopy(elementNameStack, 0, newElementNameStack, 0, elementNameStack.length);
            elementNameStack = newElementNameStack;
        }
        elementNameStack[2*depth] = prefix;
        elementNameStack[2*depth+1] = localName;
        depth++;
        startTagOpen = true;
    }

    @Override
    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId, String internalSubset) throws StreamException {
        startDTD(rootName, publicId, systemId);
        if (internalSubset != null) {
            writeInternalSubset(internalSubset);
        }
        endDTD();
    }

    public void startDTD(String name, String publicId, String systemId) throws StreamException
    {
        inDoctype = true;
        try {
            writer.write("<!DOCTYPE ");
            writer.write(name);
            if (publicId != null) {
                writer.write(" PUBLIC \"");
                writer.write(publicId);
                writer.write('\"');
            }
            if (systemId != null) {
                if (publicId == null) {
                    writer.write(" SYSTEM \"");
                } else {
                    writer.write(" \"");
                }
                writer.write(systemId);
                writer.write('\"');
            }
        }
        catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    private void writeAttribute(String prefix, String localName, String value) throws StreamException {
        try {
            writer.write(' ');
            if (!prefix.isEmpty()) {
                writer.write(prefix);
                writer.write(':');
            }
            writer.write(localName);
            writer.write("=\"");
            if (!value.isEmpty()) {
                switchContext(ATTRIBUTE_VALUE);
                characters(value);
                switchContext(TAG);
            }
            writer.write('\"');
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        if (prefix.isEmpty()) {
            writeAttribute("", "xmlns", namespaceURI);
        } else {
            writeAttribute("xmlns", prefix, namespaceURI);
        }
    }

    @Override
    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        writeAttribute(prefix, localName, value);
    }

    @Override
    public void processAttribute(String name, String value, String type, boolean specified) throws StreamException {
        writeAttribute("", name, value);
    }

    @Override
    public void attributesCompleted() throws StreamException {
    }

    @Override
    public void endElement() throws StreamException {
        depth--;
        try {
            if (startTagOpen) {
                if (spaceBeforeClose) {
                    writer.write(" />");
                } else {
                    writer.write("/>");
                }
            } else {
                switchContext(TAG);
                writer.write("</");
                String prefix = elementNameStack[2*depth];
                if (!prefix.isEmpty()) {
                    writer.write(prefix);
                    writer.write(':');
                }
                writer.write(elementNameStack[2*depth+1]);
                writer.write('>');
                switchContext(MIXED_CONTENT);
            }
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        startTagOpen = false;
    }

    @Override
    public void startComment() throws StreamException {
        closeStartTag();
        try {
            writer.write("<!--");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(COMMENT);
    }

    @Override
    public void endComment() throws StreamException {
        try {
            writer.write("-->");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(MIXED_CONTENT);
    }

    @Override
    public void endCDATASection() throws StreamException
    {
        try {
            writer.write("]]>");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(MIXED_CONTENT);
    }

    /**
     * Report the end of DTD declarations.
     * @throws StreamException The application may raise an exception.
     * @see #startDTD
     */
    public void endDTD() throws StreamException {
        try {
            if (!inDoctype) {
                writer.write("]>");
            } else {
                writer.write('>');
            }
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void startCDATASection() throws StreamException {
        closeStartTag();
        try {
            writer.write("<![CDATA[");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(CDATA_SECTION);
    }

    /**
     * For the enclosing elements starting tag write out
     * out any attributes followed by ">"
     *
     * @throws StreamException
     */
    private void closeStartTag() throws StreamException {
        if (startTagOpen) {
            try {
                writer.write('>');
                switchContext(MIXED_CONTENT);
            } catch (IOException ex) {
                throw new StreamException(ex);
            }
            startTagOpen = false;
        }
    }

    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        closeStartTag();
        switchContext(TAG);
        try {
            writer.write("<?");
            writer.write(target);
            writer.write(' ');
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(PROCESSING_INSTRUCTION);
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        try {
            writer.write("?>");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(MIXED_CONTENT);
    }

    public void writeInternalSubset(String internalSubset) throws StreamException {
        try {
            DTDprolog();
            writer.write(internalSubset);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    public void flushBuffer() throws StreamException {
        try {
            writer.flushBuffer();
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    public void notationDecl(String name, String pubID, String sysID) throws StreamException {
        try {
            DTDprolog();
            writer.write("<!NOTATION ");
            writer.write(name);
            if (pubID != null) {
                writer.write(" PUBLIC \"");
                writer.write(pubID);
            } else {
                writer.write(" SYSTEM \"");
                writer.write(sysID);
            }
            writer.write("\" >\n");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    public void unparsedEntityDecl(String name, String pubID, String sysID, String notationName) throws StreamException {
        try {
            DTDprolog();
            writer.write("<!ENTITY ");
            writer.write(name);
            if (pubID != null) {
                writer.write(" PUBLIC \"");
                writer.write(pubID);
            } else {
                writer.write(" SYSTEM \"");
                writer.write(sysID);
            }
            writer.write("\" NDATA ");
            writer.write(notationName);
            writer.write(" >\n");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }
    
    /**
     * A private helper method to output the 
     * @throws StreamException
     * @throws IOException
     */
    private void DTDprolog() throws IOException {
        if (inDoctype) {
            writer.write(" [\n");
            inDoctype = false;
        }
    }
    
    public void writeRaw(String s, UnmappableCharacterHandler unmappableCharacterHandler) throws StreamException {
        try {
            writer.setUnmappableCharacterHandler(unmappableCharacterHandler);
            writer.write(s);
            writer.setUnmappableCharacterHandler(unmappableCharacterHandlers[context]);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void processEntityReference(String name, String replacementText) throws StreamException {
        closeStartTag();
        try {
            writer.write('&');
            writer.write(name);
            writer.write(';');
        } catch(IOException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void completed() throws StreamException {
        flushBuffer();
    }

    @Override
    public boolean drain() throws StreamException {
        return true;
    }
}
