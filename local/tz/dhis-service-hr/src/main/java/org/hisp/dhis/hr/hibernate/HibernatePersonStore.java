package org.hisp.dhis.hr.hibernate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.amplecode.quick.StatementManager;
import org.amplecode.quick.mapper.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.AggregateOperands;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.hr.AggregatedReportMapper;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernatePersonStore 
extends HibernateGenericStore<Person>
implements PersonStore{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    // -------------------------------------------------------------------------
    // Person
    // -------------------------------------------------------------------------

		
		public int getPersonByMaxId()
	    {
			Criteria criteria = getCriteria();
			criteria.setProjection(Projections.max("id"));
			Number rs = (Integer) criteria.uniqueResult();
			return rs != null ? rs.intValue() : 0;
	    }
		
	  @SuppressWarnings( "unchecked" )
	    public Collection<Person> getByGender( String gender )
	    {
	        return getCriteria( Restrictions.eq( "gender", gender ) ).list();
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getByBirthDate( Date birthDate )
	    {
	        return getCriteria( Restrictions.eq( "birthdate", birthDate ) ).list();
	    }

	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getByNames( String name )
	    {
	        return getCriteria( 
	            Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + name + "%" ) ).add(
	            Restrictions.ilike( "middleName", "%" + name + "%" ) ).add(
	            Restrictions.ilike( "lastName", "%" + name + "%" ) ) ).addOrder( Order.asc( "firstName" ) ).list();        
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPerson( String firstName, String middleName, String lastName, Date birthdate){
	    	  Criteria crit = getCriteria( );
	          Conjunction con = Restrictions.conjunction();
	          
	          if( StringUtils.isNotBlank( firstName ))
	              con.add( Restrictions.eq( "firstName", firstName ) );
	          
	          if( StringUtils.isNotBlank( middleName ))
	              con.add(Restrictions.eq( "middleName", middleName ) );
	          
	          if( StringUtils.isNotBlank( lastName ))
	              con.add(Restrictions.eq( "lastName",  lastName ) );
	          
	          con.add( Restrictions.eq( "birthdate",  birthdate ) );
	          
	          crit.add( con );
	          
	          crit.addOrder( Order.asc( "firstName" ) );   
	          
	          return crit.list();
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPersonByNames( String name, int min, int max ){
	    	
	    	return getCriteria( 
	                Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + name + "%" ) ).add(
	                    Restrictions.ilike( "middleName", "%" + name + "%" ) ).add(
	                    Restrictions.ilike( "lastName", "%" + name + "%" ) ) ).addOrder( Order.asc( "firstName" ) ).setFirstResult( min ).setMaxResults( max ).list();  
	    }
	    
	    public int countGetPersonByNames( String name ){
	    	
	    	Number rs =  (Number)getCriteria( 
    	            Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + name + "%" ) ).add(
    	                Restrictions.ilike( "middleName", "%" + name + "%" ) ).add(
    	                Restrictions.ilike( "lastName", "%" + name + "%" ) ) ).addOrder( Order.asc( "firstName" ) ).setProjection( Projections.rowCount() ).uniqueResult();
    	        return rs != null ? rs.intValue() : 0;
	    }
	    
	    public int getCountPersonByDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit unit){
	    		    	
	    	Criteria criteria = getCriteria();
	        criteria.setProjection( Projections.rowCount() );
	        criteria.add( Restrictions.eq( "dataset",dataSet  ));    
	        criteria.add( Restrictions.eq("organisationUnit", unit )); 
	        return ((Number) criteria.uniqueResult()).intValue();
	    }
	    
	    public int getCountPersonByNameDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit unit, String key){
	    	
	    	Criteria criteria = getCriteria();
	        criteria.setProjection( Projections.rowCount() );
	        criteria.add( Restrictions.eq( "dataset",dataSet  ));    
	        criteria.add( Restrictions.eq("organisationUnit", unit ));
	        criteria.add( Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + key + "%" ) ).add(
	        Restrictions.ilike( "middleName", "%" + key + "%" ) ).add(
	    	Restrictions.ilike( "lastName", "%" + key + "%" ) ) );
	        return ((Number) criteria.uniqueResult()).intValue();
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPersonByDatasetAndOrganisation(HrDataSet dataSet, OrganisationUnit unit){
	    	
	    	return getCriteria( 
		            Restrictions.conjunction().add( Restrictions.eq( "dataset",dataSet  ) ).add(
		            Restrictions.eq("organisationUnit", unit ) ) ).addOrder( Order.asc( "firstName" ) ).list(); 
	    	
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPersonByDatasetAndOrganisationBetween(HrDataSet dataSet, OrganisationUnit unit, int startPos, int pageSize){
	    	
	    	Criteria criteria = getCriteria();
	    	criteria.add( Restrictions.eq( "dataset",dataSet  ) );
	    	criteria.add( Restrictions.eq( "organisationUnit",unit  ) );
	    	criteria.addOrder( Order.asc( "firstName" ) );
	        criteria.setFirstResult( startPos );
	        criteria.setMaxResults( pageSize );
	        return criteria.list();	
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPersonByNameDatasetAndOrganisationBetween(HrDataSet dataSet, OrganisationUnit unit, int startPos, int pageSize, String key){
	    	
	    	Criteria criteria = getCriteria();
	    	criteria.add( Restrictions.eq( "dataset",dataSet  ) );
	    	criteria.add( Restrictions.eq( "organisationUnit",unit  ) );
	    	criteria.add( Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + key + "%" ) ).add(
	        Restrictions.ilike( "middleName", "%" + key + "%" ) ).add(
	    	Restrictions.ilike( "lastName", "%" + key + "%" ) ) );
	    	criteria.addOrder( Order.asc( "firstName" ) );
	        criteria.setFirstResult( startPos );
	        criteria.setMaxResults( pageSize );
	        return criteria.list();	
	    }
	    
	    public int getCountPersonByDatasetAndOrganisation(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit){
	    
	    	Criteria criteria = getCriteria();
	        criteria.setProjection( Projections.rowCount() );
	        criteria.add( Restrictions.eq( "dataset",dataSet  ));    
	        criteria.add( Restrictions.in("organisationUnit", organisationUnit )); 
	        return ((Number) criteria.uniqueResult()).intValue();
	    	
	    }
	    
	    public int getCountPersonByNameDatasetAndOrganisation(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit, String key){
		    
	    	Criteria criteria = getCriteria();
	        criteria.setProjection( Projections.rowCount() );
	        criteria.add( Restrictions.eq( "dataset",dataSet  ));    
	        criteria.add( Restrictions.in("organisationUnit", organisationUnit )); 
	        criteria.add( Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + key + "%" ) ).add(
	        Restrictions.ilike( "middleName", "%" + key + "%" ) ).add(
	    	Restrictions.ilike( "lastName", "%" + key + "%" ) ) );
	        return ((Number) criteria.uniqueResult()).intValue();
	    	
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPersonByDatasetAndOrganisation(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit){
	    	
	    	return getCriteria( 
		            Restrictions.conjunction().add( Restrictions.eq( "dataset",dataSet  ) ).add(
		            Restrictions.in("organisationUnit", organisationUnit ) ) ).addOrder( Order.asc( "firstName" ) ).list(); 
	    	
	    }

	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPersonByDatasetAndOrganisationBetween(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit, int startPos, int pageSize){
	    	
	    	Criteria criteria = getCriteria();
	    	criteria.add( Restrictions.eq( "dataset",dataSet  ) );
	    	criteria.add( Restrictions.in( "organisationUnit",organisationUnit  ) );
	    	criteria.addOrder( Order.asc( "firstName" ) );
	        criteria.setFirstResult( startPos );
	        criteria.setMaxResults( pageSize );
	        return criteria.list();	    		
	    	
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPersonByNameDatasetAndOrganisationBetween(HrDataSet dataSet, Collection<OrganisationUnit> organisationUnit, int startPos, int pageSize, String key){
	    	
	    	Criteria criteria = getCriteria();
	    	criteria.add( Restrictions.eq( "dataset",dataSet  ) );
	    	criteria.add( Restrictions.in( "organisationUnit",organisationUnit  ) );
	    	criteria.add( Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + key + "%" ) ).add(
	        Restrictions.ilike( "middleName", "%" + key + "%" ) ).add(
	    	Restrictions.ilike( "lastName", "%" + key + "%" ) ) );
	    	criteria.addOrder( Order.asc( "firstName" ) );
	        criteria.setFirstResult( startPos );
	        criteria.setMaxResults( pageSize );
	        return criteria.list();	    		
	    	
	    }

	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPersonByDatasetsAndOrganisation(Collection<HrDataSet> dataSets, Collection<OrganisationUnit> organisationUnit){
	    	
	    	return getCriteria( 
		            Restrictions.conjunction().add( Restrictions.in( "dataset",dataSets  ) ).add(
		            Restrictions.in("organisationUnit", organisationUnit ) ) ).addOrder( Order.asc( "firstName" ) ).list(); 
	    	
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<Person> getPersonByDatasetsAndOrganisation(Collection<HrDataSet> dataSets, OrganisationUnit organisationUnit){
	    	
	    	return getCriteria( 
		            Restrictions.conjunction().add( Restrictions.in( "dataset",dataSets  ) ).add(
		            Restrictions.eq("organisationUnit", organisationUnit ) ) ).addOrder( Order.asc( "firstName" ) ).list(); 
	    	
	    }
	    
	    @SuppressWarnings( "unchecked" )
	    public Person getPersonByInstance(String instance) {
	    	return (Person) getCriteria( Restrictions.eq( "instance" , instance ) ).uniqueResult();
	    }	
	    
	    @SuppressWarnings( "unchecked" )
	    public Collection<AggregateOperands> getAggregatedPersonByAttributeDatasetandOrganisation(HrDataSet dataSet, Attribute attribute, OrganisationUnit organisationUnit, boolean selectedUnitOnly) {
	    	
	    	final ObjectMapper<AggregateOperands> mapper = new ObjectMapper<AggregateOperands>();
	    	
	    	Person person = new Person();
	    	
	    	String sql, groupByColomn;
	    	
	    	if(attribute.getCaption().equalsIgnoreCase(person.getGenderColumn())){
	    		
	    		sql = "SELECT person.gender as Value, count(person.gender) AS total "
	    		+ "FROM hr_person as person "
	    		+ " WHERE person.datasetid=" + dataSet.getId();
	    		
	    		groupByColomn = "person.gender";
	    			    		
	    	}else if(attribute.getCaption().equalsIgnoreCase(person.getNationalityColumn())){
	    		
	    		sql = "SELECT person.nationality as Value, count(person.nationality) AS total "
		    		+ "FROM hr_person as person "
		    		+ " WHERE person.datasetid=" + dataSet.getId();
	    		
	    		groupByColomn = "person.nationality";
	    		
	    	}else{
	    	
		    	sql = "SELECT V.value as Value, count(V.value) AS total "
		    	+ "FROM hr_datavalues as V "
		    	+ "JOIN hr_person as person on person.personid=V.personid"
		    	+ " WHERE V.attributeid=" + attribute.getId()
		    	+ " AND person.datasetid=" + dataSet.getId();
		    	
		    	groupByColomn = "V.Value";
	    	}
	    	
	    	if(selectedUnitOnly){
	    		
	    		sql += " AND person.organisationunitid =" + organisationUnit.getId();
	    		
	    	}else{
	    		
	    		String organisationUnitId = "";
	    		for (OrganisationUnit organisationUnitWithChildren : organisationUnitService.getOrganisationUnitWithChildren(organisationUnit.getId()))
	    		{
	    			organisationUnitId += organisationUnitWithChildren.getId() + ",";
	    		}
	    		int end = organisationUnitId.length() - 1;
	    		
	    		sql += " AND person.organisationunitid IN (" + organisationUnitId.substring(0, end) + ")";
	    	
	    	}
	    	
	    	sql += " GROUP BY " + groupByColomn;
	    	
	    	try
	        {
	            ResultSet resultSet = statementManager.getHolder().getStatement().executeQuery( sql );

	            return mapper.getCollection( resultSet, new AggregatedReportMapper() );
	        }
	        catch ( SQLException ex )
	        {
	            throw new RuntimeException( "Failed to get all operands", ex );
	        }
	    }	    
	    
}
