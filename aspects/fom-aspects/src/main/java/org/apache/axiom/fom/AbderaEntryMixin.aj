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
package org.apache.axiom.fom;

import static org.apache.abdera.util.Constants.APP_NS;
import static org.apache.abdera.util.Constants.AUTHOR;
import static org.apache.abdera.util.Constants.CONTENT;
import static org.apache.abdera.util.Constants.CONTRIBUTOR;
import static org.apache.abdera.util.Constants.CONTROL;
import static org.apache.abdera.util.Constants.EDITED;
import static org.apache.abdera.util.Constants.ID;
import static org.apache.abdera.util.Constants.LINK;
import static org.apache.abdera.util.Constants.PRE_RFC_CONTROL;
import static org.apache.abdera.util.Constants.PRE_RFC_EDITED;
import static org.apache.abdera.util.Constants.PUBLISHED;
import static org.apache.abdera.util.Constants.RIGHTS;
import static org.apache.abdera.util.Constants.SOURCE;
import static org.apache.abdera.util.Constants.SUMMARY;
import static org.apache.abdera.util.Constants.TITLE;
import static org.apache.abdera.util.Constants.UPDATED;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRISyntaxException;
import org.apache.abdera.i18n.text.io.InputStreamDataSource;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.axiom.fom.AbderaEntry;
import org.apache.axiom.fom.AbderaLink;
import org.apache.axiom.fom.AbderaPerson;
import org.apache.axiom.fom.LinkUtil;

