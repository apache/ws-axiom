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

/**
 * Indicates that a request to insert a node could not be fulfilled because the node belongs to a
 * different document. Whether or not this exception may be thrown by a method depends on the
 * provided {@link NodeMigrationPolicy} implementation.
 */
public class WrongDocumentException extends NodeMigrationException {
    private static final long serialVersionUID = -7135259787609333075L;

    public WrongDocumentException() {
    }

    public WrongDocumentException(String message) {
        super(message);
    }
}
