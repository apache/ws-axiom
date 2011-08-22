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

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.axiom.attachments.lifecycle.DataHandlerExt;

class PartDataHandler extends DataHandler implements DataHandlerExt {
    private final PartImpl part;
    private DataSource dataSource;

    public PartDataHandler(PartImpl part) {
        super(new PartDataSource(part));
        this.part = part;
    }

    public DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = part.getDataSource();
        }
        return dataSource == null ? super.getDataSource() : dataSource;
    }

    public InputStream readOnce() throws IOException {
        return part.getInputStream(false);
    }

    public void purgeDataSource() throws IOException {
        part.releaseContent();
    }

    public void deleteWhenReadOnce() throws IOException {
        // As shown in AXIOM-381, in all released versions of Axiom, deleteWhenReadOnce
        // always has the same effect as purgeDataSource
        purgeDataSource();
    }
}
