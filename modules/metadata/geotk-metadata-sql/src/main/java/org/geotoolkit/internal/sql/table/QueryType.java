/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal.sql.table;


/**
 * The kind of query to be executed.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public enum QueryType {
    /**
     * Only one entry will be selected using a name. This is the kind of query executed by
     * {@link SingletonTable#getEntry(String)} or {@link SingletonTable#getEntry(int)},
     * depending if the identifier is numeric or not.
     */
    SELECT,

    /**
     * Checks if an entry exists. This query is similar to {@link #SELECT} except that it
     * doesn't ask for any column, so the query is simpler for the database. The parameters
     * are usually the same than {@link #SELECT} and we are only interested to see if the
     * result set contains at least one entry.
     */
    EXISTS,

    /**
     * Every entries will be listed. This is the kind of query executed by
     * {@link SingletonTable#getEntries()}.
     */
    LIST,

    /**
     * List only the identifier of every entries. This is the kind of query executed by
     * {@link SingletonTable#getIdentifiers()}.
     */
    LIST_ID,

    /**
     * Count the entries.
     */
    COUNT,

    /**
     * Selects spatio-temporal envelope in a set of entries. This is the kind of
     * query executed by {@link BoundedSingletonTable#getEnvelope()}.
     */
    BOUNDING_BOX,

    /**
     * Selects a list of available dates or depths.
     */
    AVAILABLE_DATA,

    /**
     * An entry to be added in a table.
     */
    INSERT,

    /**
     * An entry to be deleted from a table.
     */
    DELETE,

    /**
     * Many entries to be deleted from a table.
     */
    DELETE_ALL
}
