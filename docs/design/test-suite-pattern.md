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

Reusable test suites and parameterization
=========================================

## Introduction

The Axiom project provides reusable API test suites that can be applied to different
implementations of the same API. For example, `saaj-testsuite` defines tests for the
SAAJ API that can be executed against any `SAAJMetaFactory`.

Test suites also execute tests across multiple dimensions. For instance, SAAJ tests
run against both SOAP 1.1 and SOAP 1.2.

This document examines the current pattern used to implement these test suites and
evaluates whether JUnit 5's `@TestFactory` mechanism offers a better approach.

## Current pattern: MatrixTestSuiteBuilder (JUnit 3)

### Infrastructure classes

The current pattern is built on the following custom infrastructure in the `testutils`
module:

*   **`MatrixTestCase`** — extends `junit.framework.TestCase`. Each test case is a
    separate class that overrides `runTest()`. Test parameters (e.g. SOAP version) are
    added via `addTestParameter(name, value)`, which appends `[name=value]` to the test
    name for display purposes.

*   **`MatrixTestSuiteBuilder`** — builds a `junit.framework.TestSuite` by collecting
    `MatrixTestCase` instances via `addTest()` calls in the abstract `addTests()` method.
    Supports exclusions using LDAP-style filters on test parameters.

### How it works in saaj-testsuite

The saaj-testsuite uses this pattern as follows:

1.  Each test case is a separate class extending `SAAJTestCase` (which extends
    `MatrixTestCase`). For example, `TestAddChildElementReification`,
    `TestExamineMustUnderstandHeaderElements`, etc.

2.  `SAAJTestSuiteBuilder` extends `MatrixTestSuiteBuilder`. Its `addTests()` method
    calls a private `addTests(SOAPSpec)` helper for both `SOAPSpec.SOAP11` and
    `SOAPSpec.SOAP12`, instantiating each test case class with the SAAJ implementation
    and the SOAP spec.

3.  `SAAJTestCase` provides convenience methods `newMessageFactory()` and
    `newSOAPFactory()` that create the appropriate factory for the current SOAP version.

4.  Consumers create a JUnit 3 runner class with a `static suite()` method:

    ```java
    public class SAAJRITest extends TestCase {
        public static TestSuite suite() throws Exception {
            return new SAAJTestSuiteBuilder(new SAAJMetaFactoryImpl()).build();
        }
    }
    ```

### File inventory for saaj-testsuite

For 6 test cases × 2 SOAP versions = 12 test instances, the main files are:

| File | Role |
|------|------|
| `SAAJTestCase.java` | Abstract base class for all SAAJ tests |
| `SAAJTestSuiteBuilder.java` | Suite builder; registers all tests × SOAP versions |
| `SAAJImplementation.java` | Wraps `SAAJMetaFactory` with reflective access |
| `TestAddChildElementReification.java` | Test case class |
| `TestAddChildElementLocalName.java` | Test case class |
| `TestAddChildElementLocalNamePrefixAndURI.java` | Test case class |
| `TestSetParentElement.java` | Test case class |
| `TestGetOwnerDocument.java` | Test case class |
| `TestExamineMustUnderstandHeaderElements.java` | Test case class |
| `SAAJRITest.java` | JUnit 3 runner for the reference implementation |

## Alternative: JUnit 5 @TestFactory + DynamicTest

JUnit 5 provides a built-in mechanism for dynamic test generation that directly
addresses the same use case.

### Key JUnit 5 features

*   **`@TestFactory`** — a method that returns a `Stream<DynamicNode>` (or `Collection`,
    `Iterable`, etc.). Each `DynamicNode` becomes a test in the test tree.

*   **`DynamicContainer`** — groups `DynamicNode` instances under a named container,
    enabling hierarchical test organization (e.g. grouping by SOAP version).

*   **`DynamicTest`** — a named test with an `Executable` body. Replaces the need for
    a separate class per test case.

*   **`@ParameterizedTest`** + `@MethodSource` — an alternative for simpler
    parameterization where each test method receives parameters directly.

### What saaj-testsuite would look like

The reusable test suite module would define an abstract class:

