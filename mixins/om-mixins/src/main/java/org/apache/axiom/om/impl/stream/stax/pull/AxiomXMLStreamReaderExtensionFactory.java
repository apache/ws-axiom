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
package org.apache.axiom.om.impl.stream.stax.pull;

import org.apache.axiom.core.stream.stax.pull.output.InternalXMLStreamReader;
import org.apache.axiom.core.stream.stax.pull.output.XMLStreamReaderExtensionFactory;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;

public final class AxiomXMLStreamReaderExtensionFactory implements XMLStreamReaderExtensionFactory {
    public static final AxiomXMLStreamReaderExtensionFactory INSTANCE = new AxiomXMLStreamReaderExtensionFactory();

    private AxiomXMLStreamReaderExtensionFactory() {}
    
    @Override
    public Object createExtension(String propertyName, InternalXMLStreamReader reader) {
        if (propertyName.equals(DataHandlerReader.PROPERTY)) {
            return new DataHandlerReaderImpl(reader);
        } else if (propertyName.equals(DTDReader.PROPERTY)) {
            return new DTDReaderImpl(reader);
        } else if (propertyName.equals(CharacterDataReader.PROPERTY)) {
            return new CharacterDataReaderImpl(reader);
        } else {
            return null;
        }
    }
}
