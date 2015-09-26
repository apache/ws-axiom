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

import static org.apache.abdera.util.Constants.FIXED;
import static org.apache.abdera.util.Constants.SCHEME;
import static org.apache.abdera.util.Constants.YES;

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;

public aspect AbderaCategoriesMixin {
    public final Categories AbderaCategories.addCategory(Category category) {
        coreAppendChild((AbderaCategory)category, false);
        return this;
    }

    private List<Category> AbderaCategories.copyCategoriesWithScheme(List<Category> cats) {
        List<Category> newcats = new ArrayList<Category>();
        IRI scheme = getScheme();
        for (Category cat : cats) {
            Category newcat = (Category)cat.clone();
            if (newcat.getScheme() == null && scheme != null)
                newcat.setScheme(scheme.toString());
            newcats.add(newcat);
        }
        return newcats;
    }

    public final List<Category> AbderaCategories.getCategoriesWithScheme() {
        return copyCategoriesWithScheme(getCategories());
    }

    public final List<Category> AbderaCategories.getCategoriesWithScheme(String scheme) {
        return copyCategoriesWithScheme(getCategories(scheme));
    }

    public final IRI AbderaCategories.getScheme() {
        String value = getAttributeValue(SCHEME);
        return (value != null) ? new IRI(value) : null;
    }

    public final boolean AbderaCategories.isFixed() {
        String value = getAttributeValue(FIXED);
        return (value != null && value.equals(YES));
    }

    public final Categories AbderaCategories.setFixed(boolean fixed) {
        if (fixed && !isFixed())
            setAttributeValue(FIXED, YES);
        else if (!fixed && isFixed())
            removeAttribute(FIXED);
        return this;
    }

    public final Categories AbderaCategories.setScheme(String scheme) {
        setAttributeValue(SCHEME, IRIUtil.normalize(scheme));
        return this;
    }

    public final Categories AbderaCategories.setHref(String href) {
        internalSetHref(href);
        return this;
    }

    public final boolean AbderaCategories.contains(String term) {
        return contains(term, null);
    }

    public final boolean AbderaCategories.contains(String term, String scheme) {
        List<Category> categories = getCategories();
        IRI catscheme = getScheme();
        IRI uri = (scheme != null) ? new IRI(scheme) : catscheme;
        for (Category category : categories) {
            String t = category.getTerm();
            IRI s = (category.getScheme() != null) ? category.getScheme() : catscheme;
            if (t.equals(term) && ((uri != null) ? uri.equals(s) : s == null))
                return true;
        }
        return false;
    }

    public final boolean AbderaCategories.isOutOfLine() {
        boolean answer = false;
        try {
            answer = getHref() != null;
        } catch (Exception e) {
        }
        return answer;
    }

}
