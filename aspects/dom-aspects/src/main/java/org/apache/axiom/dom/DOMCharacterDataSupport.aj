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
package org.apache.axiom.dom;

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.w3c.dom.DOMException;

public aspect DOMCharacterDataSupport {
    public final String DOMCharacterData.getNodeValue() {
        return getData();
    }

    public final void DOMCharacterData.setNodeValue(String nodeValue) {
        setData(nodeValue);
    }

    public final int DOMCharacterData.getLength() {
        String data = getData();
        return data != null ? data.length() : 0;
    }
    
    public final void DOMCharacterData.appendData(String arg) {
        setData(getData() + arg);
    }

    public final void DOMCharacterData.deleteData(int offset, int count) {
        replaceData(offset, count, null);
    }

    public final void DOMCharacterData.replaceData(int offset, int count, String arg) {
        String data = getData();
        int length = data.length();
        if (offset < 0 || offset > length || count < 0) {
            throw newDOMException(DOMException.INDEX_SIZE_ERR);
        } else {
            int end = Math.min(count + offset, length);
            if (arg == null) {
                setData(new StringBuilder(data).delete(offset, end).toString());
            } else {
                setData(new StringBuilder(data).replace(offset, end, arg).toString());
            }
        }
    }

    public final void DOMCharacterData.insertData(int offset, String arg) {
        String data = getData();
        if (offset < 0 || offset > data.length()) {
            throw newDOMException(DOMException.INDEX_SIZE_ERR);
        }
        setData(new StringBuilder(data).insert(offset, arg).toString());
    }

    public final String DOMCharacterData.substringData(int offset, int count) {
        String data = getData();
        int length = data.length();
        if (offset < 0 || offset > length || count < 0) {
            throw newDOMException(DOMException.INDEX_SIZE_ERR);
        }
        return data.substring(offset, Math.min(count + offset, length));
    }
}
