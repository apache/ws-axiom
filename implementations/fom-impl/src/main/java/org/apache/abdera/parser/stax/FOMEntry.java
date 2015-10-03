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
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.axiom.fom.AbderaEntry;
import org.apache.axiom.fom.AbderaLink;
import org.apache.axiom.fom.AbderaPerson;

@SuppressWarnings( {"unchecked", "deprecation"})
public class FOMEntry extends FOMExtensibleElement implements AbderaEntry {
    public Person getAuthor() {
        return (Person)_getFirstChildWithName(AUTHOR);
    }

    public List<Person> getAuthors() {
        return _getChildrenAsSet(AUTHOR);
    }

    public Entry addAuthor(Person person) {
        _addChild((AbderaPerson)person);
        return this;
    }

    public Person addAuthor(String name) {
        Person person = getFactory().newAuthor(this);
        person.setName(name);
        return person;
    }

    public Person addAuthor(String name, String email, String uri) {
        Person person = getFactory().newAuthor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    public Entry addCategory(Category category) {
        internalAddCategory(category);
        return this;
    }

    public Content getContentElement() {
        return (Content)_getFirstChildWithName(CONTENT);
    }

    public Entry setContentElement(Content content) {
        _setChild(CONTENT, content);
        return this;
    }

    /**
     * Sets the content for this entry as @type="text"
     */
    public Content setContent(String value) {
        Content content = getFactory().newContent();
        content.setValue(value);
        setContentElement(content);
        return content;
    }

    public Content setContentAsHtml(String value) {
        return setContent(value, Content.Type.HTML);
    }

    public Content setContentAsXhtml(String value) {
        return setContent(value, Content.Type.XHTML);
    }

    /**
     * Sets the content for this entry
     */
    public Content setContent(String value, Content.Type type) {
        Content content = getFactory().newContent(type);
        content.setValue(value);
        setContentElement(content);
        return content;
    }

    /**
     * Sets the content for this entry
     */
    public Content setContent(Element value) {
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
    public Content setContent(Element element, String mediaType) {
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
    public Content setContent(DataHandler dataHandler) {
        return setContent(dataHandler, dataHandler.getContentType());
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     */
    public Content setContent(DataHandler dataHandler, String mediatype) {
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
    public Content setContent(InputStream in) {
        InputStreamDataSource ds = new InputStreamDataSource(in);
        DataHandler dh = new DataHandler(ds);
        Content content = setContent(dh);
        return content;
    }

    /**
     * Sets the content for this entry
     */
    public Content setContent(InputStream in, String mediatype) {
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
    public Content setContent(String value, String mediatype) {
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
    public Content setContent(IRI uri, String mediatype) {
        try {
            Content content = getFactory().newContent(new MimeType(mediatype));
            content.setSrc(uri.toString());
            setContentElement(content);
            return content;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public List<Person> getContributors() {
        return _getChildrenAsSet(CONTRIBUTOR);
    }

    public Entry addContributor(Person person) {
        _addChild((AbderaPerson)person);
        return this;
    }

    public Person addContributor(String name) {
        Person person = getFactory().newContributor(this);
        person.setName(name);
        return person;
    }

    public Person addContributor(String name, String email, String uri) {
        Person person = getFactory().newContributor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    public IRIElement getIdElement() {
        return (IRIElement)_getFirstChildWithName(ID);
    }

    public Entry setIdElement(IRIElement id) {
        _setChild(ID, id);
        return this;
    }

    public IRI getId() {
        IRIElement id = getIdElement();
        return (id != null) ? id.getValue() : null;
    }

    public IRIElement setId(String value) {
        return setId(value, false);
    }

    public IRIElement newId() {
        return setId(this.getFactory().newUuidUri(), false);
    }

    public IRIElement setId(String value, boolean normalize) {
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

    public List<Link> getLinks() {
        return _getChildrenAsSet(LINK);
    }

    public List<Link> getLinks(String rel) {
        return FOMHelper.getLinks(this, rel);
    }

    public List<Link> getLinks(String... rels) {
        return FOMHelper.getLinks(this, rels);
    }

    public Entry addLink(Link link) {
        _addChild((AbderaLink)link);
        return this;
    }

    public Link addLink(String href) {
        return addLink(href, null);
    }

    public Link addLink(String href, String rel) {
        Link link = getFactory().newLink(this);
        link.setHref(href);
        if (rel != null)
            link.setRel(rel);
        return link;
    }

    public Link addLink(String href, String rel, String type, String title, String hreflang, long length) {
        Link link = getFactory().newLink(this);
        link.setHref(href);
        link.setRel(rel);
        link.setMimeType(type);
        link.setTitle(title);
        link.setHrefLang(hreflang);
        link.setLength(length);
        return link;
    }

    public DateTime getPublishedElement() {
        return (DateTime)_getFirstChildWithName(PUBLISHED);
    }

    public Entry setPublishedElement(DateTime dateTime) {
        _setChild(PUBLISHED, dateTime);
        return this;
    }

    public Date getPublished() {
        DateTime dte = getPublishedElement();
        return (dte != null) ? dte.getDate() : null;
    }

    private DateTime setPublished(AtomDate value) {
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

    public DateTime setPublished(Date value) {
        return setPublished((value != null) ? AtomDate.valueOf(value) : null);
    }

    public DateTime setPublished(String value) {
        return setPublished((value != null) ? AtomDate.valueOf(value) : null);
    }

    public Text getRightsElement() {
        return getTextElement(RIGHTS);
    }

    public Entry setRightsElement(Text text) {
        setTextElement(RIGHTS, text, false);
        return this;
    }

    public Text setRights(String value) {
        Text text = getFactory().newRights();
        text.setValue(value);
        setRightsElement(text);
        return text;
    }

    public Text setRightsAsHtml(String value) {
        return setRights(value, Text.Type.HTML);
    }

    public Text setRightsAsXhtml(String value) {
        return setRights(value, Text.Type.XHTML);
    }

    public Text setRights(String value, Text.Type type) {
        Text text = getFactory().newRights(type);
        text.setValue(value);
        setRightsElement(text);
        return text;
    }

    public Text setRights(Div value) {
        Text text = getFactory().newRights(Text.Type.XHTML);
        text.setValueElement(value);
        setRightsElement(text);
        return text;
    }

    public String getRights() {
        return getText(RIGHTS);
    }

    public Source getSource() {
        return (Source)_getFirstChildWithName(SOURCE);
    }

    public Entry setSource(Source source) {
        if (source instanceof Feed)
            source = ((Feed)source).getAsSource();
        _setChild(SOURCE, source);
        return this;
    }

    public Text getSummaryElement() {
        return getTextElement(SUMMARY);
    }

    public Entry setSummaryElement(Text text) {
        setTextElement(SUMMARY, text, false);
        return this;
    }

    public Text setSummary(String value) {
        Text text = getFactory().newSummary();
        text.setValue(value);
        setSummaryElement(text);
        return text;
    }

    public Text setSummaryAsHtml(String value) {
        return setSummary(value, Text.Type.HTML);
    }

    public Text setSummaryAsXhtml(String value) {
        return setSummary(value, Text.Type.XHTML);
    }

    public Text setSummary(String value, Text.Type type) {
        Text text = getFactory().newSummary(type);
        text.setValue(value);
        setSummaryElement(text);
        return text;
    }

    public Text setSummary(Div value) {
        Text text = getFactory().newSummary(Text.Type.XHTML);
        text.setValueElement(value);
        setSummaryElement(text);
        return text;
    }

    public String getSummary() {
        return getText(SUMMARY);
    }

    public Text getTitleElement() {
        return getTextElement(TITLE);
    }

    public Entry setTitleElement(Text title) {
        setTextElement(TITLE, title, false);
        return this;
    }

    public Text setTitle(String value) {
        Text text = getFactory().newTitle();
        text.setValue(value);
        setTitleElement(text);
        return text;
    }

    public Text setTitleAsHtml(String value) {
        return setTitle(value, Text.Type.HTML);
    }

    public Text setTitleAsXhtml(String value) {
        return setTitle(value, Text.Type.XHTML);
    }

    public Text setTitle(String value, Text.Type type) {
        Text text = getFactory().newTitle(type);
        text.setValue(value);
        setTitleElement(text);
        return text;
    }

    public Text setTitle(Div value) {
        Text text = getFactory().newTitle(Text.Type.XHTML);
        text.setValueElement(value);
        setTitleElement(text);
        return text;
    }

    public String getTitle() {
        return getText(TITLE);
    }

    public DateTime getUpdatedElement() {
        return (DateTime)_getFirstChildWithName(UPDATED);
    }

    public Entry setUpdatedElement(DateTime updated) {
        _setChild(UPDATED, updated);
        return this;
    }

    public Date getUpdated() {
        DateTime dte = getUpdatedElement();
        return (dte != null) ? dte.getDate() : null;
    }

    private DateTime setUpdated(AtomDate value) {
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

    public DateTime setUpdated(Date value) {
        return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
    }

    public DateTime setUpdated(String value) {
        return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
    }

    public DateTime getEditedElement() {
        DateTime dt = (DateTime)_getFirstChildWithName(EDITED);
        if (dt == null)
            dt = (DateTime)_getFirstChildWithName(PRE_RFC_EDITED);
        return dt;
    }

    public void setEditedElement(DateTime updated) {
        declareNamespace(APP_NS, "app");
        _removeChildren(PRE_RFC_EDITED, false);
        _setChild(EDITED, updated);
    }

    public Date getEdited() {
        DateTime dte = getEditedElement();
        return (dte != null) ? dte.getDate() : null;
    }

    private DateTime setEdited(AtomDate value) {
        declareNamespace(APP_NS, "app");
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

    public DateTime setEdited(Date value) {
        return setEdited((value != null) ? AtomDate.valueOf(value) : null);
    }

    public DateTime setEdited(String value) {
        return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
    }

    public Control getControl(boolean create) {
        Control control = getControl();
        if (control == null && create) {
            control = getFactory().newControl();
            setControl(control);
        }
        return control;
    }

    public Control getControl() {
        Control control = (Control)_getFirstChildWithName(CONTROL);
        if (control == null)
            control = (Control)_getFirstChildWithName(PRE_RFC_CONTROL);
        return control;
    }

    public Entry setControl(Control control) {
        _removeChildren(PRE_RFC_CONTROL, true);
        _setChild(CONTROL, control);
        return this;
    }

    public Link getLink(String rel) {
        List<Link> links = getLinks(rel);
        Link link = null;
        if (links.size() > 0)
            link = links.get(0);
        return link;
    }

    public Link getAlternateLink() {
        return getLink(Link.REL_ALTERNATE);
    }

    public Link getEnclosureLink() {
        return getLink(Link.REL_ENCLOSURE);
    }

    public Link getEditLink() {
        return getLink(Link.REL_EDIT);
    }

    public Link getSelfLink() {
        return getLink(Link.REL_SELF);
    }

    public Link getEditMediaLink() {
        return getLink(Link.REL_EDIT_MEDIA);
    }

    public IRI getLinkResolvedHref(String rel) {
        Link link = getLink(rel);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getAlternateLinkResolvedHref() {
        Link link = getAlternateLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getEnclosureLinkResolvedHref() {
        Link link = getEnclosureLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getEditLinkResolvedHref() {
        Link link = getEditLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getEditMediaLinkResolvedHref() {
        Link link = getEditMediaLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getSelfLinkResolvedHref() {
        Link link = getSelfLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public String getContent() {
        Content content = getContentElement();
        return (content != null) ? content.getValue() : null;
    }

    public InputStream getContentStream() throws IOException {
        Content content = getContentElement();
        DataHandler dh = content.getDataHandler();
        return dh.getInputStream();
    }

    public IRI getContentSrc() {
        Content content = getContentElement();
        return (content != null) ? content.getResolvedSrc() : null;
    }

    public Type getContentType() {
        Content content = getContentElement();
        return (content != null) ? content.getContentType() : null;
    }

    public Text.Type getRightsType() {
        Text text = getRightsElement();
        return (text != null) ? text.getTextType() : null;
    }

    public Text.Type getSummaryType() {
        Text text = getSummaryElement();
        return (text != null) ? text.getTextType() : null;
    }

    public Text.Type getTitleType() {
        Text text = getTitleElement();
        return (text != null) ? text.getTextType() : null;
    }

    public MimeType getContentMimeType() {
        Content content = getContentElement();
        return (content != null) ? content.getMimeType() : null;
    }

    public Link getAlternateLink(String type, String hreflang) {
        return selectLink(getLinks(Link.REL_ALTERNATE), type, hreflang);
    }

    public IRI getAlternateLinkResolvedHref(String type, String hreflang) {
        Link link = getAlternateLink(type, hreflang);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public Link getEditMediaLink(String type, String hreflang) {
        return selectLink(getLinks(Link.REL_EDIT_MEDIA), type, hreflang);
    }

    public IRI getEditMediaLinkResolvedHref(String type, String hreflang) {
        Link link = getEditMediaLink(type, hreflang);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public Entry setDraft(boolean draft) {
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
    public boolean isDraft() {
        Control control = getControl();
        return (control != null) ? control.isDraft() : false;
    }

    public Control addControl() {
        Control control = getControl();
        if (control == null) {
            control = getFactory().newControl(this);
        }
        return control;
    }
}
