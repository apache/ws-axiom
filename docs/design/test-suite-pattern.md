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
SAAJ API that can be executed against any `SAAJMetaFactory`, and `axiom-testsuite`
defines tests for the Axiom API that can be executed against any `OMMetaFactory`.

Most test suites also execute tests across multiple dimensions. For instance, SAAJ and
SOAP tests run against both SOAP 1.1 and SOAP 1.2, while `OMTestSuiteBuilder` multiplies
tests across XML samples, serialization strategies, builder factories, and more.

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

### Usage across the project

The same pattern is used at much larger scale in other modules:

| Module | Builder | `addTest()` calls | Estimated runtime tests |
|--------|---------|-------------------|------------------------|
| axiom-testsuite | `OMTestSuiteBuilder` | ~452 | Thousands (combinatorial) |
| axiom-testsuite | `SOAPTestSuiteBuilder` | ~197 | Hundreds |
| dom-testsuite | `DOMTestSuiteBuilder` | Many | Hundreds |
| saaj-testsuite | `SAAJTestSuiteBuilder` | 6 | 12 |

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

### saaj-testsuite (small suite)

For the saaj-testsuite specifically, migrating to JUnit 5 `@TestFactory` would:

*   Collapse 6 test case classes into methods within a single class.
*   Remove the need for `SAAJTestSuiteBuilder` entirely.
*   Replace the `SAAJTestCase` base class with a simpler abstract class.
*   Reduce the file count from 10 to approximately 3 (`SAAJImplementation`, `SAAJTests`,
    `SAAJRITests`).

The `SAAJImplementation` class (which uses reflection to access protected methods on
`SAAJMetaFactory`) would be retained as-is.

### Large suites (OMTestSuiteBuilder, SOAPTestSuiteBuilder)

The larger suites present additional considerations:

*   The one-class-per-test pattern, while verbose, keeps each test independently
    navigable and organizes tests by the API area they cover.
*   The exclusion mechanism (LDAP filters on test parameters) is heavily used by
    consumers to skip known-failing tests for specific implementations. A JUnit 5
    equivalent would need to provide comparable functionality.
*   The sheer number of test case classes (hundreds) means migration would be a
    substantial effort.
*   A phased approach is possible: a JUnit 5 adapter that wraps `MatrixTestSuiteBuilder`
    output into `DynamicTest` instances would allow consuming modules to migrate to
    JUnit 5 runners without rewriting test case classes.

### Replacement for MatrixTestSuiteBuilder: MatrixTestNode tree

Since `DynamicContainer` and `DynamicTest` are `final` in JUnit 5, they cannot be
subclassed to attach test parameters for LDAP-style filtering. Instead, a parallel
class hierarchy can act as a factory for `DynamicNode` instances while carrying the
parameters needed for exclusion filtering.

#### Class hierarchy

```java
/**
 * Base class mirroring {@link DynamicNode}. Represents a node in the test tree
 * that can be filtered before conversion to JUnit 5's dynamic test API.
 */
abstract class MatrixTestNode {
    abstract DynamicNode toDynamicNode(Dictionary<String, String> inheritedParameters,
            List<Filter> excludes);
}
```

```java
/**
 * Mirrors {@link DynamicContainer}. Represents a parameterized grouping level
 * in the test tree (e.g. a SOAP version, a serialization strategy).
 *
 * <p>Each {@code MatrixTestContainer} corresponds to a single {@link Dimension}
 * value (e.g. one particular {@code SerializationStrategy}). The {@code Dimension}
 * is stored as-is; its test parameters are extracted lazily when
 * {@link #toDynamicNode(Dictionary, List)} is called. Because a {@code Dimension}
 * may contribute multiple test parameters (for example, {@code SerializeToWriter}
 * adds both {@code serializationStrategy=Writer} and {@code cache=true}), these
 * parameters also determine the display name and are merged into the inherited
 * parameter dictionary. The full parameter dictionary for any leaf
 * {@code MatrixTestCase} is the accumulation of parameters from its ancestor
 * {@code MatrixTestContainer} chain.
 */
class MatrixTestContainer extends MatrixTestNode {
    private final Dimension dimension;
    private final List<MatrixTestNode> children = new ArrayList<>();

    MatrixTestContainer(Dimension dimension) {
        this.dimension = dimension;
    }

    void addChild(MatrixTestNode child) {
        children.add(child);
    }

    @Override
    DynamicNode toDynamicNode(Dictionary<String, String> inheritedParameters,
            List<Filter> excludes) {
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
                        .map(child -> child.toDynamicNode(params, excludes))
                        .filter(Objects::nonNull));
    }
}
```

