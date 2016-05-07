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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.serializer.utils.MsgKey;
import org.apache.axiom.core.stream.serializer.utils.Utils;

/**
 * This abstract class is a base class for other stream 
 * serializers (xml, html, text ...) that write output to a stream.
 * 
 * @xsl.usage internal
 */
abstract public class ToStream extends SerializerBase
{

    private static final String COMMENT_BEGIN = "<!--";
    private static final String COMMENT_END = "-->";

    /** Stack to keep track of disabling output escaping. */
    protected BoolStack m_disableOutputEscapingStates = new BoolStack();


    /**
     * The encoding information associated with this serializer.
     * Although initially there is no encoding,
     * there is a dummy EncodingInfo object that will say
     * that every character is in the encoding. This is useful
     * for a serializer that is in temporary output state and has
     * no associated encoding. A serializer in final output state
     * will have an encoding, and will worry about whether 
     * single chars or surrogate pairs of high/low chars form
     * characters in the output encoding. 
     */
    EncodingInfo m_encodingInfo = new EncodingInfo(null,null, '\u0000');
    
    /**
     * Stack to keep track of whether or not we need to
     * preserve whitespace.
     * 
     * Used to push/pop values used for the field m_ispreserve, but
     * m_ispreserve is only relevant if m_doIndent is true.
     * If m_doIndent is false this field has no impact.
     * 
     */
    protected BoolStack m_preserves = new BoolStack();

    /**
     * State flag to tell if preservation of whitespace
     * is important. 
     * 
     * Used only in shouldIndent() but only if m_doIndent is true.
     * If m_doIndent is false this flag has no impact.
     * 
     */
    protected boolean m_ispreserve = false;

    /**
     * State flag that tells if the previous node processed
     * was text, so we can tell if we should preserve whitespace.
     * 
     * Used in endDocument() and shouldIndent() but
     * only if m_doIndent is true. 
     * If m_doIndent is false this flag has no impact.
     */
    protected boolean m_isprevtext = false;
        
    private static final char[] s_systemLineSep;
    static {
        s_systemLineSep = SecuritySupport.getSystemProperty("line.separator").toCharArray();
    }
    
    /**
     * The system line separator for writing out line breaks.
     * The default value is from the system property,
     * but this value can be set through the xsl:output
     * extension attribute xalan:line-separator.
     */
    protected char[] m_lineSep = s_systemLineSep;
        
        
    /**
     * True if the the system line separator is to be used.
     */    
    protected boolean m_lineSepUse = true;    

    /**
     * The length of the line seperator, since the write is done
     * one character at a time.
     */
    protected int m_lineSepLen = m_lineSep.length;

    /**
     * Map that tells which characters should have special treatment, and it
     *  provides character to entity name lookup.
     */
    protected CharInfo m_charInfo;

    /** True if we control the buffer, and we should flush the output on endDocument. */
    boolean m_shouldFlush = true;

    /**
     * Add space before '/>' for XHTML.
     */
    protected boolean m_spaceBeforeClose = false;

    /**
     * Flag to signal that a newline should be added.
     * 
     * Used only in indent() which is called only if m_doIndent is true.
     * If m_doIndent is false this flag has no impact.
     */
    boolean m_startNewLine;

    /**
     * Tells if we're in an internal document type subset.
     */
    protected boolean m_inDoctype = false;

    /**
       * Flag to quickly tell if the encoding is UTF8.
       */
    boolean m_isUTF8 = false;


    /**
     * remembers if we are in between the startCDATA() and endCDATA() callbacks
     */
    protected boolean m_cdataStartCalled = false;
    
    /**
     * If this flag is true DTD entity references are not left as-is,
     * which is exiting older behavior.
     */
    private boolean m_expandDTDEntities = true;
  

    /**
     * Default constructor
     */
    public ToStream()
    {
    }

    /**
     * This helper method to writes out "]]>" when closing a CDATA section.
     *
     * @throws StreamException
     */
    protected void closeCDATA() throws StreamException
    {
        try
        {
            m_writer.write(CDATA_DELIMITER_CLOSE);
            // write out a CDATA section closing "]]>"
            m_cdataTagOpen = false; // Remember that we have done so.
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }
    }

    /**
     * Taken from XSLTC 
     */
    protected boolean m_escaping = true;

    /**
     * Flush the formatter's result stream.
     *
     * @throws StreamException
     */
    protected final void flushWriter() throws StreamException
    {
        final java.io.Writer writer = m_writer;
        if (null != writer)
        {
            try
            {
                if (writer instanceof WriterToUTF8Buffered)
                {
                    if (m_shouldFlush)
                         ((WriterToUTF8Buffered) writer).flush();
                    else
                         ((WriterToUTF8Buffered) writer).flushBuffer();
                }
                if (writer instanceof WriterToASCI)
                {
                    if (m_shouldFlush)
                        writer.flush();
                }
                else
                {
                    // Flush always. 
                    // Not a great thing if the writer was created 
                    // by this class, but don't have a choice.
                    writer.flush();
                }
            }
            catch (IOException ioe)
            {
                throw new StreamException(ioe);
            }
        }
    }

