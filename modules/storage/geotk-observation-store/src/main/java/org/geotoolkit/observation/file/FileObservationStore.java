/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.observation.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.AbstractObservationStore;
import static org.geotoolkit.observation.file.FileObservationStoreFactory.FILE_PATH;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FileObservationStore extends AbstractObservationStore {
    
    private final File dataFile;
    
    public FileObservationStore(final ParameterValueGroup params) {
        super(params);
        dataFile = (File) params.parameter(FILE_PATH.getName().toString()).getValue();
    }

    /**
     * @return the dataFile
     */
    public File getDataFile() {
        return dataFile;
    }
    
    @Override
    public void close() throws DataStoreException {
        // do nothing
    }

    @Override
    public Set<Name> getProcedureNames() {
        return new HashSet<>();
    }
}
