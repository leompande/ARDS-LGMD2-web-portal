package org.hisp.dhis.importexport.dxf.converter;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import static org.hisp.dhis.importexport.dxf.converter.DXFConverter.MINOR_VERSION_11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.importer.OrganisationUnitImporter;
import org.hisp.dhis.organisationunit.CoordinatesTuple;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class OrganisationUnitConverter
    extends OrganisationUnitImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "organisationUnits";
    public static final String ELEMENT_NAME = "organisationUnit";
    
    private static final String FIELD_ID = "id";
    private static final String FIELD_UID = "uid";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_SHORT_NAME = "shortName";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_OPENING_DATE = "openingDate";
    private static final String FIELD_CLOSED_DATE = "closedDate";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_GEO_CODE = "geoCode";
    private static final String FIELD_COORDINATES_TUPLE = "coordinatesTuple";
    private static final String FIELD_COORDINATES = "coord";
    private static final String FIELD_FEATURE = "feature";
    private static final String FIELD_LAST_UPDATED = "lastUpdated";
    private static final String ATTRIBUTE_TYPE = "type";
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public OrganisationUnitConverter( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param organisationUnitService the organisationUnitService to use.
     * @param importObjectService the importObjectService to use.
     */
    public OrganisationUnitConverter( BatchHandler<OrganisationUnit> batchHandler, 
        ImportObjectService importObjectService, 
        OrganisationUnitService organisationUnitService,
        ImportAnalyser importAnalyser )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.organisationUnitService = organisationUnitService;
        this.importAnalyser = importAnalyser;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( params.getOrganisationUnits() );
        
        if ( units != null && units.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( OrganisationUnit unit : units )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( unit.getId() ) );
                writer.writeElement( FIELD_UID, unit.getUid() );
                writer.writeElement( FIELD_NAME, unit.getName() );
                writer.writeElement( FIELD_SHORT_NAME, unit.getShortName() );
                writer.writeElement( FIELD_CODE, unit.getCode() );
                writer.writeElement( FIELD_OPENING_DATE, DateUtils.getMediumDateString( unit.getOpeningDate() ) );
                writer.writeElement( FIELD_CLOSED_DATE, DateUtils.getMediumDateString( unit.getClosedDate() ) );
                writer.writeElement( FIELD_ACTIVE, String.valueOf( unit.isActive() ) );
                writer.writeElement( FIELD_COMMENT, unit.getComment() );
                writer.writeElement( FIELD_GEO_CODE, unit.getGeoCode() );

                writer.openElement( FIELD_FEATURE, ATTRIBUTE_TYPE, unit.getFeatureType() );
                
                for ( CoordinatesTuple tuple : unit.getCoordinatesAsList() )
                {
                    if ( tuple.hasCoordinates() )
                    {
                        writer.openElement( FIELD_COORDINATES_TUPLE );
                        
                        for ( String coordinates : tuple.getCoordinatesTuple() )
                        {
                            writer.writeElement( FIELD_COORDINATES, coordinates );
                        }
                        
                        writer.closeElement();
                    }
                }

                writer.closeElement();
                
                writer.writeElement( FIELD_LAST_UPDATED, DateUtils.getMediumDateString( unit.getLastUpdated(), EMPTY ) );
                
                writer.closeElement();
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final OrganisationUnit unit = new OrganisationUnit();

            reader.moveToStartElement( FIELD_ID );
            unit.setId( Integer.parseInt( reader.getElementValue() ) );
            
            if ( params.minorVersionGreaterOrEqual( "1.3") )
            {
                reader.moveToStartElement( FIELD_UID );
                unit.setUid( reader.getElementValue() );
            }


            reader.moveToStartElement( FIELD_NAME );
            unit.setName(reader.getElementValue() );
            
            reader.moveToStartElement( FIELD_SHORT_NAME );
            unit.setShortName( reader.getElementValue() );
            
            if ( params.minorVersionGreaterOrEqual( "1.2") )
            {
              reader.moveToStartElement( FIELD_CODE );
              unit.setCode( reader.getElementValue() );
            }
            
            reader.moveToStartElement( FIELD_OPENING_DATE );
            unit.setOpeningDate( DateUtils.getMediumDate( reader.getElementValue() ) );

            reader.moveToStartElement( FIELD_CLOSED_DATE );
            unit.setClosedDate( DateUtils.getMediumDate( reader.getElementValue() ) );
            
            reader.moveToStartElement( FIELD_ACTIVE );
            unit.setActive( Boolean.parseBoolean( reader.getElementValue() ) );
            
            reader.moveToStartElement( FIELD_COMMENT );
            unit.setComment( reader.getElementValue() );
            
            if ( params.minorVersionGreaterOrEqual( MINOR_VERSION_11 ) )
            {
                reader.moveToStartElement( FIELD_GEO_CODE );
                unit.setGeoCode( reader.getElementValue() );
                
                reader.moveToStartElement( FIELD_FEATURE );
                unit.setFeatureType( reader.getAttributeValue( ATTRIBUTE_TYPE ) );
                
                if ( unit.getFeatureType() != null )
                {
                    List<CoordinatesTuple> list = new ArrayList<CoordinatesTuple>();
                    
                    while ( reader.moveToStartElement( FIELD_COORDINATES_TUPLE, FIELD_FEATURE ) )
                    {
                        CoordinatesTuple tuple = new CoordinatesTuple();
                        
                        while ( reader.moveToStartElement( FIELD_COORDINATES, FIELD_COORDINATES_TUPLE ) )
                        {
                            tuple.addCoordinates( reader.getElementValue() );
                        }
                        
                        list.add( tuple );
                    }
                    
                    if ( unit.getFeatureType().equals( OrganisationUnit.FEATURETYPE_POINT ) )
                    {
                        unit.setPointCoordinatesFromList( list );
                    }                
                    else
                    {
                        unit.setMultiPolygonCoordinatesFromList( list );
                    }
                }
                
                reader.moveToStartElement( FIELD_LAST_UPDATED );
                unit.setLastUpdated( DateUtils.getMediumDate( reader.getElementValue() ) );
            }
            
            importObject( unit, params );
        }
    }
}
