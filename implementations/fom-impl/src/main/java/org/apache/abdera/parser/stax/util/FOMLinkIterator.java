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
package org.apache.abdera.parser.stax.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Link;

public class FOMLinkIterator extends FOMElementIterator {
    private static final Map<String, String> REL_EQUIVS = new HashMap<String, String>();
    static {
        REL_EQUIVS.put(Link.REL_ALTERNATE_IANA, Link.REL_ALTERNATE);
        REL_EQUIVS.put(Link.REL_CURRENT_IANA, Link.REL_CURRENT);
        REL_EQUIVS.put(Link.REL_ENCLOSURE_IANA, Link.REL_ENCLOSURE);
        REL_EQUIVS.put(Link.REL_FIRST_IANA, Link.REL_FIRST);
        REL_EQUIVS.put(Link.REL_LAST_IANA, Link.REL_LAST);
        REL_EQUIVS.put(Link.REL_NEXT_IANA, Link.REL_NEXT);
        REL_EQUIVS.put(Link.REL_PAYMENT_IANA, Link.REL_PAYMENT);
        REL_EQUIVS.put(Link.REL_PREVIOUS_IANA, Link.REL_PREVIOUS);
        REL_EQUIVS.put(Link.REL_RELATED_IANA, Link.REL_RELATED);
        REL_EQUIVS.put(Link.REL_SELF_IANA, Link.REL_SELF);
        REL_EQUIVS.put(Link.REL_VIA_IANA, Link.REL_VIA);
        REL_EQUIVS.put(Link.REL_REPLIES_IANA, Link.REL_REPLIES);
        REL_EQUIVS.put(Link.REL_LICENSE_IANA, Link.REL_LICENSE);
        REL_EQUIVS.put(Link.REL_EDIT_IANA, Link.REL_EDIT);
        REL_EQUIVS.put(Link.REL_EDIT_MEDIA_IANA, Link.REL_EDIT_MEDIA);
        REL_EQUIVS.put(Link.REL_SERVICE_IANA, Link.REL_SERVICE);
    }

    private static final String getRelEquiv(String val) {
        try {
            val = IRI.normalizeString(val);
        } catch (Exception e) {
        }
        String rel = REL_EQUIVS.get(val);
        return (rel != null) ? rel : val;
    }

    public FOMLinkIterator(Element parent, Class<?> _class, QName attribute, String value, String defaultValue) {
        super(parent, _class, attribute, value != null ? getRelEquiv(value) : Link.REL_ALTERNATE, defaultValue);
    }

    public FOMLinkIterator(Element parent, Class<?> _class) {
        super(parent, _class);
    }

    protected boolean isMatch(Element el) {
        if (attribute != null) {
            String val = getRelEquiv(el.getAttributeValue(attribute));
            return ((val == null && value == null) || (val == null && value != null && value
                .equalsIgnoreCase(defaultValue)) || (val != null && val.equalsIgnoreCase(value)));
        }
        return true;
    }
}
