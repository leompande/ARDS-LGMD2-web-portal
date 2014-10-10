package org.hisp.dhis.dxf2.events.person;

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

import com.google.common.collect.Sets;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.system.util.TextUtils.nullIfEmpty;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractPersonService
    implements PersonService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientIdentifierService patientIdentifierService;

    @Autowired
    private PatientIdentifierTypeService patientIdentifierTypeService;

    @Autowired
    private PatientAttributeValueService patientAttributeValueService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private IdentifiableObjectManager manager;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public Persons getPersons()
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getAllPatients() );
        return getPersons( patients );
    }

    @Override
    public Person getPerson( Identifier identifier )
    {
        PatientIdentifierType patientIdentifierType = patientIdentifierTypeService
            .getPatientIdentifierTypeByUid( identifier.getIdentifier() );
        Patient patient = patientIdentifierService.getPatient( patientIdentifierType, identifier.getValue() );
        return getPerson( patient );
    }

    @Override
    public Persons getPersons( OrganisationUnit organisationUnit )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, null, null ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( Program program )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatients( program ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( OrganisationUnit organisationUnit, Program program )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, program ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( Collection<Patient> patients )
    {
        Persons persons = new Persons();

        for ( Patient patient : patients )
        {
            persons.getPersons().add( getPerson( patient ) );
        }

        return persons;
    }

    @Override
    public Person getPerson( String uid )
    {
        return getPerson( patientService.getPatient( uid ) );
    }

    @Override
    public Person getPerson( Patient patient )
    {
        if ( patient == null )
        {
            return null;
        }

        Person person = new Person();
        person.setPerson( patient.getUid() );
        person.setOrgUnit( patient.getOrganisationUnit().getUid() );

        Collection<Relationship> relationshipsForPatient = relationshipService.getRelationshipsForPatient( patient );

        for ( Relationship relationshipPatient : relationshipsForPatient )
        {
            org.hisp.dhis.dxf2.events.person.Relationship relationship = new org.hisp.dhis.dxf2.events.person.Relationship();
            relationship.setDisplayName( relationshipPatient.getRelationshipType().getDisplayName() );
            relationship.setPerson( relationshipPatient.getPatientA().getUid() );
            relationship.setType( relationshipPatient.getRelationshipType().getUid() );

            person.getRelationships().add( relationship );
        }

        for ( PatientIdentifier patientIdentifier : patient.getIdentifiers() )
        {
            String identifierId = patientIdentifier.getIdentifierType() == null ? null : patientIdentifier
                .getIdentifierType().getUid();

            String identifierType = patientIdentifier.getIdentifierType() == null ? "system" : patientIdentifier.getIdentifierType().getType();

            String displayName = patientIdentifier.getIdentifierType() != null ? patientIdentifier.getIdentifierType()
                .getDisplayName() : null;

            Identifier identifier = new Identifier( identifierId, identifierType, patientIdentifier.getIdentifier() );
            identifier.setDisplayName( displayName );
            person.getIdentifiers().add( identifier );
        }

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            Attribute attribute = new Attribute();
            attribute.setDisplayName( patientAttributeValue.getPatientAttribute().getDisplayName() );
            attribute.setAttribute( patientAttributeValue.getPatientAttribute().getUid() );
            attribute.setType( patientAttributeValue.getPatientAttribute().getValueType() );
            attribute.setValue( patientAttributeValue.getValue() );

            person.getAttributes().add( attribute );
        }

        return person;
    }

    public Patient getPatient( Person person )
    {
        Assert.hasText( person.getOrgUnit() );

        Patient patient = new Patient();

        OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, person.getOrgUnit() );
        Assert.notNull( organisationUnit );

        patient.setOrganisationUnit( organisationUnit );

        updateIdentifiers( person, patient );

        return patient;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary savePerson( Person person )
    {
        ImportSummary importSummary = new ImportSummary();
        importSummary.setDataValueCount( null );

        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        importConflicts.addAll( checkIdentifiers( person ) );
        importConflicts.addAll( checkAttributes( person ) );

        importSummary.setConflicts( importConflicts );

        if ( !importConflicts.isEmpty() )
        {
            importSummary.setStatus( ImportStatus.ERROR );
            importSummary.getImportCount().incrementIgnored();
            return importSummary;
        }

        updateSystemIdentifier( person );

        Patient patient = getPatient( person );
        patientService.savePatient( patient );

        for ( PatientIdentifier patientIdentifier : patient.getIdentifiers() )
        {
            patientIdentifierService.savePatientIdentifier( patientIdentifier );
        }

        updateAttributeValues( person, patient );
        patientService.updatePatient( patient );

        importSummary.setStatus( ImportStatus.SUCCESS );
        importSummary.setReference( patient.getUid() );
        importSummary.getImportCount().incrementImported();

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary updatePerson( Person person )
    {
        ImportSummary importSummary = new ImportSummary();
        importSummary.setDataValueCount( null );

        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        importConflicts.addAll( checkRelationships( person ) );
        importConflicts.addAll( checkIdentifiers( person ) );
        importConflicts.addAll( checkAttributes( person ) );

        Patient patient = manager.get( Patient.class, person.getPerson() );

        if ( patient == null )
        {
            importConflicts.add( new ImportConflict( "Person", "person " + person.getPerson()
                + " does not point to valid person" ) );
        }

        OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, person.getOrgUnit() );

        if ( organisationUnit == null )
        {
            importConflicts.add( new ImportConflict( "OrganisationUnit", "orgUnit " + person.getOrgUnit()
                + " does not point to valid organisation unit" ) );
        }

        importSummary.setConflicts( importConflicts );

        if ( !importConflicts.isEmpty() )
        {
            importSummary.setStatus( ImportStatus.ERROR );
            importSummary.getImportCount().incrementIgnored();
            return importSummary;
        }

        updateSystemIdentifier( person );
        removeRelationships( patient );
        removeIdentifiers( patient );
        removeAttributeValues( patient );
        patientService.updatePatient( patient );

        updateRelationships( person, patient );
        updateIdentifiers( person, patient );
        updateAttributeValues( person, patient );

        for ( PatientIdentifier patientIdentifier : patient.getIdentifiers() )
        {
            patientIdentifierService.savePatientIdentifier( patientIdentifier );
        }

        patientService.updatePatient( patient );

        importSummary.setStatus( ImportStatus.SUCCESS );
        importSummary.setReference( patient.getUid() );
        importSummary.getImportCount().incrementUpdated();

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deletePerson( Person person )
    {
        Patient patient = patientService.getPatient( person.getPerson() );

        if ( patient != null )
        {
            patientService.deletePatient( patient );
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private List<ImportConflict> checkIdentifiers( Person person )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        Collection<PatientIdentifierType> patientIdentifierTypes = manager.getAll( PatientIdentifierType.class );
        Map<String, String> cacheMap = new HashMap<String, String>();

        Patient patient = manager.get( Patient.class, person.getPerson() );

        Collection<PatientIdentifierType> programIdentifierTypes = Sets.newHashSet();

        for ( Program program : manager.getAll( Program.class ) )
        {
            programIdentifierTypes.addAll( program.getIdentifierTypes() );
        }

        for ( Identifier identifier : person.getIdentifiers() )
        {
            if ( identifier.getValue() != null )
            {
                cacheMap.put( identifier.getIdentifier(), identifier.getValue() );
            }
        }

        for ( PatientIdentifierType patientIdentifierType : patientIdentifierTypes )
        {
            if ( patientIdentifierType.isMandatory() && !patientIdentifierTypes.contains( patientIdentifierType ) )
            {
                if ( !cacheMap.keySet().contains( patientIdentifierType.getUid() ) )
                {
                    importConflicts.add( new ImportConflict( "Identifier.type", "Missing required identifier type "
                        + patientIdentifierType.getUid() ) );
                }
            }

            List<PatientIdentifier> patientIdentifiers = new ArrayList<PatientIdentifier>(
                patientIdentifierService.getAll( patientIdentifierType, cacheMap.get( patientIdentifierType.getUid() ) ) );

            if ( !patientIdentifiers.isEmpty() )
            {
                // if .size() > 1, there is something wrong with the db.. but we
                // for-loop for now
                for ( PatientIdentifier patientIdentifier : patientIdentifiers )
                {
                    if ( !patientIdentifier.getPatient().equals( patient ) )
                    {
                        importConflicts.add( new ImportConflict( "Identifier.value",
                            "Value already exists for patient " + patientIdentifier.getPatient().getUid()
                                + " with identifier type " + patientIdentifierType.getUid() ) );
                    }
                }
            }
        }

        for ( Identifier identifier : person.getIdentifiers() )
        {
            if ( identifier.getIdentifier() == null )
            {
                // if identifier is null, assume system identifier
                continue;
            }

            PatientIdentifierType patientIdentifierType = manager.get( PatientIdentifierType.class,
                identifier.getIdentifier() );

            if ( patientIdentifierType == null )
            {
                importConflicts.add( new ImportConflict( "Identifier.identifier", "Invalid identifier " + identifier.getIdentifier() ) );
            }
        }

        return importConflicts;
    }

    private List<ImportConflict> checkAttributes( Person person )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        Collection<PatientAttribute> patientAttributes = manager.getAll( PatientAttribute.class );
        Set<String> cache = new HashSet<String>();

        for ( Attribute attribute : person.getAttributes() )
        {
            if ( attribute.getValue() != null )
            {
                cache.add( attribute.getAttribute() );
            }
        }

        Collection<PatientAttribute> programAttributes = Sets.newHashSet();

        for ( Program program : manager.getAll( Program.class ) )
        {
            programAttributes.addAll( program.getAttributes() );
        }

        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            if ( patientAttribute.isMandatory() && !programAttributes.contains( patientAttribute ) )
            {
                if ( !cache.contains( patientAttribute.getUid() ) )
                {
                    importConflicts.add( new ImportConflict( "Attribute.type", "Missing required attribute type "
                        + patientAttribute.getUid() ) );
                }
            }
        }

        for ( Attribute attribute : person.getAttributes() )
        {
            PatientAttribute patientAttribute = manager.get( PatientAttribute.class, attribute.getAttribute() );

            if ( patientAttribute == null )
            {
                importConflicts.add( new ImportConflict( "Attribute.type", "Invalid type " + attribute.getAttribute() ) );
            }
        }

        return importConflicts;
    }

    private List<ImportConflict> checkRelationships( Person person )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        for ( org.hisp.dhis.dxf2.events.person.Relationship relationship : person.getRelationships() )
        {
            RelationshipType relationshipType = manager.get( RelationshipType.class, relationship.getType() );

            if ( relationshipType == null )
            {
                importConflicts
                    .add( new ImportConflict( "Relationship.type", "Invalid type " + relationship.getType() ) );
            }

            Patient patient = manager.get( Patient.class, relationship.getPerson() );

            if ( patient == null )
            {
                importConflicts.add( new ImportConflict( "Relationship.person", "Invalid person "
                    + relationship.getPerson() ) );
            }
        }

        return importConflicts;
    }

    private void updateAttributeValues( Person person, Patient patient )
    {
        for ( Attribute attribute : person.getAttributes() )
        {
            PatientAttribute patientAttribute = manager.get( PatientAttribute.class, attribute.getAttribute() );

            if ( patientAttribute != null )
            {
                PatientAttributeValue patientAttributeValue = new PatientAttributeValue();
                patientAttributeValue.setPatient( patient );
                patientAttributeValue.setValue( attribute.getValue() );
                patientAttributeValue.setPatientAttribute( patientAttribute );

                patientAttributeValueService.savePatientAttributeValue( patientAttributeValue );
            }
        }
    }

    private void updateSystemIdentifier( Person person )
    {
        Iterator<Identifier> iterator = person.getIdentifiers().iterator();

        // remove any old system identifiers
        while ( iterator.hasNext() )
        {
            Identifier identifier = iterator.next();

            if ( identifier.getIdentifier() == null )
            {
                iterator.remove();
            }
        }
        Identifier identifier = null;
        if ( person.getPerson() != null )
        {
            Patient patient = manager.get( Patient.class, person.getPerson() );

            for ( PatientIdentifier patientIdentifier : patient.getIdentifiers() )
            {
                if ( patientIdentifier.getIdentifierType() == null && patientIdentifier.getIdentifier() != null )
                {
                    identifier = new Identifier( patientIdentifier.getIdentifier() );
                    break;
                }
            }
        }

        if ( identifier != null )
        {
            person.getIdentifiers().add( identifier );
        }
    }

    // add identifiers from person => patient
    private void updateIdentifiers( Person person, Patient patient )
    {
        for ( Identifier identifier : person.getIdentifiers() )
        {
            if ( identifier.getIdentifier() == null )
            {
                PatientIdentifier patientIdentifier = new PatientIdentifier();
                patientIdentifier.setIdentifier( identifier.getValue().trim() );
                patientIdentifier.setIdentifierType( null );
                patientIdentifier.setPatient( patient );

                patient.getIdentifiers().add( patientIdentifier );

                continue;
            }

            PatientIdentifierType type = manager.get( PatientIdentifierType.class, identifier.getIdentifier() );

            if ( type != null && nullIfEmpty( identifier.getValue() ) != null )
            {
                PatientIdentifier patientIdentifier = new PatientIdentifier();
                patientIdentifier.setIdentifier( identifier.getValue().trim() );
                patientIdentifier.setIdentifierType( type );
                patientIdentifier.setPatient( patient );

                patient.getIdentifiers().add( patientIdentifier );
            }
        }
    }

    private void updateRelationships( Person person, Patient patient )
    {
        for ( org.hisp.dhis.dxf2.events.person.Relationship relationship : person.getRelationships() )
        {
            Patient patientB = manager.get( Patient.class, relationship.getPerson() );
            RelationshipType relationshipType = manager.get( RelationshipType.class, relationship.getType() );

            Relationship relationshipPatient = new Relationship();
            relationshipPatient.setPatientA( patient );
            relationshipPatient.setPatientB( patientB );
            relationshipPatient.setRelationshipType( relationshipType );

            relationshipService.saveRelationship( relationshipPatient );
        }
    }

    private void removeRelationships( Patient patient )
    {
        Collection<Relationship> relationshipsForPatient = relationshipService.getRelationshipsForPatient( patient );

        for ( Relationship relationship : relationshipsForPatient )
        {
            relationshipService.deleteRelationship( relationship );
        }
    }

    private void removeIdentifiers( Patient patient )
    {
        for ( PatientIdentifier patientIdentifier : patient.getIdentifiers() )
        {
            patientIdentifierService.deletePatientIdentifier( patientIdentifier );
        }

        patient.setIdentifiers( new HashSet<PatientIdentifier>() );
        patientService.updatePatient( patient );
    }

    private void removeAttributeValues( Patient patient )
    {
        for ( PatientAttributeValue patientAttributeValue : patient.getAttributeValues() )
        {
            patientAttributeValueService.deletePatientAttributeValue( patientAttributeValue );
        }

        patientService.updatePatient( patient );
    }
}
