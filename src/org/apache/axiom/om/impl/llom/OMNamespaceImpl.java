/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 * Class OMNamespaceImpl
 */
public class OMNamespaceImpl implements OMNamespace {
    /**
     * Field prefix
     */
    private String prefix;

    /**
     * Field uri
     */
    private String uri;
    
    private OMFactory factory;

    /**
     * @param uri
     * @param prefix
     */
    public OMNamespaceImpl(String uri, String prefix, OMFactory factory) {
        this.uri = uri;
        this.prefix = prefix;
        this.factory = factory;
    }

    /**
     * Method equals.
     *
     * @param uri
     * @param prefix
     * @return Returns boolean.
     */
    public boolean equals(String uri, String prefix) {
        return (((prefix == null) && (this.prefix == null)) ||
                ((prefix != null) && prefix.equals(this.prefix))) &&
                uri.equals(uri);
    }

    /**
     * Method getPrefix.
     *
     * @return Returns String.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Method getName.
     *
     * @return Returns String.
     */
    public String getName() {
        return uri;
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNamespace#getOMFactory()
     */
    public OMFactory getOMFactory() {
        return this.factory;
    }
}
