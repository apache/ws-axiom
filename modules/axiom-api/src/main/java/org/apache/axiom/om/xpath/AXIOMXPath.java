package org.apache.axiom.om.xpath;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

import java.util.HashMap;
import java.util.Map;

public class AXIOMXPath extends BaseXPath {

    private static final long serialVersionUID = -5839161412925154639L;

    private Map namespaces = new HashMap();

    /**
     * Construct given an XPath expression string.
     *
     * @param xpathExpr the XPath expression.
     * @throws org.jaxen.JaxenException if there is a syntax error while parsing the expression
     */
    public AXIOMXPath(String xpathExpr) throws JaxenException {
        super(xpathExpr, new DocumentNavigator());
    }

    /**
     * This override captures any added namespaces, as the Jaxen BaseXPath class nor
     * NamespaceContext (or SimpleNamespaceContext) exposes thier internal map of the prefixes to
     * the namespaces. This method - although is not the ideal solution to the issue, attempts to
     * provide an override to changing the Jaxen code.
     *
     * @param prefix a namespace prefix
     * @param uri    the URI to which the prefix matches
     * @throws JaxenException if the underlying implementation throws an exception
     */
    public void addNamespace(String prefix, String uri) throws JaxenException {
        try {
            super.addNamespace(prefix, uri);
        } catch (JaxenException e) {
            // the intention here is to prevent us caching a namespace, if the
            // underlying implementation does not accept it
            throw e;
        }
        namespaces.put(prefix, uri);
    }

    /**
     * Expose the prefix to namespace mapping for this expression
     *
     * @return a Map of namespace prefixes to the URIs
     */
    public Map getNamespaces() {
        return namespaces;
    }
}
