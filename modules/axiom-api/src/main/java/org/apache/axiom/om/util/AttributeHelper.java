/**
 * 
 */
package org.apache.axiom.om.util;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

/**
 * Helper class for attributes.
 */
public class AttributeHelper {
    /**
    * In Axiom, a single tree should always contain objects created from the same type
    * of factory (eg: LinkedListImplFactory, DOMFactory, etc.,). This method will convert
    * omAttribute to the given omFactory.
    * 
    * @see ElementHelper#importOMElement(OMElement, OMFactory) to convert instances of OMElement
    */
    public static OMAttribute importOMAttribute(OMAttribute omAttribute, OMFactory omFactory) {
        // first check whether the given OMAttribute has the same OMFactory
        if (omAttribute.getOMFactory().getClass().isInstance(omFactory)) {
            return omAttribute;
        }else {
            OMElement omElement = omAttribute.getOMFactory().createOMElement("localName", "namespace", "prefix");
            omElement.addAttribute(omAttribute);
            OMElement documentElement = new StAXOMBuilder(omFactory, omElement.getXMLStreamReader()).getDocumentElement();
            documentElement.build();
            return (OMAttribute) documentElement.getAllAttributes().next();
        }
    }
}
