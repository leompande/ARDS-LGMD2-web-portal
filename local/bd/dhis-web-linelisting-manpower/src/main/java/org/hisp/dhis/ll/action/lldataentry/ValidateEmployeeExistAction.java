package org.hisp.dhis.ll.action.lldataentry;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.opensymphony.xwork2.Action;

public class ValidateEmployeeExistAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String pdsCode;

    public void setPdsCode( String pdsCode )
    {
        this.pdsCode = pdsCode;
    }

    private Employee employee;

    public Employee getEmployee()
    {
        return employee;
    }

    private String message;
    
    public String getMessage()
    {
        return message;
    }
    
    //--------------------------------------------------------------------------
    //Action Implementation
    //--------------------------------------------------------------------------
    
    public String execute()
    {
        employee = employeeService.getEmployeeByPDSCode( pdsCode );
        
        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        LineListGroup lineListGroup = selectedStateManager.getSelectedLineListGroup();
        String departmentLineListName = lineListGroup.getName();
        
        String pdsCodeColumnName = "pdscode";
        String lastWorkingDateColumnName  = "lastworkingdate";
        
        Map<String, String> llElementValueMap = new HashMap<String, String>();
        llElementValueMap.put( pdsCodeColumnName, pdsCode );
        llElementValueMap.put( lastWorkingDateColumnName, "null" );

        if ( employee == null )
        {
            message = "The Employee with this PDSCode does not exist. Do you want to add new Employee?";
            return INPUT;
        }
        else
        {
            message = "Employee name with this PDSCode is : " + employee.getName();
            int employeeRecord = dataBaseManagerInterface.getLLValueCountByLLElements( departmentLineListName, llElementValueMap, organisationUnit );
            System.out.println("Employee record is :" + employeeRecord );
            if ( employeeRecord != 0 )
            {
                message = "The Detail of this Employee already exist. Do you wantt to update Record?";
                return "employeeexist";
            }
        }
        
        return SUCCESS;
    }

}
