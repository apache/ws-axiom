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

package org.apache.axiom.util.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Blob {
    OutputStream getOutputStream();

    /**
     * Fill this object with data read from a given InputStream.
     * <p>
     * A call <code>tmp.readFrom(in)</code> has the same effect as the
     * following code:
     * <pre>
     * OutputStream out = tmp.getOutputStream();
     * IOUtils.copy(in, out);
     * out.close();
     * </pre>
     * However it does so in a more efficient way.
     * 
     * @param in An InputStream to read data from. This method will not
     *           close the stream.
     * @throws IOException
     */
    void readFrom(InputStream in) throws IOException;

    InputStream getInputStream() throws IOException;

    /**
     * Write the data to a given output stream.
     * 
     * @param out The output stream to write the data to. This method will
     *            not close the stream.
     * @throws IOException
     */
    void writeTo(OutputStream out) throws IOException;

    long getLength();
}