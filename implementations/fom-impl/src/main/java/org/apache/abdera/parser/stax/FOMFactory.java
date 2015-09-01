/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.parser.stax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.ExtensionFactoryMap;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.Version;
import org.apache.axiom.core.CoreCDATASection;
import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreComment;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreDocumentTypeDeclaration;
import org.apache.axiom.core.CoreEntityReference;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreProcessingInstruction;
import org.apache.axiom.fom.AbderaFactory;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.AxiomAttribute;
import org.apache.axiom.om.impl.common.AxiomCDATASection;
import org.apache.axiom.om.impl.common.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.common.AxiomComment;
import org.apache.axiom.om.impl.common.AxiomDocType;
import org.apache.axiom.om.impl.common.AxiomDocument;
import org.apache.axiom.om.impl.common.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomEntityReference;
import org.apache.axiom.om.impl.common.AxiomNamespaceDeclaration;
import org.apache.axiom.om.impl.common.AxiomProcessingInstruction;
import org.apache.axiom.om.impl.common.factory.AxiomNodeFactory;

@SuppressWarnings( {"unchecked", "deprecation"})
public class FOMFactory implements AbderaFactory, AxiomNodeFactory, Constants, ExtensionFactory {
    private static final Map<QName,Class<? extends FOMElement>> elementTypeMap;
    
    static {
        elementTypeMap = new HashMap<QName,Class<? extends FOMElement>>();
        elementTypeMap.put(FEED, FOMFeed.class);
        elementTypeMap.put(SERVICE, FOMService.class);
        elementTypeMap.put(PRE_RFC_SERVICE, FOMService.class);
        elementTypeMap.put(ENTRY, FOMEntry.class);
        elementTypeMap.put(AUTHOR, FOMPerson.class);
        elementTypeMap.put(CATEGORY, FOMCategory.class);
        elementTypeMap.put(CONTENT, FOMContent.class);
        elementTypeMap.put(CONTRIBUTOR, FOMPerson.class);
        elementTypeMap.put(GENERATOR, FOMGenerator.class);
        elementTypeMap.put(ICON, FOMIRI.class);
        elementTypeMap.put(ID, FOMIRI.class);
        elementTypeMap.put(LOGO, FOMIRI.class);
        elementTypeMap.put(LINK, FOMLink.class);
        elementTypeMap.put(PUBLISHED, FOMDateTime.class);
        elementTypeMap.put(RIGHTS, FOMText.class);
        elementTypeMap.put(SOURCE, FOMSource.class);
        elementTypeMap.put(SUBTITLE, FOMText.class);
        elementTypeMap.put(SUMMARY, FOMText.class);
        elementTypeMap.put(TITLE, FOMText.class);
        elementTypeMap.put(UPDATED, FOMDateTime.class);
        elementTypeMap.put(WORKSPACE, FOMWorkspace.class);
        elementTypeMap.put(PRE_RFC_WORKSPACE, FOMWorkspace.class);
        elementTypeMap.put(COLLECTION, FOMCollection.class);
        elementTypeMap.put(PRE_RFC_COLLECTION, FOMCollection.class);
        elementTypeMap.put(NAME, FOMElement.class);
        elementTypeMap.put(EMAIL, FOMElement.class);
        elementTypeMap.put(URI, FOMIRI.class);
        elementTypeMap.put(CONTROL, FOMControl.class);
        elementTypeMap.put(DIV, FOMDiv.class);
        elementTypeMap.put(CATEGORIES, FOMCategories.class);
        elementTypeMap.put(PRE_RFC_CATEGORIES, FOMCategories.class);
        elementTypeMap.put(EDITED, FOMDateTime.class);
        elementTypeMap.put(PRE_RFC_EDITED, FOMDateTime.class);
    }

    private final ExtensionFactoryMap factoriesMap;
    private final Abdera abdera;

    public static void registerAsDefault() {
        System.setProperty(OMAbstractFactory.META_FACTORY_NAME_PROPERTY, FOMFactory.class.getName());
    }

