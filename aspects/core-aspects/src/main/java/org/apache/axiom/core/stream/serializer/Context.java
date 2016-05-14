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
package org.apache.axiom.core.stream.serializer;

import org.apache.axiom.core.stream.serializer.writer.UnmappableCharacterHandler;

enum Context {
    MIXED_CONTENT(null, UnmappableCharacterHandler.CONVERT_TO_CHARACTER_REFERENCE),
    TAG(null, UnmappableCharacterHandler.THROW_EXCEPTION),
    ATTRIBUTE_VALUE(null, UnmappableCharacterHandler.CONVERT_TO_CHARACTER_REFERENCE),
    COMMENT("--", UnmappableCharacterHandler.THROW_EXCEPTION),
    PROCESSING_INSTRUCTION("?>", UnmappableCharacterHandler.THROW_EXCEPTION),
    CDATA_SECTION("]]>", UnmappableCharacterHandler.THROW_EXCEPTION);
    
    private final String invalidCharacterSequence;
    private final UnmappableCharacterHandler unmappableCharacterHandler;
    
    private Context(String invalidCharacterSequence, UnmappableCharacterHandler unmappableCharacterHandler) {
        this.invalidCharacterSequence = invalidCharacterSequence;
        this.unmappableCharacterHandler = unmappableCharacterHandler;
    }

    String getInvalidCharacterSequence() {
        return invalidCharacterSequence;
    }

    UnmappableCharacterHandler getUnmappableCharacterHandler() {
        return unmappableCharacterHandler;
    }
}
