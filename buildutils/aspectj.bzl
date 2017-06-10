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

def _impl(ctx):
  class_jar = ctx.outputs.class_jar
  deps_provider = java_common.merge([dep[java_common.provider] for dep in ctx.attr.deps + ctx.attr.aspects + [ctx.attr._runtime]])
  ctx.action(
      inputs=list(deps_provider.transitive_runtime_jars) + ctx.files.aspects + ctx.files.srcs,
      outputs=[class_jar],
      arguments=[
          "-classpath", ctx.configuration.host_path_separator.join([f.path for f in deps_provider.transitive_runtime_jars]),
          "-aspectpath", ctx.configuration.host_path_separator.join([f.path for f in ctx.files.aspects]),
          "-outjar", class_jar.path,
          "-source", "1.7",
          "-target", "1.7",
      ] + [f.path for f in ctx.files.srcs],
      progress_message="Building %s" % class_jar.short_path,
      executable=ctx.executable._ajc)
  return [java_common.merge(
      [deps_provider, java_common.create_provider(compile_time_jars=[class_jar], runtime_jars=[class_jar])])]

aj_library = rule(
    implementation = _impl,
    attrs = {
        "srcs": attr.label_list(
            allow_files=FileType([".java", ".aj"]),
        ),
        "deps": attr.label_list(
            allow_files=False,
        ),
        "aspects": attr.label_list(
            allow_files=False,
        ),
        "_ajc": attr.label(
            default=Label("//buildutils:ajc"),
            allow_files=True,
            executable=True,
            cfg="host",
        ),
        "_runtime": attr.label(
            default=Label("@aspectj_runtime//jar"),
            allow_files=False,
        ),
    },
    outputs = {
        "class_jar": "lib%{name}.jar",
    },
)
