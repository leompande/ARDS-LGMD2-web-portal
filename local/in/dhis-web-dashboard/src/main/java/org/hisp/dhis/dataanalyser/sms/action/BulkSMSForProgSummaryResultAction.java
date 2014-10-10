package org.hisp.dhis.dataanalyser.sms.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataanalyser.util.BulkSMSHttpInterface;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

public class BulkSMSForProgSummaryResultAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
    
    private List<Integer> usergroups;
    
    public void setUsergroups( List<Integer> usergroups )
    {
        this.usergroups = usergroups;
    }

    private String resultMessage;
    
    public String getResultMessage()
    {
        return resultMessage;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        Map<OrganisationUnit,String> tempOrgUnitMap = new HashMap<OrganisationUnit, String>();
        
        BulkSMSHttpInterface bulkSMSHTTPInterface = new BulkSMSHttpInterface();
        
        try
        {
            for( Integer userGroupId : usergroups )
            {
                UserGroup userGroup = userGroupService.getUserGroup( userGroupId );
                
                for( User user : userGroup.getMembers() )
                {
                    String phoneNumber = user.getPhoneNumber();
                    
                    for( OrganisationUnit orgUnit : user.getOrganisationUnits() )
                    {
                        String prgWiseSummaryMsg = tempOrgUnitMap.get( orgUnit );
                        
                        if( prgWiseSummaryMsg == null )
                        {
                            prgWiseSummaryMsg = dashBoardService.getProgramwiseSummarySMS( orgUnit );
                        }
                        
                        bulkSMSHTTPInterface.sendMessage( prgWiseSummaryMsg, phoneNumber );
                    }
                }
            }
            
            resultMessage = bulkSMSHTTPInterface.checkBalance();
        }
        catch( Exception e )
        {
            resultMessage = "Not able to sent SMS to the group bec of "+e.getMessage();
        }
        return SUCCESS;
    }
}
