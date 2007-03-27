package org.apache.axiom.om;

import junit.framework.TestCase;

import javax.xml.namespace.QName;
import java.util.Iterator;

/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class OMTextTest extends TestCase {
    private static final String AXIS2_NS_URI = "http://ws.apache.org/axis2";
    private static final String AXIS2_NS_PREFIX = "axis2";
    private static final String SOME_TEXT = "Some Text";

    public void testTextQNames() {
        OMFactory factory = OMAbstractFactory.getOMFactory();
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
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement omElement = factory.createOMElement("TestElement", null);
        omElement.setText(SOME_TEXT);

        String elementString = omElement.toString();
        assertTrue(elementString.indexOf(":" + SOME_TEXT) == -1);

        QName textAsQName = omElement.getTextAsQName();
        assertTrue(textAsQName.equals(new QName(SOME_TEXT)));
    }
}
