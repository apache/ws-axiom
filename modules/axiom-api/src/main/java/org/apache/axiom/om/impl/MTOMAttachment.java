package org.apache.axiom.om.impl;

import javax.activation.DataHandler;


/**
 * Interface of an MTOM attachment, as processed by
 * {@link MTOMXMLStreamWriter#writeOptimized(org.apache.axiom.om.OMText)}
 * and {@link MIMEOutputUtils#complete(java.io.OutputStream, java.io.StringWriter, java.util.LinkedList, String, String, String, String)}.
 */
public interface MTOMAttachment {
    /**
     * Returns the attachments ID.
     */
    String getContentID();
    /**
     * Returns the attachments contents.
     */
    DataHandler getDataHandler();
}
