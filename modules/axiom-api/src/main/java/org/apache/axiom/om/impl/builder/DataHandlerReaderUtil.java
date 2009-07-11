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

package org.apache.axiom.om.impl.builder;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMConstants;

/**
 * Utility class to work with the {@link XMLStreamReader} extension defined by
 * {@link DataHandlerReader}. In addition to {@link DataHandlerReader} support, this class also
 * provides support for the legacy extension mechanism described below.
 * 
 * <h3>Legacy XMLStreamReader extensions for optimized base64 handling</h3>
 * 
 * <p>
 * {@link XMLStreamReader} instances supporting the legacy extension must conform to the following
 * requirements:
 * </p>
 * <ol>
 * <li>{@link XMLStreamReader#getProperty(String)} must return {@link Boolean#TRUE} for the
 * property identified by {@link org.apache.axiom.om.OMConstants#IS_DATA_HANDLERS_AWARE},
 * regardless of the current event. The property is assumed to be immutable and its value must not
 * change during the lifetime of the {@link XMLStreamReader} implementation.</li>
 * <li>
 * <p>
 * If the {@link XMLStreamReader} wishes to expose base64 encoded content using a
 * {@link javax.activation.DataHandler} object, it must do so using a single
 * {@link XMLStreamConstants#CHARACTERS} event.
 * </p>
 * <p>
 * To maintain compatibility with consumers that are unaware of the extensions described here, the
 * implementation should make sure that {@link XMLStreamReader#getText()},
 * {@link XMLStreamReader#getTextStart()}, {@link XMLStreamReader#getTextLength()},
 * {@link XMLStreamReader#getTextCharacters()},
 * {@link XMLStreamReader#getTextCharacters(int, char[], int, int)} and
 * {@link XMLStreamReader#getElementText()} behave as expected for this type of event, i.e. return
 * the base64 representation of the binary content.
 * </p>
 * </li>
 * <li>{@link XMLStreamReader#getProperty(String)} must return {@link Boolean#TRUE} for the
 * property identified by {@link org.apache.axiom.om.OMConstants#IS_BINARY} if the current event is
 * a {@link XMLStreamConstants#CHARACTERS} event representing base64 encoded binary content and for
 * which a {@link javax.activation.DataHandler} is available. For all other events, the returned
 * value must be {@link Boolean#FALSE}.</li>
 * <li>
 * <p>
 * If for a given event, the implementation returned {@link Boolean#TRUE} for the
 * {@link org.apache.axiom.om.OMConstants#IS_BINARY} property, then a call to
 * {@link XMLStreamReader#getProperty(String)} with argument
 * {@link org.apache.axiom.om.OMConstants#DATA_HANDLER} must return the corresponding
 * {@link javax.activation.DataHandler} object.
 * </p>
 * <p>
 * The {@link org.apache.axiom.om.OMConstants#DATA_HANDLER} property is undefined for any other type
 * of event. This implies that the consumer of the {@link XMLStreamReader} must check the
 * {@link org.apache.axiom.om.OMConstants#IS_BINARY} property before retrieving the
 * {@link org.apache.axiom.om.OMConstants#DATA_HANDLER} property.
 * </p>
 * </li>
 * </ol>
 * The extension mechanism described here has been deprecated mainly because it doesn't support
 * deferred loading of the binary content.
 */
public class DataHandlerReaderUtil {
    private DataHandlerReaderUtil() {}
    
    /**
     * Get the {@link DataHandlerReader} extension for a given {@link XMLStreamReader}, if
     * available. If the {@link XMLStreamReader} only supports the legacy extension (as described
     * above), then this method will return a compatibility wrapper. Note that this wrapper doesn't
     * support deferred loading of the binary content.
     * 
     * @param reader
     *            the stream reader to get the {@link DataHandlerReader} extension from
     * @return the implementation of the extension, or <code>null</code> if the
     *         {@link XMLStreamReader} doesn't expose base64 encoded binary content as
     *         {@link DataHandler} objects.
     */
    public static DataHandlerReader getDataHandlerReader(final XMLStreamReader reader) {
        try {
            DataHandlerReader dhr = (DataHandlerReader)reader.getProperty(
                    DataHandlerReader.PROPERTY);
            if (dhr != null) {
                return dhr;
            }
        } catch (IllegalArgumentException ex) {
            // Just continue
        }
        Boolean isDataHandlerAware;
        try {
            isDataHandlerAware = (Boolean)reader.getProperty(OMConstants.IS_DATA_HANDLERS_AWARE);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        if (isDataHandlerAware != null && isDataHandlerAware.booleanValue()) {
            return new DataHandlerReader() {
                public boolean isBinary() {
                    return ((Boolean)reader.getProperty(OMConstants.IS_BINARY)).booleanValue();
                }

                public boolean isDeferred() {
                    return false;
                }

                public String getContentID() {
                    return null;
                }

                public DataHandler getDataHandler() {
                    return (DataHandler)reader.getProperty(OMConstants.DATA_HANDLER);
                }

                public DataHandlerProvider getDataHandlerProvider() {
                    throw new UnsupportedOperationException();
                }
            };
        } else {
            return null;
        }
    }
    
    /**
     * Helper method to implement {@link XMLStreamReader#getProperty(String)}. This method
     * processed the properties defined by {@link DataHandlerReader#PROPERTY} and the legacy
     * extension mechanism (as described above). It can therefore be used to make a
     * {@link XMLStreamReader} implementation compatible with code that expects it to implement this
     * legacy extension.
     * 
     * @param extension
     *            the reference to the {@link DataHandlerReader} extension for the
     *            {@link XMLStreamReader} implementation
     * @param propertyName
     *            the name of the property, as passed to the
     *            {@link XMLStreamReader#getProperty(String)} method
     * @return the property value as specified by the {@link DataHandlerReader} or legacy extension;
     *         <code>null</code> if the property is not specified by any of these two extensions
     */
    public static Object processGetProperty(DataHandlerReader extension, String propertyName) {
        if (extension == null || propertyName == null) {
            throw new IllegalArgumentException();
        } else if (propertyName.equals(DataHandlerReader.PROPERTY)) {
            return extension;
        } else if (propertyName.equals(OMConstants.IS_DATA_HANDLERS_AWARE)) {
            return Boolean.TRUE;
        } else if (propertyName.equals(OMConstants.IS_BINARY)) {
            return Boolean.valueOf(extension.isBinary());
        } else if (propertyName.equals(OMConstants.DATA_HANDLER)) {
            return extension.getDataHandler();
        } else {
            return null;
        }
    }
}
