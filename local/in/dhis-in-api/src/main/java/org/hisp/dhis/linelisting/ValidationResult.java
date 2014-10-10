package org.hisp.dhis.linelisting;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in element and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of element code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import java.io.Serializable;

/**
 * @author Margrethe Store
 * @version $Id: ValidationResult.java 5277 2008-05-27 15:48:42Z larshelg $
 */
@SuppressWarnings("serial")
public class ValidationResult
    implements Serializable
{
    private LineListGroup group;
    
    private LineListElement element;
    
    private LineListValidationRule LineListValidationRule;
    
    private double leftsideValue;
    
    private double rightsideValue;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------     

    public ValidationResult()
    {      
    }
    
    public ValidationResult( LineListGroup group, LineListElement element, LineListValidationRule LineListValidationRule,
        double leftsideValue, double rightsideValue )
    {
        this.group = group;
        this.element = element;
        this.LineListValidationRule = LineListValidationRule;
        this.leftsideValue = leftsideValue;
        this.rightsideValue = rightsideValue;
    }

    // -------------------------------------------------------------------------
    // Equals, hashCode and toString
    // -------------------------------------------------------------------------     

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        
        int result = 1;
        
        result = PRIME * result + ( ( group == null ) ? 0 : group.hashCode() );
        result = PRIME * result + ( ( element == null ) ? 0 : element.hashCode() );
        result = PRIME * result + ( ( LineListValidationRule == null ) ? 0 : LineListValidationRule.hashCode() );
        
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        final ValidationResult other = (ValidationResult) object;
        
        if ( group == null )
        {
            if ( other.group != null )
            {
                return false;
            }
        }
        else if ( !group.equals( other.group ) )
        {
            return false;
        }
        
        if ( element == null )
        {
            if ( other.element != null )
            {
                return false;
            }
        }
        else if ( !element.equals( other.element ) )
        {
            return false;
        }
        
        if ( LineListValidationRule == null )
        {
            if ( other.LineListValidationRule != null )
            {
                return false;
            }
        }
        else if ( !LineListValidationRule.equals( other.LineListValidationRule ) )
        {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString()
    {
        return element + " - " + group + " - " + LineListValidationRule + " - " + leftsideValue + " - " + rightsideValue;
    }
    
    // -------------------------------------------------------------------------
    // Set and get methods
    // -------------------------------------------------------------------------     

    public LineListElement getElement() {
        return element;
    }

    public void setElement(LineListElement element) {
        this.element = element;
    }

    public LineListGroup getGroup() {
        return group;
    }

    public void setGroup(LineListGroup group) {
        this.group = group;
    }

    public LineListValidationRule getLineListValidationRule()
    {
        return LineListValidationRule;
    }
    
    public void setLineListValidationRule( LineListValidationRule LineListValidationRule )
    {
        this.LineListValidationRule = LineListValidationRule;
    }

    public double getLeftsideValue()
    {
        return leftsideValue;
    }

    public void setLeftsideValue( double leftsideValue )
    {
        this.leftsideValue = leftsideValue;
    }

    public double getRightsideValue()
    {
        return rightsideValue;
    }

    public void setRightsideValue( double rightsideValue )
    {
        this.rightsideValue = rightsideValue;
    }
}
