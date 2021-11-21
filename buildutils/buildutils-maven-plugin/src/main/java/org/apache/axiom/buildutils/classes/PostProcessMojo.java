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
package org.apache.axiom.buildutils.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

@Mojo(name="post-process-classes", defaultPhase=LifecyclePhase.PROCESS_CLASSES)
public class PostProcessMojo extends AbstractMojo {
    @Parameter(property="project.build.outputDirectory", required=true, readonly=true)
    private File classesDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!classesDir.exists()) {
            return;
        }
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes(new String[] { "**/*.class" });
        ds.setBasedir(classesDir);
        ds.scan();
        for (String relativePath : ds.getIncludedFiles()) {
            File file = new File(classesDir, relativePath);
            ClassWriter classWriter;
            try {
                InputStream in = new FileInputStream(file);
                try {
                    ClassReader classReader = new ClassReader(in);
                    classWriter = new ClassWriter(classReader, 0);
                    ClassVisitor classVisitor = classWriter;
                    if (relativePath.equals("org/apache/axiom/om/OMText.class")
                            || relativePath.equals("org/apache/axiom/om/impl/llom/AxiomCharacterDataNodeImpl.class")
                            || relativePath.equals("org/apache/axiom/om/impl/dom/DOMTextNodeImpl.class")) {
                        classVisitor = new GetDataHandlerBridgeMethodInjector(classVisitor);
                    }
                    classVisitor = new AspectJCodeRemover(classVisitor);
                    classReader.accept(classVisitor, 0);
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed to read " + relativePath + ": " + ex.getMessage(), ex);
            }
            try {
                OutputStream out = new FileOutputStream(file);
                try {
                    out.write(classWriter.toByteArray());
                } finally {
                    out.close();
                }
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed to write " + relativePath + ": " + ex.getMessage(), ex);
            }
        }
    }
}
