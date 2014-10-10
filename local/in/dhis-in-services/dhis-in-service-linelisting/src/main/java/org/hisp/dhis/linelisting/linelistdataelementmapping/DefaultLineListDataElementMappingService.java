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
package org.hisp.dhis.linelisting.linelistdataelementmapping;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultLineListDataElementMappingService.java Oct 12, 2010 11:52:43 AM
 */
@Transactional
public class DefaultLineListDataElementMappingService implements LineListDataElementMappingService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListDataElementMappingStore lineListDataElementMappingStore;

    public void setLineListDataElementMappingStore( LineListDataElementMappingStore lineListDataElementMappingStore )
    {
        this.lineListDataElementMappingStore = lineListDataElementMappingStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // LineListDataElementMapping
    // -------------------------------------------------------------------------

    public int addLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        int id = lineListDataElementMappingStore.addLineListDataElementMapping( lineListDataElementMapping );

        //i18nService.addObject( lineListDataElementMapping );

        return id;
    }

    public void deleteLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        i18nService.removeObject( lineListDataElementMapping );

        lineListDataElementMappingStore.deleteLineListDataElementMapping( lineListDataElementMapping );
    }

    public LineListDataElementMapping getLineListDataElementMapping( int id )
    {
        return lineListDataElementMappingStore.getLineListDataElementMapping( id );
    }

    public Collection<LineListDataElementMapping> getAllLineListDataElementMappings()
    {
        return lineListDataElementMappingStore.getAllLineListDataElementMappings();
    }

    public void updateLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        lineListDataElementMappingStore.updateLineListDataElementMapping( lineListDataElementMapping );
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    public String convertDataElementExpression( String arg0, Map<Object, Integer> arg1, Map<Object, Integer> arg2 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String convertLineListDataElementMapping( String arg0, Map<Object, Integer> arg1, Map<Object, Integer> arg2,
        Map<Object, Integer> arg3 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int expressionIsValid( String arg0, String arg1 )
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public Set<DataElementCategoryOptionCombo> getCategoryOptionCombosInExpression( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDataElementExpressionDescription( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<DataElement> getDataElementsInExpression( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLineListDataElementMappingDescription( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<LineListElement> getLineListElementsInLineListDataElementMapping( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<LineListGroup> getLineListGroupsInLineListDataElementMapping( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<LineListOption> getLineListOptionsInLineListDataElementMapping( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<DataElementOperand> getOperandsInDataElementExpression( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<LineListOperand> getOperandsInLineListDataElementMapping( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
