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
    StAX object supplied by application code.

*   Axiom should have well-defined (and distinct) exceptions for at least the following two error cases:

    *   An I/O error occurs during a deferred parsing operation. In that case, the unchecked exception should
        wrap the original `IOException` so that it can be extracted by application code.

    *   A parser error occurs during a deferred parsing operation.

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
