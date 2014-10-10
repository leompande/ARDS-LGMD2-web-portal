package org.hisp.dhis.importexport;

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
import java.util.Collection;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;

/**
 * @author John Francis Mukulu<john.f.mukulu@gmail.com>
 * @version $Id: ExportParams.java 5960 2008-10-17 14:07:50Z larshelg $
 */
public class HrExportParams
{
    private boolean includeDataValues;
    
    private boolean isHrDomain;
    
    private boolean includeHrMetadata;
    
    private String domainType;
    
    
    private Collection<Integer> organisationUnits = new ArrayList<Integer>();
    
    // Parameters from HR Module
    
    private Collection<Integer> hrDataSets = new ArrayList<Integer>();
    
    private Collection<Integer> attributes = new ArrayList<Integer>();
    
    private Collection<Integer> attributeOptions = new ArrayList<Integer>();
    
    private Collection<Integer> attributeGroups = new ArrayList<Integer>();
    
    private Collection<Integer> attributeOptionGroups = new ArrayList<Integer>();
    
    private Collection<Integer> dataType = new ArrayList<Integer>();
    
    private Collection<Integer> dataValues = new ArrayList<Integer>();
    
    private Collection<Integer> history = new ArrayList<Integer>();
    
    private Collection<Integer> training = new ArrayList<Integer>();
    
    private Collection<Integer> inputType = new ArrayList<Integer>();
    
    private Collection<Integer> person = new ArrayList<Integer>();
    
    private I18n i18n;
    
    private I18nFormat format;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    
    public HrExportParams()
    {   
    }
    
    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------

    public boolean isIncludeDataValues()
    {
        return includeDataValues;
    }

    public void setIncludeDataValues( boolean includeDataValues )
    {
        this.includeDataValues = includeDataValues;
    }
    
    public boolean isHrDomain()
    {
    	return isHrDomain;
    }
    
    public void setIsHrDomain(boolean isHrDomain)
    {
    	this.isHrDomain = isHrDomain;
    }
    
    public boolean getIncludeHrMetadata()
    {
    	return includeHrMetadata;
    }
    
    public void setIncludeHrMetadata(boolean includeHrMetadata)
    {
    	this.includeHrMetadata = includeHrMetadata;
    }
    
    public void setDomainType( String domainType )
    {
    	this.domainType = domainType;
    }
    
    public String getDomainType()
    {
    	return domainType;
    }


    public I18n getI18n()
    {
        return i18n;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    public I18nFormat getFormat()
    {
        return format;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public Collection<Integer> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( Collection<Integer> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }
    
    // -------------------------------------------------
    // - Getters & Setters from HR Module			----
    // -------------------------------------------------
    
    public Collection<Integer> getHrDataSets()
    {
    	return hrDataSets;
    }
    
    public void setHrDataSets(Collection<Integer> hrDataSets)
    {
    	this.hrDataSets = hrDataSets;
    }
    
    public Collection<Integer> getAttributes()
    {
    	return attributes;
    }
    
    public void setAttributes(Collection<Integer> attributes )
    {
    	this.attributes = attributes;
    }
    
    public Collection<Integer> getAttributeOptions()
    {
    	return attributeOptions;
    }
    
    public void setAttributeOptions(Collection<Integer> attributeOptions)
    {
    	this.attributeOptions = attributeOptions;
    }
    
    public Collection<Integer> getAttributeGroups()
    {
    	return attributeGroups;
    }
    
    public void setAttributeGroups(Collection<Integer> attributeGroups)
    {
    	this.attributeGroups = attributeGroups;
    }
    
    public Collection<Integer> getAttributeOptionGroups()
    {
    	return attributeOptionGroups;
    }
    
    public void setAttributeOptionGroups(Collection<Integer> attributeOptionGroups)
    {
    	this.attributeOptionGroups = attributeOptionGroups;
    }
    
    public Collection<Integer> getDataType()
    {
    	return dataType;
    }
    
    public void setDataType(Collection<Integer> dataType)
    {
    	this.dataType = dataType;
    }
    
    public Collection<Integer> getDataValues()
    {
    	return dataValues;
    }
    
    public void setDataValues(Collection<Integer> dataValues)
    {
    	this.dataValues = dataValues;
    }
    
    public Collection<Integer> getTraining()
    {
    	return training;
    }
    
    public void setTraining(Collection<Integer> training)
    {
    	this.training = training;
    }
    
    public Collection<Integer> getHistory()
    {
    	return history;
    }
    
    public void setHistory(Collection<Integer> history)
    {
    	this.history = history;
    }
    
    public Collection<Integer> getInputType()
    {
    	return inputType;
    }
    
    public void setInputType(Collection<Integer> inputType)
    {
    	this.inputType = inputType;
    }
    
    public Collection<Integer> getPerson()
    {
    	return person;
    }
    
    public void setPerson(Collection<Integer> person)
    {
    	this.person = person;
    }
    
}
