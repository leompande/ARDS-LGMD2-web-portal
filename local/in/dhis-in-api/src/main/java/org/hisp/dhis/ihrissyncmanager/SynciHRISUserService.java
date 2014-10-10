package org.hisp.dhis.ihrissyncmanager;

import org.hisp.dhis.user.User;

/**
 * Created with IntelliJ IDEA.
 * User: Mohit
 * Date: 29/8/12
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SynciHRISUserService
{
    String ID = SynciHRISUserService.class.getName();

    public User createNewiHRISUser(String ihrisUsername, String firstName, String lastName, String email, String password);


}
