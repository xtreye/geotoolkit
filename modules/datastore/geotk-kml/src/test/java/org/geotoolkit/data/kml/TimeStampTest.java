package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.TimeSpan;
import org.geotoolkit.data.kml.model.TimeStamp;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.utilities.DateUtilities;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class TimeStampTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/timeStamp.kml";

    public TimeStampTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void timeStampReadTest() throws IOException, XMLStreamException, ParseException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Placemark);
        Placemark placemark = (Placemark) feature;
        assertEquals("Colorado", placemark.getName());

        assertTrue(placemark.getTimePrimitive() instanceof TimeStamp);
        TimeStamp timeStamp = (TimeStamp) placemark.getTimePrimitive();
        String when = "1876-08-02T22:31:54.543+01:00";

        DateUtilities du = new DateUtilities();
        Calendar calendarWhen = du.getCalendar(when);
        assertEquals(calendarWhen, timeStamp.getWhen());
        assertEquals(when, du.getFormatedString(false));
    }

    @Test
    public void timeStampWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Calendar when = Calendar.getInstance();
        when.set(Calendar.YEAR, 1876);
        when.set(Calendar.MONTH, 7);
        when.set(Calendar.DAY_OF_MONTH, 2);
        when.set(Calendar.HOUR_OF_DAY, 22);
        when.set(Calendar.MINUTE, 31);
        when.set(Calendar.SECOND, 54);
        when.set(Calendar.MILLISECOND, 543);
        when.set(Calendar.ZONE_OFFSET, 3600000);

        TimeStamp timeStamp = kmlFactory.createTimeStamp();
        timeStamp.setWhen(when);

        Placemark placemark = kmlFactory.createPlacemark();
        placemark.setName("Colorado");
        placemark.setTimePrimitive(timeStamp);
        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        File temp = File.createTempFile("timeStampTest", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }

}