package org.hisp.dhis.api.controller.event;

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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.controller.exception.NotFoundException;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.events.person.Identifier;
import org.hisp.dhis.dxf2.events.person.Person;
import org.hisp.dhis.dxf2.events.person.PersonService;
import org.hisp.dhis.dxf2.events.person.Persons;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.program.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = PersonController.RESOURCE_PATH )
@PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_LIST')" )
public class PersonController
{
    public static final String RESOURCE_PATH = "/persons";

    @Autowired
    private PersonService personService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private SessionFactory sessionFactory;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.GET )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_ACCESS_PATIENT_ATTRIBUTES')" )
    public String getPersons( @RequestParam( value = "orgUnit", required = false ) String orgUnitUid,
        @RequestParam( value = "program", required = false ) String programUid,
        @RequestParam( value = "attribute", required = false ) List<String> attributeFilters,
        @RequestParam( required = false ) String identifierType,
        @RequestParam( required = false ) String identifier,
        @RequestParam Map<String, String> parameters, Model model )
        throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        Persons persons = new Persons();

        if ( attributeFilters != null )
        {
            persons = personsByFilter( attributeFilters, orgUnitUid );
        }
        else if ( identifier != null )
        {
            Identifier id = new Identifier( identifierType, identifier );
            persons.getPersons().add( personService.getPerson( id ) );
        }
        else if ( orgUnitUid != null )
        {
            if ( programUid != null )
            {
                OrganisationUnit organisationUnit = getOrganisationUnit( orgUnitUid );
                Program program = getProgram( programUid );

                persons = personService.getPersons( organisationUnit, program );
            }
            else
            {
                OrganisationUnit organisationUnit = getOrganisationUnit( orgUnitUid );
                persons = personService.getPersons( organisationUnit );
            }
        }
        else
        {
            throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, "Missing required orgUnit parameter." );
        }

        model.addAttribute( "model", persons );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return "persons";
    }

    @SuppressWarnings( "unchecked" )
    private Persons personsByFilter( List<String> attributeFilters, String orgUnitUid )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( Patient.class );
        criteria.createAlias( "attributeValues", "attributeValue" );
        criteria.createAlias( "attributeValue.patientAttribute", "attribute" );

        Disjunction or = Restrictions.or();
        criteria.add( or );

        if ( orgUnitUid != null )
        {
            OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, orgUnitUid );

            if ( organisationUnit == null )
            {
                throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, "OrganisationUnit with UID " + orgUnitUid + " does not exist." );
            }

            criteria.createAlias( "organisationUnit", "organisationUnit" );
            criteria.add( Restrictions.eq( "organisationUnit.uid", orgUnitUid ) );
        }

        // validate attributes, and build criteria
        for ( String filter : attributeFilters )
        {
            String[] split = filter.split( ":" );

            Conjunction and = Restrictions.and();
            or.add( and );

            if ( split.length != 3 )
            {
                throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, "Filter " + filter + " is not in valid format. " +
                    "Valid syntax is attribute=ATTRIBUTE_UID:OPERATOR:VALUE." );
            }

            PatientAttribute patientAttribute = manager.get( PatientAttribute.class, split[0] );

            if ( patientAttribute == null )
            {
                throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, "PersonAttribute with UID " + split[0] + " does not exist." );
            }

            if ( "like".equals( split[1].toLowerCase() ) )
            {
                and.add( Restrictions.and(
                    Restrictions.eq( "attribute.uid", split[0] ),
                    Restrictions.ilike( "attributeValue.value", "%" + split[2] + "%" )
                ) );
            }
            else if ( "eq".equals( split[1].toLowerCase() ) )
            {
                and.add( Restrictions.and(
                    Restrictions.eq( "attribute.uid", split[0] ),
                    Restrictions.eq( "attributeValue.value", split[2] )
                ) );
            }
            else if ( "ne".equals( split[1].toLowerCase() ) )
            {
                and.add( Restrictions.and(
                    Restrictions.eq( "attribute.uid", split[0] ),
                    Restrictions.ne( "attributeValue.value", split[2] )
                ) );
            }
            else if ( "gt".equals( split[1].toLowerCase() ) )
            {
                and.add( Restrictions.and(
                    Restrictions.eq( "attribute.uid", split[0] ),
                    Restrictions.gt( "attributeValue.value", split[2] )
                ) );
            }
            else if ( "lt".equals( split[1].toLowerCase() ) )
            {
                and.add( Restrictions.and(
                    Restrictions.eq( "attribute.uid", split[0] ),
                    Restrictions.lt( "attributeValue.value", split[2] )
                ) );
            }
            else if ( "ge".equals( split[1].toLowerCase() ) )
            {
                and.add( Restrictions.and(
                    Restrictions.eq( "attribute.uid", split[0] ),
                    Restrictions.ge( "attributeValue.value", split[2] )
                ) );
            }
            else if ( "in".equals( split[1].toLowerCase() ) )
            {
                String[] in = split[2].split( ";" );

                and.add( Restrictions.and(
                    Restrictions.eq( "attribute.uid", split[0] ),
                    Restrictions.in( "attributeValue.value", in )
                ) );
            }
        }

        criteria.addOrder( Order.desc( "lastUpdated" ) );

        return personService.getPersons( criteria.list() );
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_ACCESS_PATIENT_ATTRIBUTES')" )
    public String getPerson( @PathVariable
    String id, @RequestParam
    Map<String, String> parameters, Model model )
        throws NotFoundException
    {
        WebOptions options = new WebOptions( parameters );
        Person person = getPerson( id );

        model.addAttribute( "model", person );
        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return "person";
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_ADD')" )
    public void postPersonXml( HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        ImportSummaries importSummaries = personService.savePersonXml( request.getInputStream() );

        if ( importSummaries.getImportSummaries().size() > 1 )
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            JacksonUtils.toXml( response.getOutputStream(), importSummaries );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            ImportSummary importSummary = importSummaries.getImportSummaries().get( 0 );

            if ( !importSummary.getStatus().equals( ImportStatus.ERROR ) )
            {
                response.setHeader( "Location", getResourcePath( request, importSummary ) );
            }

            JacksonUtils.toXml( response.getOutputStream(), importSummary );
        }
    }

    @RequestMapping( value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_ADD')" )
    public void postPersonJson( HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        ImportSummaries importSummaries = personService.savePersonJson( request.getInputStream() );

        if ( importSummaries.getImportSummaries().size() > 1 )
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            JacksonUtils.toJson( response.getOutputStream(), importSummaries );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            ImportSummary importSummary = importSummaries.getImportSummaries().get( 0 );

            if ( !importSummary.getStatus().equals( ImportStatus.ERROR ) )
            {
                response.setHeader( "Location", getResourcePath( request, importSummary ) );
            }

            JacksonUtils.toJson( response.getOutputStream(), importSummary );
        }
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_ADD')" )
    public void updatePersonXml( @PathVariable
    String id, HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        ImportSummary importSummary = personService.updatePersonXml( id, request.getInputStream() );
        JacksonUtils.toXml( response.getOutputStream(), importSummary );
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_ADD')" )
    public void updatePersonJson( @PathVariable
    String id, HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        ImportSummary importSummary = personService.updatePersonJson( id, request.getInputStream() );
        JacksonUtils.toJson( response.getOutputStream(), importSummary );
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_DELETE')" )
    public void deletePerson( @PathVariable
    String id )
        throws NotFoundException
    {
        Person person = getPerson( id );
        personService.deletePerson( person );
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private Person getPerson( String id )
        throws NotFoundException
    {
        Person person = personService.getPerson( id );

        if ( person == null )
        {
            throw new NotFoundException( "Person", id );
        }
        return person;
    }

    private Program getProgram( String id )
        throws NotFoundException
    {
        Program program = manager.get( Program.class, id );

        if ( program == null )
        {
            throw new NotFoundException( "Person", id );
        }

        return program;
    }

    private String getResourcePath( HttpServletRequest request, ImportSummary importSummary )
    {
        return ContextUtils.getContextPath( request ) + "/api/" + "persons" + "/" + importSummary.getReference();
    }

    private OrganisationUnit getOrganisationUnit( String orgUnitUid )
    {
        OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, orgUnitUid );

        if ( organisationUnit == null )
        {
            throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, "orgUnit is not a valid uid." );
        }

        return organisationUnit;
    }
}