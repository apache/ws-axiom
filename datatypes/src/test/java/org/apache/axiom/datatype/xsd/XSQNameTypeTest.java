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

import static com.google.common.truth.Truth.assertThat;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.junit.Test;

public class XSQNameTypeTest {
    @Test
    public void testParseWithBoundPrefix() throws ParseException {
        QName qname = XSQNameType.INSTANCE.parse("p:test", MapContextAccessor.INSTANCE,
                Collections.singletonMap("p", "urn:test"), null);
        assertThat(qname.getNamespaceURI()).isEqualTo("urn:test");
        assertThat(qname.getLocalPart()).isEqualTo("test");
        assertThat(qname.getPrefix()).isEqualTo("p");
    }
    
    @Test(expected=ParseException.class)
    public void testParseWithUnboundPrefix() throws ParseException {
        XSQNameType.INSTANCE.parse("ns:test", MapContextAccessor.INSTANCE,
                Collections.<String,String>emptyMap(), null);
    }
    
    @Test
    public void testParseWithXmlPrefix() throws ParseException {
        QName qname = XSQNameType.INSTANCE.parse("xml:value", MapContextAccessor.INSTANCE,
                Collections.<String,String>emptyMap(), null);
        assertThat(qname.getNamespaceURI()).isEqualTo(XMLConstants.XML_NS_URI);
        assertThat(qname.getLocalPart()).isEqualTo("value");
        assertThat(qname.getPrefix()).isEqualTo(XMLConstants.XML_NS_PREFIX);
    }
    
    @Test
    public void testParseWithXmlnsPrefix() throws ParseException {
        QName qname = XSQNameType.INSTANCE.parse("xmlns:value", MapContextAccessor.INSTANCE,
                Collections.<String,String>emptyMap(), null);
        assertThat(qname.getNamespaceURI()).isEqualTo(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        assertThat(qname.getLocalPart()).isEqualTo("value");
        assertThat(qname.getPrefix()).isEqualTo(XMLConstants.XMLNS_ATTRIBUTE);
    }
    
    @Test
    public void testFormatWithUnboundPrefix() {
        Map<String,String> context = new HashMap<String,String>();
        assertThat(XSQNameType.INSTANCE.format(new QName("urn:test", "test", "p"),
                MapContextAccessor.INSTANCE, context, null)).isEqualTo("p:test");
        assertThat(context).hasSize(1);
        assertThat(context).containsEntry("p", "urn:test");
    }
    
    @Test
    public void testFormatWithExistingNamespace() {
        assertThat(XSQNameType.INSTANCE.format(new QName("urn:test", "test"),
                MapContextAccessor.INSTANCE, Collections.singletonMap("p", "urn:test"),
                null)).isEqualTo("p:test");
    }
    
    @Test
    public void testFormatWithGeneratedPrefix() {
        Map<String,String> context = new HashMap<String,String>();
        String literal = XSQNameType.INSTANCE.format(new QName("urn:test", "test"),
                MapContextAccessor.INSTANCE, context, null);
        assertThat(context).hasSize(1);
        Map.Entry<String,String> entry = context.entrySet().iterator().next();
        assertThat(entry.getValue()).isEqualTo("urn:test");
        assertThat(entry.getKey()).isNotEmpty();
        assertThat(literal).isEqualTo(entry.getKey() + ":test");
    }
    
    @Test
    public void testFormatWithNoNamespace() {
        assertThat(XSQNameType.INSTANCE.format(new QName("test"),
                MapContextAccessor.INSTANCE, Collections.<String,String>emptyMap(),
                null)).isEqualTo("test");
    }
}
