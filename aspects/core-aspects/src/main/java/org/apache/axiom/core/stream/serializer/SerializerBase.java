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

    /**
     * The mediatype.  Not used right now.
     */
    protected String m_mediatype;

    protected SourceLocator m_sourceLocator;
    
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
    

