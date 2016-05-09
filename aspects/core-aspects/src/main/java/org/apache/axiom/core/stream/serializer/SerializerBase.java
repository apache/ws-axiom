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

import java.util.HashMap;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.SourceLocator;

import org.apache.axiom.core.stream.StreamException;
import org.xml.sax.Locator;

/**
 * This class acts as a base class for the XML "serializers"
 * and the stream serializers.
 * It contains a number of common fields and methods.
 * 
 * @xsl.usage internal
 */
public abstract class SerializerBase
    implements SerializationHandler, SerializerConstants
{
    SerializerBase() {
        return;
    }
    
    /**
     * The name of the package that this class is in.
     * <p>
     * Not a public API.
     */
    public static final String PKG_NAME;

    /**
     * The same as the name of the package that this class is in
     * except that '.' are replaced with '/'.
     * <p>
     * Not a public API.
     */
    public static final String PKG_PATH;

    static {
        String fullyQualifiedName = SerializerBase.class.getName();
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        if (lastDot < 0) {
            PKG_NAME = "";
        } else {
            PKG_NAME = fullyQualifiedName.substring(0, lastDot);
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < PKG_NAME.length(); i++) {
            char ch = PKG_NAME.charAt(i);
            if (ch == '.')
                sb.append('/');
            else
                sb.append(ch);
        }
        PKG_PATH = sb.toString();
    }

    /** True if a trailing "]]>" still needs to be written to be
     * written out. Used to merge adjacent CDATA sections
     */
    protected boolean m_cdataTagOpen = false;

    /**
     * Tells if we're in an EntityRef event.
     */
    protected boolean m_inEntityRef = false;

    /** This flag is set while receiving events from the external DTD */
    protected boolean m_inExternalDTD = false;

    /**
     * The System ID for the doc type.
     */
    protected String m_doctypeSystem;

    /**
     * The public ID for the doc type.
     */
    protected String m_doctypePublic;

    /**
     * Flag to tell that we need to add the doctype decl, which we can't do
     * until the first element is encountered.
     */
    boolean m_needToOutputDocTypeDecl = true;

    /**
     * Tells if we should write the XML declaration.
     */
    protected boolean m_shouldNotWriteXMLHeader = false;

    /**
     * The standalone value for the doctype.
     */
    private String m_standalone;

    /**
     * True if standalone was specified.
     */
    protected boolean m_standaloneWasSpecified = false;

    /**
     * Flag to tell if indenting (pretty-printing) is on.
     */
    protected boolean m_doIndent = false;
    /**
     * Amount to indent.
     */
    protected int m_indentAmount = 0;

    /**
     * Tells the XML version, for writing out to the XML decl.
     */
    protected String m_version = null;

    /**
     * The mediatype.  Not used right now.
     */
    protected String m_mediatype;

    protected SourceLocator m_sourceLocator;
    

    /**
     * The writer to send output to. This field is only used in the ToStream
     * serializers, but exists here just so that the fireStartDoc() and
     * other fire... methods can flush this writer when tracing.
     */
    protected XmlWriter m_writer = null;
    
    /**
     * A reference to "stack frame" corresponding to
     * the current element. Such a frame is pushed at a startElement()
     * and popped at an endElement(). This frame contains information about
     * the element, such as its namespace URI. 
     */
    protected ElemContext m_elemContext = new ElemContext();
    
    /**
     * A utility buffer for converting Strings passed to
     * character() methods to character arrays.
     * Reusing this buffer means not creating a new character array
     * everytime and it runs faster.
     */
    protected char[] m_charsBuff = new char[60];
    
    /**
     * A utility buffer for converting Strings passed to
     * attribute methods to character arrays.
     * Reusing this buffer means not creating a new character array
     * everytime and it runs faster.
     */
    protected char[] m_attrBuff = new char[30];    

    /**
     * Receive notification of a comment.
     * 
     * @see ExtendedLexicalHandler#comment(String)
     */
    public void comment(String data) throws StreamException
    {
        m_docIsEmpty = false;
        
        final int length = data.length();
        if (length > m_charsBuff.length)
        {
            m_charsBuff = new char[length * 2 + 1];
        }
        data.getChars(0, length, m_charsBuff, 0);
        comment(m_charsBuff, 0, length);
    }

    /**
     * Returns the local name of a qualified name. If the name has no prefix,
     * then it works as the identity (SAX2).
     * @param qname the qualified name 
     * @return the name, but excluding any prefix and colon.
     */
    protected static String getLocalName(String qname)
    {
        final int col = qname.lastIndexOf(':');
        return (col > 0) ? qname.substring(col + 1) : qname;
    }

    /**
     * Receive an object for locating the origin of SAX document events.
     *
     * @param locator An object that can return the location of any SAX document
     * event.
     * 
     * Receive an object for locating the origin of SAX document events.
     *
     * <p>SAX parsers are strongly encouraged (though not absolutely
     * required) to supply a locator: if it does so, it must supply
     * the locator to the application by invoking this method before
     * invoking any of the other methods in the DocumentHandler
     * interface.</p>
     *
     * <p>The locator allows the application to determine the end
     * position of any document-related event, even if the parser is
     * not reporting an error.  Typically, the application will
     * use this information for reporting its own errors (such as
     * character content that does not match an application's
     * business rules).  The information returned by the locator
     * is probably not sufficient for use with a search engine.</p>
     *
     * <p>Note that the locator will return correct information only
     * during the invocation of the events in this interface.  The
     * application should not attempt to use it at any other time.</p>
     */
    public void setDocumentLocator(Locator locator)
    {
        return;

        // I don't do anything with this yet.
    }

    /**
     * Report the end of an entity.
     *
     * @param name The name of the entity that is ending.
     * @throws StreamException The application may raise an exception.
     * @see #startEntity
     */
    public void endEntity(String name) throws StreamException
    {
        if (name.equals("[dtd]"))
            m_inExternalDTD = false;
        m_inEntityRef = false;
    }

    /**
     * Flush and close the underlying java.io.Writer. This method applies to
     * ToStream serializers, not ToSAXHandler serializers.
     * @see ToStream
     */
    public void close()
    {
        // do nothing (base behavior)
    }

    /**
     * Initialize global variables
     */
    protected void initCDATA()
    {
        // CDATA stack
        //        _cdataStack = new Stack();
        //        _cdataStack.push(new Integer(-1)); // push dummy value
    }

    /**
     * Returns the character encoding to be used in the output document.
     * @return the character encoding to be used in the output document.
     */
    public String getEncoding()
    {
        return getOutputProperty(OutputKeys.ENCODING);
    }

   /**
     * Sets the character encoding coming from the xsl:output encoding stylesheet attribute.
     * @param m_encoding the character encoding
     */
    public void setEncoding(String encoding)
    {
        setOutputProperty(OutputKeys.ENCODING,encoding);
    }

    /**
     * Sets the value coming from the xsl:output omit-xml-declaration stylesheet attribute
     * @param b true if the XML declaration is to be omitted from the output
     * document.
     */
    public void setOmitXMLDeclaration(boolean b)
    {
        String val = b ? "yes":"no";
        setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,val);
    }


    /**
     * @return true if the XML declaration is to be omitted from the output
     * document.
     */
    public boolean getOmitXMLDeclaration()
    {
        return m_shouldNotWriteXMLHeader;
    }

    /**
     * Returns the previously set value of the value to be used as the public
     * identifier in the document type declaration (DTD).
     * 
     *@return the public identifier to be used in the DOCTYPE declaration in the
     * output document.
     */    
    public String getDoctypePublic()
    {
        return m_doctypePublic;
    }

    /** Set the value coming from the xsl:output doctype-public stylesheet attribute.
      * @param doctypePublic the public identifier to be used in the DOCTYPE
      * declaration in the output document.
      */
    public void setDoctypePublic(String doctypePublic)
    {
        setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctypePublic);
    }


    /**
     * Returns the previously set value of the value to be used
     * as the system identifier in the document type declaration (DTD).
	 * @return the system identifier to be used in the DOCTYPE declaration in
	 * the output document.
     *
     */    
    public String getDoctypeSystem()
    {
        return m_doctypeSystem;
    }

    /** Set the value coming from the xsl:output doctype-system stylesheet attribute.
      * @param doctypeSystem the system identifier to be used in the DOCTYPE
      * declaration in the output document.
      */
    public void setDoctypeSystem(String doctypeSystem)
    {
        setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctypeSystem);
    }

    /** Set the value coming from the xsl:output doctype-public and doctype-system stylesheet properties
     * @param doctypeSystem the system identifier to be used in the DOCTYPE
     * declaration in the output document.
     * @param doctypePublic the public identifier to be used in the DOCTYPE
     * declaration in the output document.
     */
    public void setDoctype(String doctypeSystem, String doctypePublic)
    {
        setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctypeSystem);
        setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctypePublic);
    }

    /**
     * Sets the value coming from the xsl:output standalone stylesheet attribute.
     * @param standalone a value of "yes" indicates that the
     * <code>standalone</code> delaration is to be included in the output
     * document. This method remembers if the value was explicitly set using
     * this method, verses if the value is the default value.
     */
    public void setStandalone(String standalone)
    {
        setOutputProperty(OutputKeys.STANDALONE, standalone);
    }
    /**
     * Sets the XSL standalone attribute, but does not remember if this is a
     * default or explicite setting.
     * @param standalone "yes" | "no"
     */    
    protected void setStandaloneInternal(String standalone)
    {
        if ("yes".equals(standalone))
            m_standalone = "yes";
        else
            m_standalone = "no";
        
    }

    /**
     * Gets the XSL standalone attribute
     * @return a value of "yes" if the <code>standalone</code> delaration is to
     * be included in the output document.
     *  @see XSLOutputAttributes#getStandalone()
     */
    public String getStandalone()
    {
        return m_standalone;
    }

    /**
     * @return true if the output document should be indented to visually
     * indicate its structure.
     */
    public boolean getIndent()
    {
        return m_doIndent;
    }
    /**
     * Gets the mediatype the media-type or MIME type associated with the output
     * document.
     * @return the mediatype the media-type or MIME type associated with the
     * output document.
     */
    public String getMediaType()
    {
        return m_mediatype;
    }

    /**
     * Gets the version of the output format.
     * @return the version of the output format.
     */
    public String getVersion()
    {
        return m_version;
    }

    /**
     * Sets the value coming from the xsl:output version attribute.
     * @param version the version of the output format.
     * @see SerializationHandler#setVersion(String)
     */
    public void setVersion(String version)
    {
        setOutputProperty(OutputKeys.VERSION, version);
    }

    /**
     * Sets the value coming from the xsl:output media-type stylesheet attribute.
     * @param mediaType the non-null media-type or MIME type associated with the
     * output document.
     * @see javax.xml.transform.OutputKeys#MEDIA_TYPE
     * @see SerializationHandler#setMediaType(String)
     */
    public void setMediaType(String mediaType)
    {
        setOutputProperty(OutputKeys.MEDIA_TYPE,mediaType);
    }

    /**
     * @return the number of spaces to indent for each indentation level.
     */
    public int getIndentAmount()
    {
        return m_indentAmount;
    }

    /**
     * Sets the indentation amount.
     * @param m_indentAmount The m_indentAmount to set
     */
    public void setIndentAmount(int m_indentAmount)
    {
        this.m_indentAmount = m_indentAmount;
    }

    /**
     * Sets the value coming from the xsl:output indent stylesheet
     * attribute.
     * @param doIndent true if the output document should be indented to
     * visually indicate its structure.
     * @see XSLOutputAttributes#setIndent(boolean)
     */
    public void setIndent(boolean doIndent)
    {
        String val = doIndent ? "yes":"no";
        setOutputProperty(OutputKeys.INDENT,val);
    }

    /**
     * This method is used when a prefix/uri namespace mapping
     * is indicated after the element was started with a 
     * startElement() and before and endElement().
     * startPrefixMapping(prefix,uri) would be used before the
     * startElement() call.
     * @param uri the URI of the namespace
     * @param prefix the prefix associated with the given URI.
     * 
     * @see ExtendedContentHandler#namespaceAfterStartElement(String, String)
     */
    public void namespaceAfterStartElement(String uri, String prefix)
        throws StreamException
    {
        // default behavior is to do nothing
    }

    /**
     * Tell if two strings are equal, without worry if the first string is null.
     *
     * @param p String reference, which may be null.
     * @param t String reference, which may be null.
     *
     * @return true if strings are equal.
     */
    private static final boolean subPartMatch(String p, String t)
    {
        return (p == t) || ((null != p) && (p.equals(t)));
    }

    /**
     * Returns the local name of a qualified name. 
     * If the name has no prefix,
     * then it works as the identity (SAX2). 
     * 
     * @param qname a qualified name
     * @return returns the prefix of the qualified name,
     * or null if there is no prefix.
     */
    protected static final String getPrefixPart(String qname)
    {
        final int col = qname.indexOf(':');
        return (col > 0) ? qname.substring(0, col) : null;
        //return (col > 0) ? qname.substring(0,col) : "";
    }

    /**
     * Entity reference event.
     *
     * @param name Name of entity
     *
     * @throws StreamException
     */
    public void entityReference(String name) throws StreamException
    {

        flushPending();

        startEntity(name);
        endEntity(name);
    }

    /**
     * This method gets the nodes value as a String and uses that String as if
     * it were an input character notification.
     * @param node the Node to serialize
     * @throws StreamException
     */
    public void characters(org.w3c.dom.Node node)
        throws StreamException
    {
        flushPending();
        String data = node.getNodeValue();
        if (data != null)
        {
            final int length = data.length();
            if (length > m_charsBuff.length)
            {
                m_charsBuff = new char[length * 2 + 1];
            }
            data.getChars(0, length, m_charsBuff, 0);
            characters(m_charsBuff, 0, length);
        }
    }
    

    /**
     * Report the characters event
     * @param chars  content of characters
     * @param start  starting index of characters to output
     * @param length  number of characters to output
     */
