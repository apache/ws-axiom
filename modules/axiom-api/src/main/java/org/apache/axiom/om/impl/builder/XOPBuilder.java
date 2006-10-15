package org.apache.axiom.om.impl.builder;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMException;

public interface XOPBuilder {

	public abstract DataHandler getDataHandler(String blobContentID)
			throws OMException;

}