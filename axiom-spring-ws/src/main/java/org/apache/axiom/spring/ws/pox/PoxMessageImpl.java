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
package org.apache.axiom.spring.ws.pox;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.apache.axiom.spring.ws.AxiomWebServiceMessage;
import org.apache.axiom.spring.ws.SourceExtractionStrategy;
import org.apache.axiom.spring.ws.SourceExtractionStrategyStack;

final class PoxMessageImpl implements AxiomWebServiceMessage {
    private final SourceExtractionStrategyStack extractionStrategyStack = new SourceExtractionStrategyStack();
    
    public Source getPayloadSource() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Result getPayloadResult() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public QName getPayloadRootQName() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void pushSourceExtractionStrategy(SourceExtractionStrategy strategy, Object bean) {
        extractionStrategyStack.push(strategy, bean);
    }

    public void popSourceExtractionStrategy(Object bean) {
        extractionStrategyStack.pop(bean);
    }
}
