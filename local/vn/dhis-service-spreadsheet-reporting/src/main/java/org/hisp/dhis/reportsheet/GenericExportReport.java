package org.hisp.dhis.reportsheet;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.UserAuthorityGroup;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

public abstract class GenericExportReport
    implements Serializable
{
    protected int id;

    protected String name;

    protected String excelTemplateFile;

    protected Set<? extends GenericItem> items;

    protected Set<OrganisationUnit> organisationAssocitions;

    protected Collection<UserAuthorityGroup> userRoles;

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    public abstract boolean isCategory();

    public abstract boolean isOrganisationUnitGroupListing();

    public abstract boolean isPeriodColumnListing();

    public abstract boolean isNormal();

    public abstract String getReportType();

    public abstract List<String> getItemTypes();

    // -------------------------------------------------------------------------
    // Internal classes
    // -------------------------------------------------------------------------

    public static class TYPE
    {
        public static final String NORMAL = "NORMAL";

        public static final String CATEGORY = "CATEGORY";

        public static final String PERIOD_COLUMN_LISTING = "PERIOD_COLUMN_LISTING";

        public static final String ORGANIZATION_GROUP_LISTING = "ORGANIZATION_GROUP_LISTING";
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @SuppressWarnings( "unchecked" )
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        GenericExportReport other = (GenericExportReport) obj;
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
    // Supporting methods
    // -------------------------------------------------------------------------

    public boolean itemIsExist( String name, int sheetNo )
    {
        return getItemByName( name, sheetNo ) != null;
    }

    public boolean rowAndColumnIsExist( int sheet, int row, int column )
    {
        return geItemBySheetRowColumn( sheet, row, column ) != null;
    }

    public GenericItem getItemByName( String name, int sheetNo )
    {
        for ( GenericItem item : this.items )
        {
            if ( item.getName().equalsIgnoreCase( name ) && item.getSheetNo() == sheetNo )
            {
                return item;
            }
        }

        return null;
    }

    public GenericItem geItemBySheetRowColumn( int sheet, int row, int column )
    {
        for ( GenericItem item : this.items )
        {
            if ( item.getSheetNo() == sheet && item.getRow() == row && item.getColumn() == column )
            {
                return item;
            }
        }

        return null;
    }

    public Collection<? extends GenericItem> getItemsBySheet( Integer sheetNo )
    {
        Set<GenericItem> results = new HashSet<GenericItem>();

        for ( GenericItem item : this.items )
        {
            if ( item.getSheetNo() == sheetNo )
            {
                results.add( item );
            }
        }

        return results;
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

    public String getExcelTemplateFile()
    {
        return excelTemplateFile;
    }

    public void setExcelTemplateFile( String excelTemplateFile )
    {
        this.excelTemplateFile = excelTemplateFile;
    }

    public Set<? extends GenericItem> getItems()
    {
        return items;
    }

    public void setItems( Set<? extends GenericItem> items )
    {
        this.items = items;
    }

    public Set<OrganisationUnit> getOrganisationAssocitions()
    {
        return organisationAssocitions;
    }

    public void setOrganisationAssocitions( Set<OrganisationUnit> organisationAssocitions )
    {
        this.organisationAssocitions = organisationAssocitions;
    }

    public Collection<UserAuthorityGroup> getUserRoles()
    {
        return userRoles;
    }

    public void setUserRoles( Collection<UserAuthorityGroup> userRoles )
    {
        this.userRoles = userRoles;
    }

}
