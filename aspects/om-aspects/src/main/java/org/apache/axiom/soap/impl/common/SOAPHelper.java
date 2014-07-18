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
package org.apache.axiom.soap.impl.common;

import org.apache.axiom.soap.SOAPVersion;

/**
 * Encapsulates certain SOAP version specific behaviors. This API defines methods that could also be
 * added to {@link SOAPVersion}, but that are not relevant for application code and should therefore
 * not be part of the public API.
 */
public interface SOAPHelper {
    SOAPHelper SOAP11 = new SOAPHelper() {
        public Boolean parseBoolean(String literal) {
            if (literal.equals("1")) {
                return Boolean.TRUE;
            } else if (literal.equals("0")) {
                return Boolean.FALSE;
            } else {
                return null;
            }
        }
    };
    
    SOAPHelper SOAP12 = new SOAPHelper() {
        public Boolean parseBoolean(String literal) {
            if (literal.equals("true") || literal.equals("1")) {
                return Boolean.TRUE;
            } else if (literal.equals("false") || literal.equals("0")) {
                return Boolean.FALSE;
            } else {
                return null;
            }
        }
    };
    
    Boolean parseBoolean(String literal);
}
