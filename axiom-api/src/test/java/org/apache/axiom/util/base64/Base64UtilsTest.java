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

package org.apache.axiom.util.base64;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.Random;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;

public class Base64UtilsTest extends TestCase {
    public void testDecode() {
        Random random = new Random(43219876);
        for (int len=0; len<20; len++) {
            byte[] data = new byte[len];
            random.nextBytes(data);
            assertThat(Base64Utils.decode(Base64.encodeBase64String(data))).isEqualTo(data);
        }
    }
    
    public void testMissingPadding() {
        assertThrows(IllegalArgumentException.class, () -> Base64Utils.decode("cw"));
    }

    public void testTooMuchPadding() {
        assertThrows(IllegalArgumentException.class, () -> Base64Utils.decode("cw==="));
    }
    
    public void testNonZeroRemainder() {
        assertThrows(IllegalArgumentException.class, () -> Base64Utils.decode("//=="));
    }
    
    public void testSpace() throws Exception{
        assertEquals(
                "any carnal pleasure.",
                new String(Base64Utils.decode(" YW55IG\tNhcm5hbC\r\nBwb  GVhc3VyZS4 = "), "utf-8"));
    }

    public void testInvalidCharacter() {
        assertThrows(IllegalArgumentException.class, () -> Base64Utils.decode("//-/"));
    }

    public void testInvalidPadding() {
        assertThrows(IllegalArgumentException.class, () -> Base64Utils.decode("//=/"));
    }
}