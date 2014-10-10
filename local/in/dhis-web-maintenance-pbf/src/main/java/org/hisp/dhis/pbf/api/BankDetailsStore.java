package org.hisp.dhis.pbf.api;

import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public interface BankDetailsStore
{
    String ID = BankDetailsStore.class.getName();
    
    void addBankDetails( BankDetails bankDetails );
    
    void updateBankDetails( BankDetails bankDetails );
    
    void deleteBankDetails( BankDetails bankDetails );
        
    BankDetails getBankDetails( OrganisationUnit organisationUnit, DataSet dataSet );
    
    Collection<BankDetails> getAllBankDetails();
    
    Collection<BankDetails> getBankDetails( OrganisationUnit organisationUnit );

}
