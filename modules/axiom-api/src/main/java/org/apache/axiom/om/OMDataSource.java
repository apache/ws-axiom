/**
 * 
 */
package org.apache.axiom.om;

import java.io.OutputStream;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Interface to arbitrary source of XML element data. This provides the hook for
 * using a general data source (such as data binding frameworks) as the backing
 * source of data for an element.
 */
public interface OMDataSource
{
    /**
     * Serializes element data directly to stream.
     *
     * @param output destination stream for element XML text
     * @param format output format information (<code>null</code> if none; may
     * be ignored if not supported by data binding even if supplied)
     * @throws XMLStreamException
     */
    public void serialize(OutputStream output, OMOutputFormat format)
            throws XMLStreamException;
    
    /**
     * Serializes element data directly to writer.
     *
     * @param writer destination writer for element XML text
     * @param format output format information (<code>null</code> if none; may
     * be ignored if not supported by data binding even if supplied)
     * @throws XMLStreamException
     */
    public void serialize(Writer writer, OMOutputFormat format)
            throws XMLStreamException;
    
    /**
     * Serializes element data directly to StAX writer.
     *
     * @param xmlWriter destination writer
     * @throws XMLStreamException
     */
    public void serialize(XMLStreamWriter xmlWriter)
            throws XMLStreamException;
    
    /**
     * Get parser for element data. In the general case this may require the
     * data source to serialize data as XML text and then parse that text.
     *
     * @return element parser
     * @throws XMLStreamException
     */
    public XMLStreamReader getReader() throws XMLStreamException;
}
