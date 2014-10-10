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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.DataValuesService;
import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.Completeness;
import org.hisp.dhis.hr.CompletenessService;

import com.opensymphony.xwork2.Action;

/**
 * @author Yusuph Kassim Kulindwa
 * @version $Id$
 */

public class DataEditSelectAction implements Action {

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private OrganisationUnitSelectionManager selectionManager;

	public void setSelectionManager(
			OrganisationUnitSelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	private HrDataSetService hrDataSetService;

	public void setHrDataSetService(HrDataSetService hrDataSetService) {
		this.hrDataSetService = hrDataSetService;
	}

	private PersonService personService;

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	private CompletenessService completenessService;

	public void setCompletenessService(CompletenessService completenessService) {
		this.completenessService = completenessService;
	}

	// -------------------------------------------------------------------------
	// Input/output
	// -------------------------------------------------------------------------

	private OrganisationUnit organisationUnit;

	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}

	private int hrDataSetId;

	public void setHrDataSetId(int hrDataSetId) {
		this.hrDataSetId = hrDataSetId;
	}

	public int getHrDataSetId(int hrDataSetId) {
		return hrDataSetId;
	}

	public int getHrDataSetId() {
		return hrDataSetId;
	}

	private DataValuesService dataValuesService;

	public void setDataValuesService(DataValuesService dataValuesService) {
		this.dataValuesService = dataValuesService;
	}

	private HrDataSet hrDataSets;

	public HrDataSet getHrDataSets() {
		return hrDataSets;
	}

	private String hyperText;

	public String getHyperText() {
		return hyperText;
	}

	private int personId = 0;

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	boolean personExist = false;

	public boolean getPersonExist() {
		return personExist;
	}

	private Person person;

	public Person getPerson() {
		return person;
	}
	
	private Completeness completeness;

	private boolean complete = false;

	public boolean getComplete() {
		return complete;
	}

	// -------------------------------------------------------------------------
	// Action implementation
	// -------------------------------------------------------------------------

	public String execute() throws Exception {
		// ---------------------------------------------------------------------
		// Validate selected OrganisationUnit
		// ---------------------------------------------------------------------

		organisationUnit = selectionManager.getSelectedOrganisationUnit();

		person = personService.getPerson(personId);

		hrDataSets = hrDataSetService.getHrDataSet(hrDataSetId);
		
		completeness = completenessService.getCompletenessByPerson(person);
		
		if (completeness != null){
			complete = true;
		}

		if (person != null) {
			hyperText = processDataEntryForm(hrDataSets, person);
		}

		// hrDataSets.setHypertext( hrDataSets.getHypertext() );

		return SUCCESS;
	}

	public String processDataEntryForm(HrDataSet hrDataset, Person person) {
		// hrDataset.setHypertext( hrDataset.getHypertext() );

		/**
		 * Prepares the data entry form code by injecting with Data Value for
		 * each entry field
		 * 
		 * 
		 */

		// Attribute attribute = (Attribute) hrDataset.getAttribute();

		String searchString = "";
		String replaceString = "";
		String HTMLCode = hrDataset.getHypertext();
		String dataEntryFormfinal = "";

		for (Attribute attribute : hrDataset.getAttribute()) {

			// getting Data From text boxes
			searchString = "id=\"" + attribute.getId() + "\" value=\"";

			// Attribute attributeObj = attributeService.getAttribute(
			// attribute.getId() );

			Pattern textPattern = Pattern.compile(searchString);

			Matcher textMatcher = textPattern.matcher(HTMLCode);
			// System.out.println( searchString );
			StringBuffer dataEntryForm = new StringBuffer();

			while (textMatcher.find()) {
				if (person == null) {
					System.out.println("the person Object is empty");
				}

				if (attribute == null) {
					System.out.println("the attrinute Object is empty");
				}

				// System.out.println( "person Id is = " + person.getId() +
				// "attribute Id = " + attribute.getId() );

				DataValues dataValues = dataValuesService
						.getDataValuesByPersonAndAttribute(person, attribute);

				if (dataValues != null) {

					replaceString = "id = \"" + attribute.getId()
							+ "\" value = \"" + dataValues.getValue() + "";

					textMatcher.appendReplacement(dataEntryForm, replaceString);

					// System.out.println( "replace String " + replaceString );
				} else {
					String attributeName = attribute.getName().toString()
							.trim().toLowerCase();

					// ---------------------------------------------------------------------
					// Update data
					// ---------------------------------------------------------------------

					if (attributeName.equals(person.getFirstNameColumn()
							.toString().trim().toLowerCase())) {
						replaceString = "id = \"" + attribute.getId()
								+ "\" value = \"" + person.getFirstName() + "";

						textMatcher.appendReplacement(dataEntryForm,
								replaceString);
					} else if (attributeName.equals(person
							.getMiddleNameColumn().toString().trim()
							.toLowerCase())) {
						replaceString = "id = \"" + attribute.getId()
								+ "\" value = \"" + person.getMiddleName() + "";

						textMatcher.appendReplacement(dataEntryForm,
								replaceString);
					} else if (attributeName.equals(person.getLastNameColumn()
							.toString().trim().toLowerCase())) {
						replaceString = "id = \"" + attribute.getId()
								+ "\" value = \"" + person.getLastName() + "";

						textMatcher.appendReplacement(dataEntryForm,
								replaceString);
					} else if (attributeName.equals(person.getBirthDateColumn()
							.toString().trim().toLowerCase())) {
						replaceString = "id = \"" + attribute.getId()
								+ "\" value = \"" + person.getBirthDate() + "";

						textMatcher.appendReplacement(dataEntryForm,
								replaceString);
					}

				}
			}

			HTMLCode = textMatcher.appendTail(dataEntryForm).toString();
		}

		// getting Data From text Combo Matcher

		for (Attribute attribute : hrDataset.getAttribute()) {
			// end of Text matching

			searchString = "<option id=\"" + attribute.getId()
					+ "\" value=\"></option>";

			Pattern textPattern = Pattern.compile(searchString);

			Matcher textMatcher = textPattern.matcher(HTMLCode);

			StringBuffer dataEntryForm = new StringBuffer();

			while (textMatcher.find()) {
				DataValues dataValues = dataValuesService
						.getDataValuesByPersonAndAttribute(person, attribute);

				if (dataValues != null) {

					replaceString = "<option id = \"" + attribute.getId()
							+ "\" value = \"" + dataValues.getValue() + ">"
							+ dataValues.getValue() + "</option>";

					textMatcher.appendReplacement(dataEntryForm, replaceString);
				} else {
					String attributeName = attribute.getName().toString()
							.trim().toLowerCase();

					// ---------------------------------------------------------------------
					// Update data
					// ---------------------------------------------------------------------

					if (attributeName.equals(person.getGenderColumn()
							.toString().trim().toLowerCase())) {
						replaceString = "<option id = \"" + attribute.getId()
								+ "\" value = \"" + person.getGender() + ">"
								+ person.getGender() + "</option>";

						textMatcher.appendReplacement(dataEntryForm,
								replaceString);
					} else if (attributeName.equals(person
							.getNationalityColumn().toString().trim()
							.toLowerCase())) {
						replaceString = "<option id = \"" + attribute.getId()
								+ "\" value = \"" + person.getNationality()
								+ ">" + person.getNationality() + "</option>";

						textMatcher.appendReplacement(dataEntryForm,
								replaceString);
					}

				}
			}

			// comboMatcher.appendTail( dataEntryForm );

			HTMLCode = textMatcher.appendTail(dataEntryForm).toString();
		}

		// return dataEntryFormfinal;
		return HTMLCode;

	}

}
