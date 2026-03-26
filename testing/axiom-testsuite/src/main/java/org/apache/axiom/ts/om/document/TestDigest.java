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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

public class TestDigest extends DigestTestCase {
    public record Params(String file, String algorithm, String expectedDigest) {}

    public static final ImmutableList<Params> PARAMS =
            ImmutableList.of(
                    new Params("digest1.xml", "MD5", "3e5d68c6607bc56c9c171560e4f19db9"),
                    new Params("digest2.xml", "SHA1", "3c47a807517d867d42ffacb2d3e9da81895d5aac"),
                    new Params("digest3.xml", "SHA", "41466144c1cab4234fb127cfb8cf92f9"),
                    new Params("digest4.xml", "SHA", "be3b0836cd6f0ceacdf3d40b49a0468d03d2ba2e"));

    private final Params params;

    @Inject
    public TestDigest(OMMetaFactory metaFactory, Params params) {
        super(metaFactory, params.algorithm(), params.expectedDigest());
        this.params = params;
    }

    @Override
    protected OMInformationItem createInformationItem() throws Exception {
        InputStream in = TestDigest.class.getResourceAsStream(params.file());
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
