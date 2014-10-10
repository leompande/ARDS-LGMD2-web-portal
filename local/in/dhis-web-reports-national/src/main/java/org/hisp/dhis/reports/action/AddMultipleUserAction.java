package org.hisp.dhis.reports.action;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

public class AddMultipleUserAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    /*
    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
    */
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
    
    private UserService userService;
    
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        String raFolderName = reportService.getRAFolderName();
        
        String fileName = "user.xls";
        String excelImportFolderName = "excelimport";
        String excelFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator  + fileName;
        
        Workbook templateWorkbook = Workbook.getWorkbook( new File( excelFilePath ) );
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( excelFilePath ), templateWorkbook );
        
        int sheetNo = 0 ;
        WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
        Integer rowStart = Integer.parseInt( sheet0.getCell( 8, 0 ).getContents() );
        Integer rowEnd = Integer.parseInt( sheet0.getCell( 8, 1 ).getContents() );
        System.out.println( "User  Creation Start Time is : " + new Date() );
        System.out.println( "Row Start : " + rowStart + " ,Row End : "  + rowEnd );
        int orgunitcount = 0;
        for( int i = rowStart ; i <= rowEnd ; i++ )
        {
            Integer orgUnitId = Integer.parseInt( sheet0.getCell( 0, i ).getContents() );
            String orgUnitname = sheet0.getCell( 1, i ).getContents();
            String orgUnitCode = sheet0.getCell( 2, i ).getContents();
            String userId = sheet0.getCell( 3, i ).getContents();
            String passWord = sheet0.getCell( 4, i ).getContents();
            Integer userRoleId = Integer.parseInt( sheet0.getCell( 5, i ).getContents() );
            
            OrganisationUnit orgUId = organisationUnitService.getOrganisationUnit( orgUnitId );
            Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>();
            orgUnits.add( orgUId );
            
            Collection<User> tempUserList = orgUId.getUsers();
            int flag = 0;
            if ( tempUserList != null )
            {
                for ( User u : tempUserList )
                {
                    //UserCredentials uc = userStore.getUserCredentials( u );
                    UserCredentials uc = userService.getUserCredentials( u );
                    if ( uc != null && uc.getUsername().equalsIgnoreCase( userId ) )
                        flag = 1;
                }
            }
            if ( flag == 1 )
            {
                System.out.println( userId + " ALREADY EXITS" );
                continue;
            }
            
            User user = new User();
            user.setSurname( orgUnitname );
            user.setFirstName( orgUnitCode );
            user.setOrganisationUnits( orgUnits );
            
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setUser( user );
            userCredentials.setUsername( userId );
            userCredentials.setPassword( passwordManager.encodePassword( userId, passWord ) );
            
            UserAuthorityGroup group = userService.getUserAuthorityGroup( userRoleId );
            
            //UserAuthorityGroup group = userStore.getUserAuthorityGroup( userRoleId );
            userCredentials.getUserAuthorityGroups().add( group );

            //userStore.addUser( user );
            //userStore.addUserCredentials( userCredentials );
            
            userService.addUser( user );
            userService.addUserCredentials( userCredentials );
            System.out.println( orgUnitname + " Created" );
            orgunitcount++;
        }
       
        outputReportWorkbook.close();
        
        System.out.println( "**********************************************" );
        System.out.println( "MULTIPLE USER CREATION IS FINISHED" );
        System.out.println( "Total No of User Created : -- " + orgunitcount );
        System.out.println( "**********************************************" );
        System.out.println( "User  Creation End Time is : " + new Date() );
        return SUCCESS;
    }
}