```java
public abstract class SAAJTests {
    private final SAAJImplementation impl;

    protected SAAJTests(SAAJMetaFactory metaFactory) {
        this.impl = new SAAJImplementation(metaFactory);
    }

    @TestFactory
    Stream<DynamicContainer> saajTests() {
        return Multiton.getInstances(SOAPSpec.class).stream().map(spec ->
            DynamicContainer.dynamicContainer(spec.getName(), Stream.of(
                testAddChildElementReification(spec),
                testExamineMustUnderstandHeaderElements(spec),
                testAddChildElementLocalName(spec),
                testAddChildElementLocalNamePrefixAndURI(spec),
                testSetParentElement(spec),
                testGetOwnerDocument(spec)
            ))
        );
    }

    private DynamicTest testAddChildElementReification(SOAPSpec spec) {
        return DynamicTest.dynamicTest("addChildElementReification", () -> {
            MessageFactory mf = spec.getAdapter(FactorySelector.class)
                    .newMessageFactory(impl);
            SOAPBody body = mf.createMessage().getSOAPBody();
            SOAPElement child = body.addChildElement(
                    (SOAPElement) body.getOwnerDocument().createElementNS("urn:test", "p:test"));
            assertThat(child).isInstanceOf(SOAPBodyElement.class);
        });
    }

    // ... other test methods ...
}
```

Consumers would subclass per implementation:

```java
class SAAJRITests extends SAAJTests {
    SAAJRITests() {
        super(new SAAJMetaFactoryImpl());
    }
}
```

### Comparison

| Concern | MatrixTestSuiteBuilder (JUnit 3) | JUnit 5 @TestFactory |
|---------|----------------------------------|----------------------|
| Framework version | JUnit 3 | JUnit 5 (Jupiter) |
| Test registration | Explicit `addTest()` in builder | Return `Stream<DynamicNode>` |
| One class per test case | Required | Not required — tests are methods returning `DynamicTest` |
| Boilerplate for saaj-testsuite | 10 files | 2–3 files |
| Test tree in IDE | Flat list with `[spec=SOAP11]` in name | Nested: SOAP11 > testName, SOAP12 > testName |
| Exclusion mechanism | LDAP filter on parameter dictionary | LDAP filter on `MatrixTestNode` tree (see below) |
| Reusability across implementations | Subclass `TestCase` + pass factory to builder | Subclass base test class + pass factory to constructor |
| Custom infrastructure needed | `MatrixTestSuiteBuilder`, `MatrixTestCase` | None (built into JUnit 5) |

## Considerations for migration

### saaj-testsuite

For the saaj-testsuite, migrating to JUnit 5 `@TestFactory` would:

*   Collapse 6 test case classes into methods within a single class.
*   Remove the need for `SAAJTestSuiteBuilder` entirely.
*   Replace the `SAAJTestCase` base class with a simpler abstract class.
*   Reduce the file count from 10 to approximately 3 (`SAAJImplementation`, `SAAJTests`,
    `SAAJRITests`).

The `SAAJImplementation` class (which uses reflection to access protected methods on
`SAAJMetaFactory`) would be retained as-is.

### Replacement for MatrixTestSuiteBuilder: MatrixTestNode tree with Guice

Since `DynamicContainer` and `DynamicTest` are `final` in JUnit 5, they cannot be
subclassed to attach test parameters for LDAP-style filtering. Instead, a parallel
class hierarchy acts as a factory for `DynamicNode` instances while carrying the
parameters needed for exclusion filtering.

Tests continue to be structured as one test case per class extending
`junit.framework.TestCase`, with each class overriding `runTest()`. Rather than
receiving test parameters via constructor arguments, test cases declare their
dependencies using `@Inject` annotations and are instantiated by Google Guice.
`MatrixTestContainer` builds a Guice injector hierarchy — one child injector per
dimension value — so that by the time a leaf `MatrixTest` is reached, the
accumulated injector can satisfy all `@Inject` dependencies for the test case class.

#### Class hierarchy

