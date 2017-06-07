# Partially based on bazel/tools/build_rules/java_rules_skylark.bzl

def _impl(ctx):
  class_jar = ctx.outputs.class_jar
  ctx.action(
      inputs=ctx.files.deps + ctx.files._runtime + ctx.files.srcs,
      outputs=[class_jar],
      arguments=[
          "-classpath", ctx.configuration.host_path_separator.join([f.path for f in ctx.files.deps + ctx.files._runtime]),
          "-outjar", class_jar.path,
          "-source", "1.7",
          "-target", "1.7",
      ] + [f.path for f in ctx.files.srcs],
      progress_message="Building %s" % class_jar.short_path,
      executable=ctx.executable._ajc)

aspectj_library = rule(
    implementation = _impl,
    attrs = {
        "srcs": attr.label_list(
            allow_files=FileType([".java", ".aj"]),
        ),
        "deps": attr.label_list(
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
