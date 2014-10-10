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
package org.hisp.dhis.hrentry.action.dataentry;

import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;

import com.opensymphony.xwork2.Action;

/**
 * @author Yusuph Kassim Kulindwa
 * @version $Id$
 */

public class DataEntrySelectAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private HrDataSetService hrDataSetService;

    public void setHrDataSetService( HrDataSetService hrDataSetService )
    {
        this.hrDataSetService = hrDataSetService;
    }

    private PersonService personService;

    public void setPersonService( PersonService personService )
    {
        this.personService = personService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private int hrDataSetId;

    public void setHrDataSetId( int hrDataSetId )
    {
        this.hrDataSetId = hrDataSetId;
    }

    public int getHrDataSetId( int hrDataSetId )
    {
        return hrDataSetId;
    }

    public int getHrDataSetId()
    {
        return hrDataSetId;
    }

    private HrDataSet hrDataSets;

    public HrDataSet getHrDataSets()
    {
        return hrDataSets;
    }

    private String hyperText;

    public String getHyperText()
    {
        return hyperText;
    }

    private int personId;

    public int getPersonId()
    {
        return personId;
    }

    private Person person;

    public Person getPerson()
    {
        return person;
    }
    
    private boolean complete = false;

	public boolean getComplete() {
		return complete;
	}

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Validate selected OrganisationUnit
        // ---------------------------------------------------------------------

        organisationUnit = selectionManager.getSelectedOrganisationUnit();

        person = new Person();

        personId = personService.getPersonByMaxId();

        personId = personId + 1;

        hrDataSets = hrDataSetService.getHrDataSet( hrDataSetId );

        //

        if ( person == null )
        {
            person = new Person();
        }

        hrDataSets.setHypertext( hrDataSets.getHypertext() );

        return SUCCESS;
    }

   
}
