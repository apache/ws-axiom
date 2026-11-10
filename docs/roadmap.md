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
  
*   As noted in [ADR 0003](adr/0003-no-stax-assumption-in-api.md), the Axiom API should not be
    designed around the assumption that StAX is used as the XML parser. Therefore methods defined
    by the Axiom API should only declare `XMLStreamException` if they interact directly with a
    StAX object supplied by application code. The following non-deprecated methods still violate
    this principle:

    *   `OMElement.toStringWithConsume()`
    *   `AXIOMUtil.stringToOM(String)`
    *   `AXIOMUtil.stringToOM(OMFactory, String)`

*   Axiom should have well-defined (and distinct) exceptions for at least the following two error cases:

    *   An I/O error occurs during a deferred parsing operation. In that case, the unchecked exception should
        wrap the original `IOException` so that it can be extracted by application code.

    *   A parser error occurs during a deferred parsing operation.

### Miscellaneous

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

### APIs that need to be overhauled

#### `MTOMXMLStreamWriter`

This is currently an abstract class in the `org.apache.axiom.om.impl` package. Two changes are needed:

*   It should become an interface. This would enable the implementation of proxies.

*   It should be moved out of the `impl` package, since it is a public API.

#### `SOAPVersion`

`SOAPVersion` should be changed from an interface to an abstract class so that one can
define static methods to get the SOAP version by envelope namespace or media type. Alternatively,
upgrade to Java 8 (which allows static methods in interfaces).

`SOAP11Version` and `SOAP12Version` should not be public, and the deprecated `getSingleton`
methods should be removed.
  
#### `StAXParserConfiguration`

The `StAXParserConfiguration` API relies on the assumption that the XML parser used by Axiom is
an implementation of StAX. However, as noted in [ADR 0003](adr/0003-no-stax-assumption-in-api.md),
this is not a strict requirement. Therefore `StAXParserConfiguration` should be replaced by
something more generic.

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

#### `OMDataSource`

The `OMDataSource` API has accumulated several problems over time and should be simplified:

*   The deprecated methods should be removed. These are `serialize(OutputStream, OMOutputFormat)`
    and `serialize(Writer, OMOutputFormat)` in `OMDataSource`, and `getXMLInputStream(String)` and
    `getXMLBytes(String)` in `OMDataSourceExt`.

*   The property mechanism (`setProperty`, `getProperty` and `hasProperty`) should be removed. Its
    only real use case is `SOAPHeaderBlock` (see above), which should instead rely on
    `OMSourcedElement` being able to set attributes without expanding the data source.

*   `OMDataSourceExt` should be eliminated. Most code checks whether a data source is an
    `OMDataSourceExt` and falls back to a default when it isn't, so the split between the two
    interfaces adds complexity without real benefit. The remaining methods (`getObject`, `copy`
    and `close`) should move to `OMDataSource` itself or to the new subinterfaces described below.

*   Whether a data source is pull-based or push-based is currently decided by checking whether it
    extends `AbstractPullOMDataSource` or `AbstractPushOMDataSource`. There is even a hard-coded
    check for an Axis2 class name in `OMDataSourceUtil`. Data sources that don't extend either
    class have to implement both `getReader()` and `serialize(XMLStreamWriter)`, even when one of
    them can only be emulated inefficiently.
    This should be replaced by two subinterfaces that mirror the existing abstract base classes:

    *   `PullOMDataSource`, which declares `getReader()` and `isDestructiveRead()`;
    *   `PushOMDataSource`, which declares `serialize(XMLStreamWriter)` and `isDestructiveWrite()`.

    `OMDataSource` would then only declare the methods common to both kinds of data source. A data
    source can be purely pull (implementing only `PullOMDataSource`), purely push (implementing only
    `PushOMDataSource`), or both. For a data source that implements both, `OMSourcedElement` would
    use the pull interface for expansion, which allows deferred building. For serialization it would
    use the push interface, which avoids the overhead of copying events from an `XMLStreamReader`.

    `AbstractPullOMDataSource` and `AbstractPushOMDataSource` would be removed. Their only purpose
    is to provide emulations of the other mode, which are no longer needed, and to mark a data
    source as pull or push, which the new interfaces now do. `AbstractOMDataSource` would also be
    removed: once the deprecated methods and the property mechanism are gone, the only defaults
    left (for `getObject`, `copy` and `close`) can be default methods on `OMDataSource`.
    `QNameAwareOMDataSource` can stay as a mixin interface that extends `OMDataSource`.
