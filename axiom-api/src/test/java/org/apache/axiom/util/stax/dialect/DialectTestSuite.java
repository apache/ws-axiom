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
package org.apache.axiom.util.stax.dialect;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DialectTestSuite extends TestSuite {
    public static Test suite() throws Exception {
        DialectTestSuiteBuilder builder = new DialectTestSuiteBuilder();
        
        // Neither SJSXP nor XLXP report whitespace in prolog
        builder.exclude(TestGetTextInProlog.class, "(implementation=JRE)");
        
        // SJSXP and XLXP don't report whitespace in prolog
        builder.exclude(TestGetTextInProlog.class, "(|(implementation=sjsxp-*)(implementation=com.ibm.ws.prereq.xlxp.jar)(implementation=xml.jar))");
        
        // DTDReader is not supported for all StAX implementations
        builder.exclude(TestDTDReader.class, "(|(implementation=stax-1.2.0.jar)(implementation=wstx-asl-3.*))");
        
        // TODO: investigate why this fails; didn't occur with the old TestCloseInputStream test
        builder.exclude(TestClose.class, "(&(implementation=stax-1.2.0.jar)(type=InputStream))");
        
        return builder.build();
    }
}
