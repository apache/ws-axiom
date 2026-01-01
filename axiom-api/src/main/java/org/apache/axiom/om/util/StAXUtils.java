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
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.apache.axiom.util.stax.dialect.StAXDialectDetector;
import org.apache.axiom.util.stax.wrapper.ImmutableXMLInputFactory;
import org.apache.axiom.util.stax.wrapper.ImmutableXMLOutputFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

/**
 * Utility class containing StAX related methods.
 *
 * <p>This class defines a set of methods to get {@link XMLStreamReader} and {@link XMLStreamWriter}
 * instances. This class caches the corresponding factories, i.e. {@link XMLInputFactory} and {@link
 * XMLOutputFactory} objects.
 *
 * <p>Default properties for these factories can be specified using {@code
 * XMLInputFactory.properties} and {@code XMLOutputFactory.properties} files. These files are loaded
 * using the class loader that loaded the {@link StAXUtils} class. Properties with boolean, integer
 * and string values are supported. Both standard StAX properties and implementation specific
 * properties can be specified. This feature should be used with care since changing some properties
 * to non default values will break Axiom. Good candidates for {@code XMLInputFactory.properties}
 * are:
 *
 * <dl>
 *   <dt>{@code javax.xml.stream.isCoalescing}
 *   <dd>Requires the processor to coalesce adjacent character data (text nodes and CDATA sections).
 *       This property also controls whether CDATA sections are reported or not.
 *   <dt>{@code com.ctc.wstx.inputBufferLength}
 *   <dd>Size of input buffer (in chars), to use for reading XML content from input stream/reader.
 *       This property is Woodstox specific.
 *   <dt>{@code com.ctc.wstx.minTextSegment}
 *   <dd>Property to specify shortest non-complete text segment (part of CDATA section or text
 *       content) that the parser is allowed to return, if not required to coalesce text. This
 *       property is Woodstox specific.
 * </dl>
 *
 * <p>Good candidates for {@code XMLOutputFactory.properties} are:
 *
 * <dl>
 *   <dt>{@code com.ctc.wstx.outputEscapeCr}
 *   <dd>Property that determines whether Carriage Return (\r) characters are to be escaped when
 *       output or not. If enabled, all instances of of character \r are escaped using a character
 *       entity (where possible, that is, within CHARACTERS events, and attribute values). Otherwise
 *       they are output as is. The main reason to enable this property is to ensure that carriage
 *       returns are preserved as is through parsing, since otherwise they will be converted to
 *       canonical XML linefeeds (\n), when occurring along or as part of \r\n pair. This property
 *       is Woodstox specific.
 * </dl>
 */
public class StAXUtils {
    private static final Log log = LogFactory.getLog(StAXUtils.class);

    private static final Map<StAXParserConfiguration, XMLInputFactory> inputFactoryMap =
            Collections.synchronizedMap(
                    new WeakHashMap<StAXParserConfiguration, XMLInputFactory>());

    @SuppressWarnings("deprecation")
    private static final Map<StAXWriterConfiguration, XMLOutputFactory> outputFactoryMap =
            Collections.synchronizedMap(
                    new WeakHashMap<StAXWriterConfiguration, XMLOutputFactory>());

    /**
     * Get a cached {@link XMLInputFactory} instance using the default configuration.
     *
     * @return an {@link XMLInputFactory} instance.
     */
    public static XMLInputFactory getXMLInputFactory() {
        return getXMLInputFactory(null);
    }

    /**
     * Get a cached {@link XMLInputFactory} instance using the default configuration and the
     * specified cache policy.
     *
     * @param factoryPerClassLoaderPolicy the cache policy; see {@link
     *     #getXMLInputFactory(StAXParserConfiguration, boolean)} for more details
     * @return an {@link XMLInputFactory} instance.
     * @deprecated
     */
    public static XMLInputFactory getXMLInputFactory(boolean factoryPerClassLoaderPolicy) {
        return getXMLInputFactory(null, factoryPerClassLoaderPolicy);
    }

    /**
     * Get a cached {@link XMLInputFactory} instance using the specified configuration and cache
     * policy.
     *
     * @param configuration the configuration applied to the requested factory
     * @param factoryPerClassLoaderPolicy If set to <code>true</code>, the factory cached for the
     *     current class loader will be returned. If set to <code>false</code>, the singleton
     *     factory (instantiated using the class loader that loaded {@link StAXUtils}) will be
     *     returned.
     * @return an {@link XMLInputFactory} instance.
     * @deprecated
     */
    public static XMLInputFactory getXMLInputFactory(
            StAXParserConfiguration configuration, boolean factoryPerClassLoaderPolicy) {

        if (factoryPerClassLoaderPolicy) {
            throw new UnsupportedOperationException();
        } else {
            return getXMLInputFactory(configuration);
        }
    }

    public static XMLStreamReader createXMLStreamReader(InputStream in, String encoding)
            throws XMLStreamException {

        return createXMLStreamReader(null, in, encoding);
    }

