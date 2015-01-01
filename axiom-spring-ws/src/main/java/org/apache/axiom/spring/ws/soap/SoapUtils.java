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
package org.apache.axiom.spring.ws.soap;

final class SoapUtils {
    private SoapUtils() {}

    static String escapeAction(String soapAction) {
        return org.springframework.ws.soap.support.SoapUtils.escapeAction(soapAction);
    }
    
    static String unescapeAction(String headerValue) {
        if (headerValue == null || headerValue.isEmpty()) {
            return null;
        } else if (headerValue.length() >= 2 && headerValue.charAt(0) == '"' && headerValue.charAt(headerValue.length()-1) == '"') {
            return headerValue.substring(1, headerValue.length()-1);
        } else {
            return headerValue;
        }
    }
}
