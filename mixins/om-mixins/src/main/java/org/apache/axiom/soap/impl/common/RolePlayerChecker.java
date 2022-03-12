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

import java.util.List;

import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.intf.SOAPHelper;

/**
 * This Checker uses a RolePlayer to return the appropriate headers for that RolePlayer to process.
 * Ignore "none", always "next", etc.
 */
public class RolePlayerChecker implements ElementMatcher<AxiomElement> {
    private final SOAPHelper soapHelper;
    private final RolePlayer rolePlayer;
    private final String namespace;

    /**
     * Constructor.
     *
     * @param soapHelper the helper corresponding to the SOAP version
     * @param rolePlayer the RolePlayer to check against, or {@code null} to match only header
     *     blocks for the ultimate destination.
     * @param namespace the namespace URI to check for, or {@code null} to match any header block
     */
    public RolePlayerChecker(SOAPHelper soapHelper, RolePlayer rolePlayer, String namespace) {
        this.soapHelper = soapHelper;
        this.rolePlayer = rolePlayer;
        this.namespace = namespace;
    }

    @Override
    public boolean matches(AxiomElement header, String unused1, String unused2) {
        // If we're filtering on namespace, check that first since the compare is simpler.
        if (namespace != null) {
            OMNamespace headerNamespace = header.getNamespace();
            if (headerNamespace == null || !namespace.equals(headerNamespace.getNamespaceURI())) {
                return false;
            }
        }

        String role = SOAPHeaderBlockHelper.getRole(header, soapHelper);
        SOAPVersion version = soapHelper.getVersion();

        // 1. If role is ultimatedest, go by what the rolePlayer says
        if (role == null
                || role.equals("")
                || (version instanceof SOAP12Version
                        && role.equals(SOAP12Constants.SOAP_ROLE_ULTIMATE_RECEIVER))) {
            return (rolePlayer == null || rolePlayer.isUltimateDestination());
        }

        // 2. If role is next, always return true
        if (role.equals(version.getNextRoleURI())) return true;

        // 3. If role is none, always return false
        if (version instanceof SOAP12Version && role.equals(SOAP12Constants.SOAP_ROLE_NONE)) {
            return false;
        }

        // 4. Return t/f depending on match
        List<String> roles = (rolePlayer == null) ? null : rolePlayer.getRoles();
        if (roles != null) {
            for (String thisRole : roles) {
                if (thisRole.equals(role)) return true;
            }
        }

        return false;
    }
}
