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
package org.apache.axiom.om.impl.mixin;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.apache.axiom.om.impl.intf.Serializer;
import org.apache.axiom.om.impl.stream.StreamException;

public aspect AxiomCommentSupport {
    public final int AxiomComment.getType() {
        return OMNode.COMMENT_NODE;
    }

    public String AxiomComment.getValue() {
        return coreGetCharacterData().toString();
    }

    public void AxiomComment.setValue(String text) {
        coreSetCharacterData(text, AxiomSemantics.INSTANCE);
    }
    
    public final void AxiomComment.internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws StreamException {
        serializer.processComment(coreGetCharacterData().toString());
    }
    
    public final void AxiomComment.buildWithAttachments() {
    }
}
