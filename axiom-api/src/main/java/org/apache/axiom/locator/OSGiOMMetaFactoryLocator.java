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

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.axiom.om.OMMetaFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTrackerCustomizer;

final class OSGiOMMetaFactoryLocator extends PriorityBasedOMMetaFactoryLocator
        implements BundleTrackerCustomizer<List<RegisteredImplementation>> {
    private final BundleContext apiBundleContext;
    private final List<Implementation> implementations = new ArrayList<Implementation>();

    OSGiOMMetaFactoryLocator(BundleContext apiBundleContext) {
        this.apiBundleContext = apiBundleContext;
    }

    // Need to synchronize access because the implementations may be reloaded concurrently
    @Override
    public synchronized OMMetaFactory getOMMetaFactory(String feature) {
        return super.getOMMetaFactory(feature);
    }

    @Override
    public List<RegisteredImplementation> addingBundle(Bundle bundle, BundleEvent event) {
        URL descriptorUrl = bundle.getEntry(ImplementationFactory.DESCRIPTOR_RESOURCE);
        if (descriptorUrl != null) {
            List<Implementation> discoveredImplementations =
                    ImplementationFactory.parseDescriptor(new OSGiLoader(bundle), descriptorUrl);
            List<RegisteredImplementation> registeredImplementations =
                    new ArrayList<RegisteredImplementation>(discoveredImplementations.size());
            synchronized (this) {
                implementations.addAll(discoveredImplementations);
                loadImplementations(implementations);
            }
            for (Implementation implementation : discoveredImplementations) {
                List<ServiceRegistration<?>> registrations =
                        new ArrayList<ServiceRegistration<?>>();
                List<ServiceReference<?>> references = new ArrayList<ServiceReference<?>>();
                for (Feature feature : implementation.getFeatures()) {
                    List<String> clazzes = new ArrayList<String>();
                    clazzes.add(OMMetaFactory.class.getName());
                    for (Class<?> extensionInterface : feature.getExtensionInterfaces()) {
                        clazzes.add(extensionInterface.getName());
                    }
                    Hashtable<String, Object> properties = new Hashtable<String, Object>();
                    properties.put("implementationName", implementation.getName());
                    properties.put("feature", feature.getName());
                    properties.put(Constants.SERVICE_RANKING, feature.getPriority());
                    ServiceRegistration<?> registration =
                            bundle.getBundleContext()
                                    .registerService(
                                            clazzes.toArray(new String[clazzes.size()]),
                                            implementation.getMetaFactory(),
                                            properties);
                    registrations.add(registration);
                    ServiceReference<?> reference = registration.getReference();
                    references.add(reference);
                    // Let the OSGi runtime know that the axiom-api bundle is using the service
                    apiBundleContext.getService(reference);
                }
                registeredImplementations.add(
                        new RegisteredImplementation(implementation, registrations, references));
            }
            return registeredImplementations;
        } else {
            return null;
        }
    }

    @Override
    public void modifiedBundle(
            Bundle bundle, BundleEvent event, List<RegisteredImplementation> object) {}

    @Override
    public void removedBundle(
            Bundle bundle, BundleEvent event, List<RegisteredImplementation> object) {
        for (RegisteredImplementation registeredImplementation : object) {
            for (ServiceReference<?> reference : registeredImplementation.getReferences()) {
                apiBundleContext.ungetService(reference);
            }
            for (ServiceRegistration<?> registration :
                    registeredImplementation.getRegistrations()) {
                registration.unregister();
            }
            synchronized (this) {
                implementations.remove(registeredImplementation.getImplementation());
            }
        }
        synchronized (this) {
            loadImplementations(implementations);
        }
    }
}
