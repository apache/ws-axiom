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
package org.apache.axiom.spring.ws.test.jdom;

import org.apache.axiom.spring.ws.test.MatrixTestCasePropertySource;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jdom2.input.SAXBuilder;
import org.jdom2.transform.JDOMResult;
import org.jdom2.transform.JDOMSource;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

public class ClientServerTest extends MatrixTestCase {
    private Server server;
    private GenericXmlApplicationContext context;
    
    public ClientServerTest(String soapVersion) {
        addTestParameter("soapVersion", soapVersion);
    }
    
    @Override
    @SuppressWarnings("serial")
    protected void setUp() throws Exception {
        final MatrixTestCasePropertySource testParameters = new MatrixTestCasePropertySource(this);
        
        server = new Server();
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
        servlet.setInitParameter("contextConfigLocation", ClientServerTest.class.getResource("spring-ws-servlet.xml").toString());
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
        context.load(ClientServerTest.class, "beans.xml");
        context.refresh();
    }
    
    @Override
    protected void tearDown() throws Exception {
        context.close();
        context = null;
        
        server.stop();
        server = null;
    }
    
    @Override
    protected void runTest() throws Throwable {
        JDOMSource source = new JDOMSource(new SAXBuilder().build(ClientServerTest.class.getResourceAsStream("request.xml")).getRootElement());
        JDOMResult result = new JDOMResult();
        context.getBean(WebServiceTemplate.class).sendSourceAndReceiveToResult(source, result);
        assertEquals(8.0d, Double.parseDouble(result.getDocument().getRootElement().getText()), 1e-6);
    }
}
