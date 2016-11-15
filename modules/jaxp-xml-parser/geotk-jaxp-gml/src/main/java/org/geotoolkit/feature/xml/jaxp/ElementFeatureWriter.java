/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.feature.xml.jaxp;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;

import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.Namespaces;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.internal.jaxb.JTSWrapperMarshallerPool;

import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.util.FactoryException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ElementFeatureWriter {

    /**
     * Logger for this writer.
     */
    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.feature.xml.jaxp");

    /**
     * The pool of marshallers used for marshalling geometries.
     */
    private static final MarshallerPool POOL = JTSWrapperMarshallerPool.getInstance();

    /**
     * Object factory to build a geometry.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    /**
     * GML namespace for this class.
     */
    private static final String GML = "http://www.opengis.net/gml";

    protected String schemaLocation;

    private int lastUnknowPrefix = 0;

    private final Map<String, String> unknowNamespaces = new HashMap<String, String>();

    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public ElementFeatureWriter() {
    }

    public ElementFeatureWriter(final Map<String, String> schemaLocations) {

         if (schemaLocations != null && schemaLocations.size() > 0) {
             final StringBuilder sb = new StringBuilder();
             for (Entry<String,String> entry : schemaLocations.entrySet()) {
                 sb.append(entry.getKey()).append(' ').append(entry.getValue()).append(' ');
             }
             if(sb.length()>0){
                sb.setLength(sb.length()-1); //remove last ' '
             }
             schemaLocation = sb.toString();
         }
    }

     public Element write(final Object candidate, final boolean fragment) throws IOException, DataStoreException, ParserConfigurationException {
         return write(candidate, fragment, null);
     }

    /**
     * {@inheritDoc}
     */
    public Element write(final Object candidate, final boolean fragment, final Integer nbMatched) throws IOException, DataStoreException, ParserConfigurationException {

        if (candidate instanceof Feature) {
            return writeFeature((Feature) candidate, null, fragment);
        } else if (candidate instanceof FeatureCollection) {
            return writeFeatureCollection((FeatureCollection) candidate, fragment, true, nbMatched);
        } else {
            throw new IllegalArgumentException("The given object is not a Feature or a" +
                    " FeatureCollection: "+ candidate);
        }
    }

    /**
     * Write the feature into the stream.
     *
     * @param feature The feature
     * @param root
     * @throws XMLStreamException
     */
    public Element writeFeature(final Feature feature,final Document rootDocument, boolean fragment) throws ParserConfigurationException {

        final Document document;
        if (rootDocument == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // then we have to create document-loader:
            factory.setNamespaceAware(false);
            DocumentBuilder loader = factory.newDocumentBuilder();

            // creating a new DOM-document...
            document = loader.newDocument();
        } else {
            document = rootDocument;
        }


        //the root element of the xml document (type of the feature)
        final FeatureType type = feature.getType();
        final GenericName typeName    = type.getName();
        final String namespace = NamesExt.getNamespace(typeName);
        final String localPart = typeName.tip().toString();

        final Element rootElement;
        final Prefix prefix;
        if (namespace != null) {
            prefix = getPrefix(namespace);
            rootElement = document.createElementNS(namespace, localPart);
            rootElement.setPrefix(prefix.prefix);

        } else {
            rootElement = document.createElement(localPart);
            prefix = null;
        }
        // if main document set the xmlns
        if (!fragment) {
            rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:gml", "http://www.opengis.net/gml");
        }
        final Attr idAttr = document.createAttributeNS(GML, "id");
        idAttr.setValue(feature.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()).toString());
        idAttr.setPrefix("gml");
        rootElement.setAttributeNodeNS(idAttr);

        if (rootDocument == null) {
            document.appendChild(rootElement);
        }
        //write properties in the type order
        for(final PropertyType desc : type.getProperties(true)){
            if(AttributeConvention.contains(desc.getName())) continue;
            if (desc.getName().tip().toString().startsWith("@")) {
                //skip attributes
               continue;
            }
            
            final Collection values = Utils.propertyValueAsList(feature, desc.getName().toString());
            for (Object valueA : values) {
                final PropertyType typeA = desc;
                final GenericName nameA = desc.getName();
                final String nameProperty = nameA.tip().toString();
                String namespaceProperty = NamesExt.getNamespace(nameA);
                if (valueA instanceof Collection && !(AttributeConvention.isGeometryAttribute(typeA))) {
                    for (Object value : (Collection)valueA) {
                        final Element element;
                        if (namespaceProperty != null) {
                            element = document.createElementNS(namespaceProperty, nameProperty);
                        } else {
                            element = document.createElement(nameProperty);
                        }
                        element.setTextContent(Utils.getStringValue(value));
                        if (prefix != null) {
                            element.setPrefix(prefix.prefix);
                        }
                        rootElement.appendChild(element);
                    }

                } else if (valueA != null && valueA.getClass().isArray() && !(AttributeConvention.isGeometryAttribute(typeA))) {
                    final int length = Array.getLength(valueA);
                    for (int i = 0; i < length; i++){
                        final Element element;
                        if (namespaceProperty != null) {
                            element = document.createElementNS(namespaceProperty, nameProperty);
                        } else {
                            element = document.createElement(nameProperty);
                        }
                        final Object value = Array.get(valueA, i);
                        final String textValue;
                        if (value != null && value.getClass().isArray()) { // matrix
                            final StringBuilder sb = new StringBuilder();
                            final int length2 = Array.getLength(value);
                            for (int j = 0; j < length2; j++) {
                                final Object subValue = Array.get(value, j);
                                sb.append(Utils.getStringValue(subValue)).append(" ");
                            }
                            textValue = sb.toString();
                        } else {
                            textValue = Utils.getStringValue(value);
                        }
                        element.setTextContent(textValue);
                        if (prefix != null) {
                            element.setPrefix(prefix.prefix);
                        }
                        rootElement.appendChild(element);

                    }

                } else if (valueA instanceof Map && !(AttributeConvention.isGeometryAttribute(typeA))) {
                    final Map<?,?> map = (Map)valueA;
                    for (Entry<?,?> entry : map.entrySet()) {
                        final Element element;
                        if (namespaceProperty != null) {
                            element = document.createElementNS(namespaceProperty, nameProperty);
                        } else {
                            element = document.createElement(nameProperty);
                        }
                        final Object key = entry.getKey();
                        if (key != null) {
                            element.setAttribute("name", (String)key);
                        }
                        element.setTextContent(Utils.getStringValue(entry.getValue()));
                        if (prefix != null) {
                            element.setPrefix(prefix.prefix);
                        }
                        rootElement.appendChild(element);
                    }

                } else if (!(AttributeConvention.isGeometryAttribute(typeA))) {
                    String value = Utils.getStringValue(valueA);
                    if (value != null || (value == null && !Utils.isNillable(typeA))) {

                        if ((nameProperty.equals("name") || nameProperty.equals("description")) && !GML.equals(namespaceProperty)) {
                            namespaceProperty = GML;
                            LOGGER.finer("the property name and description of a feature must have the GML namespace");
                        }
                        final Element element;
                        if (namespaceProperty != null) {
                            element = document.createElementNS(namespaceProperty, nameProperty);
                        } else {
                            element = document.createElement(nameProperty);
                        }
                        if (value != null) {
                            element.setTextContent(value);
                        }
                        if (prefix != null) {
                            element.setPrefix(prefix.prefix);
                        }
                        rootElement.appendChild(element);
                    }

                // we add the geometry
                } else {

                    if (valueA != null) {
                        final Element element;
                        if (namespaceProperty != null) {
                            element = document.createElementNS(namespaceProperty, nameProperty);
                        } else {
                            element = document.createElement(nameProperty);
                        }
                        if (prefix != null) {
                            element.setPrefix(prefix.prefix);
                        }
                        Geometry isoGeometry = JTSUtils.toISO((com.vividsolutions.jts.geom.Geometry) valueA, FeatureExt.getCRS(type));
                        try {
                            final Marshaller marshaller;
                            marshaller = POOL.acquireMarshaller();
                            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
                            marshaller.marshal(OBJECT_FACTORY.buildAnyGeometry(isoGeometry), element);
                            POOL.recycle(marshaller);
                        } catch (JAXBException ex) {
                            LOGGER.log(Level.WARNING, "JAXB Exception while marshalling the iso geometry: " + ex.getMessage(), ex);
                        }
                        rootElement.appendChild(element);
                    }
                }
            }
        }

        //writer.writeEndElement();
        return rootElement;
    }

    /**
     *
     * @param featureCollection
     * @param writer
     * @param fragment : true if we write in a stream, dont write start and end elements
     * @throws DataStoreException
     */
    public Element writeFeatureCollection(final FeatureCollection featureCollection, final boolean fragment, final boolean wfs, final Integer nbMatched) throws DataStoreException, ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // then we have to create document-loader:
        factory.setNamespaceAware(false);
        DocumentBuilder loader = factory.newDocumentBuilder();

        // creating a new DOM-document...
        Document document = loader.newDocument();

        // the XML header
        if (!fragment) {
            document.setXmlVersion("1.0");
            //writer.writeStartDocument("UTF-8", "1.0");
        }

        // the root Element
        final Element rootElement;
        if (wfs) {
            rootElement = document.createElementNS("http://www.opengis.net/wfs", "FeatureCollection");
            rootElement.setPrefix("wfs");
        } else {
            rootElement = document.createElementNS("http://www.opengis.net/gml", "FeatureCollection");
            rootElement.setPrefix("gml");
        }

        document.appendChild(rootElement);

        String collectionID = "";
        if (featureCollection.getID() != null) {
            collectionID = featureCollection.getID();
        }
        final Attr idAttribute = document.createAttributeNS(GML, "id");
        idAttribute.setValue(collectionID);
        idAttribute.setPrefix("gml");
        rootElement.setAttributeNodeNS(idAttribute);

        rootElement.setAttribute("numberOfFeatures", Integer.toString(featureCollection.size()));

        if (nbMatched != null) {
            rootElement.setAttribute("numberMatched", Integer.toString(nbMatched));
        }

        // timestamp
        synchronized(FORMATTER) {
            rootElement.setAttribute("timeStamp", FORMATTER.format(new Date(System.currentTimeMillis())));
        }

        if (schemaLocation != null && !schemaLocation.equals("")) {
            rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", schemaLocation);
        }

        /*FeatureType type = featureCollection.getFeatureType();
        if (type != null && type.getName() != null) {
            String namespace = type.getName().getNamespaceURI();
            if (namespace != null && !namespace.equals(GML)) {
                Prefix prefix    = getPrefix(namespace);
                writer.writeNamespace(prefix.prefix, namespace);
            }
        }*/
        /*
         * The boundedby part
         */
        final Element boundElement = writeBounds(featureCollection.getEnvelope(), document);
        if (boundElement != null) {
            rootElement.appendChild(boundElement);
        }

        // we write each feature member of the collection
        FeatureIterator iterator = featureCollection.iterator();
        try {
            while (iterator.hasNext()) {
                final Feature f = iterator.next();
                final Element memberElement = document.createElementNS(GML, "featureMember");
                memberElement.setPrefix("gml");
                memberElement.appendChild(writeFeature(f, document, true));
                rootElement.appendChild(memberElement);

            }

        } finally {
            // we close the stream
            iterator.close();
        }
        return rootElement;
    }

    private Element writeBounds(final Envelope bounds, final Document document) {
        if (bounds != null) {

            String srsName = null;
            if (bounds.getCoordinateReferenceSystem() != null) {
                try {
                    srsName = IdentifiedObjects.lookupURN(bounds.getCoordinateReferenceSystem(), null);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
            final Element boundElement = document.createElementNS(GML, "boundedBy");
            boundElement.setPrefix("gml");
            final Element envElement = document.createElementNS(GML, "Envelope");
            envElement.setPrefix("gml");
            if (srsName != null) {
                envElement.setAttribute("srsName", srsName);
            } else {
                envElement.setAttribute("srsName", "");
            }

            // lower corner
            final Element lower = document.createElementNS(GML, "lowerCorner");
            String lowValue = bounds.getLowerCorner().getOrdinate(0) + " " + bounds.getLowerCorner().getOrdinate(1);
            lower.setTextContent(lowValue);
            lower.setPrefix("gml");
            envElement.appendChild(lower);

            // upper corner
            final Element upper = document.createElementNS(GML, "upperCorner");
            String uppValue = bounds.getUpperCorner().getOrdinate(0) + " " + bounds.getUpperCorner().getOrdinate(1);
            upper.setTextContent(uppValue);
            upper.setPrefix("gml");
            envElement.appendChild(upper);

            boundElement.appendChild(envElement);
            return boundElement;
        }
        return null;
    }

    /**
     * Returns the prefix for the given namespace.
     *
     * @param namespace The namespace for which we want the prefix.
     */
    private Prefix getPrefix(final String namespace) {
        String prefix = Namespaces.getPreferredPrefix(namespace, null);
        boolean unknow = false;
        if (prefix == null) {
            prefix = unknowNamespaces.get(namespace);
            if (prefix == null) {
                prefix = "ns" + lastUnknowPrefix;
                lastUnknowPrefix++;
                unknow = true;
                unknowNamespaces.put(namespace, prefix);
            }
        }
        return new Prefix(unknow, prefix);
    }


    /**
     * Inner class for handling prefix and if it is already known.
     */
    private final class Prefix {
        public boolean unknow;
        public String prefix;

        public Prefix(final boolean unknow, final String prefix) {
            this.prefix = prefix;
            this.unknow = unknow;
        }
    }
}