```java
/**
 * Mirrors {@link DynamicTest}. A leaf test case with an executable body.
 * Filtering is applied based on the accumulated parameters from ancestor
 * containers plus the test class name.
 */
class MatrixTestCase extends MatrixTestNode {
    private final Class<?> testClass;
    private final Executable executable;

    MatrixTestCase(Class<?> testClass, Executable executable) {
        this.testClass = testClass;
        this.executable = executable;
    }

    @Override
    DynamicNode toDynamicNode(Dictionary<String, String> inheritedParameters,
            List<Filter> excludes) {
        for (Filter exclude : excludes) {
            if (exclude.matches(testClass, inheritedParameters)) {
                return null; // Excluded
            }
        }
        return DynamicTest.dynamicTest(testClass.getSimpleName(), executable);
    }
}
```

#### How filtering works

Each `MatrixTestContainer` level represents one `Dimension` value and carries one or
more parameters contributed by that dimension. As the tree is converted to `DynamicNode`
instances via `toDynamicNode()`, parameters accumulate from the root down:

```
MatrixTestContainer(SOAPSpec.SOAP11)                 → params: {spec=soap11}
  MatrixTestContainer(SerializeToWriter(cache=true)) → params: {spec=soap11, serializationStrategy=Writer, cache=true}
    MatrixTestCase(SerializeElement.class, ...)        → filtered against {spec=soap11, serializationStrategy=Writer, cache=true}
    MatrixTestCase(SerializeDocument.class, ...)      → filtered against {spec=soap11, serializationStrategy=Writer, cache=true}
```

Note that `SerializeToWriter` contributes two parameters (`serializationStrategy` and
`cache`) to a single container level, matching the existing `Dimension` contract where
`addTestParameters()` may call `addTestParameter()` multiple times.

Consumers apply exclusions exactly as they do today:

```java
class OMImplementationTests {
    @TestFactory
    Stream<DynamicNode> omTests() {
        MatrixTestContainer root = new OMTestTreeBuilder(metaFactory).build();
        List<Filter> excludes = new ArrayList<>();
        excludes.add(Filter.forClass(TestSerialize.class, "(spec=soap12)"));
        return root.toDynamicNode(new Hashtable<>(), excludes)
                .children(); // unwrap the root container
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

### Hybrid approach: JUnit 5 adapter for MatrixTestSuiteBuilder

A pragmatic intermediate step would be to create a JUnit 5 adapter that converts a
`MatrixTestSuiteBuilder` into a `@TestFactory` method, allowing consumers to use JUnit 5
without rewriting the test suites themselves:

```java
public abstract class MatrixTestFactory {
    protected abstract MatrixTestSuiteBuilder createBuilder();

    @TestFactory
    Stream<DynamicTest> tests() {
        TestSuite suite = createBuilder().build();
        return Collections.list(suite.tests()).stream()
                .map(test -> DynamicTest.dynamicTest(test.toString(), () -> {
                    TestResult result = new TestResult();
                    test.run(result);
                    if (result.failureCount() > 0) {
                        throw (Throwable) result.failures().nextElement()
                                .thrownException();
                    }
                }));
    }
}
```

This would allow the project to migrate runners (consuming modules) to JUnit 5
incrementally while preserving the existing test suite infrastructure.
