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

/** A MIME header. */
public final class Header {
    /** The name of the {@code Content-Type} header. */
    public static final String CONTENT_TYPE = "Content-Type";

    /** The name of the {@code Content-ID} header. */
    public static final String CONTENT_ID = "Content-ID";

    /** The name of the {@code Content-Transfer-Encoding} header. */
    public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

    private final String name;
    private final String value;

    /**
     * Constructor.
     *
     * @param name the name of the header
     * @param value the value of the header
     */
    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Get the name of this header.
     *
     * @return the name of this header
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of this header.
     *
     * @return the value of this header
     */
    public String getValue() {
        return value;
    }
}
