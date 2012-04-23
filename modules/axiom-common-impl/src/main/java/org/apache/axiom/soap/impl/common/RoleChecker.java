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

import org.apache.axiom.soap.SOAPHeaderBlock;

/**
 * A Checker to make sure headers match a given role.  If the role we're looking for is null, then
 * everything matches.
 */
public class RoleChecker implements Checker {
    String role;

    public RoleChecker(String role) {
        this.role = role;
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        if (role == null) {
            return true;
        }
        String thisRole = header.getRole();
        return (role.equals(thisRole));
    }
}
