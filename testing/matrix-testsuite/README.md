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

# Matrix Test Suite Framework

## Overview

The matrix-testsuite module provides infrastructure for building reusable,
parameterized test suites that can be applied to different implementations of the
same API. It combines JUnit 5's `@TestFactory` / `DynamicNode` mechanism with
Google Guice dependency injection to produce hierarchical, filterable test trees.

## Key concepts

### Test tree

A test suite is a tree of `MatrixTestNode` instances. Interior nodes are
**fan-out nodes** that iterate over a list of dimension values, creating one
`DynamicContainer` per value. Leaf nodes are **`MatrixTest`** instances that
instantiate and run a `junit.framework.TestCase` subclass.

### Guice injector hierarchy

Each fan-out level creates a child Guice injector that binds its value type to the
current value. By the time a leaf `MatrixTest` is reached, the accumulated injector
can satisfy all `@Inject` dependencies declared by the test case class.

```
Root Injector
  binds: implementation-level objects
  │
  ├─ Child Injector (DimensionA → value1)
  │    ├─ MatrixTest → injector.getInstance(SomeTestCase.class) → runBare()
  │    └─ MatrixTest → injector.getInstance(AnotherTestCase.class) → runBare()
  │
  └─ Child Injector (DimensionA → value2)
       ├─ MatrixTest → injector.getInstance(SomeTestCase.class) → runBare()
       └─ MatrixTest → injector.getInstance(AnotherTestCase.class) → runBare()
```

### Filtering

Parameters accumulate from the root down through the tree. At each leaf, the
accumulated parameter dictionary is checked against a
`BiPredicate<Class<?>, Dictionary<String, String>>` exclusion predicate.
Excluded tests produce an empty `Stream<DynamicNode>` and do not appear in the
test tree. `MatrixTestFilters` is a convenient implementation of this predicate
that supports LDAP-style filter expressions optionally scoped to a test class.

## Classes

### `MatrixTestNode`

Abstract base class for all nodes in the test tree. Defines a single method:

```java
abstract Stream<DynamicNode> toDynamicNodes(
        Injector parentInjector,
        Dictionary<String, String> inheritedParameters,
        BiPredicate<Class<?>, Dictionary<String, String>> excludes);
```

### `AbstractFanOutNode<T>`

Abstract fan-out node that iterates over a list of values of type `T`. For each
value, it:

1. Creates a child Guice injector binding `T` to the value.
2. Extracts test parameters (via the abstract `extractParameters` method).
3. Produces a `DynamicContainer` containing the results of recursing into its
   child nodes.

Subclasses:

- **`DimensionFanOutNode<D extends Dimension>`** — for types that implement the
  `Dimension` interface. Parameters are extracted via
  `Dimension.addTestParameters()`.

- **`ParameterFanOutNode<T>`** — for arbitrary types. The caller supplies a
  parameter name and a `Function<T, String>` to extract the parameter value.

### `MatrixTest`

Leaf node. Instantiates a `junit.framework.TestCase` subclass via Guice and
executes it through `TestCase.runBare()` (which runs the full `setUp()` →
`runTest()` → `tearDown()` lifecycle). The test is skipped if matched by the
exclusion filters.

### `InjectorNode`

A node that creates a child Guice injector from the supplied modules and threads
it through its children. Can be used at any level of the test tree to introduce
additional bindings. Provides:

```java
public Stream<DynamicNode> toDynamicNodes(BiPredicate<Class<?>, Dictionary<String, String>> excludes)
```

### `MatrixTestFilters`

Immutable set of exclusion filters. Each filter entry optionally constrains by
test class and/or an LDAP filter expression on the parameter dictionary (using
OSGi's `FrameworkUtil.createFilter()`). Built via `MatrixTestFilters.builder()`.

## Writing a test case

Test cases extend `junit.framework.TestCase` (or a domain-specific subclass) and
override `runTest()`. Dependencies are declared with `@Inject` — either on fields
or via constructor. The test case does **not** receive parameters through its
constructor and does **not** call `addTestParameter()`.

```java
public abstract class MyTestCase extends TestCase {
    @Inject protected SomeImplementation impl;
    @Inject protected SomeDimension dimension;

    // convenience methods using impl and dimension ...
}
```

```java
public class TestSomeBehavior extends MyTestCase {
    @Override
    protected void runTest() throws Throwable {
        // test logic using inherited injected fields
    }
}
```

## Defining a test suite

The test suite author creates a factory method that builds an `InjectorNode`,
adds fan-out nodes for each dimension, and registers test classes as `MatrixTest`
leaf nodes:

```java
public class MyTestSuite {
    public static InjectorNode create(SomeFactory factory) {
        SomeImplementation impl = new SomeImplementation(factory);
        InjectorNode suite = new InjectorNode(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SomeImplementation.class).toInstance(impl);
            }
        });

        ParameterFanOutNode<SomeDimension> dimensions = new ParameterFanOutNode<>(
                SomeDimension.class,
                Multiton.getInstances(SomeDimension.class),
                "dimension",
                SomeDimension::getName);
        dimensions.addChild(new MatrixTest(TestSomeBehavior.class));
        dimensions.addChild(new MatrixTest(TestOtherBehavior.class));
        suite.addChild(dimensions);

        return suite;
    }
}
```

## Consuming a test suite

Consumers create a JUnit 5 test class with a `@TestFactory` method:

```java
class MyImplTest {
    @TestFactory
    Stream<DynamicNode> tests() {
        InjectorNode suite = MyTestSuite.create(new MyFactoryImpl());
        MatrixTestFilters excludes = MatrixTestFilters.builder()
                .add(TestSomeBehavior.class, "(dimension=problematicValue)")
                .build();
        return suite.toDynamicNodes(excludes);
    }
}
```

## Legacy classes

The following classes from the old JUnit 3 based framework still exist in this
package but are deprecated and will be removed once all test suites have been
migrated:

- `MatrixTestCase`
- `MatrixTestSuiteBuilder`
