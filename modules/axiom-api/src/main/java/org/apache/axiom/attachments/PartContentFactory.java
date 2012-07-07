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
package org.apache.axiom.attachments;

import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.utils.BAAInputStream;
import org.apache.axiom.attachments.utils.BAAOutputStream;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.om.OMException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

/**
 * Factory for {@link PartContent} objects. There are different ways to store the content of a part
 * (backing file or backing array etc.). These different implementations should not be exposed to
 * the other layers of the code. The {@link PartContentFactory} helps maintain this abstraction, and
 * makes it easier to add new implementations.
 */
class PartContentFactory {
    private static final Log log = LogFactory.getLog(PartContentFactory.class);
    
    /**
     * Creates a {@link PartContent} object from a given input stream. The remaining parameters are
     * used to determine if the content should be stored in memory (byte buffers) or backed by a
     * file.
     * 
     * @param manager
     * @param in
     * @param isRootPart
     * @param thresholdSize
     * @param attachmentDir
     * @param messageContentLength
     * @return Part
     * @throws OMException
     *             if any exception is encountered while processing.
     */
    static PartContent createPartContent(LifecycleManager manager, InputStream in,
                    boolean isRootPart,
                    int thresholdSize,
                    String attachmentDir,
                    int messageContentLength
                    ) throws OMException {
        if(log.isDebugEnabled()){
            log.debug("Start createPart()");
            log.debug("  isRootPart=" + isRootPart);
            log.debug("  thresholdSize= " + thresholdSize);
            log.debug("  attachmentDir=" + attachmentDir);
            log.debug("  messageContentLength " + messageContentLength);
        }
        
        try {
            if (isRootPart ||
                    thresholdSize <= 0 ||  
                    (messageContentLength > 0 && 
                            messageContentLength < thresholdSize)) {
                // If the entire message is less than the threshold size, 
                // keep it in memory.
                // If this is the root part, keep it in memory.

                // Get the bytes of the data without a lot 
                // of resizing and GC.  The BAAOutputStream 
                // keeps the data in non-contiguous byte buffers.
                BAAOutputStream baaos = new BAAOutputStream();
                BufferUtils.inputStream2OutputStream(in, baaos);
                return new PartContentOnMemory(baaos.buffers(), baaos.length());
            } else {
                // We need to read the input stream to determine whether
                // the size is bigger or smaller than the threshold.
                BAAOutputStream baaos = new BAAOutputStream();
                int count = BufferUtils.inputStream2OutputStream(in, baaos, thresholdSize);

                if (count < thresholdSize) {
                    return new PartContentOnMemory(baaos.buffers(), baaos.length());
                } else {
                    // A BAAInputStream is an input stream over a list of non-contiguous 4K buffers.
                    BAAInputStream baais = 
                        new BAAInputStream(baaos.buffers(), baaos.length());

                    return new PartContentOnFile(manager, 
                                          baais,
                                          in, 
                                          attachmentDir);
                }
            } 
        } catch (StreamCopyException ex) {
            if (ex.getOperation() == StreamCopyException.READ) {
                throw new OMException("Failed to fetch the MIME part content", ex.getCause());
            } else {
                throw new OMException("Failed to write the MIME part content to temporary storage", ex.getCause());
            }
        } catch (Exception e) {
            throw new OMException(e);
        } 
    }
}
