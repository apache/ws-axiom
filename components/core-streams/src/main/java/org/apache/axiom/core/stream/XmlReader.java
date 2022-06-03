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

public interface XmlReader {
    /**
     * Output one or more events to the {@link XmlHandler} connected to this {@link XmlReader}.
     *
     * @return {@code true} if the last event sent to the {@link XmlHandler} was {@link
     *     XmlHandler#completed()} and there are no more events to produce; {@code false} if {@link
     *     #proceed()} should be called again to produce more events
     * @throws StreamException
     */
    boolean proceed() throws StreamException;

    void dispose();
}
