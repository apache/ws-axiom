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
import static org.junit.Assert.fail;

import java.util.Date;

import org.apache.axiom.datatype.UnexpectedEndOfStringException;
import org.junit.Test;

public class XSDateTimeTypeTest {
    @Test
    public void testParse() throws Exception {
        XSDateTime dateTime = XSDateTimeType.INSTANCE.parse("2003-12-13T18:30:02Z");
        assertThat(dateTime.getDate(null)).isEqualTo(new Date(1071340202000L));
    }
    
    @Test
    public void testParseTruncated() throws Exception {
        String literal = "2002-10-10T12:00:00-05:00";
        for (int i=0; i<literal.length()-1; i++) {
            if (i == 19) {
                // This would give a valid literal without time zone
                continue;
            }
            String truncatedLiteral = literal.substring(0, i);
            try {
                XSDateTimeType.INSTANCE.parse(truncatedLiteral);
                fail("Expected UnexpectedEndOfStringException for literal \"" + truncatedLiteral + "\"");
            } catch (UnexpectedEndOfStringException ex) {
                // Expected
            }
        }
    }
}
