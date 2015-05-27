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

import javax.activation.DataHandler;

// TODO: this needs reformatting; the (generated) Javadoc is unreadable!
/**
 * There are several places in the code where events are passed from 
 * a source to a consumer using XMLStreamReader events. 
 * 
 *     OMXMLStreamReader (impl)--> consumer of XMLStreamReader events
 * 
 * This simple class can be interjected as a filter and used to do some simple validation.
 * Validating the events coming from source (impl) can help find and correct errors 
 * when they occur.  Otherwise the errors may be caught much further downstream and hard to fix.
 * 
 *    OMXMLStreamReader (impl)--> OMXMLStreamReaderValiator-> consumer of XMLStreamReader events
 * 
 * 
 * In the initial version, the XMStreamValidator ensures that the start element events match the 
 * end element events.
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

    public DataHandler getDataHandler(String blobcid) {
        return ((OMXMLStreamReader)getParent()).getDataHandler(blobcid);
    }


    public boolean isInlineMTOM() {
        return ((OMXMLStreamReader)getParent()).isInlineMTOM();
    }


    public void setInlineMTOM(boolean value) {
        ((OMXMLStreamReader)getParent()).setInlineMTOM(value);
    }
    

}
