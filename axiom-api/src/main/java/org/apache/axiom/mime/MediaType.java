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
package org.apache.axiom.mime;

/**
 * Represents a media type as defined by <a href="http://tools.ietf.org/html/rfc2045">RFC 2045</a>
 * and <a href="http://tools.ietf.org/html/rfc2046">RFC 2046</a>. It specifies a primary type (e.g.
 * <tt>text</tt>) and a subtype (e.g. <tt>xml</tt>). Note that in RFC 2045, "media type" refers to
 * what is called "primary type" here, while this class uses "media type" to refer to the
 * combination of primary and subtype.
 * <p>
 * This class is immutable. It overrides {@link Object#equals(Object)} and {@link Object#hashCode()}
 * to allow comparing media types as described by RFC 2045, i.e. in a case insensitive way.
 */
public final class MediaType {
    /**
     * The media type for <tt>text/xml</tt>.
     */
    public static final MediaType TEXT_XML = new MediaType("text", "xml");
    
    /**
     * The media type for <tt>application/xml</tt>.
     */
    public static final MediaType APPLICATION_XML = new MediaType("application", "xml");
    
    /**
     * The media type for <tt>application/soap+xml</tt>.
     */
    public static final MediaType APPLICATION_SOAP_XML = new MediaType("application", "soap+xml");
    
    /**
     * The media type for <tt>application/xop+xml</tt>.
     */
    public static final MediaType APPLICATION_XOP_XML = new MediaType("application", "xop+xml");
    
    /**
     * The media type for <tt>multipart/related</tt>.
     */
    public static final MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    
    private final String primaryType;
    private final String subType;
    
    /**
     * Constructor.
     * 
     * @param primaryType
     *            the primary type
     * @param subType
     *            the subtype
     */
    public MediaType(String primaryType, String subType) {
        this.primaryType = primaryType;
        this.subType = subType;
    }

    /**
     * Get the primary type.
     * 
     * @return the primary type
     */
    public String getPrimaryType() {
        return primaryType;
    }

    /**
     * Get the subtype.
     * 
     * @return the subtype
     */
    public String getSubType() {
        return subType;
    }

    public int hashCode() {
        int hash = 0;
        for (int i=0, l=primaryType.length(); i<l; i++) {
            hash = 31*hash + Character.toLowerCase(primaryType.charAt(i));
        }
        hash *= 31;
        for (int i=0, l=subType.length(); i<l; i++) {
            hash = 31*hash + Character.toLowerCase(subType.charAt(i));
        }
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MediaType) {
            MediaType other = (MediaType)obj;
            return primaryType.equalsIgnoreCase(other.primaryType) && subType.equalsIgnoreCase(other.subType);
        } else {
            return false;
        }
    }

    public String toString() {
        return primaryType + "/" + subType;
    }
}
