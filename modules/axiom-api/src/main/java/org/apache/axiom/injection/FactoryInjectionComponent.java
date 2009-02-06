package org.apache.axiom.injection;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @scr.component name="factoryinjection.component" immediate="true"
 * @scr.reference name="omfactory" interface="org.apache.axiom.om.OMFactory" cardinality="0..n" policy="dynamic" bind="setOMFactory" unbind="unsetOMFactory"
 * @scr.reference name="soap12factory" interface="org.apache.axiom.soap.SOAPFactory" target="(axiom.soapVersion=*soap12*)" cardinality="0..n" policy="dynamic" bind="setSOAP12Factory" unbind="unsetSOAP12Factory"
 * @scr.reference name="soap11factory" interface="org.apache.axiom.soap.SOAPFactory" target="(axiom.soapVersion=*soap11*)" cardinality="0..n" policy="dynamic" bind="setSOAP11Factory" unbind="unsetSOAP11Factory"
 */
public class FactoryInjectionComponent {

	private static final Log log = LogFactory
			.getLog(FactoryInjectionComponent.class);

	public FactoryInjectionComponent() {
		if (log.isDebugEnabled()) {
			log.debug("FactoryInjectionComponent created");
		}
	}

	private static List omFactories = null;
	private static OMFactory currentOMFactory = null;
	
	private static List soap11Factories = null;
	private static SOAPFactory currentSOAP11Factory = null;
	
	private static List soap12Factories = null;
	private static SOAPFactory currentSOAP12Factory = null;

	protected void setOMFactory(OMFactory omfactory) {
		synchronized (FactoryInjectionComponent.class) {
			if (omFactories == null) {
				omFactories = new ArrayList();
			}
			// Special case llom - it's the default
			if (omfactory.getClass().toString().contains("llom")) {
				omFactories.add(0, omfactory);
			} else {
				omFactories.add(omfactory);
			}
			currentOMFactory = (OMFactory) omFactories.get(0);
		}
	}

	protected void unsetOMFactory(OMFactory omfactory) {
		synchronized (FactoryInjectionComponent.class) {
			if (omFactories != null) {
				omFactories.remove(omfactory);
			}
			if (omFactories.size() == 0) {
				omFactories = null;
			} else {
				currentOMFactory = (OMFactory) omFactories.get(0);
			}
		}
	}

	public static OMFactory getOMFactory() {
		return currentOMFactory;
	}

	protected void setSOAP12Factory(SOAPFactory soapfactory) {
		synchronized (FactoryInjectionComponent.class) {
			if (soap12Factories == null) {
				soap12Factories = new ArrayList();
			}
			// Special case llom - it's the default
			if (soapfactory.getClass().toString().contains("llom")) {
				soap12Factories.add(0, soapfactory);
			} else {
				soap12Factories.add(soapfactory);
			}
			currentSOAP12Factory = (SOAPFactory) soap12Factories.get(0);
		}
	}

	protected void unsetSOAP12Factory(SOAPFactory soapfactory) {
		synchronized (FactoryInjectionComponent.class) {
			if (soap12Factories != null) {
				soap12Factories.remove(soapfactory);
			}
			if (soap12Factories.size() == 0) {
				soap12Factories = null;
			} else {
				currentSOAP12Factory = (SOAPFactory) soap12Factories.get(0);
			}
		}
	}
	
	public static SOAPFactory getSOAP12Factory() {
		return currentSOAP12Factory;
	}

	protected void setSOAP11Factory(SOAPFactory soapfactory) {
		synchronized (FactoryInjectionComponent.class) {
			if (soap11Factories == null) {
				soap11Factories = new ArrayList();
			}
			// Special case llom - it's the default
			if (soapfactory.getClass().toString().contains("llom")) {
				soap11Factories.add(0, soapfactory);
			} else {
				soap11Factories.add(soapfactory);
			}
			currentSOAP11Factory = (SOAPFactory) soap11Factories.get(0);
		}
	}

	protected void unsetSOAP11Factory(SOAPFactory soapfactory) {
		synchronized (FactoryInjectionComponent.class) {
			if (soap11Factories != null) {
				soap11Factories.remove(soapfactory);
			}
			if (soap11Factories.size() == 0) {
				soap11Factories = null;
			} else {
				currentSOAP11Factory = (SOAPFactory) soap11Factories.get(0);
			}
		}
	}
	
	public static SOAPFactory getSOAP11Factory() {
		return currentSOAP11Factory;
	}
}
