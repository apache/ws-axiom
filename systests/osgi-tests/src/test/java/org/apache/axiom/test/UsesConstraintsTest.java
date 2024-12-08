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
package org.apache.axiom.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ops4j.pax.exam.CoreOptions.streamBundle;
import static org.ops4j.pax.tinybundles.TinyBundles.bundle;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.FRAMEWORK_STORAGE;
import static org.osgi.framework.Constants.FRAMEWORK_STORAGE_CLEAN;
import static org.osgi.framework.Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT;
import static org.osgi.framework.Constants.IMPORT_PACKAGE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * Tests that the Axiom bundles have {@code Export-Package} directives with the appropriate uses
 * constraints so that the OSGi runtime can guarantee that packages are wired consistently. This is
 * a regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-457">AXIOM-457</a>.
 *
 * <p>The test installs two test bundles, one importing the {@code javax.xml.stream} package from
 * the framework bundle (i.e. the JRE) and the other importing the same package from the ServiceMix
 * StAX API bundle. Both bundles also import {@code org.apache.axiom.om}. Since the StAX API is used
 * in the public Axiom API, this would lead to a class loading issue (reported as a {@link
 * LinkageError} at runtime). With the appropriate uses constraints, the OSGi runtime should detect
 * this when the bundles are resolved. The test registers a {@link FrameworkListener} to check that
 * the expected error is generated.
 */
public class UsesConstraintsTest {
    static class Listener implements FrameworkListener {
        private final Pattern regex =
                Pattern.compile(
                        "Uses constraint violation\\. Unable to resolve .* testbundle. \\[.*\\] because it is exposed to "
                                + "package 'javax\\.xml\\.stream' .* via two dependency chains\\."
                                + ".*package=org\\.apache\\.axiom\\.om.*",
                        Pattern.DOTALL);
        private final CountDownLatch latch = new CountDownLatch(1);
        private boolean gotExpectedError;

        @Override
        public void frameworkEvent(FrameworkEvent event) {
            switch (event.getType()) {
                case FrameworkEvent.STARTED:
                    latch.countDown();
                    break;
                case FrameworkEvent.ERROR:
                    if (regex.matcher(event.getThrowable().getMessage()).matches()) {
                        System.out.println("Got expected");
                        gotExpectedError = true;
                    }
            }
        }

        void awaitStart() throws InterruptedException {
            latch.await(10, TimeUnit.SECONDS);
        }

        boolean gotExpectedError() {
            return gotExpectedError;
        }
    }

    @Test
    public void test() throws Exception {
        System.setProperty("java.protocol.handler.pkgs", "org.ops4j.pax.url");
        Map<String, String> p = new HashMap<String, String>();
        p.put(FRAMEWORK_STORAGE, new File("target/felix").getAbsolutePath());
        p.put(FRAMEWORK_STORAGE_CLEAN, FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
        FrameworkFactory frameworkFactory = new org.apache.felix.framework.FrameworkFactory();
        Framework framework = frameworkFactory.newFramework(p);
        framework.init();
        BundleContext context = framework.getBundleContext();
        Listener listener = new Listener();
        context.addFrameworkListener(listener);
        List<Bundle> bundles = new ArrayList<Bundle>();
        bundles.add(
                context.installBundle(
                        "link:classpath:META-INF/links/org.ops4j.pax.logging.api.link"));
        bundles.add(
                context.installBundle(
                        "link:classpath:org.apache.aries.spifly.dynamic.framework.extension.link"));
        bundles.add(
                context.installBundle(
                        "link:classpath:org.apache.servicemix.specs.stax-api-1.0.link"));
        bundles.add(context.installBundle("link:classpath:stax2-api.link"));
        bundles.add(
                context.installBundle("link:classpath:com.fasterxml.woodstox.woodstox-core.link"));
        bundles.add(context.installBundle("link:classpath:org.apache.commons.commons-io.link"));
        bundles.add(
                context.installBundle("link:classpath:org.apache.james.apache-mime4j-core.link"));
        bundles.add(
                context.installBundle("link:classpath:org.apache.ws.commons.axiom.axiom-api.link"));
        // This bundle will be wired to the javax.xml.stream package exported by the ServiceMix
        // stax-api bundle.
        bundles.add(
                context.installBundle(
                        streamBundle(
                                        bundle().setHeader(BUNDLE_SYMBOLICNAME, "testbundle1")
                                                .setHeader(
                                                        IMPORT_PACKAGE,
                                                        "org.apache.axiom.om, javax.xml.stream; version=1.0")
                                                .build())
                                .getURL()));
        // This bundle will be wired to the javax.xml.stream package exported by the framework
        // bundle
        bundles.add(
                context.installBundle(
                        streamBundle(
                                        bundle().setHeader(BUNDLE_SYMBOLICNAME, "testbundle2")
                                                .setHeader(
                                                        IMPORT_PACKAGE,
                                                        "org.apache.axiom.om, javax.xml.stream; version=\"[0.0.0,1.0)\"")
                                                .build())
                                .getURL()));
        for (Bundle bundle : bundles) {
            bundle.start();
        }
        framework.start();
        try {
            listener.awaitStart();
            assertThat(listener.gotExpectedError()).isTrue();
        } finally {
            framework.stop();
        }
    }
}
