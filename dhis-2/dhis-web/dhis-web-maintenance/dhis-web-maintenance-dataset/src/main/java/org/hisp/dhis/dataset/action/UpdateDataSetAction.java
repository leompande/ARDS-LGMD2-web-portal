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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.UserGroupService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.hisp.dhis.system.util.TextUtils.equalsNullSafe;
import static org.hisp.dhis.system.util.TextUtils.nullIfEmpty;

/**
 * @author Kristian
 * @version $Id: UpdateDataSetAction.java 6255 2008-11-10 16:01:24Z larshelg $
 */
public class UpdateDataSetAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private SectionService sectionService;

    public void setSectionService( SectionService sectionService )
    {
        this.sectionService = sectionService;
    }

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private int expiryDays;

    public void setExpiryDays( int expiryDays )
    {
        this.expiryDays = expiryDays;
    }

    private int timelyDays;

    public void setTimelyDays( int timelyDays )
    {
        this.timelyDays = timelyDays;
    }

    private int notificationRecipients;

    public void setNotificationRecipients( int notificationRecipients )
    {
        this.notificationRecipients = notificationRecipients;
    }

    private boolean notifyCompletingUser;

    public void setNotifyCompletingUser( boolean notifyCompletingUser )
    {
        this.notifyCompletingUser = notifyCompletingUser;
    }

    private boolean approveData;

    public void setApproveData( boolean approveData )
    {
        this.approveData = approveData;
    }

    private boolean skipAggregation;

    public void setSkipAggregation( boolean skipAggregation )
    {
        this.skipAggregation = skipAggregation;
    }

    private String frequencySelect;

    public void setFrequencySelect( String frequencySelect )
    {
        this.frequencySelect = frequencySelect;
    }

    private int dataSetId;

    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private boolean allowFuturePeriods;

    public void setAllowFuturePeriods( boolean allowFuturePeriods )
    {
        this.allowFuturePeriods = allowFuturePeriods;
    }

    private boolean fieldCombinationRequired;

    public void setFieldCombinationRequired( boolean fieldCombinationRequired )
    {
        this.fieldCombinationRequired = fieldCombinationRequired;
    }

    private boolean validCompleteOnly;

    public void setValidCompleteOnly( boolean validCompleteOnly )
    {
        this.validCompleteOnly = validCompleteOnly;
    }

    private boolean skipOffline;

    public void setSkipOffline( boolean skipOffline )
    {
        this.skipOffline = skipOffline;
    }

    private boolean dataElementDecoration;

    public void setDataElementDecoration( boolean dataElementDecoration )
    {
        this.dataElementDecoration = dataElementDecoration;
    }

    private boolean renderAsTabs;

    public void setRenderAsTabs( boolean renderAsTabs )
    {
        this.renderAsTabs = renderAsTabs;
    }

    private boolean renderHorizontally;

    public void setRenderHorizontally( boolean renderHorizontally )
    {
        this.renderHorizontally = renderHorizontally;
    }

    private Collection<String> dataElementsSelectedList = new HashSet<String>();

    public void setDataElementsSelectedList( Collection<String> dataElementsSelectedList )
    {
        this.dataElementsSelectedList = dataElementsSelectedList;
    }

    private Collection<String> indicatorsSelectedList = new HashSet<String>();

    public void setIndicatorsSelectedList( Collection<String> indicatorsSelectedList )
    {
        this.indicatorsSelectedList = indicatorsSelectedList;
    }

    private Integer categoryComboId;
    
    public void setCategoryComboId( Integer categoryComboId )
    {
        this.categoryComboId = categoryComboId;
    }

    private Integer selectedLegendSetId;

    public void setSelectedLegendSetId( Integer selectedLegendSetId )
    {
        this.selectedLegendSetId = selectedLegendSetId;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        code = nullIfEmpty( code );
        shortName = nullIfEmpty( shortName );
        description = nullIfEmpty( description );

        Set<DataElement> dataElements = new HashSet<DataElement>();

        MapLegendSet legendSet = mappingService.getMapLegendSet( selectedLegendSetId );

        for ( String id : dataElementsSelectedList )
        {
            dataElements.add( dataElementService.getDataElement( Integer.parseInt( id ) ) );
        }

        Set<Indicator> indicators = new HashSet<Indicator>();

        for ( String id : indicatorsSelectedList )
        {
            indicators.add( indicatorService.getIndicator( Integer.parseInt( id ) ) );
        }

        PeriodType periodType = periodService.getPeriodTypeByName( frequencySelect );

        DataSet dataSet = dataSetService.getDataSet( dataSetId );

        dataSet.setExpiryDays( expiryDays );
        dataSet.setTimelyDays( timelyDays );
        dataSet.setSkipAggregation( skipAggregation );

        if ( !( equalsNullSafe( name, dataSet.getName() ) &&
            periodType.equals( dataSet.getPeriodType() ) &&
            dataElements.equals( dataSet.getDataElements() ) &&
            indicators.equals( dataSet.getIndicators() ) &&
            renderAsTabs == dataSet.isRenderAsTabs()) )
        {
            dataSet.increaseVersion(); // Check if version must be updated
        }

        dataSet.setName( name );
        dataSet.setShortName( shortName );
        dataSet.setDescription( description );
        dataSet.setCode( code );
        dataSet.setPeriodType( periodService.getPeriodTypeByClass( periodType.getClass() ) );
        dataSet.updateDataElements( dataElements );
        dataSet.setIndicators( indicators );
        dataSet.setAllowFuturePeriods( allowFuturePeriods );
        dataSet.setFieldCombinationRequired( fieldCombinationRequired );
        dataSet.setValidCompleteOnly( validCompleteOnly );
        dataSet.setNotifyCompletingUser( notifyCompletingUser );
        dataSet.setApproveData( approveData );
        dataSet.setSkipOffline( skipOffline );
        dataSet.setDataElementDecoration( dataElementDecoration );
        dataSet.setRenderAsTabs( renderAsTabs );
        dataSet.setRenderHorizontally( renderHorizontally );
        dataSet.setNotificationRecipients( userGroupService.getUserGroup( notificationRecipients ) );
        dataSet.setLegendSet( legendSet );

        if ( categoryComboId != null )
        {
            dataSet.setCategoryCombo( categoryService.getDataElementCategoryCombo( categoryComboId ) );
        }
        
        dataSetService.updateDataSet( dataSet );

        // ---------------------------------------------------------------------
        // Remove data elements which are removed in data set from sections
        // ---------------------------------------------------------------------

        for ( Section section : dataSet.getSections() )
        {
            if ( section.getDataElements().retainAll( dataSet.getDataElements() ) )
            {
                sectionService.updateSection( section );
            }
        }

        return SUCCESS;
    }
}
