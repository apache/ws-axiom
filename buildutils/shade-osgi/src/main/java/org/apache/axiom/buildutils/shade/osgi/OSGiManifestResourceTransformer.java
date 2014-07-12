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
package org.apache.axiom.buildutils.shade.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;

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
    private Manifest originalManifest;
    private final Set<String> importedPackages = new LinkedHashSet<String>();
    private final Set<String> packagesExportedFromIncludedJARs = new LinkedHashSet<String>();
    
    public boolean canTransformResource(String resource) {
        return resource.equals(JarFile.MANIFEST_NAME);
    }

    private static List<String> extractPackages(Attributes attributes, String header) throws IOException {
        String value = attributes.getValue(header);
        if (value == null) {
            return Collections.emptyList();
        } else {
            ManifestElement[] elements;
            try {
                elements = ManifestElement.parseHeader("Export-Package", value);
            } catch (BundleException ex) {
                throw new IOException("Invalid bundle manifest", ex);
            }
            List<String> result = new ArrayList<String>(elements.length);
            for (ManifestElement element : elements) {
                result.add(element.getValue());
            }
            return result;
        }
    }
    
    public void processResource(String resource, InputStream is, List<Relocator> relocators) throws IOException {
        Manifest manifest = new Manifest(is);
        Attributes attributes = manifest.getMainAttributes();
        importedPackages.addAll(extractPackages(attributes, "Import-Package"));
        // We know that the first invocation of processResource is for the project's
        // manifest (see the existing ManifestResourceTransformer's source code)
        if (originalManifest == null) {
            originalManifest = manifest;
        } else {
            packagesExportedFromIncludedJARs.addAll(extractPackages(attributes, "Export-Package"));
        }
        is.close();
    }

    public boolean hasTransformedResource() {
        return originalManifest != null;
    }

    public void modifyOutputStream(JarOutputStream os) throws IOException {
        importedPackages.removeAll(packagesExportedFromIncludedJARs);
        originalManifest.getMainAttributes().putValue("Import-Package",
                StringUtils.join(importedPackages.iterator(), ","));
        os.putNextEntry(new JarEntry(JarFile.MANIFEST_NAME));
        originalManifest.write(os);
    }
}
