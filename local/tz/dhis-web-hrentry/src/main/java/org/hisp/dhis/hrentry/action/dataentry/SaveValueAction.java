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

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.DataValuesService;
import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.system.util.DateUtils;
import java.util.Collection;
import java.io.*;
import java.security.*;
import java.math.*;

import com.opensymphony.xwork2.Action;

/**
 * @author Yusuph Kassim Kulindwa
 * @version $Id$
 */
public class SaveValueAction implements Action {
	private static final Log LOG = LogFactory.getLog(SaveValueAction.class);

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private CurrentUserService currentUserService;

	public void setCurrentUserService(CurrentUserService currentUserService) {
		this.currentUserService = currentUserService;
	}

	private AttributeService attributeService;

	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	private AttributeOptionsService attributeOptionsService;

	public void setAttributeOptionsService(
			AttributeOptionsService attributeOptionsService) {
		this.attributeOptionsService = attributeOptionsService;
	}

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

	private OrganisationUnitService organisationUnitService;

	public void setOrganisationUnitService(
			OrganisationUnitService organisationUnitService) {
		this.organisationUnitService = organisationUnitService;
	}

	private DataValuesService dataValuesServices;

	public void DataValuesService(DataValuesService dataValuesService) {
		this.dataValuesService = dataValuesService;
	}

	// -------------------------------------------------------------------------
	// Input/output
	// -------------------------------------------------------------------------

	private String value;

	public void setValue(String value) {
		this.value = value;
	}

	private int attributeId;

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	public int getAttributeId() {
		return attributeId;
	}

	private int organisationUnitId;

	public void setOrganisationUnitId(int organisationUnitId) {
		this.organisationUnitId = organisationUnitId;
	}

	private int statusCode = 0;

	public int getStatusCode() {
		return statusCode;
	}

	private String inputId;

	public String getInputId() {
		return inputId;
	}

	public void setInputId(String inputId) {
		this.inputId = inputId;
	}

	private int personId;

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	private int hrDataSetId;

	public void setHrDataSetId(int hrDataSetId) {
		this.hrDataSetId = hrDataSetId;
	}

	private String attributeName;

	public String getAttributeName() {
		return attributeName;
	}

	private String personName;

	public String getPersonName() {
		return personName;
	}

	private String message;

	public String getMessage() {
		return message;
	}

	// -------------------------------------------------------------------------
	// Action implementation
	// -------------------------------------------------------------------------

