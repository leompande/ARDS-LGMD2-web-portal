package org.hisp.dhis.light.namebaseddataentry.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

public class GetFindBeneficiaryFormAction implements Action {
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private CurrentUserService currentUserService;

	public void setCurrentUserService(CurrentUserService currentUserService) {
		this.currentUserService = currentUserService;
	}

	private PatientAttributeService patientAttributeService;

	public void setPatientAttributeService(
			PatientAttributeService patientAttributeService) {
		this.patientAttributeService = patientAttributeService;
	}

	// -------------------------------------------------------------------------
	// Input & Output
	// -------------------------------------------------------------------------

	private Integer patientAttributeId;

	public Integer getPatientAttributeId() {
		return patientAttributeId;
	}

	public void setPatientAttributeId(Integer patientAttributeId) {
		this.patientAttributeId = patientAttributeId;
	}

	private Integer orgUnitId;

	public Integer getOrgUnitId() {
		return orgUnitId;
	}

	public void setOrgUnitId(Integer orgUnitId) {
		this.orgUnitId = orgUnitId;
	}

	private Collection<PatientAttribute> patientAttributes;

	public Collection<PatientAttribute> getPatientAttributes() {
		return patientAttributes;
	}

	public void setPatientAttributes(
			Collection<PatientAttribute> patientAttributes) {
		this.patientAttributes = patientAttributes;
	}

	private Set<OrganisationUnit> organisationUnits;

	public Set<OrganisationUnit> getOrganisationUnits() {
		return organisationUnits;
	}

	public void setOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
		this.organisationUnits = organisationUnits;
	}

	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// use in find relation person

	private Integer originalPatientId;

	public Integer getOriginalPatientId() {
		return originalPatientId;
	}

	public void setOriginalPatientId(Integer originalPatientId) {
		this.originalPatientId = originalPatientId;
	}

	private Integer relationshipTypeId;

	public Integer getRelationshipTypeId() {
		return relationshipTypeId;
	}

	public void setRelationshipTypeId(Integer relationshipTypeId) {
		this.relationshipTypeId = relationshipTypeId;
	}

	@Override
	public String execute() throws Exception {
		user = currentUserService.getCurrentUser();
		Collection<OrganisationUnit> basicOrganisationUnits = currentUserService
				.getCurrentUser().getOrganisationUnits();
		organisationUnits = new HashSet<OrganisationUnit>();

		for (OrganisationUnit organisationUnit : basicOrganisationUnits) {
			organisationUnits.addAll(this
					.getAllParentOrganisationUnits(organisationUnit));
		}

		patientAttributes = patientAttributeService.getAllPatientAttributes();

		return SUCCESS;
	}

	private Collection<? extends OrganisationUnit> getAllParentOrganisationUnits(
			OrganisationUnit organisationUnit) {
		List<OrganisationUnit> parents = new ArrayList<OrganisationUnit>();
		parents.add(organisationUnit);

		while (organisationUnit.getParent() != null) {
			parents.add(organisationUnit.getParent());
			organisationUnit = organisationUnit.getParent();
		}
		return parents;
	}

}
