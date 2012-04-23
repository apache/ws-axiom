/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axiom.util.stax.debug;

import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.util.Stack;

// TODO: this needs reformatting; the (generated) Javadoc is unreadable!
/**
 * There are several places in the code where events are passed from 
 * a source to a consumer using XMLStreamReader events. 
 * 
 *     XMLStreamReader (impl)--> consumer of XMLStreamReader events
 * 
 * This simple class can be interjected as a filter and used to do some simple validation.
 * Validating the events coming from source (impl) can help find and correct errors 
 * when they occur.  Otherwise the errors may be caught much further downstream and hard to fix.
 * 
 *    XMLStreamReader (impl)--> XMLStreamReaderValiator-> consumer of XMLStreamReader events
 * 
 * 
 * In the initial version, the XMStreamValidator ensures that the start element events match the 
 * end element events.
 */
public class XMLStreamReaderValidator extends XMLStreamReaderWrapper {

    private static final Log log = LogFactory.getLog(XMLStreamReaderValidator.class);
    private static boolean IS_ADV_DEBUG_ENABLED = false;  // Turn this on to trace every event
    
    private boolean throwExceptions = false;   // Indicates whether OMException should be thrown if errors are disovered
    private Stack stack = new Stack();         // Stack keeps track of the nested element QName
    

    /**
     * @param delegate XMLStreamReader to validate
     * @param throwExceptions (true if exceptions should be thrown when errors are encountered)
     */
    public XMLStreamReaderValidator(XMLStreamReader delegate, boolean throwExceptions) {
        super(delegate);
        this.throwExceptions = throwExceptions;
    }

 
    public int next() throws XMLStreamException {
        int event = super.next();
        trackEvent(event);
        return event;
    }
    
    public String getElementText() throws XMLStreamException {
        String text = super.getElementText();
        trackEvent(END_ELEMENT);
        return text;
    }

    public int nextTag() throws XMLStreamException {
        int event = super.nextTag();
        trackEvent(event);
        return event;
    }

    private void trackEvent(int event) throws XMLStreamException {
        logParserState();
        
        // Make sure that the start element and end element events match.
        // Mismatched events are a key indication that the delegate stream reader is 
        // broken or corrupted.
        switch (event) {
        case XMLStreamConstants.START_ELEMENT:
            stack.push(super.getName());
            break;
        case XMLStreamConstants.END_ELEMENT:
            QName delegateQName = super.getName();
            if (stack.isEmpty()) {
                reportMismatchedEndElement(null, delegateQName);
            } else {
                QName expectedQName = (QName) stack.pop();
                
                if (!expectedQName.equals(delegateQName)) {
                    reportMismatchedEndElement(expectedQName, delegateQName);
                }
            }
            break;
            
        default :
        
        }
    }
    
    
    /**
     * Report a mismatched end element.
     * @param expectedQName
     * @param delegateQName
     * @throws XMLStreamException 
     */
    private void reportMismatchedEndElement(QName expectedQName, QName delegateQName) throws XMLStreamException {
        String text = null;
        if (expectedQName == null) {
            text = "An END_ELEMENT event for " + delegateQName + 
                " was encountered, but the START_ELEMENT stack is empty.";
        } else {
            text = "An END_ELEMENT event for " + delegateQName + 
                " was encountered, but this doesn't match the corresponding START_ELEMENT " + 
                expectedQName + " event.";
        }
        log.debug(text);
        if (throwExceptions) {
            throw new XMLStreamException(text);
        }
    }
    
    /**
     * Dump the current event of the delegate.
     */
    protected void logParserState() {
        if (IS_ADV_DEBUG_ENABLED) {
            int currentEvent = super.getEventType();
            
            switch (currentEvent) {
            case XMLStreamConstants.START_ELEMENT:
                log.trace("START_ELEMENT: ");
                log.trace("  QName: " + super.getName());
                break;
            case XMLStreamConstants.START_DOCUMENT:
                log.trace("START_DOCUMENT: ");
                break;
            case XMLStreamConstants.CHARACTERS:
                log.trace("CHARACTERS: ");
                log.trace(   "[" + super.getText() + "]");
                break;
            case XMLStreamConstants.CDATA:
                log.trace("CDATA: ");
                log.trace(   "[" + super.getText() + "]");
                break;
            case XMLStreamConstants.END_ELEMENT:
                log.trace("END_ELEMENT: ");
                log.trace("  QName: " + super.getName());
                break;
            case XMLStreamConstants.END_DOCUMENT:
                log.trace("END_DOCUMENT: ");
                break;
            case XMLStreamConstants.SPACE:
                log.trace("SPACE: ");
                log.trace(   "[" + super.getText() + "]");
                break;
            case XMLStreamConstants.COMMENT:
                log.trace("COMMENT: ");
                log.trace(   "[" + super.getText() + "]");
                break;
            case XMLStreamConstants.DTD:
                log.trace("DTD: ");
                log.trace(   "[" + super.getText() + "]");
                break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                log.trace("PROCESSING_INSTRUCTION: ");
                log.trace("   [" + super.getPITarget() + "][" +
                        super.getPIData() + "]");
                break;
            case XMLStreamConstants.ENTITY_REFERENCE:
                log.trace("ENTITY_REFERENCE: ");
                log.trace("    " + super.getLocalName() + "[" +
                        super.getText() + "]");
                break;
            default :
                log.trace("UNKNOWN_STATE: " + currentEvent);
            
            }
        }
    }
}
