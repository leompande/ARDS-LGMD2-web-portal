package org.hisp.dhis.importexport.action.datavalue;

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

import static org.hisp.dhis.system.util.CodecUtils.filenameEncode;
import static org.hisp.dhis.system.util.DateUtils.getMediumDate;
import static org.hisp.dhis.util.ContextUtils.CONTENT_TYPE_CSV;
import static org.hisp.dhis.util.ContextUtils.CONTENT_TYPE_XML;
import static org.hisp.dhis.util.ContextUtils.getZipOut;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class ExportDataValueAction
    implements Action
{
    private final static String FILE_PREFIX = "Export";
    private final static String FILE_SEPARATOR = "_";
    private final static String EXTENSION_XML_ZIP = ".xml.zip";
    private final static String EXTENSION_CSV_ZIP = ".csv.zip";
    private final static String EXTENSION_XML = ".xml";
    private final static String EXTENSION_CSV = ".csv";
    private final static String FORMAT_CSV = "csv";

    @Autowired
    private SelectionTreeManager selectionTreeManager;

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private DataValueSetService dataValueSetService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Set<String> selectedDataSets;

    public void setSelectedDataSets( Set<String> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private String exportFormat;

    public void setExportFormat( String exportFormat )
    {
        this.exportFormat = exportFormat;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        //TODO reimplement to use web api
        
        Set<String> orgUnits = new HashSet<String>();
        
        for ( OrganisationUnit unit : selectionTreeManager.getReloadedSelectedOrganisationUnits() )
        {
            Collection<OrganisationUnit> children = organisationUnitService.getOrganisationUnitWithChildren( unit.getId() );
            
            for ( OrganisationUnit child : children )
            {
                orgUnits.add( child.getUid() );
            }
        }
        
        HttpServletResponse response = ServletActionContext.getResponse();
        
        if ( FORMAT_CSV.equals( exportFormat ) )
        {
            ContextUtils.configureResponse( response, CONTENT_TYPE_CSV, true, getFileName( EXTENSION_CSV_ZIP ), true );
            
            Writer writer = new OutputStreamWriter( getZipOut( response, getFileName( EXTENSION_CSV ) ) );
            
            dataValueSetService.writeDataValueSetCsv( selectedDataSets, getMediumDate( startDate ), getMediumDate( endDate ), orgUnits, writer );
        }
        else
        {
            ContextUtils.configureResponse( response, CONTENT_TYPE_XML, true, getFileName( EXTENSION_XML_ZIP ), true );
            
            dataValueSetService.writeDataValueSet( selectedDataSets, getMediumDate( startDate ), getMediumDate( endDate ), orgUnits, getZipOut( response, getFileName( EXTENSION_XML ) ) );
        }
                
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getFileName( String extension )
    {
        String fileName = FILE_PREFIX + FILE_SEPARATOR + startDate + FILE_SEPARATOR + endDate;
        
        if ( selectionTreeManager.getSelectedOrganisationUnits().size() == 1 )
        {
            fileName += FILE_SEPARATOR + filenameEncode( selectionTreeManager.getSelectedOrganisationUnits().iterator().next().getShortName() );
        }
        
        return fileName + extension;
    }
}
