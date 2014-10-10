/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Wilfred Felix Senyoni
 *         John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */

public interface PersonService {
	
	String ID = PersonService.class.getName();

    // -------------------------------------------------------------------------
    // Person
    // -------------------------------------------------------------------------

    
    int savePerson( Person person );

    void updatePerson( Person person );

    void deletePerson( Person person );

    Person getPerson( int id );
    
    Person getPersonByInstance( String instance );
    
    int getPersonByMaxId();
    
    Collection<Person> getAllPerson();  

    int getCountPersonByDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit unit, boolean selectedUnitOnly);
    
    int getCountPersonByNameDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit unit, boolean selectedUnitOnly, String key);
    
    Collection<Person> getPersonByDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit unit, boolean selectedUnitOnly);
    
    Collection<Person> getPersonByDatasetsAndOrganisation( Collection<HrDataSet> dataSets, OrganisationUnit unit, boolean selectedUnitOnly);
    
    Collection<Person> getPersons( final Collection<Integer> identifiers );
    
    Collection<Person> getPersonByDatasetAndOrganisationBetween(HrDataSet dataSet, OrganisationUnit organisationUnit, boolean selectedUnitOnly, int startPos, int pageSize);
    
    Collection<Person> getPersonByNameDatasetAndOrganisationBetween(HrDataSet dataSet, OrganisationUnit organisationUnit, boolean selectedUnitOnly, int startPos, int pageSize, String key);
    
    Grid getGrid( Collection<Person> person, String reportingUnit , HrDataSet hrDataSet );
    
    Collection<AggregateOperands> getAggregatedPersonByAttributeDatasetandOrganisation(HrDataSet dataSet, Attribute attribute, OrganisationUnit organisationUnit, boolean selectedUnitOnly);
  
}
