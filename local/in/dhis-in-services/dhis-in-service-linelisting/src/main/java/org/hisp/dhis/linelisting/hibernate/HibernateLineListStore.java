package org.hisp.dhis.linelisting.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.linelisting.LineListDataElementMap;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;

public class HibernateLineListStore
    implements LineListStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    // -------------------------------------------------------------------------
    // Line List Group
    // -------------------------------------------------------------------------

    public int addLineListGroup( LineListGroup lineListGroup )
    {
        PeriodType periodType = periodStore.getPeriodType( lineListGroup.getPeriodType().getClass() );

        lineListGroup.setPeriodType( periodType );

        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( lineListGroup );
    }

    public void deleteLineListGroup( LineListGroup lineListGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( lineListGroup );

    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListGroup> getAllLineListGroups()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( LineListGroup.class ).list();

    }

    public LineListGroup getLineListGroup( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (LineListGroup) session.get( LineListGroup.class, id );

    }

    public LineListGroup getLineListGroupByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListGroup.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (LineListGroup) criteria.uniqueResult();
    }

    public LineListGroup getLineListGroupByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListGroup.class );
        criteria.add( Restrictions.eq( "shortName", shortName ) );

        return (LineListGroup) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListGroup> getLineListGroupsBySource( OrganisationUnit source )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListGroup.class );
        criteria.createAlias( "sources", "s" );
        criteria.add( Restrictions.eq( "s.id", source.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListGroup> getLineListGroupsByElement( LineListElement lineListElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListGroup.class );
        criteria.createAlias( "lineListElement", "l" );

        criteria.add( Restrictions.eq( "l.id", lineListElement.getId() ) );

        return criteria.list();
    }

    public void updateLineListGroup( LineListGroup lineListGroup )
    {
        PeriodType periodType = periodStore.getPeriodType( lineListGroup.getPeriodType().getClass() );

        lineListGroup.setPeriodType( periodType );
        
        Session session = sessionFactory.getCurrentSession();

        session.update( lineListGroup );

    }

    // -------------------------------------------------------------------------
    // Line List Element
    // -------------------------------------------------------------------------

    public int addLineListElement( LineListElement lineListElement )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( lineListElement );
    }

    public void deleteLineListElement( LineListElement lineListElement )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( lineListElement );

    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListElement> getAllLineListElements()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( LineListElement.class ).list();

    }

    public LineListElement getLineListElement( int id )
    {
        Session session = sessionFactory.getCurrentSession();
        return (LineListElement) session.get( LineListElement.class, id );

    }

    public LineListElement getLineListElementByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListElement.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (LineListElement) criteria.uniqueResult();
    }

    public LineListElement getLineListElementByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( LineListElement.class );
        criteria.add( Restrictions.eq( "shortName", shortName ) );
        
        return (LineListElement) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListElement> getLineListElementsByOption( LineListOption lineListOption )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListElement.class );
        criteria.createAlias( "lineListOption", "l" );

        criteria.add( Restrictions.eq( "l.id", lineListOption.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListElement> getLineListElementsBySortOrder (LineListGroup lineListGroup)
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListElement.class );
        criteria.createAlias( "lineListGroup", "l" );

        criteria.add( Restrictions.eq( "l.id", lineListGroup.getId() ) );

        return criteria.list();
    }

    public void updateLineListElement( LineListElement lineListElement )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( lineListElement );

    }

    // -------------------------------------------------------------------------
    // Line List Option
    // -------------------------------------------------------------------------

    public int addLineListOption( LineListOption lineListOption )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( lineListOption );
    }

    public void deleteLineListOption( LineListOption lineListOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( lineListOption );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListOption> getAllLineListOptions()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( LineListOption.class ).list();

    }

    public LineListOption getLineListOption( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (LineListOption) session.get( LineListOption.class, id );

    }

    public LineListOption getLineListOptionByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListOption.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (LineListOption) criteria.uniqueResult();
    }

    public LineListOption getLineListOptionByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListOption.class );
        criteria.add( Restrictions.eq( "shortName", shortName ) );

        return (LineListOption) criteria.uniqueResult();
    }

    public void updateLineListOption( LineListOption lineListOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( lineListOption );
    }
    
    // -------------------------------------------------------------------------
    // LinelistElemnet - Dataelement Mapping
    // -------------------------------------------------------------------------

    public void addLinelistDataelementMapping( LineListDataElementMap lineListDataElementMap )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( lineListDataElementMap );
    }
    
    public void updateLinelistDataelementMapping( LineListDataElementMap lineListDataElementMap )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( lineListDataElementMap );
    }
    
    public void deleteLinelistDataelementMapping( LineListDataElementMap lineListDataElementMap )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( lineListDataElementMap );
    }

    @SuppressWarnings( "unchecked" )
    public List<LineListDataElementMap> getLinelistDataelementMappings( LineListElement linelistElement, LineListOption linelistOption )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListDataElementMap.class );
        criteria.add( Restrictions.eq( "linelistElement", linelistElement ) );
        criteria.add( Restrictions.eq( "linelistOption", linelistOption ) );

        return criteria.list();
    }
    

    public LineListDataElementMap getLinelistDataelementMapping( LineListElement linelistElement, LineListOption linelistOption, DataElement dataElement, DataElementCategoryOptionCombo deCOC )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListDataElementMap.class );
        criteria.add( Restrictions.eq( "linelistElement", linelistElement ) );
        criteria.add( Restrictions.eq( "linelistOption", linelistOption ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "dataElementOptionCombo", deCOC ) );

        return (LineListDataElementMap) criteria.uniqueResult();
    }

    public int getLineListGroupCount()
    {
        Session session = sessionFactory.getCurrentSession();
        
        Query query = session.createQuery( "select count(*) from LineListGroup" );
        
        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<LineListGroup> getLineListGroupsBetween( int first, int max )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from LineListGroup" ).setFirstResult( first ).setMaxResults( max ).list();
    }

    public int getLineListElementCount()
    {
        Session session = sessionFactory.getCurrentSession();
        
        Query query = session.createQuery( "select count(*) from LineListElement" );
        
        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<LineListElement> getLineListElementsBetween( int first, int max )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from LineListElement" ).setFirstResult( first ).setMaxResults( max ).list();
    }

    public int getLineListOptionCount()
    {
        Session session = sessionFactory.getCurrentSession();
        
        Query query = session.createQuery( "select count(*) from LineListOption" );
        
        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<LineListOption> getLineListOptionsBetween( int first, int max )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from LineListOption" ).setFirstResult( first ).setMaxResults( max ).list();
    }

}
