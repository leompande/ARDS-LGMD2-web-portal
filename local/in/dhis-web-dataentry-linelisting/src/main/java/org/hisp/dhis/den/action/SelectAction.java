package org.hisp.dhis.den.action;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.den.api.LLDataSets;
import org.hisp.dhis.den.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: SelectAction.java 4746 2008-03-13 20:02:29Z abyot $
 */

@SuppressWarnings("serial")
public class SelectAction
    extends ActionSupport
{
    private static final String DEFAULT_FORM = "defaultform";

    private static final String MULTI_DIMENSIONAL_FORM = "multidimensionalform";

    private static final String SECTION_FORM = "sectionform";

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

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public Collection<DataSet> getDataSets()
    {
        return dataSets;
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

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Boolean haveSection;

    public Boolean getHaveSection()
    {
        return haveSection;
    }

    private String useSectionForm;

    public String getUseSectionForm()
    {
        return useSectionForm;
    }

    public void setUseSectionForm( String useSectionForm )
    {
        this.useSectionForm = useSectionForm;
    }

    private Boolean customDataEntryFormExists;

    public Boolean getCustomDataEntryFormExists()
    {
        return this.customDataEntryFormExists;
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

    private Integer selectedDataSetId;

    public void setSelectedDataSetId( Integer selectedDataSetId )
    {
        this.selectedDataSetId = selectedDataSetId;
    }

    public Integer getSelectedDataSetId()
    {
        return selectedDataSetId;
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

    private String selStartDate;

    public String getSelStartDate()
    {
        return selStartDate;
    }

    private String selEndDate;

    public String getSelEndDate()
    {
        return selEndDate;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Validate selected OrganisationUnit
        // ---------------------------------------------------------------------

        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
        {
            selectedDataSetId = null;
            selectedPeriodIndex = null;

            selectedStateManager.clearSelectedDataSet();
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Load DataSets
        // ---------------------------------------------------------------------

        dataSets = new ArrayList<DataSet>( organisationUnit.getDataSets() );
        //dataSets = new ArrayList<DataSet>( dataSetService.getDataSetsBySource( organisationUnit ) );
        
        // ---------------------------------------------------------------------
        // Remove DataSets which don't have a CalendarPeriodType or are locked
        // ---------------------------------------------------------------------
        if ( currentUserService.getCurrentUser() != null && !currentUserService.currentUserIsSuper() )
        {
            UserCredentials userCredentials = userService.getUserCredentials( currentUserService.getCurrentUser() );

            Set<DataSet> dataSetUserAuthorityGroups = new HashSet<DataSet>();

            for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
            {
                dataSetUserAuthorityGroups.addAll( userAuthorityGroup.getDataSets() );
            }

            dataSets.retainAll( dataSetUserAuthorityGroups );
        }

        Iterator<DataSet> it = dataSets.iterator();

        while ( it.hasNext() )
        {
            DataSet temp = it.next();
            
            if( temp.getName().equalsIgnoreCase( LLDataSets.LL_IDSP_LAB ) || 
                temp.getName().equalsIgnoreCase( LLDataSets.LL_DEATHS_IDSP ) || 
                temp.getName().equalsIgnoreCase( LLDataSets.LL_UU_IDSP_EVENTSP ) || 
                temp.getName().equalsIgnoreCase( LLDataSets.LL_BIRTHS ) || 
                temp.getName().equalsIgnoreCase( LLDataSets.LL_DEATHS )  || 
                temp.getName().equalsIgnoreCase( LLDataSets.LL_MATERNAL_DEATHS ) || 
                temp.getName().equalsIgnoreCase( LLDataSets.LL_UU_IDSP_EVENTS ) || 
                temp.getName().equalsIgnoreCase( LLDataSets.LL_COLD_CHAIN ))
            {
                if ( !( temp.getPeriodType() instanceof CalendarPeriodType ) )
                {
                    it.remove();
                }
            }
            else
            {
                it.remove();
            }
        }

        Collections.sort( dataSets, new IdentifiableObjectNameComparator() );

        // ---------------------------------------------------------------------
        // Validate selected DataSet
        // ---------------------------------------------------------------------

        DataSet selectedDataSet;

        if ( selectedDataSetId != null )
        {
            selectedDataSet = dataSetService.getDataSet( selectedDataSetId );
        }
        else
        {
            selectedDataSet = selectedStateManager.getSelectedDataSet();
        }

        if ( selectedDataSet != null && dataSets.contains( selectedDataSet ) )
        {
            selectedDataSetId = selectedDataSet.getId();
            selectedStateManager.setSelectedDataSet( selectedDataSet );
        }
        else
        {
            selectedDataSetId = null;
            selectedPeriodIndex = null;

            selectedStateManager.clearSelectedDataSet();
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Prepare for multidimensional dataentry
        // ---------------------------------------------------------------------

        //List<Section> sections = (List<Section>) sectionService.getSectionByDataSet( selectedDataSet );

        List<Section> sections = new ArrayList<Section>();
        
        haveSection = sections.size() != 0;
        
        int numberOfTotalColumns = 1;
        
        if ( selectedDataSet.getDataElements().size() > 0 )
        {
            for( DataElement de :  selectedDataSet.getDataElements() )
            {           	
            	for ( DataElementCategory category : de.getCategoryCombo().getCategories() )
                {            		            		
                    numberOfTotalColumns = numberOfTotalColumns * category.getCategoryOptions().size();                   
                }            	
            	
            	if( numberOfTotalColumns > 1 )
            	{
            		break;
            	}            	
            }           
            
        }

        // ---------------------------------------------------------------------
        // Generate Periods
        // ---------------------------------------------------------------------

        periods = selectedStateManager.getPeriodList();

        // ---------------------------------------------------------------------
        // Validate selected Period
        // ---------------------------------------------------------------------

        if ( selectedPeriodIndex == null )
        {
            selectedPeriodIndex = selectedStateManager.getSelectedPeriodIndex();
            System.out.println("Period is null");
        }

        if ( selectedPeriodIndex != null && selectedPeriodIndex >= 0 && selectedPeriodIndex < periods.size() )
        {
            Period selPeriod = periods.get( selectedPeriodIndex );
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            selStartDate = simpleDateFormat.format( selPeriod.getStartDate() );
            selEndDate = simpleDateFormat.format( selPeriod.getEndDate() );

            selectedStateManager.setSelectedPeriodIndex( selectedPeriodIndex );
        }
        else
        {
            selectedPeriodIndex = null;
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Get the custom data entry form (if any)
        // ---------------------------------------------------------------------

        // Locate custom data entry form belonging to dataset, if any.
        DataEntryForm dataEntryForm = selectedDataSet.getDataEntryForm();
        //dataEntryFormService.getDataEntryFormByDataSet( selectedDataSet );
        
        customDataEntryFormExists = ( dataEntryForm != null);

        if ( useSectionForm != null )
        {
            return SECTION_FORM;
        }

        if ( numberOfTotalColumns > 1 )
        {        	
            return MULTI_DIMENSIONAL_FORM;
        }
        else
        {        	
            return DEFAULT_FORM;
        }            
    }
}
