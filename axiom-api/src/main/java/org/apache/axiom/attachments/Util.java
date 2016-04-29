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

import javax.activation.DataSource;

final class Util {
    private Util() {}

    static String normalizeContentID(String contentID) {
        contentID = contentID.trim();
        if (contentID.length() >= 2 && contentID.charAt(0) == '<'
                && contentID.charAt(contentID.length()-1) == '>') {
            contentID = contentID.substring(1, contentID.length()-1);
        }
        // There is some evidence that some broken MIME implementations add
        // a "cid:" prefix to the Content-ID; remove it if necessary.
        if (contentID.length() > 4 && contentID.startsWith("cid:")) {
            contentID = contentID.substring(4);
        }
        return contentID;
    }

    /**
     * Get the content type that should be reported by {@link DataSource} instances created for a
     * given part.
     * 
     * @param Part
     *            the part
     * @return the content type
     */
    static String getDataSourceContentType(Part part) {
        String ct = part.getContentType();
        return ct == null ? "application/octet-stream" : ct;
    }
}