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

package org.apache.axiom.util.stax.xop;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.impl.builder.DataHandlerReaderUtils;

public class XOPUtils {
    private static final MimePartProvider nullMimePartProvider = new MimePartProvider() {
        public boolean isLoaded(String contentID) {
            throw new IllegalArgumentException("There are no MIME parts!");
        }
        
        public DataHandler getDataHandler(String contentID) throws IOException {
            throw new IllegalArgumentException("There are no MIME parts!");
        }
    };
    
    private XOPUtils() {}
    
    /**
     * Get an XOP encoded stream for a given stream reader. Depending on its
     * type and characteristics, this method may wrap or unwrap the stream
     * reader:
     * <ol>
     * <li>If the original reader is an {@link XOPEncodingStreamReader} it will
     * be preserved, since it is already XOP encoded.
     * <li>If the original reader is an {@link XOPDecodingStreamReader}, it will
     * be unwrapped to give access to the underlying XOP encoded reader.
     * <li>If the original reader is a plain XML stream reader implementing the
     * {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader}
     * extension, it will be wrapped in an {@link XOPEncodingStreamReader} so
     * that optimized binary data can be transferred using XOP.
     * <li>In all other cases, the original reader is simply preserved.
     * </ol>
     * 
     * @param reader
     *            the original reader
     * @return the XOP encoded stream
     */
    public static XOPEncodedStream getXOPEncodedStream(XMLStreamReader reader) {
        if (reader instanceof XOPEncodingStreamReader) {
            return new XOPEncodedStream(reader, (MimePartProvider)reader);
        } else if (reader instanceof XOPDecodingStreamReader) {
            return ((XOPDecodingStreamReader)reader).getXOPEncodedStream();
        } else if (DataHandlerReaderUtils.getDataHandlerReader(reader) != null) {
            XOPEncodingStreamReader wrapper = new XOPEncodingStreamReader(reader,
                    ContentIDGenerator.DEFAULT, OptimizationPolicy.ALL);
            return new XOPEncodedStream(wrapper, wrapper);
        } else {
            return new XOPEncodedStream(reader, nullMimePartProvider);
        }
    }
}
