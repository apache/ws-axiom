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

import static org.apache.abdera.util.Constants.DRAFT;
import static org.apache.abdera.util.Constants.NO;
import static org.apache.abdera.util.Constants.PRE_RFC_DRAFT;
import static org.apache.abdera.util.Constants.YES;

import org.apache.abdera.model.Control;
import org.apache.axiom.fom.AbderaControl;

@SuppressWarnings("deprecation")
public aspect AbderaControlMixin {
    public final boolean AbderaControl.isDraft() {
        String value = _getElementValue(DRAFT);
        if (value == null)
            value = _getElementValue(PRE_RFC_DRAFT);
        return (value != null && YES.equalsIgnoreCase(value));
    }

    public final Control AbderaControl.setDraft(boolean draft) {
        _removeChildren(PRE_RFC_DRAFT, true);
        _setElementValue(DRAFT, (draft) ? YES : NO);
        return this;
    }

    public final Control AbderaControl.unsetDraft() {
        _removeChildren(PRE_RFC_DRAFT, true);
        _removeChildren(DRAFT, true);
        return this;
    }

}
