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
package org.apache.axiom.ts.soap;

import javax.xml.namespace.QName;

import org.apache.axiom.testing.multiton.Multiton;

/** Describes an attribute that can appear on a SOAP header block. */
public abstract class HeaderBlockAttribute extends Multiton {
    public static final HeaderBlockAttribute MUST_UNDERSTAND =
            new HeaderBlockAttribute() {
                @Override
                public String getName(SOAPSpec spec) {
                    return "mustUnderstand";
                }

                @Override
                public boolean isBoolean() {
                    return true;
                }

                @Override
                public boolean isSupported(SOAPSpec spec) {
                    return true;
                }
            };

    public static final HeaderBlockAttribute ROLE =
            new HeaderBlockAttribute() {
                @Override
                public String getName(SOAPSpec spec) {
                    return spec == SOAPSpec.SOAP11 ? "actor" : "role";
                }

                @Override
                public boolean isBoolean() {
                    return false;
                }

                @Override
                public boolean isSupported(SOAPSpec spec) {
                    return true;
                }
            };

    public static final HeaderBlockAttribute RELAY =
            new HeaderBlockAttribute() {
                @Override
                public String getName(SOAPSpec spec) {
                    return "relay";
                }

                @Override
                public boolean isBoolean() {
                    return true;
                }

                @Override
                public boolean isSupported(SOAPSpec spec) {
                    return spec == SOAPSpec.SOAP12;
                }
            };

    /**
     * Get the name of the attribute.
     *
     * @param spec identifies the SOAP version
     * @return the name of the attribute in the given SOAP version
     */
    public abstract String getName(SOAPSpec spec);

    public final QName getQName(SOAPSpec spec) {
        return new QName(spec.getEnvelopeNamespaceURI(), getName(spec));
    }

    /**
     * Determine if the attribute is a boolean attribute.
     *
     * @return <code>true</code> if this is a boolean attribute
     */
    public abstract boolean isBoolean();

    /**
     * Determine if the attribute is supported by the given SOAP version.
     *
     * @param spec identifies the SOAP version
     * @return <code>true</code> if the attribute is supported, <code>false</code> otherwise
     */
    public abstract boolean isSupported(SOAPSpec spec);
}
