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
package org.hisp.dhis.detarget;

import java.io.Serializable;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DeTargetMember.java Jan 12, 2011 10:52:22 AM
 */
@SuppressWarnings("serial")
public class DeTargetMember implements Serializable
{
     /**
     * Part of the DeTarget's composite ID
     */
    private DeTarget detarget;

    
    /**
     * All DataElements associated with this DataElement Target.
     */
   // private Collection<DataElement> dataelements = new HashSet<DataElement>();
    private DataElement dataelements;
    
    /**
     * All DataElement Category Option Combo associated with this DataElement Target.
     */
   // private Collection<DataElementCategoryOptionCombo> decategoryOptionCombo = new HashSet<DataElementCategoryOptionCombo>();
    private DataElementCategoryOptionCombo decategoryOptionCombo;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DeTargetMember()
    {
    }
    
    public DeTargetMember( DeTarget detarget,DataElement dataelements, DataElementCategoryOptionCombo decategoryOptionCombo )
    {
        this.detarget = detarget;
        this.dataelements = dataelements;
        this.decategoryOptionCombo = decategoryOptionCombo;       
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------
/*
    @Override
    
    public int hashCode()
    {
        return name.hashCode();
    }
 */   
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof DeTargetMember) )
        {
            return false;
        }

        final DeTargetMember other = ( DeTargetMember ) o;
        return detarget.equals( other.getDetarget() ) && dataelements.equals( other.getDataelements() ) 
        && decategoryOptionCombo.equals( other.getDecategoryOptionCombo() );
    }

    /*
    public String toString()
    {
        return "[" + name + "]";
    }
    */
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    


/*
    public Collection<DataElement> getDataelements()
    {
        return dataelements;
    }

    public void setDataelements( Collection<DataElement> dataelements )
    {
        this.dataelements = dataelements;
    }

    public Collection<DataElementCategoryOptionCombo> getDecategoryOptionCombo()
    {
        return decategoryOptionCombo;
    }

    public void setDecategoryOptionCombo( Collection<DataElementCategoryOptionCombo> decategoryOptionCombo )
    {
        this.decategoryOptionCombo = decategoryOptionCombo;
    }
*/
    public DeTarget getDetarget()
    {
        return detarget;
    }

    public void setDetarget( DeTarget detarget )
    {
        this.detarget = detarget;
    }
    
    
    public DataElement getDataelements()
    {
        return dataelements;
    }

    public void setDataelements( DataElement dataelements )
    {
        this.dataelements = dataelements;
    }

    public DataElementCategoryOptionCombo getDecategoryOptionCombo()
    {
        return decategoryOptionCombo;
    }

    public void setDecategoryOptionCombo( DataElementCategoryOptionCombo decategoryOptionCombo )
    {
        this.decategoryOptionCombo = decategoryOptionCombo;
    }
    
}
