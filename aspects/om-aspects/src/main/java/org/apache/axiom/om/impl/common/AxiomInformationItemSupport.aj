package org.apache.axiom.om.impl.common;

import org.apache.axiom.core.NodeFactory;

public aspect AxiomInformationItemSupport {
    public final NodeFactory AxiomInformationItem.coreGetNodeFactory() {
        return (NodeFactory)getOMFactory();
    }
}
