/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.nodefactory.TreeNodeFactory;
import org.geotoolkit.index.tree.star.StarRTree;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.referencing.operation.TransformException;

/**Create R*Tree test suite in geographic 3D space.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class StarRTreeGeo2DTest extends TreeTest{

    public StarRTreeGeo2DTest() throws TransformException {
        super(new StarRTree(4, DefaultGeographicCRS.WGS84, TreeNodeFactory.DEFAULT_FACTORY));
    }

    /**
     * Some elements inserted Tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testInsert() throws IllegalArgumentException, TransformException {
        super.insertTest();
    }

    /**
     * Verify all boundary Node from its "children" Node.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testCheckBoundary() throws IllegalArgumentException, TransformException {
        super.checkBoundaryTest();
    }

    /**
     * Test search query inside tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testQueryInside() throws IllegalArgumentException, TransformException {
        super.queryInsideTest();
    }

    /**
     * Test query outside of tree area.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testQueryOutside() throws IllegalArgumentException, TransformException {
        super.queryOutsideTest();
    }

    /**
     * Test query on tree boundary border.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testQueryOnBorder() throws IllegalArgumentException, TransformException {
        super.queryOnBorderTest();
    }

    /**
     * Test insertion and deletion in tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void testInsertDelete() throws IllegalArgumentException, TransformException {
        super.insertDelete();
    }
}
