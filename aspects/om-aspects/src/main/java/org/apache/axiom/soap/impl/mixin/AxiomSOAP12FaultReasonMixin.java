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
package org.apache.axiom.soap.impl.mixin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.common.util.LocaleUtil;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultReason;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(AxiomSOAP12FaultReason.class)
public abstract class AxiomSOAP12FaultReasonMixin implements AxiomSOAP12FaultReason {
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12FaultReason.class;
    }

    public final boolean isChildElementAllowed(OMElement child) {
        return child instanceof SOAPFaultText;
    }
    
    public final void addSOAPText(SOAPFaultText soapFaultText) {
        addChild(soapFaultText);
    }

    public final SOAPFaultText getFirstSOAPText() {
        return (SOAPFaultText)getFirstElement();
    }

    public final List<SOAPFaultText> getAllSoapTexts() {
        List<SOAPFaultText> faultTexts = new ArrayList<SOAPFaultText>();
        for (Iterator<OMElement> it = getChildElements(); it.hasNext(); ) {
            faultTexts.add((SOAPFaultText)it.next());
        }
        return faultTexts;
    }

    public final SOAPFaultText getSOAPFaultText(String language) {
        for (Iterator<OMElement> it = getChildElements(); it.hasNext(); ) {
            SOAPFaultText text = (SOAPFaultText)it.next();
            if (language == null || language.equals(text.getLang())) {
                return text;
            }
        }
        return null;
    }

    public final String getFaultReasonText(Locale locale) {
        String text = "";
        int maxScore = -1;
        for (Iterator<OMElement> it = getChildElements(); it.hasNext(); ) {
            SOAPFaultText textNode = (SOAPFaultText)it.next();
            String lang = textNode.getLang();
            int score = LocaleUtil.getMatchScore(locale, lang == null ? null : Locale.forLanguageTag(lang));
            if (score > maxScore) {
                text = textNode.getText();
                maxScore = score;
            }
        }
        return text;
    }
}
