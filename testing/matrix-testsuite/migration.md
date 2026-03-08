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

# Migration guide: MatrixTestSuiteBuilder → MatrixTestNode

This document describes how to migrate a test suite from the old
`MatrixTestSuiteBuilder` / `MatrixTestCase` pattern (JUnit 3) to the new
`MatrixTestNode` pattern (JUnit 5 + Guice).

There are two common shapes:

- **Reusable API test suites** — the test suite is defined in one module and
  consumed by one or more implementation modules. These use `InjectorNode` at
  the root to bind implementation-level objects. See the `saaj-testsuite`
  module for a completed example.
- **Self-contained test suites** — the test case, suite structure, and consumer
  live in a single class. These typically don't need `InjectorNode` at all;
  fan-out nodes with `MatrixTest` leaves are sufficient. See
  `StAXPivotTransformerTest` in `components/core-streams` for an example.

The step-by-step guide below focuses on reusable API test suites. For
self-contained suites, see the [simplified migration](#simplified-migration-for-self-contained-tests)
section at the end.

## Prerequisites

The module being migrated must depend on:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
</dependency>
<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
</dependency>
```

These are already declared in the root POM's `<dependencyManagement>`.

## Step-by-step migration

### 1. Update the test case base class

The domain-specific base class (e.g. `SAAJTestCase`) currently:

- Extends `MatrixTestCase`
- Accepts all dimension values and implementation objects via constructor
  parameters
- Calls `addTestParameter(name, value)` in the constructor

**Change it to:**

- Extend `junit.framework.TestCase` directly
- Declare dependencies as `@Inject` fields (using `com.google.inject.Inject`)
- Remove the constructor entirely (or make it no-arg)
- Remove all `addTestParameter()` calls

**Before:**

```java
public abstract class SAAJTestCase extends MatrixTestCase {
    protected final SAAJImplementation saajImplementation;
    protected final SOAPSpec spec;

    public SAAJTestCase(SAAJImplementation saajImplementation, SOAPSpec spec) {
        this.saajImplementation = saajImplementation;
        this.spec = spec;
        addTestParameter("spec", spec.getName());
    }

    protected final MessageFactory newMessageFactory() throws SOAPException {
        return spec.getAdapter(FactorySelector.class).newMessageFactory(saajImplementation);
    }
}
```

**After:**

```java
public abstract class SAAJTestCase extends TestCase {
    @Inject protected SAAJImplementation saajImplementation;
    @Inject @Named("spec") protected SOAPSpec spec;

    protected final MessageFactory newMessageFactory() throws SOAPException {
        return spec.getAdapter(FactorySelector.class).newMessageFactory(saajImplementation);
    }
}
```

Note: the `@Named("spec")` annotation is required because `ParameterFanOutNode`
binds values with `@Named` using the parameter name. Use `com.google.inject.name.Named`.

### 2. Update each test case class

Each test case class currently accepts constructor parameters and forwards them
to the base class.

**Remove the constructor.** The `runTest()` method stays unchanged. Any imports
of the implementation class and dimension types that were only used in the
constructor can be removed.

**Before:**

```java
public class TestAddChildElementReification extends SAAJTestCase {
    public TestAddChildElementReification(SAAJImplementation saajImplementation, SOAPSpec spec) {
        super(saajImplementation, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        // ... test logic unchanged ...
    }
}
```

**After:**

```java
public class TestAddChildElementReification extends SAAJTestCase {
    @Override
    protected void runTest() throws Throwable {
        // ... test logic unchanged ...
    }
}
```

### 3. Replace the suite builder with a suite factory

The old `*TestSuiteBuilder` class extends `MatrixTestSuiteBuilder` and overrides
`addTests()` to register test instances for each dimension combination.

**Replace it** with a class that has a static factory method returning an
`InjectorNode`. The factory method:

1. Creates an `InjectorNode` with a Guice module that binds
   implementation-level objects. Pass a single `Module` directly (convenience
   constructor) or an `ImmutableList<Module>` when you need multiple modules.
   Child nodes are supplied via an `ImmutableList<MatrixTestNode>` parameter,
   or a single `MatrixTestNode` directly (convenience constructor).
2. Creates fan-out nodes for each dimension.
3. Adds `MatrixTest` leaf nodes as children of the fan-out nodes at construction
   time.

Use `ParameterFanOutNode` for types that don't implement `Dimension` (supplying a
parameter name and a function to extract the display value); the value is bound
with `@Named(parameterName)`, so injection sites must use
`@Inject @Named("...")`. Use `DimensionFanOutNode` for types that implement
`Dimension` (plain unannotated binding). Both fan-out nodes also accept a single
`MatrixTestNode` child directly (convenience constructor) instead of an
`ImmutableList<MatrixTestNode>`.

**Before:**

```java
public class SAAJTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private final SAAJImplementation saajImplementation;

    public SAAJTestSuiteBuilder(SAAJMetaFactory metaFactory) {
        saajImplementation = new SAAJImplementation(metaFactory);
    }

    @Override
    protected void addTests() {
        addTests(SOAPSpec.SOAP11);
        addTests(SOAPSpec.SOAP12);
    }

    private void addTests(SOAPSpec spec) {
        addTest(new TestAddChildElementReification(saajImplementation, spec));
        addTest(new TestGetOwnerDocument(saajImplementation, spec));
        // ...
    }
}
```

**After:**

```java
public class SAAJTestSuite {
    public static InjectorNode create(SAAJMetaFactory metaFactory) {
        return new InjectorNode(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(SAAJImplementation.class)
                                .toInstance(new SAAJImplementation(metaFactory));
                    }
                },
                new ParameterFanOutNode<>(
                        SOAPSpec.class,
                        Multiton.getInstances(SOAPSpec.class),
                        "spec",
                        SOAPSpec::getName,
                        ImmutableList.of(
                                new MatrixTest(TestAddChildElementReification.class),
                                new MatrixTest(TestGetOwnerDocument.class))));
    }
}
```

Key differences:

- Test classes are registered **once** as `MatrixTest` instances, passed as
  children to the appropriate fan-out node at construction time, rather than once
  per dimension combination.
- Dimension values are listed via `Multiton.getInstances()` (or an explicit list)
  in the fan-out node, not iterated manually.
- No constructor arguments are passed to test classes.

### 4. Replace the consumer test class

The old consumer class uses JUnit 3's `static suite()` method.

**Replace it** with a JUnit 5 class that has a `@TestFactory` method returning
`Stream<DynamicNode>`. By convention, the method should be called `tests`.

**Before:**

```java
public class SAAJRITest extends TestCase {
    public static TestSuite suite() throws Exception {
        return new SAAJTestSuiteBuilder(new SAAJMetaFactoryImpl()).build();
    }
}
```

**After:**

```java
public class SAAJRITest {
    @TestFactory
    public Stream<DynamicNode> tests() {
        return SAAJTestSuite.create(new SAAJMetaFactoryImpl())
                .toDynamicNodes();
    }
}
```

### 5. Migrate exclusions

If the old consumer called `exclude()` on the builder, convert those calls to
`MatrixTestFilters.builder().add(...)` entries.

**Before:**

```java
SAAJTestSuiteBuilder builder = new SAAJTestSuiteBuilder(factory);
builder.exclude(TestGetOwnerDocument.class, "(spec=soap12)");
builder.exclude(TestSomething.class);
builder.exclude("(parser=StAX)");
return builder.build();
```

**After:**

```java
MatrixTestFilters excludes = MatrixTestFilters.builder()
        .add(TestGetOwnerDocument.class, "(spec=soap12)")
        .add(TestSomething.class)
        .add("(parser=StAX)")
        .build();
