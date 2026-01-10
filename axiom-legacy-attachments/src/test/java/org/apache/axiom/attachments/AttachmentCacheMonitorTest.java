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

import java.io.File;

import org.apache.axiom.om.AbstractTestCase;

public class AttachmentCacheMonitorTest extends AbstractTestCase {
    public void testCachedFilesExpired() throws Exception {

        // Set file expiration to 10 seconds
        long INTERVAL = 3 * 1000; // 3 seconds for Thread to sleep

        // Get the AttachmentCacheMonitor and force it to remove files after
        // 10 seconds.
        AttachmentCacheMonitor acm = AttachmentCacheMonitor.getAttachmentCacheMonitor();
        int previousTime = acm.getTimeout();

        try {
            acm.setTimeout(10);

            File aFile = File.createTempFile("fileA", ".tmp");
            String aFileName = aFile.getCanonicalPath();
            acm.register(aFileName);

            Thread.sleep(INTERVAL);

            File bFile = File.createTempFile("fileB", ".tmp");
            String bFileName = bFile.getCanonicalPath();
            acm.register(bFileName);

            Thread.sleep(INTERVAL);

            acm.access(aFileName);

            // time since file A registration <= cached file expiration
            assertTrue("File A should still exist", aFile.exists());

            Thread.sleep(INTERVAL);

            acm.access(bFileName);

            // time since file B registration <= cached file expiration
            assertTrue("File B should still exist", bFile.exists());

            Thread.sleep(INTERVAL);

            File cFile = File.createTempFile("fileC", ".tmp");
            String cFileName = cFile.getCanonicalPath();
            acm.register(cFileName);
            acm.access(bFileName);

            Thread.sleep(INTERVAL);

            acm.checkForAgedFiles();

            // time since file C registration <= cached file expiration
            assertTrue("File C should still exist", cFile.exists());

            Thread.sleep(10 * INTERVAL); // Give task loop time to delete aged files

            // All files should be gone by now
            assertFalse("File A should no longer exist", aFile.exists());
            assertFalse("File B should no longer exist", bFile.exists());
            assertFalse("File C should no longer exist", cFile.exists());
        } finally {

            // Reset the timeout to the previous value so that no
            // other tests are affected
            acm.setTimeout(previousTime);
        }
    }
}
