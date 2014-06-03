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
package org.apache.axiom.spring.ws.test;

import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

public abstract class ScenarioTestCase extends MatrixTestCase {
    private final ScenarioConfig config;
    private Server server;
    protected GenericXmlApplicationContext context;
    
    public ScenarioTestCase(ScenarioConfig config, String soapVersion) {
        this.config = config;
        addTestParameter("client", config.getClientMessageFactoryConfigurator().getName());
        addTestParameter("server", config.getServerMessageFactoryConfigurator().getName());
        addTestParameter("soapVersion", soapVersion);
    }
    
    @Override
    protected void setUp() throws Exception {
        server = new Server();
        
        // Set up a custom thread pool to improve thread names (for logging purposes)
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setName("jetty");
        server.setThreadPool(threadPool);
        
        Connector connector = new SelectChannelConnector();
        connector.setPort(0);
        server.setConnectors(new Connector[] { connector });
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setContextClass(GenericWebApplicationContext.class);
        servlet.setContextInitializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
            public void initialize(ConfigurableApplicationContext applicationContext) {
                configureContext((GenericWebApplicationContext)applicationContext, config.getServerMessageFactoryConfigurator(), "server.xml");
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
        configureContext(context, config.getClientMessageFactoryConfigurator(), "client.xml");
        context.refresh();
    }
    
    void configureContext(GenericApplicationContext context, MessageFactoryConfigurator messageFactoryConfigurator, String relativeConfigLocation) {
        context.getEnvironment().getPropertySources().addFirst(new MatrixTestCasePropertySource(this));
        messageFactoryConfigurator.configure(context);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(
                new ClassPathResource("common.xml", ScenarioTestCase.class),
                new ClassPathResource(relativeConfigLocation, getClass()));
    }
    
    @Override
    protected void tearDown() throws Exception {
        context.close();
        context = null;
        
        server.stop();
        server = null;
    }
}
