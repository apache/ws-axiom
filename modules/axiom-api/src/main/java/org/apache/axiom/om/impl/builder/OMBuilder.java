package org.apache.axiom.om.impl.builder;

import java.io.InputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;

public interface OMBuilder {
	
	public void init(InputStream inputStream, String charSetEncoding, String url, String contentType) throws OMException;
    /**
     * @return Returns the document element.
     */
	public OMElement getDocumentElement();
 
	public String getCharsetEncoding();
}
