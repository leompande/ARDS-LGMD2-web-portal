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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DeTarget.java Jan 11, 2011 4:13:29 PM
 */
@SuppressWarnings("serial")
public class DeTarget implements Serializable
{
    /**
     * The unique identifier for this DataElement Target.
     */
    private int id;

    /**
     * The name of this DataElement Target. Required and unique.
     */
    private String name;

    /**
     * Short name of DataElement Target. Required and unique.
     */
    private String shortName;
       
    /**
     * All DataElements associated with this DataElement Target.
     */
    private Collection<DataElement> dataelements = new HashSet<DataElement>();
    
    /**
     * All DataElement Category Option Combo associated with this DataElement Target.
     */
    private Collection<DataElementCategoryOptionCombo> decategoryOptionCombo = new HashSet<DataElementCategoryOptionCombo>();
    
    
    /**
     * All Sources that register data with this DataElement Target.
     */
    private Set<OrganisationUnit> sources = new HashSet<OrganisationUnit>();
    
    /**
     * Description of this DataElement Target.
     */
    private String description;
      
    /**
     * URL for lookup of additional information of DataElement Target on the web.
     */
    private String url;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DeTarget()
    {
    }
    
    public DeTarget( String name, String shortName )
    {
        this.name = name;
        this.shortName = shortName;       
    }
    
    public DeTarget( String name, String shortName, String url, String description )
    {
        this.name = name;
        this.shortName = shortName;       
        this.url = url;
        this.description = description;
    }
     
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

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

        if ( !(o instanceof DeTarget) )
        {
            return false;
        }

        final DeTarget other = (DeTarget) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }
    

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addOrganisationUnit( OrganisationUnit unit )
    {
        sources.add( unit );
    }
    
    public void removeOrganisationUnit( OrganisationUnit unit )
    {
        sources.remove( unit );
    }
    
    public void updateOrganisationUnits( Set<OrganisationUnit> updates )
    {
        for ( OrganisationUnit unit : new HashSet<OrganisationUnit>( sources ) )
        {
            if ( !updates.contains( unit ) )
            {
                removeOrganisationUnit( unit );
            }
        }
        
        for ( OrganisationUnit unit : updates )
        {
            addOrganisationUnit( unit );
        }
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

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

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

    public Set<OrganisationUnit> getSources()
    {
        return sources;
    }

    public void setSources( Set<OrganisationUnit> sources )
    {
        this.sources = sources;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }
    
}
