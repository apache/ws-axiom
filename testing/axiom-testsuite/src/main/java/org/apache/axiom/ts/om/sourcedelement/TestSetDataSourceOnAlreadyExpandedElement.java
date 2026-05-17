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
package org.apache.axiom.ts.om.sourcedelement;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import junit.framework.TestCase;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

@SuppressWarnings("deprecation")
public class TestSetDataSourceOnAlreadyExpandedElement extends TestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {
        OMSourcedElement element = TestDocument.DOCUMENT1.createOMSourcedElement(factory, false, true);
        // Make sure the OMSourcedElement is expanded
        element.getFirstOMChild();
        assertThat(element.isExpanded()).isTrue();
        // Now set a new data source
        element.setDataSource(new PullOMDataSource(TestDocument.DOCUMENT2.getContent()));
        assertThat(element.isExpanded()).isFalse();
        // getNextOMSibling should not expand the element
        assertThat(element.getNextOMSibling()).isNull();
        assertThat(element.isExpanded()).isFalse();
    }
}
