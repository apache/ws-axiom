package org.apache.axiom.ts.dom.document;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TestGetXmlVersionFromParsedDocumentWithoutDeclaration extends DOMTestCase {
    public TestGetXmlVersionFromParsedDocumentWithoutDeclaration(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader("<root/>")));
        assertThat(doc.getXmlVersion()).isEqualTo("1.0");
    }
}
