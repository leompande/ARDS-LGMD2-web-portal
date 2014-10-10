package org.hisp.dhis.ll.action.employee;

import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;

import com.opensymphony.xwork2.Action;

public class GetEmployeeAction
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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        employee = employeeService.getEmployeeByPDSCode( pdsCode );

        if( employee == null )
        {
            message = "The Employee with this PDSCode does not exist. Do you want to add new Employee?";
            return INPUT;
        }
        message = "Employee name with this PDSCode is : " + employee.getName();
        return SUCCESS;
    }

}
