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
package org.apache.axiom.datatype.xsd;

import java.text.ParseException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.axiom.datatype.ContextAccessor;
import org.apache.axiom.datatype.TypeHelper;
import org.apache.axiom.datatype.UnexpectedCharacterException;
import org.apache.axiom.util.xml.NSUtils;

final class XSQNameTypeImpl implements XSQNameType {
    public <S,O> QName parse(String literal, ContextAccessor<S,O> contextAccessor, S contextObject, O options)
            throws ParseException {
        final int start = TypeHelper.getStartIndex(literal);
        final int end = TypeHelper.getEndIndex(literal);
        int colonIndex = -1;
        for (int index = start; index<end; index++) {
            // TODO: we should check that the literal is a valid NCName
            if (literal.charAt(index) == ':') {
                if (colonIndex != -1) {
                    throw new UnexpectedCharacterException(literal, index);
                }
                colonIndex = index;
            }
        }
        String prefix;
        String localPart;
        if (colonIndex == -1) {
            prefix = "";
            localPart = literal.toString();
        } else {
            prefix = literal.substring(start, colonIndex);
            localPart = literal.substring(colonIndex+1, end);
        }
        String namespaceURI;
        if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
            namespaceURI = XMLConstants.XML_NS_URI;
        } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            namespaceURI = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else {
            namespaceURI = contextAccessor.lookupNamespaceURI(contextObject, options, prefix);
            if (namespaceURI == null) {
                throw new ParseException("Unbound namespace prefix \"" + prefix + "\"", 0);
            }
        }
        return new QName(namespaceURI, localPart, prefix);
    }

    public <S,O> String format(QName value, ContextAccessor<S,O> contextAccessor, S contextObject, O options) {
        String prefix = value.getPrefix();
        String namespaceURI = value.getNamespaceURI();
        if (!namespaceURI.equals(contextAccessor.lookupNamespaceURI(contextObject, options, prefix))) {
            if (namespaceURI.length() == 0) {
                contextAccessor.declareNamespace(contextObject, options, "", "");
            } else {
                if (prefix.length() == 0) {
                    prefix = contextAccessor.lookupPrefix(contextObject, options, namespaceURI);
                    if (prefix == null) {
                        prefix = NSUtils.generatePrefix(namespaceURI);
                        contextAccessor.declareNamespace(contextObject, options, prefix, namespaceURI);;
                    }
                } else {
                    contextAccessor.declareNamespace(contextObject, options, prefix, namespaceURI);;
                }
            }
        }
        if (prefix.length() == 0) {
            return value.getLocalPart();
        } else {
            return prefix + ":" + value.getLocalPart();
        }
    }
}
