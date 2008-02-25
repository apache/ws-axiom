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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;


public class StAXUtils {
    private static Log log = LogFactory.getLog(StAXUtils.class);
    private static boolean isDebugEnabled = log.isDebugEnabled();
    private static XMLInputFactory inputFactory = null;
    private static XMLOutputFactory outputFactory = null;

    /**
     * Gets an XMLInputFactory instance from pool.
     *
     * @return an XMLInputFactory instance.
     */
    public static XMLInputFactory getXMLInputFactory() {
        if (inputFactory == null) {
            inputFactory = (XMLInputFactory) AccessController.doPrivileged(
                    new PrivilegedAction() {
                        public Object run() {
                            Thread currentThread = Thread.currentThread();
                            ClassLoader savedClassLoader = currentThread.getContextClassLoader();
                            XMLInputFactory factory = null;
                            try {
                                currentThread.setContextClassLoader(StAXUtils.class.getClassLoader());
                                factory = XMLInputFactory.newInstance();
                            }
                            finally {
                                currentThread.setContextClassLoader(savedClassLoader);
                            }
                            return factory;
                        }
                    });
        }
        return inputFactory;
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
        } finally {
            releaseXMLInputFactory(inputFactory);
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
        } finally {
            releaseXMLInputFactory(inputFactory);
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
        } finally {
            releaseXMLInputFactory(inputFactory);
        }
    }

    /**
     * Gets an XMLOutputFactory instance from pool.
     *
     * @return an XMLOutputFactory instance.
     */
    public static XMLOutputFactory getXMLOutputFactory() {
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
                                factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
                            }
                            finally {
                                currentThread.setContextClassLoader(savedClassLoader);
                            }
                            return factory;
                        }
                    });
        }
        return outputFactory;
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
}
