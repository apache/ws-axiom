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
package org.apache.axiom.ts.springws.soap.messagefactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;

import org.apache.axiom.ts.soap.SOAPSample;
import org.springframework.ws.transport.TransportInputStream;

final class TransportInputStreamImpl extends TransportInputStream {
    private final SOAPSample sample;

    TransportInputStreamImpl(SOAPSample sample) {
        this.sample = sample;
    }

    @Override
    protected InputStream createInputStream() throws IOException {
        return sample.getInputStream();
    }

    @Override
    public Iterator<String> getHeaderNames() throws IOException {
        return Collections.singleton("Content-Type").iterator();
    }

    @Override
    public Iterator<String> getHeaders(String name) throws IOException {
        if (name.equalsIgnoreCase("Content-Type")) {
            return Collections.singleton(
                    sample.getSOAPSpec().getContentType() + "; charset=\"" + sample.getEncoding()
                            + "\"").iterator();
        } else {
            return Collections.<String>emptySet().iterator();
        }
    }
}
