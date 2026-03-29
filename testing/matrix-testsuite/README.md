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
`DynamicContainer` per value. `ParentNode` groups multiple child nodes into a
flat sequence. Leaf nodes are **`MatrixTest`** instances that
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

Labels accumulate from the root down through the tree. At each leaf, the
accumulated label map is checked against a
`BiPredicate<Class<?>, Map<String, String>>` exclusion predicate.
Excluded tests produce an empty `Stream<DynamicNode>` and do not appear in the
test tree. `MatrixTestFilters` is a convenient implementation of this predicate
that supports LDAP-style filter expressions optionally scoped to a test class.

## Classes

### `Binding<T>`

Functional interface that configures a Guice binding for a value of type `T`.
Defines a single method `void configure(Binder binder, T value)`. Typically used
as a lambda passed to `FanOutNode`.

Provides a static helper `Binding.singleton(Key<T> key)` that returns a binding
which calls `binder.bind(key).toInstance(value)`. This covers the most common
case and avoids repetitive lambda boilerplate. Use `Key.get(MyType.class)` for
simple types, or `Key.get(Boolean.class, Names.named("flag"))` when a `@Named`
qualifier is needed.

### `MatrixTestNode`

Abstract base class for all nodes in the test tree. Defines a single method:

```java
abstract Stream<DynamicNode> toDynamicNodes(
        Injector parentInjector,
        Map<String, String> inheritedLabels,
        BiPredicate<Class<?>, Map<String, String>> excludes);
```

### `FanOutNode<T>`

Fan-out node that iterates over an `ImmutableList<T>` of values. For each
value, it:

1. Creates a child Guice injector using the supplied `Binding<T>` lambda.
2. Registers labels via the supplied `LabelBinding<? super T>`
   lambda.
3. Produces a `DynamicContainer` containing the results of recursing into its
   single child node.

Holds exactly one child node. When multiple children are needed, wrap them in a
`ParentNode`.

The `Binding<T>` lambda receives a `Binder` and the current value, and
configures the Guice binding. For the common case of binding the value as a
singleton, use `Binding.singleton(Key.get(MyType.class))`. The
`LabelBinding<? super T>` registers labels for display and filtering.
For the common case of a single label derived from the value, use one of the
`LabelBinding.simpleString` / `simpleBoolean` / `simpleInt` factory methods
(e.g. `LabelBinding.simpleString("name", MyType::getName)`).
For types implementing `Dimension`, use the predefined
`LabelBinding.DIMENSION` constant.

### `LabelBinding<T>`

Functional interface that registers labels for a fan-out value. Defines a single
method `void addLabels(Injector injector, T value, LabelTarget labels)`. Provides
several static factory methods for the common case of a single label:

| Method | Value type | Example |
|---|---|---|
| `simpleString(label, fn)` | `String` | `LabelBinding.simpleString("name", MyType::getName)` |
| `simpleBoolean(label, fn)` | `boolean` | `LabelBinding.simpleBoolean("flag", MyType::isEnabled)` |
| `simpleInt(label, fn)` | `int` | `LabelBinding.simpleInt("size", MyType::getSize)` |

Each of these also has an identity overload (no function argument) for use when
the fan-out value is already the label value:

| Method | Fan-out type | Example |
|---|---|---|
| `simpleString(label)` | `String` | `LabelBinding.simpleString("feature")` |
| `simpleBoolean(label)` | `Boolean` | `LabelBinding.simpleBoolean("cache")` |
| `simpleInt(label)` | `Integer` | `LabelBinding.simpleInt("count")` |

For types implementing `Dimension` (which contribute multiple labels), use
the predefined `LabelBinding.DIMENSION` constant.

### `MatrixTest`

Leaf node. Instantiates a `junit.framework.TestCase` subclass via Guice and
executes it through `TestCase.runBare()` (which runs the full `setUp()` →
`runTest()` → `tearDown()` lifecycle). The test is skipped if matched by the
exclusion filters.

### `InjectorNode`

A node that creates a child Guice injector from the supplied modules and threads
it through its single child. Can be used at any level of the test tree to introduce
additional bindings. Extends `MatrixTestNode` directly and holds exactly one child
node. When multiple children are needed, wrap them in a `ParentNode`. Accepts an
`ImmutableList<Module>` (primary constructor) or a single `Module` (convenience
constructor), together with a single `MatrixTestNode` child. Provides:

```java
public Stream<DynamicNode> toDynamicNodes(BiPredicate<Class<?>, Map<String, String>> excludes)
```

### `ParentNode`

A concrete node that groups a list of child nodes without injecting anything or
adding labels. The children's dynamic nodes are simply concatenated in order.
Use `ParentNode` whenever a fan-out node or `InjectorNode` needs to hold more
than one child.

### `MatrixTestFilters`

Immutable set of exclusion filters. Each filter entry optionally constrains by
test class and/or an LDAP filter expression on the label map (using
OSGi's `FrameworkUtil.createFilter()`). Built via `MatrixTestFilters.builder()`.

## Writing a test case

Test cases extend `junit.framework.TestCase` (or a domain-specific subclass) and
override `runTest()`. Dependencies are declared with `@Inject` — either on fields
or via constructor. The test case does **not** receive labels through its
constructor and does **not** call `addLabel()`.

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
    public static MatrixTestNode create(SomeFactory factory) {
        SomeImplementation impl = new SomeImplementation(factory);

        FanOutNode<SomeDimension> dimensions = new FanOutNode<>(
                Multiton.getInstances(SomeDimension.class),
                Binding.singleton(Key.get(SomeDimension.class)),
                LabelBinding.simpleString("dimension", SomeDimension::getName),
                new ParentNode(
                        new MatrixTest(TestSomeBehavior.class),
                        new MatrixTest(TestOtherBehavior.class)));

        MatrixTestNode suite = new InjectorNode(
                binder -> binder.bind(SomeImplementation.class).toInstance(impl),
                dimensions);

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
        MatrixTestNode suite = MyTestSuite.create(new MyFactoryImpl());
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
