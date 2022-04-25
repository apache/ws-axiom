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
package org.apache.axiom.weaver.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.axiom.weaver.ImplementationClassNameMapper;
import org.apache.axiom.weaver.Weaver;
import org.apache.axiom.weaver.WeaverException;
import org.apache.axiom.weaver.mixin.ClassDefinition;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(
        name = "weave",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public final class WeaveMojo extends AbstractMojo {
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    @Parameter(required = true)
    private PackageMapping[] packageMappings;

    @Parameter(required = true)
    private String[] weavablePackages;

    @Parameter(required = true)
    private String[] interfaces;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        URLClassLoader classLoader = createClassLoader();
        try {
            Weaver weaver =
                    new Weaver(
                            classLoader,
                            new ImplementationClassNameMapper() {
                                @Override
                                public String getImplementationClassName(Class<?> iface) {
                                    String packageName = iface.getPackage().getName();
                                    for (PackageMapping packageMapping : packageMappings) {
                                        if (packageName.equals(
                                                packageMapping.getInterfacePackage())) {
                                            return packageMapping.getOutputPackage()
                                                    + "."
                                                    + iface.getSimpleName()
                                                    + "Impl";
                                        }
                                    }
                                    throw new WeaverException(
                                            "No package mapping defined for package "
                                                    + packageName);
                                }
                            });
            for (String packageName : weavablePackages) {
                weaver.loadWeavablePackage(packageName);
            }
            for (String interfaceName : interfaces) {
                try {
                    weaver.addInterfaceToImplement(classLoader.loadClass(interfaceName));
                } catch (ClassNotFoundException ex) {
                    throw new MojoFailureException("Failed to load class " + interfaceName, ex);
                }
            }
            for (ClassDefinition classDefinition : weaver.generate()) {
                File outputFile =
                        new File(
                                project.getBuild().getOutputDirectory(),
                                classDefinition.getClassName() + ".class");
                outputFile.getParentFile().mkdirs();
                try (FileOutputStream out = new FileOutputStream(outputFile)) {
                    out.write(classDefinition.toByteArray());
                } catch (IOException ex) {
                    throw new MojoFailureException("Unable to write " + outputFile, ex);
                }
            }
        } finally {
            try {
                classLoader.close();
            } catch (IOException ex) {
                getLog().warn(ex);
            }
        }
    }

    private URLClassLoader createClassLoader() throws MojoExecutionException {
        List<String> paths;
        try {
            paths = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Failed to get copile classpath elements", ex);
        }
        URL[] urls = new URL[paths.size()];
        int i = 0;
        for (String path : paths) {
            try {
                urls[i++] = new File(path).toURI().toURL();
            } catch (MalformedURLException ex) {
                throw new MojoExecutionException("Failed to build URL for " + path, ex);
            }
        }
        return new URLClassLoader(urls);
    }
}
