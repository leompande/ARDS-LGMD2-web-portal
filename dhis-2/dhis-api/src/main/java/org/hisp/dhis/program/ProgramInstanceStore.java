package org.hisp.dhis.program;

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

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;

import java.util.Collection;
import java.util.Date;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface ProgramInstanceStore
    extends GenericIdentifiableObjectStore<ProgramInstance>
{
    String ID = ProgramInstanceStore.class.getName();

    /**
     * Retrieve program instances by status
     * 
     * @param status Status of program-instance, include STATUS_ACTIVE,
     *        STATUS_COMPLETED and STATUS_CANCELLED
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> getByStatus( Integer status );

    /**
     * Retrieve program instances on a program
     * 
     * @param program Program
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Program program );

    /**
     * Retrieve program instances on program list
     * 
     * @param programs Program list
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Collection<Program> programs );

    /**
     * Retrieve program instances of whom registered in to a orgunit from
     * program list
     * 
     * @param programs Program list
     * @param organisationUnit Organisation Unit
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Collection<Program> programs, OrganisationUnit organisationUnit );

    /**
     * Retrieve program instances of whom registered in to a orgunit from
     * program list with a certain status
     * 
     * @param programs Program list
     * @param organisationUnit Organisation Unit
     * @param status Status of program-instance, include STATUS_ACTIVE,
     *        STATUS_COMPLETED and STATUS_CANCELLED
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Collection<Program> programs, OrganisationUnit organisationUnit, int status );

    /**
     * Retrieve program instances on a program by status
     * 
     * @param program Program
     * @param status Status of program-instance, include STATUS_ACTIVE,
     *        STATUS_COMPLETED and STATUS_CANCELLED
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Program program, Integer status );

    /**
     * Retrieve program instances on a program list by status
     * 
     * @param programs Program list
     * @param status Status of program-instance, include STATUS_ACTIVE,
     *        STATUS_COMPLETED and STATUS_CANCELLED
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Collection<Program> programs, Integer status );

    /**
     * Retrieve program instances on a patient by a status
     * 
     * @param patient Patient
     * @param status Status of program-instance, include STATUS_ACTIVE,
     *        STATUS_COMPLETED and STATUS_CANCELLED
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Patient patient, Integer status );

    /**
     * Retrieve program instances on a patient by a program
     * 
     * @param patient Patient
     * @param program Program
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Patient patient, Program program );

    /**
     * Retrieve program instances on a patient with a status by a program
     * 
     * @param patient Patient
     * @param program Program
     * @param status Status of program-instance, include STATUS_ACTIVE,
     *        STATUS_COMPLETED and STATUS_CANCELLED
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Patient patient, Program program, Integer status );

    /**
     * Retrieve program instances with active status on an orgunit by a program
     * with result limited
     * 
     * @param program Program
     * @param organisationUnit Organisation Unit
     * @param min
     * @param max
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Program program, OrganisationUnit organisationUnit, Integer min, Integer max );

    /**
     * Retrieve program instances with active status on an orgunit by a program
     * in a certain period
     * 
     * @param program Program
     * @param organisationUnit Organisation Unit
     * @param startDate The start date for retrieving on enrollment-date
     * @param endDate The end date for retrieving on enrollment-date
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Program program, OrganisationUnit organisationUnit, Date startDate, Date endDate );

    /**
     * Retrieve program instances with active status on an orgunit by a program
     * for a certain period with result limited
     * 
     * @param program Program
     * @param organisationUnit Organisation Unit
     * @param startDate The start date for retrieving on enrollment-date
     * @param endDate The end date for retrieving on enrollment-date
     * @param min
     * @param max
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> get( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate,
        Integer min, Integer max );

    /**
     * Get the number of program instances of a program on an organisation unit
     * 
     * @param program Program
     * @param organisationUnit Organisation Unit
     * @param startDate The start date for retrieving on enrollment-date
     * @param endDate The end date for retrieving on enrollment-date
     * 
     * @return ProgramInstance list
     */
    int count( Program program, OrganisationUnit organisationUnit );

    /**
     * Get the number of program instances which are active status and
     * registered in a certain orgunit by a program for a certain period
     * 
     * @param program Program
     * @param organisationUnit Organisation Unit
     * @param startDate The start date for retrieving on enrollment-date
     * @param endDate The end date for retrieving on enrollment-date
     * 
     * @return ProgramInstance list
     */
    int count( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate );

    /**
     * Get the number of program instances of a program which have a certain
     * status and an orgunit ids list for a period
     * 
     * @param Status of program-instance, include STATUS_ACTIVE,
     *        STATUS_COMPLETED and STATUS_CANCELLED
     * @param program ProgramInstance
     * @param orgunitIds A list of orgunit ids
     * @param startDate The start date for retrieving on enrollment-date
     * @param endDate The end date for retrieving on enrollment-date
     * 
     * @return A number
     */
    int countByStatus( Integer status, Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate );

    /**
     * Retrieve program instances with a certain status on a program and an
     * orgunit ids list for a period
     * 
     * @param Status of program-instance, include STATUS_ACTIVE,
     *        STATUS_COMPLETED and STATUS_CANCELLED
     * @param program ProgramInstance
     * @param orgunitIds A list of orgunit ids
     * @param startDate The start date for retrieving on enrollment-date
     * @param endDate The end date for retrieving on enrollment-date
     * 
     * @return ProgramInstance list
     */
    Collection<ProgramInstance> getByStatus( Integer status, Program program, Collection<Integer> orgunitIds,
        Date startDate, Date endDate );

    /**
     * Rerieve schedule list of patiens registered
     * 
     * @return A SchedulingProgramObject list
     */
    Collection<SchedulingProgramObject> getSendMesssageEvents( String dateToCompare );
}
