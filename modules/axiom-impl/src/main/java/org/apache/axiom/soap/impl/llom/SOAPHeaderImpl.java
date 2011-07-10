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

package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
interface Checker {
    boolean checkHeader(SOAPHeaderBlock header);
}

/**
 * A Checker to make sure headers match a given role.  If the role we're looking for is null, then
 * everything matches.
 */
class RoleChecker implements Checker {
    String role;

    public RoleChecker(String role) {
        this.role = role;
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        if (role == null) {
            return true;
        }
        String thisRole = header.getRole();
        return (role.equals(thisRole));
    }
}

/**
 * This Checker uses a RolePlayer to return the appropriate headers for that RolePlayer to process.
 * Ignore "none", always "next", etc.
 */
class RolePlayerChecker implements Checker {
    RolePlayer rolePlayer;

    /** Optional namespace - if non-null we'll only return headers that match */
    String namespace;

    /**
     * Constructor.
     *
     * @param rolePlayer the RolePlayer to check against.  This can be null, in which
     *                   case we assume we're the ultimate destination.
     */
    public RolePlayerChecker(RolePlayer rolePlayer) {
        this.rolePlayer = rolePlayer;
    }

    public RolePlayerChecker(RolePlayer rolePlayer, String namespace) {
        this.rolePlayer = rolePlayer;
        this.namespace = namespace;
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        // If we're filtering on namespace, check that first since the compare is simpler.
        if (namespace != null) {
            OMNamespace headerNamespace = header.getNamespace();
            if (headerNamespace == null || !namespace.equals(headerNamespace.getNamespaceURI())) {
                return false;
            }
        }

        String role = header.getRole();
        SOAPVersion version = header.getVersion();

        // 1. If role is ultimatedest, go by what the rolePlayer says
        if (role == null || role.equals("") ||
                (version instanceof SOAP12Version &&
                        role.equals(SOAP12Constants.SOAP_ROLE_ULTIMATE_RECEIVER))) {
            return (rolePlayer == null || rolePlayer.isUltimateDestination());
        }

        // 2. If role is next, always return true
        if (role.equals(version.getNextRoleURI())) return true;

        // 3. If role is none, always return false
        if (version instanceof SOAP12Version &&
                role.equals(SOAP12Constants.SOAP_ROLE_NONE)) {
            return false;
        }

        // 4. Return t/f depending on match
        List roles = (rolePlayer == null) ? null : rolePlayer.getRoles();
        if (roles != null) {
            for (Iterator i = roles.iterator(); i.hasNext();) {
                String thisRole = (String) i.next();
                if (thisRole.equals(role)) return true;
            }
        }

        return false;
    }
}

/** A Checker to see that we both match a given role AND are mustUnderstand=true */
class MURoleChecker extends RoleChecker {
    public MURoleChecker(String role) {
        super(role);
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        if (header.getMustUnderstand())
            return super.checkHeader(header);
        return false;
    }
}

/** A class representing the SOAP Header, primarily allowing access to the contained HeaderBlocks. */
public abstract class SOAPHeaderImpl extends SOAPElement implements SOAPHeader {
    
    static Log log = LogFactory.getLog(SOAPHeaderImpl.class);
    /** An Iterator which walks the header list as needed, potentially filtering as we traverse. */
    class HeaderIterator implements Iterator {
        SOAPHeaderBlock current;
        boolean advance = false;
        Checker checker;

        public HeaderIterator() {
            this(null);
        }

        public HeaderIterator(Checker checker) {
            this.checker = checker;
            current = (SOAPHeaderBlock) getFirstElement();
            if (current != null) {
                if (!checkHeader(current)) {
                    advance = true;
                    hasNext();
                }
            }
        }

        public void remove() {
        }

        public boolean checkHeader(SOAPHeaderBlock header) {
            if (checker == null) return true;
            return checker.checkHeader(header);
        }

        public boolean hasNext() {
            if (!advance) {
                return current != null;
            }

            advance = false;
            OMNode sibling = current.getNextOMSibling();

            while (sibling != null) {
                if (sibling instanceof SOAPHeaderBlock) {
                    SOAPHeaderBlock possible = (SOAPHeaderBlock) sibling;
                    if (checkHeader(possible)) {
                        current = (SOAPHeaderBlock) sibling;
                        return true;
                    }
                }
                sibling = sibling.getNextOMSibling();
            }

            current = null;
            return false;
        }

        public Object next() {
            SOAPHeaderBlock ret = current;
            if (ret != null) {
                advance = true;
                hasNext();
            }
            return ret;
        }
    }


    protected SOAPHeaderImpl(OMNamespace ns, SOAPFactory factory) {
        super(SOAPConstants.HEADER_LOCAL_NAME, ns, factory);
    }

