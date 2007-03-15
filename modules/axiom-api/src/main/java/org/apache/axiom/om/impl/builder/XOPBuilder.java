package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMException;

import javax.activation.DataHandler;

public interface XOPBuilder {

    DataHandler getDataHandler(String blobContentID)
            throws OMException;

}