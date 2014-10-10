package org.hisp.dhis.hr.hibernate;


import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeStore;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.Criteria;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class HibernateAggregateAttributeStore 
	extends HibernateGenericStore<AggregateAttribute> 
	implements AggregateAttributeStore {
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    // -------------------------------------------------------------------------
    // AggregateAttribute
    // -------------------------------------------------------------------------
    
    @SuppressWarnings( "unchecked" )
    public AggregateAttribute getAggregateAttributeByName(String name){
    	
    	return (AggregateAttribute) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
    	
    }
    
	@SuppressWarnings( "unchecked" )
    public int getCountPersonByAggregateAttribute(Collection<AttributeOptions> attributeOptions, Collection<Criteria> criteria, int organisationUnitId){
    	
    	String sql;
    	String countSql = " SELECT count(*) as total FROM hr_datavalues as V JOIN hr_person as person on person.personid = V.personid  WHERE ( ";
    	String personSql = " SELECT person.personid FROM hr_datavalues as V JOIN hr_person as person on person.personid = V.personid  WHERE (";
    	
    	Attribute attribute = new Attribute();
    	
    	StatementHolder holder = statementManager.getHolder();
    	
    	//Deal with the criterias first
    	
    	sql = countSql;
    	
    	for(Criteria criterias:criteria){
    		
    		for(AttributeOptions attributeOption:criterias.getAttributeOptions()){
    			
    			sql += " v.value='" + attributeOption.getId() +"'";
    			sql += " OR";
    			attribute = attributeOption.getAttribute();
    		}
    		
    		sql = sql.substring(0, sql.length()-2);
        	
        	sql += ") AND V.attributeid=" + attribute.getId();
        	
        	sql += " AND person.personid in ( ";
        	
        	sql += personSql;    		
    	}    	
    	
    	//Deal with the attributeOption second
    	
    	for(AttributeOptions attributeOption : attributeOptions){
    		
    		sql += " V.value='" + attributeOption.getId() +"'";
    		sql += " OR";
    		attribute = attributeOption.getAttribute();
    	}
    	
    	sql = sql.substring(0, sql.length()-2);
    	
    	sql += ") AND V.attributeid=" + attribute.getId();
    	
    	sql += " AND person.organisationunitid="+organisationUnitId;
    	
    	for(Criteria criterias:criteria){
    		
    		sql += ")";
    	}
    	
    	try
        {
    		Statement statement = holder.getStatement();
    		
            ResultSet resultSet = statement.executeQuery( sql );            

            resultSet.next();
            return resultSet.getInt( 1 );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get all operands", ex  );
        }
        finally
        {
        	holder.close();
        }
    }
}
