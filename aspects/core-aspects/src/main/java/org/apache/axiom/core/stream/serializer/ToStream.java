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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.serializer.writer.UnmappableCharacterHandler;
import org.apache.axiom.core.stream.serializer.writer.WriterXmlWriter;
import org.apache.axiom.core.stream.serializer.writer.XmlWriter;

/**
 * This abstract class is a base class for other stream 
 * serializers (xml, html, text ...) that write output to a stream.
 * 
 * @xsl.usage internal
 */
public final class ToStream extends SerializerBase
{

    private static final String COMMENT_BEGIN = "<!--";
    private static final String COMMENT_END = "-->";

    private final XmlWriter m_writer;
    private final OutputStream outputStream;
    
    /**
     * Map that tells which characters should have special treatment, and it
     *  provides character to entity name lookup.
     */
    protected CharInfo m_charInfo = CharInfo.getCharInfo(CharInfo.XML_ENTITIES_RESOURCE);

    /**
     * Add space before '/>' for XHTML.
     */
    protected boolean m_spaceBeforeClose = false;

    /**
     * Tells if we're in an internal document type subset.
     */
    protected boolean m_inDoctype = false;

    protected Context context = Context.MIXED_CONTENT;
    private int matchedIllegalCharacters;
    private boolean startTagOpen;

    public ToStream(Writer out) {
        m_writer = new WriterXmlWriter(out);
        outputStream = null;
    }

    public ToStream(OutputStream out, String encoding) {
        m_writer = XmlWriter.create(out, encoding);
        outputStream = out;
    }

