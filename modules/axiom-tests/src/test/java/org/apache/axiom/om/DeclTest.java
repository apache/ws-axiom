package org.apache.axiom.om;

import org.custommonkey.xmlunit.XMLTestCase;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class DeclTest extends XMLTestCase {

    static OMFactory factory = OMAbstractFactory.getOMFactory();

    static OMDocument model = factory.createOMDocument();

    static {
        OMElement root = factory.createOMElement("root", null);
        model.addChild(root);
    }

    private OMElement getElement(String name) {
        OMNamespace ns1 = factory.createOMNamespace("axiom:declaration-test,2007:1", "test");
        OMElement elem = factory.createOMElement(name, ns1);
        return elem;
    }


    private void writeModel(OutputStream os) throws XMLStreamException {
        model.serialize(os);
    }

    public void testDecl() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        DeclTest app = new DeclTest();
        model.getOMDocumentElement().addChild(app.getElement("foo"));
        model.getOMDocumentElement().addChild(app.getElement("bar"));
        model.getOMDocumentElement().addChild(app.getElement("foo"));
        app.writeModel(baos);

        String xmlExpected = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><test:foo xmlns:test=\"axiom:declaration-test,2007:1\"></test:foo><test:bar xmlns:test=\"axiom:declaration-test,2007:1\"></test:bar><test:foo xmlns:test=\"axiom:declaration-test,2007:1\"></test:foo></root>";
        this.assertXMLEqual(new InputStreamReader(new ByteArrayInputStream(xmlExpected.getBytes())),
                new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));

    }
}
