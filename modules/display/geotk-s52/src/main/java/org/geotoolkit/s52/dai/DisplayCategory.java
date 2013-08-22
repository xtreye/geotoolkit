/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.dai;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Display Category.
 * Defines membership within a display category.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DisplayCategory extends DAIField{

    /** A(1/15) : Name of the display category */
    public String DSCN;

    public DisplayCategory() {
        super("DISC");
    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.put("DSCN", DSCN);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        DSCN = readStringByDelim(str, offset, DELIM_1F);
    }

}
