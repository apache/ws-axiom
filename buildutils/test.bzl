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
def test(**kwargs):
  native.java_test(
      # Keep heap size small. We have some unit tests that deal with volumes
      # of data proportional to the heap size (to test that Axiom is streaming
      # the data instead of loading it into memory). Obviously, the execution time of
      # these tests also are proportional to the heap size. To accelerate the execution
      # of the tests, we should use a heap size as small as possible.
      jvm_flags = ["-Xms16m", "-Xmx48m"],
      **kwargs)
