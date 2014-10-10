package org.hisp.dhis.dxf2.events.person;

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
import java.util.List;

import org.hisp.dhis.common.DxfNamespaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "person", namespace = DxfNamespaces.DXF_2_0 )
public class Person
{
    private String person;

    private String orgUnit;

    private List<Relationship> relationships = new ArrayList<Relationship>();

    private List<Identifier> identifiers = new ArrayList<Identifier>();

    private List<Attribute> attributes = new ArrayList<Attribute>();

    public Person()
    {
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public String getPerson()
    {
        return person;
    }

    public void setPerson( String person )
    {
        this.person = person;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public String getOrgUnit()
    {
        return orgUnit;
    }

    public void setOrgUnit( String orgUnit )
    {
        this.orgUnit = orgUnit;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public List<Relationship> getRelationships()
    {
        return relationships;
    }

    public void setRelationships( List<Relationship> relationships )
    {
        this.relationships = relationships;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public List<Identifier> getIdentifiers()
    {
        return identifiers;
    }

    public void setIdentifiers( List<Identifier> identifiers )
    {
        this.identifiers = identifiers;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public List<Attribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes( List<Attribute> attributes )
    {
        this.attributes = attributes;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;

        Person person1 = (Person) o;
        if ( attributes != null ? !attributes.equals( person1.attributes ) : person1.attributes != null )
            return false;
        if ( identifiers != null ? !identifiers.equals( person1.identifiers ) : person1.identifiers != null )
            return false;
        if ( orgUnit != null ? !orgUnit.equals( person1.orgUnit ) : person1.orgUnit != null )
            return false;
        if ( person != null ? !person.equals( person1.person ) : person1.person != null )
            return false;
        if ( relationships != null ? !relationships.equals( person1.relationships ) : person1.relationships != null )
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = person != null ? person.hashCode() : 0;
        result = 31 * result + (orgUnit != null ? orgUnit.hashCode() : 0);
        result = 31 * result + (relationships != null ? relationships.hashCode() : 0);
        result = 31 * result + (identifiers != null ? identifiers.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Person{" +
            "person='" + person + '\'' +
            ", orgUnit='" + orgUnit + '\'' +
            ", relationships=" + relationships +
            ", identifiers=" + identifiers +
            ", attributes=" + attributes +
            '}';
    }
}
