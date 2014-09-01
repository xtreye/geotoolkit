/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2014, Geomatys
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
package org.geotoolkit.storage;

/**
 * Base class for {@link org.geotoolkit.storage.DataStoreFactory} metadata. It should be retrived via {@link DataStoreFactory#getMetadata()}.
 *
 * @author Alexis Manin (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public interface FactoryMetadata {

    /**
     * Data types stores.
     * 
     * @return DataType
     */
    DataType getDataType();
    
    /**
     * Feature store may produce 2 kinds of features.
     * Standard unstyled features like : shapefile, postgresql, 
     * 
     * @return true if features are styled.
     */
    boolean produceStyledFeature();
    
    
}
