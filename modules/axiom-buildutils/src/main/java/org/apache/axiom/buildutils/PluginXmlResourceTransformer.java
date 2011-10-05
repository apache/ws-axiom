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
package org.apache.axiom.buildutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugins.shade.resource.ResourceTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Quick and dirty hack to adjust the groupId/artifactId/version in a shaded Maven plugin.
 */
public class PluginXmlResourceTransformer implements ResourceTransformer {
    private static final String PLUGIN_XML = "META-INF/maven/plugin.xml";
    
    String groupId;
    String artifactId;
    String version;
    
    private Document pluginXml;

    public boolean canTransformResource(String resource) {
        return resource.equals(PLUGIN_XML);
    }

    public boolean hasTransformedResource() {
        return pluginXml != null;
    }

    public void processResource(String resource, InputStream is, List relocators) throws IOException {
        try {
            pluginXml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        } catch (SAXException ex) {
            throw toIOException(ex);
        } catch (ParserConfigurationException ex) {
            throw toIOException(ex);
        }
        is.close();
        Node node = pluginXml.getDocumentElement().getFirstChild();
        while (node != null) {
            if (node instanceof Element) {
                Element element = (Element)node;
                String name = element.getTagName();
                if (name.equals("groupId")) {
                    element.setTextContent(groupId);
                } else if (name.equals("artifactId")) {
                    element.setTextContent(artifactId);
                } else if (name.equals("version")) {
                    element.setTextContent(version);
                }
            }
            node = node.getNextSibling();
        }
    }

    public void modifyOutputStream(JarOutputStream os) throws IOException {
        os.putNextEntry(new JarEntry(PLUGIN_XML));
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(pluginXml), new StreamResult(os));
        } catch (TransformerException ex) {
            throw toIOException(ex);
        }
    }
    
    private IOException toIOException(Exception ex) {
        IOException ioException = new IOException();
        ioException.initCause(ex);
        return ioException;
    }
}
