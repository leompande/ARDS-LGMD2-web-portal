package org.hisp.dhis.reportsheet.organisationunit.action;

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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunitprototype.OrganisationUnitPrototype;
import org.hisp.dhis.organisationunitprototype.OrganisationUnitPrototypeService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.system.util.AttributeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class AddMultiOrganisationUnitAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    @Autowired
    private OrganisationUnitPrototypeService organisationUnitPrototypeService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Autowired
    private OrganisationUnitSelectionManager selectionManager;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private AttributeService attributeService;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String openingDate;

    public void setOpeningDate( String openingDate )
    {
        this.openingDate = openingDate;
    }

    private boolean active;

    public void setActive( boolean active )
    {
        this.active = active;
    }

    private Collection<String> selectedList = new HashSet<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    private Collection<Integer> dataSets = new HashSet<Integer>();

    public void setDataSets( Collection<Integer> dataSets )
    {
        this.dataSets = dataSets;
    }

    private Collection<Integer> selectedGroups = new HashSet<Integer>();

    public void setSelectedGroups( Collection<Integer> selectedGroups )
    {
        this.selectedGroups = selectedGroups;
    }

    private Integer organisationUnitId;

    public Integer getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    private List<String> jsonAttributeValues;

    public void setJsonAttributeValues( List<String> jsonAttributeValues )
    {
        this.jsonAttributeValues = jsonAttributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Date date = format.parseDate( openingDate );

        // ---------------------------------------------------------------------
        // Get parent
        // ---------------------------------------------------------------------

        OrganisationUnit parent = selectionManager.getSelectedOrganisationUnit();

        if ( parent == null )
        {
            // -----------------------------------------------------------------
            // If no unit is selected, the parent is the parent of the roots
            // -----------------------------------------------------------------

            parent = selectionManager.getRootOrganisationUnitsParent();
        }

        Collection<DataSet> dataSetList = new HashSet<DataSet>( dataSetService.getDataSets( dataSets ) );

        Collection<OrganisationUnitGroup> unitGroupList = new HashSet<OrganisationUnitGroup>(
            organisationUnitGroupService.getOrganisationUnitGroups( selectedGroups ) );

        // ---------------------------------------------------------------------
        // Create organization unit
        // ---------------------------------------------------------------------

        for ( String id : selectedList )
        {
            OrganisationUnitPrototype unitPrototype = organisationUnitPrototypeService
                .getOrganisationUnitPrototype( Integer.parseInt( id ) );

            if ( unitPrototype != null )
            {
                OrganisationUnit organisationUnit = new OrganisationUnit( unitPrototype.getDisplayName(), unitPrototype.getDisplayShortName(), null, date, null, active, null );

                organisationUnit.setParent( parent );

                if ( parent != null )
                {
                    parent.getChildren().add( organisationUnit );
                }

                if ( jsonAttributeValues != null )
                {
                    AttributeUtils.updateAttributeValuesFromJson( organisationUnit.getAttributeValues(),
                        jsonAttributeValues, attributeService );
                }

                // ---------------------------------------------------------------------
                // Must persist org-unit before adding data sets because
                // association are
                // updated on both sides (and this side is inverse)
                // ---------------------------------------------------------------------

                organisationUnitId = organisationUnitService.addOrganisationUnit( organisationUnit );

                for ( DataSet ds : dataSetList )
                {
                    organisationUnit.addDataSet( ds );
                }

                for ( OrganisationUnitGroup group : unitGroupList )
                {
                    if ( group != null )
                    {
                        group.addOrganisationUnit( organisationUnit );
                        organisationUnitGroupService.updateOrganisationUnitGroup( group );
                    }
                }

                organisationUnitService.updateOrganisationUnit( organisationUnit );
            }
        }

        return SUCCESS;
    }
}
