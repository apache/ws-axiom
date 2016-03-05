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
package org.apache.axiom.om.util;

import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.util.stax.debug.XMLStreamReaderValidator;

/**
 * Subclass of {@link XMLStreamReaderValidator} that also implements {@link OMXMLStreamReader}.
 *
 * @see org.apache.axiom.om.OMElement#getXMLStreamReader(boolean)
 */
public class OMXMLStreamReaderValidator extends XMLStreamReaderValidator implements OMXMLStreamReader {
    /**
     * @param delegate XMLStreamReader to validate
     * @param throwExceptions (true if exceptions should be thrown when errors are encountered)
     */
    public OMXMLStreamReaderValidator(OMXMLStreamReader delegate, boolean throwExceptions) {
        super(delegate, throwExceptions);
    }

    public boolean isInlineMTOM() {
        return ((OMXMLStreamReader)getParent()).isInlineMTOM();
    }


    public void setInlineMTOM(boolean value) {
        ((OMXMLStreamReader)getParent()).setInlineMTOM(value);
    }
    

}
