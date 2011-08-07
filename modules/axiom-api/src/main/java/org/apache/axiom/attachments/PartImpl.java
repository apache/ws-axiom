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

import org.apache.axiom.attachments.Part;
import org.apache.axiom.attachments.impl.ContentStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.HeaderTokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * Actual implementation of the {@link Part} interface.
 */
final class PartImpl implements Part {

    private static Log log = LogFactory.getLog(PartImpl.class);
                                                 
    // Key is the lower-case name.
    // Value is a javax.mail.Header object
    private Hashtable headers;
    
    private final ContentStore content;
    private final DataHandler dataHandler;
    
    /**
     * The actual parts are constructed with the PartFactory.
     * @see org.apache.axiom.attachments.impl.ContentStoreFactory
     * @param headers
     */
    PartImpl(Hashtable in, ContentStore content) {
        headers = in;
        if (headers == null) {
            headers = new Hashtable();
        }
        this.content = content;
        this.dataHandler = new PartDataHandler(this);
    }
    
    public String getHeader(String name) {
        String key = name.toLowerCase();
        Header header = (Header) headers.get(key);
        String value = header == null ? null : header.getValue();
        if(log.isDebugEnabled()){
            log.debug("getHeader name=(" + name + ") value=(" + value +")");
        }
        return value;
    }

    public String getContentID() {
        return getHeader("content-id");
    }

    public String getContentType() {
        return getHeader("content-type");
    }
    
    /**
     * @return contentTransferEncoding
     * @throws MessagingException
     */
    public String getContentTransferEncoding() throws MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getContentTransferEncoding()");
        }
        String cte = getHeader("content-transfer-encoding");
       
        if(log.isDebugEnabled()){
            log.debug(" CTE =" + cte);
        }

        if(cte!=null){
            cte = cte.trim();
            
            if(cte.equalsIgnoreCase("7bit") || 
                cte.equalsIgnoreCase("8bit") ||
                cte.equalsIgnoreCase("quoted-printable") ||
                cte.equalsIgnoreCase("base64")){

                return cte;
            }
            
            HeaderTokenizer ht = new HeaderTokenizer(cte, HeaderTokenizer.MIME);
            boolean done = false;
            while(!done){
                HeaderTokenizer.Token token = ht.next();
                switch(token.getType()){
                case HeaderTokenizer.Token.EOF:
                    if(log.isDebugEnabled()){
                        log.debug("HeaderTokenizer EOF");
                    }
                    done = true;
                    break;
                case HeaderTokenizer.Token.ATOM:                    
                    return token.getValue();
                }
            }
            return cte;
        }
        return null;


    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public long getSize() {
        return content.getSize();
    }

    InputStream getInputStream() throws IOException {
        return content.getInputStream();
    }
    
    void writeTo(OutputStream out) throws IOException {
        content.writeTo(out);
    }

    void releaseContent() throws IOException {
        content.destroy();
    }
}
