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
import java.util.Iterator;
import java.util.List;

import org.apache.axiom.om.OMMetaFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTrackerCustomizer;

final class OSGiOMMetaFactoryLocator extends PriorityBasedOMMetaFactoryLocator implements BundleTrackerCustomizer {
    private final BundleContext apiBundleContext;
    private final List/*<Implementation>*/ implementations = new ArrayList();
    
    OSGiOMMetaFactoryLocator(BundleContext apiBundleContext) {
        this.apiBundleContext = apiBundleContext;
    }

    // Need to synchronize access because the implementations may be reloaded concurrently
    public synchronized OMMetaFactory getOMMetaFactory(String feature) {
        return super.getOMMetaFactory(feature);
    }

    public Object addingBundle(Bundle bundle, BundleEvent event) {
        URL descriptorUrl = bundle.getEntry(ImplementationFactory.DESCRIPTOR_RESOURCE);
        if (descriptorUrl != null) {
            List/*<Implementation>*/ discoveredImplementations = ImplementationFactory.parseDescriptor(new OSGiLoader(bundle), descriptorUrl);
            List/*<RegisteredImplementation>*/ registeredImplementations = new ArrayList(discoveredImplementations.size());
            synchronized (this) {
                implementations.addAll(discoveredImplementations);
                loadImplementations(implementations);
            }
            for (Iterator it = discoveredImplementations.iterator(); it.hasNext(); ) {
                Implementation implementation = (Implementation)it.next();
                Hashtable properties = new Hashtable();
                properties.put("implementationName", implementation.getName());
                // TODO: we should add the features and priorities to the properties as well
                ServiceRegistration registration = bundle.getBundleContext().registerService(OMMetaFactory.class.getName(), implementation.getMetaFactory(), properties);
                ServiceReference reference = registration.getReference();
                // Let the OSGi runtime know that the axiom-api bundle is using the service
                apiBundleContext.getService(reference);
                registeredImplementations.add(new RegisteredImplementation(implementation, registration, reference));
            }
            return registeredImplementations;
        } else {
            return null;
        }
    }

    public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
    }

    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        if (object != null) {
            for (Iterator it = ((List)object).iterator(); it.hasNext(); ) {
                RegisteredImplementation registeredImplementation = (RegisteredImplementation)it.next();
                apiBundleContext.ungetService(registeredImplementation.getReference());
                registeredImplementation.getRegistration().unregister();
                synchronized (this) {
                    implementations.remove(registeredImplementation.getImplementation());
                }
            }
            synchronized (this) {
                loadImplementations(implementations);
            }
        }
    }
}
