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

import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactoryLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.BundleTracker;

/**
 * The OSGi bundle activator for the {@code axiom-api} bundle. This activator sets up an OSGi
 * specific {@link OMMetaFactoryLocator} and injects it into {@link OMAbstractFactory} using {@link
 * OMAbstractFactory#setMetaFactoryLocator(OMMetaFactoryLocator)}.
 */
public class Activator implements BundleActivator {
    private static final Log log = LogFactory.getLog(Activator.class);

    private BundleTracker<List<RegisteredImplementation>> tracker;

    @Override
    public void start(BundleContext context) throws Exception {
        OSGiOMMetaFactoryLocator locator = new OSGiOMMetaFactoryLocator(context);
        OMAbstractFactory.setMetaFactoryLocator(locator);
        // Bundle.STARTING covers the case where the implementation bundle has
        // "Bundle-ActivationPolicy: lazy".
        tracker =
                new BundleTracker<List<RegisteredImplementation>>(
                        context, Bundle.STARTING | Bundle.ACTIVE, locator);
        tracker.open();
        log.debug("OSGi support enabled");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        tracker.close();
        OMAbstractFactory.setMetaFactoryLocator(null);
        log.debug("OSGi support disabled");
    }
}
