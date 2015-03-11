/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.metadata.geotiff;

import com.sun.media.imageio.plugins.tiff.GeoTIFFTagSet;
import java.util.logging.Level;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.imageio.metadata.IIOMetadata;

import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.builder.LocalizationGrid;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;

import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;

import org.w3c.dom.Node;

import static com.sun.media.imageio.plugins.tiff.GeoTIFFTagSet.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffMetaDataUtils.*;
import static org.geotoolkit.util.DomUtilities.*;

/**
 * Utility class to read geotiff metadata tags.
 * http://www.remotesensing.org/geotiff/faq.html
 *
 * @author Johann Sorel  (Geomatys)
 * @author Remi Marechal (Geomatys)
 */
public final class GeoTiffMetaDataReader {

    private static final Logger LOGGER = Logging.getLogger(GeoTiffMetaDataReader.class);

    public static class ValueMap extends HashMap<Integer, Object> {

        /**
	 * @param key
	 * @return A string representing the value, or null if the key was not
	 *         found or failed to parse.
	 */
	public String getAsString(final int key) {
            
            final Object value = get(key);

            if (value instanceof String) return (String) value;
            
            if (value instanceof Number) return ((Number)value).toString();
            
            return null;
        }

        public double getAsDouble(final int key) {
            final Object value = get(key);

            if (value == null)           return Double.NaN;
            if (value instanceof Number) return ((Number)value).doubleValue();
            
            try {
                final String geoKey = value.toString();
                return Double.parseDouble(geoKey);
            } catch (NumberFormatException ne) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, ne.getLocalizedMessage(), ne);
                }
                return Double.NaN;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
                return Double.NaN;
            }
        }
    }

    private final IIOMetadata imageMetadata;
    private final Node root;
    private final Node imgFileDir;
    private final Node geoKeyDir;

    public GeoTiffMetaDataReader(final IIOMetadata imageMetadata) throws IOException{
        this.imageMetadata = imageMetadata;
        root = imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName());
        if (root == null) throw new IOException("No image metadatas");

        imgFileDir = getNodeByLocalName(root,TAG_GEOTIFF_IFD);
        if (imgFileDir == null) throw new IOException("No GeoTiff metadatatas");

        geoKeyDir = getNodeByNumber(imgFileDir,TAG_GEO_KEY_DIRECTORY);
    }

    /**
     * Read the Spatial Metadatas.
     *
     * @return SpatialMetadata
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     * @throws java.io.IOException
     */
    public SpatialMetadata readSpatialMetaData() throws NoSuchAuthorityCodeException, FactoryException, IOException {
        
        final SpatialMetadata spatialMetadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(SpatialMetadataFormat.GEOTK_FORMAT_NAME));

        if (geoKeyDir != null) {
            final int[] structure = readTiffShorts(getNodeByLocalName(geoKeyDir, TAG_GEOTIFF_SHORTS));

            //first line (4 int) contain the version and number of keys
            //Header={KeyDirectoryVersion, KeyRevision, MinorRevision, NumberOfKeys}
            final int directoryVersion  = structure[0];
            final int keyVersion        = structure[1];
            final int minorVersion      = structure[2];
            final int nbKeys            = structure[3];

            //read all entries
            final ValueMap entries = new ValueMap();
            for(int i=0,l=4; i<nbKeys; i++,l+=4){
                final Object value;
                final int valueKey      = structure[l+0];
                final int valuelocation = structure[l+1];
                final int valueNb       = structure[l+2];
                final int valueOffset   = structure[l+3];
                if (valuelocation == 0) {
                    //value is located in the offset field
                    value = valueOffset;
                } else {
                    //value is in another tag
                    value = readValue(valuelocation, valueOffset, valueNb);
                }
                entries.put(valueKey, value);
            }

            //create the spatial metadatas.
            fillGridMetaDatas(spatialMetadata, entries);

            final GeoTiffCRSReader crsReader = new GeoTiffCRSReader(null);
            crsReader.fillCRSMetaDatas(spatialMetadata, entries);
        }
        
        //-- looks for additional informations
        final ThirdPartyMetaDataReader thirdReader = new ThirdPartyMetaDataReader(imageMetadata);
        thirdReader.fillSpatialMetaData(spatialMetadata);
        
        return spatialMetadata;
    }

    /**
     * Fill the spatial metadatas with the values available in the geotiff tags.
     */
    private void fillGridMetaDatas(final SpatialMetadata metadatas, final ValueMap entries) throws IOException{
        final GridDomainAccessor accesor = new GridDomainAccessor(metadatas);

        /*
         * FAQ GEOTIFF :
         * Setting the GTRasterTypeGeoKey value to RasterPixelIsPoint or RasterPixelIsArea
         * alters how the raster coordinate space is to be interpreted.
         * This is defined in section 2.5.2.2 of the GeoTIFF specification.
         * => In the case of PixelIsArea (default) a pixel is treated as an area
         * and the raster coordinate (0,0) is the top left corner of the top left pixel.
         * => PixelIsPoint treats pixels as point samples with empty space between the "pixel" samples.
         * In this case raster (0,0) is the location of the top left raster pixel.
         *
         * Note : GeoTiff mix the concepts of CellGeometry and PixelOrientation.
         */

        //get the raster type
        final Object value = entries.get(GTRasterTypeGeoKey);
        PixelOrientation orientation;
        if (value != null) {
            int type = (Integer)value;
            
            if(type < 1 || type > 2) throw new IOException("Unexpected raster type : "+ type);
            
            orientation = (type == RasterPixelIsArea) ? PixelOrientation.UPPER_LEFT : PixelOrientation.CENTER;
        } else {
            orientation = PixelOrientation.UPPER_LEFT;
        }
        final CellGeometry cellGeometry = (orientation == PixelOrientation.UPPER_LEFT)
                                          ? CellGeometry.AREA : CellGeometry.POINT;

        //read the image bounds
        final Rectangle bounds = readBounds();

        //check if a transformation is present /////////////////////////////////
        AffineTransform gridToCRS = readTransformation();
        if (gridToCRS == null) {
            //check for pixel scale and tie points /////////////////////////////////
            final double[] pixelScale = readPixelScale();
            final double[] tiePoint = readTiePoint();

            if (pixelScale == null && tiePoint != null) {

                final LocalizationGrid grid = new LocalizationGrid(2, 2);
                grid.setLocalizationPoint(0, 0, tiePoint[3], tiePoint[4]);
                grid.setLocalizationPoint(1, 0, tiePoint[9], tiePoint[10]);
                grid.setLocalizationPoint(1, 1, tiePoint[15], tiePoint[16]);
                grid.setLocalizationPoint(0, 1, tiePoint[21], tiePoint[22]);
                gridToCRS = grid.getAffineTransform();
                gridToCRS.scale(1f / bounds.width, 1f / bounds.height);

            } else if (pixelScale != null && tiePoint != null) {
                //TODO the is a third value in the tie point
                final double scaleX = pixelScale[0];
                final double scaleY = -pixelScale[1];
                final double tiePointColumn = tiePoint[0];
                final double tiePointRow = tiePoint[1];
                final double translateX = tiePoint[3] - (scaleX * tiePointColumn);
                final double translateY = tiePoint[4] - (scaleY * tiePointRow);
                gridToCRS = new AffineTransform(scaleX, 0, 0, scaleY, translateX, translateY);
            }
        }

        if (gridToCRS != null) {

            // force orientation to PixelOrientation.UPPER_LEFT to keep consistent transformation
            // when we add additional dimensions.
            if (PixelOrientation.CENTER.equals(orientation)) {
                LinearTransform linear = MathTransforms.linear(gridToCRS);
                LinearTransform translate = (LinearTransform)PixelTranslation.translate(linear, PixelInCell.CELL_CENTER, PixelInCell.CELL_CORNER);
                gridToCRS = AffineTransforms2D.castOrCopy(translate.getMatrix());
                orientation = PixelOrientation.UPPER_LEFT;
            }
            accesor.setAll(gridToCRS, bounds, cellGeometry, orientation);
            return;
        }

        //unknowned definition /////////////////////////////////////////////////
        throw new IOException("Unknowned Grid to CRS transformation definition.");
    }

    /**
     * Returns the image size from the tags : {@link GeoTiffConstants#ImageWidth} and {@link GeoTiffConstants#ImageLength}.<br>
     * May return {@code null}.
     * 
     * @return a {@link Rectangle} if expected geotiff tags are filled, else return {@code null}.
     */
    private Rectangle readBounds() {
        
        //--  get metadata node about image boundary
        final Node width  = getNodeByNumber(imgFileDir, ImageWidth);
        final Node height = getNodeByNumber(imgFileDir, ImageLength);
        if (width == null || height == null) return null;
        
        //-- study width
        int rectWidth = -1;
        
        //-- value can be stored in a short field
        Node widthNode = getNodeByLocalName(width, TAG_GEOTIFF_SHORTS);
        if(widthNode != null) rectWidth = readTiffShorts(widthNode)[0];
        
        //-- can be in a long field
        if (rectWidth == -1) {
            if(widthNode == null) widthNode = getNodeByLocalName(width, TAG_GEOTIFF_LONGS);
            if(widthNode != null) rectWidth = (int) readTiffLongs(widthNode)[0];
        }
        
        if (rectWidth == -1) {
            LOGGER.info("Unable to find geographic image boundary. Image width.");
            return null;
        }
        
        //-- study height
        int rectHeight = -1;
        //-- value can be stored in a short field
        Node heightNode = getNodeByLocalName(height, TAG_GEOTIFF_SHORTS);
        if(heightNode != null) rectHeight = readTiffShorts(heightNode)[0];
        
        //-- can be in a long field
        if (rectHeight == -1) {
            if(heightNode == null) heightNode = getNodeByLocalName(height, TAG_GEOTIFF_LONGS);
            if(heightNode != null) rectHeight = (int) readTiffLongs(heightNode)[0];
        }
            
        if (rectHeight == -1) {
            LOGGER.info("Unable to find geographic image boundary. Image height.");
            return null;
        }
        
        assert rectWidth  >= 0 : "SpatialMetadata : Geographic image boudary.width should be positive.";
        assert rectHeight >= 0 : "SpatialMetadata : Geographic image boudary.height should be positive.";
        
        return new Rectangle(rectWidth, rectHeight);
    }

    /**
     * Returns the transformation from the {@link GeoTIFFTagSet#TAG_MODEL_TRANSFORMATION} if it exist.<br>
     * May return {@code null}.
     * 
     * @return an {@link AffineTransform} if expected geotiff tags are filled, else return {@code null}.
     * @throws IOException if defined transformation is unknow. 
     */
    private AffineTransform readTransformation() throws IOException {

        final Node node = getNodeByNumber(imgFileDir, TAG_MODEL_TRANSFORMATION);
        if (node == null) return null;
        
        final Node valueNode = getNodeByLocalName(node, TAG_GEOTIFF_DOUBLES);
        if(valueNode == null) return null;
        
        //-- TODO find a multidimensional way
        final double[] matrix = readTiffDoubles(valueNode);
        if (matrix.length == 9) {
            //2D matrix
            return new AffineTransform(matrix[0],matrix[4],matrix[1],
                                       matrix[5],matrix[6],matrix[7]);
        } else if (matrix.length == 16) {
            //3D matrix, only keep the 2d part
            return new AffineTransform(matrix[0],matrix[4],matrix[1],
                                       matrix[5],matrix[3],matrix[7]);
        } else {
            throw new IOException("Unvalid transformation definition. expected 9 or 16 parameters but was "+matrix.length);
        }
    }

    /**
     * Read the tie points from the TAG_MODEL_TIE_POINT if it exist.
     */
    private double[] readTiePoint() {

        final Node node = getNodeByNumber(imgFileDir, TAG_MODEL_TIE_POINT);
        if (node == null) return null;
        
        final Node valueNode = getNodeByLocalName(node, TAG_GEOTIFF_DOUBLES);
        if (valueNode == null) return null;
        
        return readTiffDoubles(valueNode);
    }

    /**
     * Read the pixel scale from the TAG_MODEL_PIXEL_SCALE if it exist.
     */
    private double[] readPixelScale() {

        final Node node = getNodeByNumber(imgFileDir, TAG_MODEL_PIXEL_SCALE);
        if (node == null) return null;
        
        final Node valueNode = getNodeByLocalName(node, TAG_GEOTIFF_DOUBLES);
        if(valueNode == null) return null;
        
        return readTiffDoubles(valueNode);
    }

    /**
     * Read values for the given tag number.
     *
     * @param tagNumber
     * @param offset
     * @param lenght
     * @return
     * @throws IOException
     */
    private Object readValue(final int tagNumber, final int offset, final int lenght) throws IOException{
        final Node node = getNodeByNumber(imgFileDir, tagNumber);
        if(node == null){
            throw new IOException("Incorrect metadata description, no tag with number "+tagNumber);
        }

        //node should have a single subNode containing the value
        final Node valueNode = node.getChildNodes().item(0);
        if(valueNode == null){
            throw new IOException("Incorrect metadata description, no value in tag number "+tagNumber);
        }

        final String typeName = valueNode.getLocalName();

        final Object value;

        if (TAG_GEOTIFF_ASCII.equalsIgnoreCase(typeName)) {
            if (lenght != 1) {
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            value = readTiffAscii(valueNode);

        }else if(TAG_GEOTIFF_ASCIIS.equalsIgnoreCase(typeName)){
            value = readTiffAsciis(valueNode).substring(offset, offset+lenght);

        }else if(TAG_GEOTIFF_SHORT.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            value = readTiffShort(valueNode);

        }else if(TAG_GEOTIFF_SHORTS.equalsIgnoreCase(typeName)){

            final int[] shorts = readTiffShorts(valueNode);

            if(lenght == 1){
                value = shorts[offset];
            }else{
                value = new int[lenght];
                System.arraycopy(shorts, offset, value, 0, lenght);
            }

        }else if(TAG_GEOTIFF_LONG.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            value = readTiffLong(valueNode);

        }else if(TAG_GEOTIFF_LONGS.equalsIgnoreCase(typeName)){
            final long[] longs = readTiffLongs(valueNode);

            if(lenght == 1){
                value = longs[offset];
            }else{
                value = new long[lenght];
                System.arraycopy(longs, offset, value, 0, lenght);
            }

        }else if(TAG_GEOTIFF_DOUBLE.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            value = readTiffDouble(valueNode);

        }else if(TAG_GEOTIFF_DOUBLES.equalsIgnoreCase(typeName)){
            double[] doubles = readTiffDoubles(valueNode);
            if(lenght == 1){
                value = doubles[offset];
            }else{
                value = new double[lenght];
                System.arraycopy(doubles, offset, value, 0, lenght);
            }
        }else{
            throw new IOException("Incorrect metadata description, unknowned value type "
                    +typeName+" for tag number "+ tagNumber);
        }

        return value;
    }

    /**
     * @return String of tagsets on the image file directory node.
     */
    public String readTagSets(){
        return getAttributeValue(imgFileDir, ATT_TAGSETS);
    }

    @Override
    public String toString() {
        return Classes.getShortName(this.getClass()) +"\n"+Trees.toString(Trees.xmlToSwing(root));
    }
}