    public FOMFactory() {
        this(new Abdera());
    }

    public FOMFactory(Abdera abdera) {
        List<ExtensionFactory> f = abdera.getConfiguration().getExtensionFactories();
        factoriesMap =
            new ExtensionFactoryMap((f != null) ? new ArrayList<ExtensionFactory>(f)
                : new ArrayList<ExtensionFactory>());
        this.abdera = abdera;
    }

    public OMMetaFactory getMetaFactory() {
        return FOMMetaFactory.INSTANCE;
    }

    public Parser newParser() {
        return new FOMParser(abdera);
    }

    public <T extends Element> Document<T> newDocument() {
        return new FOMDocument(this);
    }

    public Service newService(Base parent) {
        return createElement(FOMService.class, SERVICE, (OMContainer)parent);
    }

    public Workspace newWorkspace() {
        return newWorkspace(null);
    }

    public Workspace newWorkspace(Element parent) {
        return createElement(FOMWorkspace.class, WORKSPACE, (OMContainer)parent);
    }

    public Collection newCollection() {
        return newCollection(null);
    }

    public Collection newCollection(Element parent) {
        return createElement(FOMCollection.class, COLLECTION, (OMContainer)parent);
    }

    public Collection newMultipartCollection(Element parent) {
        return createElement(FOMMultipartCollection.class, COLLECTION, (OMContainer)parent);
    }

    public Feed newFeed() {
        Document<Feed> doc = newDocument();
        return newFeed(doc);
    }

    public Entry newEntry() {
        Document<Entry> doc = newDocument();
        return newEntry(doc);
    }

    public Service newService() {
        Document<Service> doc = newDocument();
        return newService(doc);
    }

    public Feed newFeed(Base parent) {
        return createElement(FOMFeed.class, FEED, (OMContainer)parent);
    }

    public Entry newEntry(Base parent) {
        return createElement(FOMEntry.class, ENTRY, (OMContainer)parent);
    }

    public Category newCategory() {
        return newCategory(null);
    }

    public Category newCategory(Element parent) {
        return createElement(FOMCategory.class, CATEGORY, (OMContainer)parent);
    }

    public Content newContent() {
        return newContent(Content.Type.TEXT);
    }

    public Content newContent(Type type) {
        if (type == null)
            type = Content.Type.TEXT;
        return newContent(type, null);
    }

    public Content newContent(Type type, Element parent) {
        if (type == null)
            type = Content.Type.TEXT;
        Content content = createElement(FOMContent.class, CONTENT, (OMContainer)parent);
        content.setContentType(type);
        if (type.equals(Content.Type.XML))
            content.setMimeType(XML_MEDIA_TYPE);
        return content;
    }

    public Content newContent(MimeType mediaType) {
        return newContent(mediaType, null);
    }

    public Content newContent(MimeType mediaType, Element parent) {
        Content.Type type = (MimeTypeHelper.isXml(mediaType.toString())) ? Content.Type.XML : Content.Type.MEDIA;
        Content content = newContent(type, parent);
        content.setMimeType(mediaType.toString());
        return content;
    }

    public DateTime newDateTime(QName qname, Element parent) {
        return createElement(FOMDateTime.class, qname, (OMContainer)parent);
    }

    public Generator newDefaultGenerator() {
        return newDefaultGenerator(null);
    }

    public Generator newDefaultGenerator(Element parent) {
        Generator generator = newGenerator(parent);
        generator.setVersion(Version.VERSION);
        generator.setText(Version.APP_NAME);
        generator.setUri(Version.URI);
        return generator;
    }

    public Generator newGenerator() {
        return newGenerator(null);
    }

    public Generator newGenerator(Element parent) {
        return createElement(FOMGenerator.class, GENERATOR, (OMContainer)parent);
    }

    public IRIElement newID() {
        return newID(null);
    }

    public IRIElement newID(Element parent) {
        return newIRIElement(ID, parent);
    }

    public IRIElement newIRIElement(QName qname, Element parent) {
        return createElement(FOMIRI.class, qname, (OMContainer)parent);
    }

