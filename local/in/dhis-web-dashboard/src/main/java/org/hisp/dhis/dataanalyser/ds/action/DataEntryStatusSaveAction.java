package org.hisp.dhis.dataanalyser.ds.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataentrystatus.DataEntryStatus;
import org.hisp.dhis.dataentrystatus.DataEntryStatusService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class DataEntryStatusSaveAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private DataEntryStatusService dataEntryStatusService;
    
    public void setDataEntryStatusService( DataEntryStatusService dataEntryStatusService )
    {
        this.dataEntryStatusService = dataEntryStatusService;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Collection<Integer> selectedPeriods = new ArrayList<Integer>();

    public void setSelectedPeriods( Collection<Integer> selectedPeriods )
    {
        this.selectedPeriods = selectedPeriods;
    }

    private Collection<Integer> selectedDataSets = new ArrayList<Integer>();

    public void setSelectedDataSets( Collection<Integer> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }
    
    private String includeZeros;

    public void setIncludeZeros( String includeZeros )
    {
        this.includeZeros = includeZeros;
    }
    
    public String getIncludeZeros()
    {
        return includeZeros;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private int dataSetMemberCount;
    
    Collection<Period> periods = new ArrayList<Period>();

    Collection<DataSet> dataSets = new ArrayList<DataSet>();
    
    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        System.out.println( "Data Entry Status Mart Start Time  : " + new Date() );
        
        String currentUserName = currentUserService.getCurrentUsername();
        
        for ( Integer periodId : selectedPeriods )
        {
            periods.add( periodService.getPeriod( periodId ) );
        }
        
        String periodInfo = getCommaDelimitedString( selectedPeriods );

        for ( Integer dataSetId : selectedDataSets )
        {
            dataSets.add( dataSetService.getDataSet( dataSetId ) );
        }
        
        Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
        
        String  includeZero = ""; 
        if ( includeZeros == null )
        {
            includeZero = "N";
        }
        else
        {
            includeZero = "Y";
        }
        
        String query2 = "";
        Double dataStatusPercentatge = 0.0;
        for ( DataSet dataSet : dataSets )
        {
            List<OrganisationUnit> dataSetOrganisationUnits = new ArrayList<OrganisationUnit>( dataSet.getSources() );
            dataSetOrganisationUnits.retainAll( selectedOrganisationUnits );
            
            dataSetMemberCount = 0;
            String deInfo = "-1";
            for ( DataElement de : dataSet.getDataElements() )
            {
                deInfo += "," + de.getId();
                dataSetMemberCount += de.getCategoryCombo().getOptionCombos().size();
            }

            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, dataSetOrganisationUnits ) );
            String orgUnitInfo = getCommaDelimitedString( childOrgUnitTreeIds );
            
            List<String> orgUnitPeriodIds = new ArrayList<String>();
            for( Integer orgUnitId : childOrgUnitTreeIds )
            {
                for( Integer periodId : selectedPeriods )
                {
                    orgUnitPeriodIds.add( orgUnitId+":"+periodId );
                }
            }

            if ( includeZeros == null )
            {
                query2 = "SELECT sourceid,periodid,COUNT(*) FROM datavalue WHERE dataelementid IN (" + deInfo
                            + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ") and value <> 0 GROUP BY sourceid,periodid";
            }
            else
            {
                query2 = "SELECT sourceid,periodid,COUNT(*) FROM datavalue WHERE dataelementid IN (" + deInfo
                            + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ") GROUP BY sourceid,periodid";
            }

            SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query2 );
            
            while ( sqlResultSet.next() )
            {
                Integer ouId = sqlResultSet.getInt( 1 );
                Integer periodId = sqlResultSet.getInt( 2 );
                Integer resultCount = sqlResultSet.getInt( 3 );
                
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouId );
                Period p = periodService.getPeriod( periodId );
                
                try
                {
                    dataStatusPercentatge = ( (double) resultCount / (double) dataSetMemberCount) * 100.0;
                }
                catch ( Exception e )
                {
                    dataStatusPercentatge = 0.0;
                }
                
                dataStatusPercentatge = Math.round( dataStatusPercentatge * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                
                DataEntryStatus dataEntryStatus = dataEntryStatusService.getDataEntryStatusValue( dataSet, orgUnit, p , includeZero );
                
                if ( dataEntryStatus != null )
                {
                    dataEntryStatus.setDataset( dataSet );
                    dataEntryStatus.setOrganisationunit( orgUnit );
                    dataEntryStatus.setPeriod( p );
                    dataEntryStatus.setValue( dataStatusPercentatge.toString() );
                    dataEntryStatus.setTimestamp( new Date() );
                    dataEntryStatus.setStoredBy( currentUserName );
                    dataEntryStatus.setIncludeZero( includeZero );
                    dataEntryStatusService.updateDataEntryStatus( dataEntryStatus );
                }
                else
                {
                    dataEntryStatus = new DataEntryStatus();
                    dataEntryStatus.setDataset( dataSet );
                    dataEntryStatus.setOrganisationunit( orgUnit );
                    dataEntryStatus.setPeriod( p );
                    dataEntryStatus.setValue( dataStatusPercentatge.toString() );
                    dataEntryStatus.setTimestamp( new Date() );
                    dataEntryStatus.setStoredBy( currentUserName );
                    dataEntryStatus.setIncludeZero( includeZero );
                    dataEntryStatusService.addDataEntryStatus( dataEntryStatus );
                }
                
                if( orgUnitPeriodIds.contains( ouId+":"+periodId ) )
                {
                    orgUnitPeriodIds.remove( ouId+":"+periodId );
                }                
            }

            for( String orgUnitPeriodId : orgUnitPeriodIds )
            {
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitPeriodId.split( ":" )[0] ) );
                Period p = periodService.getPeriod( Integer.parseInt( orgUnitPeriodId.split( ":" )[1] ) ); 

                DataEntryStatus dataEntryStatus = dataEntryStatusService.getDataEntryStatusValue( dataSet, orgUnit, p , includeZero );
                
                if ( dataEntryStatus != null )
                {
                    dataEntryStatus.setDataset( dataSet );
                    dataEntryStatus.setOrganisationunit( orgUnit );
                    dataEntryStatus.setPeriod( p );
                    dataEntryStatus.setValue( "0" );
                    dataEntryStatus.setTimestamp( new Date() );
                    dataEntryStatus.setStoredBy( currentUserName );
                    dataEntryStatus.setIncludeZero( includeZero );
                    dataEntryStatusService.updateDataEntryStatus( dataEntryStatus );
                }
                else
                {
                    dataEntryStatus = new DataEntryStatus();
                    dataEntryStatus.setDataset( dataSet );
                    dataEntryStatus.setOrganisationunit( orgUnit );
                    dataEntryStatus.setPeriod( p );
                    dataEntryStatus.setValue( "0" );
                    dataEntryStatus.setTimestamp( new Date() );
                    dataEntryStatus.setStoredBy( currentUserName );
                    dataEntryStatus.setIncludeZero( includeZero );
                    dataEntryStatusService.addDataEntryStatus( dataEntryStatus );
                }
            }
        }
        
        System.out.println( "Data Entry Status Mart End Time  : " + new Date() );
        message = i18n.getString( "information_successfully_saved" );
        return SUCCESS;
    }
}
