package org.hisp.dhis.patient.state;

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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface SelectedStateManager
{
    OrganisationUnit getSelectedOrganisationUnit();

    // -------------------------------------------------------------------------
    // for Patient
    // -------------------------------------------------------------------------

    void setSelectedPatient( Patient patient );

    Patient getSelectedPatient();

    void clearSelectedPatient();

    // -------------------------------------------------------------------------
    // for Program
    // -------------------------------------------------------------------------

    void setSelectedProgram( Program program );

    Program getSelectedProgram();

    void clearSelectedProgram();

    void setSelectedProgramStage( ProgramStage programStage );

    ProgramStage getSelectedProgramStage();

    void clearSelectedProgramStage();
    
    // -------------------------------------------------------------------------
    // for Searching patients - SearchingAttribute
    // -------------------------------------------------------------------------

    void setSearchingAttributeId( int searchingAttributeId );

    Integer getSearchingAttributeId();

    void clearSearchingAttributeId();

    
    void setSearchText( String searchText );

    String getSearchText();

    void clearSearchText();
    
    
    void setListAll( boolean listAll );

    boolean getListAll();

    void clearListAll();
    
    // -------------------------------------------------------------------------
    // for Sorting patients - Sort by patient-attribute
    // -------------------------------------------------------------------------

    void setSortingAttributeId( int sortAttributeId );

    Integer getSortAttributeId();

    void clearSortingAttributeId();

}
