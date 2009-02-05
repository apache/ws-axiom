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

package org.apache.axiom.om.impl.llom;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMElementTestBase;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;

public class OMElementTest extends OMElementTestBase {
    private static final String AXIS2_NS_URI = "http://ws.apache.org/axis2";
    private static final String AXIS2_NS_PREFIX = "axis2";
    private static final String SOME_TEXT = "Some Text";
    
    protected OMFactory getOMFactory() {
        return new OMLinkedListImplFactory();
    }

    public void testTextQNames() {
        OMFactory factory = getOMFactory();
        OMElement omElement = factory.createOMElement("TestElement", null);
        omElement.setText(new QName(AXIS2_NS_URI, SOME_TEXT, AXIS2_NS_PREFIX));

        Iterator allDeclaredNamespaces = omElement.getAllDeclaredNamespaces();
        boolean foundNamespace = false;
        while (allDeclaredNamespaces.hasNext()) {
            OMNamespace omNamespace = (OMNamespace) allDeclaredNamespaces.next();
            if (AXIS2_NS_URI.equals(omNamespace.getNamespaceURI()) &&
                    AXIS2_NS_PREFIX.equals(omNamespace.getPrefix())) {
                foundNamespace = true;
            }
        }
        assertTrue("Namespace of the text is not defined in the parent element", foundNamespace);

        String elementString = omElement.toString();
        assertTrue(elementString.indexOf(AXIS2_NS_PREFIX + ":" + SOME_TEXT) > -1);
        assertTrue((AXIS2_NS_PREFIX + ":" + SOME_TEXT).equals(omElement.getText()));

        QName textAsQName = omElement.getTextAsQName();
        assertTrue(textAsQName.equals(new QName(AXIS2_NS_URI, SOME_TEXT, AXIS2_NS_PREFIX)));
    }

    public void testTextQNamesWithoutQNames() {
        OMFactory factory = getOMFactory();
        OMElement omElement = factory.createOMElement("TestElement", null);
        omElement.setText(SOME_TEXT);

        String elementString = omElement.toString();
        assertTrue(elementString.indexOf(":" + SOME_TEXT) == -1);

        QName textAsQName = omElement.getTextAsQName();
        assertTrue(textAsQName.equals(new QName(SOME_TEXT)));
    }
}
