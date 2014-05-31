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
package org.apache.axiom.ts.soap;

import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPHeaderBlock;

/**
 * Describes a boolean attribute that can appear on a SOAP header block. This includes the
 * <tt>mustUnderstand</tt> attribute in all SOAP versions as well as the <tt>relay</tt> attribute
 * defined by SOAP 1.2.
 */
public interface BooleanAttribute {
    BooleanAttribute MUST_UNDERSTAND = new BooleanAttribute() {
        public String getName() {
            return SOAPConstants.ATTR_MUSTUNDERSTAND;
        }

        public boolean isSupported(SOAPSpec spec) {
            return true;
        }

        public boolean getValue(SOAPHeaderBlock headerBlock) {
            return headerBlock.getMustUnderstand();
        }

        public void setValue(SOAPHeaderBlock headerBlock, boolean value) {
            headerBlock.setMustUnderstand(value);
        }
    };
    
    BooleanAttribute RELAY = new BooleanAttribute() {
        public String getName() {
            return SOAP12Constants.SOAP_RELAY;
        }

        public boolean isSupported(SOAPSpec spec) {
            return spec == SOAPSpec.SOAP12;
        }

        public boolean getValue(SOAPHeaderBlock headerBlock) {
            return headerBlock.getRelay();
        }

        public void setValue(SOAPHeaderBlock headerBlock, boolean value) {
            headerBlock.setRelay(value);
        }
    };
    
    /**
     * Get the name of the attribute.
     * 
     * @return the name of the attribute (<tt>mustUnderstand</tt> or <tt>relay</tt>)
     */
    String getName();
    
    /**
     * Determine if the attribute is supported by the given SOAP version.
     * 
     * @param spec
     *            identifies the SOAP version
     * @return <code>true</code> if the attribute is supported, <code>false</code> otherwise
     */
    boolean isSupported(SOAPSpec spec);
    
    /**
     * Invoke the getter method for this attribute on the given {@link SOAPHeaderBlock}.
     * 
     * @param headerBlock
     *            the header block
     * @return the value returned by the getter method
     */
    boolean getValue(SOAPHeaderBlock headerBlock);
    
    /**
     * Invoke the setter method for this attribute on the given {@link SOAPHeaderBlock}.
     * 
     * @param headerBlock
     *            the heaer block
     * @param value
     *            the value to pass to the setter
     */
    void setValue(SOAPHeaderBlock headerBlock, boolean value);
}
