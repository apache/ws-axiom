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

import org.apache.axiom.attachments.MIMEBodyPartInputStream;
import org.apache.axiom.attachments.Part;
import org.apache.axiom.attachments.PushbackFilePartInputStream;
import org.apache.axiom.om.OMException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.HeaderTokenizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
/*
 * Dynamic Part represents attachment Part, Inbound Attachment Data can be
 * be represend by a http strem of fixed length or as chunks. Dynamic Part
 * Supports both type of attachment part, it stores part content as a byte 
 * array or in a file based on the file threshold and attachment part size.  
 */
public abstract class DynamicPart implements Part {
    protected static Log log = LogFactory.getLog(DynamicPart.class);
    private static final int bufferSize = 8192;
    Hashtable headers = new Hashtable();

    /** Create Part for stream with chunked data.
     * @param in
     * @param repoDir
     * @param fileStorageThreshold
     * @return
     */
    public static DynamicPart createPart(MIMEBodyPartInputStream in, String repoDir, int fileStorageThreshold){
        if(log.isDebugEnabled()){
            log.debug("Start createPart()");
        }
        try{
            if (log.isDebugEnabled()) {
                log.debug("Buffering attachment part to determine if it should be stored in File");
            }

            byte[] buffer = toBuffer(in, fileStorageThreshold);             
            int count = (buffer!=null)?buffer.length:0;  
            if(log.isDebugEnabled()){
                log.debug("Total Byte read ="+count+" FileThresholdValue = "+fileStorageThreshold);
            }
            if(count < fileStorageThreshold){
                if(log.isDebugEnabled()){
                    log.debug("Storing Part On Byte Array");
                }
                InputStream bis = new ByteArrayInputStream(buffer);
                //Create PartOnByteArray
                return new PartOnByteArray(bis);
            }
            else{
                //Create PartOnFile
                if(log.isDebugEnabled()){
                    log.debug("Storing Part On File");
                }
                PushbackFilePartInputStream filePartStream =
                    new PushbackFilePartInputStream(in, buffer);
                return new PartOnFile(filePartStream, repoDir);
            }

        }catch (Exception e) {
            if(log.isDebugEnabled()){
                log.debug("Error in createPart(), throwing OMException");
            }
            throw new OMException("Error Creating DynamicPart", e);
        }
    }

    /**
     * Create Part for stream with fixed contentLength.
     * @param contentLength
     * @param partIndex
     * @param in
     * @param repoDir
     * @param fileStorageThreshold
     * @return
     */
    public static DynamicPart createPart(int contentLength, int partIndex, MIMEBodyPartInputStream in, String repoDir, int fileStorageThreshold){
        if(log.isDebugEnabled()){
            log.debug("Start createPart()");
        }
        try{
            if(log.isDebugEnabled()){
                log.debug("Content-Length = "+contentLength+ " FileStorageThreshold = "+fileStorageThreshold);
            }
            if(contentLength <= fileStorageThreshold || partIndex == 0){
                return new PartOnByteArray(in);
            }else{
                if(log.isDebugEnabled()){
                    log.debug("Creating Part On File");
                }
                PushbackFilePartInputStream filePartStream =
                    new PushbackFilePartInputStream(in, new byte[0]);
                //Create PartOnFile
                if(log.isDebugEnabled()){
                    log.debug("Creating PartOnFile");
                }
                return new PartOnFile(filePartStream, repoDir);
            }
        }catch(Exception e){
            if(log.isDebugEnabled()){
                log.debug("Error in createPart(), throwing OMException");
            }
            throw new OMException("Error Creating DynamicPart", e);
        }
    }


    public void addHeader(String arg0, String arg1) throws MessagingException {
        if(log.isDebugEnabled()){
            log.debug("addHeader");
        }
        Header headerObj = new Header(arg0, arg1);
        headers.put(arg0, headerObj);
    }

