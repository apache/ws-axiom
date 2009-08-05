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

package org.apache.axiom.om.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.apache.axiom.util.stax.dialect.StAXDialectDetector;
import org.apache.axiom.util.stax.wrapper.ImmutableXMLInputFactory;
import org.apache.axiom.util.stax.wrapper.ImmutableXMLOutputFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

/**
 * Utility class containing StAX related methods.
 * <p>This class defines a set of methods to get {@link XMLStreamReader} and {@link XMLStreamWriter}
 * instances. This class caches the corresponding factories ({@link XMLInputFactory}
 * and {@link XMLOutputFactory} objects) by classloader (default) or as singletons.
 * The behavior can be changed using {@link #setFactoryPerClassLoader(boolean)}.</p>
 * <p>Default properties for these factories can be specified using
 * <tt>XMLInputFactory.properties</tt> and <tt>XMLOutputFactory.properties</tt> files.
 * When a new factory is instantiated, this class will attempt to load the corresponding file using
 * the context classloader. This class supports properties with boolean, integer and string values.
 * Both standard StAX properties and implementation specific properties can be specified. This
 * feature should be used with care since changing some properties to non default values will break
 * Axiom. Good candidates for <tt>XMLInputFactory.properties</tt> are:</p>
 * <dl>
 *   <dt><tt>javax.xml.stream.isCoalescing</tt></dt>
 *   <dd>Requires the processor to coalesce adjacent character data (text nodes and CDATA
 *       sections). This property also controls whether CDATA sections are reported or not.</dd>
 *   <dt><tt>com.ctc.wstx.inputBufferLength</tt></dt>
 *   <dd>Size of input buffer (in chars), to use for reading XML content from input stream/reader.
 *       This property is Woodstox specific.</dd>
 *   <dt><tt>com.ctc.wstx.minTextSegment</tt></dt>
 *   <dd>Property to specify shortest non-complete text segment (part of CDATA section or text
 *       content) that the parser is allowed to return, if not required to coalesce text.
 *       This property is Woodstox specific.</dt>
 * </dl>
 * <p>Good candidates for <tt>XMLOutputFactory.properties</tt> are:</p>
 * <dl>
 *   <dt><tt>com.ctc.wstx.outputEscapeCr</tt></dt>
 *   <dd>Property that determines whether Carriage Return (\r) characters are to be escaped when
 *       output or not. If enabled, all instances of of character \r are escaped using a character
 *       entity (where possible, that is, within CHARACTERS events, and attribute values).
 *       Otherwise they are output as is. The main reason to enable this property is to ensure
 *       that carriage returns are preserved as is through parsing, since otherwise they will be
 *       converted to canonical XML linefeeds (\n), when occurring along or as part of \r\n pair.
 *       This property is Woodstox specific.</dd>
 * </dl>
 */
public class StAXUtils {
    private static Log log = LogFactory.getLog(StAXUtils.class);
    private static boolean isDebugEnabled = log.isDebugEnabled();
    
    // If isFactoryPerClassLoader is true (default), then 
    // a separate singleton XMLInputFactory and XMLOutputFactory is maintained
    // for the each classloader.  The different classloaders may be using different
    // implementations of STAX.
    // 
    // If isFactoryPerClassLoader is false, then
    // a single XMLInputFactory and XMLOutputFactory is constructed using
    // the classloader that loaded StAXUtils. 
    private static boolean isFactoryPerClassLoader = true;
    
    // These static singletons are used when the XML*Factory is created with
    // the StAXUtils classloader.
    private static XMLInputFactory inputFactory = null;
    private static XMLInputFactory inputNDFactory = null;
    private static XMLOutputFactory outputFactory = null;
    
    // These maps are used for the isFactoryPerClassLoader==true case
    // The maps are synchronized and weak.
    private static Map inputFactoryPerCL = Collections.synchronizedMap(new WeakHashMap());
    private static Map inputNDFactoryPerCL = Collections.synchronizedMap(new WeakHashMap());
    private static Map outputFactoryPerCL = Collections.synchronizedMap(new WeakHashMap());
    
