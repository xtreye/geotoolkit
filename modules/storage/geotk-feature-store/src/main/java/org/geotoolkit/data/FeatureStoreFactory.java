/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.data;

import org.geotoolkit.storage.DataStoreFactory;

/**
 * Factory used to open instances of FeatureStore.
 * <br/>
 * Factories must be declared using standard java services.
 * <br/>
 * Example, for a factory : x.y.XYFeatureStoreFactory<br/>
 * A service registration file must be declared like :<br/>
 * <code>META-INF/services/org.geotoolkit.storage.DataStoreFactory</code>
 * And contain a single line :<br/>
 * <code>x.y.XYFeatureStoreFactory</code>
 * 
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface FeatureStoreFactory extends DataStoreFactory {

}
