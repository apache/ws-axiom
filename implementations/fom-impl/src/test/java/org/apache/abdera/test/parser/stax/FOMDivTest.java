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
package org.apache.abdera.test.parser.stax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Text;
import org.junit.Test;

public class FOMDivTest {
    
    @Test
    public void getInternalValueWithUtf8Characters (){
        Abdera abdera = new Abdera ();
        InputStream in = FOMTest.class.getResourceAsStream("/utf8characters.xml");
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();

        assertEquals("Item", entry.getTitle());
        assertEquals(Text.Type.TEXT, entry.getTitleType());
        String value = entry.getContentElement().getValue();
        assertTrue(value.contains("Ȁȁ"));
    }
}
