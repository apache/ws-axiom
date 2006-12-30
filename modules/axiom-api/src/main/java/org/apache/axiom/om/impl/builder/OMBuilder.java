package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMElement;

public interface OMBuilder {
    /**
     * @return Returns the document element.
     */
    OMElement getDocumentElement();
    
    String getCharsetEncoding();
}
