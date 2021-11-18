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
package org.apache.axiom.om.impl.mixin;

import org.apache.axiom.core.Builder;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.DeferredParsingException;
import org.apache.axiom.core.impl.builder.BuilderImpl;
import org.apache.axiom.core.impl.builder.PlainXMLModel;
import org.apache.axiom.core.stream.FilteredXmlInput;
import org.apache.axiom.core.stream.NamespaceRepairingFilter;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.stax.pull.StAXPullInput;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.QNameAwareOMDataSource;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.DeferredNamespace;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.common.util.OMDataSourceUtil;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.stream.ds.PushOMDataSourceInput;
import org.apache.axiom.om.impl.stream.stax.pull.AxiomXMLStreamReaderHelperFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * <p>Element backed by an arbitrary data source. When necessary, this element will be expanded by
 * creating a parser from the data source.</p>
 * <p/>
 * <p>Whenever methods are added to the base {@link OMElementImpl}
 * class the corresponding methods must be added to this class (there's a unit test to verify that
 * this has been done, just to make sure nothing gets accidentally broken). If the method only
 * requires the element name and/or namespace information, the base class method can be called
 * directly. Otherwise, the element must be expanded into a full OM tree (by calling the {@link
 * #forceExpand()} method) before the base class method is called. This will typically involve a
 * heavy overhead penalty, so should be avoided if possible.</p>
 */
public aspect AxiomSourcedElementSupport {
    
    /** Data source for element data. */
    private OMDataSource AxiomSourcedElement.dataSource;

    /** Namespace for element, needed in order to bypass base class handling. */
    private OMNamespace AxiomSourcedElement.definedNamespace;
    
    /**
     * Flag indicating whether the {@link #definedNamespace} attribute has been set. If this flag is
     * <code>true</code> and {@link #definedNamespace} is <code>null</code> then the element has no
     * namespace. If this flag is set to <code>false</code> (in which case {@link #definedNamespace}
     * is always <code>null</code>) then the namespace is not known and needs to be determined
     * lazily. The flag is used only if {@link #isExpanded} is <code>false</code>.
     */
    private boolean AxiomSourcedElement.definedNamespaceSet;

    /** Flag for parser provided to base element class. */
    private boolean AxiomSourcedElement.isExpanded = true;

    private static final Log log = LogFactory.getLog(AxiomSourcedElementSupport.class);
    
    private static final Log forceExpandLog = LogFactory.getLog(AxiomSourcedElementSupport.class.getName() + ".forceExpand");
    
    private static OMNamespace getOMNamespace(QName qName) {
        return qName.getNamespaceURI().length() == 0 ? null
                : new OMNamespaceImpl(qName.getNamespaceURI(), qName.getPrefix());
    }
    
    public Class<? extends CoreNode> AxiomSourcedElement.coreGetNodeClass() {
        return AxiomSourcedElement.class;
    }
    
    public void AxiomSourcedElement.init(OMDataSource source) {
        dataSource = source;
        isExpanded = false;
    }
    
    /**
     * Constructor.
     *
     * @param localName
     * @param ns
     * @param factory
     * @param source
     */
    public void AxiomSourcedElement.init(String localName, OMNamespace ns, OMDataSource source) {
        if (source == null) {
            throw new IllegalArgumentException("OMDataSource can't be null");
        }
        internalSetLocalName(localName);
        dataSource = source;
        isExpanded = false;
        // Normalize the namespace. Note that this also covers the case where the
        // namespace URI is empty and the prefix is null (in which case we know that
        // the actual prefix must be empty)
        if (ns != null && ns.getNamespaceURI().length() == 0) {
            ns = null;
        }
        if (ns == null || ns.getPrefix() != null) {
            // Believe the prefix and create a normal OMNamespace
            definedNamespace = ns;
        } else {
            // Create a deferred namespace that forces an expand to get the prefix
            String uri = ns.getNamespaceURI();
            definedNamespace = new DeferredNamespace(this, uri);
        }
        definedNamespaceSet = true;
    }

    /**
     * Constructor that takes a QName instead of the local name and the namespace seperately
     *
     * @param qName
     * @param factory
     * @param source
     */
    public void AxiomSourcedElement.init(QName qName, OMDataSource source) {
        if (source == null) {
            throw new IllegalArgumentException("OMDataSource can't be null");
        }
        internalSetLocalName(qName.getLocalPart());
        dataSource = source;
        isExpanded = false;
        definedNamespace = getOMNamespace(qName);
        definedNamespaceSet = true;
    }

    public boolean AxiomSourcedElement.internalIsDefinedNamespaceSet() {
        return definedNamespaceSet;
    }

    public OMNamespace AxiomSourcedElement.internalGetDefinedNamespace() {
        return definedNamespace;
    }

    /**
     * Generate element name for output.
     *
     * @return name
     */
    private String AxiomSourcedElement.getPrintableName() {
        if (isExpanded || (definedNamespaceSet && internalGetLocalName() != null)) {
            String uri = null;
            if (getNamespace() != null) {
                uri = getNamespace().getNamespaceURI();
            }
            if (uri == null || uri.length() == 0) {
                return getLocalName();
            } else {
                return "{" + uri + '}' + getLocalName();
            }
        } else {
            return "<unknown>";
        }
    }

    /**
     * Set parser for OM, if not previously set. Since the builder is what actually constructs the
     * tree on demand, this first creates a builder
     */
    public void AxiomSourcedElement.forceExpand() {
        // The dataSource != null is required because this method may be called indirectly
        // by the constructor before the data source is set. After the constructor has completed,
        // isExpanded is always true if dataSource is null.
        if (!isExpanded && dataSource != null) {

            if (log.isDebugEnabled()) {
                log.debug("forceExpand: expanding element " +
                        getPrintableName());
                if(forceExpandLog.isDebugEnabled()){
                    // When using an OMSourcedElement, it can be particularly difficult to
                    // determine why an expand occurs... a stack trace should help debugging this
                    Exception e = new Exception("Debug Stack Trace");
                    forceExpandLog.debug("forceExpand stack", e);
                }
            }

            Builder builder;
            if (OMDataSourceUtil.isPushDataSource(dataSource)) {
                // Disable namespace repairing because the OMDataSource is required to produce well formed
                // XML with respect to namespaces.
                builder = new BuilderImpl(new PushOMDataSourceInput(this, dataSource), coreGetNodeFactory(), PlainXMLModel.INSTANCE, this);
            } else {
                // Get the XMLStreamReader
                XMLStreamReader readerFromDS;
                try {
                    readerFromDS = dataSource.getReader();  
                } catch (XMLStreamException ex) {
                    throw new OMException("Error obtaining parser from data source for element " + getPrintableName(), ex);
                }
                builder = new BuilderImpl(new FilteredXmlInput(new StAXPullInput(readerFromDS, AxiomXMLStreamReaderHelperFactory.INSTANCE), NamespaceRepairingFilter.DEFAULT), coreGetNodeFactory(), PlainXMLModel.INSTANCE, this);
            }
            isExpanded = true;
            coreSetState(ATTRIBUTES_PENDING);
            try {
                do {
                    builder.next();
                } while (getState() == ATTRIBUTES_PENDING);
            } catch (DeferredParsingException ex) {
                throw AxiomExceptionTranslator.translate(ex);
            }
        }
    }
    
    /**
     * Validates that the actual name of the element obtained from StAX matches the information
     * specified when the sourced element was constructed or retrieved through the
     * {@link QNameAwareOMDataSource} interface. Also updates the local name if necessary. Note that
     * the namespace information is not updated; this is the responsibility of the builder (and is
     * done at the same time as namespace repairing).
     * 
     * @param staxPrefix
     * @param staxLocalName
     * @param staxNamespaceURI
     */
    public void AxiomSourcedElement.validateName(String staxPrefix, String staxLocalName, String staxNamespaceURI) {
        if (internalGetLocalName() == null) {
            // The local name was not known in advance; initialize it from the reader
            internalSetLocalName(staxLocalName);
        } else {
            // Make sure element local name and namespace matches what was expected
            if (!staxLocalName.equals(internalGetLocalName())) {
                throw new OMException("Element name from data source is " +
                        staxLocalName + ", not the expected " + internalGetLocalName());
            }
        }
        if (definedNamespaceSet) {
            if (staxNamespaceURI == null) {
                staxNamespaceURI = "";
            }
            String namespaceURI = definedNamespace == null ? "" : definedNamespace.getNamespaceURI();
            if (!staxNamespaceURI.equals(namespaceURI)) {
                throw new OMException("Element namespace from data source is " +
                        staxNamespaceURI + ", not the expected " + namespaceURI);
            }
            if (!(definedNamespace instanceof DeferredNamespace)) {
                if (staxPrefix == null) {
                    staxPrefix = "";
                }
                String prefix = definedNamespace == null ? "" : definedNamespace.getPrefix();
                if (!staxPrefix.equals(prefix)) {
                    throw new OMException("Element prefix from data source is '" +
                            staxPrefix + "', not the expected '" + prefix + "'");
                }
            }
        }
    }
    
    /**
     * Check if element has been expanded into tree.
     *
     * @return <code>true</code> if expanded, <code>false</code> if not
     */
    public boolean AxiomSourcedElement.isExpanded() {
        return isExpanded;
    }

    public XMLStreamReader AxiomSourcedElement.getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        if (log.isDebugEnabled()) {
            log.debug("getting XMLStreamReader for " + getPrintableName()
                    + " with cache=" + cache);
        }
        if (isExpanded) {
            return defaultGetXMLStreamReader(cache, configuration);
        } else {
            if ((cache && OMDataSourceUtil.isDestructiveRead(dataSource)) || OMDataSourceUtil.isPushDataSource(dataSource)) {
                forceExpand();
                return defaultGetXMLStreamReader(true, configuration);
            } else {
                try {
                    return dataSource.getReader();  
                } catch (XMLStreamException ex) {
                    throw new OMException("Error obtaining parser from data source for element " + getPrintableName(), ex);
                }
            }
        }
    }

    public final void AxiomSourcedElement.updateLocalName() {
        if (dataSource instanceof QNameAwareOMDataSource) {
            internalSetLocalName(((QNameAwareOMDataSource)dataSource).getLocalName());
        }
        if (internalGetLocalName() == null) {
            forceExpand();
        }
    }

    public OMNamespace AxiomSourcedElement.getNamespace() throws OMException {
        if (isExpanded()) {
            return defaultGetNamespace();
        } else if (definedNamespaceSet) {
            return definedNamespace;
        } else {
            if (dataSource instanceof QNameAwareOMDataSource) {
                String namespaceURI = ((QNameAwareOMDataSource)dataSource).getNamespaceURI();
                if (namespaceURI != null) {
                    if (namespaceURI.length() == 0) {
                        // No namespace case. definedNamespace is already null, so we only need
                        // to set definedNamespaceSet to true. Note that we don't need to retrieve
                        // the namespace prefix because a prefix can't be bound to the empty
                        // namespace URI.
                        definedNamespaceSet = true;
                    } else {
                        String prefix = ((QNameAwareOMDataSource)dataSource).getPrefix();
                        if (prefix == null) {
                            // Prefix is unknown
                            definedNamespace = new DeferredNamespace(this, namespaceURI);
                        } else {
                            definedNamespace = new OMNamespaceImpl(namespaceURI, prefix);
                        }
                        definedNamespaceSet = true;
                    }
                }
            }
            if (definedNamespaceSet) {
                return definedNamespace;
            } else {
                // We have no information about the namespace of the element. Need to expand
                // the element to get it.
                forceExpand();
                return defaultGetNamespace();
            }
        }
    }

    public QName AxiomSourcedElement.getQName() {
        if (isExpanded()) {
            return defaultGetQName();
        } else if (getNamespace() != null) {
            // always ignore prefix on name from sourced element
            return new QName(getNamespace().getNamespaceURI(), getLocalName());
        } else {
            return new QName(getLocalName());
        }
    }

    public <T> void AxiomSourcedElement.initSource(ClonePolicy<T> policy, T options, CoreElement other) {
        AxiomSourcedElement o = (AxiomSourcedElement)other;
        // If already expanded or this is not an OMDataSourceExt, then
        // create a copy of the OM Tree
        OMDataSource ds = o.getDataSource();
        if (!(options instanceof OMCloneOptions) || !((OMCloneOptions)options).isCopyOMDataSources() ||
            ds == null || 
            o.isExpanded() || 
            !(ds instanceof OMDataSourceExt)) {
            return;
        }
        
        // If copying is destructive, then copy the OM tree
        OMDataSourceExt sourceDS = (OMDataSourceExt) ds;
        if (sourceDS.isDestructiveRead() ||
            sourceDS.isDestructiveWrite()) {
            return;
        }
        OMDataSourceExt targetDS = ((OMDataSourceExt) ds).copy();
        if (targetDS == null) {
            return;
        }
        // Otherwise create a target OMSE with the copied DataSource
        init(targetDS);
        definedNamespaceSet = o.internalIsDefinedNamespaceSet();
        OMNamespace otherDefinedNamespace = o.internalGetDefinedNamespace();
        if (otherDefinedNamespace instanceof DeferredNamespace) {
            definedNamespace = new DeferredNamespace(this, otherDefinedNamespace.getNamespaceURI());
        } else {
            definedNamespace = otherDefinedNamespace;
        }
    }

    public final XmlInput AxiomSourcedElement.getXmlInput(boolean cache, boolean incremental) throws StreamException {
        if (isExpanded()) {
            return null;
        }
        boolean pull;
        if (OMDataSourceUtil.isPullDataSource(dataSource)) {
            pull = true;
        } else if (OMDataSourceUtil.isPushDataSource(dataSource)) {
            if (incremental) {
                return null;
            }
            pull = false;
        } else {
            pull = incremental;
        }
        if (cache && (pull && OMDataSourceUtil.isDestructiveRead(dataSource) || !pull && OMDataSourceUtil.isDestructiveWrite(dataSource))) {
            return null;
        }
        if (pull) {
            try {
                return new StAXPullInput(dataSource.getReader(), AxiomXMLStreamReaderHelperFactory.INSTANCE);
            } catch (XMLStreamException ex) {
                throw new StreamException(ex);
            }
        } else {
            return new PushOMDataSourceInput(this, dataSource);
        }
    }

    /**
     * Provide access to the data source encapsulated in OMSourcedElement. 
     * This is usesful when we want to access the raw data in the data source.
     *
     * @return the internal datasource
     */
    public OMDataSource AxiomSourcedElement.getDataSource() {
        return dataSource;
    }
    
    /**
     * setOMDataSource
     */
    public OMDataSource AxiomSourcedElement.setDataSource(OMDataSource dataSource) {
        try {
            if (!isExpanded()) {
                OMDataSource oldDS = this.dataSource;
                this.dataSource = dataSource;
                return oldDS;  // Caller is responsible for closing the data source
            } else {
                OMDataSource oldDS = this.dataSource;
                coreSetInputContext(null);
                // TODO: remove attributes?
                coreRemoveChildren(AxiomSemantics.INSTANCE);
                isExpanded = false;
                this.dataSource = dataSource;
                return oldDS;
            }
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    public void AxiomSourcedElement.completed() {
        if (dataSource != null) {
            if (dataSource instanceof OMDataSourceExt) {
                ((OMDataSourceExt)dataSource).close();
            }
            dataSource = null;
        }
    }
    
    public Object AxiomSourcedElement.getObject(Class<? extends OMDataSourceExt> dataSourceClass) {
        if (dataSource == null || isExpanded || !dataSourceClass.isInstance(dataSource)) {
            return null;
        } else {
            return ((OMDataSourceExt)dataSource).getObject();
        }
    }
}
