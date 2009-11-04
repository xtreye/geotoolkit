
package org.geotoolkit.internal.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CoordinateReferenceSystemAdapter  extends XmlAdapter<String, CoordinateReferenceSystem> {

    @Override
    public CoordinateReferenceSystem unmarshal(String v) throws Exception {
        if (v != null) {
            return CRS.decode(v);
        }
        return null;
    }

    @Override
    public String marshal(CoordinateReferenceSystem v) throws Exception {
        if (v != null) {
            return CRS.lookupIdentifier(v, true);
        }
        return null;
    }

}
