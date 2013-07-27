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

import static org.junit.Assert.assertEquals;

import org.apache.axiom.testutils.PortAllocator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jdom2.input.SAXBuilder;
import org.jdom2.transform.JDOMResult;
import org.jdom2.transform.JDOMSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

public class ClientServerTest {
    private static Server server;
    private static int port;
    private static GenericXmlApplicationContext context;
    
    @BeforeClass
    public static void setUp() throws Exception {
        port = PortAllocator.allocatePort();
        server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        ServletHolder servlet = new ServletHolder(MessageDispatcherServlet.class);
        servlet.setName("spring-ws");
        servlet.setInitParameter("contextConfigLocation", ClientServerTest.class.getResource("spring-ws-servlet.xml").toString());
        servlet.setInitOrder(1);
        handler.addServlet(servlet, "/*");
        server.start();
        
        context = new GenericXmlApplicationContext();
        ConfigurableEnvironment environment = context.getEnvironment();
        MockPropertySource propertySource = new MockPropertySource();
        propertySource.setProperty("port", port);
        environment.getPropertySources().replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertySource);
        context.load(ClientServerTest.class, "beans.xml");
        context.refresh();
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        context.close();
        
        server.stop();
    }
    
    @Test
    public void test() throws Exception {
        JDOMSource source = new JDOMSource(new SAXBuilder().build(ClientServerTest.class.getResourceAsStream("request.xml")).getRootElement());
        JDOMResult result = new JDOMResult();
        context.getBean(WebServiceTemplate.class).sendSourceAndReceiveToResult(source, result);
        assertEquals(8.0d, Double.parseDouble(result.getDocument().getRootElement().getText()), 1e-6);
    }
}
