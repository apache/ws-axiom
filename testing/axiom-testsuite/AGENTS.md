# AI Agent Instructions

## Validating changes to axiom-testsuite

`testing/axiom-testsuite` contains abstract test cases that are executed against the Axiom implementations. The module has no tests of its own; its tests only run as part of `implementations/axiom-impl` (LLOM) and `implementations/axiom-dom` (DOOM).

**After every change to `testing/axiom-testsuite`, you MUST run the tests for both implementations:**

```
./mvnw -pl testing/axiom-testsuite,implementations/axiom-impl,implementations/axiom-dom -am test
```

Do not consider a change to `axiom-testsuite` complete until this command passes without failures.
