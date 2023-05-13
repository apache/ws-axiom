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
package org.w3c.domts;

/**
 * Replacement class for the original <code>DOMTestLoadException</code> supporting exception
 * chaining and having an additional constructor.
 */
public class DOMTestLoadException extends Exception {
    private static final long serialVersionUID = 1L;

    public DOMTestLoadException(String msg) {
        super(msg);
    }

    public DOMTestLoadException(Throwable cause) {
        super(cause);
    }
}
