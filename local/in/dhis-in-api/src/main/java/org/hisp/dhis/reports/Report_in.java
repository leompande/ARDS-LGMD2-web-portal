package org.hisp.dhis.reports;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in element and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of element code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.PeriodType;

@SuppressWarnings("serial")
public class Report_in
    implements Serializable
{

    /**
     * The unique identifier for this Report_in
     */
    private int id;

    /**
     * Name of Report_in. Required and unique.
     */
    private String name;

    /**
     * Model of the Report_in (like Static, dynamic etc.). Required.
     */
    private String model;

    /**
     * The PeriodType indicating the frequency that this Report_in should be
     * used
     */
    private PeriodType periodType;

    private String excelTemplateName;

    private String xmlTemplateName;

    private String reportType;

    private OrganisationUnitGroup orgunitGroup;
    
    private String dataSetIds;
    
    /**
     * All Sources that are generating this Report_in.
     */
    private Set<OrganisationUnit> sources = new HashSet<OrganisationUnit>();

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public Report_in()
    {

    }

    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName,
        String xmlTemplateName, String reportType )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
    }

    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName,
        String xmlTemplateName, String reportType, OrganisationUnitGroup orgunitGroup )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
        this.orgunitGroup = orgunitGroup;
    }

    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName,
        String xmlTemplateName, String reportType, String dataSetIds )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
        this.dataSetIds = dataSetIds;
    }

    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName,
        String xmlTemplateName, String reportType, OrganisationUnitGroup orgunitGroup, String dataSetIds )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
        this.orgunitGroup = orgunitGroup;
        this.dataSetIds = dataSetIds;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof Report_in) )
        {
            return false;
        }

        final Report_in other = (Report_in) o;

        return name.equals( other.getName() );
    }

    
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addOrganisationUnit( OrganisationUnit unit )
    {
        sources.add( unit );
    }
    
    public void removeOrganisationUnit( OrganisationUnit unit )
    {
        sources.remove( unit );
    }
    
    public void updateOrganisationUnits( Set<OrganisationUnit> updates )
    {
        for ( OrganisationUnit unit : new HashSet<OrganisationUnit>( sources ) )
        {
            if ( !updates.contains( unit ) )
            {
                removeOrganisationUnit( unit );
            }
        }
        
        for ( OrganisationUnit unit : updates )
        {
            addOrganisationUnit( unit );
        }
    }
    
    
    
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel( String model )
    {
        this.model = model;
    }

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    public String getExcelTemplateName()
    {
        return excelTemplateName;
    }

    public void setExcelTemplateName( String excelTemplateName )
    {
        this.excelTemplateName = excelTemplateName;
    }

    public String getXmlTemplateName()
    {
        return xmlTemplateName;
    }

    public void setXmlTemplateName( String xmlTemplateName )
    {
        this.xmlTemplateName = xmlTemplateName;
    }

    public String getReportType()
    {
        return reportType;
    }

    public void setReportType( String reportType )
    {
        this.reportType = reportType;
    }

    public Set<OrganisationUnit> getSources()
    {
        return sources;
    }

    public void setSources( Set<OrganisationUnit> sources )
    {
        this.sources = sources;
    }

    public OrganisationUnitGroup getOrgunitGroup()
    {
        return orgunitGroup;
    }

    public void setOrgunitGroup( OrganisationUnitGroup orgunitGroup )
    {
        this.orgunitGroup = orgunitGroup;
    }

    public String getDataSetIds()
    {
        return dataSetIds;
    }

    public void setDataSetIds( String dataSetIds )
    {
        this.dataSetIds = dataSetIds;
    }
}