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
import org.apache.axiom.om.OMException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.MimeTokenStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
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
    
    private final MIMEMessage message;
    private final boolean isSOAPPart;
    
    // Key is the lower-case name.
    // Value is a javax.mail.Header object
    private Hashtable headers;
    
    /**
     * The MIME parser from which the content of this part is read. This is only set if the content
     * has not been read yet. In this case the parser is in state {@link EntityState#T_BODY}.
     */
    private MimeTokenStream parser;
    
    /**
     * The content of this part. This is only set if the content of the part is buffered.
     */
    private ContentStore content;
    
    private final DataHandler dataHandler;
    
    /**
     * The actual parts are constructed with the PartFactory.
     * @see org.apache.axiom.attachments.ContentStoreFactory
     * @param headers
     */
    PartImpl(MIMEMessage message, boolean isSOAPPart, Hashtable in, MimeTokenStream parser) {
        this.message = message;
        this.isSOAPPart = isSOAPPart;
        headers = in;
        if (headers == null) {
            headers = new Hashtable();
        }
        this.parser = parser;
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
        return getContent().getSize();
    }

    private ContentStore getContent() {
        if (content == null) {
            if (parser == null) {
                throw new IllegalStateException("The content of the MIME part has already been consumed");
            } else {
                fetch();
            }
        }
        return content;
    }
    
    /**
     * Make sure that the MIME part has been fully read from the parser. If the part has not been
     * read yet, then it will be buffered. This method prepares the parser for reading the next part
     * in the stream.
     */
    void fetch() {
        if (content == null && parser != null) {
            // The PartFactory will determine which Part implementation is most appropriate.
            content = ContentStoreFactory.createContentStore(message.getLifecycleManager(), parser, 
                                          isSOAPPart, 
                                          message.getThreshold(),
                                          message.getAttachmentRepoDir(),
                                          message.getContentLengthIfKnown());  // content-length for the whole message
            try {
                EntityState state = parser.next();
                if (state == EntityState.T_EPILOGUE) {
                    while (parser.next() != EntityState.T_END_MULTIPART) {
                        // Just loop
                    }
                } else if (state != EntityState.T_START_BODYPART && state != EntityState.T_END_MULTIPART) {
                    throw new IllegalStateException("Internal error: unexpected parser state " + state);
                }
            } catch (IOException ex) {
                throw new OMException(ex);
            } catch (MimeException ex) {
                throw new OMException(ex);
            }
        }
    }
    
    InputStream getInputStream() throws IOException {
        return getContent().getInputStream();
    }
    
    DataSource getDataSource() {
        return getContent().getDataSource(getContentType());
    }

    void writeTo(OutputStream out) throws IOException {
        getContent().writeTo(out);
    }

    void releaseContent() throws IOException {
        if (content != null) {
            content.destroy();
        } else if (parser != null) {
            try {
                EntityState state;
                do {
                    state = parser.next();
                } while (state != EntityState.T_START_BODYPART && state != EntityState.T_END_MULTIPART);
            } catch (MimeException ex) {
                throw new OMException(ex);
            }
        }
    }
}
