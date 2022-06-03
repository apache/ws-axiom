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
package org.apache.axiom.core.stream.xop;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlHandlerWrapper;

public abstract class AbstractXOPEncodingFilterHandler extends XmlHandlerWrapper {
    private boolean inXOPInclude;

    public AbstractXOPEncodingFilterHandler(XmlHandler parent) {
        super(parent);
    }

    protected abstract String processCharacterData(Object data) throws StreamException;

    /**
     * Build a cid URL from the given content ID as described in RFC2392.
     *
     * <p>Note that this implementation only encodes the percent character (replacing it by "%25").
     * The reason is given by the following quotes from RFC3986:
     *
     * <blockquote>
     *
     * If a reserved character is found in a URI component and no delimiting role is known for that
     * character, then it must be interpreted as representing the data octet corresponding to that
     * character's encoding in US-ASCII. [...]
     *
     * <p>Under normal circumstances, the only time when octets within a URI are percent-encoded is
     * during the process of producing the URI from its component parts. This is when an
     * implementation determines which of the reserved characters are to be used as subcomponent
     * delimiters and which can be safely used as data. [...]
     *
     * <p>Because the percent ("%") character serves as the indicator for percent-encoded octets, it
     * must be percent-encoded as "%25" for that octet to be used as data within a URI.
     *
     * </blockquote>
     *
     * <p>Since RFC2392 doesn't define any subcomponents for the cid scheme and since RFC2045
     * specifies that only US-ASCII characters are allowed in content IDs, the percent character
     * (which is specifically allowed by RFC2045) is the only character that needs URL encoding.
     *
     * <p>Another reason to strictly limit the set of characters to be encoded is that some
     * applications fail to decode cid URLs correctly if they contain percent encoded octets.
     *
     * @param contentID the content ID (without enclosing angle brackets)
     * @return the corresponding URL in the cid scheme
     */
    private static String getURLForContentID(String contentID) {
        return "cid:" + contentID.replaceAll("%", "%25");
    }

    private boolean flushIfNecessary() throws StreamException {
        if (inXOPInclude) {
            super.endElement();
            inXOPInclude = false;
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        flushIfNecessary();
        super.startElement(namespaceURI, localName, prefix);
    }

    @Override
    public void endElement() throws StreamException {
        flushIfNecessary();
        super.endElement();
    }

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        if (!ignorable) {
            String contentID = processCharacterData(data);
            if (contentID != null) {
                super.startElement(
                        XOPConstants.NAMESPACE_URI,
                        XOPConstants.INCLUDE,
                        XOPConstants.DEFAULT_PREFIX);
                super.processNamespaceDeclaration(
                        XOPConstants.DEFAULT_PREFIX, XOPConstants.NAMESPACE_URI);
                super.processAttribute(
                        "", XOPConstants.HREF, "", getURLForContentID(contentID), "CDATA", true);
                super.attributesCompleted();
                inXOPInclude = true;
                return;
            }
        }
        super.processCharacterData(data, ignorable);
    }

    @Override
    public void completed() throws StreamException {
        super.completed();
    }

    @Override
    public boolean drain() throws StreamException {
        if (super.drain()) {
            return flushIfNecessary();
        } else {
            return false;
        }
    }
}
