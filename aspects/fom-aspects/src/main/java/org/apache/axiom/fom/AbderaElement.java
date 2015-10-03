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

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.axiom.core.CoreNSAwareElement;

public interface AbderaElement extends Element, AbderaChildNode, CoreNSAwareElement {
    <E extends Element> List<E> _getChildrenAsSet(QName qname);
    void _setChild(QName qname, Element element);
    Iterator<AbderaElement> _getChildrenWithName(QName qname);
    void _removeChildren(QName qname, boolean many);
    String _getElementValue(QName qname);
    void _setElementValue(QName qname, String value);
    AbderaElement _getFirstChildWithName(QName qname);
}
