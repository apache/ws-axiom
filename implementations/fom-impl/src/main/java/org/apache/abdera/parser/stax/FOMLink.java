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
package org.apache.abdera.parser.stax;

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Link;
import org.apache.axiom.fom.AbderaLink;
import org.apache.axiom.om.OMFactory;

public class FOMLink extends FOMExtensibleElement implements AbderaLink {
    protected FOMLink(OMFactory factory) {
        super(factory);
    }

    public IRI getHref() {
        return _getUriValue(getAttributeValue(HREF));
    }

    public IRI getResolvedHref() {
        return _resolve(getResolvedBaseUri(), getHref());
    }

    public Link setHref(String href) {
        if (href != null)
            setAttributeValue(HREF, (new IRI(href)).toString());
        else
            removeAttribute(HREF);
        return this;
    }

    public String getRel() {
        return getAttributeValue(REL);
    }

    public Link setRel(String rel) {
        setAttributeValue(REL, rel);
        return this;
    }

    public MimeType getMimeType() {
        try {
            String type = getAttributeValue(TYPE);
            return (type != null) ? new MimeType(type) : null;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public void setMimeType(MimeType type) {
        setAttributeValue(TYPE, (type != null) ? type.toString() : null);
    }

    public Link setMimeType(String type) {
        try {
            if (type != null)
                setAttributeValue(TYPE, (new MimeType(type)).toString());
            else
                removeAttribute(TYPE);
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
        return this;
    }

    public String getHrefLang() {
        return getAttributeValue(HREFLANG);
    }

    public Link setHrefLang(String lang) {
        if (lang != null)
            setAttributeValue(HREFLANG, lang);
        else
            removeAttribute(HREFLANG);
        return this;
    }

    public String getTitle() {
        return getAttributeValue(ATITLE);
    }

    public Link setTitle(String title) {
        if (title != null)
            setAttributeValue(ATITLE, title);
        else
            removeAttribute(ATITLE);
        return this;
    }

    public long getLength() {
        String l = getAttributeValue(LENGTH);
        return (l != null) ? Long.valueOf(l) : -1;
    }

    public Link setLength(long length) {
        if (length > -1)
            setAttributeValue(LENGTH, (length >= 0) ? String.valueOf(length) : "0");
        else
            removeAttribute(LENGTH);
        return this;
    }

    public String getValue() {
        return getText();
    }

    public void setValue(String value) {
        if (value != null)
            ((Element)this).setText(value);
        else
            _removeAllChildren();
    }

}
