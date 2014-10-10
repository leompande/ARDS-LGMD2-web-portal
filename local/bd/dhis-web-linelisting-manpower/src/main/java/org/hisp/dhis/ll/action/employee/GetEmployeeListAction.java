package org.hisp.dhis.ll.action.employee;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

public class GetEmployeeListAction extends ActionPagingSupport<Employee>
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
    // Parameters
    // -------------------------------------------------------------------------

    private List<Employee> employeeList;
    
    public List<Employee> getEmployeeList()
    {
        return employeeList;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        this.paging = createPaging( employeeService.getEmployeeCount() );
        
        employeeList = new ArrayList<Employee>( employeeService.getEmployeesBetween( paging.getStartPos(), paging.getPageSize() ) );
    	
        return SUCCESS;
    }
}
