/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A local interface we can use to make "header checker" objects which can be used
 * by HeaderIterators to filter results.  This really SHOULD be done with anonymous
 * classes:
 *
 * public void getHeadersByRole(final String role) {
 *     return new HeaderIterator() {
 *         public boolean checkHeader(SOAPHeaderBlock header) {
 *             ...
 *             if (role.equals(headerRole)) return true;
 *             return false;
 *         }
 *     }
 * }
 *
 * ...but there appears to be some kind of weird problem with the JVM not correctly
 * scoping the passed "role" value in a situation like the above.  As such, we have
 * to make Checker objects instead (sigh).
 */
interface Checker {
    boolean checkHeader(SOAPHeaderBlock header);
}

/**
 * A Checker to make sure headers match a given role.  If the role we're looking for is
 * null, then everything matches.
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
 * A Checker to see that we both match a given role AND are mustUnderstand=true
 */
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

/**
 * A class representing the SOAP Header, primarily allowing access to the contained
 * HeaderBlocks.
 */
public abstract class SOAPHeaderImpl extends SOAPElement implements SOAPHeader {
    /**
     * An Iterator which walks the header list as needed, potentially filtering
     * as we traverse.
     */
    class HeaderIterator implements Iterator {
        SOAPHeaderBlock current;
        boolean advance = false;
        Checker checker;

        public HeaderIterator() {
            this(null);
        }

        public HeaderIterator(Checker checker) {
            this.checker = checker;
            current = (SOAPHeaderBlock)getFirstElement();
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
                    SOAPHeaderBlock possible = (SOAPHeaderBlock)sibling;
                    if (checkHeader(possible)) {
                        current = (SOAPHeaderBlock)sibling;
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

    /**
     * Creates a new <CODE>SOAPHeaderBlock</CODE> object initialized with the specified name and
     * adds it to this <CODE>SOAPHeader</CODE> object.
     *
     * @param localName
     * @param ns
     * @return the new <CODE>SOAPHeaderBlock</CODE> object that was inserted into this
     *         <CODE>SOAPHeader</CODE> object
     * @throws org.apache.axiom.om.OMException
     *                     if a SOAP error occurs
     * @throws OMException
     */
    public abstract SOAPHeaderBlock addHeaderBlock(String localName,
                                                   OMNamespace ns)
            throws OMException;

    /**
     * Returns a list of all the <CODE>SOAPHeaderBlock</CODE> objects in this
     * <CODE>SOAPHeader</CODE> object that have the the specified actor. An actor is a global
     * attribute that indicates the intermediate parties to whom the message should be sent. An
     * actor receives the message and then sends it to the next actor. The default actor is the
     * ultimate intended recipient for the message, so if no actor attribute is included in a
     * <CODE>SOAPHeader</CODE> object, the message is sent to its ultimate destination.
     *
     * @param paramRole a <CODE>String</CODE> giving the URI of the actor for which to search
     * @return an <CODE>Iterator</CODE> object over all the <CODE> SOAPHeaderBlock</CODE> objects
     *         that contain the specified actor
     * @see #extractHeaderBlocks(String) extractHeaderBlocks(java.lang.String)
     */
    public Iterator examineHeaderBlocks(final String role) {
        return new HeaderIterator(new RoleChecker(role));
    }

    /**
     * Returns a list of all the <CODE>SOAPHeaderBlock</CODE> objects in this
     * <CODE>SOAPHeader</CODE> object that have the the specified role and detaches them from this
     * <CODE> SOAPHeader</CODE> object. <P>This method allows an role to process only the parts of
     * the <CODE>SOAPHeader</CODE> object that apply to it and to remove them before passing the
     * message on to the next role.
     *
     * @param role a <CODE>String</CODE> giving the URI of the role for which to search
     * @return an <CODE>Iterator</CODE> object over all the <CODE> SOAPHeaderBlock</CODE> objects
     *         that contain the specified role
     * @see #examineHeaderBlocks(String) examineHeaderBlocks(java.lang.String)
     */
    public abstract Iterator extractHeaderBlocks(String role);

    /**
     * Returns an <code>Iterator</code> over all the <code>SOAPHeaderBlock</code> objects in this
     * <code>SOAPHeader</code> object that have the specified actor and that have a MustUnderstand
     * attribute whose value is equivalent to <code>true</code>.
     *
     * @param actor a <code>String</code> giving the URI of the actor for which to search
     * @return an <code>Iterator</code> object over all the <code>SOAPHeaderBlock</code> objects
     *         that contain the specified actor and are marked as MustUnderstand
     */
    public Iterator examineMustUnderstandHeaderBlocks(final String actor) {
        return new HeaderIterator(new MURoleChecker(actor));
    }

    /**
     * Returns an <code>Iterator</code> over all the <code>SOAPHeaderBlock</code> objects in this
     * <code>SOAPHeader</code> object. Not that this will return elements containing the QName
     * (http://schemas.xmlsoap.org/soap/envelope/, Header)
     *
     * @return an <code>Iterator</code> object over all the <code>SOAPHeaderBlock</code> objects
     *         contained by this <code>SOAPHeader</code>
     */
    public Iterator examineAllHeaderBlocks() {
        class DefaultChecker implements Checker {
            public boolean checkHeader(SOAPHeaderBlock header) {
                return true;
            }
        }

        return new HeaderIterator(new DefaultChecker());
    }

    /**
     * Returns an <code>Iterator</code> over all the <code>SOAPHeaderBlock</code> objects in this
     * <code>SOAPHeader </code> object and detaches them from this <code>SOAPHeader</code> object.
     *
     * @return an <code>Iterator</code> object over all the <code>SOAPHeaderBlock</code> objects
     *         contained by this <code>SOAPHeader</code>
     */
    public Iterator extractAllHeaderBlocks() {
        throw new UnsupportedOperationException(); // TODO implement this
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
}
