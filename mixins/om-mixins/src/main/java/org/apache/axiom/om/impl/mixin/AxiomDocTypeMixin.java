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
package org.apache.axiom.om.impl.mixin;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.apache.axiom.om.impl.stream.stax.push.XMLStreamWriterHandler;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class AxiomDocTypeMixin implements AxiomDocType {
    @Override
    public final int getType() {
        return DTD_NODE;
    }

    @Override
    public final String getRootName() {
        return coreGetRootName();
    }

    @Override
    public final String getPublicId() {
        return coreGetPublicId();
    }

    @Override
    public final String getSystemId() {
        return coreGetSystemId();
    }

    @Override
    public final String getInternalSubset() {
        return coreGetInternalSubset();
    }

    @Override
    public final void serialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        try {
            internalSerialize(new XMLStreamWriterHandler(writer), cache);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        } catch (StreamException ex) {
            throw AxiomExceptionTranslator.toXMLStreamException(ex);
        }
    }

    @Override
    public final void buildWithAttachments() {}
}
