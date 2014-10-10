package org.hisp.dhis.pbf.api;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class PBFDataValue implements Serializable 
{
    private OrganisationUnit organisationUnit;
    
    private DataElement dataElement;
    
    private DataSet dataSet;

    private Period period;

    private Integer quantityReported;
    
    private Integer quantityValidated;
    
    private Double tariffAmount;
    
    private String storedBy;

    private Date timestamp;

    private String comment;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public PBFDataValue()
    {
        
    }
    
    public PBFDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Period period, Integer quantityReported, Integer quantityValidated, Double tariffAmount )
    {
        this.organisationUnit = organisationUnit;
        this.dataElement = dataElement;
        this.dataSet = dataSet;
        this.period = period;
        this.quantityReported = quantityReported;
        this.quantityValidated = quantityValidated;
        this.tariffAmount = tariffAmount;
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

        if ( !(o instanceof PBFDataValue) )
        {
            return false;
        }

        final PBFDataValue other = (PBFDataValue) o;

        return dataElement.equals( other.getDataElement() ) && dataSet.equals( other.getDataSet() ) && organisationUnit.equals( other.getOrganisationUnit() ) && period.equals( other.getPeriod() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + dataSet.hashCode();
        result = result * prime + dataElement.hashCode();
        result = result * prime + organisationUnit.hashCode();
        result = result * prime + period.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

	public OrganisationUnit getOrganisationUnit() 
	{
		return organisationUnit;
	}

	public void setOrganisationUnit(OrganisationUnit organisationUnit) 
	{
		this.organisationUnit = organisationUnit;
	}

	public DataElement getDataElement() 
	{
		return dataElement;
	}

	public void setDataElement(DataElement dataElement) 
	{
		this.dataElement = dataElement;
	}

	public DataSet getDataSet() 
	{
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) 
	{
		this.dataSet = dataSet;
	}

	public Period getPeriod() 
	{
		return period;
	}

	public void setPeriod(Period period) 
	{
		this.period = period;
	}

	public Integer getQuantityReported() 
	{
		return quantityReported;
	}

	public void setQuantityReported(Integer quantityReported) 
	{
		this.quantityReported = quantityReported;
	}

	public Integer getQuantityValidated() 
	{
		return quantityValidated;
	}

	public void setQuantityValidated(Integer quantityValidated) 
	{
		this.quantityValidated = quantityValidated;
	}

	public Double getTariffAmount() 
	{
		return tariffAmount;
	}

	public void setTariffAmount(Double tariffAmount) 
	{
		this.tariffAmount = tariffAmount;
	}

	public String getStoredBy() 
	{
		return storedBy;
	}

	public void setStoredBy(String storedBy) 
	{
		this.storedBy = storedBy;
	}

	public Date getTimestamp() 
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp) 
	{
		this.timestamp = timestamp;
	}

	public String getComment() 
	{
		return comment;
	}

	public void setComment(String comment) 
	{
		this.comment = comment;
	}
}
