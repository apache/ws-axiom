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
package org.apache.axiom.ts.dom.element;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.function.Executable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class TestRemoveSingleChild implements Executable {
    @Inject
    private DocumentBuilderFactory dbf;

    @Override
    public void execute() throws Throwable {
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader("<root><a/></root>")));
        Element element = document.getDocumentElement();
        Node child = element.getFirstChild();
        element.removeChild(child);
        assertThat(element.getFirstChild()).isNull();
        assertThat(element.getLastChild()).isNull();
        assertThat(child.getPreviousSibling()).isNull();
        assertThat(child.getNextSibling()).isNull();
        assertThat(child.getParentNode()).isNull();
        assertThat(child.getOwnerDocument()).isSameAs(document);
    }
}
