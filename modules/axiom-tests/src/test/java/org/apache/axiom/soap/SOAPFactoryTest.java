package org.apache.axiom.soap;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class SOAPFactoryTest extends TestCase {

    protected static final String SOAP11_FILE_NAME = "test-resources/soap/soap11/soap11message.xml";
    protected static final String SOAP12_FILE_NAME = "test-resources/soap/soap12message.xml";
    private Log log = LogFactory.getLog(getClass());

    public void testSOAPFactory() {

        try {
            SOAPEnvelope soapEnvelope = (SOAPEnvelope) new StAXSOAPModelBuilder(XMLInputFactory.newInstance().
                    createXMLStreamReader(new FileInputStream(SOAP11_FILE_NAME)), null).getDocumentElement();
            assertTrue(soapEnvelope != null);

            soapEnvelope = (SOAPEnvelope) new StAXSOAPModelBuilder(XMLInputFactory.newInstance().
                    createXMLStreamReader(new FileInputStream(SOAP12_FILE_NAME)), null).getDocumentElement();
            assertTrue(soapEnvelope != null);
        } catch (XMLStreamException e) {
            fail("Can not load soap envelope. Exception = "+ e);
        } catch (FileNotFoundException e) {
            fail("Given XML can not be found. Exception =  "+ e);
        }

    }

}