```java
/**
 * Base class mirroring {@link DynamicNode}. Represents a node in the test tree
 * that can be filtered before conversion to JUnit 5's dynamic test API.
 *
 * <p>The {@code parentInjector} parameter threads through the tree: each
 * {@link MatrixTestContainer} creates child injectors from it, and each
 * {@link MatrixTest} uses it to instantiate the test class.
 */
public abstract class MatrixTestNode {
    abstract Stream<DynamicNode> toDynamicNodes(Injector parentInjector,
            Dictionary<String, String> inheritedParameters,
            List<Filter> excludes);
}
```

```java
/**
 * Mirrors {@link DynamicContainer}. Represents a parameterized grouping level
 * in the test tree (e.g. a SOAP version).
 *
 * <p>A {@code MatrixTestContainer} holds a list of {@link Dimension} instances
 * of a given type (e.g. all {@code SOAPSpec} values). When
 * {@link #toDynamicNodes} is called, it produces one {@link DynamicContainer}
 * per dimension instance. For each dimension instance, a <em>child Guice
 * injector</em> is created from the parent injector, binding the dimension type
 * to that specific instance. This child injector is then propagated to all
 * children, forming a hierarchy where each path from root to leaf accumulates
 * all dimension bindings.
 *
 * <p>The test parameters extracted from each dimension determine both the
 * display name and the filter dictionary. The full parameter dictionary for any
 * leaf {@code MatrixTest} is the accumulation of parameters from its ancestor
 * {@code MatrixTestContainer} chain.
 *
 * @param <D> the dimension type
 */
public class MatrixTestContainer<D extends Dimension> extends MatrixTestNode {
    private final Class<D> dimensionType;
    private final List<D> dimensions;
    private final List<MatrixTestNode> children = new ArrayList<>();

    public MatrixTestContainer(Class<D> dimensionType, List<D> dimensions) {
        this.dimensionType = dimensionType;
        this.dimensions = dimensions;
    }

    public void addChild(MatrixTestNode child) {
        children.add(child);
    }

    @Override
    Stream<DynamicNode> toDynamicNodes(Injector parentInjector,
            Dictionary<String, String> inheritedParameters,
            List<Filter> excludes) {
        return dimensions.stream().map(dimension -> {
            // Create a child injector that binds this dimension value.
            Injector childInjector = parentInjector.createChildInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(dimensionType).toInstance(dimension);
                }
            });

            // Extract test parameters from the dimension for display and filtering.
            Map<String, String> parameters = new LinkedHashMap<>();
            dimension.addTestParameters(new TestParameterTarget() {
                @Override
                public void addTestParameter(String name, String value) {
                    parameters.put(name, value);
                }

                @Override
                public void addTestParameter(String name, boolean value) {
                    addTestParameter(name, String.valueOf(value));
                }

                @Override
                public void addTestParameter(String name, int value) {
                    addTestParameter(name, String.valueOf(value));
                }
            });
            Hashtable<String, String> params = new Hashtable<>();
            // Copy inherited parameters from ancestor containers
            for (Enumeration<String> e = inheritedParameters.keys(); e.hasMoreElements(); ) {
                String key = e.nextElement();
                params.put(key, inheritedParameters.get(key));
            }
            parameters.forEach(params::put);
            String displayName = parameters.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining(", "));
            return DynamicContainer.dynamicContainer(displayName,
                    children.stream()
                            .flatMap(child -> child.toDynamicNodes(childInjector, params, excludes)));
        });
    }
}
```

```java
/**
 * Mirrors {@link DynamicTest}. A leaf node that instantiates a
 * {@link junit.framework.TestCase} subclass via Guice and executes it.
 *
 * <p>The test class must have an injectable constructor (either a no-arg
 * constructor or one annotated with {@code @Inject}). Field injection is
 * also supported. The injector received from the ancestor
 * {@code MatrixTestContainer} chain will have bindings for all dimension
 * types, plus any implementation-level bindings from the root injector
 * (e.g. {@code SAAJImplementation}).
 *
 * <p>Once the instance is created, it is executed via {@link TestCase#runBare()},
 * which invokes the full {@code setUp()} → {@code runTest()} → {@code tearDown()}
 * lifecycle.
 */
public class MatrixTest extends MatrixTestNode {
    private final Class<? extends TestCase> testClass;

    public MatrixTest(Class<? extends TestCase> testClass) {
        this.testClass = testClass;
    }

    @Override
    Stream<DynamicNode> toDynamicNodes(Injector injector,
            Dictionary<String, String> inheritedParameters,
            List<Filter> excludes) {
        for (Filter exclude : excludes) {
            if (exclude.matches(testClass, inheritedParameters)) {
                return Stream.empty(); // Excluded
            }
        }
        return Stream.of(DynamicTest.dynamicTest(testClass.getSimpleName(), () -> {
            TestCase testInstance = injector.getInstance(testClass);
            testInstance.setName(testClass.getSimpleName());
            testInstance.runBare();
        }));
    }
}
```

