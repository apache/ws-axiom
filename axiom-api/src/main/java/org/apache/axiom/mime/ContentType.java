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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.MimeType;

/**
 * Represents the (parsed) value of a <tt>Content-Type</tt> header as defined by
 * <a href="http://tools.ietf.org/html/rfc2045">RFC 2045</a>.
 * <p>
 * The relevant productions from RFC 2045 and RFC 822 are:
 * <pre>
 * content := "Content-Type" ":" type "/" subtype *(";" parameter)
 * parameter := attribute "=" value
 * attribute := token
 * value := token / quoted-string
 * token := 1*&lt;any (US-ASCII) CHAR except SPACE, CTLs, or tspecials>
 * tspecials := "(" / ")" / "&lt;" / ">" / "@" / "," / ";" / ":" / "\" / &lt;"> / "/" / "[" / "]" / "?" / "="
 * quoted-string := &lt;"> *(qtext/quoted-pair) &lt;">
 * qtext := &lt;any CHAR excepting &lt;">, "\" & CR, and including linear-white-space>
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
// TODO: this class should override toString, but we don't need it yet...
public final class ContentType {
    private final MediaType mediaType;
    private final String[] parameters;
    
    /**
     * Constructor.
     * 
     * @param mediaType
     *            the media type
     * @param parameters
     *            an array specifying the parameters as name/value pairs (with even entries
     *            representing the parameter names, and odd entries the corresponding values)
     */
    public ContentType(MediaType mediaType, String[] parameters) {
        this.mediaType = mediaType;
        this.parameters = (String[])parameters.clone();
    }

    /**
     * Constructor that parses a <tt>Content-Type</tt> header value.
     * 
     * @param type
     *            the value of the <tt>Content-Type</tt> header conforming to RFC 2045
     * @throws ParseException
     *             if the value is invalid and could not be parsed
     */
    public ContentType(String type) throws ParseException {
        ContentTypeTokenizer tokenizer = new ContentTypeTokenizer(type);
        String primaryType = tokenizer.requireToken();
        tokenizer.require('/');
        String subType = tokenizer.requireToken();
        mediaType = new MediaType(primaryType, subType);
        List parameters = new ArrayList();
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
        this.parameters = (String[])parameters.toArray(new String[parameters.size()]);
    }
    
    ContentType(MediaType mediaType, Map parameters) {
        this.mediaType = mediaType;
        this.parameters = new String[parameters.size()*2];
        int i = 0;
        for (Iterator it = parameters.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            this.parameters[i++] = (String)entry.getKey();
            this.parameters[i++] = (String)entry.getValue();
        }
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
     * Create a string representation of this content type suitable as the value for a
     * <tt>Content-Type</tt> header as specified by RFC 2045. Note that this method serializes all
     * parameter values as quoted strings, even values that could be represented as tokens. This is
     * compatible with R1109 in WS-I Basic Profile 1.2 and 2.0.
     * 
     * @return the string representation of this content type
     */
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

    void getParameters(Map map) {
        for (int i=0; i<parameters.length; i+=2) {
            map.put(parameters[i].toLowerCase(Locale.ENGLISH), parameters[i+1]);
        }
    }
}
