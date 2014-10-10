package org.hisp.dhis.pbf.api;

import org.hisp.dhis.common.BaseNameableObject;

public class Lookup extends BaseNameableObject
{
    public static final String DS_PBF_TYPE = "DS_PBF_TYPE";
    
    public static final String OC_TARIFF = "OC_TARIFF";
    
    public static final String QV_TARIFF = "QV_TARIFF"; 
    
    
    public static final String BANK = "BANK";
    
    private String type;
    
    private String value;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public Lookup()
    {
        
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals
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

        if ( !(o instanceof Lookup) )
        {
            return false;
        }

        final Lookup other = (Lookup) o;

        return name.equals( other.getName() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }


    
}
