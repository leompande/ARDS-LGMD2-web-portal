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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.den.api.LLDataSets;
import org.hisp.dhis.den.api.LLDataValue;
import org.hisp.dhis.den.api.LLDataValueService;
import org.hisp.dhis.den.comments.StandardCommentsManager;
import org.hisp.dhis.den.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: FormAction.java 4733 2008-03-13 15:26:24Z larshelg $
 */

public class FormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LLDataValueService dataValueService;

    public void setDataValueService( LLDataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    @SuppressWarnings("unused")
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private StandardCommentsManager standardCommentsManager;

    public void setStandardCommentsManager( StandardCommentsManager standardCommentsManager )
    {
        this.standardCommentsManager = standardCommentsManager;
    }

    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }
    
    public SelectedStateManager getSelectedStateManager()
    {
        return selectedStateManager;
    }
    /*
    private DataSetLockService dataSetLockService;
    
    public void setDataSetLockService( DataSetLockService dataSetLockService)
    {
        this.dataSetLockService = dataSetLockService;
    }
    */
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    } 

    //SelectAction Source Code
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
    private SectionService sectionService;

    public SectionService getSectionService()
    {
        return sectionService;
    }

    public void setSectionService( SectionService sectionService )
    {
        this.sectionService = sectionService;
    }

    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private List<String> recordNos;
    
    public List<String> getRecordNos() 
    {
	return recordNos;
    }

    private List<OrganisationUnit> orgUnitChildList;

    public List<OrganisationUnit> getOrgUnitChildList() 
    {
		return orgUnitChildList;
	}

    private Map<String, List<LLDataValue>> lldataValueMap;
    
    public Map<String, List<LLDataValue>> getLldataValueMap()
    {
        return lldataValueMap;
    }

    private String isLineListing;
    
    public String getIsLineListing()
    {
        return isLineListing;
    }

    private List<DataElement> orderedDataElements = new ArrayList<DataElement>();

    public List<DataElement> getOrderedDataElements()
    {
        return orderedDataElements;
    }

    private Map<Integer, DataValue> dataValueMap;

    public Map<Integer, DataValue> getDataValueMap()
    {
        return dataValueMap;
    }
    
    private List<String> standardComments;

    public List<String> getStandardComments()
    {
        return standardComments;
    }

    private Map<String, String> dataElementTypeMap;

    public Map<String, String> getDataElementTypeMap()
    {
        return dataElementTypeMap;
    }

    private Map<Integer, MinMaxDataElement> minMaxMap;

    public Map<Integer, MinMaxDataElement> getMinMaxMap()
    {
        return minMaxMap;
    }

    private Integer integer = new Integer( 0 );

    public Integer getInteger()
    {
        return integer;
    }
    
    private Boolean cdeFormExists;

    /*
     * public Boolean getCustomDataEntryFormExists() { return
     * this.customDataEntryFormExists; }
     */
    
    private DataEntryForm dataEntryForm;

    public DataEntryForm getDataEntryForm()
    {
        return this.dataEntryForm;
    }

    private String customDataEntryFormCode;

    public String getCustomDataEntryFormCode()
    {
        return this.customDataEntryFormCode;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

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
    
    private String selDSName;
    
    public String getSelDSName()
    {
        return selDSName;
    }

    private String llbirth;
    
    public String getLlbirth()
    {
        return llbirth;
    }
    
    private String lldeath;
    
    public String getLldeath() 
    {
        return lldeath;
    }

    private String llmdeath;
        
    public String getLlmdeath() 
    {
        return llmdeath;
    }

    private String lluuidspe;
        
    public String getLluuidspe() 
    {
        return lluuidspe;
    }

    private String lluuidspep;
	
    public String getLluuidspep() 
    {
        return lluuidspep;
    }
	
    private String lldidsp;
	
    public String getLldidsp() 
    {
        return lldidsp;
    }

    private String llidspl;
	
    public String getLlidspl() 
    {
        return llidspl;
    }

    private String llcoldchain;
    
    public String getLlcoldchain()
    {
        return llcoldchain;
    }

    
    private int maxRecordNo;
    
    public int getMaxRecordNo()
    {
        return maxRecordNo;
    }
	
    private boolean locked = false;

    public boolean isLocked()
    {
        return locked;
    }
    
    //SelectAction Source Code
    
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
    
    private Boolean haveSection;

    public Boolean getHaveSection()
    {
        return haveSection;
    }
    
    
    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
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
    
    private Boolean customDataEntryFormExists;

    public Boolean getCustomDataEntryFormExists()
    {
        return this.customDataEntryFormExists;
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
/*
    private static final String DEFAULT_FORM = "defaultform";

    private static final String MULTI_DIMENSIONAL_FORM = "multidimensionalform";

    private static final String SECTION_FORM = "sectionform";
 */   
    
    private boolean lockStatus;
    
    public boolean isLockStatus()
    {
        return lockStatus;
    }


    
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute()
        throws Exception
    {
        
        //SelectAction Source Code
        
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
        
        //DataEntryForm dataEntryForm = selectedDataSet.getDataEntryForm();
        
        //dataEntryFormService.getDataEntryFormByDataSet( selectedDataSet );
        
        
        /*
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
        */          
   // }
        
        // FormAction Source Code
        
        //Intialization
        llbirth = LLDataSets.LL_BIRTHS;
        lldeath = LLDataSets.LL_DEATHS;
        llmdeath = LLDataSets.LL_MATERNAL_DEATHS;
        lluuidspe = LLDataSets.LL_UU_IDSP_EVENTS;
        lluuidspep = LLDataSets.LL_UU_IDSP_EVENTSP;
        lldidsp = LLDataSets.LL_DEATHS_IDSP;
        llidspl = LLDataSets.LL_IDSP_LAB;
        llcoldchain = LLDataSets.LL_COLD_CHAIN;
        
        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        DataSet dataSet = selectedStateManager.getSelectedDataSet();

        selDSName = dataSet.getName();

        if(selDSName.equalsIgnoreCase( llbirth ) || selDSName.equalsIgnoreCase( lldeath ) || selDSName.equalsIgnoreCase( llmdeath ) )
        {
            isLineListing = "yes";
        }
        else
        {
            isLineListing = "no";
        }

        /*
        if(selDSName.equalsIgnoreCase( llidspl ) || selDSName.equalsIgnoreCase( lldidsp ) || selDSName.equalsIgnoreCase( lluuidspep ) || selDSName.equalsIgnoreCase( llbirth ) || selDSName.equalsIgnoreCase( lldeath ) || selDSName.equalsIgnoreCase( llmdeath ) || selDSName.equalsIgnoreCase( lluuidspe ))
        {
            isLineListing = "yes";
        }
        else
        {
            isLineListing = "no";
        }
         */

        orgUnitChildList = new ArrayList<OrganisationUnit>(organisationUnit.getChildren());
        
        Period period = selectedStateManager.getSelectedPeriod();   

        Collection<DataElement> dataElements = dataSet.getDataElements();        

        if ( dataElements.size() == 0 )
        {
            return SUCCESS;
        }  
        
        Collection<DataElementCategoryOptionCombo> defaultOptionCombo = dataElements.iterator().next().getCategoryCombo().getOptionCombos();        
        
        // ---------------------------------------------------------------------
        // Get the min/max values
        // ---------------------------------------------------------------------

        Collection<MinMaxDataElement> minMaxDataElements = minMaxDataElementService.getMinMaxDataElements(
            organisationUnit, dataElements );
        
        minMaxMap = new HashMap<Integer, MinMaxDataElement>( minMaxDataElements.size() );

        for ( MinMaxDataElement minMaxDataElement : minMaxDataElements )
        {
            minMaxMap.put( minMaxDataElement.getDataElement().getId(), minMaxDataElement );
        }

        // ---------------------------------------------------------------------
        // Order the DataElements
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        // Get the DataValues and create a map
        // ---------------------------------------------------------------------

        Collection<LLDataValue> dataValues = dataValueService.getDataValues( organisationUnit, period, dataElements, defaultOptionCombo );
        
        dataValueMap = new HashMap<Integer, DataValue>( dataValues.size() );
        
        // ---------------------------------------------------------------------
        // Make the standard comments available
        // ---------------------------------------------------------------------

        standardComments = standardCommentsManager.getStandardComments();

        // ---------------------------------------------------------------------
        // Make the DataElement types available
        // ---------------------------------------------------------------------

        dataElementTypeMap = new HashMap<String, String>();
        dataElementTypeMap.put( DataElement.VALUE_TYPE_BOOL, i18n.getString( "yes_no" ) );
        dataElementTypeMap.put( DataElement.VALUE_TYPE_INT, i18n.getString( "number" ) );
        dataElementTypeMap.put( DataElement.VALUE_TYPE_STRING, i18n.getString( "text" ) );

        // ---------------------------------------------------------------------
        // Get the custom data entry form (if any)
        // ---------------------------------------------------------------------

        // Locate custom data entry form belonging to dataset, if any.
        
        
        dataEntryForm = dataSet.getDataEntryForm();
        //dataEntryFormService.getDataEntryFormByDataSet( dataSet );
        cdeFormExists = (dataEntryForm != null);

        // Add JS and meta data to dynamically persist values of form.
        if ( cdeFormExists )
            customDataEntryFormCode = prepareDataEntryFormCode( dataEntryForm.getHtmlCode(), dataElements, dataValues );

        
        if(selDSName.equalsIgnoreCase( llbirth ) )
        {
            prepareLLBirthFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( lldeath ))
        {
            prepareLLDeathFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( llmdeath ))
        {
            prepareLLMaternalDeathFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( lluuidspe ))
        {
            prepareLLUUIDSPEFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( lluuidspep ))
        {
            prepareLLUUIDSPEPFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( lldidsp ))
        {
            prepareLLDIDSPFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( llidspl ))
        {
            prepareLLIDSPLFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( llcoldchain ))
        {
            prepareLLColdChainFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else
        {
            
        }
        
        lockStatus = dataSetService.isLocked( dataSet, period, organisationUnit, null );
        
        
        
        /*
        if( dataSetLockService == null )
        {
            System.out.println(" DataSetLockService is null");
        }
        if( dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period ) != null)
        {                               
            if( dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period ).getSources().contains(organisationUnit) ) 
            {
                locked = true;
            }
        }
        */
        if( !lockStatus )
        {
            System.out.println(" DataSet Not Lock");
        }
        
        if( lockStatus )
        {
            System.out.println(" DataSet Lockd");
            //if( dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period ).getSources().contains(organisationUnit) ) 
            //{
                locked = true;
            //}
        }
        
        
        
        maxRecordNo = dataValueService.getMaxRecordNo();
        
        return SUCCESS;
    }
    
    
    private void prepareLLColdChainFormCode( OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
        Collection<LLDataValue> dataValues)
    {
        lldataValueMap = new HashMap<String, List<LLDataValue>>();
        
        for ( LLDataValue dataValue : dataValues )
        {
            Integer recordNo = dataValue.getRecordNo();
            List<LLDataValue> tempLLDVList;
            if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
            {
                tempLLDVList = new ArrayList<LLDataValue>();
            }
            else
            {
                tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
            }
            
            tempLLDVList.add( dataValue );
            lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
        }

        Set<String> llDataValueMapKeys = lldataValueMap.keySet();
        Iterator<String> it1 = llDataValueMapKeys.iterator();
        while( it1.hasNext() )
        {
            String tempRecordNo = (String) it1.next();
            List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
            List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
            LLDataValue existingLLDV = new LLDataValue();
            
            for(int i = 0; i<=6; i++)
            {
                LLDataValue tempLLDV1 = new LLDataValue();
                tempLLDVList2.add(tempLLDV1);
            }

            Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
            while( it2.hasNext() )
            {
                LLDataValue tempLLDV = (LLDataValue) it2.next();
                
                if( tempLLDV.getDataElement().getId() == LLDataSets.LLCC_EQUIPMENT )
                {
                    tempLLDVList2.set( 0, tempLLDV );
                    existingLLDV = tempLLDV;
                }    
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLCC_MACHINE )
                {
                    tempLLDVList2.set( 1, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLCC_MACHINE_WORKING )
                {
                    tempLLDVList2.set( 2, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if( tempLLDV.getDataElement().getId() == LLDataSets.LLCC_BREAKDOWN_DATE )
                {
                    tempLLDVList2.set( 3, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if( tempLLDV.getDataElement().getId() == LLDataSets.LLCC_INTIMATION_DATE )
                {
                    tempLLDVList2.set( 4, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if( tempLLDV.getDataElement().getId() == LLDataSets.LLCC_REPAIR_DATE )
                {
                    tempLLDVList2.set( 5, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if( tempLLDV.getDataElement().getId() == LLDataSets.LLCC_REMARKS )
                {
                    tempLLDVList2.set( 6, tempLLDV );
                    existingLLDV = tempLLDV;
                }
            }

            int llbDes[] = { LLDataSets.LLCC_EQUIPMENT, LLDataSets.LLCC_MACHINE, LLDataSets.LLCC_MACHINE_WORKING,
                                LLDataSets.LLCC_BREAKDOWN_DATE, LLDataSets.LLCC_INTIMATION_DATE,
                                LLDataSets.LLCC_REPAIR_DATE, LLDataSets.LLCC_REMARKS };
            
            for(int i = 0; i<=6; i++)
            {
                LLDataValue llDv = tempLLDVList2.get(i);
                if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                {
                    llDv.setPeriod(existingLLDV.getPeriod());
                    llDv.setSource(existingLLDV.getSource());
                    llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                    llDv.setRecordNo(existingLLDV.getRecordNo());
                    llDv.setOptionCombo(existingLLDV.getOptionCombo());
                    llDv.setValue(" ");
                    
                    tempLLDVList2.set(i, llDv);
                 }                               
            }

            lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
        }
     
        recordNos = new ArrayList<String>(lldataValueMap.keySet());
        Collections.sort( recordNos );
    }

    
    
    private void prepareLLIDSPLFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                //System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                    tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                    tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = (String) it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for( int i = 0; i < 7; i++ )
                {
                    LLDataValue tempLLDV1 = new LLDataValue();
                    tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = (LLDataValue) it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_PATIENT_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_AGE)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_SEX)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_ADDRESS)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_TEST)
                    {
                        tempLLDVList2.set( 4, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_LAB_DIAGNOSIS)
                    {
                        tempLLDVList2.set( 5, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_OUTCOME )
                    {
                        tempLLDVList2.set( 6, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    
                }

                int llbDes[] = {
                                        LLDataSets.LLIDSPL_PATIENT_NAME, LLDataSets.LLIDSPL_AGE, LLDataSets.LLIDSPL_SEX,
                                        LLDataSets.LLIDSPL_ADDRESS, LLDataSets.LLIDSPL_TEST, LLDataSets.LLIDSPL_LAB_DIAGNOSIS,
                                        LLDataSets.LLIDSPL_OUTCOME
                                };
                
                for( int i = 0; i < 7; i++ )
                {
                        LLDataValue llDv = tempLLDVList2.get(i);
                        if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                        {
                                llDv.setPeriod(existingLLDV.getPeriod());
                                llDv.setSource(existingLLDV.getSource());
                                llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                                llDv.setRecordNo(existingLLDV.getRecordNo());
                                llDv.setOptionCombo(existingLLDV.getOptionCombo());
                                llDv.setValue(" ");
                                
                                tempLLDVList2.set( i, llDv );                                                           
                        }                               
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
         
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }

    
    private void prepareLLMaternalDeathFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
        Collection<LLDataValue> dataValues)
    {
        lldataValueMap = new HashMap<String, List<LLDataValue>>();
        
        for ( LLDataValue dataValue : dataValues )
        {
            Integer recordNo = dataValue.getRecordNo();
            //System.out.println("RecordNo : "+recordNo);
            List<LLDataValue> tempLLDVList;
            if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
            {
                tempLLDVList = new ArrayList<LLDataValue>();
            }
            else
            {
                tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
            }
            
            tempLLDVList.add( dataValue );
            lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
        }

        Set<String> llDataValueMapKeys = lldataValueMap.keySet();
        Iterator<String> it1 = llDataValueMapKeys.iterator();
        while(it1.hasNext())
        {
            String tempRecordNo = (String) it1.next();
            List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
            List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
            LLDataValue existingLLDV = new LLDataValue();
            
            for(int i = 0; i<=7; i++)
            {
                LLDataValue tempLLDV1 = new LLDataValue();
                tempLLDVList2.add(tempLLDV1);
            }

            Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
            while(it2.hasNext())
            {
                LLDataValue tempLLDV = (LLDataValue) it2.next();
                
                if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_MOTHER_NAME)
                {
                    tempLLDVList2.set( 0, tempLLDV );
                    existingLLDV = tempLLDV;
                }    
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_VILLAGE_NAME)
                {
                    tempLLDVList2.set( 1, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_AGE_AT_DEATH)
                {
                    tempLLDVList2.set( 2, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_DURATION_OF_PREGNANCY)
                {
                    tempLLDVList2.set( 3, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_DELIVERY_AT)
                {
                    tempLLDVList2.set( 4, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_NATURE_OF_ASSISTANCE)
                {
                    tempLLDVList2.set( 5, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_DEATH_CAUSE)
                {
                    tempLLDVList2.set( 6, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_AUDITED)
                {
                    tempLLDVList2.set( 7, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                
            }

            int llbDes[] = {
                                LLDataSets.LLMD_MOTHER_NAME, LLDataSets.LLMD_VILLAGE_NAME, LLDataSets.LLMD_AGE_AT_DEATH,
                                LLDataSets.LLMD_DURATION_OF_PREGNANCY, LLDataSets.LLMD_DELIVERY_AT, LLDataSets.LLMD_NATURE_OF_ASSISTANCE,
                                LLDataSets.LLMD_DEATH_CAUSE, LLDataSets.LLMD_AUDITED 
                           };
            
            for(int i = 0; i<=7; i++)
            {
                LLDataValue llDv = tempLLDVList2.get(i);
                if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                {
                        llDv.setPeriod(existingLLDV.getPeriod());
                        llDv.setSource(existingLLDV.getSource());
                        llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                        llDv.setRecordNo(existingLLDV.getRecordNo());
                        llDv.setOptionCombo(existingLLDV.getOptionCombo());
                        llDv.setValue(" ");
                        
                        tempLLDVList2.set(i, llDv);
                }                               
            }

            lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
        }
        
        recordNos = new ArrayList<String>(lldataValueMap.keySet());
        Collections.sort( recordNos );

    }

    
    private void prepareLLBirthFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                //System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                        tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                        tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            //List<String> llDataValueMapKeys = new ArrayList<String>(lldataValueMap.keySet());
            
            //Collections.sort(llDataValueMapKeys);
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = (String) it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<=5; i++)
                {
                        LLDataValue tempLLDV1 = new LLDataValue();
                        tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = (LLDataValue) it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_CHILD_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_VILLAGE_NAME)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_SEX)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_DOB)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_WIEGH)
                    {
                        tempLLDVList2.set( 4, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_BREASTFED)
                    {
                        tempLLDVList2.set( 5, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLB_CHILD_NAME,LLDataSets.LLB_VILLAGE_NAME,LLDataSets.LLB_SEX,
                                                LLDataSets.LLB_DOB,LLDataSets.LLB_WIEGH,LLDataSets.LLB_BREASTFED};
                for(int i = 0; i<=5; i++)
                {
                        LLDataValue llDv = tempLLDVList2.get(i);
                        if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                        {
                                llDv.setPeriod(existingLLDV.getPeriod());
                                llDv.setSource(existingLLDV.getSource());
                                llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                                llDv.setRecordNo(existingLLDV.getRecordNo());
                                llDv.setOptionCombo(existingLLDV.getOptionCombo());
                                llDv.setValue(" ");
                                
                                tempLLDVList2.set(i, llDv);
                        }                               
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
         
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );
        }

    
    
    private void prepareLLDeathFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                //System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                        tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                        tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = (String) it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<5; i++)
                {
                        LLDataValue tempLLDV1 = new LLDataValue();
                        tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = (LLDataValue) it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_CHILD_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_VILLAGE_NAME)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_SEX)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_AGE_CATEGORY)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_DEATH_CAUSE)
                    {
                        tempLLDVList2.set( 4, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLD_CHILD_NAME,LLDataSets.LLD_VILLAGE_NAME,LLDataSets.LLD_SEX,
                                                LLDataSets.LLD_AGE_CATEGORY,LLDataSets.LLD_DEATH_CAUSE};
                
                for(int i = 0; i<5; i++)
                {
                        LLDataValue llDv = tempLLDVList2.get(i);
                        if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                        {
                                llDv.setPeriod(existingLLDV.getPeriod());
                                llDv.setSource(existingLLDV.getSource());
                                llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                                llDv.setRecordNo(existingLLDV.getRecordNo());
                                llDv.setOptionCombo(existingLLDV.getOptionCombo());
                                llDv.setValue(" ");
                                
                                tempLLDVList2.set(i, llDv);
                        }                               
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
            
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }

    private void prepareLLUUIDSPEFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                //System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                        tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                        tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = (String) it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<4; i++)
                {
                        LLDataValue tempLLDV1 = new LLDataValue();
                        tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = (LLDataValue) it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPE_SC_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPE_DATE_OF_EVENT)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPE_DEATAILS)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPE_WAS_INVESTIGATED)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLUUIDSPE_SC_NAME,LLDataSets.LLUUIDSPE_DATE_OF_EVENT,
                                                LLDataSets.LLUUIDSPE_DEATAILS,LLDataSets.LLUUIDSPE_WAS_INVESTIGATED};
                
                for(int i = 0; i<4; i++)
                {
                        LLDataValue llDv = tempLLDVList2.get(i);
                        if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                        {
                                llDv.setPeriod(existingLLDV.getPeriod());
                                llDv.setSource(existingLLDV.getSource());
                                llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                                llDv.setRecordNo(existingLLDV.getRecordNo());
                                llDv.setOptionCombo(existingLLDV.getOptionCombo());
                                llDv.setValue(" ");
                                
                                tempLLDVList2.set(i, llDv);
                        }                               
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
            
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }

    private void prepareLLUUIDSPEPFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                //System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                        tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                        tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = (String) it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<4; i++)
                {
                        LLDataValue tempLLDV1 = new LLDataValue();
                        tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = (LLDataValue) it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPEP_EVENT_REPORTED)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPEP_DATE_OF_EVENT)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPEP_WAS_INVESTIGATED)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPEP_ACTION_TAKEN)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLUUIDSPEP_EVENT_REPORTED,LLDataSets.LLUUIDSPEP_DATE_OF_EVENT,
                                                LLDataSets.LLUUIDSPEP_WAS_INVESTIGATED,LLDataSets.LLUUIDSPEP_ACTION_TAKEN};
                
                for(int i = 0; i<4; i++)
                {
                        LLDataValue llDv = tempLLDVList2.get(i);
                        if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                        {
                                llDv.setPeriod(existingLLDV.getPeriod());
                                llDv.setSource(existingLLDV.getSource());
                                llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                                llDv.setRecordNo(existingLLDV.getRecordNo());
                                llDv.setOptionCombo(existingLLDV.getOptionCombo());
                                llDv.setValue(" ");
                                
                                tempLLDVList2.set(i, llDv);
                        }                               
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
            
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }
    
    
    private void prepareLLDIDSPFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                //System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                        tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                        tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = (String) it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<5; i++)
                {
                        LLDataValue tempLLDV1 = new LLDataValue();
                        tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = (LLDataValue) it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_CHILD_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_VILLAGE_NAME)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_SEX)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_AGE_CATEGORY)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_DEATH_CAUSE)
                    {
                        tempLLDVList2.set( 4, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLDIDSP_CHILD_NAME,LLDataSets.LLDIDSP_VILLAGE_NAME,LLDataSets.LLDIDSP_SEX,
                                                LLDataSets.LLDIDSP_AGE_CATEGORY,LLDataSets.LLDIDSP_DEATH_CAUSE};
                
                for(int i = 0; i<5; i++)
                {
                        LLDataValue llDv = tempLLDVList2.get(i);
                        if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                        {
                                llDv.setPeriod(existingLLDV.getPeriod());
                                llDv.setSource(existingLLDV.getSource());
                                llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                                llDv.setRecordNo(existingLLDV.getRecordNo());
                                llDv.setOptionCombo(existingLLDV.getOptionCombo());
                                llDv.setValue(" ");
                                
                                tempLLDVList2.set(i, llDv);
                        }                               
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
            
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }

    
    
    

    /**
     * Prepares the daa entry form code by preparing the data element
     * placeholders with code for displaying, manipulating and persisting data
     * elements.
     * 
     * The function in turn calls separate functions for preparing boolean and
     * non-boolean data elements.
     * 
     */
    private String prepareDataEntryFormCode( String dataEntryFormCode, Collection<DataElement> dataElements,
        Collection<LLDataValue> dataValues )
    {

        String preparedCode = dataEntryFormCode;
//        preparedCode = prepareDataEntryFormInputs( preparedCode, dataElements, dataValues );
//        preparedCode = prepareDataEntryFormCombos( preparedCode, dataElements, dataValues );
        //preparedCode = prepareDataEntryFormInputsAndCombos( preparedCode, dataElements, dataValues );
        
        return preparedCode;

    }

        
        

        
        



        


    /*
    private String prepareDataEntryFormInputs( String dataEntryFormCode, Collection<DataElement> dataElements,
        Collection<DataValue> dataValues )
    {

        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting.
        // ---------------------------------------------------------------------
        final String jsCodeForInputs = " onchange=\"saveValue( $DATAELEMENTID, '$DATAELEMENTNAME' )\" onkeypress=\"return keyPress(event, this)\" style=\"text-align:center\" ";
        final String jsCodeForCombos = " onchange=\"saveBoolean( $DATAELEMENTID )\">";
        final String historyCode = " ondblclick='javascript:viewHistory( $DATAELEMENTID  )' ";
        final String calDataElementCode = " class=\"calculated\" disabled ";

        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting.
        // ---------------------------------------------------------------------
        final String metaDataCode = "<span id=\"value[$DATAELEMENTID].name\" style=\"display:none\">$DATAELEMENTNAME</span>"
            + "<span id=\"value[$DATAELEMENTID].type\" style=\"display:none\">$DATAELEMENTTYPE</span>"
            + "<div id=\"value[$DATAELEMENTID].min\" style=\"display:none\">$MIN</div>"
            + "<div id=\"value[$DATAELEMENTID].max\" style=\"display:none\">$MAX</div>";

        // Buffer to contain the final result.
        StringBuffer sb = new StringBuffer();

        // Pattern to match data elements in the HTML code.
        Pattern patDataElement = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
        Matcher matDataElement = patDataElement.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------
        boolean result = matDataElement.find();
        while ( result )
        {
            // Get input HTML code (HTML input field code).
            String dataElementCode = matDataElement.group( 1 );

            // Pattern to extract data element ID from data element field
            Pattern patDataElementId = Pattern.compile( "value\\[(.*)\\].value:value\\[(.*)\\].value" );
            Matcher matDataElementId = patDataElementId.matcher( dataElementCode );
            if ( matDataElementId.find() && matDataElementId.groupCount() > 0 )
            {
                // ---------------------------------------------------------------------
                // Get data element ID of data element.
                // ---------------------------------------------------------------------
                int dataElementId = Integer.parseInt( matDataElementId.group( 1 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                
                int optionComboId = Integer.parseInt( matDataElementId.group( 2 ) );
                // ---------------------------------------------------------------------
                // Find type of data element
                // ---------------------------------------------------------------------
                String dataElementType = dataElement.getType();

                // ---------------------------------------------------------------------
                // Find existing value of data element in data set.
                // ---------------------------------------------------------------------
                String dataElementValue = "";
                if ( (dataElement instanceof CalculatedDataElement) )
                {
                    CalculatedDataElement cde = (CalculatedDataElement) dataElement;
                    if ( cde.isSaved() )
                    {
                        for ( DataValue dv : dataValues )
                        {
                            if ( dv.getDataElement().getId() == dataElementId )
                            {
                                dataElementValue = dv.getValue();
                                break;
                            }
                        }
                    }
                    else
                    {
                        dataElementValue = String.valueOf( calculatedValueMap.get( cde ) );
                    }
                }
                else
                {
                    for ( DataValue dv : dataValues )
                    {
                        if ( dv.getDataElement().getId() == dataElementId )
                        {
                            dataElementValue = dv.getValue();
                            break;
                        }
                    }
                }

                // ---------------------------------------------------------------------
                // Insert value of data element in output code.
                // ---------------------------------------------------------------------
                if(dataElement.getType().equals("bool"))
                {
                    dataElementCode = dataElementCode.replace("input", "select");
                    dataElementCode = dataElementCode.replaceAll( "value=\".*?\"", "" );
                    dataElementCode = dataElementCode.replace( "value\\["+dataElementId+"\\].value:value\\["+optionComboId+"\\].value", "value\\["+dataElementId+"]" );
                    //dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );
                }
                else
                {
                    if ( dataElementCode.contains( "value=\"\"" ) )
                            dataElementCode = dataElementCode.replace( "value=\"\"", "value=\"" + dataElementValue + "\"" );
                    else
                            dataElementCode += "value=\"" + dataElementValue + "\"";
                    dataElementCode = dataElementCode.replace( "value["+dataElementId+"].value:value["+optionComboId+"].value", "value["+dataElementId+"].value" );
                    //System.out.println("value["+dataElementId+"].value:value["+optionComboId+"].value  --------  value["+dataElementId+"].value");
                }   

                // ---------------------------------------------------------------------
                // MIN-MAX Values
                // ---------------------------------------------------------------------
                MinMaxDataElement minMaxDataElement = minMaxMap.get( new Integer( dataElement.getId() ) );
                String minValue = "No Min";
                String maxValue = "No Max";                    
                if ( minMaxDataElement != null )
                {
                    minValue = String.valueOf( minMaxDataElement.getMin() );
                    maxValue = String.valueOf( minMaxDataElement.getMax() );
                }
                
                // ---------------------------------------------------------------------
                // Remove placeholder view attribute from input field.
                // ---------------------------------------------------------------------
                dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );

                // ---------------------------------------------------------------------
                // Insert Title Information - DataElement id,name,type,min,max
                // ---------------------------------------------------------------------                    
                if ( dataElementCode.contains( "title=\"\"" ) )
                            dataElementCode = dataElementCode.replace( "title=\"\"", "title=\"-- ID:"+dataElement.getId()+" Name:"+dataElement.getShortName()+" Type:"+dataElement.getType()+" Min:"+minValue+" Max:"+maxValue+" --\"" );
                    else
                            dataElementCode += "title=\"-- ID:"+dataElement.getId()+" Name:"+dataElement.getShortName()+" Type:"+dataElement.getType()+" Min:"+minValue+" Max:"+maxValue+" --\"";

                // ---------------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields.
                // ---------------------------------------------------------------------
                String appendCode = dataElementCode;
                
                if(dataElement.getType().equalsIgnoreCase( "bool" ))
                {
                    appendCode += jsCodeForCombos;
                    //System.out.println("DataElementValue for Boolean Data : "+dataElementValue);
                    appendCode += "<option value=\"\">" + i18n.getString( "no_value" ) + "</option>";
                    if ( dataElementValue.equalsIgnoreCase("true") )
                        appendCode += "<option value=\"true\" selected>" + i18n.getString( "yes" )
                            + "</option>";
                    else
                        appendCode += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";

                    if ( dataElementValue.equalsIgnoreCase("false") )
                        appendCode += "<option value=\"false\" selected>" + i18n.getString( "no" )
                            + "</option>";
                    else
                        appendCode += "<option value=\"false\">" + i18n.getString( "no" ) + "</option>";

                    appendCode += "</select>";
                }
                else
                {
                    appendCode += jsCodeForInputs;                   
                    if ( dataElement.getType().equalsIgnoreCase( "int" ) )
                            appendCode += historyCode;
                
                    if ( (dataElement instanceof CalculatedDataElement) )
                            appendCode += calDataElementCode;
                
                    appendCode += " />";
                }
                
                appendCode += metaDataCode;
                appendCode = appendCode.replace( "$DATAELEMENTID", String.valueOf( dataElementId ) );
                appendCode = appendCode.replace( "$DATAELEMENTNAME", dataElement.getName() );
                appendCode = appendCode.replace( "$DATAELEMENTTYPE", dataElementType );
                if ( minMaxDataElement == null )
                {
                    appendCode = appendCode.replace( "$MIN", minValue );
                    appendCode = appendCode.replace( "$MAX", maxValue );
                }
                else
                {
                    appendCode = appendCode.replace( "$MIN", String.valueOf( minMaxDataElement.getMin() ) );
                    appendCode = appendCode.replace( "$MAX", String.valueOf( minMaxDataElement.getMax() ) );
                }
                matDataElement.appendReplacement( sb, appendCode );
            }

            // Go to next data entry field
            result = matDataElement.find();
        }

        // Add remaining code (after the last match), and return formatted code.
        matDataElement.appendTail( sb );
        return sb.toString();
    }


    private String prepareDataEntryFormCombos( String dataEntryFormCode, Collection<DataElement> dataElements,
        Collection<DataValue> dataValues )
    {

        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting.
        // ---------------------------------------------------------------------
        final String jsCodeForInputs = " onchange=\"saveValue( $DATAELEMENTID, '$DATAELEMENTNAME' )\" onkeypress=\"return keyPress(event, this)\" style=\"text-align:center\" ";
        final String jsCodeForCombos = " onchange=\"saveBoolean( $DATAELEMENTID )\">";
        final String historyCode = " ondblclick='javascript:viewHistory( $DATAELEMENTID  )' ";
        final String calDataElementCode = " class=\"calculated\" disabled ";

        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting.
        // ---------------------------------------------------------------------
        final String metaDataCode = "<span id=\"value[$DATAELEMENTID].name\" style=\"display:none\">$DATAELEMENTNAME</span>"
            + "<span id=\"value[$DATAELEMENTID].type\" style=\"display:none\">$DATAELEMENTTYPE</span>"
            + "<div id=\"value[$DATAELEMENTID].min\" style=\"display:none\">$MIN</div>"
            + "<div id=\"value[$DATAELEMENTID].max\" style=\"display:none\">$MAX</div>";

        // Buffer to contain the final result.
        StringBuffer sb = new StringBuffer();

        // Pattern to match data elements in the HTML code.
        Pattern patDataElement = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
        Matcher matDataElement = patDataElement.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------
        boolean result = matDataElement.find();
        while ( result )
        {
            // Get input HTML code (HTML input field code).
            String dataElementCode = matDataElement.group( 1 );

            // Pattern to extract data element ID from data element field
            Pattern patDataElementId = Pattern.compile( "value\\[(.*)\\].value:value\\[(.*)\\].boolean" );
            Matcher matDataElementId = patDataElementId.matcher( dataElementCode );
            if ( matDataElementId.find() && matDataElementId.groupCount() > 0 )
            {
                // ---------------------------------------------------------------------
                // Get data element ID of data element.
                // ---------------------------------------------------------------------
                int dataElementId = Integer.parseInt( matDataElementId.group( 1 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                
                int optionComboId = Integer.parseInt( matDataElementId.group( 2 ) );
                // ---------------------------------------------------------------------
                // Find type of data element
                // ---------------------------------------------------------------------
                String dataElementType = dataElement.getType();

                // ---------------------------------------------------------------------
                // Find existing value of data element in data set.
                // ---------------------------------------------------------------------
                String dataElementValue = "";
                if ( (dataElement instanceof CalculatedDataElement) )
                {
                    CalculatedDataElement cde = (CalculatedDataElement) dataElement;
                    if ( cde.isSaved() )
                    {
                        for ( DataValue dv : dataValues )
                        {
                            if ( dv.getDataElement().getId() == dataElementId )
                            {
                                dataElementValue = dv.getValue();
                                break;
                            }
                        }
                    }
                    else
                    {
                        dataElementValue = String.valueOf( calculatedValueMap.get( cde ) );
                    }
                }
                else
                {
                    for ( DataValue dv : dataValues )
                    {
                        if ( dv.getDataElement().getId() == dataElementId )
                        {
                            dataElementValue = dv.getValue();
                            break;
                        }
                    }
                }

                // ---------------------------------------------------------------------
                // Insert value of data element in output code.
                // ---------------------------------------------------------------------
                if(dataElement.getType().equals("bool"))
                {
                    dataElementCode = dataElementCode.replace("input", "select");
                    dataElementCode = dataElementCode.replaceAll( "value=\".*?\"", "" );
                    dataElementCode = dataElementCode.replace( "value\\["+dataElementId+"\\].value:value\\["+optionComboId+"\\].value", "value\\["+dataElementId+"]" );
                    //dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );
                }
                else
                {
                    if ( dataElementCode.contains( "value=\"\"" ) )
                            dataElementCode = dataElementCode.replace( "value=\"\"", "value=\"" + dataElementValue + "\"" );
                    else
                            dataElementCode += "value=\"" + dataElementValue + "\"";
                    dataElementCode = dataElementCode.replace( "value["+dataElementId+"].value:value["+optionComboId+"].value", "value["+dataElementId+"].value" );
                    //System.out.println("value["+dataElementId+"].value:value["+optionComboId+"].value  --------  value["+dataElementId+"].value");
                }   

                // ---------------------------------------------------------------------
                // MIN-MAX Values
                // ---------------------------------------------------------------------
                MinMaxDataElement minMaxDataElement = minMaxMap.get( new Integer( dataElement.getId() ) );
                String minValue = "No Min";
                String maxValue = "No Max";                    
                if ( minMaxDataElement != null )
                {
                    minValue = String.valueOf( minMaxDataElement.getMin() );
                    maxValue = String.valueOf( minMaxDataElement.getMax() );
                }
                
                // ---------------------------------------------------------------------
                // Remove placeholder view attribute from input field.
                // ---------------------------------------------------------------------
                dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );

                // ---------------------------------------------------------------------
                // Insert Title Information - DataElement id,name,type,min,max
                // ---------------------------------------------------------------------                    
                if ( dataElementCode.contains( "title=\"\"" ) )
                            dataElementCode = dataElementCode.replace( "title=\"\"", "title=\"-- ID:"+dataElement.getId()+" Name:"+dataElement.getShortName()+" Type:"+dataElement.getType()+" Min:"+minValue+" Max:"+maxValue+" --\"" );
                    else
                            dataElementCode += "title=\"-- ID:"+dataElement.getId()+" Name:"+dataElement.getShortName()+" Type:"+dataElement.getType()+" Min:"+minValue+" Max:"+maxValue+" --\"";

                // ---------------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields.
                // ---------------------------------------------------------------------
                String appendCode = dataElementCode;
                
                if(dataElement.getType().equalsIgnoreCase( "bool" ))
                {
                    appendCode += jsCodeForCombos;
                    //System.out.println("DataElementValue for Boolean Data : "+dataElementValue);
                    appendCode += "<option value=\"\">" + i18n.getString( "no_value" ) + "</option>";
                    if ( dataElementValue.equalsIgnoreCase("true") )
                        appendCode += "<option value=\"true\" selected>" + i18n.getString( "yes" )
                            + "</option>";
                    else
                        appendCode += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";

                    if ( dataElementValue.equalsIgnoreCase("false") )
                        appendCode += "<option value=\"false\" selected>" + i18n.getString( "no" )
                            + "</option>";
                    else
                        appendCode += "<option value=\"false\">" + i18n.getString( "no" ) + "</option>";

                    appendCode += "</select>";
                }
                else
                {
                    appendCode += jsCodeForInputs;                   
                    if ( dataElement.getType().equalsIgnoreCase( "int" ) )
                            appendCode += historyCode;
                
                    if ( (dataElement instanceof CalculatedDataElement) )
                            appendCode += calDataElementCode;
                
                    appendCode += " />";
                }
                
                appendCode += metaDataCode;
                appendCode = appendCode.replace( "$DATAELEMENTID", String.valueOf( dataElementId ) );
                appendCode = appendCode.replace( "$DATAELEMENTNAME", dataElement.getName() );
                appendCode = appendCode.replace( "$DATAELEMENTTYPE", dataElementType );
                if ( minMaxDataElement == null )
                {
                    appendCode = appendCode.replace( "$MIN", minValue );
                    appendCode = appendCode.replace( "$MAX", maxValue );
                }
                else
                {
                    appendCode = appendCode.replace( "$MIN", String.valueOf( minMaxDataElement.getMin() ) );
                    appendCode = appendCode.replace( "$MAX", String.valueOf( minMaxDataElement.getMax() ) );
                }
                matDataElement.appendReplacement( sb, appendCode );
            }

            // Go to next data entry field
            result = matDataElement.find();
        }

        // Add remaining code (after the last match), and return formatted code.
        matDataElement.appendTail( sb );
        return sb.toString();
    }
*/
}
