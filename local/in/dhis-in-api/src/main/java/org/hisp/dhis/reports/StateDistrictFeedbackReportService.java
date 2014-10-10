package org.hisp.dhis.reports;

import java.util.Date;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * <gaurav>,Date: 7/4/12, Time: 12:44 PM
 */
public interface StateDistrictFeedbackReportService
{
    String ID = StateDistrictFeedbackReportService.class.getName();

    public Map<String, String> getDistrictFeedbackData( OrganisationUnit District, Date sDate, Date eDate,
        String XmlFileName );

}
