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
package org.apache.axiom.om.dom;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;

/**
 * Extension interface for {@link OMMetaFactory} implementations that support
 * {@link OMAbstractFactory#FEATURE_DOM}.
 */
public interface DOMMetaFactory extends OMMetaFactory {
    /**
     * Create a new {@link DocumentBuilderFactory}. Since Axiom doesn't support non namespace aware
     * processing, the returned factory is always configured with <code>namespaceAware</code> set to
     * <code>true</code> (in contrast to the default settings used by
     * {@link DocumentBuilderFactory#newInstance()}).
     * 
     * @return the factory instance
     */
    DocumentBuilderFactory newDocumentBuilderFactory();
}
