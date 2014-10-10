package org.hisp.dhis.reportsheet.status;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

public class DataEntryStatus
{
    private int id;

    private DataSet dataSet;

    private PeriodType periodType;

    private boolean makeDefault;

    private int numberOfDataElement;

    private int numberOfDataValue;

    private Period period;

    private boolean completed;

    public DataEntryStatus()
    {
    }

    public int getCompletedPercent()
    {
        return (int) ((new Double( numberOfDataValue ) / new Double( numberOfDataElement )) * 100);
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    public boolean isMakeDefault()
    {
        return makeDefault;
    }

    public void setMakeDefault( boolean makeDefault )
    {
        this.makeDefault = makeDefault;
    }

    public int getNumberOfDataElement()
    {
        return numberOfDataElement;
    }

    public void setNumberOfDataElement( int numberOfDataElement )
    {
        this.numberOfDataElement = numberOfDataElement;
    }

    public int getNumberOfDataValue()
    {
        return numberOfDataValue;
    }

    public void setNumberOfDataValue( int numberOfDataValue )
    {
        this.numberOfDataValue = numberOfDataValue;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals( Object obj ) // TODO check on better candidates
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        DataEntryStatus other = (DataEntryStatus) obj;
        if ( id != other.id )
            return false;
        return true;
    }
}
