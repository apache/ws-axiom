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
package org.apache.axiom.core.stream.stax.pull.output;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlReader;

public final class FakeReader implements XmlReader {
    private final XmlHandler handler;
    private final Queue<Action> actions;

    public FakeReader(XmlHandler handler, Action[] actions) {
        this.handler = handler;
        this.actions = new LinkedList<>(Arrays.asList(actions));
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    @Override
    public boolean proceed() throws StreamException {
        if (actions.isEmpty()) {
            return true;
        }
        actions.remove().run(handler);
        return false;
    }

    @Override
    public void dispose() {}
}