return MyTestSuite.create(factory).toDynamicNodes(excludes);
```

The filter syntax and semantics are identical.

### 6. Update dependencies in pom.xml

Add `junit-jupiter` and `guice` to the module's `<dependencies>`. If the module
uses `Multiton.getInstances()`, also add a dependency on the `multiton` module.

Remove any dependency on `junit:junit` if no code in the module still uses JUnit 3
or 4 APIs directly. (Note: test case classes still extend
`junit.framework.TestCase`, which comes from `junit:junit` transitively through
`matrix-testsuite`.)

### 7. Delete the old builder class

The old `*TestSuiteBuilder` class can be deleted once the new `*TestSuite` factory
is in place and all consumers have been updated.

## Simplified migration for self-contained tests

When the test case, suite builder, and consumer are all in a single class (i.e.
the class extends `MatrixTestCase` and has a `static suite()` method), the
migration is simpler because there is no separate base class or suite factory:

1. Change the class to extend `TestCase` directly and declare dimension values
   as `@Inject` fields instead of constructor parameters.
2. Remove the constructor and all `addTestParameter()` calls.
3. Replace the `static suite()` method with a `@TestFactory` method called
   `tests` that builds the fan-out tree directly and calls `toDynamicNodes()`
   on the root node.
   No `InjectorNode` is needed unless you have additional bindings beyond the
   dimension values.

**Before:**

```java
public class StAXPivotTransformerTest extends MatrixTestCase {
    private final XSLTImplementation xsltImplementation;
    private final XMLSample sample;

