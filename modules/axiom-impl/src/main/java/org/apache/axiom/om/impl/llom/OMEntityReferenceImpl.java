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
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;

public class OMEntityReferenceImpl extends OMLeafNode implements OMEntityReference {
    private final String name;
    private final String replacementText;

    public OMEntityReferenceImpl(OMContainer parent, String name, String replacementText,
            OMFactory factory, boolean fromBuilder) {
        super(parent, factory, fromBuilder);
        this.name = name;
        this.replacementText = replacementText;
    }

    public int getType() {
        return OMNode.ENTITY_REFERENCE_NODE;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        writer.writeEntityRef(name);
    }

    public String getName() {
        return name;
    }

    public String getReplacementText() {
        return replacementText;
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        return new OMEntityReferenceImpl(targetParent, name, replacementText, factory, false);
    }
}
