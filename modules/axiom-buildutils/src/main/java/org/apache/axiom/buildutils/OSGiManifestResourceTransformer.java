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
package org.apache.axiom.buildutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.maven.plugins.shade.resource.ResourceTransformer;
import org.codehaus.plexus.util.StringUtils;

/**
 * Resource transformer that modifies the OSGi metadata in the manifest. It is designed for
 * scenarios where maven-shade-plugin is used to include one or more OSGi bundles into a shaded
 * artifact which is itself an OSGi bundle. It transforms the manifest of the shaded bundle such
 * that:
 * <ul>
 * <li><tt>Import-Package</tt> entries satisfied by the included bundle are removed.
 * <li><tt>Import-Package</tt> entries from included bundles are added to the
 * <tt>Import-Package</tt> attribute of the shaded bundle.
 * </ul>
 */
public class OSGiManifestResourceTransformer implements ResourceTransformer {
    private Manifest shadedManifest;
    
    public boolean canTransformResource(String resource) {
        return resource.equals(JarFile.MANIFEST_NAME);
    }

    public void processResource(String resource, InputStream is, List relocators) throws IOException {
        // We know that the first invocation of processResource is for the project's
        // manifest (see the existing ManifestResourceTransformer's source code)
        if (shadedManifest == null) {
            shadedManifest = new Manifest(is);
        } else {
            Manifest manifest = new Manifest(is);
            Attributes includedAttributes = manifest.getMainAttributes();
            Attributes shadedAttributes = shadedManifest.getMainAttributes();
            Set shadedImportPackages = new LinkedHashSet(Arrays.asList(
                    shadedAttributes.getValue("Import-Package").split(",")));
            String exportPackage = includedAttributes.getValue("Export-Package");
            if (exportPackage != null) {
                shadedImportPackages.removeAll(Arrays.asList(exportPackage.split(",")));
            }
            String importPackage = includedAttributes.getValue("Import-Package");
            if (importPackage != null) {
                shadedImportPackages.addAll(Arrays.asList(importPackage.split(",")));
            }
            shadedAttributes.putValue("Import-Package",
                    StringUtils.join(shadedImportPackages.iterator(), ","));
        }
        is.close();
    }

    public boolean hasTransformedResource() {
        return shadedManifest != null;
    }

    public void modifyOutputStream(JarOutputStream os) throws IOException {
        os.putNextEntry(new JarEntry(JarFile.MANIFEST_NAME));
        shadedManifest.write(os);
    }
}
