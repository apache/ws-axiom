# Consolidate Matrix Tests

## Purpose

This skill describes how to consolidate multiple single-method matrix test classes (each
implementing `Executable`) into a single multi-method class annotated with
`@org.apache.axiom.testutils.suite.Test` methods. Use this when several closely-related test
classes in the same package share the same injected dependencies and logically belong together
(e.g., they all test the same DOM node type or API surface).

## When to consolidate

Consolidation requires that:

- Multiple `Executable` test classes live in the same package.
- All the classes being consolidated are wrapped in `MatrixTest` nodes under the same parent
  node. This (in general, but not always) means they inject the same set of fields.

## How MatrixTestContainer handles multi-method classes

`MatrixTestContainer` is a dedicated leaf node for multi-method test classes. It:

- Scans the class for methods annotated with `@org.apache.axiom.testutils.suite.Test`.
- Sorts them alphabetically for reproducibility.
- Evaluates exclusion filters **per method**: a label `"test"` set to the method name is added
  to the inherited label map before testing. Methods that match the exclusion predicate are
  omitted; if all are excluded the node produces nothing.
- Produces a `DynamicContainer` named after the class, with one `DynamicTest` per remaining method.
- Creates a fresh Guice-injected instance for each method invocation.

**Important:** use `@org.apache.axiom.testutils.suite.Test`, **not** JUnit 5's
`@org.junit.jupiter.api.Test`. The custom annotation has no JUnit meta-annotations, so
Surefire and the Jupiter engine will not discover and run the class directly as a standalone
JUnit test.

Individual methods can be excluded using the `"test"` label, for example:

```java
filters.add(SomeBehaviorTests.class, "(test=methodToSkip)")
```

## Step-by-step consolidation process

### 1. Identify the target group

Find all `Executable` test classes in a package that share the same parent node in the test
tree. Example: a `documentfragment` package containing `TestCloneNodeDeep`,
`TestCloneNodeShallow`, `TestLookupNamespaceURI`, `TestLookupPrefix`.

### 2. Migrate any existing filters

Search across the entire codebase for `MatrixTestFilters` usages that reference any of the
target classes. If any consumer excludes one of the old classes individually, migrate the filter
to use the new consolidated class plus the `"test"` label for the corresponding method:

```java
// Before (old class-level exclusion)
filters.add(TestCloneNodeDeep.class)

// After (new per-method exclusion on the consolidated class)
filters.add(DocumentFragmentTests.class, "(test=cloneNodeDeep)")
```

### 3. Create the consolidated test class

Create a new class in the same package. Follow this naming convention:
- Use the shared noun/area as the class name prefix, e.g., `DocumentFragmentTests`.
- The class name should end with `Tests` (plural) to distinguish it from single-method
  `Executable` test classes which typically end with no suffix or a singular noun.

Structure of the new class:

```java
import org.apache.axiom.testutils.suite.Test;

public class SomeTypeTests {
    // Declare only the fields that were @Inject-ed in the old classes
    @Inject
    private SomeDependency dep;

    @Test
    public void descriptiveMethodName() throws Throwable {
        // body from the old execute() method
    }

    // ... one @Test method per old class
}
```

Method naming conventions:
- Drop the `Test` prefix from the old class name and lowercase the first letter.
  - `TestCloneNodeDeep` → `cloneNodeDeep()`
  - `TestLookupNamespaceURI` → `lookupNamespaceURI()`
- Methods should be `public void` and may declare `throws Throwable`.
- If the old class had a Javadoc comment, copy it verbatim onto the corresponding method. Do
  **not** generate new Javadoc comments where the original class had none.

**Code preservation:** Copy the body of each `execute()` method exactly as-is into the
corresponding `@Test` method. Do **not** refactor, reformat, rename variables, or make any
other improvements unrelated to the consolidation itself.

### 4. Update the test suite registration

In the suite factory class (e.g., `DOMTestSuite`), replace the N individual `MatrixTest`
entries with a single `MatrixTestContainer` entry:

```java
// Before
new MatrixTest(org.example.documentfragment.TestCloneNodeDeep.class),
new MatrixTest(org.example.documentfragment.TestCloneNodeShallow.class),
new MatrixTest(org.example.documentfragment.TestLookupNamespaceURI.class),
new MatrixTest(org.example.documentfragment.TestLookupPrefix.class),

// After
new MatrixTestContainer(org.example.documentfragment.DocumentFragmentTests.class),
```

### 5. Delete the old files

Remove all the individual `Executable` test files that were consolidated.

### 6. Build and test

Run the affected module and its dependents to confirm no regressions:

```bash
./mvnw -pl <module> -amd verify
```

For changes to `testing/dom-testsuite`, run:

```bash
./mvnw -pl testing/dom-testsuite -amd verify
```

## Formatting

The project uses `spotless-maven-plugin` with `palantirJavaFormat`. After editing, run:

```bash
./mvnw -pl <module> spotless:apply
```

or let the build fail on the first attempt and re-run after applying the formatter.
