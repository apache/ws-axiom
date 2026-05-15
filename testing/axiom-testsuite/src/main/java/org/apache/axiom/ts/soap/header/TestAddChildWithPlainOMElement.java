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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import junit.framework.TestCase;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;

/** Regression test for AXIOM-512. */
public class TestAddChildWithPlainOMElement extends TestCase {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    protected void runTest() throws Throwable {
        SOAPHeader soapHeader = soapFactory.createSOAPHeader();
        soapHeader.addChild(soapFactory.createOMElement("foobar", "http://example.org", "p"));
        Iterator<SOAPHeaderBlock> it = soapHeader.examineAllHeaderBlocks();
        assertThat(it.hasNext()).isTrue();
        SOAPHeaderBlock headerBlock = it.next();
        assertThat(headerBlock.getLocalName()).isEqualTo("foobar");
    }
}
