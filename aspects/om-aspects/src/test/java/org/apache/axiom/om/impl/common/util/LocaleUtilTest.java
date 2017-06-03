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
package org.apache.axiom.om.impl.common.util;

import static com.google.common.truth.Truth.assertThat;

import java.util.Locale;

import org.junit.Test;

public class LocaleUtilTest {
    private static void assertCloserMatch(Locale requested, Locale closer, Locale other) {
        assertThat(LocaleUtil.getMatchScore(requested, closer)).isGreaterThan(LocaleUtil.getMatchScore(requested, other));
    }

    @Test
    public void testGetMatchScore() {
        assertCloserMatch(new Locale("de", "DE"), new Locale("de", "DE"), new Locale("de"));
        // English is always preferred over other non matching languages
        assertCloserMatch(new Locale("de"), new Locale("en"), new Locale("fr"));
        // Prefer locale without country
        assertCloserMatch(new Locale("fr", "CA"), new Locale("fr"), new Locale("fr", "FR"));
        assertCloserMatch(new Locale("de"), new Locale("en"), new Locale("en", "US"));
        // No locale is better than a non matching locale
        assertCloserMatch(new Locale("de"), null, new Locale("fr"));
    }
}
