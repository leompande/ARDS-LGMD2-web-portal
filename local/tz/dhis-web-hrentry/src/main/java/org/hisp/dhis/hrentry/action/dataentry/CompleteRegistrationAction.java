package org.hisp.dhis.hrentry.action.dataentry;

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

import java.util.Date;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.DataValuesService;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.Completeness;
import org.hisp.dhis.hr.CompletenessService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Yusuph Kassim Kulindwa
 * @version $Id$
 */

public class CompleteRegistrationAction implements Action {

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private PersonService personService;

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	private DataValuesService dataValuesService;

	public void setDataValuesService(DataValuesService dataValuesService) {
		this.dataValuesService = dataValuesService;
	}

	private HrDataSetService hrDataSetService;

	public void setHrDataSetService(HrDataSetService hrDataSetService) {
		this.hrDataSetService = hrDataSetService;
	}

	private CompletenessService completenessService;

	public void setCompletenessService(CompletenessService completenessService) {
		this.completenessService = completenessService;
	}

	private CurrentUserService currentUserService;

	public void setCurrentUserService(CurrentUserService currentUserService) {
		this.currentUserService = currentUserService;
	}

	// -------------------------------------------------------------------------
	// Input/output
	// -------------------------------------------------------------------------

	private int hrDataSetId;

	public void setHrDataSetId(int hrDataSetId) {
		this.hrDataSetId = hrDataSetId;
	}

	private int personId;

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	private String status;

	public String getStatus() {
		return status;
	}

	private boolean isUnique = false;

	private boolean isCompulsory = false;

	private boolean isValueEmpty = false;

	private boolean isPersonEmpty = false;

	@Override
	public String execute() throws Exception {

		HrDataSet hrDataSet = hrDataSetService.getHrDataSet(hrDataSetId);
		Person person = personService.getPerson(personId);

		for (Attribute attribute : hrDataSet.getAttribute()) {
			isUnique = attribute.getIsUnique();
			isCompulsory = attribute.getCompulsory();

			if (isUnique == true || isCompulsory == true) {
				DataValues dataValues = dataValuesService
						.getDataValuesByPersonAndAttribute(person, attribute);

				if (dataValues == null) {

					isValueEmpty = true;
					
					String attributeName = attribute.getName().toString()
							.trim().toLowerCase();

					// ---------------------------------------------------------------------
					// Update data
					// ---------------------------------------------------------------------

					if (person != null) {
						if (attributeName.equals(person.getFirstNameColumn()
								.toString().trim().toLowerCase())) {
							isValueEmpty = false;
							if (person.getFirstName().isEmpty()) {
								isPersonEmpty = true;
								System.out.println("first Name "
										+ person.getFirstName());
							}
						} else if (attributeName.equals(person
								.getMiddleNameColumn().toString().trim()
								.toLowerCase())) {
							isValueEmpty = false;
							if (person.getMiddleName().isEmpty()) {
								isPersonEmpty = true;
								System.out.println("Middle Name "
										+ person.getMiddleNameColumn());
							}

						} else if (attributeName.equals(person
								.getLastNameColumn().toString().trim()
								.toLowerCase())) {
							isValueEmpty = false;
							if (person.getLastName().isEmpty()) {
								isPersonEmpty = true;
								System.out.println("Last Name "
										+ person.getLastNameColumn());
							}
						} else if (attributeName.equals(person
								.getBirthDateColumn().toString().trim()
								.toLowerCase())) {
							isValueEmpty = false;
							if (person.getBirthDate().equals("")) {
								isPersonEmpty = true;
								System.out.println("birthDate "
										+ person.getBirthDateColumn());
							}
						} else if (attributeName.equals(person
								.getGenderColumn().toString().trim()
								.toLowerCase())) {
							isValueEmpty = false;
							if (person.getGender().isEmpty()) {
								isPersonEmpty = true;
								System.out.println("gender "
										+ person.getGenderColumn());
							}
						} else if (attributeName.equals(person
								.getNationalityColumn().toString().trim()
								.toLowerCase())) {
							isValueEmpty = false;
							if (person.getNationality().isEmpty()) {
								isPersonEmpty = true;
								System.out.println("Nationality "
										+ person.getNationalityColumn());
							}
						}

					}
				}

			}
		}

		String storedBy = currentUserService.getCurrentUsername();

		if (storedBy == null) {
			storedBy = "[unknown]";
		}
		
		if (isValueEmpty == true || isPersonEmpty == true) {
			return INPUT;
		}

		if (person != null) {

			Completeness completeness = new Completeness(person, new Date(),
					storedBy);
			completenessService.saveCompleteness(completeness);

			status = "done";
		}
		return SUCCESS;

	}
}
