package org.hisp.dhis.ll.action.employee;

import org.hisp.dhis.linelisting.Employee;

import com.opensymphony.xwork2.Action;

public class ShowAddEmployeeFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer lprPeriod;

    public Integer getLprPeriod()
    {
        return lprPeriod;
    }

    public String execute()
    {
        lprPeriod = Employee.LPR_PERIOD;

        return SUCCESS;
    }

}
