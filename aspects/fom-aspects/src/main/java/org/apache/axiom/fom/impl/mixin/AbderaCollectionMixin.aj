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

import static org.apache.abdera.util.Constants.ACCEPT;
import static org.apache.abdera.util.Constants.CATEGORIES;
import static org.apache.abdera.util.Constants.PREFIXED_TITLE;
import static org.apache.abdera.util.Constants.PRE_RFC_ACCEPT;
import static org.apache.abdera.util.Constants.PRE_RFC_CATEGORIES;
import static org.apache.abdera.util.Constants.TITLE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;

import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.axiom.fom.AbderaCategories;
import org.apache.axiom.fom.AbderaCollection;
import org.apache.axiom.fom.AbderaElement;

@SuppressWarnings("deprecation")
public aspect AbderaCollectionMixin {

    private static final String[] ENTRY = {"application/atom+xml;type=\"entry\""};
    private static final String[] EMPTY = new String[0];

    public final String AbderaCollection.getTitle() {
        Text title = this.getFirstChild(TITLE);
        return (title != null) ? title.getValue() : null;
    }

    private Text AbderaCollection.setTitle(String title, Text.Type type) {
        Text text = getFactory().newText(PREFIXED_TITLE, type);
        text.setValue(title);
        this._setChild(PREFIXED_TITLE, text);
        return text;
    }

    public final Text AbderaCollection.setTitle(String title) {
        return setTitle(title, Text.Type.TEXT);
    }

    public final Text AbderaCollection.setTitleAsHtml(String title) {
        return setTitle(title, Text.Type.HTML);
    }

    public final Text AbderaCollection.setTitleAsXHtml(String title) {
        return setTitle(title, Text.Type.XHTML);
    }

    public final Text AbderaCollection.getTitleElement() {
        return getFirstChild(TITLE);
    }

    public final Collection AbderaCollection.setHref(String href) {
        internalSetHref(href);
        return this;
    }

    public final String[] AbderaCollection.getAccept() {
        List<String> accept = new ArrayList<String>();
        Iterator<AbderaElement> i = _getChildrenWithName(ACCEPT);
        if (i == null || !i.hasNext())
            i = _getChildrenWithName(PRE_RFC_ACCEPT);
        while (i.hasNext()) {
            Element e = i.next();
            String t = e.getText();
            if (t != null) {
                accept.add(t.trim());
            }
        }
        if (accept.size() > 0) {
            String[] list = accept.toArray(new String[accept.size()]);
            return MimeTypeHelper.condense(list);
        } else {
            return EMPTY;
        }
    }

    public final Collection AbderaCollection.setAccept(String mediaRange) {
        return setAccept(new String[] {mediaRange});
    }

    public final Collection AbderaCollection.setAccept(String... mediaRanges) {
        if (mediaRanges != null && mediaRanges.length > 0) {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
            if (mediaRanges.length == 1 && mediaRanges[0].equals("")) {
                addExtension(ACCEPT);
            } else {
                mediaRanges = MimeTypeHelper.condense(mediaRanges);
                for (String type : mediaRanges) {
                    if (type.equalsIgnoreCase("entry")) {
                        addSimpleExtension(ACCEPT, "application/atom+xml;type=entry");
                    } else {
                        try {
                            addSimpleExtension(ACCEPT, new MimeType(type).toString());
                        } catch (javax.activation.MimeTypeParseException e) {
                            throw new org.apache.abdera.util.MimeTypeParseException(e);
                        }
                    }
                }
            }
        } else {
            _removeChildren(ACCEPT, true);
            _removeChildren(PRE_RFC_ACCEPT, true);
        }
        return this;
    }

    public final Collection AbderaCollection.addAccepts(String mediaRange) {
        return addAccepts(new String[] {mediaRange});
    }

    public final Collection AbderaCollection.addAccepts(String... mediaRanges) {
        if (mediaRanges != null) {
            for (String type : mediaRanges) {
                if (!accepts(type)) {
                    try {
                        addSimpleExtension(ACCEPT, new MimeType(type).toString());
                    } catch (Exception e) {
                    }
                }
            }
        }
        return this;
    }

    public final Collection AbderaCollection.addAcceptsEntry() {
        return addAccepts("application/atom+xml;type=entry");
    }

    public final Collection AbderaCollection.setAcceptsEntry() {
        return setAccept("application/atom+xml;type=entry");
    }

    public final Collection AbderaCollection.setAcceptsNothing() {
        return setAccept("");
    }

    public final boolean AbderaCollection.acceptsEntry() {
        return accepts("application/atom+xml;type=entry");
    }

    public final boolean AbderaCollection.acceptsNothing() {
        return accepts("");
    }

    public final boolean AbderaCollection.accepts(String mediaType) {
        String[] accept = getAccept();
        if (accept.length == 0)
            accept = ENTRY;
        for (String a : accept) {
            if (MimeTypeHelper.isMatch(a, mediaType))
                return true;
        }
        return false;
    }

    public final boolean AbderaCollection.accepts(MimeType mediaType) {
        return accepts(mediaType.toString());
    }

    public final Categories AbderaCollection.addCategories() {
        return getFactory().newCategories(this);
    }

    public final Collection AbderaCollection.addCategories(Categories categories) {
        _addChild((AbderaCategories)categories);
        return this;
    }

    public final Categories AbderaCollection.addCategories(String href) {
        Categories cats = getFactory().newCategories();
        cats.setHref(href);
        addCategories(cats);
        return cats;
    }

    public final Categories AbderaCollection.addCategories(List<Category> categories, boolean fixed, String scheme) {
        Categories cats = getFactory().newCategories();
        cats.setFixed(fixed);
        if (scheme != null)
            cats.setScheme(scheme);
        if (categories != null) {
            for (Category category : categories) {
                cats.addCategory(category);
            }
        }
        addCategories(cats);
        return cats;
    }

    public final List<Categories> AbderaCollection.getCategories() {
        List<Categories> list = _getChildrenAsSet(CATEGORIES);
        if (list == null || list.size() == 0)
            list = _getChildrenAsSet(PRE_RFC_CATEGORIES);
        return list;
    }

}
