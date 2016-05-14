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


/**
 * Holds information about a given encoding, which is the Java name for the
 * encoding, the equivalent ISO name.
 * <p>
 * An object of this type has two useful methods
 * <pre>
 * isInEncoding(char ch);
 * </pre>
 * which can be called if the character is not the high one in
 * a surrogate pair and:
 * <pre>
 * isInEncoding(char high, char low);
 * </pre>
 * which can be called if the two characters from a high/low surrogate pair.
 * <p>
 * An EncodingInfo object is a node in a binary search tree. Such a node
 * will answer if a character is in the encoding, and do so for a given
 * range of unicode values (<code>m_first</code> to
 * <code>m_last</code>). It will handle a certain range of values
 * explicitly (<code>m_explFirst</code> to <code>m_explLast</code>).
 * If the unicode point is before that explicit range, that is it
 * is in the range <code>m_first <= value < m_explFirst</code>, then it will delegate to another EncodingInfo object for The root
 * of such a tree, m_before.  Likewise for values in the range 
 * <code>m_explLast < value <= m_last</code>, but delgating to <code>m_after</code>
 * <p>
 * Actually figuring out if a code point is in the encoding is expensive. So the
 * purpose of this tree is to cache such determinations, and not to build the
 * entire tree of information at the start, but only build up as much of the 
 * tree as is used during the transformation.
 * <p>
 * This Class is not a public API, and should only be used internally within
 * the serializer.
 * <p>
 * This class is not a public API.
 * @xsl.usage internal
 */
public final class EncodingInfo extends Object
{

    /**
     * The ISO encoding name.
     */
    final String name;

    /**
     * The name used by the Java convertor.
     */
    final String javaName;
    
    /**
     * Create an EncodingInfo object based on the ISO name and Java name.
     * If both parameters are null any character will be considered to
     * be in the encoding. This is useful for when the serializer is in
     * temporary output state, and has no assciated encoding.
     *
     * @param name reference to the ISO name.
     * @param javaName reference to the Java encoding name.
     * @param highChar The char for which characters at or below this value are 
     * definately in the
     * encoding, although for characters above this point they might be in the encoding.
     */
    public EncodingInfo(String name, String javaName, char highChar)
    {

        this.name = name;
        this.javaName = javaName;
    }

}
