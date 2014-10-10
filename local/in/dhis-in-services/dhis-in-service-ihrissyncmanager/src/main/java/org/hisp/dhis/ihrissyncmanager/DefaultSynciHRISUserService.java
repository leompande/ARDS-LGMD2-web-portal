package org.hisp.dhis.ihrissyncmanager;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Mohit
 * Date: 29/8/12
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultSynciHRISUserService implements SynciHRISUserService {
    UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    CurrentUserService currentUserService;

    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    PasswordManager passwordManager;

    public void setPasswordManager(PasswordManager passwordManager) {
        this.passwordManager = passwordManager;
    }


    OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }

    @Override
    public User createNewiHRISUser(String ihrisUsername, String firstName, String lastName, String email, String password) {

        UserCredentials currentUserCredentials = currentUserService.getCurrentUser() != null ? currentUserService
                .getCurrentUser().getUserCredentials() : null;


        // ---------------------------------------------------------------------
        // Create userCredentials and user
        // ---------------------------------------------------------------------

        Collection<OrganisationUnit> orgUnits = organisationUnitService.getAllOrganisationUnits();

        Collection<User> tempUserList = userService.getAllUsers();

        boolean isUpdated = false;

        User user = new User();
        user.setSurname(lastName);
        user.setFirstName(firstName);
        user.setEmail(email);

        user.updateOrganisationUnits(new HashSet<OrganisationUnit>(orgUnits));

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUser(user);

        userCredentials.setUsername(ihrisUsername);
        userCredentials.setPassword(passwordManager.encodePassword(ihrisUsername, password));
        user.setUserCredentials(userCredentials);

        if (tempUserList != null) {
            for (User existingUser : tempUserList) {

                UserCredentials existingUserCredentials = userService.getUserCredentials(existingUser);

                if (existingUserCredentials != null && existingUserCredentials.getUsername().equalsIgnoreCase(ihrisUsername)) {
                    existingUser.setSurname(lastName);
                    existingUser.setFirstName(firstName);
                    existingUser.setEmail(email);

                    existingUserCredentials.setUser(existingUser);

                    existingUserCredentials.setUsername(ihrisUsername);
                    existingUserCredentials.setPassword(passwordManager.encodePassword(ihrisUsername, password));
                    existingUser.setUserCredentials(existingUserCredentials);

                    userService.updateUser(existingUser);
                    userService.updateUserCredentials(existingUserCredentials);
                    isUpdated = true;
                }


            }
        }

        if (!isUpdated) {
            userService.addUser(user);
            userService.addUserCredentials(userCredentials);
        }

        return user;

    }
}
