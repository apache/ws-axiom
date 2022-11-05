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
package org.apache.axiom.om.format.xop;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.mime.ContentType;

/**
 * Determines the content type for non-root MIME parts in an XOP package. Note that in an
 * XOP package, the {@code Content-Type} header is only required for the root part, and the
 * content type of non-root MIME parts is not part of the infoset. This API purely
 * exists to customize the serialization of an XML infoset into an XOP package.
 */
public interface ContentTypeProvider {
    /**
     * Determine the content type to use for a MIME part.
     * 
     * @param blob the content of the MIME part
     * @return the content type, or {@code null} if there is no specific content type
     */
    ContentType getContentType(Blob blob);
}
