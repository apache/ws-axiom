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

public class ContentTypeBuilderTest extends TestCase {
    public void testBasic() {
        ContentTypeBuilder builder = new ContentTypeBuilder(MediaType.TEXT_XML);
        builder.setParameter("charset", "utf-8");
        ContentType contentType = builder.build();
        assertEquals(MediaType.TEXT_XML, contentType.getMediaType());
        assertEquals("utf-8", contentType.getParameter("charset"));
    }
    
    public void testFromExistingContentType() {
        ContentType contentType = new ContentType(MediaType.TEXT_XML, "charset", "utf-8");
        ContentTypeBuilder builder = new ContentTypeBuilder(contentType);
        assertEquals(MediaType.TEXT_XML, builder.getMediaType());
        assertEquals("utf-8", builder.getParameter("charset"));
    }
    
    public void testParse() throws Exception {
        ContentTypeBuilder builder = new ContentTypeBuilder("text/xml; charset=utf-8");
        assertEquals(MediaType.TEXT_XML, builder.getMediaType());
        assertEquals("utf-8", builder.getParameter("charset"));
    }
    
    public void testSetMediaType() throws Exception {
        ContentTypeBuilder builder = new ContentTypeBuilder("text/xml; charset=utf-8");
        builder.setMediaType(MediaType.APPLICATION_XML);
        assertEquals("application/xml; charset=\"utf-8\"", builder.toString());
    }
    
    public void testGetParameterIgnoresCase() {
        ContentTypeBuilder builder = new ContentTypeBuilder(MediaType.TEXT_XML);
        builder.setParameter("charset", "utf-8");
        assertEquals("utf-8", builder.getParameter("CHARSET"));
    }
    
    public void testSetParameterIgnoresCase() {
        ContentTypeBuilder builder = new ContentTypeBuilder(MediaType.TEXT_XML);
        builder.setParameter("charset", "utf-8");
        builder.setParameter("CHARSET", "us-ascii");
        assertEquals("us-ascii", builder.getParameter("charset"));
    }
    
    public void testClearParameters() {
        ContentTypeBuilder builder = new ContentTypeBuilder(MediaType.TEXT_XML);
        builder.setParameter("charset", "utf-8");
        builder.clearParameters();
        assertNull(builder.getParameter("charset"));
    }

    public void testRemoveParameter() throws Exception {
        ContentTypeBuilder builder = new ContentTypeBuilder("text/xml; charset=utf-8; x-param=value");
        builder.removeParameter("charset");
        assertEquals("text/xml; x-param=\"value\"", builder.toString());
    }
}
