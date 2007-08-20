package org.apache.axiom.soap;

import javax.xml.stream.XMLStreamException;

/**
 * @author : Ajith Ranabahu
 *         Date: Aug 15, 2007
 *         Time: 11:57:54 PM
 */
public class SOAPDiscardTest  extends SOAPTestCase{


    public SOAPDiscardTest(String testName) {
        super(testName);
    }

    public void testDiscardHeader(){

        try {
            soap11EnvelopeWithParser.getHeader().discard();
            soap11EnvelopeWithParser.getBody().toStringWithConsume();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
