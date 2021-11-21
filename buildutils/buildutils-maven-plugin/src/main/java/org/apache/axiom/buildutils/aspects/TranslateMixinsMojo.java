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
package org.apache.axiom.buildutils.aspects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

@Mojo(name="translate-mixins", defaultPhase=LifecyclePhase.GENERATE_SOURCES)
public final class TranslateMixinsMojo extends AbstractMojo {
    private final static Pattern aspectDecl = Pattern.compile("public aspect (.*)Support \\{");

    @Parameter(defaultValue="${project}", readonly=true, required=true)
    private MavenProject project;

    @Parameter(defaultValue="${project.build.directory}/generated-sources/mixins", readonly=true, required=true)
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (project.getPackaging().equals("pom")) {
            return;
        }
        for (String sourceRoot : project.getCompileSourceRoots()) {
            DirectoryScanner ds = new DirectoryScanner();
            ds.setIncludes(new String[] { "**/*Support.aj" });
            ds.setBasedir(sourceRoot);
            ds.scan();
            for (String relativePath : ds.getIncludedFiles()) {
                File inFile = new File(sourceRoot, relativePath);
                File outFile = new File(outputDirectory, relativePath.substring(0, relativePath.length()-10) + "Mixin.java");
                outFile.getParentFile().mkdirs();
                try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "UTF-8"));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
                ) {
                    String iface = null;
                    Pattern intertypeDecl = null;
                    String line;
                    while ((line = in.readLine()) != null) {
                        Matcher matcher = aspectDecl.matcher(line);
                        if (matcher.matches()) {
                            iface = matcher.group(1);
                            out.println("@org.apache.axiom.weaver.annotation.Mixin(" + iface + ".class)");
                            out.println("public abstract class " + iface + "Mixin implements " + iface + " {");
                            intertypeDecl = Pattern.compile(iface + "\\.(?!class)");
                        } else {
                            if (iface != null) {
                                line = intertypeDecl.matcher(line).replaceAll("");
                                line = line.replace(iface + "Support.class", iface + "Mixin.class");
                            }
                            out.println(line);
                        }
                    }
                } catch (IOException ex) {
                    throw new MojoExecutionException("Failed to process " + inFile, ex);
                }
            }
        }
        project.addCompileSourceRoot(outputDirectory.getPath());
    }
}
