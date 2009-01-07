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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


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
    
    // This has package access since it is used from within anonymous inner classes
    static XMLInputFactory newXMLInputFactory(boolean isNetworkDetached) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
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
        return factory;
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
                    factory = (XMLInputFactory) 
                    AccessController.doPrivileged(
                        new PrivilegedAction() {
                            public Object run() {
                                return newXMLInputFactory(isNetworkDetached);
                            }
                        });
                } catch (ClassCastException cce) {
                    if (log.isDebugEnabled()) {
                        log.debug("Failed creation of XMLInputFactory implementation with " +
                                        "classloader=" + cl);
                        log.debug("Exception is=" + cce);
                        log.debug("Attempting with classloader: " + 
                                  XMLInputFactory.class.getClassLoader());
                    }
                    factory = (XMLInputFactory) 
                    AccessController.doPrivileged(
                        new PrivilegedAction() {
                            public Object run() {
                                ClassLoader saveCL = getContextClassLoader();
                                try {                              
                                    Thread.currentThread().
                                        setContextClassLoader(
                                            XMLInputFactory.class.getClassLoader());
                                    return newXMLInputFactory(isNetworkDetached);
                                } finally {
                                    Thread.currentThread().
                                        setContextClassLoader(saveCL);
                                }
                            }
                        });
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
            f = (XMLInputFactory) AccessController.doPrivileged(
                    new PrivilegedAction() {
                        public Object run() {
                            Thread currentThread = Thread.currentThread();
                            ClassLoader savedClassLoader = currentThread.getContextClassLoader();
                            try {
                                currentThread.setContextClassLoader(StAXUtils.class.getClassLoader());
                                return newXMLInputFactory(isNetworkDetached);
                            }
                            finally {
                                currentThread.setContextClassLoader(savedClassLoader);
                            }
                        }
                    });
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
                    factory = (XMLOutputFactory) 
                    AccessController.doPrivileged(
                        new PrivilegedAction() {
                            public Object run() {
                                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                                factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, 
                                                    Boolean.FALSE);
                                return factory;
                            }
                        });
                } catch (ClassCastException cce) {
                    if (log.isDebugEnabled()) {
                        log.debug("Failed creation of XMLOutputFactory implementation with " +
                                        "classloader=" + cl);
                        log.debug("Exception is=" + cce);
                        log.debug("Attempting with classloader: " + 
                                  XMLOutputFactory.class.getClassLoader());
                    }
                    factory = (XMLOutputFactory) 
                    AccessController.doPrivileged(
                        new PrivilegedAction() {
                            public Object run() {
                                XMLOutputFactory f = null;
                                ClassLoader saveCL = getContextClassLoader();
                                try {                              
                                    Thread.currentThread().
                                        setContextClassLoader(
                                           XMLOutputFactory.class.getClassLoader());
                                    f =XMLOutputFactory.newInstance();
                                    f.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, 
                                                        Boolean.FALSE);
                                } finally {
                                    Thread.currentThread().
                                        setContextClassLoader(saveCL);
                                }
                                return f;
                            }
                        });
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
            outputFactory = (XMLOutputFactory) AccessController.doPrivileged(
                    new PrivilegedAction() {
                        public Object run() {

                            Thread currentThread = Thread.currentThread();
                            ClassLoader savedClassLoader = currentThread.getContextClassLoader();
                            XMLOutputFactory factory = null;
                            try {
                                currentThread.setContextClassLoader(StAXUtils.class.getClassLoader());
                                factory = XMLOutputFactory.newInstance();
                                factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, 
                                                    Boolean.FALSE);
                            }
                            finally {
                                currentThread.setContextClassLoader(savedClassLoader);
                            }
                            return factory;
                        }
                    });
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

}
