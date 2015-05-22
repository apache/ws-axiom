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
package org.apache.axiom.testutils.net.protocol.mem;

import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;

import org.apache.axiom.testutils.activation.RandomDataSource;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.testutils.net.protocol.mem.DataSourceRegistration;
import org.apache.axiom.testutils.net.protocol.mem.DataSourceRegistry;

public class DataSourceRegistryTest extends TestCase {
    public void test() throws Exception {
        RandomDataSource ds = new RandomDataSource(1000);
        DataSourceRegistration registration = DataSourceRegistry.registerDataSource(ds);
        try {
            // We must be able to connect to the URL after converting it to a String
            URL url = new URL(registration.getURL().toString());
            URLConnection connection = url.openConnection();
            IOTestUtils.compareStreams(connection.getInputStream(), ds.getInputStream());
        } finally {
            registration.unregister();
        }
    }
}
