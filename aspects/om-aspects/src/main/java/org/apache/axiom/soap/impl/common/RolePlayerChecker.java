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

import java.util.Iterator;
import java.util.List;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPVersion;

/**
 * This Checker uses a RolePlayer to return the appropriate headers for that RolePlayer to process.
 * Ignore "none", always "next", etc.
 */
public class RolePlayerChecker implements Checker {
    RolePlayer rolePlayer;

    /** Optional namespace - if non-null we'll only return headers that match */
    String namespace;

    /**
     * Constructor.
     *
     * @param rolePlayer the RolePlayer to check against.  This can be null, in which
     *                   case we assume we're the ultimate destination.
     */
    public RolePlayerChecker(RolePlayer rolePlayer) {
        this.rolePlayer = rolePlayer;
    }

    public RolePlayerChecker(RolePlayer rolePlayer, String namespace) {
        this.rolePlayer = rolePlayer;
        this.namespace = namespace;
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        // If we're filtering on namespace, check that first since the compare is simpler.
        if (namespace != null) {
            OMNamespace headerNamespace = header.getNamespace();
            if (headerNamespace == null || !namespace.equals(headerNamespace.getNamespaceURI())) {
                return false;
            }
        }

        String role = header.getRole();
        SOAPVersion version = header.getVersion();

        // 1. If role is ultimatedest, go by what the rolePlayer says
        if (role == null || role.equals("") ||
                (version instanceof SOAP12Version &&
                        role.equals(SOAP12Constants.SOAP_ROLE_ULTIMATE_RECEIVER))) {
            return (rolePlayer == null || rolePlayer.isUltimateDestination());
        }

        // 2. If role is next, always return true
        if (role.equals(version.getNextRoleURI())) return true;

        // 3. If role is none, always return false
        if (version instanceof SOAP12Version &&
                role.equals(SOAP12Constants.SOAP_ROLE_NONE)) {
            return false;
        }

        // 4. Return t/f depending on match
        List roles = (rolePlayer == null) ? null : rolePlayer.getRoles();
        if (roles != null) {
            for (Iterator i = roles.iterator(); i.hasNext();) {
                String thisRole = (String) i.next();
                if (thisRole.equals(role)) return true;
            }
        }

        return false;
    }
}
