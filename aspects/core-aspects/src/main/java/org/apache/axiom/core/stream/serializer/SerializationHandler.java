/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.axiom.core.stream.serializer;

import org.apache.axiom.core.stream.StreamException;

/**
 * This interface is the one that a serializer implements. It is a group of
 * other interfaces, such as ExtendedContentHandler, ExtendedLexicalHandler etc.
 * In addition there are other methods, such as reset().
 * 
 * This class is public only because it is used in another package,
 * it is not a public API.
 * 
 * @xsl.usage internal
 */
public interface SerializationHandler
    extends
        ExtendedContentHandler,
        XSLOutputAttributes,
        Serializer
{
    public void close();

    /**
     * A SerializationHandler accepts SAX-like events, so
     * it can accumulate attributes or namespace nodes after
     * a startElement().
     * <p>
     * If the SerializationHandler has a Writer or OutputStream, 
     * a call to this method will flush such accumulated 
     * events as a closed start tag for an element.
     * <p>
     * If the SerializationHandler wraps a ContentHandler,
     * a call to this method will flush such accumulated
     * events as a SAX (not SAX-like) calls to
     * startPrefixMapping() and startElement().
     * <p>
     * If one calls endDocument() then one need not call
     * this method since a call to endDocument() will
     * do what this method does. However, in some
     * circumstances, such as with document fragments,
     * endDocument() is not called and it may be
     * necessary to call this method to flush
     * any pending events.
     * <p> 
     * For performance reasons this method should not be called
     * very often. 
     */
    public void flushPending() throws StreamException;
    
    /**
     * Default behavior is to expand DTD entities,
     * that is the initall default value is true.
     * @param expand true if DTD entities are to be expanded,
     * false if they are to be left as DTD entity references. 
     */
    public void setDTDEntityExpansion(boolean expand);
    
}
