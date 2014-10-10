package org.hisp.dhis.hr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.system.grid.ListGrid;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */
@Transactional
public class DefaultPersonService
implements PersonService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PersonStore personStore;

    public void setPersonStore( PersonStore personStore )
    {
        this.personStore = personStore;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private DataValuesService dataValuesService;

    public void setDataValuesService( DataValuesService dataValuesService )
    {
        this.dataValuesService = dataValuesService;
    }

    // -------------------------------------------------------------------------
    // Person
    // -------------------------------------------------------------------------

    public int savePerson( Person person )
    {
        return personStore.save( person );
    }

    public void updatePerson( Person person )
    {
    	personStore.update( person );
    }

    public void deletePerson( Person person )
    {
    	personStore.delete( person );
    }

    public Collection<Person> getAllPerson()
    {
        return personStore.getAll();
    }

    public Person getPerson( int id )
    {
        return personStore.get( id );
    }
    
    public Person getPersonByInstance( String instance )
    {
    	return personStore.getPersonByInstance( instance );
    }
    
    public int getPersonByMaxId()
    {
    	return personStore.getPersonByMaxId();
    }
    
    public int getCountPersonByDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit organisationUnit, boolean selectedUnitOnly) {
		
		if(selectedUnitOnly){	
			
			return personStore.getCountPersonByDatasetAndOrganisation(dataSet, organisationUnit);
			
		}else{
			
			Collection<OrganisationUnit> organisationUnitWithChildren = organisationUnitService.getOrganisationUnitWithChildren(organisationUnit.getId());
	        
			return personStore.getCountPersonByDatasetAndOrganisation(dataSet, organisationUnitWithChildren);
		}
	}
    
    public int getCountPersonByNameDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit organisationUnit, boolean selectedUnitOnly, String key) {
		
		if(selectedUnitOnly){	
			
			return personStore.getCountPersonByNameDatasetAndOrganisation(dataSet, organisationUnit, key);
			
		}else{
			
			Collection<OrganisationUnit> organisationUnitWithChildren = organisationUnitService.getOrganisationUnitWithChildren(organisationUnit.getId());
	        
			return personStore.getCountPersonByNameDatasetAndOrganisation(dataSet, organisationUnitWithChildren, key);
		}
	}

	public Collection<Person> getPersonByDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit organisationUnit, boolean selectedUnitOnly) {
		
		if(selectedUnitOnly){		
			
			return personStore.getPersonByDatasetAndOrganisation(dataSet, organisationUnit);
			
		}else{
			
			Collection<OrganisationUnit> organisationUnitWithChildren = organisationUnitService.getOrganisationUnitWithChildren(organisationUnit.getId());
	        
			return personStore.getPersonByDatasetAndOrganisation(dataSet, organisationUnitWithChildren);
		}
	}

	public Collection<Person> getPersonByDatasetAndOrganisationBetween(HrDataSet dataSet, OrganisationUnit organisationUnit, boolean selectedUnitOnly, int startPos, int pageSize) {
		
		if(selectedUnitOnly){	
			
			return personStore.getPersonByDatasetAndOrganisationBetween(dataSet, organisationUnit, startPos, pageSize);
			
		}else{
			
			Collection<OrganisationUnit> organisationUnitWithChildren = organisationUnitService.getOrganisationUnitWithChildren(organisationUnit.getId());
	        
			return personStore.getPersonByDatasetAndOrganisationBetween(dataSet, organisationUnitWithChildren, startPos, pageSize);
		}
	}
	
	public Collection<Person> getPersonByNameDatasetAndOrganisationBetween(HrDataSet dataSet, OrganisationUnit organisationUnit, boolean selectedUnitOnly, int startPos, int pageSize, String key) {
		
		if(selectedUnitOnly){	
			
			return personStore.getPersonByNameDatasetAndOrganisationBetween(dataSet, organisationUnit, startPos, pageSize, key);
			
		}else{
			
			Collection<OrganisationUnit> organisationUnitWithChildren = organisationUnitService.getOrganisationUnitWithChildren(organisationUnit.getId());
	        
			return personStore.getPersonByNameDatasetAndOrganisationBetween(dataSet, organisationUnitWithChildren, startPos, pageSize, key);
		}
	}

	public Collection<Person> getPersonByDatasetsAndOrganisation( Collection<HrDataSet> dataSets, OrganisationUnit organisationUnit, boolean selectedUnitOnly) {
		
		if(selectedUnitOnly){
			return personStore.getPersonByDatasetsAndOrganisation(dataSets, organisationUnit);
		}else{
			
			Collection<OrganisationUnit> organisationUnitWithChildren = organisationUnitService.getOrganisationUnitWithChildren(organisationUnit.getId());
	        
			return personStore.getPersonByDatasetsAndOrganisation(dataSets, organisationUnitWithChildren);
		}
	}
	
	public Collection<Person> getPersons( final Collection<Integer> identifiers )
    {
        Collection<Person> persons = getAllPerson();

        return identifiers == null ? persons : FilterUtils.filter( persons, new Filter<Person>()
        {
            public boolean retain( Person persons )
            {
                return identifiers.contains( persons.getId() );
            }
        } );
    }

	public Grid getGrid( Collection<Person> person , String reportingUnit, HrDataSet hrDataSet) 
	{
		
		Grid grid = new ListGrid().setTitle( reportingUnit ).setSubtitle( hrDataSet.getName() );
		
		// ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------
		
		Person persons = new Person();
		
		ArrayList<String> personFields = new ArrayList<String>();
		
		personFields.add( persons.getFirstNameColumn() );
		personFields.add( persons.getMiddleNameColumn() );
		personFields.add( persons.getLastNameColumn() );
		personFields.add( persons.getBirthDateColumn() );
		personFields.add( persons.getGenderColumn() );
		personFields.add( persons.getNationalityColumn() );
		
		for (String personField:personFields)
		{			
			grid.addHeader( new GridHeader( personField,false, true ) );
		}
		
		for ( Attribute attribute : hrDataSet.getAttribute() )
		{
			
			if(!personFields.contains(attribute.getCaption()))
			{
				grid.addHeader( new GridHeader( attribute.getCaption(),false, true ) ); 
			}
						
		}
		
		grid.addHeader( new GridHeader( "OrganisationUnit",false, true ) ); 
		
		// ---------------------------------------------------------------------
        // Values
        // ---------------------------------------------------------------------
		
		for ( Person tempPerson : person )
		{
			grid.addRow();
			
			grid.addValue( tempPerson.getFirstName() );
			grid.addValue( tempPerson.getMiddleName() );
			grid.addValue( tempPerson.getLastName() );
			grid.addValue( tempPerson.getBirthDate() );
			grid.addValue( tempPerson.getGender() );
			grid.addValue( tempPerson.getNationality() );
			Set<DataValues> dataValues = tempPerson.getDataValues();
			
			for ( Attribute attributes : hrDataSet.getAttribute() )
			{
				if(!personFields.contains(attributes.getCaption()))
				{
					grid.addValue( dataValuesService.getDataValuesByAttribute(dataValues, attributes) );
				}	
            }
			
			grid.addValue( tempPerson.getOrganisationUnit().getName() );		
			
		}			
			
		return grid;
			
	}
	
	@SuppressWarnings("unchecked")
	public Collection<AggregateOperands> getAggregatedPersonByAttributeDatasetandOrganisation(HrDataSet dataSet, Attribute attribute, OrganisationUnit organisationUnit, boolean selectedUnitOnly)
	{	
	
		return personStore.getAggregatedPersonByAttributeDatasetandOrganisation(dataSet, attribute, organisationUnit, selectedUnitOnly);
								
	}
}
