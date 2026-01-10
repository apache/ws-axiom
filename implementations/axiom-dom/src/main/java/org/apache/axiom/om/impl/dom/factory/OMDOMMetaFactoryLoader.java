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
package org.apache.axiom.om.impl.dom.factory;

import java.util.Map;

import org.apache.axiom.locator.loader.OMMetaFactoryLoader;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.om.impl.dom.intf.factory.DOOMNodeFactory;

public class OMDOMMetaFactoryLoader implements OMMetaFactoryLoader {
    @Override
    public DOMMetaFactory load(Map<String, Object> properties) {
        try {
            return (DOOMNodeFactory)
                    OMDOMMetaFactoryLoader.class
                            .getClassLoader()
                            .loadClass("org.apache.axiom.om.impl.dom.factory.DOOMNodeFactoryImpl")
                            .getField("INSTANCE")
                            .get(null);
        } catch (ReflectiveOperationException ex) {
            throw new Error(ex);
        }
    }
}
