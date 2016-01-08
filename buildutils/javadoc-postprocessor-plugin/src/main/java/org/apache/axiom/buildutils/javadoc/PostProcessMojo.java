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
package org.apache.axiom.buildutils.javadoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.tools.SiteTool;
import org.apache.maven.doxia.tools.SiteToolException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

@Mojo(name="post-process")
public class PostProcessMojo extends AbstractMojo {
    @Component
    private SiteTool siteTool;

    @Parameter(property="locales")
    private String locales;

    @Parameter(defaultValue="${project}", required=true, readonly=true)
    private MavenProject project;

    @Parameter(defaultValue="${reactorProjects}", required=true, readonly=true)
    private List<MavenProject> reactorProjects;

    @Parameter(defaultValue="${localRepository}", required=true, readonly=true)
    private ArtifactRepository localRepository;

    @Parameter(defaultValue="${project.remoteArtifactRepositories}", required=true, readonly=true)
    private List<ArtifactRepository> repositories;

    @Parameter(defaultValue="${basedir}/src/site")
    private File siteDirectory;

    @Parameter(required=true)
    private File javadocDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        DecorationModel decorationModel;
        try {
            decorationModel = siteTool.getDecorationModel(project, reactorProjects, localRepository, repositories,
                    siteTool.getRelativePath(siteDirectory.getAbsolutePath(), project.getBasedir().getAbsolutePath()),
                    siteTool.getAvailableLocales(locales).get(0));
        } catch (SiteToolException ex) {
            throw new MojoExecutionException("SiteToolException: " + ex.getMessage(), ex);
        }
        StringWriter sw = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter(sw, null, null);
        for (Xpp3Dom element : ((Xpp3Dom)decorationModel.getBody().getHead()).getChildren()) {
            // Turn off escaping in scripts. Note that this mimics the behavior in the default-site.vm
            // template in doxia-site-renderer.
            Xpp3DomWriter.write(xmlWriter, element, !element.getName().equals("script"));
        }
        String headElements = sw.toString();
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes(new String[] { "**/*.html" });
        ds.setBasedir(javadocDirectory);
        ds.scan();
        for (String relativePath : ds.getIncludedFiles()) {
            File file = new File(javadocDirectory, relativePath);
            File tmpFile = new File(javadocDirectory, relativePath + ".tmp");
            file.renameTo(tmpFile);
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(tmpFile), "UTF-8"));
                try {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                    try {
                        String line;
                        while ((line = in.readLine()) != null) {
                            if (line.equals("</head>")) {
                                out.println(headElements);
                            }
                            out.println(line);
                        }
                    } finally {
                        out.close();
                    }
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed to process " + relativePath + ": " + ex.getMessage(), ex);
            }
            tmpFile.delete();
        }
    }
}
