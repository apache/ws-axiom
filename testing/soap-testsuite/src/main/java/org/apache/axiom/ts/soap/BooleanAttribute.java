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

import org.apache.axiom.testing.multiton.Multiton;

/**
 * Describes a boolean attribute that can appear on a SOAP header block. This includes the
 * <tt>mustUnderstand</tt> attribute in all SOAP versions as well as the <tt>relay</tt> attribute
 * defined by SOAP 1.2.
 */
public abstract class BooleanAttribute extends Multiton {
    public static final BooleanAttribute MUST_UNDERSTAND = new BooleanAttribute() {
        public String getName() {
            return "mustUnderstand";
        }

        public boolean isSupported(SOAPSpec spec) {
            return true;
        }
    };
    
    public static final BooleanAttribute RELAY = new BooleanAttribute() {
        public String getName() {
            return "relay";
        }

        public boolean isSupported(SOAPSpec spec) {
            return spec == SOAPSpec.SOAP12;
        }
    };
    
    /**
     * Get the name of the attribute.
     * 
     * @return the name of the attribute (<tt>mustUnderstand</tt> or <tt>relay</tt>)
     */
    public abstract String getName();
    
    /**
     * Determine if the attribute is supported by the given SOAP version.
     * 
     * @param spec
     *            identifies the SOAP version
     * @return <code>true</code> if the attribute is supported, <code>false</code> otherwise
     */
    public abstract boolean isSupported(SOAPSpec spec);
}
