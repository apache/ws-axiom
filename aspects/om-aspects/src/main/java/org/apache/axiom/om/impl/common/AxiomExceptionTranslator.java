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
package org.apache.axiom.om.impl.common;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;

public class AxiomExceptionTranslator {
    private AxiomExceptionTranslator() {}
    
    public static OMException translate(CoreModelException ex) {
        return new OMException(ex);
    }
    
    public static XMLStreamException toXMLStreamException(OutputException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof XMLStreamException) {
            return (XMLStreamException)cause;
        } else {
            return new XMLStreamException(ex);
        }
    }
}