    public Link newLink() {
        return newLink(null);
    }

    public Link newLink(Element parent) {
        return createElement(FOMLink.class, LINK, (OMContainer)parent);
    }

    public Person newPerson(QName qname, Element parent) {
        return createElement(FOMPerson.class, qname, (OMContainer)parent);
    }

    public Source newSource() {
        return newSource(null);
    }

    public Source newSource(Element parent) {
        return createElement(FOMSource.class, SOURCE, (OMContainer)parent);
    }

    public Text newText(QName qname, Text.Type type) {
        return newText(qname, type, null);
    }

    public Text newText(QName qname, Text.Type type, Element parent) {
        if (type == null)
            type = Text.Type.TEXT;
        Text text = createElement(FOMText.class, qname, (OMContainer)parent);
        text.setTextType(type);
        return text;
    }

    public <T extends Element> T newElement(QName qname) {
        return (T)newElement(qname, null);
    }

    public <T extends Element> T newElement(QName qname, Base parent) {
        return (T)newExtensionElement(qname, parent);
    }

    public <T extends Element> T newExtensionElement(QName qname) {
        return (T)newExtensionElement(qname, null);
    }

    public <T extends Element> T newExtensionElement(QName qname, Base parent) {
        String ns = qname.getNamespaceURI();
        Element el = createElement(qname, (OMContainer)parent);
        return (T)((ATOM_NS.equals(ns) || APP_NS.equals(ns)) ? el : factoriesMap.getElementWrapper(el));
    }

    public Control newControl() {
        return newControl(null);
    }

    public Control newControl(Element parent) {
        return createElement(FOMControl.class, CONTROL, (OMContainer)parent);
    }

    public DateTime newPublished() {
        return newPublished(null);
    }

    public DateTime newPublished(Element parent) {
        return newDateTime(Constants.PUBLISHED, parent);
    }

    public DateTime newUpdated() {
        return newUpdated(null);
    }

    public DateTime newUpdated(Element parent) {
        return newDateTime(Constants.UPDATED, parent);
    }

    public DateTime newEdited() {
        return newEdited(null);
    }

    public DateTime newEdited(Element parent) {
        return newDateTime(Constants.EDITED, parent);
    }

    public IRIElement newIcon() {
        return newIcon(null);
    }

    public IRIElement newIcon(Element parent) {
        return newIRIElement(Constants.ICON, parent);
    }

    public IRIElement newLogo() {
        return newLogo(null);
    }

    public IRIElement newLogo(Element parent) {
        return newIRIElement(Constants.LOGO, parent);
    }

    public IRIElement newUri() {
        return newUri(null);
    }

    public IRIElement newUri(Element parent) {
        return newIRIElement(Constants.URI, parent);
    }

    public Person newAuthor() {
        return newAuthor(null);
    }

    public Person newAuthor(Element parent) {
        return newPerson(Constants.AUTHOR, parent);
    }

    public Person newContributor() {
        return newContributor(null);
    }

    public Person newContributor(Element parent) {
        return newPerson(Constants.CONTRIBUTOR, parent);
    }

    public Text newTitle() {
        return newTitle(Text.Type.TEXT);
    }

    public Text newTitle(Element parent) {
        return newTitle(Text.Type.TEXT, parent);
    }

    public Text newTitle(Text.Type type) {
        return newTitle(type, null);
    }

    public Text newTitle(Text.Type type, Element parent) {
        return newText(Constants.TITLE, type, parent);
    }

    public Text newSubtitle() {
        return newSubtitle(Text.Type.TEXT);
    }

    public Text newSubtitle(Element parent) {
        return newSubtitle(Text.Type.TEXT, parent);
    }

    public Text newSubtitle(Text.Type type) {
        return newSubtitle(type, null);
    }

    public Text newSubtitle(Text.Type type, Element parent) {
        return newText(Constants.SUBTITLE, type, parent);
    }

    public Text newSummary() {
        return newSummary(Text.Type.TEXT);
    }

