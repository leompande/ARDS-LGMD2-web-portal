package org.hisp.dhis.jdbc.batchhandler;

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

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.hr.Person;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id: PersonBatchHandler.java 5242 $
 */
public class PersonBatchHandler
    extends AbstractBatchHandler<Person>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public PersonBatchHandler( JdbcConfiguration config )
    {
        super( config, false, false );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "hr_person" );
    }

    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "personid" );
    }

    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "personid" );
    }

    @Override
    protected void setIdentifierValues( Person person )
    {        
        statementBuilder.setIdentifierValue( person.getId() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "firstname" );
        statementBuilder.setColumn( "middlename" );
        statementBuilder.setColumn( "lastname" );
        statementBuilder.setColumn( "birthdate" );
        statementBuilder.setColumn( "gender" );
        statementBuilder.setColumn( "nationality" );
        statementBuilder.setColumn( "datasetid" );
        statementBuilder.setColumn( "organisationunitid" );
        statementBuilder.setColumn("instance");
    }
    
    protected void setValues( Person person )
    {        
        statementBuilder.setValue( person.getFirstName() );
        statementBuilder.setValue( person.getMiddleName() );
        statementBuilder.setValue( person.getLastName() );
        statementBuilder.setValue( person.getBirthDate() );
        statementBuilder.setValue( person.getGender() );
        statementBuilder.setValue( person.getNationality() );
        statementBuilder.setValue( person.getDataset().getId() );
        statementBuilder.setValue( person.getOrganisationUnit().getId() );
        statementBuilder.setValue( person.getInstance());
    }

	@Override
	protected void setUniqueColumns() {
		statementBuilder.setUniqueColumn("instance");
	}

	@Override
	protected void setUniqueValues(Person person) {
		statementBuilder.setUniqueValue(person.getInstance());
	}
}
