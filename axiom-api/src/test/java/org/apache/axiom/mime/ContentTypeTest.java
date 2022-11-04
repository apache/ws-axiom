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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import java.text.ParseException;

import junit.framework.TestCase;

public class ContentTypeTest extends TestCase {
    public void testNullMediaType() {
        assertThrows(NullPointerException.class, () -> new ContentType((MediaType)null));
    }

    public void testNullParameter() {
        assertThrows(NullPointerException.class, () -> new ContentType(MediaType.TEXT_PLAIN, "charset", null));
    }

    public void testImmutable() {
        String[] parameters = { "charset", "utf-8" };
        ContentType ct = new ContentType(MediaType.TEXT_XML, parameters);
        parameters[1] = "ascii";
        assertEquals("utf-8", ct.getParameter("charset"));
    }
    
    public void testGetParameterIgnoresCase() {
        ContentType ct = new ContentType(MediaType.TEXT_XML, "charset", "utf-8");
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
        assertEquals(MediaType.MULTIPART_RELATED, ct.getMediaType());
        assertEquals("boundaryA3ADBAEE51A1A87B2A11443668160701", ct.getParameter("boundary"));
        assertEquals("application/xop+xml", ct.getParameter("type"));
        assertEquals("<A3ADBAEE51A1A87B2A11443668160702@apache.org>", ct.getParameter("start"));
        assertEquals("application/soap+xml", ct.getParameter("start-info"));
        assertEquals("UTF-8", ct.getParameter("charset"));
        assertEquals("urn:myAction", ct.getParameter("action"));
    }
    
    public void testParseWithExtraSemicolon() throws Exception {
        ContentType ct = new ContentType("text/xml; charset=utf-8;");
        assertEquals(MediaType.TEXT_XML, ct.getMediaType());
        assertEquals("utf-8", ct.getParameter("charset"));
    }
    
    public void testParseWithExtraSpaces() throws Exception {
        ContentType ct = new ContentType("text/xml ; charset = utf-8 ");
        assertEquals(MediaType.TEXT_XML, ct.getMediaType());
        assertEquals("utf-8", ct.getParameter("charset"));
    }
    
    public void testParseWithQuotedPair() throws Exception {
        ContentType ct = new ContentType("application/x-some-format; comment=\"this is not a \\\"quote\\\"\"");
        assertEquals("this is not a \"quote\"", ct.getParameter("comment"));
    }
    
    public void testParseInvalid1() {
        assertThrows(ParseException.class, () -> { new ContentType("text/xml; ?"); });
    }

    public void testParseInvalid2() {
        assertThrows(ParseException.class, () -> { new ContentType("text/"); });
    }

    public void testParseInvalid3() {
        assertThrows(ParseException.class, () -> { new ContentType("text/xml; charset="); });
    }

    public void testParseInvalid4() {
        assertThrows(ParseException.class, () -> { new ContentType("text/xml; charset=\"asc"); });
    }

    public void testParseInvalid5() {
        assertThrows(ParseException.class, () -> { new ContentType("text/xml; param=\"test\\"); });
    }

    public void testParseInvalid6() {
        assertThrows(ParseException.class, () -> { new ContentType("text/xml; param;"); });
    }

    public void testParseInvalid7() {
        assertThrows(ParseException.class, () -> { new ContentType("text/xml; param"); });
    }

    public void testToString() {
        ContentType ct = new ContentType(MediaType.TEXT_XML, "charset", "utf-8");
        assertEquals("text/xml; charset=\"utf-8\"", ct.toString());
    }
    
    public void testToStringWithQuote() {
        ContentType ct = new ContentType(new MediaType("application", "x-some-format"),
                "comment", "this is not a \"quote\"");
        assertEquals("application/x-some-format; comment=\"this is not a \\\"quote\\\"\"", ct.toString());
    }

    public void testToStringWithBackslash() {
        ContentType ct = new ContentType(new MediaType("application", "x-some-format"),
                "filename", "c:\\temp\\test.dat");
        assertEquals("application/x-some-format; filename=\"c:\\\\temp\\\\test.dat\"", ct.toString());
    }

    private static boolean isTextual(String contentType) throws Exception {
        return new ContentType(contentType).isTextual();
    }

    public void testIsTextual() throws Exception {
        assertThat(isTextual("text/xml")).isTrue();
        assertThat(isTextual("application/xml")).isTrue();
        assertThat(isTextual("application/soap+xml")).isTrue();
        assertThat(isTextual("foo/bar; charset=UTF-8")).isTrue();
        assertThat(isTextual("image/gif")).isFalse();
    }
}
