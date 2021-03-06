/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.report.graphic.map;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapContext;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.canvas.control.NeverFailMonitor;
import org.geotoolkit.display.container.GraphicContainer;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 * Special canvas used to render maps in JasperReport templates.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CanvasRenderer extends J2DCanvas implements JRRenderable{

    private final String id = System.currentTimeMillis() + "-" + Math.random();

    private Envelope area = null;

    private Graphics2D g2d = null;

    public CanvasRenderer(final MapContext context){
        super(context.getCoordinateReferenceSystem(),null);
        monitor = new NeverFailMonitor();
    }

    private CanvasRenderer( final Hints hints){
        super(CommonCRS.WGS84.normalizedGeographic(),hints);
    }

    @Override
    public void setVisibleArea(Envelope env) throws NoninvertibleTransformException, TransformException {
        super.setVisibleArea(env);
        area = env;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clearCache() {
        super.clearCache();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose(){
        super.dispose();
        context2D.dispose();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void repaint(final Shape area) {
        monitor.renderingStarted();
        fireRenderingStateChanged(RENDERING);

        final Graphics2D output = g2d;
        output.addRenderingHints(getHints(true));

        final RenderingContext2D context = prepareContext(context2D, output,null);

        //paint background if there is one.
        if(painter != null){
            painter.paint(context2D);
        }

        final GraphicContainer container = getContainer();
        if(container != null){
            render(context, container.flatten(true));
        }

        /**
         * End painting, erase dirtyArea
         */
        output.dispose();
        fireRenderingStateChanged(ON_HOLD);
        monitor.renderingFinished();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage getSnapShot(){
        throw new UnsupportedOperationException("JasperCanvas doesnt support getSnapshot");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte getType() {
        return TYPE_SVG;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte getImageType() {
        return IMAGE_TYPE_PNG;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Dimension2D getDimension() throws JRException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte[] getImageData() throws JRException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void render(final Graphics2D g, final Rectangle2D rect) throws JRException {
        double rotation = getRotation();

        setDisplayBounds(rect);
        try {
            setVisibleArea(area);
            setRotation(rotation);
        } catch (NoninvertibleTransformException | TransformException ex) {
            Logging.getLogger("org.geotoolkit.report.graphic.map").log(Level.WARNING, null, ex);
        }

        g2d = (Graphics2D) g.create();
        g2d.clip(rect);
        g2d.translate(rect.getMinX(), rect.getMinY());
        //fix Itext library not supported translucent images
        g2d = new PDFFixGraphics2D(g2d);
        repaint();
    }

}
