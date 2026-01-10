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

package org.apache.axiom.attachments;

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.ts.soap.MTOMSample;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

/** Test the PartOnFile class */

public class PartOnFileTest extends TestCase {

    public PartOnFileTest(String testName) {
        super(testName);
    }

    File temp;

    @Override
    public void setUp() throws Exception {
        createTemporaryDirectory();
    }

    @Override
    public void tearDown() throws Exception {
        deleteTemporaryDirectory();
    }

    public void testHeaderGetSet() throws Exception {

        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments =
                new Attachments(inStream, MTOMSample.SAMPLE1.getContentType(), true, temp.getPath(), "1");

        DataHandler dh = attachments
                .getDataHandler("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org");
        
        assertNotNull(dh);

        DataSource ds = dh.getDataSource();
        assertNotNull(ds);
        // The attachment cleanup code in Axis2 relies on the assumption that attachments written
        // to disk produce CachedFileDataSource instances.
        assertThat(ds).isInstanceOf(CachedFileDataSource.class);

        assertEquals("image/jpeg", dh.getContentType());
    }

    private void createTemporaryDirectory() throws Exception {
        temp = File.createTempFile("partOnFileTest", ".tmp");

        if (!temp.delete()) {
            fail("Cannot delete from temporary directory. File: " + temp);
        }

        if (!temp.mkdir()) {
            fail("Cannot create a temporary location for part files");
        }
    }

    private void deleteTemporaryDirectory() throws Exception {

        String[] fileList = temp.list();
        for (int i = 0; i < fileList.length; i++) {
            if (!(new File(temp, fileList[i])).delete()) {
                System.err.println("WARNING: temporary directory removal failed.");
            }
        }

        if (!temp.delete()) {
            System.err.println("WARNING: temporary directory removal failed.");
        }
    }
}
