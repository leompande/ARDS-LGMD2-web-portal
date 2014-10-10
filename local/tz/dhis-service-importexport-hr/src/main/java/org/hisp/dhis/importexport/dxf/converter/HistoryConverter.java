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
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.History;
import org.hisp.dhis.hr.HistoryService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.importexport.HrExportParams;
import org.hisp.dhis.importexport.ImportHrObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLHrConverter;
import org.hisp.dhis.importexport.importer.HistoryImporter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.util.DateUtils;


/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class HistoryConverter
    extends HistoryImporter implements XMLHrConverter
{
    public static final String COLLECTION_NAME = "histories";
    public static final String ELEMENT_NAME = "history";
    
    private static final String FIELD_ID = "historyId";
    private static final String FIELD_PERSONID = "personId";
    private static final String FIELD_HISTORY = "history";
    private static final String FIELD_REASON = "reason";
    private static final String FIELD_STARTDATE = "startDate";
    
    private static final String FIELD_ATTRIBUTEID = "attributeId";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> personMapping;
    private Map<Object, Integer> attributeMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public HistoryConverter( HistoryService historyService )
    {
    	this.historyService = historyService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryService the dataElementCategoryService to use.
     */
    public HistoryConverter( BatchHandler<History> batchHandler, 
        ImportHrObjectService importObjectService,
        Map<Object, Integer> personMapping,
        Map<Object, Integer> attributeMapping,
        HistoryService historyService )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.personMapping = personMapping;
        this.attributeMapping = attributeMapping;
        this.historyService = historyService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, HrExportParams params )
    {
        Collection<History> histories = historyService.getAllHistory();
        
        if ( histories != null && histories.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( History history : histories )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( history.getId() ) );
                writer.writeElement( FIELD_HISTORY, history.getHistory() );
                writer.writeElement( FIELD_REASON, history.getReason() );
                writer.writeElement( FIELD_STARTDATE, DateUtils.getMediumDateString( history.getStartDate(), EMPTY ) );
                writer.writeElement(FIELD_PERSONID, String.valueOf( history.getPerson().getId()) );
                writer.writeElement(FIELD_ATTRIBUTEID, String.valueOf(history.getAttribute().getId()));
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
            
            final History history = new History();
            
            final Person person = new Person();
            history.setPerson(person);
            
            final Attribute attribute = new Attribute();
            history.setAttribute( attribute );
            
            history.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            history.setHistory( values.get( FIELD_HISTORY ) );
            history.setReason( values.get( FIELD_REASON ) );
            history.setStartDate( DateUtils.getMediumDate( values.get( FIELD_STARTDATE ) ) );
            history.getPerson().setId( personMapping.get( Integer.parseInt( values.get( FIELD_PERSONID ) ) ) );
            history.getAttribute().setId( attributeMapping.get( Integer.parseInt( values.get( FIELD_ATTRIBUTEID ) ) ) );
            
            importObject( history, params );
            
            
        }
    }
}
