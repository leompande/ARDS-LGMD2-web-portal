package org.hisp.dhis.reports.util;

import java.util.Comparator;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

public class OrganisationUnitLevelComparator implements Comparator<OrganisationUnit>
{
    OrganisationUnitService organisationUnitService = new org.hisp.dhis.organisationunit.DefaultOrganisationUnitService();
    
    public int compare( OrganisationUnit organisationUnit0, OrganisationUnit organisationUnit1 )
    {   
        //return (organisationUnitService.getLevelOfOrganisationUnit(organisationUnit1) - organisationUnitService.getLevelOfOrganisationUnit(organisationUnit0));
        return (organisationUnitService.getLevelOfOrganisationUnit(organisationUnit1.getId()) - organisationUnitService.getLevelOfOrganisationUnit(organisationUnit0.getId()));
    }
}
