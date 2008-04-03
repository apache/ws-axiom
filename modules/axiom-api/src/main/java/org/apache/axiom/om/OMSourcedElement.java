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
 * An OMSourcedElement is an OMElement whose data is backed by 
 * an arbitrary java object.  The backing java object is accessed via
 * the OMDataSource (or OMDataSourceExt) interface.
 * 
 * An OMSourcedElement can be in one of two states.
 *   Not Expanded: In this state the backing object is used to read and write the xml
 *   Expanded: In this state, the OMSourcedElement is backed by a normal OM tree.
 * 
 * Here are the steps to place an arbitrary java object into the OM tree.
 *   1) Write an OMDataSourceExt class that provides access to your java object.
 *   2) Use OMFactory.createOMElement(OMDataSource, String, OMNamespace) to create
 *      the OMSourcedElement.
 *   3) Attach the OMSourcedElement to your OMTree.
 */
public interface OMSourcedElement extends OMElement {
    
    /**
     * @return true if tree is expanded or being expanded.
     */
    public boolean isExpanded();
    
    /**
     * @return OMDataSource
     */
    public OMDataSource getDataSource();
    
    /**
     * Replace an existing OMDataSource with a new one. 
     * @param dataSource new OMDataSource
     * @return null or old OMDataSource
     */
    public OMDataSource setDataSource(OMDataSource dataSource);
    
} 
