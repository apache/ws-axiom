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
package org.apache.axiom.soap.impl.common;

import org.apache.axiom.soap.SOAPHeaderBlock;

/**
 * A local interface we can use to make "header checker" objects which can be used by
 * HeaderIterators to filter results.  This really SHOULD be done with anonymous classes:
 * <p/>
 * public void getHeadersByRole(final String role) {
 *     return new HeaderIterator() {
 *         public boolean checkHeader(SOAPHeaderBlock header) {
 *             ...
 *             if (role.equals(headerRole)) return true;
 *             return false;
 *         }
 *     }
 * }
 * <p/>
 * ...but there appears to be some kind of weird problem with the JVM not correctly scoping the
 * passed "role" value in a situation like the above.  As such, we have to make Checker objects
 * instead (sigh).
 */
public interface Checker {
    boolean checkHeader(SOAPHeaderBlock header);
}
