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

def _new_patched_http_archive_impl(rctx):
  # Download the archive and extract it
  rctx.download_and_extract(
      url=rctx.attr.urls,
      output=rctx.path(""),
      stripPrefix=rctx.attr.strip_prefix,
      type=rctx.attr.type,
      sha256=rctx.attr.sha256)

  # Now patch the repository
  patch_file = str(rctx.path(rctx.attr.patch).realpath)
  result = rctx.execute(["bash", "-c", "patch -p0 < " + patch_file])
  if result.return_code != 0:
    fail("Failed to patch (%s): %s" % (result.return_code, result.stderr))

  # And finally add the build file
  rctx.symlink(rctx.attr.build_file, "BUILD.bazel")


new_patched_http_archive = repository_rule(
    implementation=_new_patched_http_archive_impl,
    attrs={
        "urls": attr.string_list(mandatory=True),
        "patch": attr.label(mandatory=True),
        "sha256": attr.string(mandatory=True),
        "strip_prefix": attr.string(mandatory=False, default=""),
        "type": attr.string(mandatory=False, default=""),
        "build_file": attr.label(mandatory=True),
    })

