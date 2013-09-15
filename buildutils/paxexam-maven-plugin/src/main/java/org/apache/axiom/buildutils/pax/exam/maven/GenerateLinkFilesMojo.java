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
package org.apache.axiom.buildutils.pax.exam.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import org.apache.maven.shared.artifact.filter.collection.ScopeFilter;
import org.apache.maven.shared.artifact.filter.collection.TypeFilter;

/**
 * @goal generate-link-files
 * @phase generate-test-resources
 * @requiresDependencyResolution test
 */
public class GenerateLinkFilesMojo extends AbstractMojo {
    /**
     * @parameter property="project.artifacts"
     * @readonly
     * @required
     */
    private Set<Artifact> projectArtifacts;
    
    /**
     * @parameter property="project.build"
     * @readonly
     * @required
     */
    private Build build;
    
    /**
     * @parameter default-value="${project.build.directory}/pax-exam-links"
     */
    private File outputDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        FilterArtifacts filter = new FilterArtifacts();
        filter.addFilter(new ScopeFilter(Artifact.SCOPE_TEST, null));
        filter.addFilter(new TypeFilter("jar", null));
        Set<Artifact> artifacts;
        try {
            artifacts = filter.filter(projectArtifacts);
        } catch (ArtifactFilterException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
        outputDirectory.mkdirs();
        for (Artifact artifact : artifacts) {
            File file = artifact.getFile();
            Manifest manifest = null;
            try {
                InputStream in = new FileInputStream(file);
                try {
                    ZipInputStream zip = new ZipInputStream(in);
                    ZipEntry entry;
                    while ((entry = zip.getNextEntry()) != null) {
                        if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                            manifest = new Manifest(zip);
                            break;
                        }
                    }
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                getLog().error("Unable to read " + artifact.getFile(), ex);
            }
            if (manifest != null) {
                String symbolicName = manifest.getMainAttributes().getValue("Bundle-SymbolicName");
                if (symbolicName != null) {
                    int idx = symbolicName.indexOf(';');
                    if (idx != -1) {
                        symbolicName = symbolicName.substring(0, idx);
                    }
                    File linkFile = new File(outputDirectory, symbolicName + ".link");
                    try {
                        PrintWriter out = new PrintWriter(new FileOutputStream(linkFile), false);
                        try {
                            out.write(file.toURI().toString());
                        } finally {
                            out.close();
                        }
                    } catch (IOException ex) {
                        throw new MojoExecutionException("Failed to create " + linkFile, ex);
                    }
                }
            }
        }
        Resource resource = new Resource();
        resource.setDirectory(outputDirectory.toString());
        build.addTestResource(resource);
    }
}
