/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.imageio.spi.ServiceRegistry;

import org.opengis.metadata.Identifier;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.internal.Citations;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.factory.AllAuthoritiesFactory;
import org.geotoolkit.referencing.factory.MultiAuthoritiesFactory;
import org.geotoolkit.referencing.factory.CachingAuthorityFactory;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.lang.ThreadSafe;


/**
 * The default authority factory to be used by {@link CRS#decode}.
 * This class gathers together a lot of logic in order to capture the following ideas:
 * <p>
 * <ul>
 *   <li>Uses {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER} to swap ordinate order if needed.</li>
 *   <li>Uses {@link AllAuthoritiesFactory} to select CRS Authorities from the code.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @author Andrea Aime (TOPP)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
@ThreadSafe(concurrent = true)
final class DefaultAuthorityFactory extends CachingAuthorityFactory implements CRSAuthorityFactory {
    /**
     * List of codes without authority space. We can not defines them in an ordinary
     * authority factory.
     */
    private static List<String> AUTHORITY_LESS = UnmodifiableArrayList.wrap(new String[] {
        "WGS84(DD)"  // (longitude,latitude) with decimal degrees.
    });

    /**
     * The backing store of this factory.
     */
    final MultiAuthoritiesFactory backingStore;

    /**
     * Creates a new authority factory.
     */
    private DefaultAuthorityFactory(final MultiAuthoritiesFactory backingStore) {
        super(backingStore);
        this.backingStore = backingStore;
    }

    /**
     * Creates an instance of {@code DefaultAuthorityFactory}. This method first asks for the
     * list of all factories no matter how they handle the axis order hint, then substitutes
     * the factories by instances that are compliant with the axis order hint.
     */
    static DefaultAuthorityFactory create(final boolean longitudeFirst) {
        final Hints hints = new Hints( // Default hints (system width) are inherited here.
                Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, longitudeFirst);
        final List<CRSAuthorityFactory> factories = new ArrayList<CRSAuthorityFactory>(
                AuthorityFactoryFinder.getCRSAuthorityFactories(hints));
        /*
         * Do not invoke FactoryRegistry.getServiceProviders() (which returns an Iterator over
         * all registered factories) because it doesn't create new instance. We want to create
         * new factory instances with the FORCE_LONGITUDE_FIRST_AXIS_ORDER hint set to TRUE if
         * needed. So instead, iterates over the default factories and derives factories from
         * them.
         */
        final TypeFilter filter = new TypeFilter();
        hints.put(AuthorityFactoryFinder.FILTER_KEY, filter);
        for (int i=factories.size(); --i>=0;) {
            CRSAuthorityFactory factory = factories.get(i);
            final String authority = Citations.getIdentifier(factory.getAuthority());
            hints.put(Hints.CRS_AUTHORITY_FACTORY, filter.type = factory.getClass());
            try {
                factory = AuthorityFactoryFinder.getCRSAuthorityFactory(authority, hints);
            } catch (FactoryRegistryException e) {
                /*
                 * The factory does not accept to produce CRS in XY order. Remove it from the
                 * list.  Note that this exception does NOT remove the URN and HTTP factories
                 * because they behave in a special way: they just ignore the hint (unless
                 * FORCE_AXIS_ORDER_HONORING is set) instead than declaring that they can not
                 * respect the hint.
                 */
                Logging.recoverableException(LOGGER, DefaultAuthorityFactory.class, "create", e);
                factories.remove(i);
                continue;
            }
            final CRSAuthorityFactory old = factories.set(i, factory);
            if (old != factory) {
                final LogRecord record = Loggings.format(Level.FINE,
                        Loggings.Keys.FACTORY_REPLACED_FOR_AXIS_ORDER_$4, authority,
                        factory.getClass(), old.getClass(), longitudeFirst ? 1 : 0);
                record.setSourceClassName(DefaultAuthorityFactory.class.getName());
                record.setSourceMethodName("create");
                record.setLoggerName(LOGGER.getName());
                LOGGER.log(record);
            }
        }
        /*
         * At this point we have the list of factories that we want to use.  Creates a
         * MultiAuthoritiesFactory which will use those factories. If all of them fail
         * for a given authority, then we will fallback on any factory registered in
         * AuthorityFactoryFinder without hints constraint except axis order.
         *
         * Note that the factories found above are override. If the above list contains
         * an EPSG factory, then AuthorityFactoryFinder will never be used for fetching
         * an EPSG factory. Consequently the EPSG factory used is constrained by the
         * hints computed at the begining of this method. Only factories that would not
         * be available otherwise will be fetch by AuthorityFactoryFinder with no hints
         * constraint.
         */
        hints.clear();
        hints.put(AllAuthoritiesFactory.USER_FACTORIES_KEY, factories);
        hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, longitudeFirst);
        return new DefaultAuthorityFactory(AllAuthoritiesFactory.getInstance(hints));
    }

    /**
     * The filter for requesting factories that are exactly of the given type.
     */
    private static final class TypeFilter implements ServiceRegistry.Filter {
        /**
         * The requested factory type. Must be assigned by the caller.
         */
        Class<?> type;

        /**
         * Returns {@code true} if the given factory is exactly of the given type
         * (not a subclass).
         */
        @Override
        public boolean filter(final Object provider) {
            return provider.getClass().equals(type);
        }
    }

    /**
     * Implementation of {@link CRS#getSupportedCodes}. Provided here in order to reduce the
     * amount of class loading when using {@link CRS} for other purpose than CRS decoding.
     */
    static Set<String> getSupportedCodes(final String authority) {
        final Set<String> result = new LinkedHashSet<String>(AUTHORITY_LESS);
        for (final CRSAuthorityFactory factory : AuthorityFactoryFinder.getCRSAuthorityFactories(null)) {
            if (Citations.identifierMatches(factory.getAuthority(), authority)) {
                final Set<String> codes;
                try {
                    codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
                } catch (Exception exception) {
                    /*
                     * Failed to fetch the codes either because of a database connection problem
                     * (FactoryException), or because we are using a simple factory that doesn't
                     * support this operation (UnsupportedOperationException), or any unexpected
                     * reason. No codes from this factory will be added to the set.
                     */
                    CRS.unexpectedException("getSupportedCodes", exception);
                    continue;
                }
                if (codes != null) {
                    result.addAll(codes);
                }
            }
        }
        return result;
    }

    /**
     * Implementation of {@link CRS#getSupportedAuthorities}. Provided here in order to reduce the
     * amount of class loading when using {@link CRS} for other purpose than CRS decoding.
     */
    static Set<String> getSupportedAuthorities(final boolean returnAliases) {
        final Set<String> result = new LinkedHashSet<String>();
        for (final CRSAuthorityFactory factory : AuthorityFactoryFinder.getCRSAuthorityFactories(null)) {
            for (final Identifier id : factory.getAuthority().getIdentifiers()) {
                result.add(id.getCode());
                if (!returnAliases) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns the coordinate reference system for the given code.
     */
    @Override
    public CoordinateReferenceSystem createCoordinateReferenceSystem(String code)
            throws FactoryException
    {
        if (code != null) {
            code = code.trim();
            if (code.equalsIgnoreCase("WGS84(DD)")) {
                return DefaultGeographicCRS.WGS84;
            }
        }
        assert !AUTHORITY_LESS.contains(code) : code;
        return super.createCoordinateReferenceSystem(code);
    }
}
