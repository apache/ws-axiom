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
package org.apache.axiom.ts.om;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.util.DigestGenerator;
import org.apache.axiom.testutils.DigestUtils;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Base class for unit tests validating the DOMHASH implementation.
 *
 * <p>Note that the only DOMHASH implementation available for reference is IBM's XSS4J.
 * Unfortunately, XSS4J is no longer available for download, but it can still be found in WebSphere
 * (see the <code>com.ibm.ws.wssecurity.xss4j.domutil.Digest</code> class).
 */
public abstract class DigestTestCase extends AxiomTestCase {
    private final String algorithm;
    private final String expectedDigest;

    public DigestTestCase(OMMetaFactory metaFactory, String algorithm, String expectedDigest) {
        super(metaFactory);
        this.algorithm = algorithm;
        this.expectedDigest = expectedDigest;
    }

    @Override
    protected final void runTest() throws Throwable {
        OMInformationItem node = createInformationItem();
        DigestGenerator digestGenerator = new DigestGenerator();
        byte[] digest;
        if (node instanceof OMDocument) {
            digest = digestGenerator.getDigest((OMDocument) node, algorithm);
        } else if (node instanceof OMAttribute) {
            digest = digestGenerator.getDigest((OMAttribute) node, algorithm);
        } else {
            digest = digestGenerator.getDigest((OMNode) node, algorithm);
        }
        assertEquals(expectedDigest, DigestUtils.toHexString(digest));
    }

    protected abstract OMInformationItem createInformationItem() throws Exception;
}
