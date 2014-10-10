package org.hisp.dhis.dxf2.events.event;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.hisp.dhis.common.BaseLinkableObject;
import org.hisp.dhis.common.DxfNamespaces;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "event", namespace = DxfNamespaces.DXF_2_0 )
public class Event extends BaseLinkableObject
{
    private String event;

    private EventStatus status = EventStatus.ACTIVE;

    private String program;

    private String programStage;

    private String orgUnit;

    private String person;

    private String eventDate;

    private String storedBy;

    private Coordinate coordinate;

    private List<DataValue> dataValues = new ArrayList<DataValue>();
    
    private List<Note> notes = new ArrayList<Note>();

    public Event()
    {
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public String getEvent()
    {
        return event;
    }

    public void setEvent( String event )
    {
        this.event = event;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public EventStatus getStatus()
    {
        return status;
    }

    public void setStatus( EventStatus status )
    {
        this.status = status;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public String getProgram()
    {
        return program;
    }

    public void setProgram( String program )
    {
        this.program = program;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public String getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( String programStage )
    {
        this.programStage = programStage;
    }

    @JsonProperty
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
    public String getEventDate()
    {
        return eventDate;
    }

    public void setEventDate( String eventDate )
    {
        this.eventDate = eventDate;
    }

    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    public void setCoordinate( Coordinate coordinate )
    {
        this.coordinate = coordinate;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataValues", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "dataValue", namespace = DxfNamespaces.DXF_2_0 )
    public List<DataValue> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( List<DataValue> dataValues )
    {
        this.dataValues = dataValues;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "notes", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "note", namespace = DxfNamespaces.DXF_2_0 )
    public List<Note> getNotes()
    {
        return notes;
    }

    public void setNotes( List<Note> notes )
    {
        this.notes = notes;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Event event1 = (Event) o;

        if ( coordinate != null ? !coordinate.equals( event1.coordinate ) : event1.coordinate != null ) return false;
        if ( dataValues != null ? !dataValues.equals( event1.dataValues ) : event1.dataValues != null ) return false;
        if ( event != null ? !event.equals( event1.event ) : event1.event != null ) return false;
        if ( eventDate != null ? !eventDate.equals( event1.eventDate ) : event1.eventDate != null ) return false;
        if ( orgUnit != null ? !orgUnit.equals( event1.orgUnit ) : event1.orgUnit != null ) return false;
        if ( person != null ? !person.equals( event1.person ) : event1.person != null ) return false;
        if ( program != null ? !program.equals( event1.program ) : event1.program != null ) return false;
        if ( programStage != null ? !programStage.equals( event1.programStage ) : event1.programStage != null ) return false;
        if ( status != event1.status ) return false;
        if ( storedBy != null ? !storedBy.equals( event1.storedBy ) : event1.storedBy != null ) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = event != null ? event.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (program != null ? program.hashCode() : 0);
        result = 31 * result + (programStage != null ? programStage.hashCode() : 0);
        result = 31 * result + (orgUnit != null ? orgUnit.hashCode() : 0);
        result = 31 * result + (person != null ? person.hashCode() : 0);
        result = 31 * result + (eventDate != null ? eventDate.hashCode() : 0);
        result = 31 * result + (storedBy != null ? storedBy.hashCode() : 0);
        result = 31 * result + (coordinate != null ? coordinate.hashCode() : 0);
        result = 31 * result + (dataValues != null ? dataValues.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Event{" +
            "event='" + event + '\'' +
            ", status=" + status +
            ", program='" + program + '\'' +
            ", programStage='" + programStage + '\'' +
            ", orgUnit='" + orgUnit + '\'' +
            ", person='" + person + '\'' +
            ", eventDate='" + eventDate + '\'' +
            ", storedBy='" + storedBy + '\'' +
            ", coordinate=" + coordinate +
            ", dataValues=" + dataValues +
            '}';
    }
}
