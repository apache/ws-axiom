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
package org.apache.axiom.ts.springws.scenario;

import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.springws.SpringWSTestCase;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

public abstract class ScenarioTestCase extends SpringWSTestCase {
    private final ScenarioConfig config;
    private Server server;
    protected GenericXmlApplicationContext context;
    
    public ScenarioTestCase(ScenarioConfig config, SOAPSpec spec) {
        super(spec);
        this.config = config;
        addTestParameter("client", config.getClientMessageFactoryConfigurator().getName());
        addTestParameter("server", config.getServerMessageFactoryConfigurator().getName());
    }
    
    @Override
    protected void setUp() throws Exception {
        // Set up a custom thread pool to improve thread names (for logging purposes)
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setName("jetty");
        server = new Server(threadPool);
        
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.setConnectors(new Connector[] { connector });
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setContextClass(GenericWebApplicationContext.class);
        servlet.setContextInitializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
            @Override
            public void initialize(ConfigurableApplicationContext applicationContext) {
                configureContext((GenericWebApplicationContext)applicationContext, config.getServerMessageFactoryConfigurator(),
                        new ClassPathResource("server.xml", ScenarioTestCase.this.getClass()));
            }
        });
        ServletHolder servletHolder = new ServletHolder(servlet);
        servletHolder.setName("spring-ws");
        servletHolder.setInitOrder(1);
        handler.addServlet(servletHolder, "/*");
        server.start();
        
        context = new GenericXmlApplicationContext();
        MockPropertySource propertySource = new MockPropertySource("client-properties");
        propertySource.setProperty("port", connector.getLocalPort());
        context.getEnvironment().getPropertySources().replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertySource);
        configureContext(context, config.getClientMessageFactoryConfigurator(), new ClassPathResource("client.xml", getClass()));

        context.refresh();
    }
    
    @Override
    protected void tearDown() throws Exception {
        context.close();
        context = null;
        
        server.stop();
        server = null;
    }
}
