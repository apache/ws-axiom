package org.apache.axiom.om.impl;

import javax.activation.DataHandler;


/**
 * Default implementation of {@link MTOMAttachment}.
 */
public class DefaultMTOMAttachment implements MTOMAttachment {
    private final String id;
    private final DataHandler dataHandler;

    /**
     * Creates a new instance with the given data and content ID.
     */
    public DefaultMTOMAttachment(DataHandler pDataHandler, String pId) {
        dataHandler = pDataHandler;
        id = pId;
    }

    public String getContentID() {
        return id;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }
}
