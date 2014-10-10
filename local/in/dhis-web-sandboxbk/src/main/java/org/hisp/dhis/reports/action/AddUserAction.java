package org.hisp.dhis.reports.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

public class AddUserAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private PasswordManager passwordManager;

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        String email = null;
        String username = null;
        String rawPassword = "dhis";
        String surname = "NRHM";
        String firstName = "HMIS";
        
        int userRoles[] = { 0, 1, 1, 3, 5, 6, 5 };
        int orgUnitLevels = organisationUnitService.getNumberOfOrganisationalLevels();
        for(int i = 1; i <= orgUnitLevels-1; i++)
        {
            List<OrganisationUnit> ouList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsAtLevel( i ));
            
            for( OrganisationUnit orgU : ouList )
            {
                username = orgU.getShortName();
                
                username = username.replace( " ", "" );
                
                username = username.toLowerCase();
                
                username = username.trim();
               
                Collection<User> tempUserList = userStore.getUsersByOrganisationUnit( orgU );
                
                int flag = 0;
                if(tempUserList != null )
                {
                    for( User u : tempUserList )
                    {                        
                        UserCredentials uc = userStore.getUserCredentials( u );
                        if( uc != null && uc.getUsername().equalsIgnoreCase( username ) )
                            flag = 1;
                    }                    
                }
                
                if(flag == 1 )
                {
                    System.out.println(username+" ALREADY THERE");
                    continue;
                }
                
                Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>();
                orgUnits.add( orgU );
                
                User user = new User();
                user.setSurname( surname );
                user.setFirstName( firstName );
                user.setEmail( email );
                user.setOrganisationUnits( orgUnits );
        
                UserCredentials userCredentials = new UserCredentials();
                userCredentials.setUser( user );
                userCredentials.setUsername( username );
                userCredentials.setPassword( passwordManager.encodePassword( username, rawPassword ) );
                        
                UserAuthorityGroup group = userStore.getUserAuthorityGroup( userRoles[i] );
                userCredentials.getUserAuthorityGroups().add( group );
                
                userStore.addUser( user );
                userStore.addUserCredentials( userCredentials );
                System.out.println(username+" Created");
            }// OrgUnit For Loop End
        
            System.out.println("**********************************************");
            System.out.println("User Creation for Level "+i+" is completed");
        }// OrgUnitLevel for loop end
        
        System.out.println("**********************************************");
        System.out.println("USER CREATION IS FINISHED");
        System.out.println("**********************************************");
        return SUCCESS;
    }

}
