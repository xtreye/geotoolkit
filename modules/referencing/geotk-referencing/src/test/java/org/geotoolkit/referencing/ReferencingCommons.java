/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.referencing;

import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.geotoolkit.referencing.operation.projection.UnitaryProjection;
import org.geotoolkit.referencing.operation.transform.ConcatenatedTransform;
import org.geotoolkit.test.Commons;

import static org.junit.Assert.*;


/**
 * Convenience methods to be used for various tests. The methods defined in this class requires
 * Geotk-specific API (otherwise they would be defined in the {@code geotk-test} module).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16 (derived from 3.00)
 */
public final class ReferencingCommons extends Commons {
    /**
     * Do not allow instantiation of this class.
     */
    private ReferencingCommons() {
    }

    /**
     * Returns {@code true} if a factory backed by the EPSG database has been found on the
     * classpath. Some tests have different behavior depending on whatever such factory is
     * available or not.
     *
     * @return {@code true} if an EPSG-backed factory is available on the classpath.
     */
    public static boolean isEpsgFactoryAvailable() {
        final Hints hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, ThreadedEpsgFactory.class);
        final CRSAuthorityFactory factory;
        try {
            factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
        } catch (FactoryNotFoundException e) {
            return false;
        }
        assertTrue(factory instanceof ThreadedEpsgFactory);
        return true;
    }

    /**
     * Returns the class of the projection, or {@code null} if none.
     *
     * @param  crs The CRS for which to get the projection class.
     * @return The class of the projection implementation, or {@code null} if none.
     *
     * @since 3.09
     */
    public static Class<? extends UnitaryProjection> getProjectionClass(final ProjectedCRS crs) {
        return getProjectionClass(crs.getConversionFromBase().getMathTransform());
    }

    /**
     * Returns the class of the projection, or {@code null} if none.
     * This method invokes itself recursively down the concatenated transforms tree.
     */
    private static Class<? extends UnitaryProjection> getProjectionClass(final MathTransform transform) {
        if (transform instanceof UnitaryProjection) {
            return ((UnitaryProjection) transform).getClass();
        }
        if (transform instanceof ConcatenatedTransform) {
            Class<? extends UnitaryProjection> candidate;
            candidate = getProjectionClass(((ConcatenatedTransform) transform).transform1);
            if (candidate != null) {
                return candidate;
            }
            candidate = getProjectionClass(((ConcatenatedTransform) transform).transform2);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }
}