	@SuppressWarnings("deprecation")
	public String execute() {
		OrganisationUnit organisationUnit = organisationUnitService
				.getOrganisationUnit(organisationUnitId);

		Attribute attribute = attributeService.getAttribute(attributeId);

		String storedBy = currentUserService.getCurrentUsername();

		HrDataSet dataset = hrDataSetService.getHrDataSet(hrDataSetId);

		if (storedBy == null) {
			storedBy = "[unknown]";
		}

		if (value != null && value.trim().length() == 0) {
			value = null;
		}

		if (value != null) {
			value = value.trim();
		}

		// ---------------------------------------------------------------------
		// Working Around with Data to Be entered into Person Table and Data
		// Values
		// ---------------------------------------------------------------------

		if (value != null) {
			// validating Input Data depending on the uniqueness
			boolean isUnique = attribute.getIsUnique();

			if (isUnique) {

				Collection<DataValues> dataValue = new ArrayList<DataValues>();

				dataValue = dataValuesService.getDatavalues(value, attribute);

				if (dataValue.isEmpty() == false) {
					return INPUT;
				}
			}

			// Person person = new Person();
			DataValues dataValue;

			boolean update = false;
			boolean insertPerson = false;
			String personDetails = "";

			attributeName = attribute.getName().toString().trim().toLowerCase();

			Person person = personService.getPerson(personId);

			if (person != null && attributeName != null) {

				LOG.debug("Updating Hr DataValue");

				// ---------------------------------------------------------------------
				// Generating Instance
				// ---------------------------------------------------------------------
				personDetails = person.getFirstName() + person.getMiddleName()
				+ person.getLastName() + person.getBirthDate();
				// ---------------------------------------------------------------------
				// Update data
				// ---------------------------------------------------------------------

				if (attributeName.equals(person.getFirstNameColumn().toString()
						.trim().toLowerCase())) {
					person.setFirstName(value);

					personDetails = value + person.getMiddleName()
							+ person.getLastName() + person.getBirthDate();

					update = true;
				} else if (attributeName.equals(person.getMiddleNameColumn()
						.toString().trim().toLowerCase())) {
					person.setMiddleName(value);

					personDetails = person.getFirstName() + value
							+ person.getLastName() + person.getBirthDate();

					update = true;
				} else if (attributeName.equals(person.getLastNameColumn()
						.toString().trim().toLowerCase())) {
					person.setLastName(value);

					personDetails = person.getFirstName()
							+ person.getMiddleName() + value
							+ person.getBirthDate();

					update = true;
				} else if (attributeName.equals(person.getBirthDateColumn()
						.toString().trim().toLowerCase())) {
					person.setBirthDate(DateUtils.getMediumDate(value));

					personDetails = person.getFirstName()
							+ person.getMiddleName() + person.getLastName()
							+ value;

					update = true;
				} else if (attributeName.equals(person.getGenderColumn()
						.toString().trim().toLowerCase())) {
					AttributeOptions attributeOption = attributeOptionsService
							.getAttributeOptions(Integer.parseInt(value));
					person.setGender(attributeOption.getValue());
					update = true;
				} else if (attributeName.equals(person.getNationalityColumn()
						.toString().trim().toLowerCase())) {
					AttributeOptions attributeOption = attributeOptionsService
							.getAttributeOptions(Integer.parseInt(value));
					person.setNationality(attributeOption.getValue());
					update = true;
				}

				try {
					personDetails = personDetails.toString().trim().toLowerCase();
					
					MessageDigest m = MessageDigest.getInstance("MD5");
					byte[] data = personDetails.getBytes();
					m.update(data, 0, data.length);
					BigInteger i = new BigInteger(1, m.digest());
					String md5 = String.format("%1$032X", i);
					person.setInstance(md5.toLowerCase());

				} catch (Exception e) {
					return null; // Always must return something
				}

				if (update == true) {
					personService.updatePerson(person);
				} else {
					DataValues dataValues = dataValuesService
							.getDataValuesByPersonAndAttribute(person,
									attribute);
					if (dataValues != null) {
						dataValues.setStoredBy(storedBy);
						dataValues.setValue(value);
						dataValues.setTimestamp(new Date());
						dataValuesService.updateDataValues(dataValues);
					} else {
						dataValue = new DataValues(value, person, attribute,
								storedBy, new Date());
						dataValuesService.saveDataValues(dataValue);
					}
				}

			} else {
				LOG.debug("Adding Hr DataValue");

				// ---------------------------------------------------------------------
				// Inserting New data
				// ---------------------------------------------------------------------

				person = new Person(personId, "", "", "", null, "", "",
						dataset, organisationUnit);
				personService.savePerson(person);

				person = personService.getPerson(personId);

				if (attributeName.equals(person.getFirstNameColumn().toString()
						.trim().toLowerCase())) {
					person.setFirstName(value);
					
					personDetails = value + person.getMiddleName()
					+ person.getLastName() + person.getBirthDate();
					
					insertPerson = true;
				} else if (attributeName.equals(person.getMiddleNameColumn()
						.toLowerCase())) {
					person.setMiddleName(value);
					
					insertPerson = true;
				} else if (attributeName.equals(person.getLastNameColumn()
						.toLowerCase())) {
					person.setLastName(value);
					
					personDetails = person.getFirstName()
					+ person.getMiddleName() + value
					+ person.getBirthDate();
					
					insertPerson = true;
				} else if (attributeName.equals(person.getBirthDateColumn()
						.toLowerCase())) {
					person.setBirthDate(DateUtils.getMediumDate(value));
					
					personDetails = person.getFirstName()
					+ person.getMiddleName() + person.getLastName()
					+ value;
					
					insertPerson = true;
				} else if (attributeName.equals(person.getGenderColumn()
						.toLowerCase())) {
					person.setGender(value);
					insertPerson = true;
				} else if (attributeName.equals(person.getNationalityColumn()
						.toLowerCase())) {
					person.setNationality(value);
					insertPerson = true;
				}

				try {
					
					personDetails = personDetails.toString().trim().toLowerCase();

					MessageDigest m = MessageDigest.getInstance("MD5");
					byte[] data = personDetails.getBytes();
					m.update(data, 0, data.length);
					BigInteger i = new BigInteger(1, m.digest());
					String md5 = String.format("%1$032X", i);
					person.setInstance(md5.toLowerCase());

				} catch (Exception e) {
					return null; // Always must return something
				}

				if (insertPerson == true) {
					personService.updatePerson(person);
				} else {

					dataValue = new DataValues(value, person, attribute,
							storedBy, new Date());
					dataValuesService.saveDataValues(dataValue);
				}

			}
		}

		message = "data Dublication";
		return SUCCESS;
	}
}
