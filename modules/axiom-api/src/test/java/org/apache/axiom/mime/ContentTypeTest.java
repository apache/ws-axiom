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
package org.apache.axiom.mime;

import junit.framework.TestCase;

public class ContentTypeTest extends TestCase {
    public void testImmutable() {
        String[] parameters = { "charset", "utf-8" };
        ContentType ct = new ContentType(new MediaType("text", "xml"), parameters);
        parameters[1] = "ascii";
        assertEquals("utf-8", ct.getParameter("charset"));
    }
    
    public void testGetParameterIgnoresCase() {
        ContentType ct = new ContentType(new MediaType("text", "xml"), new String[] { "charset", "utf-8" });
        assertEquals("utf-8", ct.getParameter("CHARSET"));
    }
    
    public void testParse() throws Exception {
        ContentType ct = new ContentType("multipart/related; "
                + "boundary=\"boundaryA3ADBAEE51A1A87B2A11443668160701\"; "
                + "type=\"application/xop+xml\"; "
                + "start=\"<A3ADBAEE51A1A87B2A11443668160702@apache.org>\"; "
                + "start-info=\"application/soap+xml\"; "
                + "charset=UTF-8;"
                + "action=\"urn:myAction\"");
        assertEquals(new MediaType("multipart", "related"), ct.getMediaType());
        assertEquals("boundaryA3ADBAEE51A1A87B2A11443668160701", ct.getParameter("boundary"));
        assertEquals("application/xop+xml", ct.getParameter("type"));
        assertEquals("<A3ADBAEE51A1A87B2A11443668160702@apache.org>", ct.getParameter("start"));
        assertEquals("application/soap+xml", ct.getParameter("start-info"));
        assertEquals("UTF-8", ct.getParameter("charset"));
        assertEquals("urn:myAction", ct.getParameter("action"));
    }
    
    public void testParseWithExtraSemicolon() throws Exception {
        ContentType ct = new ContentType("text/xml; charset=utf-8;");
        assertEquals(new MediaType("text", "xml"), ct.getMediaType());
        assertEquals("utf-8", ct.getParameter("charset"));
    }
}
