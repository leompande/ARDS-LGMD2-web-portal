package org.hisp.dhis.hr;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.common.IdentifiableObject;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

@SuppressWarnings( "serial" )
public class Completeness 
	extends AbstractNameableObject
	{
	
	private int id;

    private Person	person;

    private Date timestamp;
    
    private String storedBy;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Completeness()
    {
    }
    
    public Completeness(Person person, Date timestamp, String storedBy)
    {
    	this.person = person;
    	this.timestamp = timestamp;
    	this.storedBy = storedBy;    	
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public int getId()
    {
        return id;
    }
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    public Person getPerson()
    {
        return person;
    }

    public void setPerson( Person person )
    {
        this.person = person;
    }
    
    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }
    
    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

}
