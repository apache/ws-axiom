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
package org.apache.axiom.soap.impl.intf;

import java.text.ParseException;

import org.apache.axiom.datatype.InvariantType;
import org.apache.axiom.datatype.TypeHelper;

final class SOAP11BooleanType extends InvariantType<Boolean> {
    static final SOAP11BooleanType INSTANCE = new SOAP11BooleanType();
    
    private SOAP11BooleanType() {}
    
    public Boolean parse(String literal) throws ParseException {
        int start = TypeHelper.getStartIndex(literal);
        int end = TypeHelper.getEndIndex(literal);
        if (end-start == 1) {
            switch (literal.charAt(start)) {
                case '0': return Boolean.FALSE;
                case '1': return Boolean.TRUE;
            }
        }
        throw new ParseException("Unexpected boolean literal", start);
    }

    public String format(Boolean value) {
        return value ? "1" : "0";
    }
}
