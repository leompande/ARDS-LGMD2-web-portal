package org.hisp.dhis.ll.action.lldataentry;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class UpdateEmployeePostAction
    implements Action
{
    //--------------------------------------------------------------------------
    // Dependencies
    //--------------------------------------------------------------------------

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }
    
    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataBaseManagerInterface dbManagerInterface;

    public void setDbManagerInterface( DataBaseManagerInterface dbManagerInterface )
    {
        this.dbManagerInterface = dbManagerInterface;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    //--------------------------------------------------------------------------
    // Input/Output
    //--------------------------------------------------------------------------

    private String department;

    public void setDepartment( String department )
    {
        this.department = department;
    }

    private String post;

    public void setPost( String post )
    {
        this.post = post;
    }

    private String storedBy;

    public String getStoredBy()
    {
        return storedBy;
    }

    public String reportingDate;

    public void setReportingDate( String reportingDate )
    {
        this.reportingDate = reportingDate;
    }

    public String getReportingDate()
    {
        return reportingDate;
    }

    private Integer groupid;

    public void setGroupid( Integer groupid )
    {
        this.groupid = groupid;
    }

    private LineListGroup lineListGroup;

    //--------------------------------------------------------------------------
    // Action Impplementation
    //--------------------------------------------------------------------------

    public String execute()
    {
        HttpServletRequest request = ServletActionContext.getRequest();

        System.out.println( "GroupId id :::::" + groupid );

        Collection<LineListElement> linelistElements = lineListService.getLineListGroup( groupid )
            .getLineListElements();

        lineListGroup = selectedStateManager.getSelectedLineListGroup();

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        Period historyPeriod = getHistoryPeriod();

        Map<String, String> llElementValuesMap = new HashMap<String, String>();
        
        Map<String, String> llDataValueMap = new HashMap<String, String>();
        int i = 0;
        int lineListElementsSize = linelistElements.size() - 1;
        String leftReason = null;
        String pdsCode = "";
        String pdsCodeColName = "pdscode";
        String lastWorkingDateColumnName  = "lastworkingdate";
        for ( LineListElement linelistElement : linelistElements )
        {
            String linelistElementValue = request.getParameter( linelistElement.getShortName() );
            System.out.println("i="+i+" The Linelist Element value is ::::::::: " + linelistElementValue );

            if ( i == 1 )
            {
                pdsCode = linelistElementValue;
            }
            else if ( i == lineListElementsSize )
            {
                leftReason = linelistElementValue;
            }

            if ( linelistElementValue == null )
            {
                i++;
                continue;
            }
         
            if ( linelistElementValue != null && linelistElementValue.trim().equals( "" ) )
            {
                linelistElementValue = "";
            }
          
            llElementValuesMap.put( linelistElement.getShortName(), linelistElementValue );
            i++;
        }
        String postColumnId = linelistElements.iterator().next().getShortName();
        llElementValuesMap.put( postColumnId, post );
        
        llDataValueMap.put( pdsCodeColName, pdsCode );
        llDataValueMap.put( lastWorkingDateColumnName, "null" );
        llDataValueMap.put( postColumnId, post );
        
        List<LineListDataValue> llDataValueList = dbManagerInterface.getLLValuesFilterByLLElements( department, llDataValueMap, organisationUnit );
        
        LineListDataValue llDataValue = new LineListDataValue();
        
        if( llDataValueList != null && llDataValueList.size() != 0 )
        {
            llDataValue = llDataValueList.iterator().next();
        
            // add map in linelist data value
            llDataValue.setLineListValues( llElementValuesMap );
    
            // add period and source to row
            llDataValue.setPeriod( historyPeriod );
    
            // add stored by, timestamp in linelist data value
            storedBy = currentUserService.getCurrentUsername();
    
            if ( storedBy == null )
            {
                storedBy = "[unknown]";
            }
    
            llDataValue.setStoredBy( storedBy );
        }
        boolean valueUpdated = dbManagerInterface.updateSingleLLValue( llDataValue, department );

        if ( valueUpdated )
        {
            System.out.println( "Values Successfully Updated in DB" );
        }
        
        Employee employee = employeeService.getEmployeeByPDSCode( pdsCode );
        
        System.out.println("The PDSCode is :" + pdsCode + " And LeftReason is :" + leftReason );
        
        if( employee != null && leftReason.equalsIgnoreCase( "Transfer Reason" ) )
        {
            System.out.println("Inside Transfer reason" );
            employee.setIsTransferred( true );
            employeeService.updateEmployee( employee );
            System.out.println("The value of Employee Transfer is :" + employee.getIsTransferred() );
        }
        else
        {
            System.out.println("Either Employee Object is null or left reason is different than transfer");
        }

        return SUCCESS;
    }

    private Period getHistoryPeriod()
    {
        Date historyDate = format.parseDate( reportingDate );
        System.out.println( "Report Date is :::::::" + reportingDate );

        Period period;
        period = periodService.getPeriod( 0 );
        Period historyPeriod;

        if ( lineListGroup != null && lineListGroup.getPeriodType().getName().equalsIgnoreCase( "OnChange" ) )
        {
            PeriodType dailyPeriodType = new DailyPeriodType();
            historyPeriod = dailyPeriodType.createPeriod( historyDate );

            System.out.println( reportingDate + " : " + historyPeriod );
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

        return historyPeriod;
    }

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
