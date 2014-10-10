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

import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.comparator.LineListElementNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

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

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // --------------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------------
    private Integer count1;

    public void setCount1( Integer count1 )
    {
        this.count1 = count1;
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

    List<LineListElement> lineListElements;

    public Collection<LineListElement> getLineListElements()
    {
        return lineListElements;
    }

    private Map<String, Collection<LineListOption>> llElementOptionsMap;

    public Map<String, Collection<LineListOption>> getLlElementOptionsMap()
    {
        return llElementOptionsMap;
    }

    private Map<LineListElement, String> llElementValuesMap;

    public void setLlElementValuesMap( Map<LineListElement, String> llElementValuesMap )
    {
        this.llElementValuesMap = llElementValuesMap;
    }

    private List<LineListDataValue> llDataValuesList;

    public List<LineListDataValue> getLlDataValuesList()
    {
        return llDataValuesList;
    }

    public void setLlDataValuesList( List<LineListDataValue> llDataValuesList )
    {
        this.llDataValuesList = llDataValuesList;
    }

    private Collection<LineListOption> lineListOptions;

    public String execute() throws Exception
    {

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        LineListGroup lineListGroup = selectedStateManager.getSelectedLineListGroup();

        llElementOptionsMap = new HashMap<String, Collection<LineListOption>>();

        List<Integer> recordNumbers = new ArrayList<Integer>();

        lineListElements = new ArrayList<LineListElement>( lineListGroup.getLineListElements() );
        if ( lineListElements.size() == 0 )
        {
            return SUCCESS;
        } 
        else
        {
            Iterator<LineListElement> it2 = lineListElements.iterator();
            while ( it2.hasNext() )
            {
                LineListElement element = it2.next();

                lineListOptions = element.getLineListElementOptions();
                llElementOptionsMap.put( element.getShortName(), lineListOptions );
            }
        }

        Period period;

        if( lineListGroup != null && lineListGroup.getPeriodType().getName().equalsIgnoreCase( "OnChange" ) )
        {              
            period = periodService.getPeriod( 0 );
        }
        else
        {
            period = selectedStateManager.getSelectedPeriod();

            period = periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
        }
        
        if ( period != null )
        {

            llDataValuesList = new ArrayList<LineListDataValue>();

            llDataValuesList = dataBaseManagerInterface.getFromLLTable( lineListGroup.getShortName(), organisationUnit, period );

            if ( llDataValuesList.isEmpty() || llDataValuesList == null )
            {
                return SUCCESS;
            } 
            else
            {
                for ( LineListDataValue llDataValue : llDataValuesList )
                {
                    recordNumbers.add( Integer.valueOf( llDataValue.getRecordNumber() ) );
                }
            }

        }

        return SUCCESS;
    }
}
