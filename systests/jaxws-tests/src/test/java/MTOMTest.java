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
import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.example.ImageService;
import org.example.ImageServicePort;
import org.junit.Test;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.soap.MTOMFeature;

public class MTOMTest {
    /**
     * Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-492">AXIOM-492</a>.
     *
     * @throws Exception
     */
    @Test
    public void testSOAP12() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.setConnectors(new Connector[] {connector});
        ServletContextHandler handler = new ServletContextHandler("/");
        HttpServlet servlet =
                new HttpServlet() {
                    @Override
                    protected void doPost(HttpServletRequest request, HttpServletResponse response)
                            throws ServletException, IOException {
                        MultipartBody mp =
                                MultipartBody.builder()
                                        .setInputStream(request.getInputStream())
                                        .setContentType(request.getContentType())
                                        .build();
                        OMXMLBuilderFactory.createSOAPModelBuilder(mp);
                        SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
                        SOAPMessage message = factory.createDefaultSOAPMessage();
                        OMElement responseElement =
                                factory.createOMElement(
                                        "uploadImageResponse",
                                        factory.createOMNamespace("http://example.org/", "ns"),
                                        message.getSOAPEnvelope().getBody());
                        factory.createOMElement("return", null, responseElement).setText("OK");
                        response.setContentType(factory.getSOAPVersion().getMediaType().toString());
                        try {
                            message.serialize(response.getOutputStream());
                        } catch (XMLStreamException ex) {
                            throw new ServletException(ex);
                        }
                    }
                };
        ServletHolder servletHolder = new ServletHolder(servlet);
        servletHolder.setName("test");
        servletHolder.setInitOrder(1);
        handler.addServlet(servletHolder, "/");
        server.setHandler(handler);
        server.start();
        try {
            ImageServicePort imageService =
                    new ImageService().getImageServicePort(new MTOMFeature());
            ((BindingProvider) imageService)
                    .getRequestContext()
                    .put(
                            BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                            String.format("http://localhost:%d/", connector.getLocalPort()));
            assertThat(imageService.uploadImage(new byte[4096])).isEqualTo("OK");
        } finally {
            server.stop();
        }
    }
}