    public static XMLStreamReader createXMLStreamReader(
            StAXParserConfiguration configuration, InputStream in, String encoding)
            throws XMLStreamException {

        XMLStreamReader reader =
                getXMLInputFactory(configuration).createXMLStreamReader(in, encoding);
        if (log.isDebugEnabled()) {
            log.debug("XMLStreamReader is " + reader.getClass().getName());
        }
        return reader;
    }

    public static XMLStreamReader createXMLStreamReader(InputStream in) throws XMLStreamException {

        return createXMLStreamReader(null, in);
    }

    public static XMLStreamReader createXMLStreamReader(
            StAXParserConfiguration configuration, InputStream in) throws XMLStreamException {

        XMLStreamReader reader = getXMLInputFactory(configuration).createXMLStreamReader(in);
        if (log.isDebugEnabled()) {
            log.debug("XMLStreamReader is " + reader.getClass().getName());
        }
        return reader;
    }

    public static XMLStreamReader createXMLStreamReader(
            StAXParserConfiguration configuration, String systemId, InputStream in)
            throws XMLStreamException {

        XMLStreamReader reader =
                getXMLInputFactory(configuration).createXMLStreamReader(systemId, in);
        if (log.isDebugEnabled()) {
            log.debug("XMLStreamReader is " + reader.getClass().getName());
        }
        return reader;
    }

    public static XMLStreamReader createXMLStreamReader(Reader in) throws XMLStreamException {

        return createXMLStreamReader(null, in);
    }

    public static XMLStreamReader createXMLStreamReader(
            StAXParserConfiguration configuration, Reader in) throws XMLStreamException {

        XMLStreamReader reader = getXMLInputFactory(configuration).createXMLStreamReader(in);
        if (log.isDebugEnabled()) {
            log.debug("XMLStreamReader is " + reader.getClass().getName());
        }
        return reader;
    }

    /**
     * Get a cached {@link XMLOutputFactory} instance using the default configuration.
     *
     * @return an {@link XMLOutputFactory} instance.
     * @deprecated
     */
    public static XMLOutputFactory getXMLOutputFactory() {
        return getXMLOutputFactory(null);
    }

    /**
     * Get a cached {@link XMLOutputFactory} instance using the default configuration and the
     * specified cache policy.
     *
     * @param factoryPerClassLoaderPolicy the cache policy; see {@link
     *     #getXMLOutputFactory(StAXWriterConfiguration, boolean)} for more details
     * @return an {@link XMLOutputFactory} instance.
     * @deprecated
     */
    public static XMLOutputFactory getXMLOutputFactory(boolean factoryPerClassLoaderPolicy) {
        return getXMLOutputFactory(null, factoryPerClassLoaderPolicy);
    }

    /**
     * Get a cached {@link XMLOutputFactory} instance using the specified configuration and cache
     * policy.
     *
     * @param configuration the configuration applied to the requested factory
     * @param factoryPerClassLoaderPolicy If set to <code>true</code>, the factory cached for the
     *     current class loader will be returned. If set to <code>false</code>, the singleton
     *     factory (instantiated using the class loader that loaded {@link StAXUtils}) will be
     *     returned.
     * @return an {@link XMLOutputFactory} instance.
     * @deprecated
     */
    public static XMLOutputFactory getXMLOutputFactory(
            StAXWriterConfiguration configuration, boolean factoryPerClassLoaderPolicy) {

        if (factoryPerClassLoaderPolicy) {
            throw new UnsupportedOperationException();
        } else {
            return getXMLOutputFactory(configuration);
        }
    }

    /**
     * @deprecated Per class loader factories are no longer supported. The code now always uses the
     *     class loader that loaded the {@link StAXUtils} class.
     */
    public static void setFactoryPerClassLoader(boolean value) {
        if (value) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @deprecated
     */
    public static XMLStreamWriter createXMLStreamWriter(OutputStream out)
            throws XMLStreamException {

        return createXMLStreamWriter(null, out);
    }

    /**
     * @deprecated
     */
    public static XMLStreamWriter createXMLStreamWriter(
            StAXWriterConfiguration configuration, OutputStream out) throws XMLStreamException {
        XMLStreamWriter writer =
                getXMLOutputFactory(configuration).createXMLStreamWriter(out, "utf-8");
        if (log.isDebugEnabled()) {
            log.debug("XMLStreamWriter is " + writer.getClass().getName());
        }
        return writer;
    }

    /**
     * @deprecated
     */
    public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding)
            throws XMLStreamException {

        return createXMLStreamWriter(null, out, encoding);
    }

    /**
     * @deprecated
     */
    public static XMLStreamWriter createXMLStreamWriter(
            StAXWriterConfiguration configuration, OutputStream out, String encoding)
            throws XMLStreamException {
        XMLStreamWriter writer =
                getXMLOutputFactory(configuration).createXMLStreamWriter(out, encoding);
        if (log.isDebugEnabled()) {
            log.debug("XMLStreamWriter is " + writer.getClass().getName());
        }
        return writer;
    }

    /**
     * @deprecated
     */
    public static XMLStreamWriter createXMLStreamWriter(final Writer out)
            throws XMLStreamException {

        return createXMLStreamWriter(null, out);
    }

