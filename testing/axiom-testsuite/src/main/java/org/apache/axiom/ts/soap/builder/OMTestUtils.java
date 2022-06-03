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

package org.apache.axiom.ts.soap.builder;

import java.util.Iterator;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.junit.Assert;

public class OMTestUtils {
    public static void walkThrough(OMElement element) {
        Assert.assertFalse(
                "Expected " + element.getQName() + " to be incomplete", element.isComplete());
        for (Iterator<OMAttribute> it = element.getAllAttributes(); it.hasNext(); ) {
            Assert.assertNotNull(it.next());
        }
        OMNode child = element.getFirstOMChild();
        while (child != null) {
            if (child instanceof OMElement) {
                walkThrough((OMElement) child);
            }
            child = child.getNextOMSibling();
        }
        Assert.assertTrue(element.isComplete());
    }
}
