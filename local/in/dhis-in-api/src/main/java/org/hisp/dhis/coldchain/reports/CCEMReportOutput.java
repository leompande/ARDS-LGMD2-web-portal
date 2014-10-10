package org.hisp.dhis.coldchain.reports;

import java.util.List;

public class CCEMReportOutput
{

    private String reportHeading;
    
    private String outputType;
    
    private List<String> tableHeadings;
    
    private List<List<String>> tableSubHeadings;
    
    private List<List<String>> tableData;

    public List<String> getTableHeadings()
    {
        return tableHeadings;
    }

    public void setTableHeadings( List<String> tableHeadings )
    {
        this.tableHeadings = tableHeadings;
    }

    public List<List<String>> getTableData()
    {
        return tableData;
    }

    public void setTableData( List<List<String>> tableData )
    {
        this.tableData = tableData;
    }

    public List<List<String>> getTableSubHeadings()
    {
        return tableSubHeadings;
    }

    public void setTableSubHeadings( List<List<String>> tableSubHeadings )
    {
        this.tableSubHeadings = tableSubHeadings;
    }

    public String getReportHeading()
    {
        return reportHeading;
    }

    public void setReportHeading( String reportHeading )
    {
        this.reportHeading = reportHeading;
    }

    public String getOutputType()
    {
        return outputType;
    }

    public void setOutputType( String outputType )
    {
        this.outputType = outputType;
    }
    
}
