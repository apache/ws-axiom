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
package org.apache.axiom.soap;

import org.apache.axiom.om.OMCloneOptions;

public class SOAPCloneOptions extends OMCloneOptions {
    private Boolean processedFlag;

    /**
     * Determine how the {@link SOAPHeaderBlock#isProcessed()} flag should be set on cloned {@link
     * SOAPHeaderBlock} nodes. See {@link #setProcessedFlag(Boolean)} for more information about
     * this option.
     *
     * @return the current value of this option
     */
    public Boolean getProcessedFlag() {
        return processedFlag;
    }

    /**
     * Specify how the {@link SOAPHeaderBlock#isProcessed()} flag should be set on cloned {@link
     * SOAPHeaderBlock} nodes. If this option is set to <code>null</code> (default), the flag will
     * be copied from the original node. Otherwise, the flag will have the value determined by the
     * option value.
     *
     * @param processedFlag the value to set for this option
     */
    public void setProcessedFlag(Boolean processedFlag) {
        this.processedFlag = processedFlag;
    }
}
