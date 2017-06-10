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

load(":version.bzl", "VERSION")

def _impl(ctx):
  bnd_file = ctx.new_file(ctx.configuration.bin_dir, "bnd_file")
  instructions = {
      "Bundle-Version": VERSION,
      "Bundle-SymbolicName": ctx.attr.symbolic_name,
      "Private-Package": ",".join(ctx.attr.private_packages),
      "Export-Package": ",".join(ctx.attr.export_packages),
      "Import-Package": ",".join(ctx.attr.import_packages),
      "-removeheaders": "Bnd-LastModified,Build-Jdk,Built-By,Private-Package,Include-Resource",
      "-nodefaultversion": "true",
      "-consumer-policy": "",
  }
  if ctx.attr.activator:
    instructions["Bundle-Activator"] = ctx.attr.activator
  if ctx.attr.activation_policy:
    instructions["Bundle-ActivationPolicy"] = ctx.attr.activation_policy
  ctx.file_action(
      output = bnd_file,
      content = "\n".join(["%s: %s" % e for e in instructions.items()]))
  bundle_jar = ctx.outputs.bundle_jar
  dep_jars = list(java_common.merge([dep[java_common.provider] for dep in ctx.attr.deps]).transitive_runtime_jars)
  args = ["buildx", "-f", "-o", bundle_jar.path.split("/")[-1]]
  for f in dep_jars:
    args.extend(["-c", f.path])
  args.append(bnd_file.path)
  ctx.action(
      inputs=dep_jars + [bnd_file],
      outputs=[bundle_jar],
      arguments=args,
      progress_message="Building bundle %s" % bundle_jar.short_path,
      executable=ctx.executable._bnd)

bundle = rule(
    implementation = _impl,
    attrs = {
        "deps": attr.label_list(
            allow_files=False,
        ),
        "symbolic_name": attr.string(mandatory=True),
        "private_packages": attr.string_list(),
        "export_packages": attr.string_list(),
        "import_packages": attr.string_list(),
        "activator": attr.string(),
        "activation_policy": attr.string(),
        "_bnd": attr.label(
            default=Label("//buildutils:bnd"),
            allow_files=True,
            executable=True,
            cfg="host",
        ),
    },
    outputs = {
        "bundle_jar": "%{name}.jar",
    },
)
