package org.hisp.dhis.reportsheet.importitem;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reportsheet.DataElementGroupOrder;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class ImportReport
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = 1L;

    private String type;

    private Set<ImportItem> importItems;

    private Set<OrganisationUnit> organisationAssocitions;

    private List<OrganisationUnitGroup> organisationUnitGroups;

    private List<DataElementGroupOrder> dataElementOrders;

    private PeriodType periodType;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ImportReport()
    {
    }

    // -------------------------------------------------------------------------
    // Internal classes
    // -------------------------------------------------------------------------

    public static class TYPE
    {
        public static final String NORMAL = "NORMAL";

        public static final String CATEGORY = "CATEGORY";

        public static final String PERIOD_COLUMN_LISTING = "PERIOD_COLUMN_LISTING";

        public static final String ORGANIZATION_GROUP_LISTING = "ORGANIZATION_GROUP_LISTING";

        public static final List<String> getImportTypes()
        {
            List<String> list = new ArrayList<String>();

            list.add( NORMAL );
            list.add( CATEGORY );
            list.add( ORGANIZATION_GROUP_LISTING );

            return list;
        }
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
        ImportReport other = (ImportReport) obj;
        if ( !name.equals( other.name ) )
            return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public List<DataElementGroupOrder> getDataElementOrders()
    {
        return dataElementOrders;
    }

    public void setDataElementOrders( List<DataElementGroupOrder> dataElementOrders )
    {
        this.dataElementOrders = dataElementOrders;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( List<OrganisationUnitGroup> organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Set<ImportItem> getImportItems()
    {
        return importItems;
    }

    public void setImportItems( Set<ImportItem> importItems )
    {
        this.importItems = importItems;
    }

    public Set<OrganisationUnit> getOrganisationAssocitions()
    {
        return organisationAssocitions;
    }

    public void setOrganisationAssocitions( Set<OrganisationUnit> organisationAssocitions )
    {
        this.organisationAssocitions = organisationAssocitions;
    }

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    // -------------------------------------------------------------------------
    // getType
    // -------------------------------------------------------------------------

    public boolean isCategory()
    {
        return this.getType().equalsIgnoreCase( TYPE.CATEGORY );
    }

    public boolean isOrgUnitGroupListing()
    {
        return this.getType().equalsIgnoreCase( TYPE.ORGANIZATION_GROUP_LISTING );
    }

    public boolean isPeriodColumnListing()
    {
        return this.getType().equalsIgnoreCase( TYPE.PERIOD_COLUMN_LISTING );
    }

    public boolean isNormal()
    {
        return this.getType().equalsIgnoreCase( TYPE.NORMAL );
    }

    // -------------------------------------------------------------------------
    // Support method
    // -------------------------------------------------------------------------

    public boolean importItemIsExist( String name )
    {
        return getImportItemByName( name ) != null;
    }

    public boolean rowAndColumnIsExist( int sheet, int row, int column )
    {
        return getImportItemBySheetRowColumn( sheet, row, column ) != null;
    }

    public ImportItem getImportItemByName( String name )
    {
        for ( ImportItem e : this.importItems )
        {
            if ( e.getName().equals( name ) )
            {
                return e;
            }
        }

        return null;
    }

    public ImportItem getImportItemBySheetRowColumn( int sheet, int row, int column )
    {
        for ( ImportItem e : this.importItems )
        {
            if ( e.getSheetNo() == sheet && e.getRow() == row && e.getColumn() == column )
            {
                return e;
            }
        }

        return null;
    }

}
