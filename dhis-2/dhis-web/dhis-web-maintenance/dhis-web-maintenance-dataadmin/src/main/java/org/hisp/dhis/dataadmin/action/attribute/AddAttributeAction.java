package org.hisp.dhis.dataadmin.action.attribute;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;

/**
 * @author mortenoh
 */
public class AddAttributeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String valueType;

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    private Boolean mandatory = false;

    public void setMandatory( Boolean mandatory )
    {
        this.mandatory = mandatory;
    }

    private Boolean dataElementAttribute = false;

    public void setDataElementAttribute( Boolean dataElementAttribute )
    {
        this.dataElementAttribute = dataElementAttribute;
    }

    private Boolean dataElementGroupAttribute = false;

    public void setDataElementGroupAttribute( Boolean dataElementGroupAttribute )
    {
        this.dataElementGroupAttribute = dataElementGroupAttribute;
    }

    private Boolean indicatorAttribute = false;

    public void setIndicatorAttribute( Boolean indicatorAttribute )
    {
        this.indicatorAttribute = indicatorAttribute;
    }

    private Boolean indicatorGroupAttribute = false;

    public void setIndicatorGroupAttribute( Boolean indicatorGroupAttribute )
    {
        this.indicatorGroupAttribute = indicatorGroupAttribute;
    }

    private Boolean organisationUnitAttribute = false;

    public void setOrganisationUnitAttribute( Boolean organisationUnitAttribute )
    {
        this.organisationUnitAttribute = organisationUnitAttribute;
    }

    private Boolean organisationUnitGroupAttribute = false;

    public void setOrganisationUnitGroupAttribute( Boolean organisationUnitGroupAttribute )
    {
        this.organisationUnitGroupAttribute = organisationUnitGroupAttribute;
    }

    private Boolean userAttribute = false;

    public void setUserAttribute( Boolean userAttribute )
    {
        this.userAttribute = userAttribute;
    }

    private Boolean userGroupAttribute = false;

    public void setUserGroupAttribute( Boolean userGroupAttribute )
    {
        this.userGroupAttribute = userGroupAttribute;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        Attribute attribute = new Attribute( name, valueType );
        attribute.setMandatory( mandatory );
        attribute.setDataElementAttribute( dataElementAttribute );
        attribute.setDataElementGroupAttribute( dataElementGroupAttribute );
        attribute.setIndicatorAttribute( indicatorAttribute );
        attribute.setIndicatorGroupAttribute( indicatorGroupAttribute );
        attribute.setOrganisationUnitAttribute( organisationUnitAttribute );
        attribute.setOrganisationUnitGroupAttribute( organisationUnitGroupAttribute );
        attribute.setUserAttribute( userAttribute );
        attribute.setUserGroupAttribute( userGroupAttribute );

        attributeService.addAttribute( attribute );

        return SUCCESS;
    }
}