//    protected void fireCharEvent(char[] chars, int start, int length)
//        throws StreamException
//    {
//        if (m_tracer != null)
//            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_CHARACTERS, chars, start,length);     	        	    	
//    }
//        

    /**
     * Receive notification of the beginning of a document.
     * This method is never a self generated call, 
     * but only called externally.
     *
     * <p>The SAX parser will invoke this method only once, before any
     * other methods in this interface or in DTDHandler (except for
     * setDocumentLocator).</p>
     *
     * @throws StreamException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws StreamException
     */
    public void startDocument() throws StreamException
    {

        // if we do get called with startDocument(), handle it right away       
        startDocumentInternal();
        return;
    }   
    
    /**
     * This method handles what needs to be done at a startDocument() call,
     * whether from an external caller, or internally called in the 
     * serializer.  For historical reasons the serializer is flexible to
     * startDocument() not always being called.
     * Even if no external call is
     * made into startDocument() this method will always be called as a self
     * generated internal startDocument, it handles what needs to be done at a
     * startDocument() call.
     * 
     * This method exists just to make sure that startDocument() is only ever
     * called from an external caller, which in principle is just a matter of
     * style.
     * 
     * @throws StreamException
     */
    protected void startDocumentInternal() throws StreamException
    {
    } 
    /**
     * This method is used to set the source locator, which might be used to
     * generated an error message.
     * @param locator the source locator
     *
     * @see ExtendedContentHandler#setSourceLocator(javax.xml.transform.SourceLocator)
     */
    public void setSourceLocator(SourceLocator locator)
    {
        m_sourceLocator = locator;    
    }

    
    public boolean reset()
    {
    	resetSerializerBase();
    	return true;
    }
    
    /**
     * Reset all of the fields owned by SerializerBase
     *
     */
    private void resetSerializerBase()
    {
        this.m_cdataTagOpen = false;
        this.m_docIsEmpty = true;
    	this.m_doctypePublic = null;
    	this.m_doctypeSystem = null;
    	this.m_doIndent = false;
        this.m_elemContext = new ElemContext();
    	this.m_indentAmount = 0;
    	this.m_inEntityRef = false;
    	this.m_inExternalDTD = false;
    	this.m_mediatype = null;
    	this.m_needToOutputDocTypeDecl = false;
        if (m_OutputProps != null)
            this.m_OutputProps.clear();
        if (m_OutputPropsDefault != null)
            this.m_OutputPropsDefault.clear();
    	this.m_shouldNotWriteXMLHeader = false;
    	this.m_sourceLocator = null;
    	this.m_standalone = null;
    	this.m_standaloneWasSpecified = false;
    	this.m_version = null;
    	// don't set writer to null, so that it might be re-used
    	//this.m_writer = null;
    }
    
    /**
     * Returns true if the serializer is used for temporary output rather than
     * final output.
     * 
     * This concept is made clear in the XSLT 2.0 draft.
     */
    final boolean inTemporaryOutputState() 
    {
        /* This is a hack. We should really be letting the serializer know
         * that it is in temporary output state with an explicit call, but
         * from a pragmatic point of view (for now anyways) having no output
         * encoding at all, not even the default UTF-8 indicates that the serializer
         * is being used for temporary RTF.
         */ 
        return (getEncoding() == null);
        
    }
    
    /**
     * @see org.xml.sax.DTDHandler#notationDecl(java.lang.String, java.lang.String, java.lang.String)
     */
    public void notationDecl(String arg0, String arg1, String arg2)
        throws StreamException {
        // This method just provides a definition to satisfy the interface
        // A particular sub-class of SerializerBase provides the implementation (if desired)        
    }

    /**
     * @see org.xml.sax.DTDHandler#unparsedEntityDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void unparsedEntityDecl(
        String arg0,
        String arg1,
        String arg2,
        String arg3)
        throws StreamException {
        // This method just provides a definition to satisfy the interface
        // A particular sub-class of SerializerBase provides the implementation (if desired)        
    }

    /**
     * If set to false the serializer does not expand DTD entities,
     * but leaves them as is, the default value is true.
     */
    public void setDTDEntityExpansion(boolean expand) {
        // This method just provides a definition to satisfy the interface
        // A particular sub-class of SerializerBase provides the implementation (if desired)        
    }
 

    boolean m_docIsEmpty = true;
    /**
     * Return true if nothing has been sent to this result tree yet.
     * <p>
     * This is not a public API.
     * 
     * @xsl.usage internal
     */
    public boolean documentIsEmpty() {
        // If we haven't called startDocument() yet, then this document is empty
        return m_docIsEmpty && (m_elemContext.m_currentElemDepth == 0);
    }    
    
    /**
     * Get the value of an output property,
     * the explicit value, if any, otherwise the
     * default value, if any, otherwise null.
     */
    public String getOutputProperty(String name) {
        String val = getOutputPropertyNonDefault(name);
        // If no explicit value, try to get the default value
        if (val == null)
            val = getOutputPropertyDefault(name);
        return val;
        
    }
    /**
     * Get the value of an output property, 
     * not the default value. If there is a default
     * value, but no non-default value this method
     * will return null.
     * <p>
     * 
     */
    public String getOutputPropertyNonDefault(String name )
    {
        return getProp(name,false);
    }

    /**
     * Get the default value of an xsl:output property,
     * which would be null only if no default value exists
     * for the property.
     */
    public String getOutputPropertyDefault(String name) {
        return getProp(name, true);
    } 
    
    /**
     * Set the value for the output property, typically from
     * an xsl:output element, but this does not change what
     * the default value is.
     */
    public void   setOutputProperty(String name, String val) {
        setProp(name,val,false);
        
    }
    
    /**
     * Set the default value for an output property, but this does
     * not impact any explicitly set value.
     */
    public void   setOutputPropertyDefault(String name, String val) {
        setProp(name,val,true);
        
    }
    
    /**
     * A mapping of keys to explicitly set values, for example if 
     * and <xsl:output/> has an "encoding" attribute, this
     * map will have what that attribute maps to.
     */
    private HashMap m_OutputProps;
    /**
     * A mapping of keys to default values, for example if
     * the default value of the encoding is "UTF-8" then this
     * map will have that "encoding" maps to "UTF-8".
     */
    private HashMap m_OutputPropsDefault;
    
    Set getOutputPropDefaultKeys() {
        return m_OutputPropsDefault.keySet();
    }
    Set getOutputPropKeys() {
        return m_OutputProps.keySet();
    }
    
    private String getProp(String name, boolean defaultVal) {
        if (m_OutputProps == null) {
            m_OutputProps = new HashMap();
            m_OutputPropsDefault = new HashMap();
        }
        
        String val;
        if (defaultVal)
            val = (String) m_OutputPropsDefault.get(name);
        else
            val = (String) m_OutputProps.get(name);
        
        return val;
        
    }
    /**
     * 
     * @param name The name of the property, e.g. "{http://myprop}indent-tabs" or "indent".
     * @param val The value of the property, e.g. "4"
     * @param defaultVal true if this is a default value being set for the property as 
     * opposed to a user define on, set say explicitly in the stylesheet or via JAXP
     */
    void setProp(String name, String val, boolean defaultVal) {
        if (m_OutputProps == null) {
            m_OutputProps = new HashMap();
            m_OutputPropsDefault = new HashMap();
        }
        
        if (defaultVal)
            m_OutputPropsDefault.put(name,val);
        else {
            m_OutputProps.put(name,val);
        }
        

    }

    /**
     * Get the first char of the local name
     * @param name Either a local name, or a local name
     * preceeded by a uri enclosed in curly braces.
     */
    static char getFirstCharLocName(String name) {
        final char first;
        int i = name.indexOf('}');
        if (i < 0)
            first = name.charAt(0);
        else
            first = name.charAt(i+1);
        return first;
    }
}
    

