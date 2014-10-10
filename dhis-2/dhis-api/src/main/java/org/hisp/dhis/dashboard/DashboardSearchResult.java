package org.hisp.dhis.dashboard;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.patientreport.PatientTabularReport;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.user.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "dashboardSearchResult", namespace = DxfNamespaces.DXF_2_0)
public class DashboardSearchResult
{
    private List<User> users = new ArrayList<User>();
    
    private List<Chart> charts = new ArrayList<Chart>();
    
    private List<Map> maps = new ArrayList<Map>();

    private List<ReportTable> reportTables = new ArrayList<ReportTable>();
    
    private List<Report> reports = new ArrayList<Report>();

    private List<Document> resources = new ArrayList<Document>();
    
    private List<PatientTabularReport> patientTabularReports = new ArrayList<PatientTabularReport>();

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DashboardSearchResult()
    {
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    @JsonProperty
    public int getSearchCount()
    {
        int results = 0;
        results += users.size();
        results += charts.size();
        results += maps.size();
        results += reportTables.size();
        results += reports.size();
        results += resources.size();
        results += patientTabularReports.size();
        return results;
    }

    @JsonProperty
    public int getUserCount()
    {
        return users.size();
    }
    
    @JsonProperty
    public int getChartCount()
    {
        return charts.size();
    }

    @JsonProperty
    public int getMapCount()
    {
        return maps.size();
    }

    @JsonProperty
    public int getReportTableCount()
    {
        return reportTables.size();
    }

    @JsonProperty
    public int getReportCount()
    {
        return reports.size();
    }

    @JsonProperty
    public int getResourceCount()
    {
        return resources.size();
    }  
    
    @JsonProperty
    public int getPatientTabularReportCount()
    {
        return patientTabularReports.size();
    }    


    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty( value = "users" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "users", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "user", namespace = DxfNamespaces.DXF_2_0)
    public List<User> getUsers()
    {
        return users;
    }

    public void setUsers( List<User> users )
    {
        this.users = users;
    }

    @JsonProperty( value = "charts" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "charts", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "chart", namespace = DxfNamespaces.DXF_2_0)
    public List<Chart> getCharts()
    {
        return charts;
    }

    public void setCharts( List<Chart> charts )
    {
        this.charts = charts;
    }

    @JsonProperty( value = "maps" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "maps", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "map", namespace = DxfNamespaces.DXF_2_0)
    public List<Map> getMaps()
    {
        return maps;
    }

    public void setMaps( List<Map> maps )
    {
        this.maps = maps;
    }

    @JsonProperty( value = "reportTables" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "reportTables", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "reportTable", namespace = DxfNamespaces.DXF_2_0)
    public List<ReportTable> getReportTables()
    {
        return reportTables;
    }

    public void setReportTables( List<ReportTable> reportTables )
    {
        this.reportTables = reportTables;
    }

    @JsonProperty( value = "reports" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "reports", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "report", namespace = DxfNamespaces.DXF_2_0)
    public List<Report> getReports()
    {
        return reports;
    }

    public void setReports( List<Report> reports )
    {
        this.reports = reports;
    }

    @JsonProperty( value = "resources" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "resources", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "resource", namespace = DxfNamespaces.DXF_2_0)
    public List<Document> getResources()
    {
        return resources;
    }

    public void setResources( List<Document> resources )
    {
        this.resources = resources;
    }

    @JsonProperty( value = "patientTabularReports" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "patientTabularReports", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "patientTabularReports", namespace = DxfNamespaces.DXF_2_0)
    public List<PatientTabularReport> getPatientTabularReports()
    {
        return patientTabularReports;
    }

    public void setPatientTabularReports( List<PatientTabularReport> patientTabularReports )
    {
        this.patientTabularReports = patientTabularReports;
    }
}
