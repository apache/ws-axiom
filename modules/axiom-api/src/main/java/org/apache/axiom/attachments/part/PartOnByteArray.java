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

package org.apache.axiom.attachments.part;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import com.ibm.jvm.util.ByteArrayOutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import org.apache.axiom.om.OMException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/*
 * PartOnByteArray stores Attachment part data in to a byteArray
 * DataSource and provides a DataHandler to access it. 
 */
public class PartOnByteArray extends DynamicPart {
    protected static Log log = LogFactory.getLog(PartOnByteArray.class);
    private static final int bufferSize = 8192;
    ByteArrayDataSource ds;

    public PartOnByteArray(InputStream in){
        if(log.isDebugEnabled()){
            log.debug("Creating Part On Byte Array");
        }
        headers = new Hashtable();  
        int value;
        try{
            value = parseTheHeaders(in);
        }catch(IOException e){
            throw new OMException("Error Parsing Header "+e);
        }   
        try{
            if(log.isDebugEnabled()){
                log.debug("Reading byteArray from stream");
            }
            //Read all bytes minus header to the byte Array;                
            byte[] byteArray= getBytes(in, value);

            ds = new ByteArrayDataSource(byteArray, getEncoding());
            if(log.isDebugEnabled()){
                log.debug("Length of bytes read ="+((byteArray!=null)?byteArray.length:0));               
            }
        }catch(Exception e){
            throw new OMException("Error creating data source "+e);
        }       
        if(log.isDebugEnabled()){
            log.debug("Part On Byte Array Created");
        }
    }

    /** Get Bytes from InputStream for creating DataSource. The return value from
     * parsed header is inserted first in the byte array and then read
     * rest of the stream to create complete byte buffer for a input part. 
     * @param in
     * @param value
     * @return
     * @throws IOException
     */
    private static byte[] getBytes(InputStream in, int value) throws IOException{
        int bytesRead = 0, totalBytes = 0;
        if(!(in instanceof ByteArrayInputStream) && 
            !(in instanceof BufferedInputStream)){
            if(log.isDebugEnabled()){
                log.debug("InputStream is not BufferedInputStream");
            }
            in = new BufferedInputStream(in);
        }

        if(in instanceof ByteArrayInputStream){
            if(log.isDebugEnabled()){
                log.debug("input stream a ByteArrayInputStream");
            }
            int len = in.available();
            byte[] byteArray = new byte[len + 1];
            byteArray[0] = new Integer(value).byteValue();
            //create byteArray here.
            in.read(byteArray, 1, len);

            if(log.isDebugEnabled()){
                log.debug("Total Bytes Read ="+len);
            }

            return byteArray;
        }
        else{
            if(log.isDebugEnabled()){
                log.debug("input stream not a ByteArrayInputStream");
            }
            byte[] b = new byte[bufferSize];
            //TODO Should I create my own bufferStream to return the original buf
            ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferSize);
            baos.write(value);
            while((bytesRead = in.read(b))>0){
                totalBytes+=bytesRead;
                baos.write(b, 0, bytesRead);
            }
            if(log.isDebugEnabled()){
                log.debug("Total Bytes Read ="+totalBytes);
            }
            return baos.toByteArray();
        }

    }

    public DataHandler getDataHandler() throws MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getDataHandler()");
        }
        ds.setType(getContentType());
        return new DataHandler(ds);
    }

    public String getFileName() throws MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getFileName()");
        }
        throw new UnsupportedOperationException("Not Supported");
    }

    public InputStream getInputStream() throws IOException, MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getInputStream()");
        }
        return ds.getInputStream();
    }

    public int getSize() throws MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getSize()");
        }
        return ds.getSize();
    }

    class ByteArrayDataSource implements DataSource {

        private byte[] data;
        private String type;
        private String encoding;
        public ByteArrayDataSource(byte[] data,  String encoding) {
            super();
            this.data = data;
            this.encoding = encoding;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            if(log.isDebugEnabled()){
                log.debug("ByteArrayDataSource getInputStream()");
            }
            InputStream in = new ByteArrayInputStream(data == null ? new byte[0] : data);
            if(log.isDebugEnabled()){
                log.debug("Stream Encoding = "+encoding);
            }
            if(encoding !=null){
                try{
                    if(log.isDebugEnabled()){
                        log.debug("Start Decoding stream");
                    }
                    return MimeUtility.decode(in, encoding);
                }catch(Exception e){
                    if(log.isDebugEnabled()){
                        log.debug("Stream Failed decoding for encoding ="+encoding);
                    }
                }
            }
            return in;
        }

        public String getName() {

            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }

        public int getSize(){
            return (data!=null)?data.length:0;
        }
    }
}
