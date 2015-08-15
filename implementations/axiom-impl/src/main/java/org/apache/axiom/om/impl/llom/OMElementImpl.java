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

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomElement;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;

import java.util.Iterator;

/** Class OMElementImpl */
public class OMElementImpl extends OMNodeImpl
        implements AxiomElement, OMConstants {

    private static final Log log = LogFactory.getLog(OMElementImpl.class);
    
    private int lineNumber;

    public OMElementImpl(OMContainer parent, String localName, OMNamespace ns, OMXMLParserWrapper builder,
                    OMFactory factory, boolean generateNSDecl) {
        super(factory);
        if (localName == null || localName.trim().length() == 0) {
            throw new OMException("localname can not be null or empty");
        }
        internalSetLocalName(localName);
        coreSetBuilder(builder);
        if (parent != null) {
            ((AxiomContainer)parent).addChild(this, builder != null);
        }
        internalSetNamespace(generateNSDecl ? handleNamespace(this, ns, false, true) : ns);
    }

    /**
     * It is assumed that the QName passed contains, at least, the localName for this element.
     *
     * @param qname - this should be valid qname according to javax.xml.namespace.QName
     * @throws OMException
     */
    public OMElementImpl(QName qname, OMContainer parent, OMFactory factory)
            throws OMException {
        super(factory);
        if (parent != null) {
            parent.addChild(this);
        }
        internalSetLocalName(qname.getLocalPart());
        internalSetNamespace(handleNamespace(qname));
    }
    
    /**
     * Constructor reserved for use by {@link OMSourcedElementImpl}.
     * 
     * @param factory
     */
    OMElementImpl(OMFactory factory) {
        super(factory);
    }

    /** Method handleNamespace. */
    final OMNamespace handleNamespace(QName qname) {
        forceExpand();
        OMNamespace ns = null;

        // first try to find a namespace from the scope
        String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI.length() > 0) {
            String prefix = qname.getPrefix();
            ns = findNamespace(namespaceURI, prefix);

            /**
             * What is left now is
             *  1. nsURI = null & parent != null, but ns = null
             *  2. nsURI != null, (parent doesn't have an ns with given URI), but ns = null
             */
            if (ns == null) {
                if ("".equals(prefix)) {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
                ns = declareNamespace(namespaceURI, prefix);
            }
        } else if (qname.getPrefix().length() > 0) {
            throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
        }
        return ns;
    }

    public void checkChild(OMNode child) {
    }

    public final void build() throws OMException {
        /**
         * builder is null. Meaning this is a programatical created element but it has children which are not completed
         * Build them all.
         */
        if (getBuilder() == null && getState() == INCOMPLETE) {
            for (Iterator childrenIterator = this.getChildren(); childrenIterator.hasNext();) {
                OMNode omNode = (OMNode) childrenIterator.next();
                omNode.build();
            }
        } else {
            defaultBuild();
        }

    }

    public void setComplete(boolean complete) {
        coreSetState(complete ? COMPLETE : INCOMPLETE);
        OMContainer parent = getParent();
        if (parent != null) {
            if (!complete) {
                ((AxiomContainer)parent).setComplete(false);
            } else if (parent instanceof OMElementImpl) {
                ((OMElementImpl) parent).notifyChildComplete();
            } else if (parent instanceof OMDocumentImpl) {
                ((OMDocumentImpl) parent).notifyChildComplete();
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

    public final void setNamespace(OMNamespace namespace) {
        setNamespace(namespace, true);
    }

    public final OMElement cloneOMElement() {
        
        if (log.isDebugEnabled()) {
            log.debug("cloneOMElement start");
            log.debug("  element string =" + getLocalName());
            log.debug(" isComplete = " + isComplete());
            log.debug("  builder = " + getBuilder());
        }
        return (OMElement)clone(new OMCloneOptions());
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        return defaultClone(options, targetParent);
    }
    
    final OMNode defaultClone(OMCloneOptions options, OMContainer targetParent) {
        OMElement targetElement;
        if (options.isPreserveModel()) {
            targetElement = createClone(options, targetParent);
        } else {
            targetElement = getOMFactory().createOMElement(getLocalName(), getNamespace(), targetParent);
        }
        for (Iterator it = getAllDeclaredNamespaces(); it.hasNext(); ) {
            OMNamespace ns = (OMNamespace)it.next();
            targetElement.declareNamespace(ns);
        }
        for (Iterator it = getAllAttributes(); it.hasNext(); ) {
            OMAttribute attr = (OMAttribute)it.next();
            targetElement.addAttribute(attr);
        }
        for (Iterator it = getChildren(); it.hasNext(); ) {
            ((OMNodeImpl)it.next()).clone(options, targetElement);
        }
        return targetElement;
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return getOMFactory().createOMElement(getLocalName(), getNamespace(), targetParent);
    }
    
    public final void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public final int getLineNumber() {
        return lineNumber;
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.om.OMNode#buildAll()
      */
    public void buildWithAttachments() {
        if (getState() == INCOMPLETE) {
            this.build();
        }
        Iterator iterator = getChildren();
        while (iterator.hasNext()) {
            OMNode node = (OMNode) iterator.next();
            node.buildWithAttachments();
        }
    }

    /** This method will be called when one of the children becomes complete. */
    final void notifyChildComplete() {
        if (getState() == INCOMPLETE && getBuilder() == null) {
            Iterator iterator = getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode) iterator.next();
                if (!node.isComplete()) {
                    return;
                }
            }
            this.setComplete(true);
        }
    }
}

