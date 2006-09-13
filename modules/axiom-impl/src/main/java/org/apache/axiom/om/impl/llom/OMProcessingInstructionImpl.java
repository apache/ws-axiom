/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class OMProcessingInstructionImpl extends OMNodeImpl implements OMProcessingInstruction {
    protected String target;
    protected String value;

    /**
     * Constructor OMProcessingInstructionImpl.
     *
     * @param parentNode
     * @param target
     * @param value
     */
    public OMProcessingInstructionImpl(OMContainer parentNode, String target, 
            String value, OMFactory factory) {
        super(parentNode, factory, true);
        this.target = (target == null) ? null : target.trim();
        this.value = (value == null) ? null : value.trim();
        nodeType = OMNode.PI_NODE;
    }

    /**
     * Constructor OMProcessingInstructionImpl.
     *
     * @param parentNode
     */
    public OMProcessingInstructionImpl(OMContainer parentNode, 
            OMFactory factory) {
        this(parentNode, null, null, factory);
    }

    /**
     * Serializes the node with caching.
     *
     * @param writer
     * @throws XMLStreamException
     */
    public void internalSerialize(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeProcessingInstruction(this.target+" ", this.value);
    }

    /**
     * Serializes the node without caching.
     *
     * @param writer
     * @throws XMLStreamException
     */
    public void internalSerializeAndConsume(XMLStreamWriter writer) throws XMLStreamException {
        internalSerialize(writer);
    }

    /**
     * Gets the value of this Processing Instruction.
     *
     * @return string
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the target of this Processing Instruction.
     *
     * @param target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Gets the target of this Processing Instruction.
     *
     * @return Returns String.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of this Processing Instruction.
     *
     * @param text
     */
    public void setValue(String text) {
        this.value = text;
    }

    /**
     * Discards this node.
     *
     * @throws OMException
     */
    public void discard() throws OMException {
        if (done) {
            this.detach();
        } else {
            builder.discard((OMElement) this.parent);
        }
    }
}
