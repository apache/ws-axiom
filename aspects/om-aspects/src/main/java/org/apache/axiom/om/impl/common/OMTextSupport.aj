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
package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMText;

public aspect OMTextSupport {
    declare parents: (InformationItem+ && OMText+) implements IText;

    private boolean IText.optimize;
    private boolean IText.binary;

    public final boolean IText.isBinary() {
        return binary;
    }

    public final void IText.setBinary(boolean binary) {
        this.binary = binary;
    }

    public final boolean IText.isOptimized() {
        return optimize;
    }

    public final void IText.setOptimize(boolean optimize) {
        this.optimize = optimize;
        if (optimize) {
            binary = true;
        }
    }
}
