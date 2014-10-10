package org.hisp.dhis.ll.action.lldataentry;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.linelisting.comparator.LineListGroupNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: SelectAction.java 4746 2008-03-13 20:02:29Z abyot $
 */
public class SelectAction
    implements Action
{

    private static final String DEFAULT_FORM = "defaultform";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private SectionService sectionService;

    public SectionService getSectionService()
    {
        return sectionService;
    }

    public void setSectionService( SectionService sectionService )
    {
        this.sectionService = sectionService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private List<LineListGroup> lineListGroups = new ArrayList<LineListGroup>();

    public Collection<LineListGroup> getLineListGroups()
    {
        return lineListGroups;
    }

    private List<LineListOption> lineListOptions;
    
    public List<LineListOption> getLineListOptions()
    {
        return lineListOptions;
    }

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    public SelectedStateManager getSelectedStateManager()
    {
        return selectedStateManager;
    }

    LineListGroup selectedLineListGroup;

    public LineListGroup getSelectedLineListGroup()
    {
        return selectedLineListGroup;
    }
    
    LineListOption selectedLineListOption;
    
    public LineListOption getSelectedLineListOption()
    {
        return selectedLineListOption;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private String selectedLineListOptionId;
    
    public String getSelectedLineListOptionId()
    {
        return selectedLineListOptionId;
    }

    public void setSelectedLineListOptionId( String selectedLineListOptionId )
    {
        this.selectedLineListOptionId = selectedLineListOptionId;
    }

    private String useDefaultForm;

    public String getUseDefaultForm()
    {
        return useDefaultForm;
    }

    public void setUseDefaultForm( String useDefaultForm )
    {
        this.useDefaultForm = useDefaultForm;
    }

    private Integer selectedLineListGroupId;

    public Integer getSelectedLineListGroupId()
    {
        return selectedLineListGroupId;
    }

    public void setSelectedLineListGroupId( Integer selectedLineListGroupId )
    {
        this.selectedLineListGroupId = selectedLineListGroupId;
    }

    private Integer selectedPeriodIndex;

    public void setSelectedPeriodIndex( Integer selectedPeriodIndex )
    {
        this.selectedPeriodIndex = selectedPeriodIndex;
    }

    public Integer getSelectedPeriodIndex()
    {
        return selectedPeriodIndex;
    }

    private String useShortName;

    public void setUseShortName( String useShortName )
    {
        this.useShortName = useShortName;
    }

    public String getUseShortName()
    {
        return useShortName;
    }

    private String curDate;

    public String getCurDate()
    {
        return curDate;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        curDate = simpleDateFormat.format( new Date() );

        // ---------------------------------------------------------------------
        // Validate selected OrganisationUnit
        // ---------------------------------------------------------------------

        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
        {
            selectedLineListGroupId = null;
            selectedLineListOptionId = null;
            selectedPeriodIndex = null;

            selectedStateManager.clearSelectedLineListGroup();
            selectedStateManager.clearSelectedLineListOption();
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Load LineListGroups
        // ---------------------------------------------------------------------
        lineListGroups = selectedStateManager.loadLineListGroupsForSelectedOrgUnit( organisationUnit );

        // ---------------------------------------------------------------------
        // Remove LineListGroups which don't have a CalendarPeriodType or are
        // locked
        // ---------------------------------------------------------------------

        Collections.sort( lineListGroups, new LineListGroupNameComparator() );

        // ---------------------------------------------------------------------
        // Validate selected LineListGroup
        // ---------------------------------------------------------------------

        if ( selectedLineListGroupId != null )
        {
            selectedLineListGroup = lineListService.getLineListGroup( selectedLineListGroupId );
        }
        else
        {
            selectedLineListGroup = selectedStateManager.getSelectedLineListGroup();

        }

        if ( selectedLineListGroup != null && lineListGroups.contains( selectedLineListGroup ) )
        {
            selectedLineListGroupId = selectedLineListGroup.getId();
            selectedStateManager.setSelectedLineListGroup( selectedLineListGroup );
        }
        else
        {

            selectedLineListGroupId = null;
            selectedLineListOptionId = null;
            selectedPeriodIndex = null;

            selectedStateManager.clearSelectedLineListGroup();
            selectedStateManager.clearSelectedLineListOption();
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // Load Options for First LineListElement in the Group
        lineListOptions = new ArrayList<LineListOption>( selectedLineListGroup.getLineListElements().iterator().next().getLineListElementOptions() );

        // Validate selected LineListOption
        if ( selectedLineListOptionId != null && !selectedLineListOptionId.equalsIgnoreCase( "null" ) )
        {
            selectedLineListOption = lineListService.getLineListOptionByName( selectedLineListOptionId );
        }
        else
        {
            selectedLineListOption = selectedStateManager.getSelectedLineListOption();
        }

        if ( selectedLineListOption != null && lineListOptions.contains( selectedLineListOption ) )
        {
            selectedLineListOptionId = selectedLineListOption.getName();
            selectedStateManager.setSelectedLineListOption( selectedLineListOption );
        }
        else
        {
            selectedLineListOptionId = null; 
            selectedPeriodIndex = null;

            selectedStateManager.clearSelectedLineListOption();
            selectedStateManager.clearSelectedPeriod();
            
            return SUCCESS;
        }

        // Prepare for multidimensional dataentry
        int numberOfTotalColumns = 1;

        if ( selectedLineListGroup.getLineListElements().size() > 0 )
        {
            for ( LineListElement de : selectedLineListGroup.getLineListElements() )
            {
                if ( numberOfTotalColumns > 1 )
                {
                    break;
                }
            }
        }

        // Generate Periods
        if ( selectedLineListGroup != null
            && selectedLineListGroup.getPeriodType().getName().equalsIgnoreCase( "OnChange" ) )
        {
            periods = new ArrayList<Period>();

            selectedPeriodIndex = null;
            selectedStateManager.clearSelectedPeriod();

            return DEFAULT_FORM;
        }
        else
        {
            periods = selectedStateManager.getPeriodList();
        }

        // Validate selected Period
        if ( selectedPeriodIndex == null )
        {
            selectedPeriodIndex = selectedStateManager.getSelectedPeriodIndex();
        }

        if ( selectedPeriodIndex != null && selectedPeriodIndex >= 0 && selectedPeriodIndex < periods.size() )
        {
            selectedStateManager.setSelectedPeriodIndex( selectedPeriodIndex );
        }
        else
        {
            selectedPeriodIndex = null;
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        return DEFAULT_FORM;

    }
}
