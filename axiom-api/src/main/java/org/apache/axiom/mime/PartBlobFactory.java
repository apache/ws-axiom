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
package org.apache.axiom.mime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.io.StreamCopyException;

/**
 * Factory for the {@link PartBlob} instances returned by {@link Part#getBlob()}. This may be used
 * to create {@link PartBlob} instances that wrap some other type of objects representing the
 * content of MIME parts.
 */
public interface PartBlobFactory {
    /**
     * Default factory that creates {@link PartBlob} instances that lazily access the underlying
     * content.
     */
    PartBlobFactory DEFAULT =
            new PartBlobFactory() {
                @Override
                public PartBlob createBlob(Part part) {
                    return new PartBlob() {
                        @Override
                        public Part getPart() {
                            return part;
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return part.getBlob().getInputStream();
                        }

                        @Override
                        public void writeTo(OutputStream out) throws StreamCopyException {
                            part.getBlob().writeTo(out);
                        }

                        @Override
                        public long getSize() {
                            return part.getBlob().getSize();
                        }
                    };
                }
            };

    /**
     * Create a {@link PartBlob} for the given MIME part.
     *
     * @param part the MIME part
     * @return the blob
     */
    PartBlob createBlob(Part part);
}
