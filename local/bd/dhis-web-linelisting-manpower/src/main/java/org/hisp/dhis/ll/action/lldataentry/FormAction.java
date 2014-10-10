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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;
import org.hisp.dhis.linelisting.LineListDataElementMap;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

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

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }

    //--------------------------------------------------------------------------
    // Parameters
    //--------------------------------------------------------------------------

    private Map<String, DataValue> dataValueMap;

    public Map<String, DataValue> getDataValueMap()
    {
        return dataValueMap;
    }

    private Map<String, String> dataelementMap;

    public Map<String, String> getDataelementMap()
    {
        return dataelementMap;
    }

    private String sactionedPostdataelement;

    public String getSactionedPostdataelement()
    {
        return sactionedPostdataelement;
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

    private String selectedLineListOptionId;

    public String getSelectedLineListOptionId()
    {
        return selectedLineListOptionId;
    }

    public void setSelectedLineListOptionId( String selectedLineListOptionId )
    {
        this.selectedLineListOptionId = selectedLineListOptionId;
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

    private List<LineListDataValue> llDataValuesList;

    public List<LineListDataValue> getLlDataValuesList()
    {
        return llDataValuesList;
    }

    public void setLlDataValuesList( List<LineListDataValue> llDataValuesList )
    {
        this.llDataValuesList = llDataValuesList;
    }

    private List<Employee> employeeList;

    public List<Employee> getEmployeeList()
    {
        return employeeList;
    }

    private Collection<LineListOption> lineListOptions;

    //--------------------------------------------------------------------------
    // Action Implementation
    //--------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        employeeList = new ArrayList<Employee>();

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        LineListGroup lineListGroup = selectedStateManager.getSelectedLineListGroup();

        LineListOption lineListOption = selectedStateManager.getSelectedLineListOption();

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

        // Removing POST linelist element from the list
        String postLineListElementName = lineListGroup.getLineListElements().iterator().next().getShortName();
        lineListElements.remove( 0 );

        // HardCoding to get lastworkingdate linelist element
        String lastWorkingDateLLElementName = "lastworkingdate";

        // preparing map to filter records from linelist table
        Map<String, String> llElementValueMap = new HashMap<String, String>();
        llElementValueMap.put( postLineListElementName, lineListOption.getName() );
        llElementValueMap.put( lastWorkingDateLLElementName, "null" );

        Period period;

        if ( lineListGroup != null && lineListGroup.getPeriodType().getName().equalsIgnoreCase( "OnChange" ) )
        {
            period = dataBaseManagerInterface.getRecentPeriodForOnChangeData( lineListGroup.getShortName(),
                postLineListElementName, lineListOption.getName(), organisationUnit );
        }
        else
        {
            period = selectedStateManager.getSelectedPeriod();

            period = periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
        }

        if ( period != null )
        {

            // To get datavalues from datavalue table for normal dataelements

            Period dataValuePeriod = periodService.getPeriod( 0 );
            dataValueMap = new HashMap<String, DataValue>();

            dataelementMap = new HashMap<String, String>();

            LineListElement lineListElement = lineListService.getLineListElementByShortName( postLineListElementName );

            List<LineListDataElementMap> lineListDataElementMaps = lineListService.getLinelistDataelementMappings(
                lineListElement, lineListOption );

            System.out.println( "Agg De size : " + lineListDataElementMaps.size() + " :: " + lineListElement.getId()
                + " :: " + lineListOption.getId() );
            Iterator<LineListDataElementMap> lineListDEMapIterator = lineListDataElementMaps.iterator();

            while ( lineListDEMapIterator.hasNext() )
            {
                LineListDataElementMap lineListDataElementMap = lineListDEMapIterator.next();

                DataValue dataValue = dataValueService.getDataValue( organisationUnit, lineListDataElementMap
                    .getDataElement(), dataValuePeriod, lineListDataElementMap.getDataElementOptionCombo() );

                System.out.println( organisationUnit.getId() + " : " + lineListDataElementMap.getDataElement().getId()
                    + " : " + dataValuePeriod.getId() + " : " + lineListDataElementMap.getDataElementOptionCombo().getId() );

                String mapName = "DE:" + lineListDataElementMap.getDataElement().getId() + ":"
                    + lineListDataElementMap.getDataElementOptionCombo().getId();

                if ( lineListDataElementMap.getDataElement().getName().contains( "Sanctioned Post" ) )
                {
                    sactionedPostdataelement = mapName;
                }
                dataValueMap.put( mapName, dataValue );

                dataelementMap.put( mapName, lineListDataElementMap.getDataElement().getName() + " : "
                    + lineListDataElementMap.getDataElementOptionCombo().getName() );
            }

            llDataValuesList = new ArrayList<LineListDataValue>();

            llDataValuesList = dataBaseManagerInterface.getLLValuesFilterByLLElements( lineListGroup.getShortName(),
                llElementValueMap, organisationUnit );

            // HardCoding Column Name

            String pdsCodeColName = "pdscode";
            if ( llDataValuesList == null || llDataValuesList.isEmpty() )
            {
                return SUCCESS;
            }
            else
            {
                for ( LineListDataValue llDataValue : llDataValuesList )
                {
                    Map<String, String> llValueMap = llDataValue.getLineListValues();
                    if ( llValueMap != null )
                    {
                        String pdsCode = llValueMap.get( pdsCodeColName );
                        if ( pdsCode != null )
                        {
                            Employee employee = employeeService.getEmployeeByPDSCode( pdsCode );
                            if ( employee != null )
                            {
                                employeeList.add( employee );
                            }
                        }
                    }
                    recordNumbers.add( Integer.valueOf( llDataValue.getRecordNumber() ) );
                }
            }

        }

        return SUCCESS;
    }
}
