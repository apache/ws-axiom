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
package org.apache.axiom.ts.om.sourcedelement;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

/** Verifies that a OMDataSource can be replaced with another one */
@SuppressWarnings("deprecation")
public class TestSetDataSource extends AxiomTestCase {
    public TestSetDataSource(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        String payload1 = "<tns:myPayload xmlns:tns=\"urn://test\">Payload One</tns:myPayload>";
        String payload2 = "<tns:myPayload xmlns:tns=\"urn://test\">Payload Two</tns:myPayload>";
        OMDataSource nonDestructiveOMDataSource1 = new PullOMDataSource(payload1, false);
        OMDataSource nonDestructiveOMDataSource2 = new PullOMDataSource(payload2, false);
        OMDataSource destructiveOMDataSource1 = new PullOMDataSource(payload1, true);
        OMDataSource destructiveOMDataSource2 = new PullOMDataSource(payload2, true);

        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent = factory.createOMElement("parent", null);
        OMSourcedElement omse =
                factory.createOMElement(
                        nonDestructiveOMDataSource1,
                        "myPayload",
                        factory.createOMNamespace("urn://test", "tns"));
        parent.addChild(omse);
        OMNode firstChild = parent.getFirstOMChild();
        assertTrue("Expected OMSourcedElement child", firstChild instanceof OMSourcedElement);
        OMSourcedElement child = (OMSourcedElement) firstChild;
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        assertThat(child.getDataSource()).isSameInstanceAs(nonDestructiveOMDataSource1);

        // Write out the body
        StringWriter sw = new StringWriter();
        parent.serialize(sw);
        String output = sw.toString();
        //        System.out.println(output);
        assertTrue("The payload was not present in the output", output.indexOf(payload1) > 0);
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());

        // Replace with payload2.
        // Important note, it is legal to replace the OMDataSource, but
        // the namespace and local name of the OMSourcedElement cannot be changed.
        child.setDataSource(nonDestructiveOMDataSource2);

        // Write out the body
        sw = new StringWriter();
        parent.serialize(sw);
        output = sw.toString();
        //        System.out.println(output);
        assertTrue("The payload was not present in the output", output.indexOf(payload2) > 0);
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());

        // Now Replace with payload1 from an destructiveOMDataSource1
        child.setDataSource(destructiveOMDataSource1);
        sw = new StringWriter();
        parent.serialize(sw);
        output = sw.toString();
        //        System.out.println(output);
        assertTrue("The payload was not present in the output", output.indexOf(payload1) > 0);

        // Now Replace with payload2 from an destructiveOMDataSource2.
        // Note at this point, the child's tree is expanded.
        child.setDataSource(destructiveOMDataSource2);
        sw = new StringWriter();
        parent.serialize(sw);
        output = sw.toString();
        //        System.out.println(output);
        assertTrue("The payload was not present in the output", output.indexOf(payload2) > 0);
    }
}
