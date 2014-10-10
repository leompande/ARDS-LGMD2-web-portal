package org.hisp.dhis.dd.action.category;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.concept.ConceptService;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class UpdateDataElementCategoryAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private ConceptService conceptService;

    public void setConceptService( ConceptService conceptService )
    {
        this.conceptService = conceptService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private boolean dataDimension;

    public void setDataDimension( boolean dataDimension )
    {
        this.dataDimension = dataDimension;
    }

    private Integer conceptId;

    public void setConceptId( Integer conceptId )
    {
        this.conceptId = conceptId;
    }

    private List<String> selectedList = new ArrayList<String>();

    public void setSelectedList( List<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        DataElementCategory dataElementCategory = dataElementCategoryService.getDataElementCategory( id );
        dataElementCategory.setName( name );
        dataElementCategory.setDataDimension( dataDimension );
        dataElementCategory.setConcept( conceptService.getConcept( conceptId ) );

        List<DataElementCategoryOption> options = new ArrayList<DataElementCategoryOption>();

        for ( String id : selectedList )
        {
            options.add( dataElementCategoryService.getDataElementCategoryOption( Integer.parseInt( id ) ) );
        }

        dataElementCategory.setCategoryOptions( options );

        dataElementCategoryService.updateDataElementCategory( dataElementCategory );

        return SUCCESS;
    }
}
