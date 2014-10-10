package org.hisp.dhis.ll.action.employee;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;

import com.opensymphony.xwork2.Action;

public class ValidateEmployeeAction
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
    // Input/Output
    // -------------------------------------------------------------------------

    private String pdscode;

    public void setPdscode( String pdscode )
    {
        this.pdscode = pdscode;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
    {

        if ( pdscode != null )
        {

            Employee match = employeeService.getEmployeeByPDSCode( pdscode );

            if ( match != null )
            {
                message = i18n.getString( "pdsCode_in_use" );

                return ERROR;
            }
        }

        message = "OK";

        return SUCCESS;
    }

}
