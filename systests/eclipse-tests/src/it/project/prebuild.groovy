/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// This script adjusts the Bundle-Version in META-INF/MANIFEST.MF. It is needed because:
//  * Filtering is only supported for POM files (see MINVOKER-117).
//  * Tycho complains if there is a version mismatch between the POM and the manifest.
//  * The test project must have the same version as the main project. The reason is that
//    we want the main project to be the parent of the test project. If the versions don't
//    match, then ${project.version} will be substituted by the wrong value.

import java.io.*
import java.util.*
import java.util.jar.*

stream = new FileInputStream(new File(basedir, "../script.properties"))
props = new Properties()
props.load(stream)
stream.close()

version = props.getProperty("version").replace("-SNAPSHOT", ".qualifier")

manifestFile = new File(basedir, "META-INF/MANIFEST.MF")

stream = new FileInputStream(manifestFile)
manifest = new Manifest(stream)
stream.close()

manifest.mainAttributes.putValue("Bundle-Version", version)

stream = new FileOutputStream(manifestFile)
manifest.write(stream)
stream.close()
