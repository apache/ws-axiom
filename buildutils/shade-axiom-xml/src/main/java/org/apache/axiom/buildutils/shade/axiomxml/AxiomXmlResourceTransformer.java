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
package org.apache.axiom.buildutils.shade.axiomxml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Merges {@code META-INF/axiom.xml} files.
 */
public class AxiomXmlResourceTransformer implements ResourceTransformer {
    private static final String AXIOM_XML = "META-INF/axiom.xml";
    
    private Document mergedAxiomXml;

    @Override
    public boolean canTransformResource(String resource) {
        return resource.equals(AXIOM_XML);
    }

    @Override
    public boolean hasTransformedResource() {
        return mergedAxiomXml != null;
    }

    @Override
    public void processResource(String resource, InputStream is, List<Relocator> relocators) throws IOException {
        Document axiomXml = DOMUtils.parse(is);
        is.close();
        NodeList implementations = axiomXml.getElementsByTagNameNS("http://ws.apache.org/axiom/", "implementation");
        for (int i=0; i<implementations.getLength(); i++) {
            Element implementation = (Element)implementations.item(i);
            String loader = implementation.getAttributeNS(null, "loader");
            for (Relocator relocator : relocators) {
                if (relocator.canRelocateClass(loader)) {
                    implementation.setAttributeNS(null, "loader", relocator.relocateClass(loader));
                    break;
                }
            }
        }
        if (mergedAxiomXml == null) {
            mergedAxiomXml = axiomXml;
        } else {
            for (Node node = axiomXml.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling()) {
                mergedAxiomXml.getDocumentElement().appendChild(mergedAxiomXml.importNode(node, true));
            }
        }
    }

    @Override
    public void modifyOutputStream(JarOutputStream os) throws IOException {
        os.putNextEntry(new JarEntry(AXIOM_XML));
        DOMUtils.serialize(mergedAxiomXml, os);
    }
}
