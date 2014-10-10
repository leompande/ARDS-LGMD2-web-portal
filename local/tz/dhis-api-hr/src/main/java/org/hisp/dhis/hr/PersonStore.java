package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;


import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface PersonStore 
	extends GenericStore<Person>
{
	String ID = PersonStore.class.getName();
	
	int getPersonByMaxId();
	
    Collection<Person> getByGender( String gender );

    Collection<Person> getByBirthDate( Date birthDate );

    Collection<Person> getByNames( String name );
    
    Person getPersonByInstance( String instance );
    
    Collection<Person> getPerson( String firstName, String middleName, String lastName, Date birthdate);
    
    Collection<Person> getPersonByNames( String name, int min, int max );
    
    int getCountPersonByDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit organisationUnit);
    
    int getCountPersonByNameDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit organisationUnit, String key);
    
    Collection<Person> getPersonByDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit organisationUnit);
    
    Collection<Person> getPersonByDatasetAndOrganisationBetween(HrDataSet dataSet, OrganisationUnit unit, int startPos, int pageSize);
    
    Collection<Person> getPersonByNameDatasetAndOrganisationBetween(HrDataSet dataSet, OrganisationUnit unit, int startPos, int pageSize, String key);
    
    int getCountPersonByDatasetAndOrganisation(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit);
    
    int getCountPersonByNameDatasetAndOrganisation(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit, String key);
    
    Collection<Person> getPersonByDatasetAndOrganisation(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit);

    Collection<Person> getPersonByDatasetAndOrganisationBetween(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit, int startPos, int pageSize);
    
    Collection<Person> getPersonByNameDatasetAndOrganisationBetween(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit, int startPos, int pageSize, String key);

    Collection<Person> getPersonByDatasetsAndOrganisation(Collection<HrDataSet> dataSets, OrganisationUnit organisationUnit);
    
    Collection<Person> getPersonByDatasetsAndOrganisation(Collection<HrDataSet> dataSet, Collection<OrganisationUnit> organisationUnit);
    
    Collection<AggregateOperands> getAggregatedPersonByAttributeDatasetandOrganisation(HrDataSet dataSet, Attribute attribute, OrganisationUnit organisationUnit, boolean selectedUnitOnly);

    int countGetPersonByNames( String name );
    

}
