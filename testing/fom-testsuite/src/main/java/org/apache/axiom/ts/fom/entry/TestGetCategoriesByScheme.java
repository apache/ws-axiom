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

import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.axiom.ts.fom.AbderaTestCase;

public class TestGetCategoriesByScheme extends AbderaTestCase {
    public TestGetCategoriesByScheme(Abdera abdera) {
        super(abdera);
    }

    @Override
    protected void runTest() throws Throwable {
        Document<Entry> document = abdera.getParser().parse(
                TestGetCategoriesByScheme.class.getResourceAsStream("entry-with-categories.xml"));
        List<Category> categories = document.getRoot().getCategories("http://www.example.org/");
        assertThat(categories).hasSize(2);
        assertThat(categories.get(0).getTerm()).isEqualTo("term1");
        assertThat(categories.get(1).getTerm()).isEqualTo("term2");
    }
}
