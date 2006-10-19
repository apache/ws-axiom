package org.apache.axiom.om.impl.llom.util;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;

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

public class AXIOMUtil {

    /**
     * This will help you to create an OMElement from an xml fragment which you have as a string.
     *
     * @param xmlFragment - the well-formed xml fragment
     * @return The OMElement created out of the string xml fragment.
     * @throws XMLStreamException
     */
    public static OMElement stringToOM(String xmlFragment) throws XMLStreamException {
        if(xmlFragment != null){
            return new StAXOMBuilder(new ByteArrayInputStream(xmlFragment.getBytes())).getDocumentElement();
        }
        return null;
    }
}
