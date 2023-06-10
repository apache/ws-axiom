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
package org.apache.axiom.mime.activation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;

import jakarta.activation.DataSource;

import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.mime.PartBlob;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.soap.MTOMSample;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

public class PartDataHandlerBlobFactoryTest {
    @Test
    public void testDefaultImplementation() throws Exception {
        MTOMSample testMessage = MTOMSample.SAMPLE1;
        MultipartBody mb =
                MultipartBody.builder()
                        .setInputStream(testMessage.getInputStream())
                        .setContentType(testMessage.getContentType())
                        .setPartBlobFactory(PartDataHandlerBlobFactory.DEFAULT)
                        .build();
        PartBlob blob =
                (PartBlob)
                        ((OMText)
                                        OMXMLBuilderFactory.createSOAPModelBuilder(mb)
                                                .getSOAPEnvelope()
                                                .getBody()
                                                .getFirstElement()
                                                .getFirstElement()
                                                .getFirstOMChild())
                                .getBlob();
        assertThat(blob).isInstanceOf(PartDataHandlerBlob.class);
        PartDataHandler dh = ((PartDataHandlerBlob) blob).getDataHandler();
        assertThat(dh.getPart()).isSameAs(blob.getPart());
        assertThat(dh.getBlob()).isSameAs(blob);
        assertThat(dh.getContentType()).isEqualTo("image/jpeg");
        DataSource ds = dh.getDataSource();
        assertThat(ds.getContentType()).isEqualTo("image/jpeg");
        byte[] expectedContent = IOUtils.toByteArray(testMessage.getPart(1));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        dh.writeTo(out);
        assertThat(out.toByteArray()).isEqualTo(expectedContent);
        assertThat(dh.getInputStream()).hasBinaryContent(expectedContent);
        assertThat(ds.getInputStream()).hasBinaryContent(expectedContent);
    }
}
