/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.expression.Expression;


/**
 * <p>Java class for PropertyIsLikeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyIsLikeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Literal"/>
 *       &lt;/sequence>
 *       &lt;attribute name="escape" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="singleChar" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="wildCard" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsLikeType", propOrder = {
    "propertyName",
    "literal"
})
public class PropertyIsLikeType extends ComparisonOpsType {

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;
    @XmlElement(name = "Literal", required = true)
    private LiteralType literal;
    @XmlAttribute(required = true)
    private String escape;
    @XmlAttribute(required = true)
    private String singleChar;
    @XmlAttribute(required = true)
    private String wildCard;

    /**
     * An empty constructor used by JAXB.
     */
    public PropertyIsLikeType() {
        
    }
    
    /**
     *Build a new Property is like operator
     */
    public PropertyIsLikeType(final Expression expr, final String pattern, final String wildcard, final String singleChar, final String escape) {
        this.escape   = escape;
        if (expr instanceof PropertyNameType) {
            this.propertyName = (PropertyNameType) expr;
        } else {
            throw new IllegalArgumentException("expr must be of type PropertyNameType.");
        }
        this.singleChar   = singleChar;
        this.wildCard     = wildcard;
        this.literal      = new LiteralType(pattern);
    }

    /**
     *Build a new Property is like operator
     */
    public PropertyIsLikeType(final String expr, final String pattern, final String wildcard, final String singleChar, final String escape) {
        this.escape       = escape;
        this.propertyName =  new PropertyNameType(expr);
        this.singleChar   = singleChar;
        this.wildCard     = wildcard;
        this.literal      = new LiteralType(pattern);
    }

    public PropertyIsLikeType(final PropertyIsLikeType that) {
        if (that != null) {
            this.escape = that.escape;
            if (that.literal != null) {
                this.literal = new LiteralType(that.literal);
            }
            if (that.propertyName != null) {
                this.propertyName = new PropertyNameType(that.propertyName);
            }
            this.singleChar = that.singleChar;
            this.wildCard   = that.wildCard;
        }
    }
    
    /**
     * Gets the value of the propertyName property.
     * 
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     * 
     */
    public void setPropertyName(final PropertyNameType value) {
        this.propertyName = value;
    }

    /**
     * Gets the value of the literal property.
     * 
     */
    public LiteralType getLiteral() {
        return literal;
    }

    /**
     * Sets the value of the literal property.
     * 
    */
    public void setLiteral(final LiteralType value) {
        this.literal = value;
    }

    /**
     * Gets the value of the escape property.
     * 
     */
    public String getEscape() {
        return escape;
    }

    /**
     * Sets the value of the escape property.
     * 
     */
    public void setEscape(final String value) {
        this.escape = value;
    }

    /**
     * Gets the value of the singleChar property.
     * 
     */
    public String getSingleChar() {
        return singleChar;
    }

    /**
     * Sets the value of the singleChar property.
     * 
     */
    public void setSingleChar(final String value) {
        this.singleChar = value;
    }

    /**
     * Gets the value of the wildCard property.
     * 
     */
    public String getWildCard() {
        return wildCard;
    }

    /**
     * Sets the value of the wildCard property.
     * 
     */
    public void setWildCard(final String value) {
        this.wildCard = value;
    }

    @Override
    public ComparisonOpsType getClone() {
        return new PropertyIsLikeType(this);
    }
}
