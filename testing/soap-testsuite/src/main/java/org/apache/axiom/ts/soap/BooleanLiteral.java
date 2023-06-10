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
package org.apache.axiom.ts.soap;

/**
 * Describes a boolean literal recognized by a given SOAP version.
 *
 * @see SOAPSpec#getBooleanLiterals()
 */
public final class BooleanLiteral {
    static final BooleanLiteral TRUE = new BooleanLiteral("true", true);
    static final BooleanLiteral FALSE = new BooleanLiteral("false", false);
    static final BooleanLiteral ONE = new BooleanLiteral("1", true);
    static final BooleanLiteral ZERO = new BooleanLiteral("0", false);

    private final String lexicalRepresentation;
    private final boolean value;

    BooleanLiteral(String lexicalRepresentation, boolean value) {
        this.lexicalRepresentation = lexicalRepresentation;
        this.value = value;
    }

    /**
     * Get the lexical representation of this literal.
     *
     * @return the lexical representation
     */
    public String getLexicalRepresentation() {
        return lexicalRepresentation;
    }

    /**
     * Get the value corresponding to this literal.
     *
     * @return the value
     */
    public boolean getValue() {
        return value;
    }
}
