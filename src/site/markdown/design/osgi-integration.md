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

OSGi integration and separation between API and implementation
==============================================================

## Introduction

This section addresses two related architectural questions:

*   OSGi support was originally introduced in Axiom 1.2.9, but the implementation had
    a couple of flaws. This section discusses the rationale behind the new OSGi
    support introduced in Axiom 1.2.13.

*   Axiom is designed as a set of abstract APIs for which two implementations are
    provided: LLOM and DOOM. It is important to make a clear distinction between what
    is part of the public API and what should be considered implementation classes that
    must not be used by application code directly. This also implies that Axiom must
    provide the necessary APIs to allow application code to access all features without
    the need to access implementation classes directly. This section in particular
    discusses the question how application code can request factories that support DOM
    without the need to refer directly to DOOM.

These two questions are closely related because OSGi allows to enforce the distinction
between public API and implementation classes by carefully selecting the packages
exported by the different bundles: only classes belonging to the public API
should be exported, while implementation classes should be private to the bundles
containing them. This in turn has implications for the packaging of these artifacts.

## Requirements

<a name="req1"></a>

**Requirement 1**: *The Axiom artifacts SHOULD be usable both as normal JAR files and as OSGi bundles.*

The alternative would be to produce two sets of artifacts during the build. This
should be avoided in order to keep the build process as simple as possible.
It should also be noted that the Geronimo Spec artifacts also meet this requirement.

<a name="req2"></a>

**Requirement 2**: *All APIs defined by the `axiom-api` module, and in particular the
`OMAbstractFactory` API MUST continue to work as expected in an OSGi environment, so that code
in downstream projects doesn't need to be rewritten.*

This requirement was already satisfied by the OSGi support introduced in Axiom 1.2.9.
It therefore also ensures that the transition to the new OSGi support in Axiom 1.2.13
is transparent for applications that already use Axiom in an OSGi container.

<a name="req3"></a>

**Requirement 3**: *`OMAbstractFactory` MUST select the same implementation
regardless of the type of container (OSGi or non OSGi). The only exception is
related to the usage of system properties to specify the default `OMMetaFactory`
implementation: in an OSGi environment, selecting an implementation class using
a system property is not meaningful.*

<a name="req4"></a>

**Requirement 4**: *Only classes belonging to the public API should be exported by the OSGi bundles.
Implementation classes should not be exported. In particular,
the bundles for the LLOM and DOOM implementations MUST NOT export any packages.
This is required to keep a clean separation between the public API and implementation
specific classes and to make sure that the implementations can be modified without the
risk of breaking existing code.
An exception MAY be made for factory classes related to foreign APIs, such as the
`DocumentBuilderFactory` implementation for an Axiom implementation
supporting DOM.*

When the Axiom artifacts are used as normal JAR files in a Maven build, this requirement implies that
they should be used in scope `runtime`.

Although this requirement is easy to implement for the Axiom project, it requires
changes to downstreams project to make this actually work:

