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
package org.apache.axiom.buildutils.enforcer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.objectweb.asm.ClassReader;

@Mojo(name="enforce", defaultPhase=LifecyclePhase.PROCESS_CLASSES)
public class EnforceMojo extends AbstractMojo {
    @Parameter(defaultValue="${project.build.outputDirectory}", required=true, readonly=true)
    private File classesDir;

    @Parameter
    private String ignore;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!classesDir.exists()) {
            return;
        }
        Set<Reference> ignoredClassReferences = new HashSet<Reference>();
        if (ignore != null) {
            for (String ignoreRule : ignore.split(",")) {
                String[] s = ignoreRule.split("->");
                ignoredClassReferences.add(new Reference(s[0].trim(), s[1].trim()));
            }
        }
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes(new String[] { "**/*.class" });
        ds.setBasedir(classesDir);
        ds.scan();
        ReferenceCollector referenceCollector = new ReferenceCollector(ignoredClassReferences);
        for (String relativePath : ds.getIncludedFiles()) {
            try {
                InputStream in = new FileInputStream(new File(classesDir, relativePath));
                try {
                    new ClassReader(in).accept(new ClassProcessor(referenceCollector), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed to read " + relativePath + ": " + ex.getMessage(), ex);
            }
        }
        Set<Reference> references = referenceCollector.getClassReferencesForPackageCycle();
        if (references != null) {
            StringBuilder buffer = new StringBuilder("Package cycle detected. Classes involved:");
            for (Reference reference : references) {
                buffer.append("\n  ");
                buffer.append(reference.getFrom());
                buffer.append(" -> ");
                buffer.append(reference.getTo());
            }
            throw new MojoFailureException(buffer.toString());
        }
        Set<Reference> unusedIgnoredClassReferences = referenceCollector.getUnusedIgnoredClassReferences();
        if (!unusedIgnoredClassReferences.isEmpty()) {
            StringBuilder buffer = new StringBuilder("Found unused ignored class references:");
            for (Reference reference : unusedIgnoredClassReferences) {
                buffer.append("\n  ");
                buffer.append(reference.getFrom());
                buffer.append(" -> ");
                buffer.append(reference.getTo());
            }
            throw new MojoFailureException(buffer.toString());
        }
    }
}
