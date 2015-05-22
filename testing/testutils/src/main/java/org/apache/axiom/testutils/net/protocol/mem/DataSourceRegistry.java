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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataSource;

public final class DataSourceRegistry {
    private static boolean handlerRegistered;
    private static final Map<String,DataSource> dataSources = new HashMap<String,DataSource>();
    
    private DataSourceRegistry() {}

    public synchronized static DataSourceRegistration registerDataSource(DataSource dataSource) {
        if (!handlerRegistered) {
            Properties systemProps = System.getProperties();
            synchronized (systemProps) {
                StringBuilder pkgs = new StringBuilder(systemProps.getProperty("java.protocol.handler.pkgs", ""));
                if (pkgs.length() > 0) {
                    pkgs.append('|');
                }
                pkgs.append("org.apache.axiom.testutils.net.protocol");
                systemProps.setProperty("java.protocol.handler.pkgs", pkgs.toString());
            }
            handlerRegistered = true;
        }
        final String id = UUID.randomUUID().toString();
        dataSources.put(id, dataSource);
        return new DataSourceRegistration() {
            @Override
            public URL getURL() {
                try {
                    // Note: Woodstox expects that URL schemes are between 3 and 8 characters long;
                    //       that's why we chose "mem" and not "ds".
                    return new URL("mem", "", id);
                } catch (MalformedURLException ex) {
                    // We should never get here
                    throw new Error(ex);
                }
            }
            
            @Override
            public void unregister() {
                synchronized (DataSourceRegistry.class) {
                    dataSources.remove(id);
                }
            }
        };
    }
    
    static synchronized DataSource lookupDataSource(String id) {
        return dataSources.get(id);
    }
}
