#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

# Keep heap size small. We have some unit tests that deal with volumes
# of data proportional to the heap size (to test that Axiom is streaming
# the data instead of loading it into memory). Obviously, the execution time of
# these tests also are proportional to the heap size. To accelerate the execution
# of the tests, we should use a heap size as small as possible.
JVM_FLAGS = ["-Xms16m", "-Xmx48m"] 

def _jacoco_impl(ctx):
  jars = list(ctx.attr.lib[java_common.provider].transitive_runtime_jars)
  ctx.action(
      inputs = jars + ctx.files._agent,
      outputs = [ctx.outputs.exec],
      arguments = JVM_FLAGS + [
          "-classpath", ctx.configuration.host_path_separator.join([f.path for f in jars]),
          "-javaagent:%s=destfile=%s" % (ctx.files._agent[0].path, ctx.outputs.exec.path),
          "org.junit.runner.JUnitCore",
          ctx.attr.test_class,
      ],
      progress_message = "Generating JaCoCo coverage data for %s" % ctx.attr.test_name,
      executable = ctx.executable._java)

_jacoco = rule(
    implementation = _jacoco_impl,
    attrs = {
        "test_name": attr.string(),
        "lib": attr.label(
            allow_files=False,
        ),
        "test_class": attr.string(),
        "_java": attr.label(
            default=Label("@local_jdk//:java"),
            allow_files=True,
            executable=True,
            cfg="host",
        ),
        "_agent": attr.label(
            default=Label("//third_party:jacocoagent"),
            allow_files=False,
        ),
    },
    outputs = {
        "exec": "%{name}.exec",
    },
)

def test(name, test_class, srcs=[], deps=[], runtime_deps=[], data=[]):
  native.java_library(
      name = "%s_lib" % name,
      srcs = srcs,
      deps = deps + ["//third_party:junit"] if deps else None,
      runtime_deps = runtime_deps,
      data = data,
  )

  native.java_test(
      name = name,
      runtime_deps = ["%s_lib" % name],
      test_class = test_class,
      jvm_flags = JVM_FLAGS,
  )

  _jacoco(
      name = "%s_jacoco" % name,
      test_name = name,
      lib = ":%s_lib" % name,
      test_class = test_class,
  )
