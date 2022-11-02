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

package org.apache.axiom.om.impl.stream.xop;

import java.io.IOException;

import javax.activation.DataHandler;

import org.apache.axiom.ext.stax.BlobProvider;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link OptimizationPolicy} implementation that takes a decision based on the settings in {@link
 * OMOutputFormat}.
 *
 * <p>For internal use only.
 */
public final class OptimizationPolicyImpl implements OptimizationPolicy {
    private static final Log log = LogFactory.getLog(OptimizationPolicyImpl.class);

    private final OMOutputFormat format;

    public OptimizationPolicyImpl(OMOutputFormat format) {
        this.format = format;
    }

    @Override
    public boolean isOptimized(DataHandler dataHandler, boolean optimize) {
        if (!optimize) {
            return false;
        }
        int threshold = format.getOptimizedThreshold();
        if (threshold == 0) {
            return true;
        }
        try {
            return DataHandlerUtils.isLargerThan(dataHandler, threshold);
        } catch (IOException ex) {
            log.warn("DataHandler.writeTo(OutputStream) threw IOException", ex);
            return true;
        }
    }

    @Override
    public boolean isOptimized(BlobProvider blobProvider, boolean optimize) throws IOException {

        if (!optimize) {
            return false;
        } else if (format.getOptimizedThreshold() == 0) {
            // If no threshold is set, return the result immediately and avoid the call to
            // DataHandlerProvider#getDataHandler(), which would force loading the data handler.
            return true;
        } else {
            return isOptimized(DataHandlerUtils.toDataHandler(blobProvider.getBlob()), optimize);
        }
    }
}
