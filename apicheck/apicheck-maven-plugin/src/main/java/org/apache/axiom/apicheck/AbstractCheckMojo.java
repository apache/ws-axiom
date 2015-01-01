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
package org.apache.axiom.apicheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.axiom.apicheck.checker.APIChecker;
import org.apache.axiom.apicheck.checker.Element;
import org.apache.axiom.apicheck.checker.ViolationListener;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

abstract class AbstractCheckMojo extends AbstractMojo {
    @Parameter(defaultValue="${project}", readonly=true, required=true)
    private MavenProject project;
    
    abstract List<String> getClasspathElements(MavenProject project) throws DependencyResolutionRequiredException;
    abstract String getOutputDirectory(MavenProject project);
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> classpathElements;
        try {
            classpathElements = getClasspathElements(project);
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Unexpected exception", ex);
        }
        if (classpathElements.isEmpty()) {
            return;
        }
        
        File outputDirectory = new File(getOutputDirectory(project));
        if (!outputDirectory.exists()) {
            return;
        }
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(outputDirectory);
        ds.setIncludes(new String[] {"**/*.class"});
        ds.addDefaultExcludes();
        ds.scan();
        String[] files = ds.getIncludedFiles();
        if (files.length == 0) {
            return;
        }
        final Log log = getLog();
        APIChecker checker = new APIChecker(new ViolationListener() {
            @Override
            public void log(Element element, String msg) {
                log.error(msg);
            }
        });
        try {
            InputStream in = AbstractCheckMojo.class.getResourceAsStream("signatures.txt");
            try {
                checker.loadSignatures(in);
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Failed to load signature file", ex);
        }
        for (String file : files) {
            
            try {
                InputStream in = new FileInputStream(new File(outputDirectory, file));
                try {
                    checker.checkClass(in);
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed to load class file " + file, ex);
            }
        }
    }
}
