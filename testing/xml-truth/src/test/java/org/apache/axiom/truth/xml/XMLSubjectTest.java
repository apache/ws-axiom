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
package org.apache.axiom.truth.xml;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.junit.Test;

public class XMLSubjectTest {
    @Test
    public void testIgnoringRedundantNamespaceDeclarations1() {
        assertAbout(xml())
                .that("<a xmlns:p='#1' xmlns=''><b xmlns:p='#1'/></a>")
                .ignoringRedundantNamespaceDeclarations()
                .hasSameContentAs("<a xmlns:p='#1'><b/></a>");
    }

    @Test(expected = AssertionError.class)
    public void testIgnoringRedundantNamespaceDeclarations2() {
        assertAbout(xml())
                .that("<a xmlns:p='#1'><b xmlns:p='#2'/></a>")
                .ignoringRedundantNamespaceDeclarations()
                .hasSameContentAs("<a xmlns:p='#1'><b/></a>");
    }

    @Test
    public void testIgnoringNamespaceDeclarationsAndPrefixes() {
        // START SNIPPET: sample
        assertAbout(xml())
                .that("<p:a xmlns:p='urn:ns'/>")
                .ignoringNamespacePrefixes()
                .ignoringNamespaceDeclarations()
                .hasSameContentAs("<a xmlns='urn:ns'/>");
        // END SNIPPET: sample
    }

    @Test
    public void testIgnoringComments() {
        assertAbout(xml())
                .that("<a><!-- comment --></a>")
                .ignoringComments()
                .hasSameContentAs("<a/>");
    }
}
