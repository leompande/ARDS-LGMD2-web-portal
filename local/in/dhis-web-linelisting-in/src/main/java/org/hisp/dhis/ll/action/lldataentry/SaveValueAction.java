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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
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

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }

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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        System.out.println("Reported Date " + reportedDate);


        // Map<String, String> llElementValuesMap = new HashMap<String,
        // String>();
        List<LineListElement> lineListElements;

        List<LineListDataValue> llDataValuesList = new ArrayList<LineListDataValue>();
        List<LineListDataValue> llDataValuesUpdatedList = new ArrayList<LineListDataValue>();

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        // period = periodService.getPeriod(period.getStartDate(),
        // period.getEndDate(), period.getPeriodType());
        lineListGroup = selectedStateManager.getSelectedLineListGroup();

        Period period;

        if ( lineListGroup != null && lineListGroup.getPeriodType().getName().equalsIgnoreCase( "OnChange" ) )
        {
            period = periodService.getPeriod( 0 );
        }
        else
        {
            period = selectedStateManager.getSelectedPeriod();

            period = reloadPeriodForceAdd( period );
        }

        // String parts[] = dataElementId.split( ":" );

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );
        session = req.getSession();

        int totalRecords = Integer.parseInt( req.getParameter( "totalRecords" ) );
        int recordsFromDb = Integer.parseInt( req.getParameter( "recordsFromDb" ) );

        lineListElements = new ArrayList<LineListElement>( lineListGroup.getLineListElements() );

        String recordNumbersList = req.getParameter( "recordNumbersList" );
        System.out.println( recordNumbersList );
        // this code is for edited value saving
        String[] recordNos = recordNumbersList.split( ":" );
        for ( int j = 0; j < recordNos.length; j++ )
        {
            String recordNo = recordNos[j];
            System.out.println( "recordNo = " + recordNo );
            if ( !(recordNo.equals( "" )) )
            {
                String valueChangedName = "changedValue:" + recordNo;
                String valueChanged = req.getParameter( valueChangedName );
                //System.out.println("valueChanged = "+valueChanged);
                if ( !(valueChanged.equalsIgnoreCase( "" )) )
                {
                    System.out.println( "valueChanged = " + valueChanged );
                    String[] elementNames = valueChanged.split( " " );
                    LineListDataValue llDataValue = new LineListDataValue();
                    Map<String, String> llElementValuesMap = new HashMap<String, String>();
                    for ( int e = 0; e < elementNames.length; e++ )
                    {
                        String name = elementNames[e] + ":" + recordNo;
                        String dataValue = req.getParameter( name );
                        // System.out.println("name = " + name + " value  = " +
                        // dataValue);
                        if ( dataValue != null && dataValue.trim().equals( "" ) )
                        {
                            dataValue = "";
                        }
                        // if(dataValue.equals( "" ))
                        llElementValuesMap.put( elementNames[e], dataValue );
                        System.out.println("llElementValuesMap size = "+llElementValuesMap.size() + " key = "+elementNames[e]+ " value = "+llElementValuesMap.get( elementNames[e]));

                    }

                    // add map in linelist data value
                    llDataValue.setLineListValues( llElementValuesMap );

                    // add recordNumber to pass to the update query
                    llDataValue.setRecordNumber( Integer.parseInt( recordNo ) );

                    // add stored by, timestamp in linelist data value
                    storedBy = currentUserService.getCurrentUsername();

                    if ( storedBy == null )
                    {
                        storedBy = "[unknown]";
                    }

                    llDataValue.setStoredBy( storedBy );
                    
                    if(reportedDate != null )
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

        // this code is for newly added values save
        while ( recordsFromDb < totalRecords )
        {
            System.out.println( "recordsFromDb = " + recordsFromDb );
            System.out.println( "totalRecords = " + totalRecords );
            recordsFromDb++;
            LineListDataValue llDataValue = new LineListDataValue();
            Map<String, String> llElementValuesMap = new HashMap<String, String>();
            for ( LineListElement element : lineListElements )
            {

                String name = element.getShortName() + ":" + recordsFromDb;
                String dataValue = req.getParameter( name );
                System.out.println( "name = " + name + " value  = " + req.getParameter( name ) );

                if ( dataValue != null && dataValue.trim().equals( "" ) )
                {
                    dataValue = "";
                }
                if ( !(dataValue.equals( "" )) )
                    llElementValuesMap.put( element.getShortName(), dataValue );
            }

            // add map in linelist data value
            llDataValue.setLineListValues( llElementValuesMap );

            // add period source, stored by, timestamp in linelist data value
            llDataValue.setPeriod( period );
            llDataValue.setSource( organisationUnit );

            storedBy = currentUserService.getCurrentUsername();

            if ( storedBy == null )
            {
                storedBy = "[unknown]";
            }

            llDataValue.setStoredBy( storedBy );

            if(reportedDate != null && !reportedDate.trim().equalsIgnoreCase( "" ))
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

            boolean valueInserted = dataBaseManagerInterface.insertLLValueIntoDb( llDataValuesList, lineListGroup
                .getShortName() );
            System.out.println( "valueInserted = " + valueInserted );
        }

        if ( llDataValuesUpdatedList.isEmpty() || llDataValuesUpdatedList == null )
        {

        }
        else
        {
            boolean updateLLValue = dataBaseManagerInterface.updateLLValue( llDataValuesUpdatedList, lineListGroup
                .getShortName() );
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
