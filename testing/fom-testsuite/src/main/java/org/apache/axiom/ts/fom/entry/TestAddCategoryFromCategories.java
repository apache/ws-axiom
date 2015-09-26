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
package org.apache.axiom.ts.fom.entry;

import static com.google.common.truth.Truth.assertThat;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.axiom.ts.fom.AbderaTestCase;

public class TestAddCategoryFromCategories extends AbderaTestCase {
    public TestAddCategoryFromCategories(Abdera abdera) {
        super(abdera);
    }

    @Override
    protected void runTest() throws Throwable {
        Document<Categories> document = abdera.getParser().parse(
                TestAddCategoryFromCategories.class.getResourceAsStream("categories.xml"));
        Categories categories = document.getRoot();
        Entry entry = abdera.getFactory().newEntry();
        Category orgCategory = categories.getCategories().get(0);
        entry.addCategory(orgCategory);
        Category category = entry.getCategories().get(0);
        assertThat(category).isNotSameAs(orgCategory);
        // Assert that the original Category has not been removed from the Categories instance
        assertThat(categories.getCategories().get(0)).isSameAs(orgCategory);
        assertThat(category.getTerm()).isEqualTo(orgCategory.getTerm());
        // The scheme is inherited from the app:categories element
        assertThat(category.getScheme()).isEqualTo(categories.getScheme());
    }
}
