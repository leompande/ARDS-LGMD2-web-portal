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

import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.controller.exception.NotFoundException;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.events.enrollment.Enrollment;
import org.hisp.dhis.dxf2.events.enrollment.EnrollmentService;
import org.hisp.dhis.dxf2.events.enrollment.EnrollmentStatus;
import org.hisp.dhis.dxf2.events.enrollment.Enrollments;
import org.hisp.dhis.dxf2.events.person.Person;
import org.hisp.dhis.dxf2.events.person.PersonService;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = EnrollmentController.RESOURCE_PATH )
public class EnrollmentController
{
    public static final String RESOURCE_PATH = "/enrollments";

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private PersonService personService;

    @Autowired
    private IdentifiableObjectManager manager;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.GET )
    public String getEnrollments(
        @RequestParam( value = "orgUnit", required = false ) String orgUnitUid,
        @RequestParam( value = "program", required = false ) String programUid,
        @RequestParam( value = "person", required = false ) String personUid,
        @RequestParam( value = "status", required = false ) EnrollmentStatus status,
        @RequestParam Map<String, String> parameters, Model model ) throws NotFoundException
    {
        WebOptions options = new WebOptions( parameters );
        Enrollments enrollments;

        if ( orgUnitUid == null && programUid == null && personUid == null )
        {
            enrollments = status != null ? enrollmentService.getEnrollments( status ) : enrollmentService.getEnrollments();
        }
        else if ( orgUnitUid != null && programUid != null )
        {
            OrganisationUnit organisationUnit = getOrganisationUnit( orgUnitUid );
            Program program = getProgram( programUid );

            enrollments = enrollmentService.getEnrollments( program, organisationUnit );
        }
        else if ( programUid != null && personUid != null )
        {
            Program program = getProgram( programUid );
            Person person = getPerson( personUid );

            enrollments = status != null ? enrollmentService.getEnrollments( program, person, status )
                : enrollmentService.getEnrollments( program, person );
        }
        else if ( orgUnitUid != null )
        {
            OrganisationUnit organisationUnit = getOrganisationUnit( orgUnitUid );
            enrollments = status != null ? enrollmentService.getEnrollments( organisationUnit, status )
                : enrollmentService.getEnrollments( organisationUnit );
        }
        else if ( programUid != null )
        {
            Program program = getProgram( programUid );
            enrollments = status != null ? enrollmentService.getEnrollments( program, status ) : enrollmentService.getEnrollments( program );
        }
        else
        {
            Person person = getPerson( personUid );
            enrollments = status != null ? enrollmentService.getEnrollments( person, status ) : enrollmentService.getEnrollments( person );
        }

        model.addAttribute( "model", enrollments );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return "enrollments";
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    public String getEnrollment( @PathVariable String id, @RequestParam Map<String, String> parameters, Model model ) throws NotFoundException
    {
        WebOptions options = new WebOptions( parameters );
        Enrollment enrollment = getEnrollment( id );

        model.addAttribute( "model", enrollment );
        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return "enrollment";
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE )
    @PreAuthorize("hasRole('ALL') or hasRole('F_PROGRAM_ENROLLMENT')")
    public void postEnrollmentXml( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        ImportSummaries importSummaries = enrollmentService.saveEnrollmentsXml( request.getInputStream() );

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
    @PreAuthorize("hasRole('ALL') or hasRole('F_PROGRAM_ENROLLMENT')")
    public void postEnrollmentJson( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        ImportSummaries importSummaries = enrollmentService.saveEnrollmentsJson( request.getInputStream() );

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
    @PreAuthorize("hasRole('ALL') or hasRole('F_PROGRAM_UNENROLLMENT')")
    public void updateEnrollmentXml( @PathVariable String id, HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        ImportSummary importSummary = enrollmentService.updateEnrollmentXml( id, request.getInputStream() );
        JacksonUtils.toXml( response.getOutputStream(), importSummary );
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    @PreAuthorize("hasRole('ALL') or hasRole('F_PROGRAM_UNENROLLMENT')")
    public void updateEnrollmentJson( @PathVariable String id, HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        ImportSummary importSummary = enrollmentService.updateEnrollmentJson( id, request.getInputStream() );
        JacksonUtils.toJson( response.getOutputStream(), importSummary );
    }

    @RequestMapping( value = "/{id}/cancelled", method = RequestMethod.PUT )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    @PreAuthorize("hasRole('ALL') or hasRole('F_PROGRAM_UNENROLLMENT')")
    public void cancelEnrollment( @PathVariable String id ) throws NotFoundException
    {
        Enrollment enrollment = getEnrollment( id );
        enrollmentService.cancelEnrollment( enrollment );
    }

    @RequestMapping( value = "/{id}/completed", method = RequestMethod.PUT )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    @PreAuthorize("hasRole('ALL') or hasRole('F_PROGRAM_UNENROLLMENT')")
    public void completedEnrollment( @PathVariable String id ) throws NotFoundException
    {
        Enrollment enrollment = getEnrollment( id );
        enrollmentService.completeEnrollment( enrollment );
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    @PreAuthorize("hasRole('ALL') or hasRole('F_PROGRAM_UNENROLLMENT')")
    public void deleteEnrollment( @PathVariable String id ) throws NotFoundException
    {
        Enrollment enrollment = getEnrollment( id );
        enrollmentService.deleteEnrollment( enrollment );
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private Enrollment getEnrollment( String id ) throws NotFoundException
    {
        Enrollment enrollment = enrollmentService.getEnrollment( id );

        if ( enrollment == null )
        {
            throw new NotFoundException( "Enrollment", id );
        }

        return enrollment;
    }

    private Person getPerson( String id ) throws NotFoundException
    {
        Person person = personService.getPerson( id );

        if ( person == null )
        {
            throw new NotFoundException( "Person", id );
        }

        return person;
    }

    private OrganisationUnit getOrganisationUnit( String id ) throws NotFoundException
    {
        OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, id );

        if ( organisationUnit == null )
        {
            throw new NotFoundException( "OrganisationUnit", id );
        }

        return organisationUnit;
    }

    private Program getProgram( String id ) throws NotFoundException
    {
        Program program = manager.get( Program.class, id );

        if ( program == null )
        {
            throw new NotFoundException( "Program", id );
        }

        return program;
    }

    private String getResourcePath( HttpServletRequest request, ImportSummary importSummary )
    {
        return ContextUtils.getContextPath( request ) + "/api/" + "enrollments" + "/" + importSummary.getReference();
    }
}
