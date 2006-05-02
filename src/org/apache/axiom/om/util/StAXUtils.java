package org.apache.axiom.om.util;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Stack;

public class StAXUtils {

    /**
     * Pool of XMLOutputFactory instances
     */
    private static Stack xmlOutputFactoryPool = new Stack();

    /**
     * Pool of XMLInputFactory instances
     */
    private static Stack xmlInputFactoryPool = new Stack();

    /**
     * Gets an XMLInputFactory instance from pool.
     *
     * @return an XMLInputFactory instance.
     */
    synchronized public static XMLInputFactory getXMLInputFactory() {
        if (!xmlInputFactoryPool.empty()) {
            return (XMLInputFactory) xmlInputFactoryPool.pop();
        }
        return XMLInputFactory.newInstance();
    }

    /**
     * Returns an XMLInputFactory instance for reuse.
     *
     * @param factory An XMLInputFactory instance that is available for reuse
     */
    synchronized public static void releaseXMLInputFactory(XMLInputFactory factory) {
        xmlInputFactoryPool.push(factory);
    }

    public static XMLStreamReader createXMLStreamReader(InputStream in)
            throws XMLStreamException {
        XMLInputFactory inputFactory = getXMLInputFactory();
        try {
            return inputFactory.createXMLStreamReader(in);
        } finally {
            releaseXMLInputFactory(inputFactory);
        }
    }

    public static XMLStreamReader createXMLStreamReader(Reader in)
            throws XMLStreamException {
        XMLInputFactory inputFactory = getXMLInputFactory();
        try {
            return inputFactory.createXMLStreamReader(in);
        } finally {
            releaseXMLInputFactory(inputFactory);
        }
    }

    /**
     * Gets an XMLOutputFactory instance from pool.
     *
     * @return an XMLOutputFactory instance.
     */
    synchronized public static XMLOutputFactory getXMLOutputFactory() {
        if (!xmlOutputFactoryPool.empty()) {
            return (XMLOutputFactory) xmlOutputFactoryPool.pop();
        }
        return XMLOutputFactory.newInstance();
    }

    /**
     * Returns an XMLOutputFactory instance for reuse.
     *
     * @param factory An XMLOutputFactory instance that is available for reuse.
     */
    synchronized public static void releaseXMLOutputFactory(XMLOutputFactory factory) {
        xmlOutputFactoryPool.push(factory);
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out)
            throws XMLStreamException {
        XMLOutputFactory outputFactory = getXMLOutputFactory();
        try {
            return outputFactory.createXMLStreamWriter(out);
        } finally {
            releaseXMLOutputFactory(outputFactory);
        }
    }

    public static XMLStreamWriter createXMLStreamWriter(Writer out)
            throws XMLStreamException {
        XMLOutputFactory outputFactory = getXMLOutputFactory();
        try {
            return outputFactory.createXMLStreamWriter(out);
        } finally {
            releaseXMLOutputFactory(outputFactory);
        }
    }

    public static void reset() {
        xmlOutputFactoryPool.removeAllElements();
        xmlInputFactoryPool.removeAllElements();
    }
}
