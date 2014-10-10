package org.hisp.dhis.ll.action.employee;

import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;

import com.opensymphony.xwork2.Action;

public class ShowUpdateEmployeeFormAction
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

    private String id;

    public void setId( String id )
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    private Employee employee;

    public Employee getEmployee()
    {
        return employee;
    }

    private Integer lprPeriod;

    public Integer getLprPeriod()
    {
        return lprPeriod;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        employee = employeeService.getEmployeeByPDSCode( id );

        lprPeriod = Employee.LPR_PERIOD;

        return SUCCESS;
    }

}
