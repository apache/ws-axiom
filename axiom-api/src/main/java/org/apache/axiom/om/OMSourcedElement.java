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

/**
 * Element whose data is backed by an arbitrary Java object. The backing Java object is accessed via
 * the {@link OMDataSource} (or {@link OMDataSourceExt}) interface.
 *
 * <p>An OMSourcedElement can be in one of two states:
 *
 * <dl>
 *   <dt>Not Expanded
 *   <dd>In this state the backing object is used to read and write the XML.
 *   <dt>Expanded
 *   <dd>In this state, the OMSourcedElement is backed by a normal OM tree.
 * </dl>
 *
 * <p>Here are the steps to place an arbitrary java object into the OM tree:
 *
 * <ol>
 *   <li>Write an {@link OMDataSourceExt} implementation that provides access to your Java object.
 *   <li>Use {@link OMFactory#createOMElement(OMDataSource, String, OMNamespace)} to create the
 *       OMSourcedElement.
 *   <li>Attach the OMSourcedElement to the tree.
 * </ol>
 */
public interface OMSourcedElement extends OMElement {

    /**
     * @return true if tree is expanded or being expanded.
     */
    boolean isExpanded();

    /**
     * @return OMDataSource
     */
    OMDataSource getDataSource();

    /**
     * @deprecated This method is deprecated because its semantics are not clearly defined. In
     *     particular it is unspecified whether the name of the element may change as a result of an
     *     invocation of this method.
     */
    OMDataSource setDataSource(OMDataSource dataSource);

    /**
     * Get the object that backs the data source set on this element. This method provides a safe
     * way to access that object. It will return a non null value if all of the following conditions
     * are satisfied:
     *
     * <ol>
     *   <li>The element is configured with an {@link OMDataSource} of the type specified by the
     *       <code>dataSourceClass</code> parameter (in the sense of {@link
     *       Class#isInstance(Object)}).
     *   <li>The {@link OMDataSourceExt#getObject()} method returns a non null value on the
     *       configured data source.
     *   <li>The instance can ensure that the content of the element has not been modified. This is
     *       always the case if the element has not been expanded (i.e. if {@link #isExpanded()}
     *       returns <code>false</code>), but the implementation may use additional mechanisms to
     *       detect changes after expansion of the element.
     * </ol>
     *
     * These are exactly the conditions that must be satisfied if the application code wants to
     * implement optimized processing of the sourced element by accessing the backing object.
     *
     * @param dataSourceClass the expected data source class; must be assignment compatible with
     *     {@link OMDataSourceExt}
     * @return the backing Java object or <code>null</code> if the conditions specified above are
     *     not satisfied
     */
    Object getObject(Class<? extends OMDataSourceExt> dataSourceClass);
}
