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
package org.apache.axiom.testutils.stax;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

final class AttributeSortingXMLStreamReaderFilter extends StreamReaderDelegate {
    private int[] indexMap;

    public AttributeSortingXMLStreamReaderFilter(XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public int next() throws XMLStreamException {
        indexMap = null;
        return super.next();
    }

    @Override
    public int nextTag() throws XMLStreamException {
        indexMap = null;
        return super.nextTag();
    }

    private int getIndex(int index) {
        if (indexMap == null) {
            int n = super.getAttributeCount();
            indexMap = new int[n];
            SortedMap<String, Integer> map = new TreeMap<>();
            for (int i = 0; i < n; i++) {
                map.put(super.getAttributeName(i).toString(), i);
            }
            int newIndex = 0;
            for (int orgIndex : map.values()) {
                indexMap[newIndex++] = orgIndex;
            }
        }
        return indexMap[index];
    }

    @Override
    public String getAttributeLocalName(int index) {
        return super.getAttributeLocalName(getIndex(index));
    }

    @Override
    public QName getAttributeName(int index) {
        return super.getAttributeName(getIndex(index));
    }

    @Override
    public String getAttributeNamespace(int index) {
        return super.getAttributeNamespace(getIndex(index));
    }

    @Override
    public String getAttributePrefix(int index) {
        return super.getAttributePrefix(getIndex(index));
    }

    @Override
    public String getAttributeType(int index) {
        return super.getAttributeType(getIndex(index));
    }

    @Override
    public String getAttributeValue(int index) {
        return super.getAttributeValue(getIndex(index));
    }
}
