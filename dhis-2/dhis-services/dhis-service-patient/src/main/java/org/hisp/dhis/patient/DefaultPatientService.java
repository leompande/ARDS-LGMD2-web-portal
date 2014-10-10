package org.hisp.dhis.patient;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.validation.ValidationCriteria;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultPatientService
    implements PatientService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientStore patientStore;

    public void setPatientStore( PatientStore patientStore )
    {
        this.patientStore = patientStore;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private RelationshipService relationshipService;

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    private RelationshipTypeService relationshipTypeService;

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public int savePatient( Patient patient )
    {
        return patientStore.save( patient );
    }

    @Override
    public int createPatient( Patient patient, Integer representativeId, Integer relationshipTypeId,
        Set<PatientAttributeValue> patientAttributeValues )
    {
        int id = savePatient( patient );

        for ( PatientAttributeValue pav : patientAttributeValues )
        {
            patientAttributeValueService.savePatientAttributeValue( pav );
            patient.getAttributeValues().add( pav );
        }

        // ---------------------------------------------------------------------
        // If under age, save representative information
        // ---------------------------------------------------------------------

        if ( representativeId != null )
        {
            Patient representative = patientStore.get( representativeId );
            if ( representative != null )
            {
                patient.setRepresentative( representative );

                Relationship rel = new Relationship();
                rel.setPatientA( representative );
                rel.setPatientB( patient );

                if ( relationshipTypeId != null )
                {
                    RelationshipType relType = relationshipTypeService.getRelationshipType( relationshipTypeId );
                    if ( relType != null )
                    {
                        rel.setRelationshipType( relType );
                        relationshipService.saveRelationship( rel );
                    }
                }
            }
        }

        updatePatient( patient ); // Save patient to update associations

        return id;
    }

    @Override
    public void updatePatient( Patient patient )
    {
        patientStore.update( patient );
    }

    @Override
    public void deletePatient( Patient patient )
    {
        patientStore.delete( patient );
    }

    @Override
    public Patient getPatient( int id )
    {
        return patientStore.get( id );
    }

    @Override
    public Patient getPatient( String uid )
    {
        return patientStore.getByUid( uid );
    }

    @Override
    public Collection<Patient> getAllPatients()
    {
        return patientStore.getAll();
    }

    @Override
    public Collection<Patient> getPatientsForMobile( String searchText, int orgUnitId )
    {
        Set<Patient> patients = new HashSet<Patient>();
        patients.addAll( patientIdentifierService.getPatientsByIdentifier( searchText, 0, Integer.MAX_VALUE ) );
        patients.addAll( getPatientsByPhone( searchText, 0, Integer.MAX_VALUE ) );

        // if an org-unit has been selected, filter out every patient that has a
        // different org-unit
        if ( orgUnitId != 0 )
        {
            Set<Patient> toRemoveList = new HashSet<Patient>();

            for ( Patient patient : patients )
            {
                if ( patient.getOrganisationUnit().getId() != orgUnitId )
                {
                    toRemoveList.add( patient );
                }
            }

            patients.removeAll( toRemoveList );
        }

        return patients;
    }

    @Override
    public Collection<Patient> getPatients( OrganisationUnit organisationUnit, Integer min, Integer max )
    {
        return patientStore.getByOrgUnit( organisationUnit, min, max );
    }

    @Override
    public Collection<Patient> getPatients( Program program )
    {
        return patientStore.getByProgram( program, 0, Integer.MAX_VALUE );
    }

    @Override
    public Collection<Patient> getPatients( OrganisationUnit organisationUnit, Program program )
    {
        return patientStore.getByOrgUnitProgram( organisationUnit, program, 0, Integer.MAX_VALUE );
    }

    @Override
    public Collection<Patient> getPatient( Integer identifierTypeId, Integer attributeId, String value )
    {
        if ( attributeId != null )
        {
            PatientAttribute attribute = patientAttributeService.getPatientAttribute( attributeId );
            if ( attribute != null )
            {
                return patientAttributeValueService.getPatient( attribute, value );
            }
        }
        else if ( identifierTypeId != null )
        {
            PatientIdentifierType identifierType = patientIdentifierTypeService
                .getPatientIdentifierType( identifierTypeId );

            if ( identifierType != null )
            {
                Patient p = patientIdentifierService.getPatient( identifierType, value );
                if ( p != null )
                {
                    Set<Patient> set = new HashSet<Patient>();
                    set.add( p );
                    return set;
                }
            }
        }

        return null;
    }

    @Override
    public Collection<Patient> sortPatientsByAttribute( Collection<Patient> patients, PatientAttribute patientAttribute )
    {
        Collection<Patient> sortedPatients = new ArrayList<Patient>();

        // ---------------------------------------------------------------------
        // Better to fetch all attribute values at once than fetching the
        // required attribute value of each patient using loop
        // ---------------------------------------------------------------------

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patients );

        if ( patientAttributeValues != null )
        {
            for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
            {
                if ( patientAttribute == patientAttributeValue.getPatientAttribute() )
                {
                    sortedPatients.add( patientAttributeValue.getPatient() );
                    patients.remove( patientAttributeValue.getPatient() );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Make sure all patients are in the sorted list - because all
        // patients might not have the sorting attribute/value
        // ---------------------------------------------------------------------

        sortedPatients.addAll( patients );

        return sortedPatients;
    }

    @Override
    public int countGetPatientsByOrgUnit( OrganisationUnit organisationUnit )
    {
        return patientStore.countListPatientByOrgunit( organisationUnit );
    }

    @Override
    public void updatePatient( Patient patient, Integer representativeId, Integer relationshipTypeId,
        List<PatientAttributeValue> valuesForSave, List<PatientAttributeValue> valuesForUpdate,
        Collection<PatientAttributeValue> valuesForDelete )
    {
        patientStore.update( patient );

        for ( PatientAttributeValue av : valuesForSave )
        {
            patientAttributeValueService.savePatientAttributeValue( av );
        }

        for ( PatientAttributeValue av : valuesForUpdate )
        {
            patientAttributeValueService.updatePatientAttributeValue( av );
        }

        for ( PatientAttributeValue av : valuesForDelete )
        {
            patientAttributeValueService.deletePatientAttributeValue( av );
        }

        if ( shouldSaveRepresentativeInformation( patient, representativeId ) )
        {
            Patient representative = patientStore.get( representativeId );

            if ( representative != null )
            {
                patient.setRepresentative( representative );

                Relationship rel = new Relationship();
                rel.setPatientA( representative );
                rel.setPatientB( patient );

                if ( relationshipTypeId != null )
                {
                    RelationshipType relType = relationshipTypeService.getRelationshipType( relationshipTypeId );
                    if ( relType != null )
                    {
                        rel.setRelationshipType( relType );
                        relationshipService.saveRelationship( rel );
                    }
                }
            }
        }
    }

    private boolean shouldSaveRepresentativeInformation( Patient patient, Integer representativeId )
    {
        if ( representativeId == null )
        {
            return false;
        }

        return patient.getRepresentative() == null || !(patient.getRepresentative().getId() == representativeId);
    }

    @Override
    public Collection<Patient> getPatients( OrganisationUnit organisationUnit, Program program, Integer min, Integer max )
    {
        return patientStore.getByOrgUnitProgram( organisationUnit, program, min, max );
    }

    @Override
    public int countGetPatientsByOrgUnitProgram( OrganisationUnit organisationUnit, Program program )
    {
        return patientStore.countGetPatientsByOrgUnitProgram( organisationUnit, program );
    }

    @Override
    public Object getObjectValue( String property, String value, I18nFormat format )
    {
        try
        {
            Type type = Patient.class.getMethod( "get" + StringUtils.capitalize( property ) ).getReturnType();

            if ( type == Integer.class || type == Integer.TYPE )
            {
                return Integer.valueOf( value );
            }
            else if ( type.equals( Boolean.class ) || type == Boolean.TYPE )
            {
                return Boolean.valueOf( value );
            }
            else if ( type.equals( Date.class ) )
            {
                return format.parseDate( value.trim() );
            }
            else if ( type.equals( Character.class ) || type == Character.TYPE )
            {
                return Character.valueOf( value.charAt( 0 ) );
            }

            return value;
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<Patient> getRepresentatives( Patient patient )
    {
        return patientStore.getRepresentatives( patient );
    }

    @Override
    public Collection<Patient> searchPatients( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max )
    {
        return patientStore.search( searchKeys, orgunits, followup, patientAttributes, identifierTypes,
            statusEnrollment, min, max );
    }

    @Override
    public int countSearchPatients( List<String> searchKeys, Collection<OrganisationUnit> orgunits, Boolean followup,
        Integer statusEnrollment )
    {
        return patientStore.countSearch( searchKeys, orgunits, followup, statusEnrollment );
    }

    @Override
    public Collection<String> getPatientPhoneNumbers( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max )
    {
        Collection<Patient> patients = patientStore.search( searchKeys, orgunits, followup, null, null,
            statusEnrollment, min, max );
        Set<String> phoneNumbers = new HashSet<String>();

        for ( Patient patient : patients )
        {
            Collection<PatientAttributeValue> attributeValues = patient.getAttributeValues();
            if ( attributeValues != null )
            {
                for ( PatientAttributeValue attributeValue : attributeValues )
                {
                    if ( attributeValue.getPatientAttribute().getValueType()
                        .equals( PatientAttribute.TYPE_PHONE_NUMBER ) )
                    {
                        phoneNumbers.add( attributeValue.getValue() );
                    }
                }
            }
        }

        return phoneNumbers;
    }

    @Override
    public List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max )
    {
        return patientStore.getProgramStageInstances( searchKeys, orgunits, followup, null, null, statusEnrollment,
            min, max );
    }

    @Override
    public Collection<Patient> getPatientsByPhone( String phoneNumber, Integer min, Integer max )
    {
        return patientStore.getByPhoneNumber( phoneNumber, min, max );
    }

    @Override
    public Grid getScheduledEventsReport( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max, I18n i18n )
    {
        String startDate = "";
        String endDate = "";
        for ( String searchKey : searchKeys )
        {
            String[] keys = searchKey.split( "_" );
            if ( keys[0].equals( Patient.PREFIX_PROGRAM_EVENT_BY_STATUS ) )
            {
                startDate = keys[2];
                endDate = keys[3];
            }
        }

        Grid grid = new ListGrid();
        grid.setTitle( i18n.getString( "activity_plan" ) );
        if ( !startDate.isEmpty() && !endDate.isEmpty() )
        {
            grid.setSubtitle( i18n.getString( "from" ) + " " + startDate + " " + i18n.getString( "to" ) + " " + endDate );
        }

        grid.addHeader( new GridHeader( "patientid", true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "first_name" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "middle_name" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "last_name" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "gender" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "phone_number" ), false, true ) );

        Collection<PatientAttribute> patientAttributes = patientAttributeService
            .getPatientAttributesByDisplayOnVisitSchedule( true );
        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            grid.addHeader( new GridHeader( patientAttribute.getDisplayName(), false, true ) );
        }

        grid.addHeader( new GridHeader( "programstageinstanceid", true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "program_stage" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "due_date" ), false, true ) );

        return patientStore.getPatientEventReport( grid, searchKeys, orgunits, followup, patientAttributes, null,
            statusEnrollment, min, max );
    }

    @Override
    public Grid getTrackingEventsReport( Program program, List<String> searchKeys,
        Collection<OrganisationUnit> orgunits, Boolean followup, Integer statusEnrollment, I18n i18n )
    {
        String startDate = "";
        String endDate = "";
        for ( String searchKey : searchKeys )
        {
            String[] keys = searchKey.split( "_" );
            if ( keys[0].equals( Patient.PREFIX_PROGRAM_EVENT_BY_STATUS ) )
            {
                startDate = keys[2];
                endDate = keys[3];
            }
        }

        Grid grid = new ListGrid();
        grid.setTitle( i18n.getString( "program_tracking" ) );
        if ( !startDate.isEmpty() && !endDate.isEmpty() )
        {
            grid.setSubtitle( i18n.getString( "from" ) + " " + startDate + " " + i18n.getString( "to" ) + " " + endDate );
        }

        grid.addHeader( new GridHeader( "patientid", true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "first_name" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "middle_name" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "last_name" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "gender" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "phone_number" ), false, true ) );

        Collection<PatientIdentifierType> patientIdentifierTypes = program.getIdentifierTypes();

        for ( PatientIdentifierType patientIdentifierType : patientIdentifierTypes )
        {
            grid.addHeader( new GridHeader( patientIdentifierType.getDisplayName(), false, true ) );
        }
        grid.addHeader( new GridHeader( "programstageinstanceid", true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "program_stage" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "due_date" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "risk" ), false, true ) );

        return patientStore.getPatientEventReport( grid, searchKeys, orgunits, followup, null, patientIdentifierTypes,
            statusEnrollment, null, null );
    }

    @Override
    public int validatePatient( Patient patient, Program program, I18nFormat format )
    {
        return patientStore.validate( patient, program, format );
    }

    public ValidationCriteria validateEnrollment( Patient patient, Program program, I18nFormat format )
    {
        return patientStore.validateEnrollment( patient, program, format );
    }
   
        
    @Override
    public Collection<Patient> searchPatientsForMobile( String searchText,

    int orgUnitId, int patientAttributeId )
    {

        Set<Patient> patients = new HashSet<Patient>();

        patients.addAll( getPatientsByAttributeValue( searchText,

        patientAttributeId, 0, Integer.MAX_VALUE ) );

        // if an org-unit has been selected, filter out every patient that has a

        // different org-unit

        if ( orgUnitId != 0 )
        {

            Set<Patient> toRemoveList = new HashSet<Patient>();

            for ( Patient patient : patients )
            {

                if ( patient.getOrganisationUnit().getId() != orgUnitId )
                {
                    toRemoveList.add( patient );
                }
            }
            patients.removeAll( toRemoveList );
        }
        return patients;
    }

    @Override
    public Collection<Patient> getPatientsByAttributeValue( String searchText, int patientAttributeId, Integer min,
        Integer max )
    {
        return patientStore.getByPatientAttributeValue( searchText, patientAttributeId, min, max );
    }

}