    /**
     * Gets an XMLInputFactory instance from pool.
     *
     * @return an XMLInputFactory instance.
     */
    public static XMLInputFactory getXMLInputFactory() {
        
        if (isFactoryPerClassLoader) {
            return getXMLInputFactory_perClassLoader(false);
        } else {
            return getXMLInputFactory_singleton(false);
        }
    }
    
    /**
     * Get XMLInputFactory
     * @param factoryPerClassLoaderPolicy 
     * (if true, then factory using current classloader.
     * if false, then factory using the classloader that loaded StAXUtils)
     * @return XMLInputFactory
     */
    public static XMLInputFactory getXMLInputFactory(boolean factoryPerClassLoaderPolicy) {
        if (factoryPerClassLoaderPolicy) {
            return getXMLInputFactory_perClassLoader(false);
        } else {
            return getXMLInputFactory_singleton(false);
        }
    }

    /**
     * @deprecated
     * Returns an XMLInputFactory instance for reuse.
     *
     * @param factory An XMLInputFactory instance that is available for reuse
     */
    public static void releaseXMLInputFactory(XMLInputFactory factory) {
    }

    public static XMLStreamReader createXMLStreamReader(final InputStream in, final String encoding)
            throws XMLStreamException {
        final XMLInputFactory inputFactory = getXMLInputFactory();
        try {
            XMLStreamReader reader = 
                (XMLStreamReader) 
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws XMLStreamException {
                        return inputFactory.createXMLStreamReader(in, encoding);
                    }
                }
                );
            if (isDebugEnabled) {
                log.debug("XMLStreamReader is " + reader.getClass().getName());
            }
            return reader;
        } catch (PrivilegedActionException pae) {
            throw (XMLStreamException) pae.getException();
        }
    }

    public static XMLStreamReader createXMLStreamReader(final InputStream in)
            throws XMLStreamException {
        final XMLInputFactory inputFactory = getXMLInputFactory();
        try {
            XMLStreamReader reader = 
                (XMLStreamReader)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws XMLStreamException {
                        return inputFactory.createXMLStreamReader(in);
                    }
                }
                );
            
            if (isDebugEnabled) {
                log.debug("XMLStreamReader is " + reader.getClass().getName());
            }
            return reader;
        } catch (PrivilegedActionException pae) {
            throw (XMLStreamException) pae.getException();
        }
    }

    public static XMLStreamReader createXMLStreamReader(final Reader in)
            throws XMLStreamException {
        final XMLInputFactory inputFactory = getXMLInputFactory();
        try {
            XMLStreamReader reader = 
                (XMLStreamReader)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws XMLStreamException {
                        return inputFactory.createXMLStreamReader(in);
                    }
                }
                );
            if (isDebugEnabled) {
                log.debug("XMLStreamReader is " + reader.getClass().getName());
            }
            return reader;
        } catch (PrivilegedActionException pae) {
            throw (XMLStreamException) pae.getException();
        }
    }

    /**
     * Gets an XMLOutputFactory instance from pool.
     *
     * @return an XMLOutputFactory instance.
     */
    public static XMLOutputFactory getXMLOutputFactory() {
        if (isFactoryPerClassLoader) {
            return getXMLOutputFactory_perClassLoader();
        } else {
            return getXMLOutputFactory_singleton();
        }
    }
    
    /**
     * Get XMLOutputFactory
     * @param factoryPerClassLoaderPolicy 
     * (if true, then factory using current classloader.
     * if false, then factory using the classloader that loaded StAXUtils)
     * @return XMLInputFactory
     */
    public static XMLOutputFactory getXMLOutputFactory(boolean factoryPerClassLoaderPolicy) {
        if (factoryPerClassLoaderPolicy) {
            return getXMLOutputFactory_perClassLoader();
        } else {
            return getXMLOutputFactory_singleton();
        }
    }
    
    /**
     * Set the policy for how to maintain the XMLInputFactory and XMLOutputFactory
     * @param value (if false, then one singleton...if true...then singleton per class loader 
     *  (default is true)
     */
    public static void setFactoryPerClassLoader(boolean value) {
        isFactoryPerClassLoader = value;
    }

    /**
     * @deprecated
     * Returns an XMLOutputFactory instance for reuse.
     *
     * @param factory An XMLOutputFactory instance that is available for reuse.
     */
    public static void releaseXMLOutputFactory(XMLOutputFactory factory) {
    }

    public static XMLStreamWriter createXMLStreamWriter(final OutputStream out)
            throws XMLStreamException {
        final XMLOutputFactory outputFactory = getXMLOutputFactory();
        try {
            XMLStreamWriter writer = 
                (XMLStreamWriter)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws XMLStreamException {
                        return outputFactory.createXMLStreamWriter(out, OMConstants.DEFAULT_CHAR_SET_ENCODING);
                    }
                }
                );
                
            if (isDebugEnabled) {
                log.debug("XMLStreamWriter is " + writer.getClass().getName());
            }
            return writer;
        } catch (PrivilegedActionException pae) {
            throw (XMLStreamException) pae.getException();
        }
    }

    public static XMLStreamWriter createXMLStreamWriter(final OutputStream out, final String encoding)
            throws XMLStreamException {
        final XMLOutputFactory outputFactory = getXMLOutputFactory();
        try {
            XMLStreamWriter writer = 
                (XMLStreamWriter)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws XMLStreamException {
                        return outputFactory.createXMLStreamWriter(out, encoding);
                    }
                }
                );
            
            if (isDebugEnabled) {
                log.debug("XMLStreamWriter is " + writer.getClass().getName());
            }
            return writer;
        } catch (PrivilegedActionException pae) {
            throw (XMLStreamException) pae.getException();
        }
    }

    public static XMLStreamWriter createXMLStreamWriter(final Writer out)
            throws XMLStreamException {
        final XMLOutputFactory outputFactory = getXMLOutputFactory();
        try {
            XMLStreamWriter writer = 
                (XMLStreamWriter)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws XMLStreamException {
                        return outputFactory.createXMLStreamWriter(out);
                    }
                }
                );
            if (isDebugEnabled) {
                log.debug("XMLStreamWriter is " + writer.getClass().getName());
            }
            return writer;
        } catch (PrivilegedActionException pae) {
            throw (XMLStreamException) pae.getException();
        }
    }

    /**
     * @deprecated
     */
    public static void reset() {
    }
    
    /**
     * Load factory properties from a resource. The context class loader is used to locate
     * the resource. The method converts boolean and integer values to the right Java types.
     * All other values are returned as strings.
     * 
     * @param name
     * @return
     */
    // This has package access since it is used from within anonymous inner classes
    static Map loadFactoryProperties(String name) {
        ClassLoader cl = getContextClassLoader();
        InputStream in = cl.getResourceAsStream(name);
        if (in == null) {
            return null;
        } else {
            try {
                Properties rawProps = new Properties();
                Map props = new HashMap();
                rawProps.load(in);
                for (Iterator it = rawProps.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry)it.next();
                    String strValue = (String)entry.getValue();
                    Object value;
                    if (strValue.equals("true")) {
                        value = Boolean.TRUE;
                    } else if (strValue.equals("false")) {
                        value = Boolean.FALSE;
                    } else {
                        try {
                            value = Integer.valueOf(strValue);
                        } catch (NumberFormatException ex) {
                            value = strValue;
                        }
                    }
                    props.put(entry.getKey(), value);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Loaded factory properties from " + name + ": " + props);
                }
                return props;
            } catch (IOException ex) {
                log.error("Failed to read " + name, ex);
                return null;
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    // Ignore
                }
            }
        }
    }
    
    private static XMLInputFactory newXMLInputFactory(final ClassLoader classLoader,
            final boolean isNetworkDetached) {
        
        return (XMLInputFactory)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                ClassLoader savedClassLoader;
                if (classLoader == null) {
                    savedClassLoader = null;
                } else {
                    savedClassLoader = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(classLoader);
                }
                try {
                    XMLInputFactory factory = XMLInputFactory.newInstance();
                    // Woodstox by default creates coalescing parsers. Even if this violates
                    // the StAX specs, for compatibility with Woodstox, we always enable the
                    // coalescing mode. Note that we need to do that before loading
                    // XMLInputFactory.properties so that this setting can be overridden.
                    factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
                    Map props = loadFactoryProperties("XMLInputFactory.properties");
                    if (props != null) {
                        for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
                            Map.Entry entry = (Map.Entry)it.next();
                            factory.setProperty((String)entry.getKey(), entry.getValue());
                        }
                    }
                    if (isNetworkDetached) {
                        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, 
                                  Boolean.FALSE);
                        // Some StAX parser such as Woodstox still try to load the external DTD subset,
                        // even if IS_SUPPORTING_EXTERNAL_ENTITIES is set to false. To work around this,
                        // we add a custom XMLResolver that returns empty documents. See WSTX-117 for
                        // an interesting discussion about this.
                        factory.setXMLResolver(new XMLResolver() {
                            public Object resolveEntity(String publicID, String systemID, String baseURI,
                                    String namespace) throws XMLStreamException {
                                return new ByteArrayInputStream(new byte[0]);
                            }
                        });
                    }
                    StAXDialect dialect = StAXDialectDetector.getDialect(factory.getClass());
                    return new ImmutableXMLInputFactory(dialect.normalize(
                            dialect.makeThreadSafe(factory)));
                } finally {
                    if (savedClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(savedClassLoader);
                    }
                }
            }
        });
    }

    /**
     * @return XMLInputFactory for the current classloader
     */
    private static XMLInputFactory getXMLInputFactory_perClassLoader(final boolean isNetworkDetached) {
        
        ClassLoader cl = getContextClassLoader();
        XMLInputFactory factory;
        if (cl == null) {
            factory = getXMLInputFactory_singleton(isNetworkDetached);
        } else {
            // Check the cache
            if (isNetworkDetached) {
                factory = (XMLInputFactory) inputNDFactoryPerCL.get(cl);
            } else {
                factory = (XMLInputFactory) inputFactoryPerCL.get(cl);
            }
            
            // If not found in the cache map, crate a new factory
            if (factory == null) {

                if (log.isDebugEnabled()) {
                    log.debug("About to create XMLInputFactory implementation with " +
                                "classloader=" + cl);
                    log.debug("The classloader for javax.xml.stream.XMLInputFactory is: "
                              + XMLInputFactory.class.getClassLoader());
                }
                factory = null;
                try {
                    factory = newXMLInputFactory(null, isNetworkDetached);
                } catch (ClassCastException cce) {
                    if (log.isDebugEnabled()) {
                        log.debug("Failed creation of XMLInputFactory implementation with " +
                                        "classloader=" + cl);
                        log.debug("Exception is=" + cce);
                        log.debug("Attempting with classloader: " + 
                                  XMLInputFactory.class.getClassLoader());
                    }
                    factory = newXMLInputFactory(XMLInputFactory.class.getClassLoader(),
                            isNetworkDetached);
                }
                    
                if (factory != null) {
                    // Cache the new factory
                    if (isNetworkDetached) {
                        inputNDFactoryPerCL.put(cl, factory);
                    } else {
                        inputFactoryPerCL.put(cl, factory);
                    }
                    
                    if (log.isDebugEnabled()) {
                        log.debug("Created XMLInputFactory = " + factory.getClass() + 
                                  " with classloader=" + cl);
                        log.debug("Size of XMLInputFactory map =" + inputFactoryPerCL.size());
                        log.debug("isNetworkDetached =" + isNetworkDetached);
                    }
                } else {
                    factory = getXMLInputFactory_singleton(isNetworkDetached);
                }
            }
            
        }
        return factory;
    }
    
    /**
     * @return singleton XMLInputFactory loaded with the StAXUtils classloader
     */
    private static XMLInputFactory getXMLInputFactory_singleton(final boolean isNetworkDetached) {
        XMLInputFactory f;
        if (isNetworkDetached) {
            f = inputNDFactory;
        } else {
            f = inputFactory;
        }
        if (f == null) {
            f = newXMLInputFactory(StAXUtils.class.getClassLoader(), isNetworkDetached);
            if (isNetworkDetached) {
                inputNDFactory = f;
            } else {
                inputFactory = f;
            }
            if (log.isDebugEnabled()) {
                if (f != null) {
                    if (isNetworkDetached) {
                        log.debug("Created singleton network detached XMLInputFactory = " + f.getClass());
                    } else {
                        log.debug("Created singleton XMLInputFactory = " + f.getClass());
                    }
                }
            }
        }
        
        return f;
    }
    
    private static XMLOutputFactory newXMLOutputFactory(final ClassLoader classLoader) {
        return (XMLOutputFactory)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                ClassLoader savedClassLoader;
                if (classLoader == null) {
                    savedClassLoader = null;
                } else {
                    savedClassLoader = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(classLoader);
                }
                try {
                    XMLOutputFactory factory = XMLOutputFactory.newInstance();
                    factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, 
                                        Boolean.FALSE);
                    Map props = loadFactoryProperties("XMLOutputFactory.properties");
                    if (props != null) {
                        for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
                            Map.Entry entry = (Map.Entry)it.next();
                            factory.setProperty((String)entry.getKey(), entry.getValue());
                        }
                    }
                    StAXDialect dialect = StAXDialectDetector.getDialect(factory.getClass());
                    return new ImmutableXMLOutputFactory(dialect.normalize(
                            dialect.makeThreadSafe(factory)));
                } finally {
                    if (savedClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(savedClassLoader);
                    }
                }
            }
        });
    }
    
    /**
     * @return XMLOutputFactory for the current classloader
     */
    public static XMLOutputFactory getXMLOutputFactory_perClassLoader() {
        ClassLoader cl = getContextClassLoader();
        XMLOutputFactory factory;
        if (cl == null) {
            factory = getXMLOutputFactory_singleton();
        } else {
            factory = (XMLOutputFactory) outputFactoryPerCL.get(cl);
            if (factory == null) {
                if (log.isDebugEnabled()) {
                    log.debug("About to create XMLOutputFactory implementation with " +
                                "classloader=" + cl);
                    log.debug("The classloader for javax.xml.stream.XMLOutputFactory is: " + 
                              XMLOutputFactory.class.getClassLoader());
                }
                try {
                    factory = newXMLOutputFactory(null);
                } catch (ClassCastException cce) {
                    if (log.isDebugEnabled()) {
                        log.debug("Failed creation of XMLOutputFactory implementation with " +
                                        "classloader=" + cl);
                        log.debug("Exception is=" + cce);
                        log.debug("Attempting with classloader: " + 
                                  XMLOutputFactory.class.getClassLoader());
                    }
                    factory = newXMLOutputFactory(XMLOutputFactory.class.getClassLoader());
                }
                if (factory != null) {
                    outputFactoryPerCL.put(cl, factory);
                    if (log.isDebugEnabled()) {
                        log.debug("Created XMLOutputFactory = " + factory.getClass() 
                                  + " for classloader=" + cl);
                        log.debug("Size of XMLOutputFactory map =" + outputFactoryPerCL.size());
                    }
                } else {
                    factory = getXMLOutputFactory_singleton();
                }
            }
            
        }
        return factory;
    }
    
    /**
     * @return XMLOutputFactory singleton loaded with the StAXUtils classloader
     */
    public static XMLOutputFactory getXMLOutputFactory_singleton() {
        if (outputFactory == null) {
            outputFactory = newXMLOutputFactory(StAXUtils.class.getClassLoader());
            if (log.isDebugEnabled()) {
                if (outputFactory != null) {
                    log.debug("Created singleton XMLOutputFactory = " + outputFactory.getClass());
                }
            }
        }
        return outputFactory;
    }
    
    /**
     * @return Trhead Context ClassLoader
     */
    private static ClassLoader getContextClassLoader() {
        ClassLoader cl = (ClassLoader) AccessController.doPrivileged(
                    new PrivilegedAction() {
                        public Object run()  {
                            return Thread.currentThread().getContextClassLoader();
                        }
                    }
            );
        
        return cl;
    }

    /**
     * Create an XMLStreamReader that will operate when detached from a network.
     * The XMLStreamReader is created from a OMInputFactory that has external
     * entities disabled.  This kind of XMLStreamReader is useful for reading 
     * deployment information.
     * @param in
     * @param encoding
     * @return
     * @throws XMLStreamException
     */
    public static XMLStreamReader createNetworkDetachedXMLStreamReader(final InputStream in, final String encoding)
        throws XMLStreamException {
        final XMLInputFactory inputFactory = getNetworkDetachedXMLInputFactory();
        try {
            XMLStreamReader reader = 
                (XMLStreamReader) 
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws XMLStreamException {
                        return inputFactory.createXMLStreamReader(in, encoding);
                    }
                }
                );
            if (isDebugEnabled) {
                log.debug("XMLStreamReader is " + reader.getClass().getName());
            }
            return reader;
        } catch (PrivilegedActionException pae) {
            throw (XMLStreamException) pae.getException();
        }
    }

    /**
     * Gets an XMLInputFactory instance from pool.
     *
     * @return an XMLInputFactory instance.
     */
    public static XMLInputFactory getNetworkDetachedXMLInputFactory() {
        if (isFactoryPerClassLoader) {
            return getXMLInputFactory_perClassLoader(true);
        } else {
            return getXMLInputFactory_singleton(true);
        }
    }
    
    /**
     * Create an XMLStreamReader that will operate when detached from a network.
     * The XMLStreamReader is created from a OMInputFactory that has external
     * entities disabled.  This kind of XMLStreamReader is useful for reading 
     * deployment information.
     * 
     * @param in
     * @return
     * @throws XMLStreamException
     */
    public static XMLStreamReader createNetworkDetachedXMLStreamReader(final InputStream in)
    throws XMLStreamException {
        final XMLInputFactory inputFactory = getNetworkDetachedXMLInputFactory();
        try {
            XMLStreamReader reader = 
                (XMLStreamReader)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws XMLStreamException {
                        return inputFactory.createXMLStreamReader(in);
                    }
                }
                );

            if (isDebugEnabled) {
                log.debug("XMLStreamReader is " + reader.getClass().getName());
            }
            return reader;
        } catch (PrivilegedActionException pae) {
            throw (XMLStreamException) pae.getException();
        }
    }

    /**
     * Create an XMLStreamReader that will operate when detached from a network.
     * The XMLStreamReader is created from a OMInputFactory that has external
     * entities disabled.  This kind of XMLStreamReader is useful for reading 
     * deployment information.
     * 
     * @param in
     * @return
     * @throws XMLStreamException
     */
    public static XMLStreamReader createNetworkDetachedXMLStreamReader(final Reader in)
    throws XMLStreamException {
        final XMLInputFactory inputFactory = getNetworkDetachedXMLInputFactory();
        try {
            XMLStreamReader reader = 
                (XMLStreamReader)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws XMLStreamException {
                        return inputFactory.createXMLStreamReader(in);
                    }
                }
                );
            if (isDebugEnabled) {
                log.debug("XMLStreamReader is " + reader.getClass().getName());
            }
            return reader;
        } catch (PrivilegedActionException pae) {
            throw (XMLStreamException) pae.getException();
        }
    }

    /**
     * Get the string representation of a given StAX event type. The returned value is the name
     * of the constant in {@link XMLStreamReader} corresponding to the event type.
     * 
     * @param event the event type as returned by {@link XMLStreamReader#getEventType()} or
     *              {@link XMLStreamReader#next()}
     * @return a string representation of the event type
     */
    public static String getEventTypeString(int event) {
        String state = null;
        switch(event) {
        case XMLStreamConstants.START_ELEMENT:
            state = "START_ELEMENT";
            break;
        case XMLStreamConstants.START_DOCUMENT:
            state = "START_DOCUMENT";
            break;
        case XMLStreamConstants.CHARACTERS:
            state = "CHARACTERS";
            break;
        case XMLStreamConstants.CDATA:
            state = "CDATA";
            break;
        case XMLStreamConstants.END_ELEMENT:
            state = "END_ELEMENT";
            break;
        case XMLStreamConstants.END_DOCUMENT:
            state = "END_DOCUMENT";
            break;
        case XMLStreamConstants.SPACE:
            state = "SPACE";
            break;
        case XMLStreamConstants.COMMENT:
            state = "COMMENT";
            break;
        case XMLStreamConstants.DTD:
            state = "DTD";
            break;
        case XMLStreamConstants.PROCESSING_INSTRUCTION:
            state = "PROCESSING_INSTRUCTION";
            break;
        case XMLStreamConstants.ENTITY_REFERENCE:
            state = "ENTITY_REFERENCE";
            break;
        default :
            state = "UNKNOWN_STATE: " + event;
        }
        return state;
    }
}
