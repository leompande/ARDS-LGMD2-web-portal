package org.hisp.dhis.reports.comparator;

import java.util.Comparator;

import org.hisp.dhis.reports.Report_in;

public class Report_inNameComparator implements Comparator<Report_in>
{
    public int compare( Report_in report0, Report_in report1 )
    {
        return report0.getName().compareTo( report1.getName() );
    }
}
