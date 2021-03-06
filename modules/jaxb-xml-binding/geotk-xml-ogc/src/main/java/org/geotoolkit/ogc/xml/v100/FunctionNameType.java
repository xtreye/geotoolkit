/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.opengis.filter.capability.FunctionName;


/**
 * <p>Java class for Function_NameType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Function_NameType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="nArgs" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Function_NameType", propOrder = {
    "value"
})
public class FunctionNameType implements FunctionName {

    @XmlValue
    private String value;
    @XmlAttribute(required = true)
    private String nArgs;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the nArgs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNArgs() {
        return nArgs;
    }

    /**
     * Sets the value of the nArgs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNArgs(String value) {
        this.nArgs = value;
    }

    @Override
    public int getArgumentCount() {
        if (nArgs != null) {
            try {
                return Integer.parseInt(nArgs);
            } catch (NumberFormatException ex) {}
        }
        return 0;
    }

    @Override
    public List<String> getArgumentNames() {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return value;
    }

}
