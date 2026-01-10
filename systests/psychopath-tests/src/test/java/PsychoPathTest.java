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
import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.api.XPath2Expression;
import org.eclipse.wst.xml.xpath2.processor.Engine;
import org.eclipse.wst.xml.xpath2.processor.util.DynamicContextBuilder;
import org.eclipse.wst.xml.xpath2.processor.util.StaticContextBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests that Axiom's DOM implementation is interoperable with the PsychoPath XPath 2.0 processor.
 */
public class PsychoPathTest {
    private static ResultSequence evaluate(String xpath) throws Exception {
        InputStream is = PsychoPathTest.class.getResourceAsStream("test.xml");
        try {
            OMFactory factory =
                    OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM).getOMFactory();
            Document doc =
                    (Document) OMXMLBuilderFactory.createOMBuilder(factory, is).getDocument();
            StaticContextBuilder scb = new StaticContextBuilder();
            XPath2Expression expr = new Engine().parseExpression(xpath, scb);
            return expr.evaluate(new DynamicContextBuilder(scb), new Object[] {doc});
        } finally {
            is.close();
        }
    }

    @Test
    public void testSingleNodeResult() throws Exception {
        ResultSequence rs = evaluate("/Persons/Person[1]/Name");
        assertEquals(1, rs.size());
        assertEquals("Albert Einstein", rs.item(0).getStringValue());
    }

    @Ignore // TODO: doesn't work yet because Node#compareDocumentPosition is not implemented
    @Test
    public void testNodeSetResult() throws Exception {
        ResultSequence rs = evaluate("/Persons/Person[BirthYear='1664']/Name");
        assertEquals(2, rs.size());
        assertEquals("Jean Meslier", rs.item(0).getStringValue());
        assertEquals("Andreas Schlüter", rs.item(1).getStringValue());
    }
}
