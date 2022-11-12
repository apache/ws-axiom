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

package org.apache.axiom.attachments.lifecycle;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;

public interface DataHandlerExt {
    /**
     * Get an {@link InputStream} that consumes the content of this data handler. This method is
     * similar to {@link DataHandler#getInputStream()} except that it can be invoked only once. If
     * the content has not been buffered yet, then the implementation may choose to enable streaming
     * of the content.
     * <p>
     * The implementation ensures that after the returned input steam is consumed, the data handler
     * will be in the same state as after a call to {@link #purgeDataSource()}.
     * 
     * @return the stream representing the content; never <code>null</code>
     * @throws IOException
     *             if an error occurs
     */
    InputStream readOnce() throws IOException;
	
	/**
	 * This method will give users an option to trigger a purge
	 * on temporary attachment files. Temp files are created for
	 * attachment data that is greater than a threshold limit. 
	 * On client side These temp attachment files are not deleted 
	 * untilthe virtual machine exits as user can choose to read 
	 * this dataHandler. So if user is not going to use the data 
	 * handlers provided on this temproray files they can choose 
	 * to purge the file. 
	 */
	public void purgeDataSource() throws IOException;
	
	/**
	 * @deprecated Use {@link #readOnce()} or {@link #purgeDataSource()} instead.
	 */
	public void deleteWhenReadOnce() throws IOException;
}
