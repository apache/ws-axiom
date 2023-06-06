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

package org.apache.axiom.testutils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;

public final class IOTestUtils {
    private IOTestUtils() {}

    @Deprecated
    public static void compareStreams(InputStream s1, InputStream s2) throws IOException {
        compareStreams(s1, "s1", s2, "s2");
    }

    public static void compareStreams(InputStream s1, String name1, InputStream s2, String name2)
            throws IOException {
        OutputStream comparator = new ByteStreamComparator(s2, name2, name1);
        IOUtils.copy(s1, comparator);
        comparator.close();
    }

    @Deprecated
    public static void compareStreams(Reader s1, Reader s2) throws IOException {
        compareStreams(s1, "s1", s2, "s2");
    }

    public static void compareStreams(Reader s1, String name1, Reader s2, String name2)
            throws IOException {
        Writer comparator = new CharacterStreamComparator(s2, name2, name1);
        IOUtils.copy(s1, comparator);
        comparator.close();
    }
}
