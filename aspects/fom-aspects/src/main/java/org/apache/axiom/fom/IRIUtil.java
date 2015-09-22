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
package org.apache.axiom.fom;

import org.apache.abdera.i18n.iri.IRI;

public final class IRIUtil {
    private IRIUtil() {}

    public static IRI getUriValue(String v) {
        return (v != null) ? new IRI(v) : null;
    }

    public static String getStringValue(IRI uri) {
        return (uri != null) ? uri.toString() : null;
    }

    public static IRI resolve(IRI base, IRI value) {
        return base != null ? base.resolve(value) : value;
    }

    public static String normalize(String iri) {
        return iri == null ? null : new IRI(iri).toString();
    }
}