@SuppressWarnings( {"unchecked", "deprecation"})
public aspect AbderaEntryMixin {
    public final Person AbderaEntry.getAuthor() {
        return (Person)_getFirstChildWithName(AUTHOR);
    }

    public final List<Person> AbderaEntry.getAuthors() {
        return _getChildrenAsSet(AUTHOR);
    }

    public final Entry AbderaEntry.addAuthor(Person person) {
        _addChild((AbderaPerson)person);
        return this;
    }

    public final Person AbderaEntry.addAuthor(String name) {
        Person person = getFactory().newAuthor(this);
        person.setName(name);
        return person;
    }

    public final Person AbderaEntry.addAuthor(String name, String email, String uri) {
        Person person = getFactory().newAuthor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    public final Entry AbderaEntry.addCategory(Category category) {
        internalAddCategory(category);
        return this;
    }

    public final Content AbderaEntry.getContentElement() {
        return (Content)_getFirstChildWithName(CONTENT);
    }

    public final Entry AbderaEntry.setContentElement(Content content) {
        _setChild(CONTENT, content);
        return this;
    }

    /**
     * Sets the content for this entry as @type="text"
     */
    public final Content AbderaEntry.setContent(String value) {
        Content content = getFactory().newContent();
        content.setValue(value);
        setContentElement(content);
        return content;
    }

    public final Content AbderaEntry.setContentAsHtml(String value) {
        return setContent(value, Content.Type.HTML);
    }

    public final Content AbderaEntry.setContentAsXhtml(String value) {
        return setContent(value, Content.Type.XHTML);
    }

    /**
     * Sets the content for this entry
     */
    public final Content AbderaEntry.setContent(String value, Content.Type type) {
        Content content = getFactory().newContent(type);
        content.setValue(value);
        setContentElement(content);
        return content;
    }

    /**
     * Sets the content for this entry
     */
    public final Content AbderaEntry.setContent(Element value) {
        Content content = getFactory().newContent();
        content.setValueElement(value);
        setContentElement(content);
        return content;
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     */
    public final Content AbderaEntry.setContent(Element element, String mediaType) {
        try {
            if (MimeTypeHelper.isText(mediaType))
                throw new IllegalArgumentException();
            Content content = getFactory().newContent(new MimeType(mediaType));
            content.setValueElement(element);
            setContentElement(content);
            return content;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     */
    public final Content AbderaEntry.setContent(DataHandler dataHandler) {
        return setContent(dataHandler, dataHandler.getContentType());
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     */
    public final Content AbderaEntry.setContent(DataHandler dataHandler, String mediatype) {
        if (MimeTypeHelper.isText(mediatype)) {
            try {
                return setContent(dataHandler.getInputStream(), mediatype);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Content content = getFactory().newContent(Content.Type.MEDIA);
            content.setDataHandler(dataHandler);
            if (mediatype != null)
                content.setMimeType(mediatype);
            setContentElement(content);
            return content;
        }
    }

    /**
     * Sets the content for this entry
     */
    public final Content AbderaEntry.setContent(InputStream in) {
        InputStreamDataSource ds = new InputStreamDataSource(in);
        DataHandler dh = new DataHandler(ds);
        Content content = setContent(dh);
        return content;
    }

    /**
     * Sets the content for this entry
     */
    public final Content AbderaEntry.setContent(InputStream in, String mediatype) {
        if (MimeTypeHelper.isText(mediatype)) {
            try {
                StringBuilder buf = new StringBuilder();
                String charset = MimeTypeHelper.getCharset(mediatype);
                Document doc = this.getDocument();
                charset = charset != null ? charset : doc != null ? doc.getCharset() : null;
                charset = charset != null ? charset : "UTF-8";
                InputStreamReader isr = new InputStreamReader(in, charset);
                char[] data = new char[500];
                int r = -1;
                while ((r = isr.read(data)) != -1) {
                    buf.append(data, 0, r);
                }
                return setContent(buf.toString(), mediatype);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            InputStreamDataSource ds = new InputStreamDataSource(in, mediatype);
            DataHandler dh = new DataHandler(ds);
            return setContent(dh, mediatype);
        }
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     */
    public final Content AbderaEntry.setContent(String value, String mediatype) {
        try {
            Content content = getFactory().newContent(new MimeType(mediatype));
            content.setValue(value);
            content.setMimeType(mediatype);
            setContentElement(content);
            return content;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     * @throws IRISyntaxException
     */
    public final Content AbderaEntry.setContent(IRI uri, String mediatype) {
        try {
            Content content = getFactory().newContent(new MimeType(mediatype));
            content.setSrc(uri.toString());
            setContentElement(content);
            return content;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public final List<Person> AbderaEntry.getContributors() {
        return _getChildrenAsSet(CONTRIBUTOR);
    }

    public final Entry AbderaEntry.addContributor(Person person) {
        _addChild((AbderaPerson)person);
        return this;
    }

    public final Person AbderaEntry.addContributor(String name) {
        Person person = getFactory().newContributor(this);
        person.setName(name);
        return person;
    }

    public final Person AbderaEntry.addContributor(String name, String email, String uri) {
        Person person = getFactory().newContributor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    public final IRIElement AbderaEntry.getIdElement() {
        return (IRIElement)_getFirstChildWithName(ID);
    }

    public final Entry AbderaEntry.setIdElement(IRIElement id) {
        _setChild(ID, id);
        return this;
    }

    public final IRI AbderaEntry.getId() {
        IRIElement id = getIdElement();
        return (id != null) ? id.getValue() : null;
    }

    public final IRIElement AbderaEntry.setId(String value) {
        return setId(value, false);
    }

    public final IRIElement AbderaEntry.newId() {
        return setId(this.getFactory().newUuidUri(), false);
    }

    public final IRIElement AbderaEntry.setId(String value, boolean normalize) {
        if (value == null) {
            _removeChildren(ID, false);
            return null;
        }
        IRIElement id = getIdElement();
        if (id != null) {
            if (normalize)
                id.setNormalizedValue(value);
            else
                id.setValue(value);
            return id;
        } else {
            IRIElement iri = getFactory().newID(this);
            iri.setValue((normalize) ? IRI.normalizeString(value) : value);
            return iri;
        }
    }

    public final List<Link> AbderaEntry.getLinks() {
        return _getChildrenAsSet(LINK);
    }

    public final Entry AbderaEntry.addLink(Link link) {
        _addChild((AbderaLink)link);
        return this;
    }

    public final Link AbderaEntry.addLink(String href) {
        return addLink(href, null);
    }

    public final Link AbderaEntry.addLink(String href, String rel) {
        Link link = getFactory().newLink(this);
        link.setHref(href);
        if (rel != null)
            link.setRel(rel);
        return link;
    }

    public final Link AbderaEntry.addLink(String href, String rel, String type, String title, String hreflang, long length) {
        Link link = getFactory().newLink(this);
        link.setHref(href);
        link.setRel(rel);
        link.setMimeType(type);
        link.setTitle(title);
        link.setHrefLang(hreflang);
        link.setLength(length);
        return link;
    }

    public final DateTime AbderaEntry.getPublishedElement() {
        return (DateTime)_getFirstChildWithName(PUBLISHED);
    }

    public final Entry AbderaEntry.setPublishedElement(DateTime dateTime) {
        _setChild(PUBLISHED, dateTime);
        return this;
    }

    public final Date AbderaEntry.getPublished() {
        DateTime dte = getPublishedElement();
        return (dte != null) ? dte.getDate() : null;
    }

    private DateTime AbderaEntry.setPublished(AtomDate value) {
        if (value == null) {
            _removeChildren(PUBLISHED, false);
            return null;
        }
        DateTime dte = getPublishedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        } else {
            DateTime dt = getFactory().newPublished(this);
            dt.setValue(value);
            return dt;
        }
    }

    public final DateTime AbderaEntry.setPublished(Date value) {
        return setPublished((value != null) ? AtomDate.valueOf(value) : null);
    }

    public final DateTime AbderaEntry.setPublished(String value) {
        return setPublished((value != null) ? AtomDate.valueOf(value) : null);
    }

    public final Text AbderaEntry.getRightsElement() {
        return getTextElement(RIGHTS);
    }

    public final Entry AbderaEntry.setRightsElement(Text text) {
        setTextElement(RIGHTS, text, false);
        return this;
    }

    public final Text AbderaEntry.setRights(String value) {
        Text text = getFactory().newRights();
        text.setValue(value);
        setRightsElement(text);
        return text;
    }

    public final Text AbderaEntry.setRightsAsHtml(String value) {
        return setRights(value, Text.Type.HTML);
    }

    public final Text AbderaEntry.setRightsAsXhtml(String value) {
        return setRights(value, Text.Type.XHTML);
    }

    public final Text AbderaEntry.setRights(String value, Text.Type type) {
        Text text = getFactory().newRights(type);
        text.setValue(value);
        setRightsElement(text);
        return text;
    }

    public final Text AbderaEntry.setRights(Div value) {
        Text text = getFactory().newRights(Text.Type.XHTML);
        text.setValueElement(value);
        setRightsElement(text);
        return text;
    }

    public final String AbderaEntry.getRights() {
        return getText(RIGHTS);
    }

    public final Source AbderaEntry.getSource() {
        return (Source)_getFirstChildWithName(SOURCE);
    }

    public final Entry AbderaEntry.setSource(Source source) {
        if (source instanceof Feed)
            source = ((Feed)source).getAsSource();
        _setChild(SOURCE, source);
        return this;
    }

    public final Text AbderaEntry.getSummaryElement() {
        return getTextElement(SUMMARY);
    }

    public final Entry AbderaEntry.setSummaryElement(Text text) {
        setTextElement(SUMMARY, text, false);
        return this;
    }

    public final Text AbderaEntry.setSummary(String value) {
        Text text = getFactory().newSummary();
        text.setValue(value);
        setSummaryElement(text);
        return text;
    }

    public final Text AbderaEntry.setSummaryAsHtml(String value) {
        return setSummary(value, Text.Type.HTML);
    }

    public final Text AbderaEntry.setSummaryAsXhtml(String value) {
        return setSummary(value, Text.Type.XHTML);
    }

    public final Text AbderaEntry.setSummary(String value, Text.Type type) {
        Text text = getFactory().newSummary(type);
        text.setValue(value);
        setSummaryElement(text);
        return text;
    }

    public final Text AbderaEntry.setSummary(Div value) {
        Text text = getFactory().newSummary(Text.Type.XHTML);
        text.setValueElement(value);
        setSummaryElement(text);
        return text;
    }

    public final String AbderaEntry.getSummary() {
        return getText(SUMMARY);
    }

    public final Text AbderaEntry.getTitleElement() {
        return getTextElement(TITLE);
    }

    public final Entry AbderaEntry.setTitleElement(Text title) {
        setTextElement(TITLE, title, false);
        return this;
    }

    public final Text AbderaEntry.setTitle(String value) {
        Text text = getFactory().newTitle();
        text.setValue(value);
        setTitleElement(text);
        return text;
    }

    public final Text AbderaEntry.setTitleAsHtml(String value) {
        return setTitle(value, Text.Type.HTML);
    }

    public final Text AbderaEntry.setTitleAsXhtml(String value) {
        return setTitle(value, Text.Type.XHTML);
    }

    public final Text AbderaEntry.setTitle(String value, Text.Type type) {
        Text text = getFactory().newTitle(type);
        text.setValue(value);
        setTitleElement(text);
        return text;
    }

    public final Text AbderaEntry.setTitle(Div value) {
        Text text = getFactory().newTitle(Text.Type.XHTML);
        text.setValueElement(value);
        setTitleElement(text);
        return text;
    }

    public final String AbderaEntry.getTitle() {
        return getText(TITLE);
    }

    public final DateTime AbderaEntry.getUpdatedElement() {
        return (DateTime)_getFirstChildWithName(UPDATED);
    }

    public final Entry AbderaEntry.setUpdatedElement(DateTime updated) {
        _setChild(UPDATED, updated);
        return this;
    }

    public final Date AbderaEntry.getUpdated() {
        DateTime dte = getUpdatedElement();
        return (dte != null) ? dte.getDate() : null;
    }

    private DateTime AbderaEntry.setUpdated(AtomDate value) {
        if (value == null) {
            _removeChildren(UPDATED, false);
            return null;
        }
        DateTime dte = getUpdatedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        } else {
            DateTime dt = getFactory().newUpdated(this);
            dt.setValue(value);
            return dt;
        }
    }

    public final DateTime AbderaEntry.setUpdated(Date value) {
        return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
    }

    public final DateTime AbderaEntry.setUpdated(String value) {
        return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
    }

    public final DateTime AbderaEntry.getEditedElement() {
        DateTime dt = (DateTime)_getFirstChildWithName(EDITED);
        if (dt == null)
            dt = (DateTime)_getFirstChildWithName(PRE_RFC_EDITED);
        return dt;
    }

    public final void AbderaEntry.setEditedElement(DateTime updated) {
        declareNS(APP_NS, "app");
        _removeChildren(PRE_RFC_EDITED, false);
        _setChild(EDITED, updated);
    }

    public final Date AbderaEntry.getEdited() {
        DateTime dte = getEditedElement();
        return (dte != null) ? dte.getDate() : null;
    }

    private DateTime AbderaEntry.setEdited(AtomDate value) {
        declareNS(APP_NS, "app");
        if (value == null) {
            _removeChildren(PRE_RFC_EDITED, false);
            _removeChildren(EDITED, false);
            return null;
        }
        DateTime dte = getEditedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        } else {
            DateTime dt = getFactory().newEdited(this);
            dt.setValue(value);
            return dt;
        }
    }

    public final DateTime AbderaEntry.setEdited(Date value) {
        return setEdited((value != null) ? AtomDate.valueOf(value) : null);
    }

    public final DateTime AbderaEntry.setEdited(String value) {
        return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
    }

    public final Control AbderaEntry.getControl(boolean create) {
        Control control = getControl();
        if (control == null && create) {
            control = getFactory().newControl();
            setControl(control);
        }
        return control;
    }

    public final Control AbderaEntry.getControl() {
        Control control = (Control)_getFirstChildWithName(CONTROL);
        if (control == null)
            control = (Control)_getFirstChildWithName(PRE_RFC_CONTROL);
        return control;
    }

    public final Entry AbderaEntry.setControl(Control control) {
        _removeChildren(PRE_RFC_CONTROL, true);
        _setChild(CONTROL, control);
        return this;
    }

    public final Link AbderaEntry.getLink(String rel) {
        List<Link> links = getLinks(rel);
        Link link = null;
        if (links.size() > 0)
            link = links.get(0);
        return link;
    }

    public final Link AbderaEntry.getAlternateLink() {
        return getLink(Link.REL_ALTERNATE);
    }

    public final Link AbderaEntry.getEnclosureLink() {
        return getLink(Link.REL_ENCLOSURE);
    }

    public final Link AbderaEntry.getEditLink() {
        return getLink(Link.REL_EDIT);
    }

    public final Link AbderaEntry.getSelfLink() {
        return getLink(Link.REL_SELF);
    }

    public final Link AbderaEntry.getEditMediaLink() {
        return getLink(Link.REL_EDIT_MEDIA);
    }

    public final IRI AbderaEntry.getLinkResolvedHref(String rel) {
        Link link = getLink(rel);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public final IRI AbderaEntry.getAlternateLinkResolvedHref() {
        Link link = getAlternateLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public final IRI AbderaEntry.getEnclosureLinkResolvedHref() {
        Link link = getEnclosureLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public final IRI AbderaEntry.getEditLinkResolvedHref() {
        Link link = getEditLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public final IRI AbderaEntry.getEditMediaLinkResolvedHref() {
        Link link = getEditMediaLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public final IRI AbderaEntry.getSelfLinkResolvedHref() {
        Link link = getSelfLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public final String AbderaEntry.getContent() {
        Content content = getContentElement();
        return (content != null) ? content.getValue() : null;
    }

    public final InputStream AbderaEntry.getContentStream() throws IOException {
        Content content = getContentElement();
        DataHandler dh = content.getDataHandler();
        return dh.getInputStream();
    }

    public final IRI AbderaEntry.getContentSrc() {
        Content content = getContentElement();
        return (content != null) ? content.getResolvedSrc() : null;
    }

    public final Type AbderaEntry.getContentType() {
        Content content = getContentElement();
        return (content != null) ? content.getContentType() : null;
    }

    public final Text.Type AbderaEntry.getRightsType() {
        Text text = getRightsElement();
        return (text != null) ? text.getTextType() : null;
    }

    public final Text.Type AbderaEntry.getSummaryType() {
        Text text = getSummaryElement();
        return (text != null) ? text.getTextType() : null;
    }

    public final Text.Type AbderaEntry.getTitleType() {
        Text text = getTitleElement();
        return (text != null) ? text.getTextType() : null;
    }

    public final MimeType AbderaEntry.getContentMimeType() {
        Content content = getContentElement();
        return (content != null) ? content.getMimeType() : null;
    }

    public final Link AbderaEntry.getAlternateLink(String type, String hreflang) {
        return LinkUtil.selectLink(getLinks(Link.REL_ALTERNATE), type, hreflang);
    }

    public final IRI AbderaEntry.getAlternateLinkResolvedHref(String type, String hreflang) {
        Link link = getAlternateLink(type, hreflang);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public final Link AbderaEntry.getEditMediaLink(String type, String hreflang) {
        return LinkUtil.selectLink(getLinks(Link.REL_EDIT_MEDIA), type, hreflang);
    }

    public final IRI AbderaEntry.getEditMediaLinkResolvedHref(String type, String hreflang) {
        Link link = getEditMediaLink(type, hreflang);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public final Entry AbderaEntry.setDraft(boolean draft) {
        Control control = getControl();
        if (control == null && draft) {
            control = getFactory().newControl(this);
        }
        if (control != null)
            control.setDraft(draft);
        return this;
    }

    /**
     * Returns true if this entry is a draft
     */
    public final boolean AbderaEntry.isDraft() {
        Control control = getControl();
        return (control != null) ? control.isDraft() : false;
    }

    public final Control AbderaEntry.addControl() {
        Control control = getControl();
        if (control == null) {
            control = getFactory().newControl(this);
        }
        return control;
    }
}
