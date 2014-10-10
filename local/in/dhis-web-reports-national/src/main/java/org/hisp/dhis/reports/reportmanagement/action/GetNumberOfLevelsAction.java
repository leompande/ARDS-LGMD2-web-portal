package org.hisp.dhis.reports.reportmanagement.action;

//import java.util.ArrayList;
import java.util.Collection; //import java.util.Iterator;
import java.util.List;

//import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService; //import org.hisp.dhis.user.CurrentUserService;
//import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

public class GetNumberOfLevelsAction
  implements Action
{
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private OrganisationUnitService organisationUnitService;

  public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
  {
      this.organisationUnitService = organisationUnitService;
  }

  private OrganisationUnitGroupService organisationUnitGroupService;

  public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
  {
      this.organisationUnitGroupService = organisationUnitGroupService;
  }

  /*
   * private CurrentUserService currentUserService;
   * 
   * public void setCurrentUserService( CurrentUserService currentUserService
   * ) { this.currentUserService = currentUserService; }
   */
  // -------------------------------------------------------------------------
  // Input/Output Getters & setters
  // -------------------------------------------------------------------------

  private List<OrganisationUnitLevel> levels;

  public List<OrganisationUnitLevel> getLevels()
  {
      return levels;
  }

  private Collection<OrganisationUnitGroup> orgUnitGroups;

  public Collection<OrganisationUnitGroup> getOrgUnitGroups()
  {
      return orgUnitGroups;
  }

  // -------------------------------------------------------------------------
  // ActionSupport implementation
  // -------------------------------------------------------------------------

  public String execute()
  {
      levels = organisationUnitService.getOrganisationUnitLevels();
      orgUnitGroups = organisationUnitGroupService.getAllOrganisationUnitGroups();

      /*
       * int minLevel =
       * organisationUnitService.getNumberOfOrganisationalLevels();
       * System.out.println(minLevel); User currentUser =
       * currentUserService.getCurrentUser(); List<OrganisationUnit>
       * orgUnitList = new ArrayList<OrganisationUnit>(
       * currentUser.getOrganisationUnits() ); for( OrganisationUnit orgUnit :
       * orgUnitList ) { int tempLevel =
       * organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId()
       * ); if( minLevel > tempLevel ) minLevel = tempLevel; }
       * 
       * Iterator<OrganisationUnitLevel> ouLevelIterator = levels.iterator();
       * while( ouLevelIterator.hasNext() ) { OrganisationUnitLevel ouLevel =
       * ouLevelIterator.next();
       * 
       * if(minLevel > ouLevel.getLevel() ) { ouLevelIterator.remove(); } }
       */

      return SUCCESS;
  }
}