```java
/**
 * Root of a test suite. Owns the Guice root injector and the tree of
 * {@link MatrixTestNode} instances. Provides a {@link #toDynamicNodes(List)}
 * method that converts the tree to JUnit 5 dynamic nodes, applying the
 * supplied exclusion filters.
 *
 * <p>Exclusion filters are <em>not</em> owned by the suite itself because
 * they are specific to each consumer (implementation under test), whereas
 * the suite structure and bindings are defined by the test suite author.
 */
public class MatrixTestSuite {
    private final Injector rootInjector;
    private final List<MatrixTestNode> children = new ArrayList<>();

    public MatrixTestSuite(Module... modules) {
        this.rootInjector = Guice.createInjector(modules);
    }

    public void addChild(MatrixTestNode child) {
        children.add(child);
    }

    public Stream<DynamicNode> toDynamicNodes(List<Filter> excludes) {
        return children.stream()
                .flatMap(child -> child.toDynamicNodes(
                        rootInjector, new Hashtable<>(), excludes));
    }
}
```

#### Guice injector hierarchy

The injector hierarchy mirrors the `MatrixTestContainer` nesting. The root injector
is created by the consumer and binds implementation-level objects. Each
`MatrixTestContainer` level creates one child injector per dimension value, binding
the dimension type. By the time a leaf `MatrixTest` is reached, the injector
can satisfy all `@Inject` dependencies.

```
Root Injector
  binds: SAAJImplementation → instance
  │
  ├─ Child Injector (SOAPSpec → SOAP11)
  │    │
  │    ├─ MatrixTest → injector.getInstance(TestAddChildElementReification.class)
  │    │               → testInstance.runBare()
  │    └─ MatrixTest → injector.getInstance(TestGetOwnerDocument.class)
  │                    → testInstance.runBare()
  │
  └─ Child Injector (SOAPSpec → SOAP12)
       │
       ├─ MatrixTest → injector.getInstance(TestAddChildElementReification.class)
       │               → testInstance.runBare()
       └─ MatrixTest → injector.getInstance(TestGetOwnerDocument.class)
                           → testInstance.runBare()
```

#### What test case classes look like

Test case classes continue to extend `junit.framework.TestCase` (or a domain-specific
subclass) and override `runTest()`. The key difference is that dependencies are injected
by Guice rather than passed via constructor arguments.

**Before (constructor parameters):**

```java
public class TestAddChildElementReification extends SAAJTestCase {
    public TestAddChildElementReification(SAAJImplementation saajImplementation, SOAPSpec spec) {
        super(saajImplementation, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPBody body = newMessageFactory().createMessage().getSOAPBody();
        SOAPElement child = body.addChildElement(
                (SOAPElement) body.getOwnerDocument().createElementNS("urn:test", "p:test"));
        assertThat(child).isInstanceOf(SOAPBodyElement.class);
    }
}
```

**After (Guice injection):**

```java
public class TestAddChildElementReification extends SAAJTestCase {
    @Override
    protected void runTest() throws Throwable {
        SOAPBody body = newMessageFactory().createMessage().getSOAPBody();
        SOAPElement child = body.addChildElement(
                (SOAPElement) body.getOwnerDocument().createElementNS("urn:test", "p:test"));
        assertThat(child).isInstanceOf(SOAPBodyElement.class);
    }
}
```

The intermediate base class `SAAJTestCase` uses `@Inject` for its dependencies:

