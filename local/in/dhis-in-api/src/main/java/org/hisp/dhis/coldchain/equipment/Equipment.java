package org.hisp.dhis.coldchain.equipment;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class Equipment implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    private int id;
    
    private EquipmentType equipmentType;
    
    private OrganisationUnit organisationUnit;
    
    private Model model;
    
    private boolean working = false;
    
    private Date registrationDate;
    
    private Set<EquipmentStatus> equipmentStatusUpdates;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public Equipment()
    {
        
    }
    
    public Equipment( EquipmentType equipmentType, OrganisationUnit organisationUnit )
    {
        this.equipmentType = equipmentType;
        this.organisationUnit = organisationUnit;
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

        if ( !(o instanceof Equipment) )
        {
            return false;
        }

        final Equipment other = (Equipment) o;

        return equipmentType.equals( other.getEquipmentType() ) && organisationUnit.equals( other.getOrganisationUnit() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + equipmentType.hashCode();
        result = result * prime + organisationUnit.hashCode();

        return result;
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

    public EquipmentType getEquipmentType()
    {
        return equipmentType;
    }

    public void setEquipmentType( EquipmentType equipmentType )
    {
        this.equipmentType = equipmentType;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public boolean isWorking()
    {
        return working;
    }

    public void setWorking( boolean working )
    {
        this.working = working;
    }

    public Set<EquipmentStatus> getEquipmentStatusUpdates()
    {
        return equipmentStatusUpdates;
    }

    public void setEquipmentStatusUpdates( Set<EquipmentStatus> equipmentStatusUpdates )
    {
        this.equipmentStatusUpdates = equipmentStatusUpdates;
    }

    public Model getModel()
    {
        return model;
    }

    public void setModel( Model model )
    {
        this.model = model;
    }

    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate( Date registrationDate )
    {
        this.registrationDate = registrationDate;
    }
    
}
