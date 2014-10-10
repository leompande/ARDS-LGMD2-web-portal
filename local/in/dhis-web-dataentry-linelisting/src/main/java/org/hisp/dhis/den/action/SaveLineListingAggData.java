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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.den.api.LLDataSets;
import org.hisp.dhis.den.api.LLDataValueService;
import org.hisp.dhis.den.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class SaveLineListingAggData
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

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private LLDataValueService lldataValueService;

    public void setLldataValueService( LLDataValueService lldataValueService )
    {
        this.lldataValueService = lldataValueService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<Integer, String> llValueMap;

    public Map<Integer, String> getLlValueMap()
    {
        return llValueMap;
    }

    private Map<Integer, Integer> liDEMap;

    public Map<Integer, Integer> getLiDEMap()
    {
        return liDEMap;
    }

    private OrganisationUnit organisationUnit;

    private Period period;

    private DataSet dataSet;

    private String storedBy;

    private Map<String, String> lldeValueMap;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        // Initialization
        //lineListAggDes = new HashMap<String, String>();
        llValueMap = new HashMap<Integer, String>();
        liDEMap = new HashMap<Integer, Integer>();
        lldeValueMap = new HashMap<String, String>();

        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        period = selectedStateManager.getSelectedPeriod();

        dataSet = selectedStateManager.getSelectedDataSet();

        storedBy = currentUserService.getCurrentUsername();

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        if ( dataSet.getName().equalsIgnoreCase( LLDataSets.LL_BIRTHS ) )
        {
            lldeValueMap = lldataValueService.processLineListBirths( organisationUnit, period );
            List<String> aggDeList = new ArrayList<String>( lldeValueMap.keySet() );
            for ( String aggde : aggDeList )
            {
                String aggDeVal = lldeValueMap.get( aggde );
                saveData( organisationUnit, period, aggde, aggDeVal );
            }

            //System.out.println( "LineListing Birth AggDataValues Saved" );

        }
        else if ( dataSet.getName().equalsIgnoreCase( LLDataSets.LL_DEATHS ) )
        {
            lldeValueMap = lldataValueService.processLineListDeaths( organisationUnit, period );
            List<String> aggDeList = new ArrayList<String>( lldeValueMap.keySet() );
            for ( String aggde : aggDeList )
            {
                String aggDeVal = lldeValueMap.get( aggde );
                saveData( organisationUnit, period, aggde, aggDeVal );
            }

            //System.out.println( "LineListing Death AggDataValues Saved" );
        }
        else if ( dataSet.getName().equalsIgnoreCase( LLDataSets.LL_MATERNAL_DEATHS ) )
        {

            lldeValueMap = lldataValueService.processLineListMaternalDeaths( organisationUnit, period );
            List<String> aggDeList = new ArrayList<String>( lldeValueMap.keySet() );
            for ( String aggde : aggDeList )
            {
                String aggDeVal = lldeValueMap.get( aggde );
                saveData( organisationUnit, period, aggde, aggDeVal );
            }

            //System.out.println( "LineListing Maternal Death AggDataValues Saved" );
        }

        return SUCCESS;
    }

    private void saveData( OrganisationUnit organisationUnit, Period period, String deString, String value )
    {
        String partsOfdeString[] = deString.split( ":" );

        int dataElementId = Integer.parseInt( partsOfdeString[0] );
        int optionComboId = Integer.parseInt( partsOfdeString[1] );

        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
            .getDataElementCategoryOptionCombo( optionComboId );

        if ( dataElement == null || optionCombo == null )
        {

        }
        else
        {
            DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, optionCombo );
            if ( dataValue == null )
            {
                if ( value != null )
                {
                    dataValue = new DataValue( dataElement, period, organisationUnit, value, storedBy, new Date(),
                        null, optionCombo );

                    dataValueService.addDataValue( dataValue );
                    llValueMap.put( dataElement.getId(), value );
                    liDEMap.put( dataElement.getId(), optionCombo.getId() );
                }
            }
            else
            {
                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );
                dataValue.setStoredBy( storedBy );

                dataValueService.updateDataValue( dataValue );
                llValueMap.put( dataElement.getId(), value );
                liDEMap.put( dataElement.getId(), optionCombo.getId() );
            }
        }

    }


}
