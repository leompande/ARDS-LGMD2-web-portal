package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dxf2.metadata.ExportService;
import org.hisp.dhis.dxf2.metadata.FilterOptions;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.metadata.tasks.ImportMetaDataTask;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.filter.MetaDataFilter;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.scheduling.TaskCategory;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
@Controller
public class FilteredMetaDataController
{
    public static final String RESOURCE_PATH = "/filteredMetaData";

    @Autowired
    private ExportService exportService;

    @Qualifier( "contextUtils" )
    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ImportService importService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private Notifier notifier;

    private boolean dryRun;

    private ImportStrategy strategy;

    //--------------------------------------------------------------------------
    // Getters & Setters
    //--------------------------------------------------------------------------

    public void setDryRun( boolean dryRun )
    {
        this.dryRun = dryRun;
    }

    public void setStrategy( String strategy )
    {
        this.strategy = ImportStrategy.valueOf( strategy );
    }

    //--------------------------------------------------------------------------
    // Detailed MetaData Export - POST Requests
    //--------------------------------------------------------------------------

    @RequestMapping( value = FilteredMetaDataController.RESOURCE_PATH, headers = "Accept=application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public String detailedExport( @RequestParam Map<String, String> parameters, Model model )
    {
        WebOptions options = new WebOptions( parameters );
        MetaData metaData = exportService.getMetaData( options );

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", "export" );

        return "export";
    }

    @RequestMapping( method = RequestMethod.POST, value = FilteredMetaDataController.RESOURCE_PATH + ".xml", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportXml( @RequestParam String exportJsonValue, HttpServletResponse response ) throws IOException
    {
        FilterOptions filterOptions = new FilterOptions( JSONObject.fromObject( exportJsonValue ) );
        MetaData metaData = exportService.getFilteredMetaData( filterOptions );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, ContextUtils.CacheStrategy.NO_CACHE, "metaData.xml", true );
        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, ExportView.class );
    }

    @RequestMapping( method = RequestMethod.POST, value = FilteredMetaDataController.RESOURCE_PATH + ".json", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportJson( @RequestParam String exportJsonValue, HttpServletResponse response ) throws IOException
    {
        FilterOptions filterOptions = new FilterOptions( JSONObject.fromObject( exportJsonValue ) );
        MetaData metaData = exportService.getFilteredMetaData( filterOptions );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, ContextUtils.CacheStrategy.NO_CACHE, "metaData.json", true );
        JacksonUtils.toJsonWithView( response.getOutputStream(), metaData, ExportView.class );
    }

    @RequestMapping( method = RequestMethod.POST, value = { FilteredMetaDataController.RESOURCE_PATH + ".xml.zip" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedXML( @RequestParam String exportJsonValue, HttpServletResponse response ) throws IOException
    {
        FilterOptions filterOptions = new FilterOptions( JSONObject.fromObject( exportJsonValue ) );
        MetaData metaData = exportService.getFilteredMetaData( filterOptions );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_ZIP, ContextUtils.CacheStrategy.NO_CACHE, "metaData.xml.zip", true );
        response.addHeader( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );

        ZipOutputStream zip = new ZipOutputStream( response.getOutputStream() );
        zip.putNextEntry( new ZipEntry( "metaData.xml" ) );

        JacksonUtils.toXmlWithView( zip, metaData, ExportView.class );
    }

    @RequestMapping( method = RequestMethod.POST, value = { FilteredMetaDataController.RESOURCE_PATH + ".json.zip" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedJSON( @RequestParam String exportJsonValue, HttpServletResponse response ) throws IOException
    {
        FilterOptions filterOptions = new FilterOptions( JSONObject.fromObject( exportJsonValue ) );
        MetaData metaData = exportService.getFilteredMetaData( filterOptions );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_ZIP, ContextUtils.CacheStrategy.NO_CACHE, "metaData.json.zip", true );
        response.addHeader( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );

        ZipOutputStream zip = new ZipOutputStream( response.getOutputStream() );
        zip.putNextEntry( new ZipEntry( "metaData.json" ) );

        JacksonUtils.toJsonWithView( zip, metaData, ExportView.class );
    }

    @RequestMapping( method = RequestMethod.POST, value = { FilteredMetaDataController.RESOURCE_PATH + ".xml.gz" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZippedXML( @RequestParam String exportJsonValue, HttpServletResponse response ) throws IOException
    {
        FilterOptions filterOptions = new FilterOptions( JSONObject.fromObject( exportJsonValue ) );
        MetaData metaData = exportService.getFilteredMetaData( filterOptions );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_GZIP, ContextUtils.CacheStrategy.NO_CACHE, "metaData.xml.gz", true );
        response.addHeader( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );

        GZIPOutputStream gzip = new GZIPOutputStream( response.getOutputStream() );
        JacksonUtils.toXmlWithView( gzip, metaData, ExportView.class );
    }

    @RequestMapping( method = RequestMethod.POST, value = { FilteredMetaDataController.RESOURCE_PATH + ".json.gz" }, produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportGZippedJSON( @RequestParam String exportJsonValue, HttpServletResponse response ) throws IOException
    {
        FilterOptions filterOptions = new FilterOptions( JSONObject.fromObject( exportJsonValue ) );
        MetaData metaData = exportService.getFilteredMetaData( filterOptions );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_GZIP, ContextUtils.CacheStrategy.NO_CACHE, "metaData.json.gz", true );
        response.addHeader( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );

        GZIPOutputStream gzip = new GZIPOutputStream( response.getOutputStream() );
        JacksonUtils.toJsonWithView( gzip, metaData, ExportView.class );
    }

    //--------------------------------------------------------------------------
    // Detailed MetaData Export - Filter functionality
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET, value = FilteredMetaDataController.RESOURCE_PATH + "/getMetaDataFilters" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public @ResponseBody String getFilters( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        List<MetaDataFilter> metaDataFilters = exportService.getFilters();
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, ContextUtils.CacheStrategy.NO_CACHE );
        return JacksonUtils.toJsonAsString( metaDataFilters );
    }

    @RequestMapping( method = RequestMethod.POST, value = FilteredMetaDataController.RESOURCE_PATH + "/saveFilter" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void saveFilter( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        exportService.saveFilter( json );
    }

    @RequestMapping( method = RequestMethod.POST, value = FilteredMetaDataController.RESOURCE_PATH + "/updateFilter" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void updateFilter( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        exportService.updateFilter( json );
    }

    @RequestMapping( method = RequestMethod.POST, value = FilteredMetaDataController.RESOURCE_PATH + "/deleteFilter" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void deleteFilter( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        exportService.deleteFilter( json );
    }

    //--------------------------------------------------------------------------
    // Detailed MetaData Import - POST Requests
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, value = FilteredMetaDataController.RESOURCE_PATH + "/importDetailedMetaData" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importDetailedMetaData( @RequestBody JSONObject json, HttpServletResponse response ) throws IOException
    {
        strategy = ImportStrategy.valueOf( json.getString( "strategy" ) );
        dryRun = json.getBoolean( "dryRun" );

        TaskId taskId = new TaskId( TaskCategory.METADATA_IMPORT, currentUserService.getCurrentUser() );
        User user = currentUserService.getCurrentUser();

        MetaData metaData = new ObjectMapper().readValue( json.getString( "metaData" ), MetaData.class );

        ImportOptions importOptions = new ImportOptions();
        importOptions.setStrategy( strategy.toString() );
        importOptions.setDryRun( dryRun );

        scheduler.executeTask( new ImportMetaDataTask( user.getUid(), importService, importOptions, taskId, metaData ) );
    }
}
