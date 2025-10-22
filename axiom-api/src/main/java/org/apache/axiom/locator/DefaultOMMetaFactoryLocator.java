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
package org.apache.axiom.locator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactoryLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** The default {@link OMMetaFactoryLocator} implementation used in non OSGi environments. */
public final class DefaultOMMetaFactoryLocator extends PriorityBasedOMMetaFactoryLocator {
    private static final Log log = LogFactory.getLog(DefaultOMMetaFactoryLocator.class);

    public DefaultOMMetaFactoryLocator() {
        ClassLoader classLoader = DefaultOMMetaFactoryLocator.class.getClassLoader();

        // Fall back to the system class loader if Axiom is loaded form the bootstrap
        // class loader (There is no good reason to do that, but we don't want people to
        // blame Axiom if things break).
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        Loader loader = new DefaultLoader(classLoader);

        List<Implementation> implementations = new ArrayList<Implementation>();

        // If a meta factory is specified using the system property, we register it as an
        // implementation with feature "default" and maximum priority, so that it will always
        // be used as default implementation. Note that even if the system property is specified
        // we still need to run a discovery because there may be other non default implementations
        // in the classpath.
        String metaFactoryClassName = null;
        try {
            metaFactoryClassName = System.getProperty(OMAbstractFactory.META_FACTORY_NAME_PROPERTY);
            if ("".equals(metaFactoryClassName)) {
                metaFactoryClassName = null;
            }
        } catch (SecurityException e) {
            // Ignore and continue
        }
        if (metaFactoryClassName != null) {
            if (log.isDebugEnabled()) {
                log.debug(
                        OMAbstractFactory.META_FACTORY_NAME_PROPERTY
                                + " system property is set; value="
                                + metaFactoryClassName);
            }
            Implementation implementation =
                    ImplementationFactory.createDefaultImplementation(loader, metaFactoryClassName);
            if (implementation != null) {
                implementations.add(implementation);
            }
        }

        // Now discover the available implementations by looking for the axiom.xml descriptor.
        log.debug("Starting class path based discovery");
        Enumeration<URL> e;
        try {
            e = classLoader.getResources(ImplementationFactory.DESCRIPTOR_RESOURCE);
        } catch (IOException ex) {
            log.error(
                    "Failed to look up "
                            + ImplementationFactory.DESCRIPTOR_RESOURCE
                            + " from class loader",
                    ex);
            e = null;
        }
        if (e != null) {
            while (e.hasMoreElements()) {
                implementations.addAll(
                        ImplementationFactory.parseDescriptor(loader, e.nextElement()));
            }
        }

        loadImplementations(implementations);
    }
}
