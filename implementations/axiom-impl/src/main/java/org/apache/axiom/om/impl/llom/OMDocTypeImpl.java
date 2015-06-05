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

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.AxiomDocType;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;

public class OMDocTypeImpl extends OMLeafNode implements AxiomDocType {
    private final String rootName;
    private final String publicId;
    private final String systemId;
    private final String internalSubset;

    public OMDocTypeImpl(OMContainer parentNode, String rootName, String publicId, String systemId,
            String internalSubset, OMFactory factory, boolean fromBuilder) {
        super(parentNode, factory, fromBuilder);
        this.rootName = rootName;
        this.publicId = publicId;
        this.systemId = systemId;
        this.internalSubset = internalSubset;
    }

    public final int getType() {
        return OMNode.DTD_NODE;
    }

    public void internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        serializer.writeDTD(rootName, publicId, systemId, internalSubset);
    }

    public String getRootName() {
        return rootName;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getInternalSubset() {
        return internalSubset;
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        return getOMFactory().createOMDocType(targetParent, rootName, publicId, systemId, internalSubset);
    }
}
