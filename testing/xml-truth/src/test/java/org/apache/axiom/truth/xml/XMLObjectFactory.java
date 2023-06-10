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

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.testing.multiton.Instances;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.xml.XMLSample;
import org.xml.sax.InputSource;

public abstract class XMLObjectFactory extends Multiton {
    public static final XMLObjectFactory DEFAULT =
            new XMLObjectFactory("url") {
                @Override
                public Object toXMLObject(XMLSample sample) {
                    return sample.getUrl();
                }
            };

    private final String name;

    private XMLObjectFactory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Object toXMLObject(XMLSample sample) throws Exception;

    @Instances
    private static XMLObjectFactory[] instances() {
        List<XMLObjectFactory> instances = new ArrayList<>();
        for (final DOMImplementation impl : getInstances(DOMImplementation.class)) {
            instances.add(
                    new XMLObjectFactory(impl.getName() + "-dom") {
                        @Override
                        public Object toXMLObject(XMLSample sample) throws Exception {
                            return impl.parse(new InputSource(sample.getUrl().toString()), false);
                        }
                    });
        }
        return instances.toArray(new XMLObjectFactory[instances.size()]);
    }
}
