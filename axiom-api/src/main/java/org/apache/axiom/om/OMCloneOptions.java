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
package org.apache.axiom.om;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.stax.BlobProvider;

public class OMCloneOptions {
    private boolean fetchBlobs;
    private boolean copyOMDataSources;
    private boolean preserveModel;

    /**
     * Determine whether {@link Blob} objects should be fetched when cloning {@link OMText}
     * nodes. See {@link #setFetchBlobs(boolean)} for more information about this option.
     * 
     * @return the current value of this option
     */
    public boolean isFetchBlobs() {
        return fetchBlobs;
    }

    /**
     * Specify whether {@link Blob} objects should be fetched when cloning {@link OMText}
     * nodes. If this option is set to <code>false</code> (default) then an {@link OMText} node
     * backed by a {@link BlobProvider} will be cloned by copying the reference to that
     * {@link BlobProvider} to the cloned {@link OMText} node. This implies that if the
     * original tree was constructed from an XOP encoded stream, then the clone may become unusable
     * if that stream is closed. If this option is set to <code>true</code>, then
     * {@link BlobProvider} references will be replaced by {@link Blob} references. In
     * addition, the necessary actions are taken to ensure that the content of these
     * {@link Blob} instances is fetched into memory or temporary storage, so that the clones
     * remain usable even after the underlying stream is closed.
     * 
     * @param fetchBlobs
     *            the value to set for this option
     */
    public void setFetchBlobs(boolean fetchBlobs) {
        this.fetchBlobs = fetchBlobs;
    }

    /**
     * Determine whether {@link OMSourcedElement} nodes should be cloned as {@link OMSourcedElement}
     * nodes by copying the corresponding {@link OMDataSource} objects. See
     * {@link #setCopyOMDataSources(boolean)} for more information about this option.
     * 
     * @return the current value of this option
     */
    public boolean isCopyOMDataSources() {
        return copyOMDataSources;
    }

    /**
     * Specify whether {@link OMSourcedElement} nodes should be cloned as {@link OMSourcedElement}
     * nodes by copying the corresponding {@link OMDataSource} objects. If this option is set to
     * <code>false</code> (default), then all {@link OMSourcedElement} nodes will be cloned as
     * simple {@link OMElement} instances, which implies that the original {@link OMSourcedElement}
     * nodes will be expanded. If this option is set to <code>true</code>, then an attempt is made
     * to clone {@link OMSourcedElement} nodes as {@link OMSourcedElement} nodes by copying the
     * corresponding {@link OMDataSource} instances. Note that there are several cases where this is
     * not possible:
     * <ul>
     * <li>There is no {@link OMDataSource} set.
     * <li>The {@link OMDataSource} is destructive (or doesn't implement {@link OMDataSourceExt}.
     * <li>The {@link OMSourcedElement} is expanded.
     * </ul>
     * <p>
     * In these cases, {@link OMSourcedElement} nodes will always be cloned as simple
     * {@link OMElement} instances.
     * 
     * @param copyOMDataSources
     *            the value to set for this option
     */
    public void setCopyOMDataSources(boolean copyOMDataSources) {
        this.copyOMDataSources = copyOMDataSources;
    }

    /**
     * Determine whether domain specific extensions to the object model should be preserved. See
     * {@link #setPreserveModel(boolean)} for more information about this option.
     * 
     * @return the current value of this option
     */
    public boolean isPreserveModel() {
        return preserveModel;
    }
    
    /**
     * Specify whether domain specific extensions to the object model should be preserved. If this
     * option is set to <code>false</code> (default), then the object model is always cloned as
     * plain XML even if the original uses domain specific extensions such as SOAP. If this option
     * is set to <code>true</code>, then domain specific extensions are preserved.
     * 
     * @param preserveModel
     *            the value to set for this option
     */
    public void setPreserveModel(boolean preserveModel) {
        this.preserveModel = preserveModel;
    }
}