    public Text newSummary(Element parent) {
        return newSummary(Text.Type.TEXT, parent);
    }

    public Text newSummary(Text.Type type) {
        return newSummary(type, null);
    }

    public Text newSummary(Text.Type type, Element parent) {
        return newText(Constants.SUMMARY, type, parent);
    }

    public Text newRights() {
        return newRights(Text.Type.TEXT);
    }

    public Text newRights(Element parent) {
        return newRights(Text.Type.TEXT, parent);
    }

    public Text newRights(Text.Type type) {
        return newRights(type, null);
    }

    public Text newRights(Text.Type type, Element parent) {
        return newText(Constants.RIGHTS, type, parent);
    }

    public Element newName() {
        return newName(null);
    }

    public Element newName(Element parent) {
        return newElement(Constants.NAME, parent);
    }

    public Element newEmail() {
        return newEmail(null);
    }

    public Element newEmail(Element parent) {
        return newElement(Constants.EMAIL, parent);
    }

    public Div newDiv() {
        return newDiv(null);
    }

    public Div newDiv(Base parent) {
        return createElement(FOMDiv.class, DIV, (OMContainer)parent);
    }

    public <T extends CoreNode> T createNode(Class<T> type) {
        CoreNode node;
        if (type == CoreCDATASection.class || type == AxiomCDATASection.class || type == FOMCDATASection.class) {
            node = new FOMCDATASection(this);
        } else if (type == CoreCharacterDataNode.class || type == AxiomCharacterDataNode.class || type == FOMCharacterDataNode.class) {
            node = new FOMCharacterDataNode(this);
        } else if (type == CoreComment.class || type == AxiomComment.class || type == FOMComment.class) {
            node = new FOMComment(this);
        } else if (type == CoreDocument.class || type == AxiomDocument.class || type == FOMDocument.class) {
            node = new FOMDocument(this);
        } else if (type == CoreDocumentTypeDeclaration.class || type == AxiomDocType.class) {
            node = new FOMDocType(this);
        } else if (type == CoreEntityReference.class || type == AxiomEntityReference.class) {
            node = new FOMEntityReference(this);
        } else if (type == CoreNamespaceDeclaration.class || type == AxiomNamespaceDeclaration.class) {
            node = new FOMNamespaceDeclaration(this);
        } else if (type == CoreNSAwareAttribute.class || type == AxiomAttribute.class || type == FOMAttribute.class) {
            node = new FOMAttribute(this);
        } else if (type == CoreNSAwareElement.class || type == AxiomElement.class || type == FOMElement.class) {
            node = new FOMElement(this);
        } else if (type == CoreProcessingInstruction.class || type == AxiomProcessingInstruction.class || type == FOMProcessingInstruction.class) {
            node = new FOMProcessingInstruction(this);
        } else if (type == FOMCategories.class) {
            node = new FOMCategories(this);
        } else if (type == FOMCategory.class) {
            node = new FOMCategory(this);
        } else if (type == FOMCollection.class) {
            node = new FOMCollection(this);
        } else if (type == FOMContent.class) {
            node = new FOMContent(this);
        } else if (type == FOMControl.class) {
            node = new FOMControl(this);
        } else if (type == FOMDateTime.class) {
            node = new FOMDateTime(this);
        } else if (type == FOMDiv.class) {
            node = new FOMDiv(this);
        } else if (type == FOMEntry.class) {
            node = new FOMEntry(this);
        } else if (type == FOMExtensibleElement.class) {
            node = new FOMExtensibleElement(this);
        } else if (type == FOMFeed.class) {
            node = new FOMFeed(this);
        } else if (type == FOMGenerator.class) {
            node = new FOMGenerator(this);
        } else if (type == FOMIRI.class) {
            node = new FOMIRI(this);
        } else if (type == FOMLink.class) {
            node = new FOMLink(this);
        } else if (type == FOMMultipartCollection.class) {
            node = new FOMMultipartCollection(this);
        } else if (type == FOMPerson.class) {
            node = new FOMPerson(this);
        } else if (type == FOMService.class) {
            node = new FOMService(this);
        } else if (type == FOMSource.class) {
            node = new FOMSource(this);
        } else if (type == FOMText.class) {
            node = new FOMText(this);
        } else if (type == FOMWorkspace.class) {
            node = new FOMWorkspace(this);
        } else {
            throw new IllegalArgumentException(type.getName() + " not supported");
        }
        return type.cast(node);
    }

