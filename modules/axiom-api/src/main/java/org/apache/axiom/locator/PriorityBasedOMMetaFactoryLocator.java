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
package org.apache.axiom.locator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMMetaFactoryLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class PriorityBasedOMMetaFactoryLocator implements OMMetaFactoryLocator {
    private static final Log log = LogFactory.getLog(PriorityBasedOMMetaFactoryLocator.class);
    
    private final Map/*<String,OMMetaFactory>*/ factories = new HashMap();
    
    void loadImplementations(List/*<Implementation>*/ implementations) {
        Map/*<String,Integer>*/ priorityMap = new HashMap();
        factories.clear();
        for (Iterator it = implementations.iterator(); it.hasNext(); ) {
            Implementation implementation = (Implementation)it.next();
            Feature[] features = implementation.getFeatures();
            for (int i=0; i<features.length; i++) {
                Feature feature = features[i];
                String name = feature.getName();
                int priority = feature.getPriority();
                Integer highestPriority = (Integer)priorityMap.get(name);
                if (highestPriority == null || priority > ((Integer)highestPriority).intValue()) {
                    priorityMap.put(name, Integer.valueOf(priority));
                    factories.put(name, implementation.getMetaFactory());
                }
            }
        }
        if (log.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder("Meta factories:");
            for (Iterator it = factories.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry)it.next();
                buffer.append("\n  ");
                buffer.append(entry.getKey());
                buffer.append(": ");
                buffer.append(entry.getValue().getClass().getName());
            }
            log.debug(buffer);
        }
    }
    
    public OMMetaFactory getOMMetaFactory(String feature) {
        return (OMMetaFactory)factories.get(feature);
    }
}
