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

import junit.framework.TestCase;

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;

public class OMTestUtils {
    public static void walkThrough(OMElement omEle) {
        Iterator attibIt = omEle.getAllAttributes();
        if (attibIt != null) {
            while (attibIt.hasNext()) {
                TestCase.assertNotNull("once the has next is not null, the " +
                        "element should not be null",
                                       attibIt.next());
            }
        }
        Iterator it = omEle.getChildren();
        if (it != null) {
            while (it.hasNext()) {
                OMNode ele = (OMNode) it.next();
                TestCase.assertNotNull("once the has next is not null, the " +
                        "element should not be null", ele);
                if (ele instanceof OMElement) {
                    walkThrough((OMElement) ele);
                }
            }
        }
    }
}
