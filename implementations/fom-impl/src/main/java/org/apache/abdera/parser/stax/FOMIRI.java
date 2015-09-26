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
import org.apache.abdera.model.IRIElement;
import org.apache.axiom.fom.AbderaIRIElement;
import org.apache.axiom.fom.IRIUtil;

public class FOMIRI extends FOMElement implements AbderaIRIElement {
    public IRI getValue() {
        return IRIUtil.getUriValue(getText());
    }

    public IRIElement setValue(String iri) {
        setText(IRIUtil.normalize(iri));
        return this;
    }

    public IRI getResolvedValue() {
        return IRIUtil.resolve(getResolvedBaseUri(), getValue());
    }

    public IRIElement setNormalizedValue(String uri) {
        if (uri != null)
            setValue(IRI.normalizeString(uri));
        else
            setValue(null);
        return this;
    }
}