    private <T extends FOMElement> T createElement(Class<T> type, QName qname, OMContainer parent) {
        T element = createNode(type);
        if (parent != null) {
            parent.addChild(element);
        }
        String namespace = qname.getNamespaceURI();
        String prefix = qname.getPrefix();
        OMNamespace ns = element.findNamespace(namespace, prefix);
        if (ns == null && namespace.length() > 0) {
            ns = createOMNamespace(namespace, prefix);
        }
        element.initName(qname.getLocalPart(), ns, true);
        if (element instanceof FOMService) {
            element.declareDefaultNamespace(APP_NS);
            element.declareNamespace(ATOM_NS, "atom");
        } else if (element instanceof FOMCategories) {
            element.declareNamespace(ATOM_NS, "atom");
        }
        return element;
    }
    
    protected FOMElement createElement(QName qname, OMContainer parent) {
        Class<? extends FOMElement> elementType = elementTypeMap.get(qname);
        if (elementType == null) {
            if (parent instanceof ExtensibleElement || parent instanceof Document) {
                elementType = FOMExtensibleElement.class;
            } else {
                elementType = FOMExtensibleElement.class;
                parent = null;
            }
        }
        return createElement(elementType, qname, parent);
    }

    protected OMElement createElementFromBuilder(QName qname, OMContainer parent, FOMBuilder builder) {
        Class<? extends FOMElement> elementType = elementTypeMap.get(qname);
        if (elementType == null) {
            if (parent instanceof ExtensibleElement || parent instanceof Document) {
                elementType = FOMExtensibleElement.class;
            } else {
                elementType = FOMElement.class;
            }
        }
        OMElement element = createAxiomElement(elementType, parent, qname.getLocalPart(), null, builder, false);
        if (element instanceof FOMContent) {
            Content.Type type = builder.getContentType();
            ((FOMContent)element).setContentType(type == null ? Content.Type.TEXT : type);
        } else if (element instanceof FOMText) {
            Text.Type type = builder.getTextType();
            ((FOMText)element).setTextType(type == null ? Text.Type.TEXT : type);
        }
        return element;
    }

    public Factory registerExtension(ExtensionFactory factory) {
        factoriesMap.addFactory(factory);
        return this;
    }

    public Categories newCategories() {
        Document<Categories> doc = newDocument();
        return newCategories(doc);
    }

    public Categories newCategories(Base parent) {
        return createElement(FOMCategories.class, CATEGORIES, (OMContainer)parent);
    }

    public String newUuidUri() {
        return FOMHelper.generateUuid();
    }

    // public void setElementWrapper(Element internal, Element wrapper) {
    // factoriesMap.setElementWrapper(internal, wrapper);
    // }
    //  

    public <T extends Element> T getElementWrapper(Element internal) {
        if (internal == null)
            return null;
        String ns = internal.getQName().getNamespaceURI();
        return (T)((ATOM_NS.equals(ns) || APP_NS.equals(ns) || internal.getQName().equals(DIV)) ? internal
            : factoriesMap.getElementWrapper(internal));
    }

    public String[] getNamespaces() {
        return factoriesMap.getNamespaces();
    }

    public boolean handlesNamespace(String namespace) {
        return factoriesMap.handlesNamespace(namespace);
    }

    public Abdera getAbdera() {
        return abdera;
    }

    public <T extends Base> String getMimeType(T base) {
        String type = factoriesMap.getMimeType(base);
        return type;
    }

    public String[] listExtensionFactories() {
        return factoriesMap.listExtensionFactories();
    }
}
