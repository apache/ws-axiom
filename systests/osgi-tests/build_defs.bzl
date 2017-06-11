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

def link(name, bundle, symbolic_name):
  native.genrule(
      name = "%s_link" % name,
      srcs = [],
      outs = ["src/generated/resources/%s.link" % symbolic_name],
      cmd = "echo classpath:%s.jar > $@" % name,
  )

  native.genrule(
      name = "%s_jar" % name,
      srcs = [bundle],
      outs = ["src/generated/resources/%s.jar" % name],
      cmd = "cp $< $@",
  )

  native.java_library(
      name = name,
      resources = [
          ":%s_link" % name,
          ":%s_jar" % name,
      ],
      data = [bundle],
  )
