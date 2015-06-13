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
 * Indicates an attempt to perform an invalid operation on the model. This exception is checked
 * since the frontend implementation must translate any exception thrown by the backend
 * implementation to an exception specific to the frontend API.
 */
public abstract class CoreModelException extends Exception {
    private static final long serialVersionUID = 1204321445792058777L;

    public CoreModelException() {
    }

    public CoreModelException(String message) {
        super(message);
    }

    public CoreModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreModelException(Throwable cause) {
        super(cause);
    }
}
