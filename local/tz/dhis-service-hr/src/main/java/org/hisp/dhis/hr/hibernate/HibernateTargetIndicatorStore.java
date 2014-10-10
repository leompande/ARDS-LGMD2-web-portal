package org.hisp.dhis.hr.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.TargetIndicator;
import org.hisp.dhis.hr.TargetIndicatorStore;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateTargetIndicatorStore 
extends HibernateGenericStore<TargetIndicator>
implements TargetIndicatorStore{
	
	@SuppressWarnings( "unchecked" )
	 public Collection<TargetIndicator> getByYear( int year ){
		 
		 return getCriteria( Restrictions.eq( "year", year ) ).list();
	 }
	 
	@SuppressWarnings( "unchecked" )
	 public Collection<TargetIndicator> getByOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup ){
		
		return getCriteria( Restrictions.eq( "organisationUnitGroup" , organisationUnitGroup ) ).list();		 
	 }
	 
	@SuppressWarnings( "unchecked" )
	 public Collection<TargetIndicator> getByAttributeOptionGroup( AttributeOptionGroup attributeOptionGroup ){
		
		return getCriteria( Restrictions.eq( "attributeOptionGroup" , attributeOptionGroup ) ).list();		 
	 }
	 
	 public int countGetTargetIndicatorByOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup ){
		 
		 Number rs =  (Number) getCriteria( Restrictions.eq( "organisationUnitGroup", organisationUnitGroup ) )
	        .setProjection(Projections.rowCount() ).uniqueResult();
			return rs != null ? rs.intValue() : 0;
	 }

}
