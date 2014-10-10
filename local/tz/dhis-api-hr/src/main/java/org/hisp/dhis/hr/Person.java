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

package org.hisp.dhis.hr;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

@SuppressWarnings( "serial" )
public class Person
	extends AbstractNameableObject
	{
	
	private int id;
	
	private String instance;

    private String firstName;

    private String middleName;
    
    private String lastName;
    
    private Date birthDate;
    
    private String gender;
    
    private String nationality;
    
    private HrDataSet dataset;
    
    private OrganisationUnit organisationUnit;
    
    private Set<DataValues> dataValues = new HashSet<DataValues>();
    
    private Set<History> history = new HashSet<History>();
    
    private Set<Training> training = new HashSet<Training>();
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Person()
    {
    }
    
    public Person(String firstName, String middleName, String lastName, HrDataSet dataset, OrganisationUnit organisationUnit)
    {
    	this.firstName = firstName;
    	this.middleName = middleName;
    	this.lastName = lastName;
    	this.dataset = dataset;
    	this.organisationUnit = organisationUnit;
    	
    }
    
    public Person(int id, String firstName, String middleName, String lastName , Date birthDate, String gender, String nationality, HrDataSet dataset, OrganisationUnit organisationUnit)
    {
    	this.id = id;
    	this.firstName = firstName;
    	this.middleName = middleName;
    	this.lastName = lastName;
    	this.birthDate = birthDate;
    	this.gender = gender;
    	this.nationality = nationality;
    	this.dataset = dataset;
    	this.organisationUnit = organisationUnit;
    }
    
 
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public int getId()
    {
        return id;
    }
    
    public void setId( int id )
    {
        this.id = id;
    }
    
    public String getInstance()
    {
        return instance;
    }

    public void setInstance( String instance )
    {
        this.instance = instance;
    }
    
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }
    
    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }
    
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }
    
    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate( Date birthDate )
    {
        this.birthDate = birthDate;
    }
    
    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }
    
    public String getNationality()
    {
        return nationality;
    }

    public void setNationality( String nationality )
    {
        this.nationality = nationality;
    }
    
    public HrDataSet getDataset()
    {
        return dataset;
    }
    
    public void setDataset( HrDataSet dataset )
    {
        this.dataset = dataset;
    }
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }
    
    public void setDataValues( Set<DataValues> dataValues )
    {
        this.dataValues = dataValues;
    }

    public Set<DataValues> getDataValues()
    {
        return dataValues;
    }
    
    public void setTraining( Set<Training> training )
    {
        this.training = training;
    }

    public Set<Training> getTraining()
    {
        return training;
    }
    
    public void setHistory( Set<History> history )
    {
        this.history = history;
    }

    public Set<History> getHistory()
    {
        return history;
    }

    public String getFirstNameColumn(){
    	return "Firstname";
    }
    
    public String getMiddleNameColumn(){
    	return "Middlename";
    }
    
    public String getLastNameColumn(){
    	return "Lastname";
    }
    
    public String getBirthDateColumn(){
    	return "Birthdate";
    }
    
    public String getGenderColumn(){
    	return "Gender";
    }
    
    public String getNationalityColumn(){
    	return "Nationality";
    }
    
}
