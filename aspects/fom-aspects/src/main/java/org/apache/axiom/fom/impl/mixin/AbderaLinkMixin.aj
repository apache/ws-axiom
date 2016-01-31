/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.axiom.fom.impl.mixin;

import static org.apache.abdera.util.Constants.ATITLE;
import static org.apache.abdera.util.Constants.HREFLANG;
import static org.apache.abdera.util.Constants.LENGTH;
import static org.apache.abdera.util.Constants.REL;
import static org.apache.abdera.util.Constants.TYPE;

import javax.activation.MimeType;

import org.apache.abdera.model.Link;
import org.apache.axiom.fom.AbderaLink;

public aspect AbderaLinkMixin {
    public final Link AbderaLink.setHref(String href) {
        internalSetHref(href);
        return this;
    }

    public final String AbderaLink.getRel() {
        return getAttributeValue(REL);
    }

    public final Link AbderaLink.setRel(String rel) {
        setAttributeValue(REL, rel);
        return this;
    }

    public final MimeType AbderaLink.getMimeType() {
        try {
            String type = getAttributeValue(TYPE);
            return (type != null) ? new MimeType(type) : null;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public final Link AbderaLink.setMimeType(String type) {
        try {
            setAttributeValue(TYPE, type == null ? null : (new MimeType(type)).toString());
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
        return this;
    }

    public final String AbderaLink.getHrefLang() {
        return getAttributeValue(HREFLANG);
    }

    public final Link AbderaLink.setHrefLang(String lang) {
        setAttributeValue(HREFLANG, lang);
        return this;
    }

    public final String AbderaLink.getTitle() {
        return getAttributeValue(ATITLE);
    }

    public final Link AbderaLink.setTitle(String title) {
        setAttributeValue(ATITLE, title);
        return this;
    }

    public final long AbderaLink.getLength() {
        String l = getAttributeValue(LENGTH);
        return (l != null) ? Long.valueOf(l) : -1;
    }

    public final Link AbderaLink.setLength(long length) {
        if (length > -1)
            setAttributeValue(LENGTH, (length >= 0) ? String.valueOf(length) : "0");
        else
            removeAttribute(LENGTH);
        return this;
    }
}
