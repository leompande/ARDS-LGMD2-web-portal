package org.hisp.dhis.importexport.dxf.converter;

/*
 * Copyright (c) 2004-2012, University of Oslo, University Of Dar es salaam
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.hr.Training;
import org.hisp.dhis.hr.TrainingService;
import org.hisp.dhis.importexport.HrExportParams;
import org.hisp.dhis.importexport.ImportHrObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLHrConverter;
import org.hisp.dhis.importexport.importer.TrainingImporter;
import org.hisp.dhis.system.util.DateUtils;


/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class TrainingConverter
    extends TrainingImporter implements XMLHrConverter
{
    public static final String COLLECTION_NAME = "trainings";
    public static final String ELEMENT_NAME = "training";
    
    private static final String FIELD_ID = "trainingId";
    private static final String FIELD_NAME = "trainingName";
    private static final String FIELD_PERSONID = "personId";
    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_SPONSOR = "sponsor";
    private static final String FIELD_STARTDATE = "startDate";
    private static final String FIELD_ENDDATE = "endDate";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> personMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public TrainingConverter( TrainingService trainingService )
    {
    	this.trainingService = trainingService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryService the dataElementCategoryService to use.
     */
    public TrainingConverter( BatchHandler<Training> batchHandler, 
        ImportHrObjectService importObjectService,
        Map<Object, Integer> personMapping,
        TrainingService trainingService )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.personMapping = personMapping;
        this.trainingService = trainingService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, HrExportParams params )
    {
        Collection<Training> trainings = trainingService.getAllTraining();
        
        if ( trainings != null && trainings.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( Training training : trainings )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( training.getId() ) );
                writer.writeElement( FIELD_NAME, training.getName() );
                writer.writeElement(FIELD_PERSONID, String.valueOf(training.getPerson().getId()));
                writer.writeElement(FIELD_LOCATION, training.getLocation() );
                writer.writeElement( FIELD_SPONSOR, training.getSponsor() );
                writer.writeElement( FIELD_STARTDATE, DateUtils.getMediumDateString(training.getStartDate(), EMPTY ) );
                writer.writeElement( FIELD_ENDDATE, DateUtils.getMediumDateString(training.getEndDate(), EMPTY ) );
                
                writer.closeElement();
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final Training training = new Training();
            
            training.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            training.setName( values.get( FIELD_NAME ) );
            training.getPerson().setId( personMapping.get( Integer.parseInt( values.get( FIELD_PERSONID ) ) ) );
            training.setLocation( values.get( FIELD_LOCATION ) );
            training.setSponsor( values.get( FIELD_SPONSOR ) );
            training.setStartDate( DateUtils.getMediumDate( values.get( FIELD_STARTDATE ) ) );
            training.setEndDate( DateUtils.getMediumDate( values.get( FIELD_ENDDATE ) ) );
            
            importObject( training, params );
        }
    }
}
