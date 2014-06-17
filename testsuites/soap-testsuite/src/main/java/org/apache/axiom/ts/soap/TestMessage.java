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
package org.apache.axiom.ts.soap;

import java.io.InputStream;

/**
 * A SOAP test message.
 */
public abstract class TestMessage extends Adaptable {
    private final SOAPSpec spec;
    private final String name;
    
    TestMessage(SOAPSpec spec, String name) {
        this.spec = spec;
        this.name = name;
    }
    
    /**
     * Get the SOAP version of this message.
     * 
     * @return the SOAP specification version
     */
    public final SOAPSpec getSOAPSpec() {
        return spec;
    }
    
    /**
     * Get the name of this message (for use in test case naming e.g.).
     * 
     * @return the name of this test message
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Get the content of this message.
     * 
     * @return an input stream with the content of this message
     */
    public abstract InputStream getInputStream();
}
