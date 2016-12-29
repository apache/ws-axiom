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
import org.apache.axiom.om.ds.ByteArrayDataSource;

import javax.xml.stream.XMLStreamException;

import java.io.ByteArrayOutputStream;


/**
 * @deprecated Use {@link BlobOMDataSourceCustomBuilder} instead.
 */
public class ByteArrayCustomBuilder implements CustomBuilder {
    private String encoding = null;
    
    /**
     * Constructor
     * @param encoding 
     */
    public ByteArrayCustomBuilder(String encoding) {
        this.encoding = (encoding == null) ? "utf-8" :encoding;
    }

    @Override
    public OMDataSource create(OMElement element) throws OMException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            element.serializeAndConsume(baos);
            byte[] bytes = baos.toByteArray();
            return new ByteArrayDataSource(bytes, encoding);
        } catch (XMLStreamException e) {
            throw new OMException(e);
        } catch (OMException e) {
            throw e;
        } catch (Throwable t) {
            throw new OMException(t);
        }
    }

}
