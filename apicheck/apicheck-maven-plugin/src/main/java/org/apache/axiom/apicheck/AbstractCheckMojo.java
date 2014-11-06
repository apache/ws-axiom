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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

import de.thetaphi.forbiddenapis.Checker;
import de.thetaphi.forbiddenapis.ForbiddenApiException;
import de.thetaphi.forbiddenapis.ParseException;

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
        
        URL[] urls = new URL[classpathElements.size()];
        int i = 0;
        for (String classpathElement : classpathElements) {
            try {
                urls[i++] = new File(classpathElement).toURI().toURL();
            } catch (MalformedURLException ex) {
                throw new MojoExecutionException("Unexpected exception", ex);
            }
        }
        URLClassLoader classLoader = new URLClassLoader(urls);
        try {
            final Log log = getLog();
            Checker checker = new Checker(classLoader, false, false, false) {
                @Override
                protected void logError(String msg) {
                    log.error(msg);
                }
                
                @Override
                protected void logWarn(String msg) {
                    log.warn(msg);
                }
                
                @Override
                protected void logInfo(String msg) {
                    log.info(msg);
                }
            };
            try {
                checker.parseSignaturesFile(AbstractCheckMojo.class.getResourceAsStream("signatures.txt"));
            } catch (IOException ex) {
                throw new MojoExecutionException("I/O error while reading signatures", ex);
            } catch (ParseException ex) {
                throw new MojoExecutionException("Failed to parse signatures", ex);
            }
            if (checker.hasNoSignatures()) {
                // We get here if Axiom is not in the classpath
                return;
            }
            for (String file : files) {
                try {
                    checker.addClassToCheck(new FileInputStream(new File(outputDirectory, file)));
                } catch (IOException ex) {
                    throw new MojoExecutionException("Failed to load class file " + file, ex);
                }
            }
            log.info("Scanning for forbidden API invocations...");
            try {
                checker.run();
            } catch (ForbiddenApiException ex) {
                throw new MojoFailureException(ex.getMessage());
            }
        } finally {
            if (classLoader instanceof Closeable) try {
                ((Closeable)classLoader).close();
            } catch (IOException ex) {
                // Ignore
            }
        }
    }
}
