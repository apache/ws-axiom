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
package org.apache.axiom.soap.impl.common;

import java.util.Iterator;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;

/** An Iterator which walks the header list as needed, potentially filtering as we traverse. */
public class HeaderIterator implements Iterator {
    SOAPHeaderBlock current;
    boolean advance = false;
    Checker checker;

    public HeaderIterator(SOAPHeader header) {
        this(header, null);
    }

    public HeaderIterator(SOAPHeader header, Checker checker) {
        this.checker = checker;
        current = (SOAPHeaderBlock) header.getFirstElement();
        if (current != null) {
            if (!checkHeader(current)) {
                advance = true;
                hasNext();
            }
        }
    }

    public void remove() {
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        if (checker == null) return true;
        return checker.checkHeader(header);
    }

    public boolean hasNext() {
        if (!advance) {
            return current != null;
        }

        advance = false;
        OMNode sibling = current.getNextOMSibling();

        while (sibling != null) {
            if (sibling instanceof SOAPHeaderBlock) {
                SOAPHeaderBlock possible = (SOAPHeaderBlock) sibling;
                if (checkHeader(possible)) {
                    current = (SOAPHeaderBlock) sibling;
                    return true;
                }
            }
            sibling = sibling.getNextOMSibling();
        }

        current = null;
        return false;
    }

    public Object next() {
        SOAPHeaderBlock ret = current;
        if (ret != null) {
            advance = true;
            hasNext();
        }
        return ret;
    }
}