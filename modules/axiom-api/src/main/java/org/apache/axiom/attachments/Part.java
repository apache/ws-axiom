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

import javax.activation.DataHandler;
import javax.mail.MessagingException;

/**
 * Interface representing a MIME part. A part can be the SOAP part (or more generally the root part
 * for non-MTOM XOP encoded messages) or an attachment part.
 */
public interface Part {
    /**
     * @return DataHandler representing this part
     */
    public DataHandler getDataHandler();
    
    /**
     * @return size
     */
    public long getSize();

    /**
     * @return content type of the part
     */
    public String getContentType();

    /**
     * @return content id of the part
     */
    public String getContentID();

    /**
     * Get the value of a specific header
     * @param name
     * @return value or null
     * @throws MessagingException
     */
    public String getHeader(String name) throws MessagingException;
}
