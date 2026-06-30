<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
-->

# Apache Axiom Security Threat Model (draft)

## §1 Header

- **Project**: Apache Axiom — *"contains core interfaces of the Axiom
  API"*, a StAX-based XML object model conforming to the W3C XML
  Information Set, with a SAX adapter, a MIME multipart parser for
  SOAP-with-Attachments / XOP / MTOM, and pluggable implementations
  (`axiom-impl` LLOM, `axiom-dom`) *(documented:
  `axiom-api/src/main/java/org/apache/axiom/om/package.html`)*.
- **Repository**: `apache/ws-axiom`.
- **Version / commit**: this model is drafted against the default branch
  at clone time. A report against project release *N* should be triaged
  against the model as it stood at *N*, not at HEAD.
- **Date**: 2026-05-30.
- **Authors**: ASF Security team draft, awaiting Axiom / Webservices PMC
  review.
- **Status**: draft — under maintainer review.
- **Reporting**: vulnerabilities that fall under §8 (claimed
  properties) should be reported per the Apache Security Team disclosure
  channel (<https://www.apache.org/security/>); reports that fall under
  §3 (out of scope) or §9 (properties not provided) will be closed by
  Axiom triagers citing this document. The project does not ship an
  in-repo `SECURITY.md` at draft time *(inferred — §14 Q1)*.
- **Provenance legend** —
  *(documented)* = drawn from in-repo docs / source comments / project
  website with citation;
  *(maintainer)* = stated by an Axiom maintainer in response to this
  draft;
  *(inferred)* = synthesized by the producer from code structure or
  domain knowledge, awaiting PMC ratification (every *(inferred)* tag has
  a matching §14 question).
- **Draft confidence**: 24 documented / 0 maintainer / 27 inferred.

Axiom is a Java library that provides an in-memory representation of an
XML document conforming to the W3C XML Infoset, with a streaming
backbone (StAX). It is the underlying OM (object model) used by Apache
Axis2, Apache WSS4J's bindings, Apache Neethi, and other SOAP-centric
runtimes. Axiom ships an API module (`axiom-api`), two implementations
(`axiom-impl` — the LLOM linked-list model; `axiom-dom` — a W3C-DOM
view of the same), a multipart MIME parser
(`org.apache.axiom.mime.MultipartBody`) for SOAP-with-Attachments / XOP
/ MTOM, a SAX adapter, a base64 utility, weaver-based bytecode
generation for the LLOM (`axiom-weaver`), and language-server / Jakarta
/ Javax compatibility shims.

## §2 Scope and intended use

### Intended use

- In-process Java OM for XML documents — both general-purpose XML and
  SOAP envelopes specifically *(documented:
  `axiom-api/src/main/java/org/apache/axiom/om/package.html`)*.
- The SOAP-oriented entry points (`OMXMLBuilderFactory.createSOAPModelBuilder`,
  `MultipartBody.builder()`) are designed to be consumed by a SOAP
  stack — historically Apache Axis2 — that builds an OM, performs
  message-level processing, and routes to a service implementation.

### Deployment shape

Axiom is an in-process library. No daemon, no listening socket, no CLI
on the supported surface. The threat model is therefore that of a
**streaming XML parser + DOM-like object model + MIME multipart
parser** with caller-selectable hardening profiles.

### Caller roles

| Role | Trust level | Notes |
| --- | --- | --- |
| **Embedding Java application / SOAP stack** | trusted | Calls Axiom's `OMXMLBuilderFactory.*` entry points, selects a `StAXParserConfiguration` (DEFAULT / STANDALONE / SOAP / COALESCING / NON_COALESCING / PRESERVE_CDATA_SECTIONS), supplies the bytes, optionally configures an `OMFactory` or custom `XMLInputFactory`. |
| **Caller-supplied `OMFactory` / `OMMetaFactory`** | trusted | Pluggable; `axiom-impl` (LLOM) and `axiom-dom` are the bundled choices. |
| **Producer of the XML / SOAP / MIME bytes** | **untrusted** | The only attacker-controllable input position. |
| **Producer of the JVM system property `org.apache.axiom.om.OMMetaFactoryLocator`** (and similar OSGi locator state) | trusted | Locator selection is a JVM-startup-time decision. |
| **Caller-supplied `XMLInputFactory` / `XMLOutputFactory`** | trusted | Caller may pass in their own; Axiom wraps it. If the caller's factory is mis-hardened, that is the caller's problem. |
| **Bundled implementations of `OMFactory`** (LLOM `axiom-impl`, DOM `axiom-dom`) | trusted upstream within this repo | Vulnerabilities in the implementation modules are in-model. |

### Component-family table

| Family | Representative entry point | Touches outside the process? | In-model? |
| --- | --- | --- | --- |
| `axiom-api` core OM — `OMElement`, `OMDocument`, `OMText`, `OMNode`, `OMContainer`, `OMSerializable` | `OMXMLBuilderFactory.createOMBuilder(InputStream)` | **no** | **yes** |
| `axiom-api` StAX wrapper — `XMLStreamReader`/`Writer` wrappers (`util/stax/`) | n/a | **no** | **yes** |
| `axiom-api` SAX adapter — `AbstractXMLReader` | n/a | **no** | **yes** |
| `axiom-api` MIME parser — `MultipartBody`, `PartImpl`, `Part`, `MultipartBodyWriter`, `ContentType`, `ContentTransferEncoding` | `MultipartBody.builder().setInputStream(...).build()` | **no** (operates on in-memory / caller stream) | **yes** |
| `axiom-api` base64 — `org.apache.axiom.util.base64.*` | n/a | **no** | **yes** |
| `axiom-api` locator — `org.apache.axiom.locator.*` (`OMMetaFactoryLocator`) | reads system properties + OSGi service registry | filesystem (classpath) | **yes** |
| `axiom-api` StAX dialect detection — `org.apache.axiom.util.stax.dialect.*` (Woodstox, SJSXP, Stax2) | reads classpath / `org.codehaus.stax2.*` | none directly | **yes** |
| `axiom-impl` (LLOM) — linked-list OM implementation | invoked through `OMFactory` | none | **yes** |
| `axiom-dom` — W3C DOM view of the same OM | invoked through `OMFactory` | none | **yes** |
| `axiom-weaver` + `axiom-weaver-annotations` — bytecode-generation helper for the implementations | build-time only | none at runtime | **out of model at runtime** *(§3)* — in-model only insofar as it affects the generated implementation |
| `axiom-jakarta-activation`, `axiom-javax-activation`, `axiom-jakarta-jaxb`, `axiom-javax-jaxb`, `axiom-compat`, `axiom-legacy-attachments`, `axiom-mixins`, `jakarta-bom`, `javax-bom` | shim / BOM modules | none directly | **yes** insofar as they re-expose APIs |
| `samples/`, `systests/`, `apidocs/`, `devguide/`, `legal/`, `distribution/`, `etc/`, `testing/`, `buildutils/`, `mixins/` | sample / test / packaging | n/a | **out of model** *(§3)* |

A finding is in-model only if it reaches a row marked **yes**.

## §3 Out of scope (explicit non-goals)

1. **A SOAP / WSDL processor.** Axiom provides the OM and the
   SOAP-message-level helpers (`SOAPEnvelope`, `SOAPHeader`, `SOAPBody`,
   `SOAPMessage`, etc.); routing / service-implementation dispatch is
   the SOAP stack's responsibility (Axis2). → `OUT-OF-MODEL:
   out-of-layer`.
2. **The transport layer.** Axiom does not open sockets or read from
   the network on its own. → `OUT-OF-MODEL: out-of-layer`.
3. **Validation against an XML Schema.** Axiom is an OM library, not
   a validator. → `OUT-OF-MODEL: out-of-layer`.
4. **The XML parser itself.** Axiom uses whatever StAX implementation
   the JDK / classpath supplies (Woodstox, SJSXP, JDK built-in). It
   *configures* the parser via `StAXParserConfiguration` but does not
   ship a parser. Defects intrinsic to Woodstox / SJSXP / the JDK StAX
   implementation are out of model and reported upstream *(inferred —
   §14 Q2)*. → `OUT-OF-MODEL: unsupported-component`.
5. **`axiom-weaver` runtime behavior.** The weaver is a build-time
   tool that generates LLOM implementation classes; defects in
   *generated* code land in `axiom-impl` and are in-model there. The
   weaver itself is out of model. → `OUT-OF-MODEL: unsupported-component`.
6. **`samples/`, `systests/`, `testing/*`.** Sample apps and test
   support. → `OUT-OF-MODEL: unsupported-component`.
7. **Apache Axis2.** Axis2 is the most well-known consumer of Axiom
   but lives in a separate repository (`apache/axis-axis2-java-core`)
   with its own threat model. → `OUT-OF-MODEL: out-of-layer`.
8. **Apache WSS4J's WS-Security processing on an Axiom OM.** WSS4J
   has its own threat model; Axiom only provides the OM. → `OUT-OF-MODEL:
   out-of-layer`.

## §4 Trust boundaries and data flow

| # | Transition | Authentication | Authorization |
| --- | --- | --- | --- |
| B1 | Caller → `OMXMLBuilderFactory.createOMBuilder(InputStream | Reader | Source | XMLStreamReader)` | none — caller is trusted | none |
| B2 | `OMXMLBuilderFactory.*` → bundled `XMLInputFactory` (selected by `StAXDialectDetector`, configured by `StAXParserConfiguration`) | none | the *configuration* chosen at the builder call is the gate |
| B3 | `OMXMLBuilderFactory.createSOAPModelBuilder(InputStream)` → bundled `XMLInputFactory` configured for SOAP (rejects DOCTYPE) — see `StAXParserConfiguration.SOAP` *(documented: `StAXParserConfiguration.java` lines 135-145)* | none | SOAP profile is the gate |
| B4 | Caller → `MultipartBody.builder().setInputStream(...).build()` | none | MIME framing parsed by Axiom; per-part bytes wrapped as `Part` |
| B5 | Caller → `OMMetaFactoryLocator` (via `org.apache.axiom.om.OMMetaFactoryLocator` system property and OSGi service registry) | trusted | startup-time class loading |

### Reachability preconditions per family

- **Core OM builder** (`OMXMLBuilderFactory.createOMBuilder(InputStream)`):
  in-model when bytes are attacker-controlled. The default
  `StAXParserConfiguration` is **DEFAULT** = **PRESERVE_CDATA_SECTIONS**
  *(documented: `StAXParserConfiguration.java` lines 48-58, 60-61;
  `OMXMLBuilderFactory.java` line 107)*. DEFAULT does **not** disable
  DTD support, does **not** disable external entities, does **not**
  disallow DOCTYPE. It only enables CDATA reporting.
- **SOAP-specific builder** (`OMXMLBuilderFactory.createSOAPModelBuilder(InputStream)`):
  in-model when bytes are attacker-controlled. The builder uses the
  **SOAP** profile, which *disallows DOCTYPE declarations* on the wire
  *(documented: `StAXParserConfiguration.java` lines 125-145)*.
- **STANDALONE profile** (caller explicitly chooses
  `StAXParserConfiguration.STANDALONE`): sets
  `IS_SUPPORTING_EXTERNAL_ENTITIES=false` and installs a custom
  `XMLResolver` that returns empty content for any external resource
  *(documented: `StAXParserConfiguration.java` lines 72-95)*. This is
  the hardened profile for general XML input.
- **MIME parser** (`MultipartBody.builder().setInputStream(...).build()`):
  in-model when bytes are attacker-controlled. Parses MIME framing,
  produces per-part streams; size is bounded only by what the
  configured `PartBlobFactory` chooses to do *(inferred — §14 Q3)*.
- **`axiom-impl` / `axiom-dom`**: in-model insofar as they implement
  the API surface; reachable only via the API.
- **Locator / dialect detection**: in-model insofar as it influences
  which StAX implementation is loaded; the *selection* relies on the
  system property and OSGi state being operator-controlled.

## §5 Assumptions about the environment

- **JDK**: minimum supported JDK version is in `pom.xml`. Axiom 2.0+
  targets a modern JDK; earlier branches supported older JDKs
  *(inferred — §14 Q4)*.
- **StAX implementation**: a working StAX provider on the classpath
  (Woodstox is the supported preferred provider; SJSXP and the
  JDK StAX implementation are also recognized by `StAXDialectDetector`)
  *(documented: `axiom-api/src/main/java/org/apache/axiom/util/stax/dialect/`)*.
- **Bundled JDK XML platform**: standard `javax.xml.stream.XMLInputFactory`
  / `XMLOutputFactory`.
- **JDK MIME/Mail platform**: NOT required — Axiom's MIME parser is
  self-contained in `org.apache.axiom.mime`.
- **`jakarta-activation` / `javax-activation`**: optional shim modules
  for the JavaBeans Activation Framework (DataSource / DataHandler);
  in-model insofar as Axiom uses them.
- **`AGENTS.md`** — *"This project uses the spotless-maven-plugin
  with `palantirJavaFormat` to enforce consistent Java formatting"*
  *(documented: `AGENTS.md`)*. This is build-time formatting; not a
  runtime claim.

### What Axiom does *not* do to its host (negative claims, awaiting maintainer ratification)

- Opens **no** listening sockets *(inferred — §14 Q5)*.
- Spawns **no** child processes *(inferred — §14 Q5)*.
- Installs **no** signal handlers *(inferred — §14 Q5)*.
- Reads a small set of documented system properties for
  `OMMetaFactoryLocator` selection and for compatibility hacks; does
  **not** consume `LD_*`-style envvars for security-sensitive decisions
  *(inferred — §14 Q5)*.
- Writes nothing of its own initiative; the serializer writes to the
  `OutputStream` / `Writer` the caller hands in *(inferred — §14 Q5)*.

## §5a Build-time and configuration variants

Axiom ships as a single Maven artifact set. There are no compile-time
feature toggles known to materially change the security envelope
*(inferred — §14 Q6)*. The runtime security envelope is shaped by
the choice of **`StAXParserConfiguration`** at builder-call time:

| Profile | XXE / DTD posture | Maintainer stance | When intended |
| --- | --- | --- | --- |
| `DEFAULT` (== `PRESERVE_CDATA_SECTIONS`) | DTD allowed; external entities **enabled by JDK default**; DOCTYPE allowed | **maintainer ruling required** — see §14 Q7 | General XML input where the caller already trusts the bytes |
| `STANDALONE` | DTD allowed but external entities returned as empty via custom `XMLResolver`; DOCTYPE allowed | safe-by-default for non-SOAP input from untrusted sources; caller explicitly opts in | XML parsing of untrusted-but-non-SOAP input |
| `SOAP` | DTD **disallowed** at first encounter (`dialect.disallowDoctypeDecl(...)`) | safe-by-default for SOAP input | SOAP envelope parsing (RFC says SOAP MUST NOT contain DTDs) |
| `COALESCING` | inherits the calling profile | n/a (orthogonal to XXE) | Caller wants coalesced text nodes |
| `NON_COALESCING` | inherits the calling profile | n/a (orthogonal to XXE) | Caller wants non-coalesced text nodes |
| `PRESERVE_CDATA_SECTIONS` | inherits the calling profile | n/a (orthogonal to XXE) | Caller wants CDATA preserved |

### The insecure-default case

The default `OMXMLBuilderFactory.createOMBuilder(InputStream)` resolves
to `StAXParserConfiguration.DEFAULT`, which does **not** disable DTD
processing or external entities *(documented:
`OMXMLBuilderFactory.java` lines 106-107, 119-120, 222, 248;
`StAXParserConfiguration.java` lines 60-61)*. The maintainer ruling
captured in §14 Q7 will determine whether a report shaped "I called
`createOMBuilder(InputStream)` on attacker bytes and an XXE worked"
is:

- (a) `VALID` — because the SDK default should be safe and the project
  will fix by changing the default; **or**
- (b) `OUT-OF-MODEL: non-default-build` — because the caller is
  *documented as expected* to choose `SOAP` for SOAP input and
  `STANDALONE` for general untrusted XML, per §10.

The SOAP-specific entry point
(`OMXMLBuilderFactory.createSOAPModelBuilder`) installs the SOAP
profile by default, which **does** block DOCTYPE. Reports against
SOAP input through the SOAP-specific entry point are unambiguously
`VALID` if a DTD slips through.

System property knobs (locator selection, dialect overrides) are
trusted by §3 / §5.

## §6 Assumptions about inputs

### Per-entry-point trust table

| Entry point | Parameter | Attacker-controllable? | Caller must enforce |
| --- | --- | --- | --- |
| `OMXMLBuilderFactory.createOMBuilder(InputStream)` | bytes | **yes** | pick the right `StAXParserConfiguration` — DEFAULT does **not** block XXE; caller must use `STANDALONE` for untrusted general XML |
| `OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration, InputStream)` | bytes + config | **yes** | use `STANDALONE` for untrusted general XML, `SOAP` for SOAP input |
| `OMXMLBuilderFactory.createOMBuilder(Reader)` | character data | **yes** | same as above |
| `OMXMLBuilderFactory.createOMBuilder(Source)` | bytes / chars | **yes** | same as above |
| `OMXMLBuilderFactory.createOMBuilder(XMLStreamReader)` | events | **yes if the StreamReader was built on attacker bytes** | caller's `XMLInputFactory` is responsible for XXE / DTD posture; Axiom does not re-configure |
| `OMXMLBuilderFactory.createSOAPModelBuilder(InputStream)` | SOAP bytes | **yes** | nothing — uses `StAXParserConfiguration.SOAP` which disallows DOCTYPE |
| `OMXMLBuilderFactory.createSOAPModelBuilder(StAXParserConfiguration, InputStream)` | SOAP bytes + override | **yes** | if caller overrides to a less safe profile, the override is the caller's choice |
| `MultipartBody.builder().setInputStream(InputStream).build()` | MIME bytes | **yes** | bound the maximum total body size externally |
| `MultipartBody.builder().setPartBlobFactory(...)` | factory | **trusted** | caller's choice; affects per-part storage (memory vs spool to disk) |
| `MultipartBody.builder().setContentType(...)` | Content-Type string | caller-supplied | caller controls; Axiom parses the boundary delimiter from it |
| `PartImpl.getInputStream()` | per-part read | streams attacker bytes | caller must close the stream |
| `OMElement.serialize(OutputStream)` / `.serialize(Writer)` | output sink | caller-supplied | caller's choice |
| `Base64.decode(String)` | base64 input | **yes if from attacker** | size is the caller's responsibility |
| `OMMetaFactoryLocator` (system property) | class name | **trusted by §3, §5** | operator must lock down property setting |

### Size / shape / rate

- No documented bound on input XML size; the StAX implementation may
  apply its own (Woodstox `P_MAX_*` properties, JDK
  `entityExpansionLimit`).
- No documented bound on MIME multipart total size, per-part size, or
  part count *(inferred — §14 Q3)*.
- No documented bound on base64 input size or output expansion ratio
  *(inferred — §14 Q3)*.
- No rate limit of any kind; Axiom is a parsing library, not a
  service.

## §7 Adversary model

### Actors

| Actor | In scope? | Capabilities |
| --- | --- | --- |
| **Producer of the XML / SOAP bytes** | **yes** | full byte control of the input stream |
| **Producer of the MIME multipart bytes** | **yes** | full byte control of `Content-Type` boundary + part bodies |
| **Producer of a `<!DOCTYPE>` declaration referencing `http://attacker/`** | in scope only when the **DEFAULT** profile is in use against untrusted bytes — see §14 Q7 |
| **Producer of a base64-encoded payload that decompresses to gigabytes** | in scope insofar as Axiom decodes; **caller bounds output size** |
| **In-process caller** | **out of scope** — trusted by construction |
| **Owner of the JVM system properties / classpath** | **out of scope** |
| **Author of a hostile `OMFactory` / `OMMetaFactory`** | **out of scope** *(§2)* |
| **Author of a hostile `XMLInputFactory` / `XMLOutputFactory` passed via `setStAXFactory`** | **out of scope** *(§2)* |
| **Co-tenant in shared JVM** | **out of scope** *(inferred — §14 Q8)* |
| **Side-channel observer** | **out of scope** *(inferred — §14 Q8)* |
| **Quantum adversary** | **out of scope** |

## §8 Security properties the project provides

### P1 — `StAXParserConfiguration.SOAP` rejects DOCTYPE in incoming SOAP envelopes

- **Condition**: caller used `OMXMLBuilderFactory.createSOAPModelBuilder(InputStream)`
  (which selects the SOAP profile by default) or explicitly selected
  `StAXParserConfiguration.SOAP`.
- **Violation symptom**: an inbound SOAP envelope containing a
  `<!DOCTYPE ...>` is parsed to completion without an exception, and
  the XXE / billion-laughs payload triggers.
- **Severity**: **security-critical**, `VALID` per §13.
- *(documented: `StAXParserConfiguration.java` lines 125-145;
  `OMXMLBuilderFactory.java` SOAP overloads (e.g.
  `createSOAPModelBuilder(InputStream)` family))*

### P2 — `StAXParserConfiguration.STANDALONE` returns empty content for any external entity reference

- **Condition**: caller explicitly selected
  `StAXParserConfiguration.STANDALONE`.
- **Violation symptom**: an external entity reference (`<!ENTITY foo
  SYSTEM "http://attacker/" >`) results in HTTP fetch to the
  attacker URL.
- **Severity**: **security-critical**, `VALID` per §13.
- *(documented: `StAXParserConfiguration.java` lines 72-95)*

### P3 — `OMSerializable.serialize(OutputStream)` produces well-formed XML for any well-formed OM

- **Condition**: the OM was built by Axiom from a well-formed source.
- **Violation symptom**: serialized output is not well-formed XML.
- **Severity**: **correctness-only**, `VALID-HARDENING` if a
  downstream parser interprets the malformed output in a
  security-relevant way *(inferred — §14 Q9)*.
- *(inferred — §14 Q9)*

### P4 — MIME multipart parser produces per-`Part` streams that do not bleed bytes from neighboring parts

- **Condition**: a well-formed MIME multipart input.
- **Violation symptom**: `PartImpl.getInputStream()` returns bytes
  past the boundary of its part, contaminating a neighboring `Part`'s
  body.
- **Severity**: **security-critical** when the parts cross a trust
  boundary (e.g. one part is signed/authenticated and a neighboring
  part is not); `VALID` per §13.
- *(inferred — §14 Q10)*

### P5 — `Base64.decode` does not crash the process on adversarial input

- **Condition**: input is a `String` or `byte[]`.
- **Violation symptom**: Java exception not caught at the caller —
  but this is Java; memory corruption is JVM-level.
- **Severity**: **correctness-only** unless the decoder produces
  output orders-of-magnitude larger than its input without warning.
- *(inferred — §14 Q11)*

## §9 Security properties the project does *not* provide

State each plainly so a triager can route an inbound report to the
matching disclaimer.

- **No XXE defense on the default `OMXMLBuilderFactory.createOMBuilder`
  path.** The default profile (`DEFAULT` == `PRESERVE_CDATA_SECTIONS`)
  does *not* disable DTD processing or external entities; it only
  enables CDATA reporting. Callers handling untrusted *non-SOAP* XML
  must explicitly select `STANDALONE` *(documented:
  `StAXParserConfiguration.java` lines 48-95;
  `OMXMLBuilderFactory.java` line 107)*.
- **No defense when the caller passes in a pre-built `XMLStreamReader`.**
  Axiom does not re-configure caller-supplied readers; the caller's
  `XMLInputFactory` is the gate.
- **No bound on input XML / SOAP / MIME size.** Whatever the embedding
  application supplied, Axiom processes. The StAX implementation may
  enforce its own limits.
- **No bound on the number of parts in a MIME multipart, the size of
  any individual part, or the total decoded size of a base64 payload.**
- **No protection against XML signature wrapping.** Axiom is the OM;
  signature-wrapping is the WS-Security layer's concern (WSS4J) *(see
  WSS4J §8 P2)*.
- **No data-at-rest protection.** Serialized output is whatever
  bytes the caller's sink stores.
- **No constant-time guarantees.** Axiom does not deal with secrets.
- **No transport-layer security** — there is no transport.
- **No protection against a hostile `OMFactory`, `OMMetaFactory`,
  `XMLInputFactory`, `XMLOutputFactory`, or `XMLResolver` plugged in
  by the caller.**
- **No defense against side-channel observation.**
- **No quantum resistance.**
- **No claim about thread-safety of the OM model across writes.**
  Concurrent mutation of the same `OMElement` is unsupported *(inferred
  — §14 Q12)*.

### False-friend properties (call out separately)

- **`DEFAULT` profile name looks safe, but it isn't a hardened
  default for untrusted bytes.** `StAXParserConfiguration.DEFAULT` is
  the *baseline* shape (CDATA preserved); it does **not** disable XXE
  / DTD. The `SOAP` and `STANDALONE` profiles are the hardened
  selections *(documented: `StAXParserConfiguration.java`)*.
- **`OMXMLBuilderFactory.createOMBuilder(InputStream)` looks
  symmetric with `createSOAPModelBuilder(InputStream)`, but its
  default profile is different.** The SOAP-specific entry point
  selects the SOAP-hardened profile; the general OM entry point
  selects DEFAULT.
- **`PRESERVE_CDATA_SECTIONS` looks like a security feature, but it
  isn't.** It is a fidelity feature; CDATA preservation is about
  round-tripping, not safety.
- **`MultipartBody` parser looks like a complete RFC 2046 / RFC 5322
  email parser, but it isn't.** It is targeted at SOAP-with-Attachments
  / XOP / MTOM and may not handle the full surface of email-style MIME
  (`message/rfc822` nested envelopes, transfer-encoding `quoted-printable`
  edge cases, …) *(inferred — §14 Q3)*.
- **`OMMetaFactoryLocator` system property is a privilege-escalation
  surface, not a feature flag.** A class named there is `Class.forName`-
  loaded into the running JVM.
- **The `axiom-dom` implementation is *not* a drop-in W3C DOM**; it
  is a W3C-DOM-shaped view of an Axiom OM *(documented: `axiom-dom`
  module purpose)*.

### Well-known attack classes Axiom does not single-handedly defend against

- **XXE / external entity disclosure** via the DEFAULT profile.
- **Billion-laughs / quadratic blowup** via the DEFAULT profile.
- **Compression bombs in base64** — decoder size is the caller's
  bound.
- **MIME nesting bombs** via deeply-nested multipart bodies
  *(inferred — §14 Q3)*.
- **Signature wrapping** — WSS4J's concern.
- **MIME boundary smuggling** — Axiom parses the boundary the caller's
  `Content-Type` declared; if a part body legitimately contains the
  boundary string and the producer didn't escape, Axiom's behavior is
  per RFC.

## §10 Downstream responsibilities

The embedding Java application **must**:

1. For SOAP input, use `OMXMLBuilderFactory.createSOAPModelBuilder(InputStream)`
   (or pass `StAXParserConfiguration.SOAP` explicitly). The SOAP
   profile blocks DOCTYPE *(documented:
   `StAXParserConfiguration.java` line 135-145)*.
2. For general untrusted XML input, **explicitly** pass
   `StAXParserConfiguration.STANDALONE` to `createOMBuilder`. The
   bare `createOMBuilder(InputStream)` defaults to a profile that does
   **not** disable XXE / DTD.
3. When passing a pre-built `XMLStreamReader`, configure the source
   `XMLInputFactory` with `SUPPORT_DTD=false` and
   `IS_SUPPORTING_EXTERNAL_ENTITIES=false`.
4. Bound the maximum input size at the *caller's* HTTP / transport
   layer — Axiom imposes none.
5. Bound MIME multipart total size, per-part size, and part count at
   the caller layer.
6. Set `OMMetaFactoryLocator` system properties only at JVM startup
   from a trusted source.
7. Treat any caller-installed `OMFactory`, `XMLInputFactory`,
   `XMLOutputFactory`, `XMLResolver`, or `PartBlobFactory` as part of
   the security TCB.
8. For deployment shapes that ingest SOAP envelopes that may carry
   attachments, route signature-wrapping defense through WSS4J or an
   equivalent message-level security layer; Axiom is the OM, not the
   policy.
9. Do not let untrusted actors influence the `Content-Type` header
   whose `boundary` parameter the MIME parser will key on.

## §11 Known misuse patterns

- **Calling `OMXMLBuilderFactory.createOMBuilder(InputStream)` on
  attacker-controlled XML and assuming the default profile blocks
  XXE.** It doesn't *(documented: `StAXParserConfiguration.java`)*.
- **Using `createSOAPModelBuilder(...)` with an explicit
  `StAXParserConfiguration.DEFAULT` override.** Override defeats the
  SOAP-profile DOCTYPE block.
- **Wrapping a JDK `XMLInputFactory` that was not hardened against
  XXE.** Axiom does not retroactively reconfigure.
- **Trusting `MultipartBody` to enforce a maximum part count or
  per-part size.** It does not; caller is responsible.
- **Using `Base64.decode` on attacker bytes and storing the result in
  memory unbounded.**
- **Setting `OMMetaFactoryLocator` system property from a servlet-
  context init parameter.** The named class is `Class.forName`-loaded.
- **Mutating the same `OMElement` from multiple threads.** Threading
  guarantees are unspecified *(inferred — §14 Q12)*.
- **Mixing `axiom-impl` and `axiom-dom` OMs on the same
  `OMMetaFactory` chain.** Behavior is undefined *(inferred —
  §14 Q13)*.
- **Building a SOAP envelope via the general `createOMBuilder` then
  calling `getEnvelope()`.** The two builder paths are not
  interchangeable; the SOAP path validates SOAP-specific shape
  *(inferred — §14 Q14)*.
- **Trusting that `MultipartBody.getPart(contentId)` returns at most
  one part per content-id.** Repeated content-ids are RFC-illegal but
  not necessarily rejected by Axiom *(inferred — §14 Q15)*.

## §11a Known non-findings (recurring false positives)

This section is the highest-leverage input for automated agentic
security scans. Each entry: tool symptom, why it is safe under the
model, the section that licenses the call.

- **"`XMLInputFactory.newInstance()` in `StAXUtils` does not set
  `SUPPORT_DTD=false`."** Axiom's *profile-based* design intentionally
  exposes the choice to the caller; the `SOAP` profile sets it via
  `disallowDoctypeDecl()`, the `STANDALONE` profile sets it
  *(documented: `StAXParserConfiguration.java` lines 72-95, 125-145)*.
  → `KNOWN-NON-FINDING` if the report was against the general
  `createOMBuilder` path; investigate via §14 Q7 otherwise.
- **"`DocumentBuilderFactory.newInstance()` without hardening."**
  Axiom does not provide a `DocumentBuilder`-based entry point; if a
  tool flags this against `axiom-dom`, check whether `axiom-dom`
  internally constructs DOMs from caller OMs (it does not). →
  `KNOWN-NON-FINDING`.
- **"`Class.forName(System.getProperty('org.apache.axiom.om.OMMetaFactoryLocator'))`
  is dynamic-class-loading."** Documented locator extension point; the
  system property is the trust gate. → `OUT-OF-MODEL: trusted-input`.
- **"`MultipartBody` reads MIME boundaries from `Content-Type` without
  authentication."** The MIME parser parses what RFC 2046 says to
  parse; authentication is a higher-layer concern. →
  `BY-DESIGN: property-disclaimed` per §9.
- **"Hardcoded `'transmitter.jks'` / `'receiver.jks'` password in
  test resources."** No such resource ships in `axiom-api/src/main`;
  if a tool flags `testing/*`, that's test data. → `OUT-OF-MODEL:
  unsupported-component`.
- **"`AccessController.doPrivileged` deprecated in JDK 17."** JDK
  compatibility wrapper. → `KNOWN-NON-FINDING`.
- **"`InputStream.close()` not in finally."** Code-quality finding,
  not security. → `OUT-OF-MODEL: out-of-layer`.
- **"`OMElement.toString()` may serialize without escaping."** The
  documented API is `OMElement.serialize(OutputStream)`; `toString()`
  is debug-only *(inferred — §14 Q16)*. → `KNOWN-NON-FINDING`.
- **"`base64` decoder accepts non-canonical whitespace / padding."**
  RFC-permissive behavior; not a security defect. → `KNOWN-NON-FINDING`.
- **"`OMXMLBuilderFactory.createOMBuilder(InputStream)` enables XXE."**
  By design — the caller chose the DEFAULT profile by not passing one.
  See §14 Q7 — currently *(inferred)* as `OUT-OF-MODEL:
  non-default-build` *or* `VALID-HARDENING`. → `KNOWN-NON-FINDING`
  *only* if Q7 rules the default is "dev-default, caller must pick";
  otherwise `VALID-HARDENING`.
- **"`StAXDialectDetector` reflectively loads provider-specific
  classes."** Dialect detection helper; loads only documented
  provider classes (Woodstox, SJSXP, Stax2). → `KNOWN-NON-FINDING`.
- **"`axiom-weaver` generates bytecode at build time."** Build-time
  tool; not in the runtime trust boundary. → `OUT-OF-MODEL:
  unsupported-component`.
- **"`OMText` may carry binary attachment data — possible deserialization
  injection."** `OMText` is a typed leaf, not Java deserialization. →
  `KNOWN-NON-FINDING`.

## §12 Conditions that would change this model

Revise this document when any of the following lands:

- A change in the default `StAXParserConfiguration` for
  `OMXMLBuilderFactory.createOMBuilder(InputStream)` (e.g. flipping
  DEFAULT to a hardened profile).
- A new `StAXParserConfiguration` profile or a change in the SOAP
  profile's DOCTYPE block.
- A new public entry point on `OMXMLBuilderFactory` that accepts new
  input shapes.
- A new MIME-multipart entry point or a change in `MultipartBody`'s
  per-part bound behavior.
- A new OM implementation (`axiom-impl` / `axiom-dom` siblings).
- A new locator mechanism beyond `OMMetaFactoryLocator`.
- A change in the supported StAX provider matrix.
- A change in the Jakarta / Javax bifurcation that introduces a new
  module.
- A vulnerability report that cannot be cleanly routed to one of the
  §13 dispositions — evidence the model has a gap.

## §13 Triage dispositions

A report against Axiom receives exactly one of the following:

| Disposition | Meaning | Licensed by |
| --- | --- | --- |
| `VALID` | Violates a §8 property via an in-scope §7 adversary using an in-scope §6 input. | §8, §6, §7 |
| `VALID-HARDENING` | No §8 property violated, but a §11 misuse pattern can be made harder to fall into by code change. Typically no CVE. | §11 |
| `OUT-OF-MODEL: trusted-input` | Requires attacker control of a §6 parameter the model marks trusted (caller-supplied `XMLStreamReader`, hostile `OMFactory`, system-property-controlled locator). | §6 |
| `OUT-OF-MODEL: adversary-not-in-scope` | Requires a §7 actor the model excludes (in-process caller, operator). | §7 |
| `OUT-OF-MODEL: unsupported-component` | Lands in `samples/`, `systests/`, `testing/`, `axiom-weaver/`, etc. | §3 items 5, 6 |
| `OUT-OF-MODEL: non-default-build` | Only manifests under a `StAXParserConfiguration` the maintainer rules dev/test (see §14 Q7). | §5a |
| `OUT-OF-MODEL: out-of-layer` | Concerns a SOAP / Axis2 / WSS4J responsibility, or a transport / validation step Axiom does not implement. | §3 items 1–3, 7, 8 |
| `BY-DESIGN: property-disclaimed` | Concerns a §9 property the project explicitly does not provide (no XXE defense on DEFAULT, no MIME size bound, no thread-safety on mutation). | §9 |
| `KNOWN-NON-FINDING` | Matches a §11a recurring false positive. | §11a |
| `MODEL-GAP` | Cannot be cleanly routed to any of the above — triggers §12 model revision. | §12 |

## §14 Open questions for the maintainers

Every *(inferred)* tag in the body maps to one of these. Proposed
answers are inline; please confirm, correct, or strike.

### Wave 1 — security policy + meta

**Q1.** Axiom does not currently ship an in-repo `SECURITY.md`.
Should the project (a) adopt a `SECURITY.md` that names a supported-
branch matrix (proposed), (b) leave reporting to the foundation page
only, or (c) defer to the Webservices PMC's umbrella policy? *(meta)*

**Q2.** Defects intrinsic to Woodstox / SJSXP / the JDK StAX
implementation — confirm policy is "report upstream; Axiom picks up
via classpath" (proposed: **yes**). *(maps to §3 item 4)*

### Wave 2 — XXE / DTD posture (highest leverage)

**Q3.** Confirm that `MultipartBody`'s MIME parser imposes **no**
documented bound on:
  - total body size
  - per-part body size
  - number of parts
  - MIME nesting depth (nested `multipart/related`)
  - `Content-Transfer-Encoding: quoted-printable` expansion ratio

Proposed: **no bound; caller imposes**. *(maps to §6, §9, §10 item 5)*

**Q4.** Supported JDK matrix per branch. Proposed: 2.0+ targets a
modern JDK (please specify). *(maps to §5)*

**Q5.** Negative-side inventory in §5: Axiom opens **no** sockets,
spawns **no** processes, installs **no** signal handlers, reads only
documented system properties, and writes nothing of its own
initiative. Confirm? *(maps to §5)*

**Q6.** Build-time variants: confirm there are no compile-time feature
toggles that materially change the security envelope (proposed: none).
*(maps to §5a)*

**Q7.** **The big DEFAULT-profile question.** A SOAP-specific
constructor exists and uses `StAXParserConfiguration.SOAP`, which
blocks DOCTYPE. The general `OMXMLBuilderFactory.createOMBuilder(InputStream)`
constructor defaults to `StAXParserConfiguration.DEFAULT`, which does
**not** block XXE / DTD. Is this:

- (a) **Supported production posture** — i.e. a report that the
  bare `createOMBuilder(InputStream)` allowed XXE is `VALID`, and
  the project will fix by flipping the default; **or**
- (b) **"Dev/test default; callers are documented as required to
  pick `STANDALONE` for untrusted general XML"** — i.e. same report
  is `OUT-OF-MODEL: non-default-build`?

Proposed: **(b)**, but a strong argument can be made that the default
should be flipped to STANDALONE for safety. *(maps to §5a, §9, §10
item 2, §11, §11a, §13)*

### Wave 3 — adversary model, edge cases

**Q8.** Co-tenant in shared JVM and side-channel observers: out of
scope (proposed)? *(maps to §7)*

**Q9.** §8 P3 (round-trip serialize correctness) — confirm this is
correctness-only, with a `VALID-HARDENING` carve-out when the
divergence creates a security-meaningful downstream misinterpretation.
*(maps to §8 P3)*

**Q10.** §8 P4 (MIME parts do not bleed bytes) — confirm this is a
claimed property; identify the existing regression tests that exercise
the boundary parsing on adversarial input. *(maps to §8 P4)*

**Q11.** §8 P5 (base64 decoder safety) — confirm correctness-only
with no claim about output-size expansion. *(maps to §8 P5)*

**Q12.** Thread-safety: confirm that concurrent *mutation* of the
same `OMElement` is unsupported (proposed); concurrent *reads* on a
fully-built immutable OM are supported (proposed). *(maps to §9, §11)*

**Q13.** Mixing `axiom-impl` and `axiom-dom` on the same
`OMMetaFactory` chain: behavior undefined (proposed)? *(maps to §11)*

**Q14.** SOAP shape validation: confirm the SOAP-specific builders
validate SOAP-specific structural rules (envelope element, header /
body location) and the general builders do not. *(maps to §11)*

**Q15.** `MultipartBody.getPart(contentId)`: behavior on repeated
content-id values — first-wins, last-wins, or all-rejected (proposed:
first-wins)? *(maps to §11)*

**Q16.** `OMElement.toString()` semantics — documented as debug-only
(proposed)? *(maps to §11a)*

### Wave 4 — coexistence & publication

**Q17.** This document should be hosted in-repo at
`docs/security/threat-model.md` (proposed) or on
`ws.apache.org/axiom/`? *(meta)*

**Q18.** Should this document explicitly note the AGENTS.md
`spotless:apply` formatting requirement as **out of scope** (proposed:
out of scope — formatting is not a security property)? *(meta)*

**Q19.** §11a known-non-findings is thin (~13 patterns). Could the
Axiom PMC populate from JIRA "not a bug" / "wontfix" closures
(`AXIOM-*` tickets)? Concrete asks: 3–5 patterns the PMC sees recur in
inbound reports. *(meta — §11a)*

**Q20.** What kind of change to Axiom should trigger a revision
(proposed list in §12 — confirm or correct)? *(meta — §12)*

---

## Appendix: SECURITY.md / website → §x back-map

Axiom does not ship a `SECURITY.md`. The threat-model sources are the
in-repo `README.txt`, `AGENTS.md`, the JavaDoc / source comments, the
`docs/` directory, and the project website
<https://ws.apache.org/axiom/>.

| Source | Claim | Lands in |
| --- | --- | --- |
| `README.txt` | Axiom is the OM library | §1 |
| `AGENTS.md` | spotless formatting requirement at build time | §5 environment (build-time only) |
| `axiom-api/src/main/java/org/apache/axiom/om/package.html` | "Contains core interfaces of the Axiom API … defines interfaces for the information items identified by the XML Information Set" | §1, §2 intended use, §2 component-family table |
| `axiom-api/src/main/java/org/apache/axiom/om/util/StAXParserConfiguration.java` lines 48-58, 60-61 | DEFAULT == PRESERVE_CDATA_SECTIONS — enables CDATA reporting, **does not** disable DTD / external entities | §5a, §6, §9 first bullet, §11, §11a, §14 Q7 |
| `StAXParserConfiguration.java` lines 72-95 | STANDALONE sets `IS_SUPPORTING_EXTERNAL_ENTITIES=false` and installs `XMLResolver` returning empty content; "to process documents referencing DTDs with system IDs that are network locations" | §5a, §8 P2, §10 item 2 |
| `StAXParserConfiguration.java` lines 125-145 | SOAP profile disallows DOCTYPE — "A SOAP message MUST NOT contain a Document Type Declaration" | §5a, §8 P1, §10 item 1 |
| `axiom-api/src/main/java/org/apache/axiom/om/OMXMLBuilderFactory.java` | general builders use DEFAULT; SOAP builders use SOAP | §4 reachability, §5a insecure-default case, §11 first bullet |
| `axiom-api/src/main/java/org/apache/axiom/mime/MultipartBody.java` (and siblings `PartImpl`, `Part`, `PartInputStream`) | MIME multipart parser for SOAP-with-Attachments / XOP / MTOM | §2 component-family, §6 trust table, §8 P4, §9 |
| `axiom-api/src/main/java/org/apache/axiom/locator/` | `OMMetaFactoryLocator` system-property + OSGi-service locator | §3, §5a |
| `axiom-api/src/main/java/org/apache/axiom/util/stax/dialect/` | StAX dialect detection (Woodstox, SJSXP, Stax2) | §3 item 4, §5 environment, §11a |
| `docs/design/osgi-integration.md` (per `docs/design/README.md`) | OSGi separation between API and implementation | §3 (out-of-scope context for `axiom-weaver`) |
| `docs/release-process.md` | Release-engineering process; not a security artifact | meta |
| `apidocs/` | Per-module javadoc tree; mineable but no security policy per se | §11a sources |
| `samples/`, `systests/`, `testing/` | Sample apps, regression suites | §3 item 6 (out-of-scope inventory) |
| `axiom-weaver/`, `axiom-weaver-annotations/` | Build-time bytecode generator for LLOM | §3 item 5 |
| `axiom-jakarta-activation/`, `axiom-javax-activation/`, `axiom-jakarta-jaxb/`, `axiom-javax-jaxb/`, `jakarta-bom/`, `javax-bom/` | Jakarta / Javax compatibility shims and BOMs | §2 component-family table |
| `axiom-compat/`, `axiom-legacy-attachments/` | Backwards-compat shims | §2 component-family table |
