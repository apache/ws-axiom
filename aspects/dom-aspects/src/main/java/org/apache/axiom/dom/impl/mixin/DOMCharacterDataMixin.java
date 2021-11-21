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
package org.apache.axiom.dom.impl.mixin;

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.dom.DOMCharacterData;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMSemantics;
import org.w3c.dom.DOMException;

@org.apache.axiom.weaver.annotation.Mixin(DOMCharacterData.class)
public abstract class DOMCharacterDataMixin implements DOMCharacterData {
    public final String getData() {
        try {
            return coreGetCharacterData().toString();
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final void setData(String data) {
        try {
            coreSetCharacterData(data, DOMSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final String getNodeValue() {
        return getData();
    }

    public final void setNodeValue(String nodeValue) {
        setData(nodeValue);
    }

    public final int getLength() {
        String data = getData();
        return data != null ? data.length() : 0;
    }
    
    public final void appendData(String arg) {
        setData(getData() + arg);
    }

    public final void deleteData(int offset, int count) {
        replaceData(offset, count, null);
    }

    public final void replaceData(int offset, int count, String arg) {
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

    public final void insertData(int offset, String arg) {
        String data = getData();
        if (offset < 0 || offset > data.length()) {
            throw newDOMException(DOMException.INDEX_SIZE_ERR);
        }
        setData(new StringBuilder(data).insert(offset, arg).toString());
    }

    public final String substringData(int offset, int count) {
        String data = getData();
        int length = data.length();
        if (offset < 0 || offset > length || count < 0) {
            throw newDOMException(DOMException.INDEX_SIZE_ERR);
        }
        return data.substring(offset, Math.min(count + offset, length));
    }
}
