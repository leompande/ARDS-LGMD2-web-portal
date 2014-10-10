package org.hisp.dhis.importexport.dhis14.xml.converter;

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

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.dhis14.util.Dhis14DateUtil;
import org.hisp.dhis.importexport.importer.OrganisationUnitImporter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import java.util.Collection;
import java.util.Map;

import static org.hisp.dhis.importexport.dhis14.util.Dhis14TypeHandler.convertBooleanFromDhis14;
import static org.hisp.dhis.importexport.dhis14.util.Dhis14TypeHandler.convertBooleanToDhis14;


/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class OrganisationUnitConverter
    extends OrganisationUnitImporter implements XMLConverter
{
    public static final String ELEMENT_NAME = "OrgUnit";
    
    private static final String FIELD_ID = "OrgUnitID";
    private static final String FIELD_CODE = "OrgUnitCode";
    private static final String FIELD_LEVEL = "OrgUnitLevel";
    private static final String FIELD_NAME = "OrgUnitName";
    private static final String FIELD_SHORT_NAME = "OrgUnitShort";
    private static final String FIELD_VALID_FROM = "ValidFrom";
    private static final String FIELD_VALID_TO = "ValidTo";
    private static final String FIELD_ACTIVE = "Active";
    private static final String FIELD_COMMENT = "Comment";
    private static final String FIELD_SELECTED = "Selected";
    private static final String FIELD_LAST_USER = "LastUserID";
    private static final String FIELD_LAST_UPDATED = "LastUpdated";

    private static final int VALID_FROM = 34335;
    private static final int VALID_TO = 2958465;

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
     * @param organisationUnitService the organisationUnitService to use.
     * @param importObjectService the importObjectService to use.
     */
    public OrganisationUnitConverter( ImportObjectService importObjectService, 
        OrganisationUnitService organisationUnitService,
        ImportAnalyser importAnalyser )
    {
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
            for ( OrganisationUnit unit : units )
            {
                int level = unit.getOrganisationUnitLevel();

                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( unit.getId() ) );
                writer.writeElement( FIELD_CODE, unit.getCode() );
                writer.writeElement( FIELD_LEVEL, String.valueOf( level ) );
                writer.writeElement( FIELD_NAME, unit.getName() );
                writer.writeElement( FIELD_SHORT_NAME, unit.getShortName() );
                writer.writeElement( FIELD_VALID_FROM, String.valueOf( VALID_FROM ) );
                writer.writeElement( FIELD_VALID_TO, String.valueOf( VALID_TO ) );
                writer.writeElement( FIELD_ACTIVE, convertBooleanToDhis14( unit.isActive() ) );
                writer.writeElement (FIELD_COMMENT, unit.getComment() );
                writer.writeElement( FIELD_SELECTED, String.valueOf( 0 ) );
                writer.writeElement( FIELD_LAST_USER, String.valueOf( 1 ) );
                writer.writeElement( FIELD_LAST_UPDATED, Dhis14DateUtil.getDateString( unit.getLastUpdated() ) );
                
                writer.closeElement();
            }
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        OrganisationUnit unit = new OrganisationUnit();
        
        Map<String, String> values = reader.readElements( ELEMENT_NAME );
        
        unit.setId( Integer.valueOf( values.get( FIELD_ID ) ) );
        unit.setName( values.get( FIELD_NAME ) );
        unit.setShortName( values.get( FIELD_SHORT_NAME ) );
        unit.setOpeningDate(   Dhis14DateUtil.getDate( Integer.parseInt( values.get( FIELD_VALID_FROM ) ) ) );
        unit.setClosedDate( Dhis14DateUtil.getDate( Integer.parseInt( values.get( FIELD_VALID_TO ) ) ) );
        unit.setActive( convertBooleanFromDhis14(values.get( FIELD_ACTIVE ) ) ) ;
        unit.setComment( values.get( FIELD_COMMENT ) );
        unit.setLastUpdated( Dhis14DateUtil.getDate( values.get( FIELD_LAST_UPDATED ) ) );
        importObject( unit, params );        
    }
}