    OutputStream m_outputStream;
    /**
     * Get the output stream where the events will be serialized to.
     *
     * @return reference to the result stream, or null of only a writer was
     * set.
     */
    public OutputStream getOutputStream()
    {
        return m_outputStream;
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
        // Do not inline external DTD
        if (m_inExternalDTD)
            return;
        try
        {
            final java.io.Writer writer = m_writer;
            DTDprolog();

            writer.write("<!ELEMENT ");
            writer.write(name);
            writer.write(' ');
            writer.write(model);
            writer.write('>');
            writer.write(m_lineSep, 0, m_lineSepLen);
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
        // Do not inline external DTD
        if (m_inExternalDTD)
            return;
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
        final java.io.Writer writer = m_writer;
        writer.write("<!ENTITY ");
        writer.write(name);
        writer.write(" \"");
        writer.write(value);
        writer.write("\">");
        writer.write(m_lineSep, 0, m_lineSepLen);
    }

    /**
     * Output a system-dependent line break.
     *
     * @throws StreamException
     */
    protected final void outputLineSep() throws IOException
    {

        m_writer.write(m_lineSep, 0, m_lineSepLen);
    }

    void setProp(String name, String val, boolean defaultVal) {
        if (val != null) {


            char first = getFirstCharLocName(name);
            switch (first) {
            case 'd':
                if (OutputKeys.DOCTYPE_SYSTEM.equals(name)) {
                    this.m_doctypeSystem = val;
                } else if (OutputKeys.DOCTYPE_PUBLIC.equals(name)) {
                    this.m_doctypePublic = val;
                    if (val.startsWith("-//W3C//DTD XHTML"))
                        m_spaceBeforeClose = true;
                }
                break;
            case 'e':
                String newEncoding = val;
                if (OutputKeys.ENCODING.equals(name)) {
                    String possible_encoding = Encodings.getMimeEncoding(val);
                    if (possible_encoding != null) {
                        // if the encoding is being set, try to get the
                        // preferred
                        // mime-name and set it too.
                        super.setProp("mime-name", possible_encoding,
                                defaultVal);
                    }
                    final String oldExplicitEncoding = getOutputPropertyNonDefault(OutputKeys.ENCODING);
                    final String oldDefaultEncoding  = getOutputPropertyDefault(OutputKeys.ENCODING);
                    if ( (defaultVal && ( oldDefaultEncoding == null || !oldDefaultEncoding.equalsIgnoreCase(newEncoding)))
                            || ( !defaultVal && (oldExplicitEncoding == null || !oldExplicitEncoding.equalsIgnoreCase(newEncoding) ))) {
                       // We are trying to change the default or the non-default setting of the encoding to a different value
                       // from what it was
                       
                       EncodingInfo encodingInfo = Encodings.getEncodingInfo(newEncoding);
                       if (newEncoding != null && encodingInfo.name == null) {
                        // We tried to get an EncodingInfo for Object for the given
                        // encoding, but it came back with an internall null name
                        // so the encoding is not supported by the JDK, issue a message.
                        final String msg = Utils.messages.createMessage(
                                MsgKey.ER_ENCODING_NOT_SUPPORTED,new Object[]{ newEncoding });
                        
                        final String msg2 = 
                            "Warning: encoding \"" + newEncoding + "\" not supported, using "
                                   + Encodings.DEFAULT_MIME_ENCODING;
                        try {
                                // Prepare to issue the warning message
                                final Transformer tran = super.getTransformer();
                                if (tran != null) {
                                    final ErrorListener errHandler = tran
                                            .getErrorListener();
                                    // Issue the warning message
                                    if (null != errHandler
                                            && m_sourceLocator != null) {
                                        errHandler
                                                .warning(new TransformerException(
                                                        msg, m_sourceLocator));
                                        errHandler
                                                .warning(new TransformerException(
                                                        msg2, m_sourceLocator));
                                    } else {
                                        System.out.println(msg);
                                        System.out.println(msg2);
                                    }
                                } else {
                                    System.out.println(msg);
                                    System.out.println(msg2);
                                }
                            } catch (Exception e) {
                            }

                            // We said we are using UTF-8, so use it
                            newEncoding = Encodings.DEFAULT_MIME_ENCODING;
                            val = Encodings.DEFAULT_MIME_ENCODING; // to store the modified value into the properties a little later
                            encodingInfo = Encodings.getEncodingInfo(newEncoding);

                        } 
                       // The encoding was good, or was forced to UTF-8 above
                       
                       
                       // If there is already a non-default set encoding and we 
                       // are trying to set the default encoding, skip the this block
                       // as the non-default value is already the one to use.
                       if (defaultVal == false || oldExplicitEncoding == null) {
                           m_encodingInfo = encodingInfo;
                           if (newEncoding != null)
                               m_isUTF8 = newEncoding.equals(Encodings.DEFAULT_MIME_ENCODING);
                           
                           // if there was a previously set OutputStream
                           OutputStream os = getOutputStream();
                           if (os != null) {
                               Writer w = getWriter();
                               
                               // If the writer was previously set, but
                               // set by the user, or if the new encoding is the same
                               // as the old encoding, skip this block
                               String oldEncoding = getOutputProperty(OutputKeys.ENCODING);
                               if ((w == null || !m_writer_set_by_user) 
                                       && !newEncoding.equalsIgnoreCase(oldEncoding)) {
                                   // Make the change of encoding in our internal
                                   // table, then call setOutputStreamInternal
                                   // which will stomp on the old Writer (if any)
                                   // with a new Writer with the new encoding.
                                   super.setProp(name, val, defaultVal);
                                   setOutputStreamInternal(os,false);
                               }
                           }
                       }
                    }
                }
                break;
            case 'i':
                if (OutputPropertiesFactory.S_KEY_INDENT_AMOUNT.equals(name)) {
                    setIndentAmount(Integer.parseInt(val));
                } else if (OutputKeys.INDENT.equals(name)) {
                    boolean b = "yes".equals(val) ? true : false;
                    m_doIndent = b;
                }

                break;
            case 'l':
                if (OutputPropertiesFactory.S_KEY_LINE_SEPARATOR.equals(name)) {
                    m_lineSep = val.toCharArray();
                    m_lineSepLen = m_lineSep.length;
                }

                break;
            case 'm':
                if (OutputKeys.MEDIA_TYPE.equals(name)) {
                    m_mediatype = val;
                }
                break;
            case 'o':
                if (OutputKeys.OMIT_XML_DECLARATION.equals(name)) {
                    boolean b = "yes".equals(val) ? true : false;
                    this.m_shouldNotWriteXMLHeader = b;
                }
                break;
            case 's':
                // if standalone was explicitly specified
                if (OutputKeys.STANDALONE.equals(name)) {
                    if (defaultVal) {
                        setStandaloneInternal(val);
                    } else {
                        m_standaloneWasSpecified = true;
                        setStandaloneInternal(val);
                    }
                }

                break;
            case 'v':
                if (OutputKeys.VERSION.equals(name)) {
                    m_version = val;
                }
                break;
            default:
                break;

            } 
            super.setProp(name, val, defaultVal);
        }
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

        boolean shouldFlush = m_shouldFlush;
        
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


         

        m_shouldFlush = shouldFlush;
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

    /**
     * Specifies a writer to which the document should be serialized.
     * This method should not be called while the serializer is in
     * the process of serializing a document.
     *
     * @param writer The output writer stream
     */
    public void setWriter(Writer writer)
    {        
        setWriterInternal(writer, true);
    }
    
    private boolean m_writer_set_by_user;
    private void setWriterInternal(Writer writer, boolean setByUser) {

        m_writer_set_by_user = setByUser;
        m_writer = writer;
    }
    
    /**
     * Set if the operating systems end-of-line line separator should
     * be used when serializing.  If set false NL character 
     * (decimal 10) is left alone, otherwise the new-line will be replaced on
     * output with the systems line separator. For example on UNIX this is
     * NL, while on Windows it is two characters, CR NL, where CR is the
     * carriage-return (decimal 13).
     *  
     * @param use_sytem_line_break True if an input NL is replaced with the 
     * operating systems end-of-line separator.
     * @return The previously set value of the serializer.
     */
    public boolean setLineSepUse(boolean use_sytem_line_break)
    {
        boolean oldValue = m_lineSepUse;
        m_lineSepUse = use_sytem_line_break;
        return oldValue;
    }

    /**
     * Specifies an output stream to which the document should be
     * serialized. This method should not be called while the
     * serializer is in the process of serializing a document.
     * <p>
     * The encoding specified in the output properties is used, or
     * if no encoding was specified, the default for the selected
     * output method.
     *
     * @param output The output stream
     */
    public void setOutputStream(OutputStream output)
    {
        setOutputStreamInternal(output, true);
    }
    
    private void setOutputStreamInternal(OutputStream output, boolean setByUser)
    {
        m_outputStream = output;
        String encoding = getOutputProperty(OutputKeys.ENCODING);        
        if (Encodings.DEFAULT_MIME_ENCODING.equalsIgnoreCase(encoding))
        {
            // We wrap the OutputStream with a writer, but
            // not one set by the user
            setWriterInternal(new WriterToUTF8Buffered(output), false);
        } else if (
                "WINDOWS-1250".equals(encoding)
                || "US-ASCII".equals(encoding)
                || "ASCII".equals(encoding))
        {
            setWriterInternal(new WriterToASCI(output), false);
        } else if (encoding != null) {
            Writer osw = null;
                try
                {
                    osw = Encodings.getWriter(output, encoding);
                }
                catch (UnsupportedEncodingException uee)
                {
                    osw = null;
                }

            
            if (osw == null) {
                System.out.println(
                    "Warning: encoding \""
                        + encoding
                        + "\" not supported"
                        + ", using "
                        + Encodings.DEFAULT_MIME_ENCODING);

                encoding = Encodings.DEFAULT_MIME_ENCODING;
                setEncoding(encoding);
                try {
                    osw = Encodings.getWriter(output, encoding);
                } catch (UnsupportedEncodingException e) {
                    // We can't really get here, UTF-8 is always supported
                    // This try-catch exists to make the compiler happy
                    e.printStackTrace();
                }
            }
            setWriterInternal(osw,false);
        }
        else {
            // don't have any encoding, but we have an OutputStream
            Writer osw = new OutputStreamWriter(output);
            setWriterInternal(osw,false);
        }
    }

    /**
     * @see SerializationHandler#setEscaping(boolean)
     */
    public boolean setEscaping(boolean escape)
    {
        final boolean temp = m_escaping;
        m_escaping = escape;
        return temp;

    }


    /**
     * Might print a newline character and the indentation amount
     * of the given depth.
     * 
     * @param depth the indentation depth (element nesting depth)
     *
     * @throws StreamException if an error occurs during writing.
     */
    protected void indent(int depth) throws IOException
    {

        if (m_startNewLine)
            outputLineSep();
        /* For m_indentAmount > 0 this extra test might be slower
         * but Xalan's default value is 0, so this extra test
         * will run faster in that situation.
         */
        if (m_indentAmount > 0)
            printSpace(depth * m_indentAmount);

    }
    
    /**
     * Indent at the current element nesting depth.
     * @throws IOException
     */
    protected void indent() throws IOException
    {
        indent(m_elemContext.m_currentElemDepth);
    }
    /**
     * Prints <var>n</var> spaces.
     * @param n         Number of spaces to print.
     *
     * @throws StreamException if an error occurs when writing.
     */
    private void printSpace(int n) throws IOException
    {
        final java.io.Writer writer = m_writer;
        for (int i = 0; i < n; i++)
        {
            writer.write(' ');
        }

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
        // Do not inline external DTD
        if (m_inExternalDTD)
            return;
        try
        {
            final java.io.Writer writer = m_writer;
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
            writer.write('>');
            writer.write(m_lineSep, 0, m_lineSepLen);
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
    public Writer getWriter()
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
            m_writer.write("\" >");
            m_writer.write(m_lineSep, 0, m_lineSepLen);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Tell if this character can be written without escaping.
     */
    protected boolean escapingNotNeeded(char ch)
    {
        final boolean ret;
        if (ch < 127)
        {
            // This is the old/fast code here, but is this 
            // correct for all encodings?
            if (ch >= CharInfo.S_SPACE || (CharInfo.S_LINEFEED == ch || 
                    CharInfo.S_CARRIAGERETURN == ch || CharInfo.S_HORIZONAL_TAB == ch))
                ret= true;
            else
                ret = false;
        }
        else {            
            ret = m_encodingInfo.isInEncoding(ch);
        }
        return ret;
    }

    /**
     * Once a surrogate has been detected, write out the pair of
     * characters if it is in the encoding, or if there is no
     * encoding, otherwise write out an entity reference
     * of the value of the unicode code point of the character
     * represented by the high/low surrogate pair.
     * <p>
     * An exception is thrown if there is no low surrogate in the pair,
     * because the array ends unexpectely, or if the low char is there
     * but its value is such that it is not a low surrogate.
     *
     * @param c the first (high) part of the surrogate, which
     * must be confirmed before calling this method.
     * @param ch Character array.
     * @param i position Where the surrogate was detected.
     * @param end The end index of the significant characters.
     * @return 0 if the pair of characters was written out as-is,
     * the unicode code point of the character represented by
     * the surrogate pair if an entity reference with that value
     * was written out. 
     * 
     * @throws IOException
     * @throws StreamException if invalid UTF-16 surrogate detected.
     */
    protected int writeUTF16Surrogate(char c, char ch[], int i, int end)
        throws IOException
    {
        int codePoint = 0;
        if (i + 1 >= end)
        {
            throw new IOException(
                Utils.messages.createMessage(
                    MsgKey.ER_INVALID_UTF16_SURROGATE,
                    new Object[] { Integer.toHexString((int) c)}));
        }
        
        final char high = c;
        final char low = ch[i+1];
        if (!Encodings.isLowUTF16Surrogate(low)) {
            throw new IOException(
                Utils.messages.createMessage(
                    MsgKey.ER_INVALID_UTF16_SURROGATE,
                    new Object[] {
                        Integer.toHexString((int) c)
                            + " "
                            + Integer.toHexString(low)}));
        }

        final java.io.Writer writer = m_writer;
                
        // If we make it to here we have a valid high, low surrogate pair
        if (m_encodingInfo.isInEncoding(c,low)) {
            // If the character formed by the surrogate pair
            // is in the encoding, so just write it out
            writer.write(ch,i,2);
        }
        else {
            // Don't know what to do with this char, it is
            // not in the encoding and not a high char in
            // a surrogate pair, so write out as an entity ref
            final String encoding = getEncoding();
            if (encoding != null) {
                /* The output encoding is known, 
                 * so somthing is wrong.
                  */
                codePoint = Encodings.toCodePoint(high, low);
                // not in the encoding, so write out a character reference
                writer.write('&');
                writer.write('#');
                writer.write(Integer.toString(codePoint));
                writer.write(';');
            } else {
                /* The output encoding is not known,
                 * so just write it out as-is.
                 */
                writer.write(ch, i, 2);
            }
        }
        // non-zero only if character reference was written out.
        return codePoint;
    }

    /**
     * Handle one of the default entities, return false if it
     * is not a default entity.
     *
     * @param ch character to be escaped.
     * @param i index into character array.
     * @param chars non-null reference to character array.
     * @param len length of chars.
     * @param fromTextNode true if the characters being processed
     * are from a text node, false if they are from an attribute value
     * @param escLF true if the linefeed should be escaped.
     *
     * @return i+1 if the character was written, else i.
     *
     * @throws java.io.IOException
     */
    int accumDefaultEntity(
        java.io.Writer writer,
        char ch,
        int i,
        char[] chars,
        int len,
        boolean fromTextNode,
        boolean escLF)
        throws IOException
    {

        if (!escLF && CharInfo.S_LINEFEED == ch)
        {
            writer.write(m_lineSep, 0, m_lineSepLen);
        }
        else
        {
            // if this is text node character and a special one of those,
            // or if this is a character from attribute value and a special one of those
            if ((fromTextNode && m_charInfo.shouldMapTextChar(ch)) || (!fromTextNode && m_charInfo.shouldMapAttrChar(ch)))
            {
                String outputStringForChar = m_charInfo.getOutputStringForChar(ch);

                if (null != outputStringForChar)
                {
                    writer.write(outputStringForChar);
                }
                else
                    return i;
            }
            else
                return i;
        }

        return i + 1;

    }
    /**
     * Normalize the characters, but don't escape.
     *
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     * @param isCData true if a CDATA block should be built around the characters.
     * @param useSystemLineSeparator true if the operating systems 
     * end-of-line separator should be output rather than a new-line character.
     *
     * @throws IOException
     * @throws StreamException
     */
    void writeNormalizedChars(
        char ch[],
        int start,
        int length,
        boolean isCData,
        boolean useSystemLineSeparator)
        throws IOException, StreamException
    {
        final java.io.Writer writer = m_writer;
        int end = start + length;

        for (int i = start; i < end; i++)
        {
            char c = ch[i];

            if (CharInfo.S_LINEFEED == c && useSystemLineSeparator)
            {
                writer.write(m_lineSep, 0, m_lineSepLen);
            }
            else if (isCData && (!escapingNotNeeded(c)))
            {
                //                if (i != 0)
                if (m_cdataTagOpen)
                    closeCDATA();

                // This needs to go into a function... 
                if (Encodings.isHighUTF16Surrogate(c))
                {
                    writeUTF16Surrogate(c, ch, i, end);
                    i++ ; // process two input characters
                }
                else
                {
                    writer.write("&#");

                    String intStr = Integer.toString((int) c);

                    writer.write(intStr);
                    writer.write(';');
                }

                //                if ((i != 0) && (i < (end - 1)))
                //                if (!m_cdataTagOpen && (i < (end - 1)))
                //                {
                //                    writer.write(CDATA_DELIMITER_OPEN);
                //                    m_cdataTagOpen = true;
                //                }
            }
            else if (
                isCData
                    && ((i < (end - 2))
                        && (']' == c)
                        && (']' == ch[i + 1])
                        && ('>' == ch[i + 2])))
            {
                writer.write(CDATA_CONTINUE);

                i += 2;
            }
            else
            {
                if (escapingNotNeeded(c))
                {
                    if (isCData && !m_cdataTagOpen)
                    {
                        writer.write(CDATA_DELIMITER_OPEN);
                        m_cdataTagOpen = true;
                    }
                    writer.write(c);
                }

                // This needs to go into a function... 
                else if (Encodings.isHighUTF16Surrogate(c))
                {
                    if (m_cdataTagOpen)
                        closeCDATA();
                    writeUTF16Surrogate(c, ch, i, end);
                    i++; // process two input characters
                }
                else
                {
                    if (m_cdataTagOpen)
                        closeCDATA();
                    writer.write("&#");

                    String intStr = Integer.toString((int) c);

                    writer.write(intStr);
                    writer.write(';');
                }
            }
        }

    }

    /**
     * Ends an un-escaping section.
     *
     * @see #startNonEscaping
     *
     * @throws StreamException
     */
    public void endNonEscaping() throws StreamException
    {
        m_disableOutputEscapingStates.pop();
    }

    /**
     * Starts an un-escaping section. All characters printed within an un-
     * escaping section are printed as is, without escaping special characters
     * into entity references. Only XML and HTML serializers need to support
     * this method.
     * <p> The contents of the un-escaping section will be delivered through the
     * regular <tt>characters</tt> event.
     *
     * @throws StreamException
     */
    public void startNonEscaping() throws StreamException
    {
        m_disableOutputEscapingStates.push(true);
    }

    /**
     * Receive notification of cdata.
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
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     * @throws StreamException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #ignorableWhitespace
     * @see org.xml.sax.Locator
     *
     * @throws StreamException
     */
    protected void cdata(char ch[], int start, final int length)
        throws StreamException
    {

        try
        {
            final int old_start = start;
            m_ispreserve = true;

            if (shouldIndent())
                indent();

            boolean writeCDataBrackets =
                (((length >= 1) && escapingNotNeeded(ch[start])));

            /* Write out the CDATA opening delimiter only if
             * we are supposed to, and if we are not already in
             * the middle of a CDATA section  
             */
            if (writeCDataBrackets && !m_cdataTagOpen)
            {
                m_writer.write(CDATA_DELIMITER_OPEN);
                m_cdataTagOpen = true;
            }

            // writer.write(ch, start, length);
            if (isEscapingDisabled())
            {
                charactersRaw(ch, start, length);
            }
            else
                writeNormalizedChars(ch, start, length, true, m_lineSepUse);

            /* used to always write out CDATA closing delimiter here,
             * but now we delay, so that we can merge CDATA sections on output.    
             * need to write closing delimiter later
             */
            if (writeCDataBrackets)
            {
                /* if the CDATA section ends with ] don't leave it open
                 * as there is a chance that an adjacent CDATA sections
                 * starts with ]>.  
                 * We don't want to merge ]] with > , or ] with ]> 
                 */
                if (ch[start + length - 1] == ']')
                    closeCDATA();
            }
        }
        catch (IOException ioe)
        {
            throw new StreamException(
                Utils.messages.createMessage(
                    MsgKey.ER_OIERROR,
                    null),
                ioe);
            //"IO error", ioe);
        }
    }

    /**
     * Tell if the character escaping should be disabled for the current state.
     *
     * @return true if the character escaping should be disabled.
     */
    private boolean isEscapingDisabled()
    {
        return m_disableOutputEscapingStates.peekOrFalse();
    }

    /**
     * If available, when the disable-output-escaping attribute is used,
     * output raw text without escaping.
     *
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     *
     * @throws StreamException
     */
    protected void charactersRaw(char ch[], int start, int length)
        throws StreamException
    {

        if (m_inEntityRef)
            return;
        try
        {
            m_ispreserve = true;

            m_writer.write(ch, start, length);
        }
        catch (IOException e)
        {
            throw new StreamException(e);
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
        if (length == 0 || (m_inEntityRef && !m_expandDTDEntities))
            return;
            
        m_docIsEmpty = false;
        
        if (m_cdataStartCalled)
        {
            /* either due to startCDATA() being called or due to 
             * cdata-section-elements atribute, we need this as cdata
             */
            cdata(chars, start, length);

            return;
        }

        if (m_cdataTagOpen)
            closeCDATA();
        
        if (m_disableOutputEscapingStates.peekOrFalse() || (!m_escaping))
        {
            charactersRaw(chars, start, length);
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
            final Writer writer = m_writer;
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
                        // Just accumulate the clean whitespace
                        i++;
                        break;
                    case CharInfo.S_LINEFEED:
                        lastDirtyCharProcessed = processLineFeed(chars, i,
                                lastDirtyCharProcessed, writer);
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

            /* If there is some non-whitespace, mark that we may need
             * to preserve this. This is only important if we have indentation on.
             */            
            if (i < end || !isAllWhitespace) 
                m_ispreserve = true;
            
            
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
                            // Leave whitespace TAB as a real character
                            break;
                        case CharInfo.S_LINEFEED:
                            lastDirtyCharProcessed = processLineFeed(chars, i, lastDirtyCharProcessed, writer);
                            break;
                        case CharInfo.S_CARRIAGERETURN:
                        	writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        	writer.write("&#13;");
                        	lastDirtyCharProcessed = i;
                            // Leave whitespace carriage return as a real character
                            break;
                        default:
                            writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                            writer.write("&#");
                            writer.write(Integer.toString(ch));
                            writer.write(';');
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
                        writer.write("&#");
                        writer.write(Integer.toString(ch));
                        writer.write(';');
                        lastDirtyCharProcessed = i;
                    }
                    else if (ch == CharInfo.S_LINE_SEPARATOR) {
                        // LINE SEPARATOR
                        writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        writer.write("&#8232;");
                        lastDirtyCharProcessed = i;
                    }
                    else if (m_encodingInfo.isInEncoding(ch)) {
                        // If the character is in the encoding, and
                        // not in the normal ASCII range, we also
                        // just leave it get added on to the clean characters
                        
                    }
                    else {
                        // This is a fallback plan, we should never get here
                        // but if the character wasn't previously handled
                        // (i.e. isn't in the encoding, etc.) then what
                        // should we do?  We choose to write out an entity
                        writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        writer.write("&#");
                        writer.write(Integer.toString(ch));
                        writer.write(';');
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

            // For indentation purposes, mark that we've just writen text out
            m_isprevtext = true;
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }
    }

	private int processLineFeed(final char[] chars, int i, int lastProcessed, final Writer writer) throws IOException {
		if (!m_lineSepUse 
		|| (m_lineSepLen ==1 && m_lineSep[0] == CharInfo.S_LINEFEED)){
		    // We are leaving the new-line alone, and it is just
		    // being added to the 'clean' characters,
			// so the last dirty character processed remains unchanged
		}
		else {
		    writeOutCleanChars(chars, i, lastProcessed);
		    writer.write(m_lineSep, 0, m_lineSepLen);
		    lastProcessed = i;
		}
		return lastProcessed;
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
     * Process a dirty character and any preeceding clean characters
     * that were not yet processed.
     * @param chars array of characters being processed
     * @param end one (1) beyond the last character 
     * in chars to be processed
     * @param i the index of the dirty character
     * @param ch the character in chars[i]
     * @param lastDirty the last dirty character previous to i
     * @param fromTextNode true if the characters being processed are
     * from a text node, false if they are from an attribute value.
     * @return the index of the last character processed
     */
    private int processDirty(
        char[] chars, 
        int end,
        int i, 
        char ch,
        int lastDirty,
        boolean fromTextNode) throws IOException
    {
        int startClean = lastDirty + 1;
        // if we have some clean characters accumulated
        // process them before the dirty one.                   
        if (i > startClean)
        {
            int lengthClean = i - startClean;
            m_writer.write(chars, startClean, lengthClean);
        }

        // process the "dirty" character
        if (CharInfo.S_LINEFEED == ch && fromTextNode)
        {
            m_writer.write(m_lineSep, 0, m_lineSepLen);
        }
        else
        {
            startClean =
                accumDefaultEscape(
                    m_writer,
                    (char)ch,
                    i,
                    chars,
                    end,
                    fromTextNode,
                    false);
            i = startClean - 1;
        }
        // Return the index of the last character that we just processed 
        // which is a dirty character.
        return i;
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
        if (m_inEntityRef && !m_expandDTDEntities)
            return;
        final int length = s.length();
        if (length > m_charsBuff.length)
        {
            m_charsBuff = new char[length * 2 + 1];
        }
        s.getChars(0, length, m_charsBuff, 0);
        characters(m_charsBuff, 0, length);
    }

    /**
     * Escape and writer.write a character.
     *
     * @param ch character to be escaped.
     * @param i index into character array.
     * @param chars non-null reference to character array.
     * @param len length of chars.
     * @param fromTextNode true if the characters being processed are
     * from a text node, false if the characters being processed are from
     * an attribute value.
     * @param escLF true if the linefeed should be escaped.
     *
     * @return i+1 if a character was written, i+2 if two characters
     * were written out, else return i.
     *
     * @throws StreamException
     */
    private int accumDefaultEscape(
        Writer writer,
        char ch,
        int i,
        char[] chars,
        int len,
        boolean fromTextNode,
        boolean escLF)
        throws IOException
    {

        int pos = accumDefaultEntity(writer, ch, i, chars, len, fromTextNode, escLF);

        if (i == pos)
        {
            if (Encodings.isHighUTF16Surrogate(ch))
            {

                // Should be the UTF-16 low surrogate of the hig/low pair.
                char next;
                // Unicode code point formed from the high/low pair.
                int codePoint = 0;

                if (i + 1 >= len)
                {
                    throw new IOException(
                        Utils.messages.createMessage(
                            MsgKey.ER_INVALID_UTF16_SURROGATE,
                            new Object[] { Integer.toHexString(ch)}));
                    //"Invalid UTF-16 surrogate detected: "

                    //+Integer.toHexString(ch)+ " ?");
                }
                else
                {
                    next = chars[++i];

                    if (!(Encodings.isLowUTF16Surrogate(next)))
                        throw new IOException(
                            Utils.messages.createMessage(
                                MsgKey
                                    .ER_INVALID_UTF16_SURROGATE,
                                new Object[] {
                                    Integer.toHexString(ch)
                                        + " "
                                        + Integer.toHexString(next)}));
                    //"Invalid UTF-16 surrogate detected: "

                    //+Integer.toHexString(ch)+" "+Integer.toHexString(next));
                    codePoint = Encodings.toCodePoint(ch,next);
                }

                writer.write("&#");
                writer.write(Integer.toString(codePoint));
                writer.write(';');
                pos += 2; // count the two characters that went into writing out this entity
            }
            else
            {
                /*  This if check is added to support control characters in XML 1.1.
                 *  If a character is a Control Character within C0 and C1 range, it is desirable
                 *  to write it out as Numeric Character Reference(NCR) regardless of XML Version
                 *  being used for output document.
                 */ 
                if (isCharacterInC0orC1Range(ch) || isNELorLSEPCharacter(ch))
                {
                    writer.write("&#");
                    writer.write(Integer.toString(ch));
                    writer.write(';');
                }
                else if ((!escapingNotNeeded(ch) || 
                    (  (fromTextNode && m_charInfo.shouldMapTextChar(ch))
                     || (!fromTextNode && m_charInfo.shouldMapAttrChar(ch)))) 
                && m_elemContext.m_currentElemDepth > 0)
                {
                    writer.write("&#");
                    writer.write(Integer.toString(ch));
                    writer.write(';');
                }
                else
                {
                    writer.write(ch);
                }
                pos++;  // count the single character that was processed
            }

        }
        return pos;
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
        if (m_inEntityRef)
            return;

        if (m_cdataTagOpen)
            closeCDATA();
        try
        {
            if (m_needToOutputDocTypeDecl) {
                if(null != getDoctypeSystem()) {
                    outputDocTypeDecl(name, true);
                }
                m_needToOutputDocTypeDecl = false;
            }
        
            m_ispreserve = false;

            if (shouldIndent() && m_startNewLine)
            {
                indent();
            }

            m_startNewLine = true;

            final java.io.Writer writer = m_writer;
            writer.write('<');
            writer.write(name);
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }

        m_elemContext = m_elemContext.push(namespaceURI,localName,name);
        m_isprevtext = false;
    }

    public void startElement(String elementName) throws StreamException
    {
        startElement(null, null, elementName);
    }

    /**
     * Output the doc type declaration.
     *
     * @param name non-null reference to document type name.
     * NEEDSDOC @param closeDecl
     *
     * @throws java.io.IOException
     */
    void outputDocTypeDecl(String name, boolean closeDecl) throws StreamException
    {
        if (m_cdataTagOpen)
            closeCDATA();
        try
        {
            final java.io.Writer writer = m_writer;
            writer.write("<!DOCTYPE ");
            writer.write(name);

            String doctypePublic = getDoctypePublic();
            if (null != doctypePublic)
            {
                writer.write(" PUBLIC \"");
                writer.write(doctypePublic);
                writer.write('\"');
            }

            String doctypeSystem = getDoctypeSystem();
            if (null != doctypeSystem)
            {
                if (null == doctypePublic)
                    writer.write(" SYSTEM \"");
                else
                    writer.write(" \"");

                writer.write(doctypeSystem);

                if (closeDecl)
                {
                    writer.write("\">");
                    writer.write(m_lineSep, 0, m_lineSepLen);
                    closeDecl = false; // done closing
                }
                else
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
            final Writer writer = m_writer;
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
        Writer writer,
        String string)
        throws IOException
    {
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
                accumDefaultEscape(writer, ch, i, stringChars, len, false, true);
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
                        writer.write("&#");
                        writer.write(Integer.toString(ch));
                        writer.write(';');
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
                    writer.write("&#");
                    writer.write(Integer.toString(ch));
                    writer.write(';');
                }
                else if (ch == CharInfo.S_LINE_SEPARATOR) {
                    // LINE SEPARATOR
                    writer.write("&#8232;");
                }
                else if (m_encodingInfo.isInEncoding(ch)) {
                    // If the character is in the encoding, and
                    // not in the normal ASCII range, we also
                    // just write it out
                    writer.write(ch);
                }
                else {
                    // This is a fallback plan, we should never get here
                    // but if the character wasn't previously handled
                    // (i.e. isn't in the encoding, etc.) then what
                    // should we do?  We choose to write out a character ref
                    writer.write("&#");
                    writer.write(Integer.toString(ch));
                    writer.write(';');
                }
                    
            }
        }
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
        if (m_inEntityRef)
            return;

        // namespaces declared at the current depth are no longer valid
        // so get rid of them    
        m_prefixMap.popNamespaces(m_elemContext.m_currentElemDepth);

        try
        {
            final java.io.Writer writer = m_writer;
            if (m_elemContext.m_startTagOpen)
            {
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
                if (m_cdataTagOpen)
                    closeCDATA();

                if (shouldIndent())
                    indent(m_elemContext.m_currentElemDepth - 1);
                writer.write('<');
                writer.write('/');
                writer.write(name);
                writer.write('>');
            }
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }

        if (!m_elemContext.m_startTagOpen && m_doIndent)
        {
            m_ispreserve = m_preserves.isEmpty() ? false : m_preserves.pop();
        }

        m_isprevtext = false;
        m_elemContext = m_elemContext.m_prev;
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

    /**
     * Receive notification of an XML comment anywhere in the document. This
     * callback will be used for comments inside or outside the document
     * element, including comments in the external DTD subset (if read).
     * @param ch An array holding the characters in the comment.
     * @param start The starting position in the array.
     * @param length The number of characters to use from the array.
     * @throws StreamException The application may raise an exception.
     */
    public void comment(char ch[], int start, int length)
        throws StreamException
    {

        int start_old = start;
        if (m_inEntityRef)
            return;

        try
        {
            final int limit = start + length;
            boolean wasDash = false;
            if (m_cdataTagOpen)
                closeCDATA();
            
            if (shouldIndent())
                indent();
            
            final java.io.Writer writer = m_writer;    
            writer.write(COMMENT_BEGIN);
            // Detect occurrences of two consecutive dashes, handle as necessary.
            for (int i = start; i < limit; i++)
            {
                if (wasDash && ch[i] == '-')
                {
                    writer.write(ch, start, i - start);
                    writer.write(" -");
                    start = i + 1;
                }
                wasDash = (ch[i] == '-');
            }

            // if we have some chars in the comment
            if (length > 0)
            {
                // Output the remaining characters (if any)
                final int remainingChars = (limit - start);
                if (remainingChars > 0)
                    writer.write(ch, start, remainingChars);
                // Protect comment end from a single trailing dash
                if (ch[limit - 1] == '-')
                    writer.write(' ');
            }
            writer.write(COMMENT_END);
        }
        catch (IOException e)
        {
            throw new StreamException(e);
        }

        /*
         * Don't write out any indentation whitespace now,
         * because there may be non-whitespace text after this.
         * 
         * Simply mark that at this point if we do decide
         * to indent that we should 
         * add a newline on the end of the current line before
         * the indentation at the start of the next line.
         */ 
        m_startNewLine = true;
    }

    /**
     * Report the end of a CDATA section.
     * @throws StreamException The application may raise an exception.
     *
     *  @see  #startCDATA
     */
    public void endCDATA() throws StreamException
    {
        if (m_cdataTagOpen)
            closeCDATA();
        m_cdataStartCalled = false;
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
            if (m_needToOutputDocTypeDecl)
            {
                outputDocTypeDecl(m_elemContext.m_elementName, false);
                m_needToOutputDocTypeDecl = false;
            }
            final java.io.Writer writer = m_writer;
            if (!m_inDoctype)
                writer.write("]>");
            else
            {
                writer.write('>');
            }

            writer.write(m_lineSep, 0, m_lineSepLen);
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
     * Receive notification of a skipped entity.
     * 
     * @param name The name of the skipped entity.  If it is a
     *       parameter                   entity, the name will begin with '%',
     * and if it is the external DTD subset, it will be the string
     * "[dtd]".
     * @throws StreamException Any SAX exception, possibly wrapping
     * another exception.
     */
    public void skippedEntity(String name) throws StreamException
    { // TODO: Should handle
    }

    /**
     * Report the start of a CDATA section.
     * 
     * @throws StreamException The application may raise an exception.
     * @see #endCDATA
     */
    public void startCDATA() throws StreamException
    {
        m_cdataStartCalled = true;
    }

    /**
     * Report the beginning of an entity.
     * 
     * The start and end of the document entity are not reported.
     * The start and end of the external DTD subset are reported
     * using the pseudo-name "[dtd]".  All other events must be
     * properly nested within start/end entity events.
     * 
     * @param name The name of the entity.  If it is a parameter
     *        entity, the name will begin with '%'.
     * @throws StreamException The application may raise an exception.
     * @see #endEntity
     * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
     * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
     */
    public void startEntity(String name) throws StreamException
    {
        if (name.equals("[dtd]"))
            m_inExternalDTD = true;

        if (!m_expandDTDEntities && !m_inExternalDTD) {
            /* Only leave the entity as-is if
             * we've been told not to expand them
             * and this is not the magic [dtd] name.
             */
            startNonEscaping();
            characters("&" + name + ';');
            endNonEscaping();
        }

        m_inEntityRef = true;
    }

    /**
     * For the enclosing elements starting tag write out
     * out any attributes followed by ">"
     *
     * @throws StreamException
     */
    protected void closeStartTag() throws StreamException
    {

        if (m_elemContext.m_startTagOpen)
        {

            try
            {
                m_writer.write('>');
            }
            catch (IOException e)
            {
                throw new StreamException(e);
            }

            if (m_doIndent)
            {
                m_isprevtext = false;
                m_preserves.push(m_ispreserve);
            }

            m_elemContext.m_startTagOpen = false;
        }

    }

    /**
     * Report the start of DTD declarations, if any.
     *
     * Any declarations are assumed to be in the internal subset unless
     * otherwise indicated.
     * 
     * @param name The document type name.
     * @param publicId The declared public identifier for the
     *        external DTD subset, or null if none was declared.
     * @param systemId The declared system identifier for the
     *        external DTD subset, or null if none was declared.
     * @throws StreamException The application may raise an
     *            exception.
     * @see #endDTD
     * @see #startEntity
     */
    public void startDTD(String name, String publicId, String systemId)
        throws StreamException
    {
        setDoctypeSystem(systemId);
        setDoctypePublic(publicId);

        m_elemContext.m_elementName = name;
        m_inDoctype = true;
    }

    /**
     * Returns the m_indentAmount.
     * @return int
     */
    public int getIndentAmount()
    {
        return m_indentAmount;
    }

    /**
     * Sets the m_indentAmount.
     * 
     * @param m_indentAmount The m_indentAmount to set
     */
    public void setIndentAmount(int m_indentAmount)
    {
        this.m_indentAmount = m_indentAmount;
    }

    /**
     * Tell if, based on space preservation constraints and the doIndent property,
     * if an indent should occur.
     *
     * @return True if an indent should occur.
     */
    protected boolean shouldIndent()
    {
        return m_doIndent && (!m_ispreserve && !m_isprevtext) && m_elemContext.m_currentElemDepth > 0;
    }

    /**
     * This method flushes any pending events, which can be startDocument()
     * closing the opening tag of an element, or closing an open CDATA section.
     */
    public void flushPending() throws StreamException
    {
            if (m_cdataTagOpen)
            {
                closeCDATA();
                m_cdataTagOpen = false;
            }
            if (m_writer != null) {
                try {
                    m_writer.flush();
                }
                catch(IOException e) {
                    // what? me worry?
                }
            }
    }

    /**
     * Try's to reset the super class and reset this class for 
     * re-use, so that you don't need to create a new serializer 
     * (mostly for performance reasons).
     * 
     * @return true if the class was successfuly reset.
     */
    public boolean reset()
    {
        boolean wasReset = false;
        if (super.reset())
        {
            resetToStream();
            wasReset = true;
        }
        return wasReset;
    }
    
    /**
     * Reset all of the fields owned by ToStream class
     *
     */
    private void resetToStream()
    {
         this.m_cdataStartCalled = false;
         /* The stream is being reset. It is one of
          * ToXMLStream, ToHTMLStream ... and this type can't be changed
          * so neither should m_charInfo which is associated with the
          * type of Stream. Just leave m_charInfo as-is for the next re-use.
          * 
          */
         // this.m_charInfo = null; // don't set to null 
         this.m_disableOutputEscapingStates.clear();
         // this.m_encodingInfo = null; // don't set to null
         
         this.m_escaping = true;
         // Leave m_format alone for now - Brian M.
         // this.m_format = null;
         this.m_expandDTDEntities = true; 
         this.m_inDoctype = false;
         this.m_ispreserve = false;
         this.m_isprevtext = false;
         this.m_isUTF8 = false; //  ?? used anywhere ??
         this.m_lineSep = s_systemLineSep;
         this.m_lineSepLen = s_systemLineSep.length;
         this.m_lineSepUse = true;
         // this.m_outputStream = null; // Don't reset it may be re-used
         this.m_preserves.clear();
         this.m_shouldFlush = true;
         this.m_spaceBeforeClose = false;
         this.m_startNewLine = false;
         this.m_writer_set_by_user = false;
    }        
    
    /**
      * Sets the character encoding coming from the xsl:output encoding stylesheet attribute.
      * @param encoding the character encoding
      */
     public void setEncoding(String encoding)
     {
         setOutputProperty(OutputKeys.ENCODING,encoding);
     }
     
    /**
     * Simple stack for boolean values.
     * 
     * This class is a copy of the one in org.apache.xml.utils. 
     * It exists to cut the serializers dependancy on that package.
     * A minor changes from that package are:
     * doesn't implement Clonable
     *  
     * @xsl.usage internal
     */
    static final class BoolStack
    {

      /** Array of boolean values          */
      private boolean m_values[];

      /** Array size allocated           */
      private int m_allocatedSize;

      /** Index into the array of booleans          */
      private int m_index;

      /**
       * Default constructor.  Note that the default
       * block size is very small, for small lists.
       */
      public BoolStack()
      {
        this(32);
      }

      /**
       * Construct a IntVector, using the given block size.
       *
       * @param size array size to allocate
       */
      public BoolStack(int size)
      {

        m_allocatedSize = size;
        m_values = new boolean[size];
        m_index = -1;
      }

      /**
       * Get the length of the list.
       *
       * @return Current length of the list
       */
      public final int size()
      {
        return m_index + 1;
      }

      /**
       * Clears the stack.
       *
       */
      public final void clear()
      {
        m_index = -1;
      }

      /**
       * Pushes an item onto the top of this stack.
       *
       *
       * @param val the boolean to be pushed onto this stack.
       * @return  the <code>item</code> argument.
       */
      public final boolean push(boolean val)
      {

        if (m_index == m_allocatedSize - 1)
          grow();

        return (m_values[++m_index] = val);
      }

      /**
       * Removes the object at the top of this stack and returns that
       * object as the value of this function.
       *
       * @return     The object at the top of this stack.
       * @throws  EmptyStackException  if this stack is empty.
       */
      public final boolean pop()
      {
        return m_values[m_index--];
      }

      /**
       * Removes the object at the top of this stack and returns the
       * next object at the top as the value of this function.
       *
       *
       * @return Next object to the top or false if none there
       */
      public final boolean popAndTop()
      {

        m_index--;

        return (m_index >= 0) ? m_values[m_index] : false;
      }

      /**
       * Set the item at the top of this stack  
       *
       *
       * @param b Object to set at the top of this stack
       */
      public final void setTop(boolean b)
      {
        m_values[m_index] = b;
      }

      /**
       * Looks at the object at the top of this stack without removing it
       * from the stack.
       *
       * @return     the object at the top of this stack.
       * @throws  EmptyStackException  if this stack is empty.
       */
      public final boolean peek()
      {
        return m_values[m_index];
      }

      /**
       * Looks at the object at the top of this stack without removing it
       * from the stack.  If the stack is empty, it returns false.
       *
       * @return     the object at the top of this stack.
       */
      public final boolean peekOrFalse()
      {
        return (m_index > -1) ? m_values[m_index] : false;
      }

      /**
       * Looks at the object at the top of this stack without removing it
       * from the stack.  If the stack is empty, it returns true.
       *
       * @return     the object at the top of this stack.
       */
      public final boolean peekOrTrue()
      {
        return (m_index > -1) ? m_values[m_index] : true;
      }

      /**
       * Tests if this stack is empty.
       *
       * @return  <code>true</code> if this stack is empty;
       *          <code>false</code> otherwise.
       */
      public boolean isEmpty()
      {
        return (m_index == -1);
      }

      /**
       * Grows the size of the stack
       *
       */
      private void grow()
      {

        m_allocatedSize *= 2;

        boolean newVector[] = new boolean[m_allocatedSize];

        System.arraycopy(m_values, 0, newVector, 0, m_index + 1);

        m_values = newVector;
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
            m_writer.write("\" >");
            m_writer.write(m_lineSep, 0, m_lineSepLen);
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
            m_writer.write(" >");
            m_writer.write(m_lineSep, 0, m_lineSepLen);
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
        final java.io.Writer writer = m_writer;
        if (m_needToOutputDocTypeDecl)
        {
            outputDocTypeDecl(m_elemContext.m_elementName, false);
            m_needToOutputDocTypeDecl = false;
        }
        if (m_inDoctype)
        {
            writer.write(" [");
            writer.write(m_lineSep, 0, m_lineSepLen);
            m_inDoctype = false;
        }
    }
    
    /**
     * If set to false the serializer does not expand DTD entities,
     * but leaves them as is, the default value is true;
     */
    public void setDTDEntityExpansion(boolean expand) { 
        m_expandDTDEntities = expand;     
    }
        
    /**
     * Sets the end of line characters to be used during serialization
     * @param eolChars A character array corresponding to the characters to be used.
     */    
    public void setNewLine (char[] eolChars) {
        m_lineSep = eolChars;
        m_lineSepLen = eolChars.length;
    }
}