*   As explained in [AXIS2-4902](https://issues.apache.org/jira/browse/AXIS2-4902),
    there used to be many places in Axis2 that still referred directly to Axiom implementation classes.
    The same was true for Rampart and Sandesha2. This has now been fixed and all three projects
    use `axiom-impl` and `axiom-dom` as dependencies in scope
    `runtime`.

*   Abdera extends the LLOM implementation. Probably, some `maven-shade-plugin`
    magic will be required here to create Abdera OSGi bundles that work properly with
    the Axiom bundles.

*   For Spring Web Services this issue is addressed by
    [SWS-822](https://jira.springsource.org/browse/SWS-822).

<a name="req5"></a>

**Requirement 5**: *It MUST be possible to use a non standard (third party) Axiom implementation as a drop-in replacement
for the standard LLOM and DOOM implementation, i.e. the `axiom-impl`
and `axiom-dom` bundles. It MUST be possible to replace `axiom-impl`
(resp. `axiom-dom`) by any Axiom implementation that supports the full Axiom API
(resp. that supports DOM in addition to the Axiom API), without the need to change any application code.*

This requirement has several important implications:

*   It restricts the allowable exceptions to [Requirement 4](#req4).

*   It implies that there must be an API that allows application code to select an Axiom
    implementation based on its capabilities (e.g. DOM support) without introducing a
    hard dependency on a particular Axiom implementation.

*   In accordance with [Requirement 2](#req2) and [Requirement 3](#req3)
    this requirement not only applies to an OSGi environment, but extends to non OSGi environments as well.

<a name="req6"></a>

**Requirement 6**: *The OSGi integration SHOULD remove the necessity for downstreams projects
to produce their own custom OSGi bundles for Axiom. There SHOULD be one
and only one set of OSGi bundles for Axiom, namely the ones released by the Axiom project.*

Currently there are at least two projects that create their own modified Axiom bundles:

*   Apache Geronimo has a custom Axiom bundle to support the Axis2 integration.

*   ServiceMix also has a custom bundles for Axiom. However, this bundle only seem to exist to
    support their own custom Abdera bundle, which is basically an incorrect repackaging of the
    original Abdera code. See
    [SMX4-877](https://issues.apache.org/jira/browse/SMX4-877) for more details.

Note that this requirement can't be satisfied directly by Axiom. It requires that
the above mentioned projects (Geronimo, Axis2 and Abdera) use Axiom in a way that is
compatible with its design, and in particular with [Requirement 4](#req4).
Nevertheless, Axiom must provide the necessary APIs and features to meet the needs
of these projects.

<a name="req7"></a>

**Requirement 7**: *The Axiom OSGi integration SHOULD NOT rely on any particular OSGi framework such
as Felix SCR (Declarative Services). When deployed in an OSGi environment, Axiom should have the same
runtime dependencies as in a non OSGi environment (i.e. StAX, Activation and JavaMail).*

Axiom 1.2.12 relies on Felix SCR. Although there is no real issue with that, getting rid
of this extra dependency is seen as a nice to have. One of the reasons for using Felix SCR
was to avoid introducing OSGi specific code into Axiom. However, there is no issue with
having such code, provided that [Requirement 8](#req8) is satisfied.

<a name="req8"></a>

**Requirement 8**: *In a non OSGi environment, Axiom MUST NOT have any OSGi related dependencies. That means
that the OSGi integration must be written in such a way that no OSGi specific classes are
ever loaded in a non OSGi environment.*

<a name="req9"></a>

**Requirement 9**: *The OSGi integration MUST follow established best practices. It SHOULD be inspired by
what has been done to add OSGi integration to APIs that have a similar structure as Axiom.*

Axiom is designed around an abstract API and allows for the existence of multiple
independent implementations. A factory (`OMAbstractFactory`) is used to
locate and instantiate the desired implementation. This is similar to APIs such as
JAXP (`DocumentBuilderFactory`, etc.) and JAXB (`JAXBContext`).
These APIs have been successfully "OSGi-fied" e.g. by the Apache Geronimo project.
Instead of reinventing the wheel, we should leverage that work and adapt it to
Axiom's specific requirements.

It should be noted that because of the way the Axiom API is designed and taking into account
[Requirement 2](#req2), it is not possible to make Axiom entirely compatible
with OSGi paradigms (the same is true for JAXB). In an OSGi-only world, each Axiom
implementation would simply expose itself as an OSGi service (of type `OMMetaFactory` e.g.)
and code depending on Axiom would bind to one (or more) of these services depending on its needs.
That is not possible because it would conflict with [Requirement 2](#req2).

**Non-Requirement 1**: *APIs such as JAXP and JAXB have been designed from the start for inclusion into the JRE.
They need to support scenarios where an application bundles its own implementation
(e.g. an application may package a version of Apache Xerces, which would then be
instantiated by the `newInstance` method in
`DocumentBuilderFactory`). That implies that the selected implementation
depends on the thread context class loader. It is assumed that there is no such requirement
for Axiom, which means that in a non OSGi environment, the Axiom implementations are always loaded from the same
class loader as the `axiom-api` JAR.*

This (non-)requirement is actually not directly relevant for the OSGi support, but it
nevertheless has some importance because of [Requirement 3](#req3)
(which implies that the OSGi support needs to be designed in parallel with the implementation
discovery strategy applicable in a non OSGi environment).

## Analysis of the Geronimo JAXB bundles

As noted in [Requirement 9](#req9) the Apache Geronimo has successfully
added OSGi support to the JAXB API which has a structure similar to the Axiom API. This section briefly describes
how this works. The analysis refers to the following Geronimo artifacts: 
`org.apache.geronimo.specs:geronimo-jaxb_2.2_spec:1.0.1` (called the "API bundle" hereafter),
`org.apache.geronimo.bundles:jaxb-impl:2.2.3-1_1` (the "implementation bundle"),
`org.apache.geronimo.specs:geronimo-osgi-locator:1.0` (the "locator bundle") and
`org.apache.geronimo.specs:geronimo-osgi-registry:1.0` (the "registry bundle"):

*   The implementation bundle retains the `META-INF/services/javax.xml.bind.JAXBContext`
    resource from the original artifact (`com.sun.xml.bind:jaxb-impl`).
    In a non OSGi environment, that resource will be used to discover the implementation, following
    the standard JDK 1.3 service discovery algorithm will (as required by the JAXB specification).
    This is the equivalent of our [Requirement 1](#req1).

*   The manifest of the implementation bundle has an attribute `SPI-Provider: true` that indicates
    that it contains provider implementations that are discovered using the JDK 1.3 service discovery.

*   The registry bundle creates a `BundleTracker` that looks for
    the `SPI-Provider` attribute in active bundles. For each bundle
    that has this attribute set to `true`, it will scan the content of
    `META-INF/services` and add the discovered services to a registry
    (Note that the registry bundle supports other ways to declare SPI providers,
    but this is not really relevant for the present discussion).

*   The `ContextFinder` class (the interface of which is defined by
    the JAXB specification and that is used by the `newInstance`
    method in `JAXBContext`) in the API bundle delegates the discovery
    of the SPI implementation to a static method of the `ProviderLocator`
    class defined by the locator bundle (which is not specific to JAXB and is used by other
    API bundles as well). This is true both in an OSGi environment and in a non OSGi environment.

    The build is configured (using a `Private-Package` instruction)
    such that the classes of the locator bundle are actually included into the API bundle, thus
    avoiding an additional dependency.

*   The `ProviderLocator` class and related code provided by the locator bundle is designed
    such that in a non OSGi environment, it will simply use JDK 1.3 service discovery to locate
    the SPI implementation, without ever loading any OSGi specific class. On the other hand,
    in an OSGi environment, it will query the registry maintained by the registry bundle to locate
    the provider. The reference to the registry is injected into the `ProviderLocator`
    class using a bundle activator.

*   Finally, it should also be noted that the API bundle is configured with `singleton=true`.
    There is indeed no meaningful way how providers could be matched with different versions of the same API
    bundle.

This is an example of a particularly elegant way to satisfy [Requirement 1](#req1),
[Requirement 2](#req2) and [Requirement 3](#req3), especially because
it relies on the same metadata (the `META-INF/services/javax.xml.bind.JAXBContext` resources)
in OSGi and non OSGi environments.

Obviously, Axiom could reuse the registry and locator bundles developed by Geronimo. This however would
contradict [Requirement 7](#req7). In addition, for Axiom there is no requirement to
strictly follow the JDK 1.3 service discovery algorithm. Therefore Axiom should reuse the pattern
developed by Geronimo, but not the actual implementation.

## New abstract APIs

Application code rarely uses DOOM as the default Axiom implementation. Several downstream projects
(e.g. the Axis2/Rampart combination) use both the default (LLOM) implementation and DOOM. They select
the implementation based on the particular context. As of Axiom 1.2.12, the only way to create an object
model instance with the DOOM implementation is to use the `DOOMAbstractFactory` API
or to instantiate one of the factory classes (`OMDOMMetaFactory`, `OMDOMFactory`
or one of the subclasses of `DOMSOAPFactory`). All these classes are part of
the `axiom-dom` artifact. This is clearly in contradiction with [Requirement 4](#req4)
and [Requirement 5](#req5).

To overcome this problem the Axiom API must be enhanced to make it possible to select an Axiom
implementation based on capabilities/features requested by the application code. E.g. in the case
of DOOM, the application code would request a factory that implements the DOM API. It is then up
to the Axiom API classes to locate an appropriate implementation, which may be DOOM or another
drop-in replacement, as per [Requirement 5](#req5).

If multiple Axiom implementations are available (on the class path in non OSGi environment or
deployed as bundles in an OSGi environment), then the Axiom API must also be able to select an
appropriate default implementation if no specific feature is requested by the application code.
This can be easily implemented by defining a special feature called "default" that would be
declared by any Axiom implementation that is suitable as a default implementation.

**Note:** DOOM is generally not considered suitable as a default implementation because it doesn't
implement the complete Axiom API (e.g. it doesn't support `OMSourcedElement`).
In addition, in earlier versions of Axiom, the factory classes for DOOM were not stateless
(see [AXIOM-412](https://issues.apache.org/jira/browse/AXIOM-412)).

Finally, to make the selection algorithm deterministic, there should also be a concept
of priority: if multiple Axiom implementations are found for the same feature, then the Axiom API
would select the one with the highest priority.

This leads to the following design:

1.  Every Axiom implementation declares a set of features that it supports. A feature is
    simply identified by a string. Two features are predefined by the Axiom API:

    *   `default`: indicates that the implementation is a complete
        implementation of the Axiom API and may be used as a default implementation.
    *   `dom`: indicates that the implementation supports DOM
        in addition to the Axiom API.

    For every feature it declares, the Axiom implementation specifies a priority,
    which is a positive integer.

2.  The relevant Axiom APIs are enhanced so that they take an optional argument
    specifying the feature requested by the application code. If no explicit feature
    is requested, then Axiom will use the `default` feature.

3.  To determine the `OMMetaFactory` to be used, Axiom locates
    the implementations declaring the requested feature and selects the one that
    has the highest priority for that feature.

A remaining question is how the implementation declares the feature/priority information.
There are two options:

*   Add a method to `OMMetaFactory` that allows the Axiom API
    to query the feature/priority information from the implementation (i.e. the
    features and priorities are hardcoded in the implementation).

*   Let the implementation provide this information declaratively in its metadata
    (either in the manifest or in a separate resource with a well defined name).
    Note that in a non OSGi environment, such a metadata resource must be used anyway
    to enable the Axiom API to locate the `OMMetaFactory` implementations.
    Therefore this would be a natural place to declare the features as well.

The second option has the advantage to make it easier for users to debug and tweak
the implementation discovery process (e.g. there may be a need to
customize the features and priorities declared by the different implementations to ensure
that the right implementation is chosen in a particular use case).

This leads to the following design decision:
the features and priorities (together with the class name of the `OMMetaFactory`
implementation) will be defined in an XML descriptor with resource name `META-INF/axiom.xml`.
The format of that descriptor must take into account that a single JAR may contain several
Axiom implementations (e.g. if the JAR is an uber-JAR repackaged from the standard Axiom JARs).

## Common implementation classes

Obviously the LLOM and DOOM implementations share some amount of common code. Historically,
implementation classes reusable between LLOM and DOOM were placed in `axiom-api`.
This however tends to blur the distinction between the public API and implementation classes.
Starting with Axiom 1.2.13 such classes are placed into a separate module called
`axiom-common-impl`. However, `axiom-common-impl` cannot simply
be a dependency of `axiom-impl` and `axiom-dom`.
The reason is that in an OSGi environment, the `axiom-common-impl` bundle
would have to export these shared classes, which is in contradiction with [Requirement 4](#req4).
Therefore the code from `axiom-common-impl` needs to be packaged into
`axiom-impl` and `axiom-dom` by the build process so that
the `axiom-common-impl` artifact is not required at runtime.
[Requirement 1](#req1) forbids using embedded JARs to achieve this.
Instead `maven-shade-plugin` is used to include the classes
from `axiom-common-impl` into `axiom-impl` and `axiom-dom`
(and to modify the POMs to remove the dependencies on `axiom-common-impl`).

This raises the question whether `maven-shade-plugin` should be configured to
simply copy the classes or to relocate them (i.e. to change their package names). There are a couple
of arguments in favor of relocating them:

*   According to [Requirement 1](#req1), the Axiom artifacts should be
    usable both as normal JARs and as OSGi bundles. Obviously the expectation is that from the
    point of view of application code, they should work in the same in OSGi and non OSGi environments.
    Relocation is required if one wants to strictly satisfy this requirement even if different versions
    of `axiom-impl` and `axiom-dom` are mixed.
    Since the container creates separate class loaders for the `axiom-impl` and `axiom-dom` bundles,
    it is always possible to do that in an OSGi environment: even if the shared classes
    included in `axiom-impl` and `axiom-dom` are
    not relocated, but have the same names, this will not result in conflicts.
    The situation is different in a non OSGi environment where the classes in `axiom-impl`
    and `axiom-dom` are loaded by the same class loader. If the shared classes
    are not relocated, then there may be a conflict if the versions don't match.

    However, in practice it is unlikely that there are valid use case where one would use
    `axiom-impl` and `axiom-dom` artifacts from different Axiom versions.

*   Relocation allows to preserve compatibility when duplicate code from
    `axiom-impl` and `axiom-dom` is merged and moved
    to `axiom-common-impl`. The `OMNamespaceImpl`,
    `OMNavigator` and `OMStAXWrapper` classes
    from `axiom-impl` and the `NamespaceImpl`,
    `DOMNavigator` and `DOMStAXWrapper`
    classes from `axiom-dom` that existed in earlier versions of Axiom
    are examples of this. The classes in `axiom-dom` were almost identical
    to the corresponding classes in `axiom-impl`. These classes have been
    merged and moved to `axiom-common-impl`. Relocation then allows them
    to retain their original name (including the original package name) in the
    `axiom-impl` and `axiom-dom` artifacts.

    However, this is only a concern if one wants to preserve compatibility with existing
    code that directly uses these implementation specific classes (which is something that is
    strongly discouraged). One example where this was relevant was the SAAJ implementation
    in Axis2 which used to be very strongly coupled to the DOOM implementation. This however
    has been fixed now.

Using relocation also has some serious disadvantages:

*   Stack traces may contain class names that don't match class names in the Axiom source
    code, making debugging harder.

*   Axiom now uses JaCoCo to produce code coverage reports. However these reports are
    incomplete if relocation is used. This doesn't affect test cases executed in
    the `axiom-impl` and `axiom-dom` modules
    (because they are executed with the original classes), but tests in separate modules
    (such as integration tests). There are actually two issues:

    *   For the relocated classes, JaCoCo is unable to find the corresponding source code.
        This means that the reported code coverage is inaccurate for classes in
        `axiom-common-impl`.
    *   Relocation not only modifies the classes in `axiom-common-impl`, but
        also the classes in `axiom-impl` and `axiom-dom`
        that use them. JaCoCo [detects this](https://github.com/jacoco/jacoco/issues/51)
        and excludes the data from the coverage analysis. This means that the
        reported code coverage will also be inaccurate for classes in
        `axiom-impl` and `axiom-dom`.

In Axiom 1.2.14 relocation was used, but this has been changed in Axiom 1.2.15 because the disadvantages
outweigh the advantages.
