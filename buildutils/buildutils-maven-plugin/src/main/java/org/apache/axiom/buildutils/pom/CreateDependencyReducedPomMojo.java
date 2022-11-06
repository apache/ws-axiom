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
package org.apache.axiom.buildutils.pom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

@Mojo(name = "create-dependency-reduced-pom", defaultPhase = LifecyclePhase.PACKAGE)
public class CreateDependencyReducedPomMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter private Set<String> excludedArtifacts;

    @Parameter(defaultValue = "${project.build.directory}/dependency-reduced-pom.xml")
    private File dependencyReducedPomLocation;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (excludedArtifacts == null || excludedArtifacts.isEmpty()) {
            return;
        }
        Model model;
        try (InputStream in = new FileInputStream(project.getFile())) {
            model = new MavenXpp3Reader().read(in);
        } catch (IOException | XmlPullParserException ex) {
            throw new MojoExecutionException("Error reading POM", ex);
        }
        for (Iterator<Dependency> it = model.getDependencies().iterator(); it.hasNext(); ) {
            Dependency dependency = it.next();
            if (excludedArtifacts.contains(dependency.getArtifactId())) {
                it.remove();
            }
        }
        dependencyReducedPomLocation.getParentFile().mkdirs();
        MavenXpp3Writer pomWriter = new MavenXpp3Writer();
        try (OutputStream out = new FileOutputStream(dependencyReducedPomLocation)) {
            pomWriter.write(out, model);
        } catch (IOException ex) {
            throw new MojoExecutionException("Error writing dependency-reduced POM", ex);
        }
        project.setPomFile(dependencyReducedPomLocation);
    }
}
