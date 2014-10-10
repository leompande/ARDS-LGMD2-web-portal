package org.hisp.dhis.dataentrystatus;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;


@SuppressWarnings("serial")
public class DataEntryStatus implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
   // private static final long serialVersionUID = 6269303850789110610L;

    //public static final String TRUE = "true";
   // public static final String FALSE = "false";
   
    /**
     * Part of the DataEntryStatus's composite ID
     */
    private String includeZero;

    /**
     * Part of the DataEntryStatus's composite ID
     */
    private DataSet dataset;
    
    /**
     * Part of the DataEntryStatus's composite ID
     */
    private OrganisationUnit organisationunit;

    /**
     * Part of the DataEntryStatus's composite ID
     */
    private Period period;
    
    private String value;
    
    private String storedBy;
    
    private Date timestamp;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataEntryStatus()
    {
    }
    
    public DataEntryStatus( DataSet dataset, OrganisationUnit organisationunit, Period period )
    {
        this.dataset = dataset;
        this.organisationunit = organisationunit;
        this.period = period;
    }
    
    public DataEntryStatus( DataSet dataset, OrganisationUnit organisationunit, Period period, String value )
    {
        this.dataset = dataset;
        this.organisationunit = organisationunit;
        this.period = period;
        this.value = value;
    }
    
    public DataEntryStatus( DataSet dataset, OrganisationUnit organisationunit, Period period, String value, String storedBy, Date timestamp )
    {
        this.dataset = dataset;
        this.organisationunit = organisationunit;
        this.period = period;
        this.value = value;
        this.storedBy = storedBy;
        this.timestamp = timestamp;
    }
    
    // -------------------------------------------------------------------------
    // Dimension
    // -------------------------------------------------------------------------
/*  
    public String getMeasure()
    {
        return value;
    }
    
    public String getName()
    {
        throw new UnsupportedOperationException();
    }
*/   
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

   // public boolean isIncludeZero()
   //{
      //  return includeZero != null && includeZero;
    //}
    
    public boolean isZero()
    {
        return dataset != null  && value != null && new Double( value ).intValue() == 0;
    }
    
    public boolean isNullValue()
    {
        return value == null;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

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

        if ( !(o instanceof DataEntryStatus ) )
        {
            return false;
        }

        final DataEntryStatus other = (DataEntryStatus) o;

        return dataset.equals( other.getDataset() ) && organisationunit.equals( other.getOrganisationunit()) && period.equals( other.getPeriod() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + dataset.hashCode();
        result = result * prime + organisationunit.hashCode();
        result = result * prime + period.hashCode();
        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public DataSet getDataset()
    {
        return dataset;
    }

    public void setDataset( DataSet dataset )
    {
        this.dataset = dataset;
    }
    
    public OrganisationUnit getOrganisationunit()
    {
        return organisationunit;
    }

    public void setOrganisationunit( OrganisationUnit organisationunit )
    {
        this.organisationunit = organisationunit;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public String getIncludeZero()
    {
        return includeZero;
    }

    public void setIncludeZero( String includeZero )
    {
        this.includeZero = includeZero;
    }
}
