package org.hisp.dhis.ihrissyncmanager.action;

/**
 * Created with IntelliJ IDEA.
 * User: Mohit
 * Date: 29/8/12
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.ihrissyncmanager.SynciHRISUserService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

public class SynciHRISUserAction implements Action {

    //------------------------------------------------------------------------------------------------------
    //                                       Dependencies
    //------------------------------------------------------------------------------------------------------


    private SynciHRISUserService synciHRISUserService;

    public void setSynciHRISUserService(SynciHRISUserService synciHRISUserService) {
        this.synciHRISUserService = synciHRISUserService;
    }

    //------------------------------------------------------------------------------------------------------
    //                                      Web-Params
    //------------------------------------------------------------------------------------------------------

    String ihrisUsername;
    String firstName;

    public void setIhrisUsername(String ihrisUsername) {
        this.ihrisUsername = ihrisUsername;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String lastName;
    String email;
    String password;

    //------------------------------------------------------------------------------------------------------
    //                                       Action Implementation
    //------------------------------------------------------------------------------------------------------

    public String execute() throws Exception
    {

        System.out.println("=====================================================================================================================================");

        System.out.println("* TEST ihris User ACTION [Username:"+ihrisUsername+" ,First Name:"+firstName+" ,Last Name:"+lastName+" , Email: "+email+", Password: "+password+"]");

        System.out.println("======================================================================================================================================");

        synciHRISUserService.createNewiHRISUser(ihrisUsername,firstName,lastName,email,password);

        return null;
    }
}
