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

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Builder for {@link ContentType} objects. This class can be used to construct {@link ContentType}
 * objects or as a mutable alternative to {@link ContentType} (which is designed to be immutable).
 */
public final class ContentTypeBuilder {
    private MediaType mediaType;
    private final LinkedHashMap/*<String,String>*/ parameters = new LinkedHashMap();
    
    /**
     * Constructor that initializes the builder with a media type and no parameters.
     * 
     * @param mediaType
     *            the media type
     */
    public ContentTypeBuilder(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Constructor that initializes the builder with the media type and parameters from an existing
     * {@link ContentType} object.
     * 
     * @param type
     *            the content type
     */
    public ContentTypeBuilder(ContentType type) {
        this(type.getMediaType());
        type.getParameters(parameters);
    }
    
    /**
     * Constructor that parses a <tt>Content-Type</tt> header value.
     * 
     * @param type
     *            the value of the <tt>Content-Type</tt> header conforming to RFC 2045
     * @throws ParseException
     *             if the value is invalid and could not be parsed
     */
    public ContentTypeBuilder(String type) throws ParseException {
        this(new ContentType(type));
    }

    /**
     * Get the media type.
     * 
     * @return the media type
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * Set the media type.
     * 
     * @param mediaType
     *            the media type
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
    
    /**
     * Get the specified parameter value.
     * 
     * @param name
     *            the parameter name
     * @return the parameter value, or <code>null</code> if no parameter with the given name was
     *         found
     */
    public String getParameter(String name) {
        return (String)parameters.get(name.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Set the specified parameter value. If a parameter with the given name already exists, it will
     * be replaced. Note that parameter names are case insensitive.
     * 
     * @param name
     *            the parameter name
     * @param value
     *            the parameter value
     */
    public void setParameter(String name, String value) {
        parameters.put(name.toLowerCase(Locale.ENGLISH), value);
    }

    /**
     * Remove all parameters.
     */
    public void clearParameters() {
        parameters.clear();
    }
    
    /**
     * Build the {@link ContentType} object.
     * 
     * @return the {@link ContentType} object
     */
    public ContentType build() {
        return new ContentType(mediaType, parameters);
    }

    /**
     * Create a string representation of the content type. This method uses the same conventions as
     * {@link ContentType#toString()}.
     * 
     * @return the string representation of this content type
     */
    public String toString() {
        return build().toString();
    }
}
