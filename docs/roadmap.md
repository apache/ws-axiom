<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements. See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership. The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License. You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied. See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->

Roadmap
=======

## Axiom 1.3

### Introduction

This page summarizes the planned changes for the next major release, i.e. Axiom 1.3.
Note that it is not intended as a wish list for new features, but identifies a set of
changes that break backward compatibility and therefore need to
be postponed to the 1.3 release.

The overall goals for the 1.3 are:
  
*   Upgrade the API to use Java 5 features, in particular generics.
   
*   Eliminate deprecated APIs and utility classes.
   
*   Eliminate remaining API inconsistencies.
   
*   Make the API more compact by clarifying the separation between the public API
    and implementation classes and moving implementation classes out of `axiom-api`.

### API inconsistencies to be eliminated

#### Exception hierarchy

The way exceptions are used in Axiom 1.2.x is not very consistent. In addition it doesn't allow application
code to distinguish between different types of error cases. This should be improved in
Axiom 1.3 to meet the following requirements:
  
*   Although the two implementations of the Axiom API (LLOM and DOOM) use StAX as XML parser and serializer, this
    is not a strict requirement. The only strict requirements are that the implementation is able to
    construct a tree from a `XMLStreamReader` supplied by application code, is able to serialize
    a tree to an `XMLStreamWriter` and is able to construct an `XMLStreamReader` from a tree.
    Therefore methods defined by the Axiom API should only declare `XMLStreamException` if they interact
    directly with a StAX object supplied by application code.

*   Axiom should have well-defined (and distinct) exceptions for at least the following two error cases:

    *   An I/O error occurs during a deferred parsing operation. In that case, the unchecked exception should
        wrap the original `IOException` so that it can be extracted by application code.

    *   A parser error occurs during a deferred parsing operation.

### Removal of unnecessary or irrelevant APIs

This section identifies APIs that have become unnecessary or irrelevant. Note that APIs that
have already been deprecated in 1.2.x (and will be removed in 1.3 anyway) are not listed here.

#### `org.apache.axiom.om.impl.builder.XOPBuilder`

The `XOPBuilder` interface is implemented by `XOPAwareStAXOMBuilder` and `MTOMStAXSOAPModelBuilder`.
With the changes in r1164997 and r1207662, it is no longer used internally by Axiom.
The interface declares methods that give access to the `Attachments` object that was
used to create the builder. That is undesirable for two reasons:
  
*   For XOP/MTOM encoded messages, optimized binary data should always be accessed
    through the `OMText` API and not by accessing MIME parts directly through
    the `Attachments` API.

*   The existence of the `XOPBuilder` API (and in particular the `getAttachments`
    method defined by that interface) implies that an XOP/MTOM builder is necessarily
    created from an `Attachments` object. However, it may be desirable to support other
    ways to supply the MIME parts to the builder (e.g. using an implementation of the
    `MimePartProvider` API).

Therefore the `XOPBuilder` API will be removed in Axiom 1.3.

### Classes to be moved from `axiom-api` to `om-aspects`

Up to version 1.2.12, the core Axiom code was organized in three modules,
namely `axiom-api`, `axiom-impl` and `axiom-dom`, where `axiom-api`
contains both the public API as well as implementation classes shared by LLOM and DOOM.
Unfortunately the distinction between the public API and these shared implementation
classes has become somewhat blurred over time. In Axiom 1.2.13 a new module
called `axiom-common-impl` was introduced with the specific goal of separating the
shared implementation classes from the public API; with the introduction of AspectJ
in Axiom 1.2.15 this module is now called `om-aspects`.

However, in Axiom 1.2.x it is generally not possible to move classes from
`axiom-api` to `om-aspects` without the risk of breaking existing code.
A new major release gives us the opportunity to move the existing shared classes to
`om-aspects` as well, so that in Axiom 1.3, `axiom-api` will only
contain Axiom's public API. This is one of the important goals for Axiom 1.3
because it has multiple benefits:
  
*   The more compact the public API is, the easier it is for users to understand the
    API and to locate the features they are looking for.

*   By definition, anything that is not part of the public API can be modified
    without the risk of breaking application code. Clarifying the distinction between
    the public API and internal implementation classes therefore gives the project
    more flexibility to implement changes.

*   Having a well defined abstract API allows to create alternative implementations
    of that API.
  
This section identifies the classes and internal APIs that will be removed
from `axiom-api`.

#### Builder implementations

In Axiom 1.2.13, the `OMXMLBuilderFactory` API allows to create any type of
object model builder (plain XML, SOAP, XOP and MTOM). The API also defines two
interfaces representing a builder: `OMXMLParserWrapper` and `SOAPModelBuilder`.
This means that application code should no longer reference the builder implementation
classes directly, but only `OMXMLBuilderFactory`, `OMXMLParserWrapper` and
`SOAPModelBuilder`. In Axiom 1.3 the implementation classes can therefore be moved
to `om-aspects`. They are:
  
