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

import javax.activation.DataHandler;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.mime.activation.PartDataHandler;
import org.apache.axiom.mime.activation.PartDataHandlerBlob;

/**
 * Contains utility methods to work with {@link DataHandler} objects.
 */
public final class DataHandlerUtils {
    private DataHandlerUtils() {}

    /**
     * Get the {@link DataHandler} wrapped by the given {@link Blob}.
     * 
     * @param blob the {@link Blob} to unwrap
     * @return the wrapped {@link DataHandler}, or {@code null} if the blob doesn't wrap a {@link DataHandler}
     */
    public static DataHandler getDataHandler(Blob blob) {
        if (blob instanceof DataHandlerBlob) {
            return ((DataHandlerBlob)blob).getDataHandler();
        }
        if (blob instanceof PartDataHandlerBlob) {
            return ((PartDataHandlerBlob)blob).getDataHandler();
        }
        return null;
    }

    /**
     * Get a {@link DataHandler} for the given {@link Blob}. If the blob was obtained from {@link
     * #toBlob(DataHandler)}, the original {@link DataHandler} is returned.
     * 
     * @param blob the {@link Blob} to convert
     * @return a {@link DataHandler} representing the {@link Blob}
     */
    public static DataHandler toDataHandler(Blob blob) {
        DataHandler dh = getDataHandler(blob);
        return dh != null ? dh : new BlobDataHandler(blob);
    }

    /**
     * Get a {@link Blob} for the given {@link DataHandler}. If the {@link DataHandler} was obtained from {@link
     * #toDataHandler(Blob)}, the original {@link Blob} is returned.
     * 
     * @param dh the {@link DataHandler} to convert
     * @return a {@link Blob} representing the {@link DataHandler}
     */
    public static Blob toBlob(DataHandler dh) {
        if (dh instanceof BlobDataHandler) {
            return ((BlobDataHandler)dh).getBlob();
        }
        if (dh instanceof PartDataHandler) {
            return ((PartDataHandler)dh).getBlob();
        }
        return new DataHandlerBlob(dh);
    }
}
