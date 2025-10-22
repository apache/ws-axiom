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

package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;

import java.util.List;
import java.util.Locale;

public interface SOAPFaultReason extends OMElement {
    void addSOAPText(SOAPFaultText soapFaultText) throws SOAPProcessingException;

    SOAPFaultText getFirstSOAPText();

    List<SOAPFaultText> getAllSoapTexts();

    SOAPFaultText getSOAPFaultText(String language);

    /**
     * Extract the fault reason text for the locale that best matches the given locale. This method
     * works for all SOAP versions. Since SOAP 1.1 doesn't support localized fault reasons, the
     * locale is effectively ignored in that version. For SOAP 1.2 the method extracts the text from
     * the {@link SOAPFaultText} that best matches the given locale, falling back to English.
     *
     * @param locale the requested locale
     * @return the reason text
     */
    String getFaultReasonText(Locale locale);
}
