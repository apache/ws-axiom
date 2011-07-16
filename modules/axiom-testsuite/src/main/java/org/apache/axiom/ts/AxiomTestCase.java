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
package org.apache.axiom.ts;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.commons.io.output.NullOutputStream;
import org.custommonkey.xmlunit.XMLTestCase;

public abstract class AxiomTestCase extends XMLTestCase {
    protected final OMMetaFactory metaFactory;
    private final Dictionary/*<String,String>*/ properties = new Hashtable();

    public AxiomTestCase(OMMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
        setName(getClass().getName());
    }
    
    public void addTestProperty(String name, String value) {
        setName(getName() + " [" + name + "=" + value + "]");
        properties.put(name, value);
    }
    
    public Dictionary getTestProperties() {
        return properties;
    }

    protected void assertConsumed(OMContainer container) {
        assertFalse("Expected the node to be incomplete", container.isComplete());
        boolean isConsumed;
        try {
            container.serialize(new NullOutputStream());
            isConsumed = false;
        } catch (Exception ex) {
            isConsumed = true;
        }
        assertTrue(isConsumed);
    }

    protected static int getChildrenCount(Iterator childrenIter) {
        int childCount = 0;
        while (childrenIter.hasNext()) {
            childrenIter.next();
            childCount++;
        }

        return childCount;
    }

    protected static int getNumberOfOccurrences(String xml, String pattern) {
        int index = -1;
        int count = 0;
        while ((index = xml.indexOf(pattern, index + 1)) != -1) {
            count++;
        }

        return count;
    }
}
