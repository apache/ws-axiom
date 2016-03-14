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
package org.apache.axiom.fom.impl.mixin;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.util.Constants;
import org.apache.axiom.core.Axis;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.core.Mapper;
import org.apache.axiom.fom.AbderaCategory;
import org.apache.axiom.fom.CategoryContainer;
import org.apache.axiom.fom.FOMList;
import org.apache.axiom.fom.FOMSemantics;

public aspect CategoryContainerMixin {
    private static final ElementMatcher<AbderaCategory> CATEGORY_BY_SCHEME = new ElementMatcher<AbderaCategory>() {
        public boolean matches(AbderaCategory element, String namespaceURI, String name) {
            String scheme = element.getAttributeValue(Constants.SCHEME);
            return scheme == null && name == null || scheme != null && scheme.equals(name);
        }
    };

    public final Category CategoryContainer.addCategory(String term) {
        Category category = getFactory().newCategory(this);
        category.setTerm(term);
        return category;
    }

    public final Category CategoryContainer.addCategory(String scheme, String term, String label) {
        Category category = getFactory().newCategory(this);
        category.setTerm(term);
        category.setScheme(scheme);
        category.setLabel(label);
        return category;
    }

    public final List<Category> CategoryContainer.getCategories() {
        return _getChildrenAsSet(Constants.CATEGORY);
    }

    private static final Mapper<Category,AbderaCategory> categoryMapper = new Mapper<Category,AbderaCategory>() {
        public Category map(AbderaCategory category) {
            return category;
        }
    };
    
    public final List<Category> CategoryContainer.getCategories(String scheme) {
        // TODO: we should probably set detachPolicy to null
        return new FOMList<Category>(coreGetElements(
                Axis.CHILDREN, AbderaCategory.class, CATEGORY_BY_SCHEME, null, scheme,
                categoryMapper, FOMSemantics.INSTANCE));
    }
}
