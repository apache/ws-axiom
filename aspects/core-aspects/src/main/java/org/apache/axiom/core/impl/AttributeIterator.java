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
package org.apache.axiom.core.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.Mapper;
import org.apache.axiom.core.Semantics;

public final class AttributeIterator<T extends CoreAttribute,S> implements Iterator<S> {
    private final Class<T> type;
    private final Mapper<S,? super T> mapper;
    private final Semantics semantics;
    private CoreAttribute currentAttribute;
    private CoreAttribute nextAttribute;
    private boolean nextAttributeSet;
    
    private AttributeIterator(CoreAttribute firstAttribute, Class<T> type, Mapper<S,? super T> mapper, Semantics semantics) {
        this.type = type;
        this.mapper = mapper;
        this.semantics = semantics;
        nextAttribute = firstAttribute;
        nextAttributeSet = true;
    }
    
    public static <T extends CoreAttribute,S> Iterator<S> create(CoreElement element, Class<T> type, Mapper<S,? super T> mapper, Semantics semantics) {
        CoreAttribute attribute = element.coreGetFirstAttribute();
        while (attribute != null && !type.isInstance(attribute)) {
            attribute = attribute.coreGetNextAttribute();
        }
        if (attribute == null) {
            return Collections.<S>emptyList().iterator();
        } else {
            return new AttributeIterator<T,S>(attribute, type, mapper, semantics);
        }
    }
    
    @Override
    public final boolean hasNext() {
        if (!nextAttributeSet) {
            CoreAttribute attribute = currentAttribute;
            do {
                attribute = attribute.coreGetNextAttribute();
            } while (attribute != null && !type.isInstance(attribute));
            nextAttribute = attribute;
            nextAttributeSet = true;
        }
        return nextAttribute != null;
    }

    @Override
    public final S next() {
        if (hasNext()) {
            CoreAttribute attribute = nextAttribute;
            currentAttribute = attribute;
            nextAttribute = null;
            nextAttributeSet = false;
            return mapper.map(type.cast(attribute));
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public final void remove() {
        if (currentAttribute == null) {
            throw new IllegalStateException();
        } else {
            // Ensure that the next attribute is known before we remove the current one.
            hasNext();
            currentAttribute.coreRemove(semantics);
            currentAttribute = null;
        }
    }
}
