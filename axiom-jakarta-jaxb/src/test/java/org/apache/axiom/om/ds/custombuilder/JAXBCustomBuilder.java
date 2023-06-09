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

package org.apache.axiom.om.ds.custombuilder;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.ds.jaxb.JAXBOMDataSource;
import org.apache.axiom.om.util.jaxb.JAXBUtils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

public class JAXBCustomBuilder implements CustomBuilder {
    private final JAXBContext jaxbContext;
    private Object jaxbObject;

    public JAXBCustomBuilder(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    @Override
    public OMDataSource create(OMElement element) throws OMException {
        try {
            jaxbObject = JAXBUtils.unmarshal(element, jaxbContext, null, false);
            return new JAXBOMDataSource(jaxbContext, jaxbObject);
        } catch (JAXBException ex) {
            throw new OMException(ex);
        }
    }

    public Object getJaxbObject() {
        return jaxbObject;
    }
}
