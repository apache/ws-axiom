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
package org.apache.axiom.om;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Interface to a backing object that can can be read and written as XML.
 * 
 * <p>To plug an arbitrary object into an OM tree.  Follow these steps
 * <ol>
 *   <li>Provide a class that implements {@code OMDataSourceExt}.
 *   <li>Use {@link OMFactory#createOMElement(OMDataSource, String, OMNamespace)} to build an
 *       {@link OMSourcedElement}.
 *   <li>Add the {@link OMSourcedElement} to the OM tree.
 * </ol>
 * 
 * <p>{@code OMDataSourceExt} provides additional methods that are not available on the
 * original {@link OMDataSource}.
 * 
 * @see OMDataSource
 * @see OMSourcedElement
 */
public interface OMDataSourceExt extends OMDataSource {
    /**
     * Get the object that backs this data source. Application code should in general not call this
     * method directly, but use {@link OMSourcedElement#getObject(Class)} instead.
     * <p>
     * Data sources that support non destructive read/write should return the object from which the
     * XML is produced. Data sources with destructive read/write should return a non null value only
     * if the backing object has not been consumed yet (even partially).
     * 
     * @return the backing object, or <code>null</code> if the data source has no backing object or
     *         if the backing object can't be accessed in a safe way
     */
    Object getObject();
    
    /**
     * Returns true if reading the backing object is destructive.
     * An example of an object with a destructive read is an InputSteam.
     * The owning OMSourcedElement uses this information to detemine if OM tree
     * expansion is needed when reading the OMDataSourceExt.
     * @return boolean
     */
    boolean isDestructiveRead();
    
    /**
     * Returns true if writing the backing object is destructive.
     * An example of an object with a destructive write is an InputStream.
     * The owning OMSourcedElement uses this information to detemine if OM tree
     * expansion is needed when writing the OMDataSourceExt.
     * @return boolean
     */
    boolean isDestructiveWrite();
    
    /**
     * Returns a InputStream representing the xml data
     * @param encoding String encoding of InputStream
     * @return InputStream
     * 
     * @deprecated
     */
    InputStream getXMLInputStream(String encoding) throws UnsupportedEncodingException;
    
    /**
     * Returns a byte[] representing the xml data
     * @param encoding String encoding of InputStream
     * @return byte[]
     * @see #getXMLInputStream(String)
     * 
     * @deprecated
     */
    byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException;
    
    /**
     * Close the DataSource and free its resources.
     */
    void close();
    
    /**
     * Create a copy of the data source. This method is used by
     * {@link OMInformationItem#clone(OMCloneOptions)} when the
     * {@link OMCloneOptions#isCopyOMDataSources()} option is enabled. If the data source is
     * immutable and stateless, then it may return a reference to itself instead of creating a new
     * data source instance.
     * 
     * @return the copy of the data source, or <code>null</code> if the data source can't be copied
     *         (e.g. because it is destructive)
     */
    OMDataSourceExt copy();
    
    /**
     * Check if a property with the given name is set on this instance.
     * 
     * @param name
     *            the property name
     * @return {@code true} if the property is set
     */
    boolean hasProperty(String name);
    
    /**
     * Get the value of a property stored on this instance.
     * 
     * @param name
     *            the property name
     * @return the property value or {@code null}
     */
    Object getProperty(String name);
    
    /**
     * Set a property on this instance.
     * 
     * @param name
     *            the property name
     * @param value
     *            the property value
     * @return the old property value or {@code null}
     */
    Object setProperty(String name, Object value);
}
