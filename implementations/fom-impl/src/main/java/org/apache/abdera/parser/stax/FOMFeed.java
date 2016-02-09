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

import static org.apache.abdera.util.Constants.ENTRY;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Source;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.fom.AbderaElement;
import org.apache.axiom.fom.AbderaEntry;
import org.apache.axiom.fom.AbderaFeed;
import org.apache.axiom.fom.FOMSemantics;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;

public class FOMFeed extends FOMSource implements AbderaFeed {
    public List<Entry> getEntries() {
        return _getChildrenAsSet(ENTRY);
    }

    public Feed addEntry(Entry entry) {
        _addChild((AbderaEntry)entry);
        return this;
    }

    public Entry addEntry() {
        return getFactory().newEntry(this);
    }

    public Feed insertEntry(Entry entry) {
        OMElement el = (OMElement)_getFirstChildWithName(ENTRY);
        if (el == null) {
            addEntry(entry);
        } else {
            el.insertSiblingBefore((OMElement)entry);
        }
        return this;
    }

    public Entry insertEntry() {
        Entry entry = getFactory().newEntry((Feed)null);
        insertEntry(entry);
        return entry;
    }

    public Source getAsSource() {
        FOMSource source = (FOMSource)getFactory().newSource(null);
        for (Iterator<?> i = this.getChildElements(); i.hasNext();) {
            FOMElement child = (FOMElement)i.next();
            if (!child.getQName().equals(ENTRY)) {
                source.addChild((OMNode)child.clone());
            }
        }
        try {
            if (this.getBaseUri() != null) {
                source.setBaseUri(this.getBaseUri());
            }
        } catch (Exception e) {
        }
        return source;
    }

    @Override
    public void _addChild(AbderaElement element) {
        try {
            if (!(element instanceof Entry)) {
                AbderaElement entry = _getFirstChildWithName(ENTRY);
                if (entry != null) {
                    entry.coreInsertSiblingBefore(element);
                    return;
                }
            }
            coreAppendChild(element, false);
        } catch (CoreModelException ex) {
            throw FOMSemantics.INSTANCE.toUncheckedException(ex);
        }
    }

    public Feed sortEntriesByUpdated(boolean new_first) {
        sortEntries(new UpdatedComparator(new_first));
        return this;
    }

    public Feed sortEntriesByEdited(boolean new_first) {
        sortEntries(new EditedComparator(new_first));
        return this;
    }

    public Feed sortEntries(Comparator<Entry> comparator) {
        if (comparator == null)
            return this;
        List<Entry> entries = this.getEntries();
        Entry[] a = entries.toArray(new Entry[entries.size()]);
        Arrays.sort(a, comparator);
        for (Entry e : entries) {
            e.discard();
        }
        for (Entry e : a) {
            addEntry(e);
        }
        return this;
    }

    private static class EditedComparator implements Comparator<Entry> {
        private boolean new_first = true;

        EditedComparator(boolean new_first) {
            this.new_first = new_first;
        }

        public int compare(Entry o1, Entry o2) {
            Date d1 = o1.getEdited();
            Date d2 = o2.getEdited();
            if (d1 == null)
                d1 = o1.getUpdated();
            if (d2 == null)
                d2 = o2.getUpdated();
            if (d1 == null && d2 == null)
                return 0;
            if (d1 == null && d2 != null)
                return -1;
            if (d1 != null && d2 == null)
                return 1;
            int r = d1.compareTo(d2);
            return (new_first) ? -r : r;
        }
    };

    private static class UpdatedComparator implements Comparator<Entry> {
        private boolean new_first = true;

        UpdatedComparator(boolean new_first) {
            this.new_first = new_first;
        }

        public int compare(Entry o1, Entry o2) {
            Date d1 = o1.getUpdated();
            Date d2 = o2.getUpdated();
            if (d1 == null && d2 == null)
                return 0;
            if (d1 == null && d2 != null)
                return -1;
            if (d1 != null && d2 == null)
                return 1;
            int r = d1.compareTo(d2);
            return (new_first) ? -r : r;
        }
    };

    public Entry getEntry(String id) {
        if (id == null)
            return null;
        List<Entry> l = getEntries();
        for (Entry e : l) {
            IRI eid = e.getId();
            if (eid != null && eid.equals(new IRI(id)))
                return e;
        }
        return null;
    }
}
