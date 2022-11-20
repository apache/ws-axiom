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
package org.apache.axiom.mime.activation;

import org.apache.axiom.mime.PartBlobFactory;
import org.apache.axiom.mime.Part;
import org.apache.axiom.mime.PartBlob;

/**
 * {@link PartBlobFactory} implementation that creates {@link PartBlob} instances that wrap {@link
 * PartDataHandler} instances.
 */
public abstract class PartDataHandlerBlobFactory implements PartBlobFactory {
    /**
     * {@link PartDataHandlerBlobFactory} instance that creates plain {@link PartDataHandler}
     * instances.
     */
    public static final PartDataHandlerBlobFactory DEFAULT =
            new PartDataHandlerBlobFactory() {
                @Override
                protected PartDataHandler createDataHandler(Part part) {
                    return new PartDataHandler(part);
                }
            };

    protected PartDataHandlerBlobFactory() {}

    @Override
    public final PartBlob createBlob(Part part) {
        return createDataHandler(part).getBlob();
    }

    /**
     * Create a {@link PartDataHandler} for the given {@link Part}.
     *
     * @param part the MIME part
     * @return the data handler
     */
    protected abstract PartDataHandler createDataHandler(Part part);
}
