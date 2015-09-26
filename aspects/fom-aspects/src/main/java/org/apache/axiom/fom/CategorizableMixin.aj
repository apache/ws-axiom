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
package org.apache.axiom.fom;

import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;

public aspect CategorizableMixin {
    public final void Categorizable.internalAddCategory(Category category) {
        Element el = category.getParentElement();
        if (el != null && el instanceof Categories) {
            Categories cats = category.getParentElement();
            category = (Category)category.clone();
            try {
                if (category.getScheme() == null && cats.getScheme() != null)
                    category.setScheme(cats.getScheme().toString());
            } catch (Exception e) {
                // Do nothing, shouldn't happen
            }
        }
        coreAppendChild((AbderaCategory)category, false);
    }
}
