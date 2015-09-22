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

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.axiom.fom.AbderaCategory;
import org.apache.axiom.fom.IRIUtil;

public class FOMCategory extends FOMExtensibleElement implements AbderaCategory {
    public String getTerm() {
        return getAttributeValue(TERM);
    }

    public Category setTerm(String term) {
        setAttributeValue(TERM, term);
        return this;
    }

    public IRI getScheme() {
        return IRIUtil.getUriValue(getAttributeValue(SCHEME));
    }

    public Category setScheme(String scheme) {
        setAttributeValue(SCHEME, IRIUtil.normalize(scheme));
        return this;
    }

    public String getLabel() {
        return getAttributeValue(LABEL);
    }

    public Category setLabel(String label) {
        setAttributeValue(LABEL, label);
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
