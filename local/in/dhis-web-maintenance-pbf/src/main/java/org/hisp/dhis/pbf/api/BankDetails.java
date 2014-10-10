package org.hisp.dhis.pbf.api;

import java.io.Serializable;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class BankDetails implements Serializable
{
    private OrganisationUnit organisationUnit;
    
    private DataSet dataSet;
    
    private String bank;
    
    private String branchName;
    
    private String accountName;
    
    private String accountNumber;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public BankDetails()
    {
        
    }

    public BankDetails( OrganisationUnit organisationUnit, DataSet dataSet, String bank, String branchName, String accountName, String accountNumber )
    {
        this.organisationUnit = organisationUnit;
        this.dataSet = dataSet;
        this.bank = bank;
        this.branchName = branchName;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof BankDetails) )
        {
            return false;
        }

        final BankDetails other = (BankDetails) o;

        return dataSet.equals( other.getDataSet() ) && organisationUnit.equals( other.getOrganisationUnit() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + dataSet.hashCode();
        result = result * prime + organisationUnit.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public String getBank()
    {
        return bank;
    }

    public void setBank( String bank )
    {
        this.bank = bank;
    }

    public String getBranchName()
    {
        return branchName;
    }

    public void setBranchName( String branchName )
    {
        this.branchName = branchName;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName( String accountName )
    {
        this.accountName = accountName;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setAccountNumber( String accountNumber )
    {
        this.accountNumber = accountNumber;
    }

}