    /**
     * @deprecated
     */
    public static XMLStreamWriter createXMLStreamWriter(
            StAXWriterConfiguration configuration, Writer out) throws XMLStreamException {
        XMLStreamWriter writer = getXMLOutputFactory(configuration).createXMLStreamWriter(out);
        if (log.isDebugEnabled()) {
            log.debug("XMLStreamWriter is " + writer.getClass().getName());
        }
        return writer;
    }

    /**
     * Load factory properties from a resource. The method converts boolean and integer values to
     * the right Java types. All other values are returned as strings.
     *
     * @param cl
     * @param name
     * @return the factory properties
     */
    // This has package access since it is used from within anonymous inner classes
    static Map<String, Object> loadFactoryProperties(ClassLoader cl, String name) {
        InputStream in = cl.getResourceAsStream(name);
        if (in == null) {
            return null;
        } else {
            try {
                Properties rawProps = new Properties();
                Map<String, Object> props = new HashMap<String, Object>();
                rawProps.load(in);
                for (Map.Entry<Object, Object> entry : rawProps.entrySet()) {
                    String strValue = (String) entry.getValue();
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
                    props.put((String) entry.getKey(), value);
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

    private static XMLInputFactory newXMLInputFactory(
            final ClassLoader classLoader, final StAXParserConfiguration configuration) {
        ClassLoader savedClassLoader;
        if (classLoader == null) {
            savedClassLoader = null;
        } else {
            savedClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            // Woodstox 3.x by default creates coalescing parsers. Even if this violates the StAX
            // specs (see WSTX-140), for compatibility with Woodstox 3.x, we always enable
            // coalescing mode. Note that we need to do that before loading
            // XMLInputFactory.properties so that this setting can be overridden.
            factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            Map<String, Object> props =
                    loadFactoryProperties(classLoader, "XMLInputFactory.properties");
            if (props != null) {
                for (Map.Entry<String, Object> entry : props.entrySet()) {
                    factory.setProperty(entry.getKey(), entry.getValue());
                }
            }
            StAXDialect dialect = StAXDialectDetector.getDialect(factory);
            if (configuration != null) {
                factory = configuration.configure(factory, dialect);
            }
            return new ImmutableXMLInputFactory(dialect.normalize(dialect.makeThreadSafe(factory)));
        } finally {
            if (savedClassLoader != null) {
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        }
    }

    /**
     * Get a cached {@link XMLInputFactory} instance using the specified configuration.
     *
     * @param configuration the configuration applied to the requested factory
     * @return an {@link XMLInputFactory} instance.
     */
    public static XMLInputFactory getXMLInputFactory(StAXParserConfiguration configuration) {
        if (configuration == null) {
            configuration = StAXParserConfiguration.DEFAULT;
        }
        XMLInputFactory f = inputFactoryMap.get(configuration);
        if (f == null) {
            f = newXMLInputFactory(StAXUtils.class.getClassLoader(), configuration);
            inputFactoryMap.put(configuration, f);
            if (log.isDebugEnabled()) {
                if (f != null) {
                    log.debug(
                            "Created singleton XMLInputFactory "
                                    + f.getClass()
                                    + " with configuration "
                                    + configuration);
                }
            }
        }

        return f;
    }

    /**
     * @deprecated
     */
    private static XMLOutputFactory newXMLOutputFactory(
            final ClassLoader classLoader, final StAXWriterConfiguration configuration) {
        ClassLoader savedClassLoader;
        if (classLoader == null) {
            savedClassLoader = null;
        } else {
            savedClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
            Map<String, Object> props =
                    loadFactoryProperties(classLoader, "XMLOutputFactory.properties");
            if (props != null) {
                for (Map.Entry<String, Object> entry : props.entrySet()) {
                    factory.setProperty(entry.getKey(), entry.getValue());
                }
            }
            StAXDialect dialect = StAXDialectDetector.getDialect(factory);
            if (configuration != null) {
                factory = configuration.configure(factory, dialect);
            }
            return new ImmutableXMLOutputFactory(
                    dialect.normalize(dialect.makeThreadSafe(factory)));
        } finally {
            if (savedClassLoader != null) {
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        }
    }

    /**
     * Get a cached {@link XMLOutputFactory} instance using the specified configuration.
     *
     * @param configuration the configuration applied to the requested factory
     * @return an {@link XMLOutputFactory} instance.
     * @deprecated
     */
    public static XMLOutputFactory getXMLOutputFactory(StAXWriterConfiguration configuration) {
        if (configuration == null) {
            configuration = StAXWriterConfiguration.DEFAULT;
        }
        XMLOutputFactory f = outputFactoryMap.get(configuration);
        if (f == null) {
            f = newXMLOutputFactory(StAXUtils.class.getClassLoader(), configuration);
            outputFactoryMap.put(configuration, f);
            if (log.isDebugEnabled()) {
                if (f != null) {
                    log.debug(
                            "Created singleton XMLOutputFactory "
                                    + f.getClass()
                                    + " with configuration "
                                    + configuration);
                }
            }
        }
        return f;
    }
}
