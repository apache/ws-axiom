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
package org.apache.axiom.core.stream;

import java.io.IOException;
import org.apache.axiom.checker.union.Union;

// TODO: clean up this Javadoc
/**
 * Represents character data stored by a {@link org.apache.axiom.core.CoreCharacterDataNode}
 * instance. The content of a {@link CoreCharacterDataNode} is either a {@link String} object or an
 * instance of this interface.
 */
public interface CharacterData {
    @Override
    String toString();

    void writeTo(CharacterDataSink sink) throws IOException;

    void appendTo(StringBuilder buffer);

    /**
     * Returns a {@link String} or a {@link CharacterData} instance that remains valid beyond the
     * current {@link XmlHandler#processCharacterData} invocation. If this instance is already valid
     * beyond that scope, implementations may return {@code this}.
     *
     * <p>The return type is {@link Object} because the value is either a {@link String} or a
     * {@link CharacterData}, consistent with the {@code data} parameter of {@link
     * XmlHandler#processCharacterData}.
     *
     * <p>Callers must invoke this method before storing a {@link CharacterData} value beyond the
     * duration of a {@link XmlHandler#processCharacterData} invocation.
     *
     * @return a {@link String} or {@link CharacterData} representation of this instance that
     *     remains valid beyond the current invocation
     */
    @Union(types = {String.class, CharacterData.class})
    Object retain();
}
