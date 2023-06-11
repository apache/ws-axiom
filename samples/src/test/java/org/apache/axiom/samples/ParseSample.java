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
package org.apache.axiom.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLBuilderFactory;

public class ParseSample extends TestCase {
    // START SNIPPET: main
    public void processFile(File file) throws IOException, OMException {
        // Create a builder for the file and get the root element
        InputStream in = new FileInputStream(file);
        OMElement root = OMXMLBuilderFactory.createOMBuilder(in).getDocumentElement();

        // Process the content of the file
        OMElement urlElement =
                root.getFirstChildWithName(new QName("http://maven.apache.org/POM/4.0.0", "url"));
        if (urlElement == null) {
            System.out.println("No <url> element found");
        } else {
            System.out.println("url = " + urlElement.getText());
        }

        // Because Axiom uses deferred parsing, the stream must be closed AFTER
        // processing the document (unless OMElement#build() is called)
        in.close();
    }
    // END SNIPPET: main

    public void test() throws Exception {
        String basedir = System.getProperty("basedir");
        processFile(new File(basedir == null ? "." : basedir, "pom.xml"));
    }
}
