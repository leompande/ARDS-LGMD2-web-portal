package org.hisp.dhis.reports;

import java.util.Date;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;

/**
 * <gaurav>,Date: 7/10/12, Time: 5:24 PM
 */
public interface ReportOrgSpecificDataService 
{
    public Map<String, String> getOrgSpecificData(OrganisationUnit District, Date sDate, Date eDate, Date aggSDate, PeriodType periodType, String XmlFileName);

}
