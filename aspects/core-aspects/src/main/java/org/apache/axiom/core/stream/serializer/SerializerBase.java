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
 * This class acts as a base class for the XML "serializers"
 * and the stream serializers.
 * It contains a number of common fields and methods.
 * 
 * @xsl.usage internal
 */
public abstract class SerializerBase
    implements SerializerConstants
{
    SerializerBase() {
        return;
    }
    
    /**
     * The name of the package that this class is in.
     * <p>
     * Not a public API.
     */
    public static final String PKG_NAME;

    /**
     * The same as the name of the package that this class is in
     * except that '.' are replaced with '/'.
     * <p>
     * Not a public API.
     */
    public static final String PKG_PATH;

    static {
        String fullyQualifiedName = SerializerBase.class.getName();
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        if (lastDot < 0) {
            PKG_NAME = "";
        } else {
            PKG_NAME = fullyQualifiedName.substring(0, lastDot);
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < PKG_NAME.length(); i++) {
            char ch = PKG_NAME.charAt(i);
            if (ch == '.')
                sb.append('/');
            else
                sb.append(ch);
        }
        PKG_PATH = sb.toString();
    }

    /**
     * A utility buffer for converting Strings passed to
     * character() methods to character arrays.
     * Reusing this buffer means not creating a new character array
     * everytime and it runs faster.
     */
    protected char[] m_charsBuff = new char[60];
}
