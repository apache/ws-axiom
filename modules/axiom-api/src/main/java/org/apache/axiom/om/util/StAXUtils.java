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
import java.util.ArrayList;
import java.util.List;


public class StAXUtils {

    private static interface ObjectCreator {
        Object newObject();
    }

    private static class Pool {
        private final int MAX_POOL_SIZE = 100;
        private final List objects = new ArrayList();
        private final ObjectCreator objectCreator;

        Pool(ObjectCreator[] creators) {
            ObjectCreator oc = null;
            for (int i = 0; i < creators.length; i++) {
                try {
                    creators[i].newObject();
                    oc = creators[i];
                    break;
                } catch (Throwable t) {
                    // Ignore me
                }
            }
            if (oc == null) {
                throw new IllegalStateException("No valid ObjectCreator found.");
            }
            objectCreator = oc;
        }

        synchronized Object getInstance() {
            final int size = objects.size();
            if (size > 0) {
                return objects.remove(size - 1);
            }
            return objectCreator.newObject();
        }

        synchronized void releaseInstance(Object object) {
            if (objects.size() < MAX_POOL_SIZE) {
                objects.add(object);
            }
        }

        synchronized void clear() {
            objects.clear();
        }
    }

    private static final Pool xmlInputFactoryPool = new Pool(new ObjectCreator[] {
            new ObjectCreator() {
                public Object newObject() {
                    return AccessController.doPrivileged(
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
            },
            new ObjectCreator() {
                public Object newObject() {
                    return XMLInputFactory.newInstance();
                }
            }
    });

    private static final Pool xmlOutputFactoryPool = new Pool(new ObjectCreator[] {
            new ObjectCreator() {
                public Object newObject() {
                    return AccessController.doPrivileged(
                            new PrivilegedAction() {
                                public Object run() {
                                                                       
                                    Thread currentThread = Thread.currentThread();
                                    ClassLoader savedClassLoader = currentThread.getContextClassLoader();
                                    XMLOutputFactory factory = null;
                                    try {
                                        currentThread.setContextClassLoader(StAXUtils.class.getClassLoader());
                                        factory = XMLOutputFactory.newInstance();
                                    }
                                    finally {
                                        currentThread.setContextClassLoader(savedClassLoader);
                                    }
                                    return factory;
                                }
                            });
                }
            },
            new ObjectCreator() {
                public Object newObject() {
                    return XMLOutputFactory.newInstance();
                }
            }
    });


    private static Log log = LogFactory.getLog(StAXUtils.class);
    private static boolean isDebugEnabled = log.isDebugEnabled();


    /**
     * Gets an XMLInputFactory instance from pool.
     *
     * @return an XMLInputFactory instance.
     */
    public static XMLInputFactory getXMLInputFactory() {
        return (XMLInputFactory) xmlInputFactoryPool.getInstance();
    }

    /**
     * Returns an XMLInputFactory instance for reuse.
     *
     * @param factory An XMLInputFactory instance that is available for reuse
     */
    public static void releaseXMLInputFactory(XMLInputFactory factory) {
        xmlInputFactoryPool.releaseInstance(factory);
    }

    public static XMLStreamReader createXMLStreamReader(InputStream in, String encoding)
            throws XMLStreamException {
        XMLInputFactory inputFactory = getXMLInputFactory();
        try {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(in, encoding);
            if (isDebugEnabled) {
                log.debug("XMLStreamReader is " + reader.getClass().getName());
            }
            return reader;
        } finally {
            releaseXMLInputFactory(inputFactory);
        }
    }

    public static XMLStreamReader createXMLStreamReader(InputStream in)
            throws XMLStreamException {
        XMLInputFactory inputFactory = getXMLInputFactory();
        try {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
            if (isDebugEnabled) {
                log.debug("XMLStreamReader is " + reader.getClass().getName());
            }
            return reader;
        } finally {
            releaseXMLInputFactory(inputFactory);
        }
    }

    public static XMLStreamReader createXMLStreamReader(Reader in)
            throws XMLStreamException {
        XMLInputFactory inputFactory = getXMLInputFactory();
        try {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
            if (isDebugEnabled) {
                log.debug("XMLStreamReader is " + reader.getClass().getName());
            }
            return reader;
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
        return (XMLOutputFactory) xmlOutputFactoryPool.getInstance();
    }

    /**
     * Returns an XMLOutputFactory instance for reuse.
     *
     * @param factory An XMLOutputFactory instance that is available for reuse.
     */
    public static void releaseXMLOutputFactory(XMLOutputFactory factory) {
        xmlOutputFactoryPool.releaseInstance(factory);
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out)
            throws XMLStreamException {
        XMLOutputFactory outputFactory = getXMLOutputFactory();
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
            if (isDebugEnabled) {
                log.debug("XMLStreamWriter is " + writer.getClass().getName());
            }
            return writer;
        } finally {
            releaseXMLOutputFactory(outputFactory);
        }
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding)
            throws XMLStreamException {
        XMLOutputFactory outputFactory = getXMLOutputFactory();
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out, encoding);
            if (isDebugEnabled) {
                log.debug("XMLStreamWriter is " + writer.getClass().getName());
            }
            return writer;
        } finally {
            releaseXMLOutputFactory(outputFactory);
        }
    }

    public static XMLStreamWriter createXMLStreamWriter(Writer out)
            throws XMLStreamException {
        XMLOutputFactory outputFactory = getXMLOutputFactory();
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
            if (isDebugEnabled) {
                log.debug("XMLStreamWriter is " + writer.getClass().getName());
            }
            return writer;
        } finally {
            releaseXMLOutputFactory(outputFactory);
        }
    }

    public static void reset() {
        xmlOutputFactoryPool.clear();
        xmlInputFactoryPool.clear();
    }
}
