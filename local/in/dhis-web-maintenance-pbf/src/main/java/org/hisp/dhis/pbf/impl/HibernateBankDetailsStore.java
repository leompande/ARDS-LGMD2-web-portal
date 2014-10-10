package org.hisp.dhis.pbf.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.BankDetails;
import org.hisp.dhis.pbf.api.BankDetailsStore;

public class HibernateBankDetailsStore implements BankDetailsStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // BankDetails
    // -------------------------------------------------------------------------

    @Override
    public void addBankDetails( BankDetails bankDetails )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( bankDetails );
    }

    @Override
    public void updateBankDetails( BankDetails bankDetails )
    {
        Session session = sessionFactory.getCurrentSession();
        
        session.update( bankDetails );
    }

    @Override
    public void deleteBankDetails( BankDetails bankDetails )
    {
        Session session = sessionFactory.getCurrentSession();
        
        session.delete( bankDetails );
    }

    @Override
    public BankDetails getBankDetails( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( BankDetails.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return (BankDetails) criteria.uniqueResult();
    }

    @Override
    public Collection<BankDetails> getAllBankDetails()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( BankDetails.class );

        return criteria.list();
    }

    @Override
    public Collection<BankDetails> getBankDetails( OrganisationUnit organisationUnit )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( BankDetails.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );

        return criteria.list();
    }

}