    public SOAPHeaderImpl(SOAPEnvelope envelope, SOAPFactory factory)
            throws SOAPProcessingException {
        super(envelope, SOAPConstants.HEADER_LOCAL_NAME, true, factory);

    }

    /**
     * Constructor SOAPHeaderImpl
     *
     * @param envelope
     * @param builder
     */
    public SOAPHeaderImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder,
                          SOAPFactory factory) {
        super(envelope, SOAPConstants.HEADER_LOCAL_NAME, builder, factory);
    }

    public SOAPHeaderBlock addHeaderBlock(String localName, OMNamespace ns)
            throws OMException {
        
        if (ns == null || ns.getNamespaceURI().length() == 0) {
            throw new OMException(
                    "All the SOAP Header blocks should be namespace qualified");
        }
        
        OMNamespace namespace = findNamespace(ns.getNamespaceURI(), ns.getPrefix());
        if (namespace != null) {
            ns = namespace;
        }
        
        SOAPHeaderBlock soapHeaderBlock;
        try {
            soapHeaderBlock = createHeaderBlock(localName, ns);
        } catch (SOAPProcessingException e) {
            throw new OMException(e);
        }
        ((OMNodeEx) soapHeaderBlock).setComplete(true);
        return soapHeaderBlock;
    }

    protected abstract SOAPHeaderBlock createHeaderBlock(String localname, OMNamespace ns);
    
    public Iterator getHeadersToProcess(RolePlayer rolePlayer) {
        return new HeaderIterator(new RolePlayerChecker(rolePlayer));
    }

    public Iterator getHeadersToProcess(RolePlayer rolePlayer, String namespace) {
        return new HeaderIterator(new RolePlayerChecker(rolePlayer, namespace));
    }

    public Iterator examineHeaderBlocks(String role) {
        return new HeaderIterator(new RoleChecker(role));
    }

    public abstract Iterator extractHeaderBlocks(String role);

    public Iterator examineMustUnderstandHeaderBlocks(String actor) {
        return new HeaderIterator(new MURoleChecker(actor));
    }

    public Iterator examineAllHeaderBlocks() {
        class DefaultChecker implements Checker {
            public boolean checkHeader(SOAPHeaderBlock header) {
                return true;
            }
        }

        return new HeaderIterator(new DefaultChecker());
    }

    public Iterator extractAllHeaderBlocks() {
        List result = new ArrayList();
        for (Iterator iter = getChildElements(); iter.hasNext();) {
            OMElement headerBlock = (OMElement) iter.next();
            iter.remove();
            result.add(headerBlock);
        }
        return result.iterator();
    }

    public ArrayList getHeaderBlocksWithNSURI(String nsURI) {
        ArrayList headers = null;
        OMNode node;
        OMElement header = this.getFirstElement();

        if (header != null) {
            headers = new ArrayList();
        }

        node = header;

        while (node != null) {
            if (node.getType() == OMNode.ELEMENT_NODE) {
                header = (OMElement) node;
                OMNamespace namespace = header.getNamespace();
                if (nsURI == null) {
                    if (namespace == null) {
                        headers.add(header);
                    }
                } else {
                    if (namespace != null) {
                        if (nsURI.equals(namespace.getNamespaceURI())) {
                            headers.add(header);
                        }
                    }
                }
            }
            node = node.getNextOMSibling();

        }
        return headers;

    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAPEnvelopeImpl)) {
            throw new SOAPProcessingException(
                    "Expecting an implementation of SOAP Envelope as the parent. But received some other implementation");
        }
    }

    
    public void addChild(OMNode child) {
        
        // Make sure a proper element is added.  The children of a SOAPHeader should be
        // SOAPHeaderBlock objects.
        // Due to legacy usages (AXIS2 has a lot of tests that violate this constraint)
        // I am only going to log an exception when debug is enabled. 
        if (log.isDebugEnabled()) {
            if (child instanceof OMElement &&
            !(child instanceof SOAPHeaderBlock)) {
                Exception e = new SOAPProcessingException(
                  "An attempt was made to add a normal OMElement as a child of a SOAPHeader." +
                  "  This is not supported.  The child should be a SOAPHeaderBlock.");
                log.debug(exceptionToString(e));
            }
        }
        super.addChild(child);
    }
    
    public static String exceptionToString(Throwable e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.BufferedWriter bw = new java.io.BufferedWriter(sw);
        java.io.PrintWriter pw = new java.io.PrintWriter(bw);
        e.printStackTrace(pw);
        pw.close();
        String text = sw.getBuffer().toString();
        return text;
    }
}