```java
public abstract class SAAJTestCase extends TestCase {
    @Inject protected SAAJImplementation saajImplementation;
    @Inject protected SOAPSpec spec;

    protected final MessageFactory newMessageFactory() throws SOAPException {
        return spec.getAdapter(FactorySelector.class).newMessageFactory(saajImplementation);
    }

    protected final SOAPFactory newSOAPFactory() throws SOAPException {
        return spec.getAdapter(FactorySelector.class).newSOAPFactory(saajImplementation);
    }
}
```

Note that the existing `Dimension.addTestParameters()` mechanism is **not** used by test
case classes at all. Parameters are extracted only by `MatrixTestContainer` for display
names and filter matching. Test cases interact with dimensions purely as typed objects
obtained through injection.

#### How filtering works

Each `MatrixTestContainer` level holds a list of `Dimension` instances (e.g. all
`SOAPSpec` values). When `toDynamicNodes()` is called, each container produces one
`DynamicContainer` per dimension instance, and parameters accumulate from the root down:

```
MatrixTestContainer(SOAPSpec.class, [SOAPSpec.SOAP11, SOAPSpec.SOAP12])
  MatrixTest(TestAddChildElementReification.class)
  MatrixTest(TestGetOwnerDocument.class)

→ spec=soap11                          [child injector binds SOAPSpec]
    → TestAddChildElementReification   (filtered against {spec=soap11})
    → TestGetOwnerDocument             (filtered against {spec=soap11})
  spec=soap12
    → TestAddChildElementReification   (filtered against {spec=soap12})
    → TestGetOwnerDocument             (filtered against {spec=soap12})
```

Consumers build a `MatrixTestSuite` and return its dynamic nodes:

```java
class SAAJRITests {
    @TestFactory
    Stream<DynamicNode> saajTests() {
        SAAJImplementation impl = new SAAJImplementation(new SAAJMetaFactoryImpl());
        MatrixTestSuite suite = new MatrixTestSuite(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SAAJImplementation.class).toInstance(impl);
            }
        });

        MatrixTestContainer<SOAPSpec> specs = new MatrixTestContainer<>(
                SOAPSpec.class, Multiton.getInstances(SOAPSpec.class));
        specs.addChild(new MatrixTest(TestAddChildElementReification.class));
        specs.addChild(new MatrixTest(TestExamineMustUnderstandHeaderElements.class));
        specs.addChild(new MatrixTest(TestAddChildElementLocalName.class));
        specs.addChild(new MatrixTest(TestAddChildElementLocalNamePrefixAndURI.class));
        specs.addChild(new MatrixTest(TestSetParentElement.class));
        specs.addChild(new MatrixTest(TestGetOwnerDocument.class));
        suite.addChild(specs);

        List<Filter> excludes = new ArrayList<>();
        excludes.add(Filter.forClass(TestGetOwnerDocument.class, "(spec=soap12)"));
        return suite.toDynamicNodes(excludes);
    }
}
```

#### Benefits over MatrixTestSuiteBuilder

*   Produces a hierarchical test tree in the IDE (grouped by dimension) instead of a
    flat list with parameter suffixes in the test name.
*   Parameters are distributed across the tree (one `Dimension` per container level,
    possibly contributing multiple parameters) rather than accumulated on each leaf
    test case, making the structure explicit.
*   Uses standard JUnit 5 `DynamicNode` for execution while keeping the filtering
    infrastructure in the intermediate `MatrixTestNode` layer.
*   The LDAP-style filter mechanism is preserved unchanged.
*   **Guice injection decouples test cases from the tree structure.** Test cases declare
    what they need (`@Inject SOAPSpec spec`) without knowing how or where in the tree
    hierarchy that binding is provided. Adding a new dimension to the tree does not
    require changing test case constructors.
*   **No boilerplate parameter passing in builders.** The current pattern requires each
    `addTest()` call to manually pass all dimension values to the test constructor.
    With Guice, `MatrixTest` only needs the test class; the injector supplies
    everything.
*   **Test case base classes become simpler.** `SAAJTestCase` no longer needs
    constructor parameters or chains of `super(...)` calls — it simply declares
    `@Inject` fields.
