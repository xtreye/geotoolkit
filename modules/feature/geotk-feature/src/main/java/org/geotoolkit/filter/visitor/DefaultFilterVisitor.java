/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.visitor;

import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;


/**
 * Abstract implementation of FilterVisitor that simply walks the data structure.
 * <p>
 * This class implements the full FilterVisitor interface and will visit every Filter member of a
 * Filter object. This class performs no actions and is not intended to be used directly, instead
 * extend it and override the methods for the Filter type you are interested in. Remember to call the
 * super method if you want to ensure that the entire filter tree is still visited.
 *
 * <pre><code>
 * FilterVisitor allFids = new DefaultFilterVisitor(){
 *     public Object visit( Id filter, Object data ) {
 *         Set set = (Set) data;
 *         set.addAll(filter.getIDs());
 *         return set;
 *     }
 * };
 * Set set = (Set) myFilter.accept(allFids, new HashSet());
 * </code></pre>
 *
 * @author Jody
 * @module
 */
public abstract class DefaultFilterVisitor extends DefaultExpressionVisitor implements FilterVisitor {

    public DefaultFilterVisitor() {
    }

    @Override
    public Object visit(final ExcludeFilter filter, final Object data) {
        return data;
    }

    @Override
    public Object visit(final IncludeFilter filter, final Object data) {
        return data;
    }

    @Override
    public Object visit(final And filter, Object data) {
        if (filter.getChildren() != null) {
            for (Filter child : filter.getChildren()) {
                data = child.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final Id filter, final Object data) {
        return data;
    }

    @Override
    public Object visit(final Not filter, Object data) {
        Filter child = filter.getFilter();
        if (child != null) {
            data = child.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(final Or filter, Object data) {
        if (filter.getChildren() != null) {
            for (Filter child : filter.getChildren()) {
                data = child.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final PropertyIsBetween filter, Object data) {
        data = filter.getLowerBoundary().accept(this, data);
        data = filter.getExpression().accept(this, data);
        data = filter.getUpperBoundary().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final PropertyIsEqualTo filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final PropertyIsNotEqualTo filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final PropertyIsGreaterThan filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final PropertyIsGreaterThanOrEqualTo filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final PropertyIsLessThan filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final PropertyIsLessThanOrEqualTo filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final PropertyIsLike filter, Object data) {
        data = filter.getExpression().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final PropertyIsNull filter, Object data) {
        data = filter.getExpression().accept(this, data);
        return data;
    }
    
    @Override
    public Object visit(final PropertyIsNil filter, Object data) {
        data = filter.getExpression().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final BBOX filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final Beyond filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final Contains filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final Crosses filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final Disjoint filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final DWithin filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final Equals filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final Intersects filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final Overlaps filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final Touches filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(final Within filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(After filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(AnyInteracts filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(Before filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(Begins filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(BegunBy filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(During filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(EndedBy filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(Ends filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(Meets filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(MetBy filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(OverlappedBy filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(TContains filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(TEquals filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(TOverlaps filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }
    
    @Override
    public Object visitNullFilter(final Object data) {
        return data;
    }

}
