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

package org.apache.axiom.om;

import javax.xml.namespace.QName;


/**
 * Interface OMText.
 * <p/>
 * OMText can contain data as a String, char[] or a DataHandler.
 */
public interface OMText extends OMNode {
    /**
     * Returns the text value of this node.
     *
     * @return Returns String.
     */
    String getText();

    char[] getTextCharacters();

    boolean isCharacters();

    QName getTextAsQName();

    /**
     * Returns the Namespace if this contains a QName Return null otherwise
     *
     * @deprecate This API is going away.  Please use getTextAsQName() instead.
     * @return OMNamespace
     */
    OMNamespace getNamespace();

    /**
     * Gets the datahandler.
     *
     * @return Returns datahandler.
     */
    Object getDataHandler();

    /** @return Returns boolean flag saying whether the node contains an optimized text or not. */
    boolean isOptimized();

    /**
     * Sets the optimize flag.
     *
     * @param value
     */
    void setOptimize(boolean value);

    /** @return Returns boolean flag saying whether the node contains binary or not. */
    boolean isBinary();

    /**
     * Sets the isBinary flag.
     *
     * @param value
     */
    void setBinary(boolean value);


    /**
     * Gets the content id.
     *
     * @return Returns String.
     */
    String getContentID();

}
