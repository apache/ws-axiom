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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactoryLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.BundleTracker;

/**
 * The OSGi bundle activator for the <tt>axiom-api</tt> bundle. This activator sets up an OSGi
 * specific {@link OMMetaFactoryLocator} and inject it into {@link OMAbstractFactory} using
 * {@link OMAbstractFactory#setMetaFactoryLocator(OMMetaFactoryLocator)}.
 */
public class Activator implements BundleActivator {
    private BundleTracker tracker;

    public void start(BundleContext context) throws Exception {
        OSGiOMMetaFactoryLocator locator = new OSGiOMMetaFactoryLocator(context);
        OMAbstractFactory.setMetaFactoryLocator(locator);
        tracker = new BundleTracker(context, Bundle.ACTIVE, locator);
        tracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        tracker.close();
        OMAbstractFactory.setMetaFactoryLocator(null);
    }
}
