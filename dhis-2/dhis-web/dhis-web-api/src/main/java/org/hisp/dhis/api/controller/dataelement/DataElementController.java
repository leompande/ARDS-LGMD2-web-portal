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

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping(value = DataElementController.RESOURCE_PATH)
public class DataElementController
    extends AbstractCrudController<DataElement>
{
    public static final String RESOURCE_PATH = "/dataElements";

    @Autowired
    private DataElementService dataElementService;

    protected List<DataElement> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<DataElement> entityList;

        Date lastUpdated = options.getLastUpdated();

        String KEY_DOMAIN_TYPE = "domainType";

        if ( DataElement.DOMAIN_TYPE_AGGREGATE.equals( options.getOptions().get( KEY_DOMAIN_TYPE ) )
            || DataElement.DOMAIN_TYPE_PATIENT.equals( options.getOptions().get( KEY_DOMAIN_TYPE ) ) )
        {
            String domainType = options.getOptions().get( KEY_DOMAIN_TYPE );

            if ( options.hasPaging() )
            {
                int count = dataElementService.getDataElementCountByDomainType( domainType );

                Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
                metaData.setPager( pager );

                entityList = new ArrayList<DataElement>( dataElementService.getDataElementsByDomainType( domainType, pager.getOffset(), pager.getPageSize() ) );
            }
            else
            {
                entityList = new ArrayList<DataElement>( dataElementService.getDataElementsByDomainType( domainType ) );
                Collections.sort( entityList, IdentifiableObjectNameComparator.INSTANCE );
            }
        }
        else if ( lastUpdated != null )
        {
            entityList = new ArrayList<DataElement>( manager.getByLastUpdatedSorted( getEntityClass(), lastUpdated ) );
        }
        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            entityList = new ArrayList<DataElement>( manager.getBetween( getEntityClass(), pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<DataElement>( manager.getAllSorted( getEntityClass() ) );
        }

        return entityList;
    }
}
