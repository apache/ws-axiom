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
package org.apache.axiom.ts.om.document;

import java.io.InputStream;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.om.DigestTestCase;

public class TestDigest extends DigestTestCase {
    private final String file;

    public TestDigest(
            OMMetaFactory metaFactory, String file, String algorithm, String expectedDigest) {
        super(metaFactory, algorithm, expectedDigest);
        this.file = file;
        addTestParameter("file", file);
    }

    @Override
    protected OMInformationItem createInformationItem() throws Exception {
        InputStream in = TestDigest.class.getResourceAsStream(file);
        try {
            OMDocument document =
                    OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), in)
                            .getDocument();
            document.build();
            return document;
        } finally {
            in.close();
        }
    }
}
