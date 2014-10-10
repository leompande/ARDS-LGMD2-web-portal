package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */
@Transactional
public class DefaultDataValuesService 
implements DataValuesService
{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataValuesStore dataValuesStore;

    public void setDataValuesStore( DataValuesStore dataValuesStore )
    {
        this.dataValuesStore = dataValuesStore;
    }
    
    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    }

    // -------------------------------------------------------------------------
    // DataValues
    // -------------------------------------------------------------------------

    public int saveDataValues( DataValues dataValues )
    {
        return dataValuesStore.save( dataValues );
    }

    public void updateDataValues( DataValues dataValues )
    {
    	dataValuesStore.update( dataValues );
    }

    public void deleteDataValues( DataValues dataValues )
    {
    	dataValuesStore.delete( dataValues );
    }

    public Collection<DataValues> getAllDataValues()
    {
        return dataValuesStore.getAll();
    }

    public DataValues getDataValues( int id )
    {
        return dataValuesStore.get( id );
    }
    
    public String getDataValuesByAttribute( Set<DataValues> dataValues , Attribute attribute )
    {
    	for ( DataValues dataValuess : dataValues )
        {
            if ( dataValuess.getAttribute().equals(attribute) )
            {
            	if (attribute.getInputType().getName().equals("combo"))
            	{            		
            		return attributeOptionsService.getAttributeOptions(Integer.parseInt(dataValuess.getValue())).getValue();            		
            	}
            	else
            	{
            		return dataValuess.getValue();
            	}
            }
        }
    	return "";
    	
    }

    public Collection<DataValues> getDataValuesByName( String name )
    {
    	Set<DataValues> dataValue = new HashSet<DataValues>();

        for ( DataValues dataValues : getAllDataValues() )
        {
            if ( dataValues.getValue().contains( name ) )
            {
            	dataValue.add( dataValues );
            }
        }

        return dataValue;
    }
    
    public Collection<DataValues> getDataValues( final Collection<Integer> identifiers )
    {
        Collection<DataValues> dataValues = getAllDataValues();

        return identifiers == null ? dataValues : FilterUtils.filter( dataValues, new Filter<DataValues>()
        {
            public boolean retain( DataValues dataValues )
            {
                return identifiers.contains( dataValues.getId() );
            }
        } );
    }

    public Collection<Integer> getAggregatedreportByPersonAndAttribute(Collection<Person> persons, Attribute attribute)
    {
    	return dataValuesStore.getAggregatedreportByPersonAndAttribute( persons, attribute);
    }
    
    public Collection<DataValues> getDataValuesByPerson(Person person)
    {
    	return dataValuesStore.getByPerson(person);
    }
   
    public DataValues getDataValuesByPersonAndAttribute(Person person, Attribute attribute)
    {
    	return dataValuesStore.getDataValuesByPersonAndAttribute( person, attribute);
    }
    
    public Collection<DataValues> getDatavalues(String value, Attribute attribute)
    {
    	return dataValuesStore.getDatavalues( value, attribute);
    }
}
