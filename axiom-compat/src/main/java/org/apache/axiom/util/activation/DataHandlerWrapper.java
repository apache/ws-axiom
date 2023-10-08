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
package org.apache.axiom.util.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.activation.ActivationDataFlavor;
import jakarta.activation.CommandInfo;
import jakarta.activation.CommandMap;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

/**
 * Base class for {@link DataHandler} wrappers.
 * 
 * @deprecated
 */
public class DataHandlerWrapper extends DataHandler {
    private final DataHandler parent;

    public DataHandlerWrapper(DataHandler parent) {
        // Some JavaMail implementations allow passing null to the constructor,
        // but this is not the case for all implementations. We use an empty data
        // source to avoid this issue. This approach is known to work with Sun's
        // and Geronimo's JavaMail implementations.
        super(EmptyDataSource.INSTANCE);
        this.parent = parent;
    }

    @Override
    public CommandInfo[] getAllCommands() {
        return parent.getAllCommands();
    }

    @Override
    public Object getBean(CommandInfo cmdinfo) {
        return parent.getBean(cmdinfo);
    }

    @Override
    public CommandInfo getCommand(String cmdName) {
        return parent.getCommand(cmdName);
    }

    @Override
    public Object getContent() throws IOException {
        return parent.getContent();
    }

    @Override
    public String getContentType() {
        return parent.getContentType();
    }

    @Override
    public DataSource getDataSource() {
        return parent.getDataSource();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return parent.getInputStream();
    }

    @Override
    public String getName() {
        return parent.getName();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return parent.getOutputStream();
    }

    @Override
    public CommandInfo[] getPreferredCommands() {
        return parent.getPreferredCommands();
    }

    @Override
    public Object getTransferData(ActivationDataFlavor flavor)
            throws IOException {
        return parent.getTransferData(flavor);
    }

    @Override
    public ActivationDataFlavor[] getTransferDataFlavors() {
        return parent.getTransferDataFlavors();
    }

    @Override
    public boolean isDataFlavorSupported(ActivationDataFlavor flavor) {
        return parent.isDataFlavorSupported(flavor);
    }

    @Override
    public void setCommandMap(CommandMap commandMap) {
        parent.setCommandMap(commandMap);
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        parent.writeTo(os);
    }
}
