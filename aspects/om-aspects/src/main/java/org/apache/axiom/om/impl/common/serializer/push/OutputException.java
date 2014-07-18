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
package org.apache.axiom.om.impl.common.serializer.push;

/**
 * Wraps an exception thrown by the underlying API (StAX, SAX, etc.) while writing data. This class
 * is abstract so that the type of the wrapped exception can be effectively restricted by defining
 * an appropriate subclass.
 */
public abstract class OutputException extends Exception {
    private static final long serialVersionUID = 7173617216602466028L;

    public OutputException(Throwable cause) {
        super(cause);
    }
}
