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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.activation.MimeType;

/**
 * Represents the (parsed) value of a {@code Content-Type} header as defined by
 * <a href="http://tools.ietf.org/html/rfc2045">RFC 2045</a>.
 * <p>
 * The relevant productions from RFC 2045 and RFC 822 are:
 * <pre>
 * content := "Content-Type" ":" type "/" subtype *(";" parameter)
 * parameter := attribute "=" value
 * attribute := token
 * value := token / quoted-string
 * token := 1*&lt;any (US-ASCII) CHAR except SPACE, CTLs, or tspecials&gt;
 * tspecials := "(" / ")" / "&lt;" / "&gt;" / "@" / "," / ";" / ":" / "\" / &lt;"&gt; / "/" / "[" / "]" / "?" / "="
 * quoted-string := &lt;"&gt; *(qtext/quoted-pair) &lt;"&gt;
 * qtext := &lt;any CHAR excepting &lt;"&gt;, "\" &amp; CR, and including linear-white-space&gt;
 * quoted-pair := "\" CHAR
 * </pre>
 * <p>
 * This class is similar to {@link MimeType} and JavaMail's <code>ContentType</code> class, but the
 * following differences exist:
 * <ul>
 * <li>This class is more lenient than (certain implementations of) {@link MimeType}. It will
 * accept content types that are not strictly valid, but that are commonly found. E.g. it will
 * accept content types with an extra semicolon at the end.
 * <li>This class is immutable.
 * <li>This class makes a distinction between a media type (which is defined by a primary type and a
 * sub type and represented by a {@link MediaType} object) and a content type, which is defined by a
 * media type and a set of parameters.
 * </ul>
 * <p>
 * Another reason for the existence of this class is to avoid a dependency on JavaMail. 
 * <p>
 * Note that this class doesn't override {@link Object#equals(Object)} because there is no
 * meaningful way to compare content types with parameters.
 */
public final class ContentType {
    public static final class Builder {
        private MediaType mediaType;
        private final LinkedHashMap<String,String> parameters = new LinkedHashMap<String,String>();
        
        Builder() {}
        
        Builder(ContentType type) {
            mediaType = type.mediaType;
            type.getParameters(parameters);
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
         * @return the builder
         */
        public Builder setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
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
            return parameters.get(name.toLowerCase(Locale.ENGLISH));
        }

        /**
         * Set the specified parameter value. If a parameter with the given name already exists, it will
         * be replaced. If the value is {@code null}, the parameter will be removed.
         * Note that parameter names are case insensitive.
         * 
         * @param name
         *            the parameter name
         * @param value
         *            the parameter value
         * @return the builder
         */
        public Builder setParameter(String name, String value) {
            if (value == null) {
                parameters.remove(name.toLowerCase(Locale.ENGLISH));
            } else {
                parameters.put(name.toLowerCase(Locale.ENGLISH), value);
            }
            return this;
        }

        /**
         * Remove the parameter with the specified name.
         * 
         * @param name
         *            the parameter name
         * @return the builder
         */
        public Builder removeParameter(String name) {
            parameters.remove(name.toLowerCase(Locale.ENGLISH));
            return this;
        }

        /**
         * Remove all parameters.
         * 
         * @return the builder
         */
        public Builder clearParameters() {
            parameters.clear();
            return this;
        }
        
        /**
         * Build the {@link ContentType} object.
         * 
         * @return the {@link ContentType} object
         */
        public ContentType build() {
            return new ContentType(mediaType, parameters);
        }
    }
    
    private final MediaType mediaType;
    private final String[] parameters;
    
    /**
     * Constructor.
     * 
     * @param mediaType
     *            the media type
     * @param parameters
     *            the parameters as name/value pairs (with even entries
     *            representing the parameter names, and odd entries the corresponding values)
     */
    public ContentType(MediaType mediaType, String... parameters) {
        Objects.requireNonNull(mediaType);
        for (String parameter : parameters) {
            Objects.requireNonNull(parameter);
        }
        this.mediaType = mediaType;
        this.parameters = parameters.clone();
    }

    /**
     * Constructor that parses a {@code Content-Type} header value.
     * 
     * @param type
     *            the value of the {@code Content-Type} header conforming to RFC 2045
     * @throws ParseException
     *             if the value is invalid and could not be parsed
     */
    public ContentType(String type) throws ParseException {
        ContentTypeTokenizer tokenizer = new ContentTypeTokenizer(type);
        String primaryType = tokenizer.requireToken();
        tokenizer.require('/');
        String subType = tokenizer.requireToken();
        mediaType = new MediaType(primaryType, subType);
        List<String> parameters = new ArrayList<String>();
        while (tokenizer.expect(';')) {
            String name = tokenizer.expectToken();
            if (name == null) {
                // If we get here, then there was an extra ';' at the end of the value.
                // This is not allowed by RFC 2045, but we are lenient.
                break;
            }
            parameters.add(name);
            tokenizer.require('=');
            parameters.add(tokenizer.requireTokenOrQuotedString());
        }
        this.parameters = parameters.toArray(new String[parameters.size()]);
    }
    
    ContentType(MediaType mediaType, Map<String,String> parameters) {
        this.mediaType = mediaType;
        this.parameters = new String[parameters.size()*2];
        int i = 0;
        for (Map.Entry<String,String> entry : parameters.entrySet()) {
            this.parameters[i++] = entry.getKey();
            this.parameters[i++] = entry.getValue();
        }
    }
    
    /**
     * Get a new builder instance.
     * 
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Get a new builder initialized with the media type and parameters from this instance.
     * 
     * @return the builder
     */
    public Builder toBuilder() {
        return new Builder(this);
    }
    
    /**
     * Get the media type this content type refers to.
     * 
     * @return the media type
     */
    public MediaType getMediaType() {
        return mediaType;
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
        for (int i=0; i<parameters.length; i+=2) {
            if (name.equalsIgnoreCase(parameters[i])) {
                return parameters[i+1];
            }
        }
        return null;
    }
    
    /**
     * Check if the content type is textual, i.e. if an entity with this content type should be
     * human readable. This information may be used to select a content transfer encoding.
     * 
     * @return whether the content type is textual
     */
    public boolean isTextual() {
        return mediaType.hasPrimaryType("text") || mediaType.isXML() || getParameter("charset") != null;
    }
    
    /**
     * Create a string representation of this content type suitable as the value for a
     * {@code Content-Type} header as specified by RFC 2045. Note that this method serializes all
     * parameter values as quoted strings, even values that could be represented as tokens. This is
     * compatible with R1109 in WS-I Basic Profile 1.2 and 2.0.
     * 
     * @return the string representation of this content type
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(mediaType.getPrimaryType());
        buffer.append('/');
        buffer.append(mediaType.getSubType());
        for (int i=0; i<parameters.length; ) {
            buffer.append("; ");
            buffer.append(parameters[i++]);
            buffer.append("=\"");
            String value = parameters[i++];
            for (int j=0, l=value.length(); j<l; j++) {
                char c = value.charAt(j);
                if (c == '"' || c == '\\') {
                    buffer.append('\\');
                }
                buffer.append(c);
            }
            buffer.append('"');
        }
        return buffer.toString();
    }

    void getParameters(Map<String,String> map) {
        for (int i=0; i<parameters.length; i+=2) {
            map.put(parameters[i].toLowerCase(Locale.ENGLISH), parameters[i+1]);
        }
    }
}
