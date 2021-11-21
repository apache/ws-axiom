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
package org.apache.axiom.core;

import org.apache.axiom.core.stream.StreamException;

/**
 * Indicates that a parsing error occurred. This exception may be thrown by any core model method
 * that accesses parts of the tree that have not been visited before. This exception wraps a
 * {@link StreamException} that further describes the parsing error. The {@link StreamException}
 * itself typically wraps an exception specific to the underlying parser, e.g. a
 * {@link javax.xml.stream.XMLStreamException}.
 */
public class DeferredParsingException extends DeferredBuildingException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param cause the cause
     */
    public DeferredParsingException(StreamException cause) {
        super(cause);
    }

    @Override
    public Throwable initCause(Throwable cause) {
        if (!(cause instanceof StreamException)) {
            throw new IllegalArgumentException();
        }
        return super.initCause(cause);
    }
    
    /**
     * Get the {@link StreamException} wrapped by this exception.
     * 
     * @return the wrapped exception
     */
    public StreamException getStreamException() {
        return (StreamException)getCause();
    }
}
