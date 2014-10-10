package org.hisp.dhis.caseentry.action.caseentry;

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
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Chau Thu Tran
 * @version $GetOptionsByDataElementAction.java Jun 15, 2012 10:36:29 AM$
 */
public class GetOptionsByDataElementAction
    implements Action
{
    private static Integer MAX_OPTIONS_DISPLAYED = 30;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OptionService optionService;

    @Autowired
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }

    private DataElementService dataElementService;

    @Autowired
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String id;

    public void setId( String id )
    {
        this.id = id;
    }

    private String query;

    public void setQuery( String query )
    {
        this.query = query;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<String> options;

    public List<String> getOptions()
    {
        return options;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        query = StringUtils.trimToNull( query );

        OptionSet optionSet = optionService.getOptionSet( id );

        // retry using id as dataElementId
        if ( optionSet == null )
        {
            DataElement dataElement = dataElementService.getDataElement( id );

            if ( dataElement != null )
            {
                optionSet = dataElement.getOptionSet();
            }
        }

        if ( optionSet == null )
        {
            return INPUT;
        }

        // ---------------------------------------------------------------------
        // If the query is null and the option set has not changed since last
        // request we can tell the client to use its cached response (304)
        // ---------------------------------------------------------------------

        boolean isNotModified = (query == null && ContextUtils.isNotModified( ServletActionContext.getRequest(),
            ServletActionContext.getResponse(), optionSet ));

        if ( !isNotModified )
        {
            options = optionService.getOptions( optionSet.getId(), query, MAX_OPTIONS_DISPLAYED );
        }

        return SUCCESS;
    }
}
