package org.hisp.dhis.reportsheet;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.UserAuthorityGroup;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public abstract class ExportReport
    extends BaseIdentifiableObject
{
    private Integer periodRow;

    private Integer periodColumn;

    private Integer organisationRow;

    private Integer organisationColumn;

    private Set<ExportItem> exportItems;

    private Set<OrganisationUnit> organisationAssocitions;

    private Set<DataSet> dataSets;

    private Collection<UserAuthorityGroup> userRoles;

    private String group;

    private String excelTemplateFile;

    private String createdBy = "[DHIS-System]";

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ExportReport()
    {
        this.exportItems = new HashSet<ExportItem>();
        this.organisationAssocitions = new HashSet<OrganisationUnit>();
    }

    public ExportReport( String name, String group, String excelTemplateFile )
    {
        this.name = name;
        this.group = group;
        this.excelTemplateFile = excelTemplateFile;
        this.exportItems = new HashSet<ExportItem>();
        this.organisationAssocitions = new HashSet<OrganisationUnit>();
    }

    public ExportReport( String name, String group, String excelTemplateFile, String createdBy )
    {
        this.name = name;
        this.group = group;
        this.excelTemplateFile = excelTemplateFile;
        this.createdBy = (createdBy == null || createdBy.trim().isEmpty()) ? this.createdBy : createdBy;
        this.exportItems = new HashSet<ExportItem>();
        this.organisationAssocitions = new HashSet<OrganisationUnit>();
    }

    public Collection<ExportItem> getExportItemBySheet( Integer sheetNo )
    {
        Set<ExportItem> results = new HashSet<ExportItem>();

        for ( ExportItem exportItem : this.exportItems )
        {
            if ( exportItem.getSheetNo() == sheetNo )
            {
                results.add( exportItem );
            }
        }

        return results;
    }

    public boolean isAttribute()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.ATTRIBUTE );
    }

    public boolean isCategory()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.CATEGORY );
    }

    public boolean isCategoryVertical()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.CATEGORY_VERTICAL );
    }

    public boolean isNormal()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.NORMAL );
    }

    public boolean isOrgUnitGroupListing()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.ORGANIZATION_GROUP_LISTING );
    }

    public boolean isPeriodColumnListing()
    {
        return this.getReportType().equalsIgnoreCase( TYPE.PERIOD_COLUMN_LISTING );
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    public abstract String getReportType();

    public abstract List<String> getItemTypes();

    // -------------------------------------------------------------------------
    // Internal classes
    // -------------------------------------------------------------------------

    public static class TYPE
    {
        public static final String NORMAL = "NORMAL";

        public static final String CATEGORY = "CATEGORY";

        public static final String CATEGORY_VERTICAL = "CATEGORY_VERTICAL";

        public static final String ATTRIBUTE = "ATTRIBUTE";

        public static final String PERIOD_COLUMN_LISTING = "PERIOD_COLUMN_LISTING";

        public static final String ORGANIZATION_GROUP_LISTING = "ORGANIZATION_GROUP_LISTING";
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ExportReport other = (ExportReport) obj;
        if ( !name.equals( other.name ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Support method
    // -------------------------------------------------------------------------

    public boolean exportItemIsExist( String name, int sheetNo )
    {
        return getExportItemByName( name, sheetNo ) != null;
    }

    public boolean rowAndColumnIsExist( int sheet, int row, int column )
    {
        return getExportItemBySheetRowColumn( sheet, row, column ) != null;
    }

    public ExportItem getExportItemByName( String name, int sheetNo )
    {
        for ( ExportItem exportItem : this.exportItems )
        {
            if ( exportItem.getName().equalsIgnoreCase( name ) && exportItem.getSheetNo() == sheetNo )
            {
                return exportItem;
            }
        }

        return null;
    }

    public ExportItem getExportItemBySheetRowColumn( int sheet, int row, int column )
    {
        for ( ExportItem e : this.exportItems )
        {
            if ( e.getSheetNo() == sheet && e.getRow() == row && e.getColumn() == column )
            {
                return e;
            }
        }

        return null;
    }

    public Collection<ExportItem> getExportItemsByItemType( String... types )
    {
        List<ExportItem> items = new ArrayList<ExportItem>();

        for ( ExportItem e : this.exportItems )
        {
            for ( String type : types )
            {
                if ( e.getItemType().equalsIgnoreCase( type ) )
                {
                    items.add( e );
                }
            }
        }

        return items;
    }

    public void updateDataSetMembers( Set<DataSet> dataSetList )
    {
        for ( DataSet ds : new HashSet<DataSet>( dataSets ) )
        {
            if ( !dataSetList.contains( ds ) )
            {
                dataSets.remove( ds );
            }
        }

        dataSets.addAll( dataSetList );
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

    public Integer getPeriodRow()
    {
        return periodRow;
    }

    public void setPeriodRow( Integer periodRow )
    {
        this.periodRow = periodRow;
    }

    public Integer getPeriodColumn()
    {
        return periodColumn;
    }

    public void setPeriodColumn( Integer periodColumn )
    {
        this.periodColumn = periodColumn;
    }

    public Integer getOrganisationRow()
    {
        return organisationRow;
    }

    public void setOrganisationRow( Integer organisationRow )
    {
        this.organisationRow = organisationRow;
    }

    public Integer getOrganisationColumn()
    {
        return organisationColumn;
    }

    public void setOrganisationColumn( Integer organisationColumn )
    {
        this.organisationColumn = organisationColumn;
    }

    public Set<ExportItem> getExportItems()
    {
        return exportItems;
    }

    public void setExportItems( Set<ExportItem> exportItems )
    {
        this.exportItems = exportItems;
    }

    public Set<OrganisationUnit> getOrganisationAssocitions()
    {
        return organisationAssocitions;
    }

    public void setOrganisationAssocitions( Set<OrganisationUnit> organisationAssocitions )
    {
        this.organisationAssocitions = organisationAssocitions;
    }

    public Set<DataSet> getDataSets()
    {
        return dataSets == null ? new HashSet<DataSet>() : dataSets;
    }

    public void setDataSets( Set<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    public Collection<UserAuthorityGroup> getUserRoles()
    {
        return userRoles;
    }

    public void setUserRoles( Collection<UserAuthorityGroup> userRoles )
    {
        this.userRoles = userRoles;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public String getExcelTemplateFile()
    {
        return excelTemplateFile;
    }

    public void setExcelTemplateFile( String excelTemplateFile )
    {
        this.excelTemplateFile = excelTemplateFile;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy( String createdBy )
    {
        this.createdBy = (createdBy == null || createdBy.trim().isEmpty()) ? this.createdBy : createdBy;
    }
}
