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
}
