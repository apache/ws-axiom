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
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

public abstract class ScenarioTestCase extends MatrixTestCase {
    private Server server;
    protected GenericXmlApplicationContext context;
    
    public ScenarioTestCase(String soapVersion) {
        addTestParameter("soapVersion", soapVersion);
    }
    
    @Override
    @SuppressWarnings("serial")
    protected void setUp() throws Exception {
        final MatrixTestCasePropertySource testParameters = new MatrixTestCasePropertySource(this);
        
        server = new Server();
        
        // Set up a custom thread pool to improve thread names (for logging purposes)
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setName("jetty");
        server.setThreadPool(threadPool);
        
        Connector connector = new SelectChannelConnector();
        connector.setPort(0);
        server.setConnectors(new Connector[] { connector });
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        ServletHolder servlet = new ServletHolder(new MessageDispatcherServlet() {
            @Override
            protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
                wac.getEnvironment().getPropertySources().addFirst(testParameters);
            }
        });
        servlet.setName("spring-ws");
        servlet.setInitParameter("contextConfigLocation", getClass().getResource("server.xml").toString());
        servlet.setInitOrder(1);
        handler.addServlet(servlet, "/*");
        server.start();
        
        context = new GenericXmlApplicationContext();
        ConfigurableEnvironment environment = context.getEnvironment();
        MockPropertySource propertySource = new MockPropertySource();
        propertySource.setProperty("port", connector.getLocalPort());
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertySource);
        propertySources.addFirst(testParameters);
        context.load(getClass(), "client.xml");
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
