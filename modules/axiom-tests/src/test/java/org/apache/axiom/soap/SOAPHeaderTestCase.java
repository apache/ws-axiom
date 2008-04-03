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

import org.apache.axiom.om.OMNamespace;

public abstract class SOAPHeaderTestCase extends SOAPTestCase {
    protected SOAPHeader soap11Header;
    protected SOAPHeader soap12Header;
    protected SOAPHeader soap11HeaderWithParser;
    protected SOAPHeader soap12HeaderWithParser;
    protected OMNamespace namespace;

    public SOAPHeaderTestCase(String testName) {
        super(testName);
        namespace =
                omFactory.createOMNamespace("http://www.example.org", "test");
    }

    protected void setUp() throws Exception {
        super.setUp();
        soap11Header = soap11Factory.createSOAPHeader(soap11Envelope);
        soap12Header = soap12Factory.createSOAPHeader(soap12Envelope);
        soap11HeaderWithParser = soap11EnvelopeWithParser.getHeader();
        soap12HeaderWithParser = soap12EnvelopeWithParser.getHeader();
    }
}
