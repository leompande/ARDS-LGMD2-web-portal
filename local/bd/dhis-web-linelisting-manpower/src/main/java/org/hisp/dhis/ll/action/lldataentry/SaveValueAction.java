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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.linelisting.LineListDataElementMap;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class SaveValueAction
    implements Action
{

    private static final Log LOG = LogFactory.getLog( SaveValueAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

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

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
/*
    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }
*/
    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    private Date timestamp;

    public Date getTimestamp()
    {
        return timestamp;
    }

    private String storedBy;

    public String getStoredBy()
    {
        return storedBy;
    }

    private HttpSession session;

    public HttpSession getSession()
    {
        return session;
    }

    private LineListDataValue lineListDataValue;

    public LineListDataValue getLineListDataValue()
    {
        return lineListDataValue;
    }

    public void setLineListDataValue( LineListDataValue lineListDataValue )
    {
        this.lineListDataValue = lineListDataValue;
    }

    private Integer delRecordNo;

    public void setDelRecordNo( Integer delRecordNo )
    {
        this.delRecordNo = delRecordNo;
    }

    private String reportedDate;

    public void setReportedDate( String reportedDate )
    {
        this.reportedDate = reportedDate;
    }

    private LineListGroup lineListGroup;

    private LineListOption lineListOption;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        System.out.println( "Reported Date " + reportedDate );

        List<LineListElement> lineListElements;

        List<LineListDataValue> llDataValuesList = new ArrayList<LineListDataValue>();
        List<LineListDataValue> llDataValuesUpdatedList = new ArrayList<LineListDataValue>();

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        // period = periodService.getPeriod(period.getStartDate(),
        // period.getEndDate(), period.getPeriodType());
        lineListGroup = selectedStateManager.getSelectedLineListGroup();

        lineListOption = selectedStateManager.getSelectedLineListOption();

        Period period;

        Period historyPeriod;
        Date historyDate = format.parseDate( reportedDate );

        if ( lineListGroup != null && lineListGroup.getPeriodType().getName().equalsIgnoreCase( "OnChange" ) )
        {
            period = periodService.getPeriod( 0 );

            //historyPeriod = periodService.getPeriod( historyDate, historyDate, new DailyPeriodType() );
            PeriodType dailyPeriodType = new DailyPeriodType();
            historyPeriod = dailyPeriodType.createPeriod( historyDate );
            //historyPeriod = new Period( new DailyPeriodType(), historyDate, historyDate );
            System.out.println( reportedDate + " : " + historyDate );
            if ( historyPeriod == null )
            {
                System.out.println( "historyPeriod is null" );
            }
            historyPeriod = reloadPeriodForceAdd( historyPeriod );
        }
        else
        {
            period = selectedStateManager.getSelectedPeriod();

            period = reloadPeriodForceAdd( period );

            historyPeriod = period;
        }

        // String parts[] = dataElementId.split( ":" );

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );
        session = req.getSession();

        int postLineListElementId = lineListGroup.getLineListElements().iterator().next().getId();


        // Saving aggregated data
        LineListElement lineListElement = lineListService.getLineListElement( postLineListElementId );

        List<LineListDataElementMap> lineListDataElementMaps = lineListService.getLinelistDataelementMappings( lineListElement, lineListOption );

        System.out.println( lineListDataElementMaps.size() );
        Iterator<LineListDataElementMap> lineListDEMapIterator = lineListDataElementMaps.iterator();

        while ( lineListDEMapIterator.hasNext() )
        {
            LineListDataElementMap lineListDataElementMap = lineListDEMapIterator.next();

            String mapName = "DE:" + lineListDataElementMap.getDataElement().getId() + ":" + lineListDataElementMap.getDataElementOptionCombo().getId();

            String value = req.getParameter( mapName );

            DataElement dataElement = lineListDataElementMap.getDataElement();

            DataElementCategoryOptionCombo optionCombo = lineListDataElementMap.getDataElementOptionCombo();

            DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, historyPeriod, optionCombo );

            if ( dataValue == null )
            {
                if ( value != null )
                {
                    LOG.debug( "Adding DataValue, value added" );

                    dataValue = new DataValue( dataElement, historyPeriod, organisationUnit, value, storedBy, new Date(), null, optionCombo );
                    dataValueService.addDataValue( dataValue );
                }
            }
            else
            {
                LOG.debug( "Updating DataValue, value added/changed" );

                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );
                dataValue.setStoredBy( storedBy );

                dataValueService.updateDataValue( dataValue );
            }
        }


        int totalRecords = Integer.parseInt( req.getParameter( "totalRecords" ) );
        int recordsFromDb = Integer.parseInt( req.getParameter( "recordsFromDb" ) );

        lineListElements = new ArrayList<LineListElement>( lineListGroup.getLineListElements() );


        String recordNumbersList = req.getParameter( "recordNumbersList" );
        //System.out.println( "RecordNumber List : " + recordNumbersList );

        // this code is for edited value saving
        String[] recordNos = recordNumbersList.split( ":" );
        for ( int j = 0; j < recordNos.length; j++ )
        {
            String recordNo = recordNos[j];
            System.out.println( "recordNo = " + recordNo );
            if ( !( recordNo.equals( "" ) ) )
            {
                Period tempPeriod = dataBaseManagerInterface.getPeriodByRecordNumber( lineListGroup.getShortName(), Integer.parseInt( recordNo ) );
                //if record is already present for this period than update it.
                if ( tempPeriod != null && tempPeriod.getId() == historyPeriod.getId() )
                {
                    System.out.println( "tempPeriod = " + tempPeriod.getId() + " historyPeriod = " + historyPeriod.getId() );
                    LineListDataValue llDataValue = new LineListDataValue();
                    String valueChangedName = "changedValue:" + recordNo;
                    String valueChanged = req.getParameter( valueChangedName );

                    // System.out.println( "While updating date value : " + valueChangedName + " : " + valueChanged );
                    if ( !( valueChanged.equalsIgnoreCase( "" ) ) )
                    {
                        String[] elementNames = valueChanged.split( " " );


                        Map<String, String> llElementValuesMap = new HashMap<String, String>();

                        for ( int e = 0; e < elementNames.length; e++ )
                        {
                            String name = elementNames[e] + ":" + recordNo;
                            String dataValue = req.getParameter( name );
                            if ( dataValue != null && dataValue.trim().equals( "" ) )
                            {
                                dataValue = "";
                            }
                            llElementValuesMap.put( elementNames[e], dataValue );
                        }

                        // add map in linelist data value
                        llDataValue.setLineListValues( llElementValuesMap );

                        //add period and source to row
                        llDataValue.setPeriod( historyPeriod );
                        llDataValue.setSource( organisationUnit );

                        // add recordNumber to pass to the update query
                        llDataValue.setRecordNumber( Integer.parseInt( recordNo ) );

                        // add stored by, timestamp in linelist data value
                        storedBy = currentUserService.getCurrentUsername();

                        if ( storedBy == null )
                        {
                            storedBy = "[unknown]";
                        }

                        llDataValue.setStoredBy( storedBy );

                        if ( reportedDate != null && !reportedDate.trim().equalsIgnoreCase( "" ) )
                        {
                            Date reportDate = format.parseDate( reportedDate );
                            llDataValue.setTimestamp( reportDate );
                        }
                        else
                        {
                            llDataValue.setTimestamp( new Date() );
                        }

                        llDataValuesUpdatedList.add( llDataValue );
                    }
                }
                else
                { //else add the records.
                    LineListDataValue llDataValue = new LineListDataValue();
                    Map<String, String> llElementValuesMap = new HashMap<String, String>();
                    System.out.println( "_________________new recordNo from  old period " + recordNo );
                    for ( LineListElement element : lineListElements )
                    {

                        String name = element.getShortName() + ":" + recordNo;
                        
                        String dataValue = req.getParameter( name );

                        if ( postLineListElementId == element.getId() )
                        {
                            dataValue = lineListOption.getName();
                        }


                        if ( dataValue != null && dataValue.trim().equals( "" ) )
                        {
                            dataValue = "";
                        }
                        if ( dataValue != null && !( dataValue.equals( "" ) ) )
                        {
                            //System.out.println( "name = " + element.getShortName() + " value  = " + dataValue );
                            llElementValuesMap.put( element.getShortName(), dataValue );
                        }
                    }

                    // add map in linelist data value
                    llDataValue.setLineListValues( llElementValuesMap );

                    //add period and source to row
                    llDataValue.setPeriod( historyPeriod );
                    llDataValue.setSource( organisationUnit );

                    // add recordNumber to pass to the update query
                    llDataValue.setRecordNumber( Integer.parseInt( recordNo ) );

                    // add stored by, timestamp in linelist data value
                    storedBy = currentUserService.getCurrentUsername();

                    if ( storedBy == null )
                    {
                        storedBy = "[unknown]";
                    }

                    llDataValue.setStoredBy( storedBy );

                    if ( reportedDate != null && !reportedDate.trim().equalsIgnoreCase( "" ) )
                    {
                        Date reportDate = format.parseDate( reportedDate );
                        llDataValue.setTimestamp( reportDate );
                    }
                    else
                    {
                        llDataValue.setTimestamp( new Date() );
                    }
                    llDataValuesUpdatedList.add( llDataValue );
                }
            }
        }

        //System.out.println( "recordsFromDb = " + recordsFromDb );
        // System.out.println( "totalRecords = " + totalRecords );
        
        // this code is for newly added values save
        while ( recordsFromDb < totalRecords )
        {

            recordsFromDb++;
            LineListDataValue llDataValue = new LineListDataValue();
            Map<String, String> llElementValuesMap = new HashMap<String, String>();
            System.out.println( "_________________new recordNo " + recordsFromDb );
            for ( LineListElement element : lineListElements )
            {

                String name = element.getShortName() + ":" + recordsFromDb;
                
                String dataValue = req.getParameter( name );

                if ( postLineListElementId == element.getId() )
                {
                    dataValue = lineListOption.getName();
                }


                if ( dataValue != null && dataValue.trim().equals( "" ) )
                {
                    dataValue = "";
                }
                if ( dataValue != null && !( dataValue.equals( "" ) ) )
                {
                    llElementValuesMap.put( element.getShortName(), dataValue );
                }
            }

            // add map in linelist data value
            llDataValue.setLineListValues( llElementValuesMap );

            // add period source, stored by, timestamp in linelist data value
            llDataValue.setPeriod( historyPeriod );
            llDataValue.setSource( organisationUnit );

            storedBy = currentUserService.getCurrentUsername();

            if ( storedBy == null )
            {
                storedBy = "[unknown]";
            }

            llDataValue.setStoredBy( storedBy );

            if ( reportedDate != null && !reportedDate.trim().equalsIgnoreCase( "" ) )
            {
                Date reportDate = format.parseDate( reportedDate );
                llDataValue.setTimestamp( reportDate );
            }
            else
            {
                llDataValue.setTimestamp( new Date() );
            }

            llDataValuesList.add( llDataValue );
        }

        if ( llDataValuesList.isEmpty() || llDataValuesList == null )
        {
            // deleteLLValue();
        }
        else
        {
            boolean valueInserted = dataBaseManagerInterface.insertLLValueIntoDb( llDataValuesList, lineListGroup.getShortName() );
            System.out.println( "valueInserted = " + valueInserted );
        }

        if ( llDataValuesUpdatedList.isEmpty() || llDataValuesUpdatedList == null )
        {
        }
        else
        {
            boolean updateLLValue = dataBaseManagerInterface.updateLLValue( llDataValuesUpdatedList, lineListGroup.getShortName() );
            System.out.println( "updateLLValue = " + updateLLValue );
        }
        if ( delRecordNo != null )
        {
            deleteLLValue();
        }
        return SUCCESS;

    }

    public void deleteLLValue()
    {
        if ( delRecordNo != null )
        {
            // System.out.println("delRecordNo = " + delRecordNo);
            boolean valueDeleted = dataBaseManagerInterface.removeLLRecord( delRecordNo, lineListGroup.getShortName() );
            System.out.println( "valueDeleted = " + valueDeleted );
        }
        else
        {
            System.out.println( "delRecordNo is null" );
        }
    }

    // -------------------------------------------------------------------------
    // Support methods for reloading periods
    // -------------------------------------------------------------------------
    private final Period reloadPeriod( Period period )
    {
        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }
}
