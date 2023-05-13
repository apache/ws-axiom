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

package org.apache.axiom.ts.dom.document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.apache.xerces.jaxp.validation.XMLSchemaFactory;

public class TestValidator extends DOMTestCase {
    public TestValidator(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        SchemaFactory factory = new XMLSchemaFactory();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Schema schema =
                factory.newSchema(
                        new DOMSource(
                                builder.parse(TestValidator.class.getResourceAsStream("ipo.xsd"))));
        Validator validator = schema.newValidator();
        validator.validate(
                new DOMSource(builder.parse(TestValidator.class.getResourceAsStream("ipo_1.xml"))));
    }
}
