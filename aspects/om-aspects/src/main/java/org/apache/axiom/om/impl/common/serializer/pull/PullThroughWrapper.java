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
package org.apache.axiom.om.impl.common.serializer.pull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

final class PullThroughWrapper extends AbstractWrapper {
    private final StAXOMBuilder builder;
    private final OMContainer container;

    PullThroughWrapper(PullSerializer serializer, StAXOMBuilder builder, OMContainer container, XMLStreamReader reader, int startDepth) {
        super(serializer, reader, startDepth);
        this.builder = builder;
        this.container = container;
    }

    void released() throws XMLStreamException {
        if (container instanceof OMDocument) {
            // No need to reenable caching; just close the builder
            builder.close();
        } else {
            // Consume remaining events so that we can reenable caching
            while (doNext()) {
                // Just loop
            }
            builder.reenableCaching(container);
        }
    }
}
