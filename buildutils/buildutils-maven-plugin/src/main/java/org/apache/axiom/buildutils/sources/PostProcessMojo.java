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
package org.apache.axiom.buildutils.sources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.transfer.artifact.DefaultArtifactCoordinate;
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResolverException;
import org.apache.maven.shared.utils.io.IOUtil;
import org.objectweb.asm.ClassReader;

@Mojo(
        name = "post-process-sources-jar",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PostProcessMojo extends AbstractMojo {
    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "session", readonly = true, required = true)
    private MavenSession session;

    @Component private ArtifactResolver artifactResolver;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (project.getPackaging().equals("pom")) {
            return;
        }
        Set<String> sources = new HashSet<>();
        try (JarInputStream in =
                new JarInputStream(new FileInputStream(project.getArtifact().getFile()))) {
            JarEntry entry;
            while ((entry = in.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    new ClassReader(in)
                            .accept(
                                    new SourceExtractor(
                                            sources, name.substring(0, name.lastIndexOf('/') + 1)),
                                    ClassReader.SKIP_CODE);
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Error reading jar: " + ex.getMessage(), ex);
        }
        File sourcesJar =
                new File(
                        project.getBuild().getDirectory(),
                        project.getBuild().getFinalName() + "-sources.jar");
        File postProcessedSourcesJar =
                new File(
                        project.getBuild().getDirectory(),
                        project.getBuild().getFinalName() + "-post-processed-sources.jar");
        try (JarOutputStream out =
                new JarOutputStream(new FileOutputStream(postProcessedSourcesJar))) {
            processSourceJar(sourcesJar, sources, true, out);
            ArtifactFilter filter = new ScopeArtifactFilter(Artifact.SCOPE_RUNTIME);
            for (Artifact artifact : project.getArtifacts()) {
                if (sources.isEmpty()) {
                    break;
                }
                if (filter.include(artifact)) {
                    DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();
                    coordinate.setGroupId(artifact.getGroupId());
                    coordinate.setArtifactId(artifact.getArtifactId());
                    coordinate.setVersion(artifact.getVersion());
                    coordinate.setExtension("jar");
                    coordinate.setClassifier("sources");
                    Artifact resolvedArtifact;
                    try {
                        resolvedArtifact =
                                artifactResolver
                                        .resolveArtifact(
                                                session.getProjectBuildingRequest(), coordinate)
                                        .getArtifact();
                    } catch (ArtifactResolverException ex) {
                        getLog().warn("Could not get sources for " + artifact);
                        continue;
                    }
                    if (resolvedArtifact.isResolved()) {
                        processSourceJar(resolvedArtifact.getFile(), sources, false, out);
                    }
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Error writing jar: " + ex.getMessage(), ex);
        }
        sourcesJar.delete();
        postProcessedSourcesJar.renameTo(sourcesJar);
    }

    private void processSourceJar(
            File file, Set<String> sources, boolean includeAll, JarOutputStream out)
            throws MojoExecutionException {
        try (JarInputStream in = new JarInputStream(new FileInputStream(file))) {
            JarEntry entry;
            while ((entry = in.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (sources.remove(name) || includeAll) {
                    out.putNextEntry(entry);
                    IOUtil.copy(in, out);
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Error reading " + file + ": " + ex.getMessage(), ex);
        }
    }
}
