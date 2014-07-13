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

package org.apache.axiom.ts.soap.header;

import java.util.List;
import java.util.ArrayList;

import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.apache.axiom.om.OMMetaFactory;

public abstract class SOAPRoleTest extends SOAPTestCase {
    public static final String CUSTOM_ROLE = "http://example.org/myCustomRole";

    protected static class MyRolePlayer implements RolePlayer {
        boolean ultimateReceiver;
        List roles;

        public MyRolePlayer(boolean ultimateReceiver) {
            this.ultimateReceiver = ultimateReceiver;
            roles = null;
        }

        public MyRolePlayer(boolean ultimateReceiver, String [] roles) {
            this.ultimateReceiver = ultimateReceiver;
            this.roles = new ArrayList();
            for (int i = 0; i < roles.length; i++) {
                this.roles.add(roles[i]);
            }
        }

        public List getRoles() {
            return roles;
        }

        public boolean isUltimateDestination() {
            return ultimateReceiver;
        }
    }

    public SOAPRoleTest(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }
}
