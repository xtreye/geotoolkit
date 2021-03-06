/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.render2d.control.edition;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.apache.sis.feature.FeatureExt;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GeometryNodeTool extends AbstractEditionTool {

    public GeometryNodeTool() {
        super(300,"geometryNodes", MessageBundle.formatInternational(MessageBundle.Keys.editNode),
             new SimpleInternationalString("Tool for editing geometry nodes."), 
             IconBundle.getIcon("16_multi_point"), FeatureMapLayer.class);
    }

    @Override
    public boolean canHandle(final Object candidate) {
        if(!super.canHandle(candidate)){
            return false;
        }

        //check the geometry type is type Point
        final FeatureMapLayer layer = (FeatureMapLayer) candidate;
        final FeatureType ft = layer.getCollection().getFeatureType();
        final AttributeType desc = FeatureExt.getDefaultGeometryAttribute(ft);

        if(desc == null){
            return false;
        }

        if(Point.class.isAssignableFrom(desc.getValueClass())){
            //moving node on a Point type is the same as moving full geometry.
            //avoid duplicating the same purpose tool.
            return false;
        }
        
        return Geometry.class.isAssignableFrom(desc.getValueClass());
    }

    @Override
    public EditionDelegate createDelegate(final JMap2D map, final Object candidate) {
        return new GeometryNodeDelegate(map, (FeatureMapLayer) candidate);
    }

}