    public StAXPivotTransformerTest(
            XSLTImplementation xsltImplementation, XMLSample sample) {
        this.xsltImplementation = xsltImplementation;
        this.sample = sample;
        addTestParameter("xslt", xsltImplementation.getName());
        addTestParameter("sample", sample.getName());
    }

    @Override
    protected void runTest() throws Throwable {
        // ... test logic ...
    }

    public static TestSuite suite() {
        return new MatrixTestSuiteBuilder() {
            @Override
            protected void addTests() {
                for (XSLTImplementation xsltImplementation :
                        getInstances(XSLTImplementation.class)) {
                    for (XMLSample sample : getInstances(XMLSample.class)) {
                        addTest(new StAXPivotTransformerTest(xsltImplementation, sample));
                    }
                }
            }
        }.build();
    }
}
```

**After:**

```java
public class StAXPivotTransformerTest extends TestCase {
    @Inject @Named("xslt") private XSLTImplementation xsltImplementation;
    @Inject @Named("sample") private XMLSample sample;

    @Override
    protected void runTest() throws Throwable {
        // ... test logic unchanged ...
    }

    @TestFactory
    public static Stream<DynamicNode> tests() {
        return new ParameterFanOutNode<>(
                XSLTImplementation.class,
                Multiton.getInstances(XSLTImplementation.class),
                "xslt",
                XSLTImplementation::getName,
                new ParameterFanOutNode<>(
                        XMLSample.class,
                        Multiton.getInstances(XMLSample.class),
                        "sample",
                        XMLSample::getName,
                        new MatrixTest(StAXPivotTransformerTest.class)))
                .toDynamicNodes();
    }
}
```

Note that filtering logic (e.g. skipping values based on a condition like
`xsltImplementation.supportsStAXSource()`) that was previously expressed as
`if` guards in the `addTests()` loop should be handled differently — for
example by filtering the list of instances passed to the fan-out node.

## Checklist

### Reusable API test suites

- [ ] Base test case class: extends `TestCase`, uses `@Inject` fields, no
      constructor
- [ ] All test case classes: constructor removed, `runTest()` unchanged
- [ ] Suite factory class: creates `InjectorNode` with Guice module, builds
      immutable fan-out tree with `MatrixTest` leaves supplied at construction
      time
- [ ] Consumer test class: uses `@TestFactory` returning `Stream<DynamicNode>`
- [ ] Exclusions: converted to `MatrixTestFilters.builder()` calls
- [ ] `pom.xml`: `junit-jupiter`, `guice`, and (if needed) `multiton` added
- [ ] Old builder class deleted
- [ ] Tests pass: `mvn clean test -pl <module> -am`

### Self-contained test suites

- [ ] Test class: extends `TestCase`, uses `@Inject` fields, no constructor
- [ ] `static suite()` replaced with `@TestFactory` method `tests()` building
      fan-out tree and calling `toDynamicNodes()`
- [ ] `pom.xml`: `junit-jupiter`, `guice`, and (if needed) `multiton` added
- [ ] Tests pass: `mvn clean test -pl <module> -am`
