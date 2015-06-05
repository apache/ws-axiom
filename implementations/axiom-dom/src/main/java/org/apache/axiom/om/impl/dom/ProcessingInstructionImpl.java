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

package org.apache.axiom.om.impl.dom;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.AxiomProcessingInstruction;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class ProcessingInstructionImpl extends LeafNode implements ProcessingInstruction, AxiomProcessingInstruction {
    private String target;
    private String value;

    public ProcessingInstructionImpl(String target, String value, OMFactory factory) {
        
        super(factory);
        this.target = target;
        this.value = value;
    }

    public int getType() {
        return OMNode.PI_NODE;
    }

    public short getNodeType() {
        return Node.PROCESSING_INSTRUCTION_NODE;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String text) {
        this.value = text;
    }
    
    public String getData() {
        return value;
    }
    
    public void setData(String data) throws DOMException {
        value = data;
    }
    
    public String getNodeName() {
        return target;
    }

    public String getNodeValue() throws DOMException {
        return value;
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        value = nodeValue;
    }

    public void internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        serializer.writeProcessingInstruction(target + " ", value);
    }

    ChildNode createClone() {
        return new ProcessingInstructionImpl(target, value, getOMFactory());
    }
}
