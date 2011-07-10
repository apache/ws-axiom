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

package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;

import java.util.ArrayList;
import java.util.Iterator;

/** Interface SOAPHeader */
public interface SOAPHeader extends OMElement {
    /**
     * Creates a new {@link SOAPHeaderBlock} object initialized with the specified name and adds it
     * to this {@link SOAPHeader} object.
     * 
     * @param localName
     * @param ns
     * @return the new {@link SOAPHeaderBlock} object that was inserted into this {@link SOAPHeader}
     *         object
     * @throws OMException
     *             if a SOAP error occurs
     */
    // TODO: specify that the element to be added must have a namespace
    SOAPHeaderBlock addHeaderBlock(String localName, OMNamespace ns) throws OMException;

    /**
     * Get the appropriate set of headers for a {@link RolePlayer}.
     * <p>
     * The {@link RolePlayer} indicates whether it is the ultimate destination (in which case
     * headers with no role or the explicit UltimateDestination role will be included), and any
     * non-standard roles it supports. Headers targeted to "next" will always be included, and those
     * targeted to "none" (for SOAP 1.2) will never be included.
     * 
     * @param rolePlayer
     *            the {@link RolePlayer} object specifying the role configuration
     * @return an iterator over all the {@link SOAPHeaderBlock} objects the RolePlayer should
     *         process
     */
    Iterator getHeadersToProcess(RolePlayer rolePlayer);

    /**
     * Returns a list of all the {@link SOAPHeaderBlock} objects in this {@link SOAPHeader} object
     * that have the the specified role. An role is a global attribute that indicates the
     * intermediate parties to whom the message should be sent. A role receives the message and then
     * sends it to the next role. The default role is the ultimate intended recipient for the
     * message, so if no role attribute is included in a {@link SOAPHeaderBlock} object, the message
     * is sent to its ultimate destination.
     * 
     * @param role
     *            the URI of the role for which to search
     * @return an iterator over all the {@link SOAPHeaderBlock} objects that contain the specified
     *         role
     * @see #extractHeaderBlocks(String)
     */
    Iterator examineHeaderBlocks(String role);

    /**
     * Returns a list of all the {@link SOAPHeaderBlock} objects in this {@link SOAPHeader} object
     * that have the the specified role and detaches them from this {@link SOAPHeader} object.
     * <p>
     * This method allows an role to process only the parts of the {@link SOAPHeader} object that
     * apply to it and to remove them before passing the message on to the next role.
     * 
     * @param role
     *            the URI of the role for which to search
     * @return an iterator over all the {@link SOAPHeaderBlock} objects that contain the specified
     *         role
     * @see #examineHeaderBlocks(String)
     */
    Iterator extractHeaderBlocks(String role);

    /**
     * Returns an iterator over all the {@link SOAPHeaderBlock} objects in this {@link SOAPHeader}
     * object that have the specified role and that have a <tt>MustUnderstand</tt> attribute whose
     * value is equivalent to <code>true</code>.
     * 
     * @param role
     *            the URI of the role for which to search
     * @return an iterator over all the {@link SOAPHeaderBlock} objects that contain the specified
     *         role and are marked as MustUnderstand.
     */
    Iterator examineMustUnderstandHeaderBlocks(String role);

    /**
     * Returns an iterator over all the {@link SOAPHeaderBlock} objects in this {@link SOAPHeader}
     * object.
     * 
     * @return An iterator over all the {@link SOAPHeaderBlock} objects contained by this
     *         {@link SOAPHeader}. If there are no header blocks then an empty iterator is returned.
     */
    Iterator examineAllHeaderBlocks();

    /**
     * Returns an iterator over all the {@link SOAPHeaderBlock} objects in this {@link SOAPHeader}
     * object and detaches them from this {@link SOAPHeader} object.
     * 
     * @return an iterator over all the {@link SOAPHeaderBlock} objects contained by this
     *         {@link SOAPHeader}
     */
    Iterator extractAllHeaderBlocks();

    /**
     * Return all the Headers that has the Namespace URI to given NS URI.
     *
     * @param nsURI
     * @return Returns ArrayList.
     */
    ArrayList getHeaderBlocksWithNSURI(String nsURI);

    /**
     * Get the appropriate set of headers for a RolePlayer and a particular namespace.
     * <p>
     * The {@link RolePlayer} indicates whether it is the ultimate destination (in which case
     * headers with no role or the explicit UltimateDestination role will be included), and any
     * non-standard roles it supports. Headers targeted to "next" will always be included, and those
     * targeted to "none" (for SOAP 1.2) will never be included.
     * <p>
     * This version of the API allows us to iterate only once over the headers searching for a
     * particular namespace for headers targeted at "us".
     * 
     * @param rolePlayer
     *            the {@link RolePlayer} object specifying the role configuration
     * @param namespace
     *            if specified, we'll only return headers from this namespace
     * @return an iterator over all the {@link SOAPHeaderBlock} objects the RolePlayer should
     *         process
     */
    Iterator getHeadersToProcess(RolePlayer rolePlayer, String namespace);
}
