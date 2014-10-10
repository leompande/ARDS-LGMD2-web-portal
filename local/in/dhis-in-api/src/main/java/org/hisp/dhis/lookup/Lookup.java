package org.hisp.dhis.lookup;

import org.hisp.dhis.common.BaseNameableObject;

public class Lookup extends BaseNameableObject
{
    public static final String DS_PBF_TYPE = "DS_PBF_TYPE";
    
    public static final String BANK = "BANK";
    
    public static final String CCEI_AGG_TYPE = "CCEI_AGG_TYPE";
    
    public static final String WS_REF_TYPE = "WS_REF_TYPE";
    
    public static final String CCEI_AGG_TYPE_STORAGE_CAPACITY = "STORAGE CAPACITY";
    public static final String CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_MODEL = "REF WORKING STATUS BY MODEL";
    public static final String CCEI_AGG_TYPE_REF_UTILIZATION = "REF UTILIZATION";
    public static final String CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_TYPE = "REF WORKING STATUS BY TYPE";
    public static final String CCEI_AGG_TYPE_REF_TEMP_ALARMS = "REF TEMP ALARMS";
    
    public static final String CCEI_WORKING_STATUS_OPTIONSET = "WORKING_STATUS_OPTIONSET";
    
    public static final String CCEI_EQUIPMENTTYPE_MODELTYPEATTRIBUTE = "EQUIPMENTTYPE_MODELTYPEATTRIBUTE";    
    public static final String CCEI_MODEL_MODELTYPEATTRIBUTE = "MODEL_MODELTYPEATTRIBUTE";
    
    public static final String CCEI_UTILIZATION_OPTIONSET = "UTILIZATION_OPTIONSET";
    
    public static final String CCEI_REF_EQUIPMENTTYPE_ID = "REF_EQUIPMENTTYPE_ID";
    
    public static final String ORGUNITID_BY_COMMA = "ORGUNITID_BY_COMMA";
    public static final String CURRENT_PERIOD_ENDDATE = "CURRENT_PERIOD_ENDDATE";
    
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
