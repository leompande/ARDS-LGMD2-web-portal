package org.hisp.dhis.dataset.action;

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

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author mortenoh
 */
public class EditDataSetFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private List<PeriodType> periodTypes = new ArrayList<PeriodType>();

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    private List<UserGroup> userGroups = new ArrayList<UserGroup>();

    public List<UserGroup> getUserGroups()
    {
        return userGroups;
    }

    private DataSet dataSet;

    public DataSet getDataSet()
    {
        return dataSet;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private List<Indicator> indicators = new ArrayList<Indicator>();

    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    private List<DataElementCategoryCombo> categoryCombos = new ArrayList<DataElementCategoryCombo>();
    
    public List<DataElementCategoryCombo> getCategoryCombos()
    {
        return categoryCombos;
    }

    private List<MapLegendSet> legendSets;

    public List<MapLegendSet> getLegendSets()
    {
        return legendSets;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        periodTypes = periodService.getAllPeriodTypes();
        userGroups = new ArrayList<UserGroup>( userGroupService.getAllUserGroups() );
        categoryCombos = new ArrayList<DataElementCategoryCombo>( categoryService.getAttributeCategoryCombos() );
        legendSets = new ArrayList<MapLegendSet>( mappingService.getAllMapLegendSets() );
        
        if ( dataSetId != null )
        {
            dataSet = dataSetService.getDataSet( dataSetId, true, true, false );
            dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
            indicators = new ArrayList<Indicator>( dataSet.getIndicators() );
        }

        Collections.sort( userGroups, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataElements, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( indicators, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( legendSets, IdentifiableObjectNameComparator.INSTANCE );

        return SUCCESS;
    }
}
