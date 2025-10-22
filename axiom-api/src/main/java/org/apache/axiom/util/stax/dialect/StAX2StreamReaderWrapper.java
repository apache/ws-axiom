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
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;
import org.codehaus.stax2.DTDInfo;

/**
 * {@link XMLStreamReaderWrapper} implementation that exposes the {@link DTDReader} extension based
 * on the {@link DTDInfo} defined by "StAX2".
 */
class StAX2StreamReaderWrapper extends XMLStreamReaderWrapper implements DTDReader {
    public StAX2StreamReaderWrapper(XMLStreamReader parent) {
        super(parent);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (DTDReader.PROPERTY.equals(name)) {
            return this;
        } else {
            return super.getProperty(name);
        }
    }

    @Override
    public String getRootName() {
        return ((DTDInfo) getParent()).getDTDRootName();
    }

    @Override
    public String getPublicId() {
        return ((DTDInfo) getParent()).getDTDPublicId();
    }

    @Override
    public String getSystemId() {
        return ((DTDInfo) getParent()).getDTDSystemId();
    }
}
