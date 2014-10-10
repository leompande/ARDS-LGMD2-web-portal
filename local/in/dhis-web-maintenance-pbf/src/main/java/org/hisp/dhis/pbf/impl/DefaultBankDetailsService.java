package org.hisp.dhis.pbf.impl;

import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.BankDetails;
import org.hisp.dhis.pbf.api.BankDetailsService;
import org.hisp.dhis.pbf.api.BankDetailsStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultBankDetailsService implements BankDetailsService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private BankDetailsStore bankDetailsStore;

    public void setBankDetailsStore( BankDetailsStore bankDetailsStore )
    {
        this.bankDetailsStore = bankDetailsStore;
    }

    // -------------------------------------------------------------------------
    // BankDetails
    // -------------------------------------------------------------------------

    @Override
    public void addBankDetails( BankDetails bankDetails )
    {
        bankDetailsStore.addBankDetails( bankDetails );        
    }

    @Override
    public void updateBankDetails( BankDetails bankDetails )
    {
        bankDetailsStore.updateBankDetails( bankDetails );        
    }

    @Override
    public void deleteBankDetails( BankDetails bankDetails )
    {
        bankDetailsStore.deleteBankDetails( bankDetails );        
    }

    @Override
    public BankDetails getBankDetails( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        return bankDetailsStore.getBankDetails( organisationUnit, dataSet );
    }

    @Override
    public Collection<BankDetails> getAllBankDetails()
    {
        return bankDetailsStore.getAllBankDetails();
    }

    @Override
    public Collection<BankDetails> getBankDetails( OrganisationUnit organisationUnit )
    {
        return bankDetailsStore.getBankDetails( organisationUnit );
    }
    
}
