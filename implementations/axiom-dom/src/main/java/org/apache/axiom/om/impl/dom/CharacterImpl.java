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

import org.apache.axiom.dom.DOMCharacterData;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.OMNodeEx;
import org.w3c.dom.DOMException;

/**
 * This implements the OMText operations which are to be inherited by TextImpl, CommentImpl,
 * CDATASectionImpl.
 */
public abstract class CharacterImpl extends LeafNode implements DOMCharacterData, OMNodeEx {

    protected String textValue;

    protected CharacterImpl(OMFactory factory) {
        super(factory);
    }

    public CharacterImpl(String value, OMFactory factory) {
        super(factory);
        this.textValue = (value != null) ? value : "";
    }

    /** Returns the value of the data. */
    public String getData() throws DOMException {
        return (this.textValue != null) ? this.textValue : "";
    }

    /** Sets the text value of data. */
    public void setData(String data) throws DOMException {
        this.textValue = data;
    }
}
