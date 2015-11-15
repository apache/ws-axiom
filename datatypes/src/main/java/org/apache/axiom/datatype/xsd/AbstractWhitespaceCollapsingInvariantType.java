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

import org.apache.axiom.datatype.AbstractInvariantType;
import org.apache.axiom.datatype.UnexpectedEndOfStringException;

abstract class AbstractWhitespaceCollapsingInvariantType<T> extends AbstractInvariantType<T> {
    public final T parse(String literal) throws ParseException {
        final int len = literal.length();
        if (len == 0) {
            throw new UnexpectedEndOfStringException(literal);
        }
        int begin = 0;
        while (Util.isWhitespace(literal.charAt(begin))) {
            if (++begin == len) {
                throw new UnexpectedEndOfStringException(literal);
            }
        }
        int end = len;
        while (Util.isWhitespace(literal.charAt(end-1))) {
            end--;
        }
        return parse(literal, begin, end);
    }
    
    protected abstract T parse(String literal, int begin, int end) throws ParseException;
}