*   `org.apache.axiom.om.impl.builder.StAXBuilder`

*   `org.apache.axiom.om.impl.builder.StAXOMBuilder`

*   `org.apache.axiom.om.impl.builder.XOPAwareStAXOMBuilder`

*   `org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder`

*   `org.apache.axiom.soap.impl.builder.MTOMStAXSOAPModelBuilder`
  
TODO: there is one API that still references `StAXBuilder`, namely `BuilderAwareReader`;
it needs to be changed (or removed)

Together with these classes, the following interfaces and helper classes should also
be moved to `om-aspects`:

*   TODO

### Public APIs that need to be moved to another package

Some interfaces are part of the public API although they are placed in an
`impl` package. These interfaces need to be moved to another package to make it
clear that they are not internal or implementation specific interfaces:

*   `org.apache.axiom.om.impl.builder.CustomBuilder`

### APIs that need to be overhauled

#### `MTOMXMLStreamReader`

This should become an interface:
  
*   This would enable the implementation of proxies and maybe allow us to get rid of
    `XMLStreamWriterFilter`.

*   This would allow us to move the implementation code out of `axiom-api`.

#### Attachment lifecycle manager

The `LifecycleManager` API has a couple of issues that can't be fixed without breaking
backward compatibility:

*   There is some overlap with the `AttachmentCacheMonitor` class. There should be a single API to
    manage the lifecycle of buffered attachments.

*   `LifecycleManager` is an abstract API (interface), but refers to `FileAccessor` which
    is placed in an `impl` package.

#### `SOAPVersion`

`SOAPVersion` should be changed from an interface to an abstract class so that one can
define static methods to get the SOAP version by envelope namespace or media type. Alternatively,
upgrade to Java 8 (which allows static methods in interfaces).

`SOAP11Version` and `SOAP12Version` should not be public, and the deprecated `getSingleton`
methods should be removed.
  
#### `StAXParserConfiguration`

The `StAXParserConfiguration` API relies on the assumption that the XML parser used by Axiom is
an implementation of StAX. However, as noted above, this is not a strict requirement; an Axiom
implementation could equally well use another API or provide its own XML parser.
Therefore `StAXParserConfiguration` should be replaced by something more generic.

#### `OMMetaFactory`

The argument order is not consistent across methods. See e.g. the two `createOMBuilder` methods that
take an `InputSource` argument.

#### `OMElement`

The argument order of the `addAttribute(String, String, OMNamespace)` method is inconsistent with that
of the `createOMAttribute` method in `OMFactory`.

#### `OMOutputFormat`

This class should be made immutable (which would require introducing a builder class) so that instances
are safe to reuse.

#### `SOAPHeaderBlock`

`SOAPHeaderBlock` has a feature that allows to use properties on the `OMDataSource` to specify
the role, relay and mustUnderstand attributes. That shouldn't be necessary; instead `OMSourcedElement`
should allow setting attributes without expanding the `OMDataSource`. The `setProperty` method
can then be removed from `OMDataSource`.

### Miscellaneous

#### Make non coalescing mode the default

By default, Axiom configures the underlying parser in coalescing mode. The reason is purely historical.
Axiom originally used Woodstox 3.x and that version implemented one aspect of the StAX
specification incorrectly, namely [it configured the parser by default
in coalescing mode](http://jira.codehaus.org/browse/WSTX-140), while the specification says otherwise. The problem is that (poorly
written) code that uses Axiom with a parser in coalescing mode doesn't
necessarily work with non coalescing mode. Therefore the choice was
made to make coalescing mode the default in order to ensure
compatibility when using a StAX implementation other than Woodstox 3.x.

A new major release would be the right moment to change this and make non coalescing mode the default.
This enables a couple of optimizations (e.g. when reading and decoding base64 from a text node) and
ensures that an XML document can be streamed with constant memory, even if it contains large text nodes.

#### Don't allow `addChild` to reorder children

The `SOAPEnvelope` implementations in LLOM and DOOM override the `addChild` method to
reorder the nodes if an attempt is made to add a `SOAPHeader` after the `SOAPBody`.
This introduces unnecessary complexity in the implementation and is questionable from an OO
design perspective because it breaks the general contract of the `addChild` method which is
to add the node as the last child.

The `addChild` implementation for `SOAPEnvelope` should not do this. Instead it should
just throw an exception if a `SOAPHeader` is added at the wrong position.

## Axiom 3.0

### Methods declared by the wrong interface in the node type hierarchy

Some methods are declared at the wrong level in the node type hierarchy so that they may
be called on nodes for which they are not meaningful:

*   `OMContainer` declares several methods that return child elements by name: `getChildrenWithLocalName`,
    `getChildrenWithName`, `getChildrenWithNamespaceURI` and `getFirstChildWithName`.
    Since the document element is unique, these methods are not meaningful for `OMDocument`
    and they should be declared by `OMElement` instead.