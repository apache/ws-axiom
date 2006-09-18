package org.apache.axiom.om.util;

import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.SimpleNamespaceContext;

import java.util.List;

public class XPathEvaluator {

    public List evaluateXpath(String xpathExpression, Object element, String nsURI) throws Exception{
        AXIOMXPath xpath = new AXIOMXPath(xpathExpression);
        if (nsURI!=null){
            SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
            nsContext.addNamespace(null,nsURI);
            xpath.setNamespaceContext(nsContext);
        }
        return xpath.selectNodes(element);
    }


}
