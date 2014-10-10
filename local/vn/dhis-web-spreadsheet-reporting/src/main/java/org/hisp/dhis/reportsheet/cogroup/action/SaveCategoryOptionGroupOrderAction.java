package org.hisp.dhis.reportsheet.cogroup.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.reportsheet.CategoryOptionGroupOrder;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ExportReportVerticalCategory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class SaveCategoryOptionGroupOrderAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private ExportReportService exportReportService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private String name;

    private String clazzName;

    private List<String> categoryOptionIds = new ArrayList<String>();

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setName( String name )
    {
        this.name = name;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public void setClazzName( String clazzName )
    {
        this.clazzName = clazzName;
    }

    public void setCategoryOptionIds( List<String> categoryOptionIds )
    {
        this.categoryOptionIds = categoryOptionIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public String execute()
        throws Exception
    {
        CategoryOptionGroupOrder categoryOptionGroupOrder = new CategoryOptionGroupOrder( name );

        List<String> finalList = new ArrayList<String>();
        List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

        removeDuplicatedItems( categoryOptionIds, finalList );

        for ( String id : this.categoryOptionIds )
        {
            DataElementCategoryOption categoryOption = categoryService.getDataElementCategoryOption( Integer
                .parseInt( id ) );
            categoryOptions.add( categoryOption );
        }

        categoryOptionGroupOrder.setCategoryOptions( categoryOptions );

        if ( clazzName.equals( ExportReport.class.getSimpleName() ) )
        {
            ExportReportVerticalCategory exportReportVerticalCategory = (ExportReportVerticalCategory) exportReportService
                .getExportReport( id );

            List<CategoryOptionGroupOrder> categoryOptionGroupOrders = exportReportVerticalCategory
                .getCategoryOptionGroupOrders();

            categoryOptionGroupOrders.add( categoryOptionGroupOrder );

            exportReportVerticalCategory.setCategoryOptionGroupOrders( categoryOptionGroupOrders );

            exportReportService.updateExportReport( exportReportVerticalCategory );
        }

        categoryOptions = null;

        return SUCCESS;
    }

    private static void removeDuplicatedItems( List<String> a, List<String> b )
    {
        for ( String s1 : a )
        {
            if ( !b.contains( s1 ) )
            {
                b.add( s1 );
            }
        }
    }
}