    protected void switchContext(Context context) throws StreamException {
        this.context = context;
        try {
            m_writer.setUnmappableCharacterHandler(context.getUnmappableCharacterHandler());
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        matchedIllegalCharacters = 0;
    }

    /**
     * Get the output stream where the events will be serialized to.
     *
     * @return reference to the result stream, or null of only a writer was
     * set.
     */
    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    // Implement DeclHandler

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
    public void elementDecl(String name, String model) throws StreamException
    {
        try
        {
            final XmlWriter writer = m_writer;
            DTDprolog();

            writer.write("<!ELEMENT ");
            writer.write(name);
            writer.write(' ');
            writer.write(model);
            writer.write(">\n");
        }
        catch (IOException e)
        {
            throw new StreamException(e);
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
    public void internalEntityDecl(String name, String value)
        throws StreamException
    {
        try
        {
            DTDprolog();
            outputEntityDecl(name, value);
        }
        catch (IOException e)
        {
            throw new StreamException(e);
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
    void outputEntityDecl(String name, String value) throws IOException
    {
        final XmlWriter writer = m_writer;
        writer.write("<!ENTITY ");
        writer.write(name);
        writer.write(" \"");
        writer.write(value);
        writer.write("\">\n");
    }

    /**
     * Specifies an output format for this serializer. It the
     * serializer has already been associated with an output format,
     * it will switch to the new format. This method should not be
     * called while the serializer is in the process of serializing
     * a document.
     *
     * @param format The output format to use
     */
    public void setOutputFormat(Properties format)
    {
        if (format != null)
        {
            // Set the default values first,
            // and the non-default values after that, 
            // just in case there is some unexpected
            // residual values left over from over-ridden default values
            Enumeration propNames;
            propNames = format.propertyNames();
            while (propNames.hasMoreElements())
            {
                String key = (String) propNames.nextElement();
                // Get the value, possibly a default value
                String value = format.getProperty(key);
                // Get the non-default value (if any).
                String explicitValue = (String) format.get(key);
                if (explicitValue == null && value != null) {
                    // This is a default value
                    this.setOutputPropertyDefault(key,value);
                }
                if (explicitValue != null) {
                    // This is an explicit non-default value
                    this.setOutputProperty(key,explicitValue);
                }
            } 
        }   

        // Access this only from the Hashtable level... we don't want to 
        // get default properties.
        String entitiesFileName =
            (String) format.get(OutputPropertiesFactory.S_KEY_ENTITIES);

        if (null != entitiesFileName)
        {

            m_charInfo = CharInfo.getCharInfo(entitiesFileName);
        }
    }

    /**
     * Returns the output format for this serializer.
     *
     * @return The output format in use
     */
    public Properties getOutputFormat() {
        Properties def = new Properties();
        {
            Set s = getOutputPropDefaultKeys();
            Iterator i = s.iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                String val = getOutputPropertyDefault(key);
                def.put(key, val);
            }
        }
        
        Properties props = new Properties(def);
        {
            Set s = getOutputPropKeys();
            Iterator i = s.iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                String val = getOutputPropertyNonDefault(key);
                if (val != null)
                    props.put(key, val);
            }
        }
        return props;
    }

    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            Boolean standalone) throws StreamException {
        switchContext(Context.TAG);
        try {
            final XmlWriter writer = m_writer;
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
        switchContext(Context.MIXED_CONTENT);
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
    public void attributeDecl(
        String eName,
        String aName,
        String type,
        String valueDefault,
        String value)
        throws StreamException
    {
        try
        {
            final XmlWriter writer = m_writer;
            DTDprolog();

            writer.write("<!ATTLIST ");
            writer.write(eName);
            writer.write(' ');

            writer.write(aName);
            writer.write(' ');
            writer.write(type);
            if (valueDefault != null)
            {
                writer.write(' ');
                writer.write(valueDefault);
            }

            //writer.write(" ");
            //writer.write(value);
            writer.write(">\n");
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }
    }

    /**
     * Get the character stream where the events will be serialized to.
     *
     * @return Reference to the result Writer, or null.
     */
    public XmlWriter getWriter()
    {
        return m_writer;
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
    public void externalEntityDecl(
        String name,
        String publicId,
        String systemId)
        throws StreamException
    {
        try {
            DTDprolog();
            
            m_writer.write("<!ENTITY ");            
            m_writer.write(name);
            if (publicId != null) {
                m_writer.write(" PUBLIC \"");
                m_writer.write(publicId);
  
            }
            else {
                m_writer.write(" SYSTEM \"");
                m_writer.write(systemId);
            }
            m_writer.write("\" >\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
    public void characters(final char chars[], final int start, final int length)
        throws StreamException
    {
        // It does not make sense to continue with rest of the method if the number of 
        // characters to read from array is 0.
        // Section 7.6.1 of XSLT 1.0 (http://www.w3.org/TR/xslt#value-of) suggest no text node
        // is created if string is empty.	
        if (length == 0)
            return;
            
        m_docIsEmpty = false;
        
        String illegalCharacterSequence = context.getIllegalCharacterSequence();
        if (illegalCharacterSequence != null) {
            int matchedIllegalCharacters = this.matchedIllegalCharacters;
            for (int i = 0; i < length; i++) {
                while (true) {
                    if (chars[start+i] == illegalCharacterSequence.charAt(matchedIllegalCharacters)) {
                        if (++matchedIllegalCharacters == illegalCharacterSequence.length()) {
                            throw new IllegalCharacterSequenceException(context);
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
            this.matchedIllegalCharacters = matchedIllegalCharacters;
        }
        
        if (context == Context.CDATA_SECTION || context == Context.COMMENT || context == Context.PROCESSING_INSTRUCTION) {
            // TODO: this doesn't take care of illegal characters
            try {
                m_writer.write(chars, start, length);
            } catch (IOException ex) {
                throw new StreamException(ex);
            }
            return;
        }

        try
        {
            int i;
            int startClean;
            
            // skip any leading whitspace 
            // don't go off the end and use a hand inlined version
            // of isWhitespace(ch)
            final int end = start + length;
            int lastDirtyCharProcessed = start - 1; // last non-clean character that was processed
													// that was processed
            final XmlWriter writer = m_writer;
            boolean isAllWhitespace = true;

            // process any leading whitspace
            i = start;
            while (i < end && isAllWhitespace) {
                char ch1 = chars[i];

                if (m_charInfo.shouldMapTextChar(ch1)) {
                    // The character is supposed to be replaced by a String
                    // so write out the clean whitespace characters accumulated
                    // so far
                    // then the String.
                    writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                    String outputStringForChar = m_charInfo
                            .getOutputStringForChar(ch1);
                    writer.write(outputStringForChar);
                    // We can't say that everything we are writing out is
                    // all whitespace, we just wrote out a String.
                    isAllWhitespace = false;
                    lastDirtyCharProcessed = i; // mark the last non-clean
                    // character processed
                    i++;
                } else {
                    // The character is clean, but is it a whitespace ?
                    switch (ch1) {
                    // TODO: Any other whitespace to consider?
                    case CharInfo.S_SPACE:
                    case CharInfo.S_LINEFEED:
                        // Just accumulate the clean whitespace
                        i++;
                        break;
                    case CharInfo.S_CARRIAGERETURN:
                        writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        writer.write("&#13;");
                        lastDirtyCharProcessed = i;
                        i++;
                        break;
                    case CharInfo.S_HORIZONAL_TAB:
                        // Just accumulate the clean whitespace
                        i++;
                        break;
                    default:
                        // The character was clean, but not a whitespace
                        // so break the loop to continue with this character
                        // (we don't increment index i !!)
                        isAllWhitespace = false;
                        break;
                    }
                }
            }

            for (; i < end; i++)
            {
                char ch = chars[i];
                
                if (m_charInfo.shouldMapTextChar(ch)) {
                    // The character is supposed to be replaced by a String
                    // e.g.   '&'  -->  "&amp;"
                    // e.g.   '<'  -->  "&lt;"
                    writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                    String outputStringForChar = m_charInfo.getOutputStringForChar(ch);
                    writer.write(outputStringForChar);
                    lastDirtyCharProcessed = i;
                }
                else {
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

                        case CharInfo.S_HORIZONAL_TAB:
                        case CharInfo.S_LINEFEED:
                            // Leave whitespace as a real character
                            break;
                        case CharInfo.S_CARRIAGERETURN:
                        	writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        	writer.write("&#13;");
                        	lastDirtyCharProcessed = i;
                            // Leave whitespace carriage return as a real character
                            break;
                        default:
                            writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                            writer.writeCharacterReference(ch);
                            lastDirtyCharProcessed = i;
                            break;

                        }
                    }
                    else if (ch < 0x7F) {  
                        // Range 0x20 through 0x7E inclusive
                        // Normal ASCII chars, do nothing, just add it to
                        // the clean characters
                            
                    }
                    else if (ch <= 0x9F){
                        // Range 0x7F through 0x9F inclusive
                        // More control characters, including NEL (0x85)
                        writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        writer.writeCharacterReference(ch);
                        lastDirtyCharProcessed = i;
                    }
                    else if (ch == CharInfo.S_LINE_SEPARATOR) {
                        // LINE SEPARATOR
                        writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        writer.write("&#8232;");
                        lastDirtyCharProcessed = i;
                    }
                }
            }
            
            // we've reached the end. Any clean characters at the
            // end of the array than need to be written out?
            startClean = lastDirtyCharProcessed + 1;
            if (i > startClean)
            {
                int lengthClean = i - startClean;
                m_writer.write(chars, startClean, lengthClean);
            }
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }
    }

	private void writeOutCleanChars(final char[] chars, int i, int lastProcessed) throws IOException {
        int startClean;
        startClean = lastProcessed + 1;
        if (startClean < i)
        {
            int lengthClean = i - startClean;
            m_writer.write(chars, startClean, lengthClean);
        }
    }     
    /**
     * This method checks if a given character is between C0 or C1 range
     * of Control characters.
     * This method is added to support Control Characters for XML 1.1
     * If a given character is TAB (0x09), LF (0x0A) or CR (0x0D), this method
     * return false. Since they are whitespace characters, no special processing is needed.
     * 
     * @param ch
     * @return boolean
     */
    private static boolean isCharacterInC0orC1Range(char ch)
    {
        if(ch == 0x09 || ch == 0x0A || ch == 0x0D)
        	return false;
        else        	    	
        	return (ch >= 0x7F && ch <= 0x9F)|| (ch >= 0x01 && ch <= 0x1F);
    }
    /**
     * This method checks if a given character either NEL (0x85) or LSEP (0x2028)
     * These are new end of line charcters added in XML 1.1.  These characters must be
     * written as Numeric Character References (NCR) in XML 1.1 output document.
     * 
     * @param ch
     * @return boolean
     */
    private static boolean isNELorLSEPCharacter(char ch)
    {
        return (ch == 0x85 || ch == 0x2028);
    }

    /**
     * Receive notification of character data.
     *
     * @param s The string of characters to process.
     *
     * @throws StreamException
     */
    public void characters(String s) throws StreamException
    {
        final int length = s.length();
        if (length > m_charsBuff.length)
        {
            m_charsBuff = new char[length * 2 + 1];
        }
        s.getChars(0, length, m_charsBuff, 0);
        characters(m_charsBuff, 0, length);
    }

    /**
     * Receive notification of the beginning of an element, although this is a
     * SAX method additional namespace or attribute information can occur before
     * or after this call, that is associated with this element.
     *
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param name The element type name.
     * @param atts The attributes attached to the element, if any.
     * @throws StreamException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.AttributeList
     *
     * @throws StreamException
     */
    public void startElement(
        String namespaceURI,
        String localName,
        String name)
        throws StreamException
    {
        try
        {
            final XmlWriter writer = m_writer;
            switchContext(Context.TAG);
            writer.write('<');
            writer.write(name);
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }

        m_elemContext = m_elemContext.push(namespaceURI,localName,name);
        startTagOpen = true;
    }

    public void startElement(String elementName) throws StreamException
    {
        startElement(null, null, elementName);
    }

    public void startDTD(String name, String publicId, String systemId) throws StreamException
    {
        m_inDoctype = true;
        try
        {
            final XmlWriter writer = m_writer;
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
        catch (IOException e)
        {
            throw new StreamException(e);
        }
    }

    public void writeAttribute(String prefix, String localName, String value) throws StreamException {
        try {
            final XmlWriter writer = m_writer;
            writer.write(' ');
            if (!prefix.isEmpty()) {
                writer.write(prefix);
                writer.write(':');
            }
            writer.write(localName);
            writer.write("=\"");
            writeAttrString(writer, value);
            writer.write('\"');
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    /**
     * Returns the specified <var>string</var> after substituting <VAR>specials</VAR>,
     * and UTF-16 surrogates for chracter references <CODE>&amp;#xnn</CODE>.
     *
     * @param   string      String to convert to XML format.
     *
     * @throws java.io.IOException
     */
    public void writeAttrString(
        XmlWriter writer,
        String string)
        throws IOException, StreamException
    {
        switchContext(Context.ATTRIBUTE_VALUE);
        final int len = string.length();
        if (len > m_attrBuff.length)
        {
           m_attrBuff = new char[len*2 + 1];             
        }
        string.getChars(0,len, m_attrBuff, 0);   
        final char[] stringChars = m_attrBuff;

        for (int i = 0; i < len; i++)
        {
            char ch = stringChars[i];
            
            if (m_charInfo.shouldMapAttrChar(ch)) {
                // The character is supposed to be replaced by a String
                // e.g.   '&'  -->  "&amp;"
                // e.g.   '<'  -->  "&lt;"
                writer.write(m_charInfo.getOutputStringForChar(ch));
            }
            else {
                if (0x0 <= ch && ch <= 0x1F) {
                    // Range 0x00 through 0x1F inclusive
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

                    case CharInfo.S_HORIZONAL_TAB:
                        writer.write("&#9;");
                        break;
                    case CharInfo.S_LINEFEED:
                        writer.write("&#10;");
                        break;
                    case CharInfo.S_CARRIAGERETURN:
                        writer.write("&#13;");
                        break;
                    default:
                        writer.writeCharacterReference(ch);
                        break;

                    }
                }
                else if (ch < 0x7F) {   
                    // Range 0x20 through 0x7E inclusive
                    // Normal ASCII chars
                        writer.write(ch);
                }
                else if (ch <= 0x9F){
                    // Range 0x7F through 0x9F inclusive
                    // More control characters
                    writer.writeCharacterReference(ch);
                }
                else if (ch == CharInfo.S_LINE_SEPARATOR) {
                    // LINE SEPARATOR
                    writer.write("&#8232;");
                }
                else {
                    writer.write(ch);
                }
            }
        }
        switchContext(Context.TAG);
    }

    /**
     * Receive notification of the end of an element.
     *
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param name The element type name
     * @throws StreamException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws StreamException
     */
    public void endElement(String namespaceURI, String localName, String name)
        throws StreamException
    {
        try
        {
            final XmlWriter writer = m_writer;
            if (startTagOpen) {
                if (m_spaceBeforeClose)
                    writer.write(" />");
                else
                    writer.write("/>");
                /* don't need to pop cdataSectionState because
                 * this element ended so quickly that we didn't get
                 * to push the state.
                 */

            }
            else
            {
                switchContext(Context.TAG);
                writer.write('<');
                writer.write('/');
                writer.write(name);
                writer.write('>');
                switchContext(Context.MIXED_CONTENT);
            }
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }

        m_elemContext = m_elemContext.m_prev;
        startTagOpen = false;
    }

    /**
     * Receive notification of the end of an element.
     * @param name The element type name
     * @throws StreamException Any SAX exception, possibly
     *     wrapping another exception.
     */
    public void endElement(String name) throws StreamException
    {
        endElement(null, null, name);
    }

    public void startComment() throws StreamException {
        try {
            m_writer.write(COMMENT_BEGIN);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(Context.COMMENT);
    }

    public void endComment() throws StreamException {
        try {
            m_writer.write(COMMENT_END);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(Context.MIXED_CONTENT);
    }

    /**
     * Report the end of a CDATA section.
     * @throws StreamException The application may raise an exception.
     *
     *  @see  #startCDATA
     */
    public void endCDATA() throws StreamException
    {
        try {
            m_writer.write(CDATA_DELIMITER_CLOSE);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(Context.MIXED_CONTENT);
    }

    /**
     * Report the end of DTD declarations.
     * @throws StreamException The application may raise an exception.
     * @see #startDTD
     */
    public void endDTD() throws StreamException
    {
        try
        {
            final XmlWriter writer = m_writer;
            if (!m_inDoctype)
                writer.write("]>");
            else
            {
                writer.write('>');
            }
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }

    }

    /**
     * End the scope of a prefix-URI Namespace mapping.
     * 
     * @param prefix The prefix that was being mapping.
     * @throws StreamException The client may throw
     *            an exception during processing.
     */
    public void endPrefixMapping(String prefix) throws StreamException
    { // do nothing
    }

    /**
     * Receive notification of ignorable whitespace in element content.
     * 
     * Not sure how to get this invoked quite yet.
     * 
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     * @throws StreamException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #characters
     * 
     * @throws StreamException
     */
    public void ignorableWhitespace(char ch[], int start, int length)
        throws StreamException
    {

        if (0 == length)
            return;
        characters(ch, start, length);
    }

    /**
     * Report the start of a CDATA section.
     * 
     * @throws StreamException The application may raise an exception.
     * @see #endCDATA
     */
    public void startCDATA() throws StreamException
    {
        try {
            m_writer.write(CDATA_DELIMITER_OPEN);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(Context.CDATA_SECTION);
    }

    /**
     * For the enclosing elements starting tag write out
     * out any attributes followed by ">"
     *
     * @throws StreamException
     */
    protected void closeStartTag() throws StreamException
    {

        if (startTagOpen) {

            try
            {
                m_writer.write('>');
                switchContext(Context.MIXED_CONTENT);
            }
            catch (IOException e)
            {
                throw new StreamException(e);
            }

            startTagOpen = false;
        }

    }

    public void startProcessingInstruction(String target) throws StreamException {
        switchContext(Context.TAG);
        try {
            m_writer.write("<?");
            m_writer.write(target);
            m_writer.write(' ');
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(Context.PROCESSING_INSTRUCTION);
    }

    public void endProcessingInstruction() throws StreamException {
        try {
            m_writer.write("?>");
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
        switchContext(Context.MIXED_CONTENT);
    }

    public void writeInternalSubset(String internalSubset) throws StreamException {
        try {
            DTDprolog();
            m_writer.write(internalSubset);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    public void flushBuffer() throws StreamException {
        try {
            m_writer.flushBuffer();
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    // Implement DTDHandler
    /**
     * If this method is called, the serializer is used as a
     * DTDHandler, which changes behavior how the serializer 
     * handles document entities. 
     * @see org.xml.sax.DTDHandler#notationDecl(java.lang.String, java.lang.String, java.lang.String)
     */
    public void notationDecl(String name, String pubID, String sysID) throws StreamException {
        // TODO Auto-generated method stub
        try {
            DTDprolog();
            
            m_writer.write("<!NOTATION ");            
            m_writer.write(name);
            if (pubID != null) {
                m_writer.write(" PUBLIC \"");
                m_writer.write(pubID);
  
            }
            else {
                m_writer.write(" SYSTEM \"");
                m_writer.write(sysID);
            }
            m_writer.write("\" >\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * If this method is called, the serializer is used as a
     * DTDHandler, which changes behavior how the serializer 
     * handles document entities. 
     * @see org.xml.sax.DTDHandler#unparsedEntityDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void unparsedEntityDecl(String name, String pubID, String sysID, String notationName) throws StreamException {
        // TODO Auto-generated method stub
        try {
            DTDprolog();       
            
            m_writer.write("<!ENTITY ");            
            m_writer.write(name);
            if (pubID != null) {
                m_writer.write(" PUBLIC \"");
                m_writer.write(pubID);
  
            }
            else {
                m_writer.write(" SYSTEM \"");
                m_writer.write(sysID);
            }
            m_writer.write("\" NDATA ");
            m_writer.write(notationName);
            m_writer.write(" >\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }
    
    /**
     * A private helper method to output the 
     * @throws StreamException
     * @throws IOException
     */
    private void DTDprolog() throws StreamException, IOException {
        final XmlWriter writer = m_writer;
        if (m_inDoctype)
        {
            writer.write(" [\n");
            m_inDoctype = false;
        }
    }
    
    public void writeRaw(String s, UnmappableCharacterHandler unmappableCharacterHandler) throws StreamException {
        try {
            m_writer.setUnmappableCharacterHandler(unmappableCharacterHandler);
            m_writer.write(s);
            m_writer.setUnmappableCharacterHandler(context.getUnmappableCharacterHandler());
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    public void processEntityReference(String name) throws StreamException {
        try {
            final XmlWriter writer = m_writer;
            writer.write('&');
            writer.write(name);
            writer.write(';');
        } catch(IOException ex) {
            throw new StreamException(ex);
        }
    }

    public void completed() throws StreamException {
        flushBuffer();
    }
}
