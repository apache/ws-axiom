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
package org.apache.axiom.locator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

final class ImplementationFactory {
    static final String DESCRIPTOR_RESOURCE = "META-INF/axiom.xml";
    
    private static final String NS = "http://ws.apache.org/axiom/";
    private static final QName QNAME_IMPLEMENTATIONS = new QName(NS, "implementations");
    private static final QName QNAME_IMPLEMENTATION = new QName(NS, "implementation");
    private static final QName QNAME_FEATURE = new QName(NS, "feature");
    
    private static final Log log = LogFactory.getLog(ImplementationFactory.class);
    
    private ImplementationFactory() {}
    
    static Implementation createDefaultImplementation(Loader loader, String className) {
        OMMetaFactory metaFactory = load(loader, className);
        return metaFactory == null ? null : new Implementation(null, metaFactory,
                new Feature[] { new Feature(OMAbstractFactory.FEATURE_DEFAULT, Integer.MAX_VALUE) });
    }
    
    static OMMetaFactory load(Loader loader, String className) {
        Class clazz;
        try {
            clazz = loader.load(className);
        } catch (ClassNotFoundException ex) {
            log.error("The class " + className + " could not be loaded", ex);
            return null;
        }
        try {
            return (OMMetaFactory)clazz.newInstance();
        } catch (Exception ex) {
            log.error("The class " + className + " could not be instantiated", ex);
            return null;
        }
    }
    
    static List/*<Implementation>*/ parseDescriptor(Loader loader, URL url) {
        List implementations = new ArrayList();
        try {
            // Since this code is used to discover Axiom implementations, we have to use DOM here.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Element root = dbf.newDocumentBuilder().parse(url.toString()).getDocumentElement();
            QName rootQName = getQName(root);
            if (rootQName.equals(QNAME_IMPLEMENTATIONS)) {
                Node child = root.getFirstChild();
                while (child != null) {
                    if (child instanceof Element) {
                        QName childQName = getQName(child);
                        if (childQName.equals(QNAME_IMPLEMENTATION)) {
                            Implementation implementation = parseImplementation(loader, (Element)child);
                            if (implementation != null) {
                                implementations.add(implementation);
                            }
                        } else {
                            log.warn("Skipping unexpected element " + childQName + "; only "
                                    + QNAME_IMPLEMENTATION + " is expected");
                        }
                    }
                    child = child.getNextSibling();
                }
            } else {
                log.error(url + " is not a valid implementation descriptor: unexpected root element "
                        + rootQName + "; expected " + QNAME_IMPLEMENTATIONS);
            }
        } catch (ParserConfigurationException ex) {
            // If we get here, something went badly wrong
            throw new Error(ex);
        } catch (IOException ex) {
            log.error("Unable to read " + url, ex);
        } catch (SAXException ex) {
            log.error("Parser error while reading " + url, ex);
        }
        return implementations;
    }
    
    private static Implementation parseImplementation(Loader loader, Element implementation) {
        String name = implementation.getAttributeNS(null, "name");
        if (name.length() == 0) {
            log.error("Encountered " + QNAME_IMPLEMENTATION + " element without name attribute");
            return null;
        }
        String className = implementation.getAttributeNS(null, "metaFactory");
        if (className.length() == 0) {
            log.error("Encountered " + QNAME_IMPLEMENTATION + " element without metaFactory attribute");
            return null;
        }
        OMMetaFactory metaFactory = load(loader, className);
        if (metaFactory == null) {
            return null;
        }
        List/*<Feature>*/ features = new ArrayList();
        Node child = implementation.getFirstChild();
        while (child != null) {
            if (child instanceof Element) {
                QName childQName = getQName(child);
                if (childQName.equals(QNAME_FEATURE)) {
                    Feature feature = parseFeature((Element)child);
                    if (feature != null) {
                        features.add(feature);
                    }
                } else {
                    log.warn("Skipping unexpected element " + childQName + "; only "
                            + QNAME_FEATURE + " is expected");
                }
            }
            child = child.getNextSibling();
        }
        return new Implementation(name, metaFactory, (Feature[])features.toArray(new Feature[features.size()]));
    }

    private static Feature parseFeature(Element feature) {
        String name = feature.getAttributeNS(null, "name");
        if (name.length() == 0) {
            log.error("Encountered " + QNAME_FEATURE + " element without name attribute");
            return null;
        }
        String priority = feature.getAttributeNS(null, "priority");
        if (priority.length() == 0) {
            log.error("Encountered " + QNAME_FEATURE + " element without priority attribute");
            return null;
        }
        try {
            return new Feature(name, Integer.parseInt(priority));
        } catch (NumberFormatException ex) {
            log.error("Invalid priority value '" + priority + "'; must be an integer");
            return null;
        }
    }
    
    private static QName getQName(Node node) {
        String namespaceURI = node.getNamespaceURI();
        return namespaceURI == null ? new QName(node.getLocalName()) : new QName(namespaceURI, node.getLocalName());
    }
}
