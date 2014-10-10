package org.hisp.dhis.oum.action.organisationunit;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.system.util.AttributeUtils;
import org.hisp.dhis.system.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.hisp.dhis.system.util.TextUtils.nullIfEmpty;

/**
 * @author Torgeir Lorange Ostby
 */
public class AddOrganisationUnitAction
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private IdentifiableObjectManager manager;

    @Autowired
    public void setManager( IdentifiableObjectManager manager )
    {
        this.manager = manager;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

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

    private String comment;

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    private String longitude;

    public void setLongitude( String longitude )
    {
        this.longitude = longitude;
    }

    private String latitude;

    public void setLatitude( String latitude )
    {
        this.latitude = latitude;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }

    private String contactPerson;

    public void setContactPerson( String contactPerson )
    {
        this.contactPerson = contactPerson;
    }

    private String address;

    public void setAddress( String address )
    {
        this.address = address;
    }

    private String email;

    public void setEmail( String email )
    {
        this.email = email;
    }

    private String phoneNumber;

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    private Collection<String> dataSets = new HashSet<String>();

    public void setDataSets( Collection<String> dataSets )
    {
        this.dataSets = dataSets;
    }

    private Collection<String> selectedGroups = new HashSet<String>();

    public void setSelectedGroups( Collection<String> selectedGroups )
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
        code = nullIfEmpty( code );
        comment = nullIfEmpty( comment );
        description = nullIfEmpty( description );
        longitude = nullIfEmpty( longitude );
        latitude = nullIfEmpty( latitude );
        url = nullIfEmpty( url );

        contactPerson = nullIfEmpty( contactPerson );
        address = nullIfEmpty( address );
        email = nullIfEmpty( email );
        phoneNumber = nullIfEmpty( phoneNumber );

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

        // ---------------------------------------------------------------------
        // Create organisation unit
        // ---------------------------------------------------------------------

        OrganisationUnit organisationUnit = new OrganisationUnit( name, shortName, code, date, null, active, comment );

        organisationUnit.setDescription( description );
        organisationUnit.setUrl( url );
        organisationUnit.setParent( parent );
        organisationUnit.setContactPerson( contactPerson );
        organisationUnit.setAddress( address );
        organisationUnit.setEmail( email );
        organisationUnit.setPhoneNumber( phoneNumber );

        if ( parent != null )
        {
            parent.getChildren().add( organisationUnit );
        }

        if ( jsonAttributeValues != null )
        {
            AttributeUtils.updateAttributeValuesFromJson( organisationUnit.getAttributeValues(), jsonAttributeValues,
                attributeService );
        }

        // ---------------------------------------------------------------------
        // Set coordinates and feature type to point if valid
        // ---------------------------------------------------------------------

        if ( longitude != null && latitude != null )
        {
            String coordinates = ValidationUtils.getCoordinate( longitude, latitude );

            if ( ValidationUtils.coordinateIsValid( coordinates ) )
            {
                organisationUnit.setCoordinates( coordinates );
                organisationUnit.setFeatureType( OrganisationUnit.FEATURETYPE_POINT );
            }
        }

        // ---------------------------------------------------------------------
        // Must persist org unit before adding data sets because association are
        // updated on both sides (and this side is inverse)
        // ---------------------------------------------------------------------

        organisationUnitId = organisationUnitService.addOrganisationUnit( organisationUnit );

        for ( String id : dataSets )
        {
            organisationUnit.addDataSet( dataSetService.getDataSet( Integer.parseInt( id ) ) );
        }

        for ( String id : selectedGroups )
        {
            OrganisationUnitGroup group = organisationUnitGroupService
                .getOrganisationUnitGroup( Integer.parseInt( id ) );

            if ( group != null )
            {
                group.addOrganisationUnit( organisationUnit );
                manager.updateNoAcl( group );
            }
        }

        organisationUnitService.updateOrganisationUnit( organisationUnit );

        return SUCCESS;
    }
}
