package org.hisp.dhis.reports.util;

import java.util.Comparator;

import org.hisp.dhis.organisationunit.OrganisationUnit;

public class OrganisationUnitCommentComparator implements Comparator<OrganisationUnit>
{
    public int compare( OrganisationUnit organisationUnit0, OrganisationUnit organisationUnit1 )
    {
    	if(organisationUnit0.getComment() == null || organisationUnit1.getComment() == null ) return 0;

    	return organisationUnit0.getComment().compareTo( organisationUnit1.getComment() );
    }
}