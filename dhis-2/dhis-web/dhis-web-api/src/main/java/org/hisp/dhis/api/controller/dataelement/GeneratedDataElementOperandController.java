package org.hisp.dhis.api.controller.dataelement;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping(value = GeneratedDataElementOperandController.RESOURCE_PATH)
public class GeneratedDataElementOperandController
    extends AbstractCrudController<DataElementOperand>
{
    public static final String RESOURCE_PATH = "/generatedDataElementOperands";

    @Autowired
    private DataElementCategoryService categoryService;

    @Override
    protected List<DataElementOperand> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<DataElement> dataElements = null;
        
        if ( options.getOptions().containsKey( "dataElementGroup" ) )
        {
            DataElementGroup group = manager.get( DataElementGroup.class, options.getOptions().get( "dataElementGroup" ) );

            if ( group != null )
            {
                dataElements = new ArrayList<DataElement>( group.getMembers() );

                Collections.sort( dataElements, IdentifiableObjectNameComparator.INSTANCE );
            }
        }
        else
        {
            dataElements = new ArrayList<DataElement>( manager.getAllSorted( DataElement.class ) );
        }
        
        List<DataElementOperand> entityList = new ArrayList<DataElementOperand>( categoryService.getOperands( dataElements ) );
        
        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), entityList.size(), options.getPageSize() );
            metaData.setPager( pager );
            entityList = PagerUtils.pageCollection( entityList, pager );            
        }

        return entityList;
    }

    @Override
    @RequestMapping( value = "/query/{query}", method = RequestMethod.GET )
    public String query( @PathVariable String query, @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        WebMetaData metaData = new WebMetaData();

        List<DataElementOperand> allOperands = new ArrayList<DataElementOperand>( categoryService.getOperands( manager.getAllSorted( DataElement.class ) ) );
        List<DataElementOperand> dataElementOperands = new ArrayList<DataElementOperand>();
        
        //TODO this will not scale well

        for ( DataElementOperand operand : allOperands )
        {
            if ( operand.getDisplayName().toLowerCase().contains( query.toLowerCase() ) )
            {
                dataElementOperands.add( operand );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataElementOperands.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataElementOperands = PagerUtils.pageCollection( dataElementOperands, pager );
        }

        metaData.setDataElementOperands( dataElementOperands );

        String viewClass = options.getViewClass( "basic" );

        if ( viewClass.equals( "basic" ) || viewClass.equals( "sharingBasic" ) )
        {
            handleLinksAndAccess( options, metaData, dataElementOperands, false );
        }
        else
        {
            handleLinksAndAccess( options, metaData, dataElementOperands, true );
        }

        postProcessEntities( dataElementOperands );
        postProcessEntities( dataElementOperands, options, parameters );

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", viewClass );

        return StringUtils.uncapitalize( getEntitySimpleName() ) + "List";
    }
}
