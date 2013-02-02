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

/**
 * Exception indicating that an operation could not be completed because it needed to read
 * additional data from the underlying parser and that action failed. This is typically caused by
 * malformed XML.
 */
public class DeferredParsingException extends OMException {
    private static final long serialVersionUID = -4000648685202187908L;

    public DeferredParsingException(Throwable cause) {
        super(cause);
    }
}