    public Enumeration getAllHeaders() throws MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getAllHeaders");
        }
        return headers.elements();
    }

    public String getContentID() throws MessagingException {	
        if(log.isDebugEnabled()){
            log.debug("getContentID");
        }
        Header cID = (Header) headers.get("Content-ID");
        if (cID == null) {
            cID = (Header) headers.get("Content-Id");
            if (cID == null) {
                cID = (Header) headers.get("Content-id");
                if (cID == null) {
                    cID = (Header) headers.get("content-id");
                }
            }
        }
        return cID.getValue();
    }

    public String getContentType() throws MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getContentType");
        }
        Header cType = (Header) headers.get("Content-Type");
        if (cType == null) {
            cType = (Header) headers.get("Content-type");
            if (cType == null) {
                cType = (Header) headers.get("content-type");
            }
        }
        return cType.getValue();
    }


    public String getHeader(String arg0) throws MessagingException {	
        return ((Header) headers.get(arg0)).getValue();
    }

    public abstract InputStream getInputStream() throws IOException, MessagingException;

    public abstract int getSize() throws MessagingException;

    public abstract DataHandler getDataHandler() throws MessagingException;

    public abstract String getFileName() throws MessagingException ;


    /**
     * Read bytes into buffer of 8k, expand by 8k if buffer is full.
     * Keep filling buffer until threshold is reached.
     * @param is
     * @param buffer
     * @return number of bytes read
     * @throws IOException
     * 
     */
 
    private static int readToBuffer(byte[] buffer, InputStream is, int fileStorageThreshold ) throws IOException {
        int index = 0;
        int remainder = fileStorageThreshold;
        int len = buffer.length;
        if(log.isDebugEnabled()){
            log.debug("Start Buffering Stream Data");
        }
        do {
            int bytesRead;
            while ((bytesRead = is.read(buffer, index, len)) > 0) {
                index += bytesRead;
                remainder -= bytesRead;
                if(remainder >0){
                    //expand buffer by twice the current size or to fileStorageThreshold value.
                    int newBufSize = ((buffer.length * 2) > fileStorageThreshold)?fileStorageThreshold:(buffer.length * 2);
                    buffer = expandBuffer(buffer, newBufSize);
                }
                len = buffer.length-index;
            }
        } while (remainder > 0 && is.available() > 0);  // repeat if more bytes are now available

        if(log.isDebugEnabled()){
            log.debug("Done Creating Buffer, total bytes read " +index);
        }
        return index;
    }


    /**
     * create byte buffer from input stream.  
     * @param is
     * @param buffer
     * @return number of bytes read
     * @throws IOException
     */
    private static byte[] toBuffer(InputStream is, int fileStorageThreshold ) throws IOException {
        int index = 0, bytesRead=0;
        int remainder = fileStorageThreshold;

        if(log.isDebugEnabled()){
            log.debug("Start Buffering Stream Data");
        }

        int len =0;
        len = is.available();
        //if buffer size (8k) is greater than available byte size, user availabe size
        // or lets just read 8k first.
        len = (len >0 && bufferSize > len)?len:bufferSize;        
        //if available or buffersize is greater than fileStorageThreshold lets just use
        //fileStorageThreshold as we should not be reading bytes more than threshold value.
        len = (len>fileStorageThreshold)?fileStorageThreshold:len;
        if(log.isDebugEnabled()){
            log.debug("Initial Length "+len);
        }
        byte[] b = new byte[len];

        PartOutputStream baos = null;
        do {
            while(remainder>0 &&((bytesRead = is.read(b))>0)){
                index += bytesRead;
                remainder -= bytesRead;
                //Lets initialize Outputstream with initial buf size as bytesRead.
                if(baos == null){
                    baos = new PartOutputStream(bytesRead);
                }
                //Write read bytes to outputstream
                baos.write(b, 0, bytesRead);
                //Compute new length, so if there are still bytes available to be read
                //and we have not hit the file threshold limit. Lets just try to expand
                //buffer to twice its current size. 
                if(remainder >0){
                    len = ((len * 2) > fileStorageThreshold)?fileStorageThreshold:(len * 2);
                    //Twice buffer size greater than remainder, just 
                    //read remainder.
                    b = null; //==> Garbage Collect.
                    len = (len>remainder)?remainder:len;
                    if(log.isDebugEnabled()){
                        log.debug("New Length "+len);
                    }
                    b = new byte[len];
                }
            }
        } while (remainder > 0 && is.available() > 0);  // repeat if more bytes are now available

        b = null; 
        

        if(log.isDebugEnabled()){
            log.debug("Total Bytes Read ="+index);
        }
        //return outputStream buffer;
        return  (baos!=null)?baos.getBytes():new byte[0];
    }

    private static byte[] expandBuffer(byte[] buffer, int newBufSize){
        if(buffer == null){
            if(log.isDebugEnabled()){
                log.debug("Initializing Buffer to be 8K in size");
            }
            return new byte[bufferSize];
        }
        if(log.isDebugEnabled()){
            log.debug("Expanding Buffer to be of size ="+newBufSize);
        }
        byte[] newBuffer =new byte[newBufSize];
        //copy old buffer content to new expanded buffer
        System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
        //return new buffer;
        return newBuffer;
    }

    int  parseTheHeaders(InputStream inStream) throws IOException {
        int value;
        if(log.isDebugEnabled()){
            log.debug("Start Parsing Header");
        }
        boolean readingHeaders = true;
        StringBuffer header = new StringBuffer();
        while (readingHeaders & (value = inStream.read()) != -1) {
            if (value == 13) {
                if ((value = inStream.read()) == 10) {
                    if ((value = inStream.read()) == 13) {
                        if ((value = inStream.read()) == 10) {
                            putToMap(header);
                            readingHeaders = false;
                        }
                    } else {
                        putToMap(header);
                        header = new StringBuffer();
                        header.append((char) value);
                    }
                } else {
                    header.append(13);
                    header.append(value);
                }
            } else {
                header.append((char) value);
            }
        }
        if(log.isDebugEnabled()){
            log.debug("End Parsing Header");
        }
        return value;
    }

    private void putToMap(StringBuffer header) {
        String headerString = header.toString();
        int delimiter = headerString.indexOf(":");
        String name = headerString.substring(0, delimiter).trim();
        String value = headerString.substring(delimiter + 1, headerString.length()).trim();
        if(log.isDebugEnabled()){
            log.debug("Header name ="+name+ " value = "+value);
        }
        Header headerObj = new Header(name, value);
        headers.put(name, headerObj);
    }

    public String getEncoding() throws MessagingException{
        if(log.isDebugEnabled()){
            log.debug("getEncoding()");
        }
        String encoding = null;
        Header cType = (Header) headers.get("Content-Transfer-Encoding");
        if (cType == null) {
            cType = (Header) headers.get("content-transfer-encoding");            
        }

        if(cType!=null){
            encoding = cType.getValue();
        }
        if(encoding!=null){
            encoding = encoding.trim();
            if(log.isDebugEnabled()){
                log.debug("Encoding =" + encoding);
            }
            if(encoding.equalsIgnoreCase("7bit") || 
                encoding.equalsIgnoreCase("8bit") ||
                encoding.equalsIgnoreCase("qyited-printable") ||
                encoding.equalsIgnoreCase("base64")){

                return encoding;
            }
            
            HeaderTokenizer ht = new HeaderTokenizer(encoding, HeaderTokenizer.MIME);
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
            return encoding;
        }
        return null;


    }



}
