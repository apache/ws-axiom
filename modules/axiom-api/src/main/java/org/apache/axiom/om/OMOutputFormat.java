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

package org.apache.axiom.om;

import org.apache.axiom.om.impl.MTOMConstants;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;


/**
 * Formats options for OM Output.
 * <p/>
 * Setting of all the properties in a OMOutputFormat should be done before calling the
 * getContentType() method. It is advised to set all the properties at the creation time of the
 * OMOutputFormat and not to change them later.
 */
public class OMOutputFormat {
    private String mimeBoundary = null;
    private String rootContentId = null;
    private int nextid = 0;
    private boolean doOptimize = false;
    private boolean doingSWA = false;
    private boolean isSoap11 = true;

    /** Field DEFAULT_CHAR_SET_ENCODING. Specifies the default character encoding scheme to be used. */
    public static final String DEFAULT_CHAR_SET_ENCODING = "utf-8";

    private String charSetEncoding;
    private String xmlVersion;
    private String contentType;
    private boolean ignoreXMLDeclaration = false;
    private boolean autoCloseWriter = false;


    public OMOutputFormat() {
    }

    public boolean isOptimized() {
        return doOptimize;
    }

    /**
     * Return the content-type value that should be written with the message.
     * (i.e. if optimized, then a multipart/related content-type is returned).
     * @return content-type value
     */
    public String getContentType() {
       
        if (contentType == null) {
            if (isSoap11) {
                contentType = SOAP11Constants.SOAP_11_CONTENT_TYPE;
            } else {
                contentType = SOAP12Constants.SOAP_12_CONTENT_TYPE;
            }
        }
        // If MTOM or SWA, the returned content-type is an 
        // appropriate multipart/related content type.
        if (isOptimized()) {
            return this.getContentTypeForMTOM(contentType);
        } else if (isDoingSWA()) {
            return this.getContentTypeForSwA(contentType);
        } else {
            return contentType;
        }
    }
    
    /**
     * Set a raw content-type 
     * (i.e. "text/xml" (SOAP 1.1) or "application/xml" (REST))
     * If this method is not invoked, OMOutputFormat will choose
     * a content-type value consistent with the soap version.
     * @param c
     */
    public void setContentType(String c) {
        contentType = c;
    }

    public String getMimeBoundary() {
        if (mimeBoundary == null) {
            mimeBoundary =
                    "MIMEBoundary"
                            + UUIDGenerator.getUUID().replace(':', '_');

        }
        return mimeBoundary;
    }

    public String getRootContentId() {
        if (rootContentId == null) {
            rootContentId =
                    "0."
                            + UUIDGenerator.getUUID()
                            + "@apache.org";
        }
        return rootContentId;
    }

    public String getNextContentId() {
        nextid++;
        return nextid
                + "."
                + UUIDGenerator.getUUID()
                + "@apache.org";
    }

    /**
     * Returns the character set encoding scheme. If the value of the charSetEncoding is not set
     * then the default will be returned.
     *
     * @return Returns encoding string.
     */
    public String getCharSetEncoding() {
        return this.charSetEncoding;
    }

    public void setCharSetEncoding(String charSetEncoding) {
        this.charSetEncoding = charSetEncoding;
    }

    public String getXmlVersion() {
        return xmlVersion;
    }

    public void setXmlVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }

    public void setSOAP11(boolean b) {
        isSoap11 = b;
    }

    public boolean isSOAP11() {
        return isSoap11;
    }

    public boolean isIgnoreXMLDeclaration() {
        return ignoreXMLDeclaration;
    }

    public void setIgnoreXMLDeclaration(boolean ignoreXMLDeclaration) {
        this.ignoreXMLDeclaration = ignoreXMLDeclaration;
    }

    public void setDoOptimize(boolean b) {
        doOptimize = b;
    }

    public boolean isDoingSWA() {
        return doingSWA;
    }

    public void setDoingSWA(boolean doingSWA) {
        this.doingSWA = doingSWA;
    }

    public String getContentTypeForMTOM(String SOAPContentType) {
        StringBuffer sb = new StringBuffer();
        sb.append("multipart/related");
        sb.append("; ");
        sb.append("boundary=");
        sb.append(getMimeBoundary());
        sb.append("; ");
        sb.append("type=\"" + MTOMConstants.MTOM_TYPE + "\"");
        sb.append("; ");
        sb.append("start=\"<").append(getRootContentId()).append(">\"");
        sb.append("; ");
        sb.append("start-info=\"").append(SOAPContentType).append("\"");
        return sb.toString();
    }

    public String getContentTypeForSwA(String SOAPContentType) {
        StringBuffer sb = new StringBuffer();
        sb.append("multipart/related");
        sb.append("; ");
        sb.append("boundary=");
        sb.append(getMimeBoundary());
        sb.append("; ");
        sb.append("type=\"").append(SOAPContentType).append("\"");
        sb.append("; ");
        sb.append("start=\"<").append(getRootContentId()).append(">\"");
        return sb.toString();
    }

    public boolean isAutoCloseWriter() {
        return autoCloseWriter;
    }

    public void setAutoCloseWriter(boolean autoCloseWriter) {
        this.autoCloseWriter = autoCloseWriter;
    }

    public void setMimeBoundary(String mimeBoundary) {
        this.mimeBoundary = mimeBoundary;
    }
    public void setRootContentId(String rootContentId) {
		this.rootContentId = rootContentId;
	}
}
