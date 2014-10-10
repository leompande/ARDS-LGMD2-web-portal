package org.hisp.dhis.ll.action.employee;

import java.util.Date;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;

import com.opensymphony.xwork2.Action;

public class UpdateEmployeeAction
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
    // Input
    // -------------------------------------------------------------------------
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private String pdscode;

    public String getPdscode()
    {
        return pdscode;
    }

    public void setPdscode( String pdscode )
    {
        this.pdscode = pdscode;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String dob;

    public void setDob( String dob )
    {
        this.dob = dob;
    }

    private String lprDate;

    public void setLprDate( String lprDate )
    {
        this.lprDate = lprDate;
    }

    private String govtSerJoinDate;

    public void setGovtSerJoinDate( String govtSerJoinDate )
    {
        this.govtSerJoinDate = govtSerJoinDate;
    }

    private String sex;

    public void setSex( String sex )
    {
        this.sex = sex;
    }

    private String resAdd;

    public void setResAdd( String resAdd )
    {
        this.resAdd = resAdd;
    }

    private String contactNo;

    public void setContactNo( String contactNo )
    {
        this.contactNo = contactNo;
    }

    private String emerContactNo;

    public void setEmerContactNo( String emerContactNo )
    {
        this.emerContactNo = emerContactNo;
    }

    public String execute()
    {
        Employee employee = employeeService.getEmployeeByPDSCode( pdscode );

        Date dateOfBirth = format.parseDate( dob );

        Date lpRetirementDate = format.parseDate( lprDate );

        Date joinDate = format.parseDate( govtSerJoinDate );

        employee.setPdsCode( pdscode );

        employee.setName( name );

        employee.setDateOfBirth( dateOfBirth );

        employee.setLprDate( lpRetirementDate );

        employee.setSex( sex );

        employee.setJoinDateToGovtService( joinDate );

        employee.setResAddress( resAdd );

        employee.setContactNumber( contactNo );

        employee.setEmergencyContactNumber( emerContactNo );

        employeeService.updateEmployee( employee );

        return SUCCESS;
    }

}