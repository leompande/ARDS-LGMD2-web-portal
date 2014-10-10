package org.hisp.dhis.validationrule.minmax.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class MinMaxViolationResultAction implements Action
{

    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    /*
    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }
*/
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
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

    public DataSetService getDataSetService()
    {
        return dataSetService;
    }
    
    // ---------------------------------------------------------------
    // Input & Output Parameters
    // ---------------------------------------------------------------
    
    private List<String> selectedDataSets;

    public void setSelectedDataSets( List<String> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }
    
    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }
    
    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    public int getSDateLB()
    {
        return sDateLB;
    }
    
    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    public int getEDateLB()
    {
        return eDateLB;
    }
    
    private String dsId;

    public void setDsId( String dsId )
    {
        this.dsId = dsId;
    }
    
    private String selectedButton;
    
    public void setselectedButton( String selectedButton)
    {
        this.selectedButton = selectedButton;
    }

    private String ouId;
    
    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }

    private String immChildOption;

    public void setImmChildOption( String immChildOption )
    {
        this.immChildOption = immChildOption;
    }
    
    private Connection con = null;
    private DataSet selDataSet;
    
    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute()
        throws Exception
    {

        con = sessionFactory.getCurrentSession().connection();
        
        if(immChildOption!= null && immChildOption.equalsIgnoreCase( "yes" ))
        {
            orgUnitListCB = new ArrayList<String>();
            orgUnitListCB.add( ouId );
            
            facilityLB = "immChildren";
            
            selectedDataSets = new ArrayList<String>();
            selectedDataSets.add( dsId );
        }
        
        
        // DataSet Related Info
        selDataSet = dataSetService.getDataSet( Integer.parseInt( selectedDataSets.get( 0 ) ) );
        
        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );
                        
        PeriodType dataSetPeriodType = selDataSet.getPeriodType();
        
        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( dataSetPeriodType, startPeriod.getStartDate(), endPeriod.getEndDate() ));

        
        for( String ou : orgUnitListCB )
        {
            OrganisationUnit curOU = organisationUnitService.getOrganisationUnit( Integer.parseInt( ou ) );
            
            for( Period p :periodList )
            {
                // ---------------------------------------------------------------------
                // Get the min/max values
                // ---------------------------------------------------------------------
    
                //Collection<MinMaxDataElement> minMaxDataElements = minMaxDataElementStore.getMinMaxDataElements( organisationUnit, dataElements );
                
                //minMaxMap = new HashMap<Integer, MinMaxDataElement>( minMaxDataElements.size() );
        
                //for ( MinMaxDataElement minMaxDataElement : minMaxDataElements )
                //{
                    //minMaxMap.put( minMaxDataElement.getDataElement().getId(), minMaxDataElement );
                //}
            }
        }
        
        return SUCCESS;
    }
    
}
