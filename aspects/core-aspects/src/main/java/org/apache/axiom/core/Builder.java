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

import javax.xml.stream.XMLStreamReader;

public interface Builder {
    int next();
    boolean isCompleted();
    void close();
    boolean isClosed();

    /**
     * Get the value of a feature/property from the underlying XMLStreamReader implementation
     * without accessing the XMLStreamReader. https://issues.apache.org/jira/browse/AXIOM-348
     *
     * @param name
     * @return TODO
     */
    Object getReaderProperty(String name) throws IllegalArgumentException;

    XMLStreamReader disableCaching();

    void reenableCaching(CoreParentNode container);

    void discard(CoreParentNode container);

    void debugDiscarded(CoreParentNode container);
}
