package org.hisp.dhis.dataanalyser.ds.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class GenerateNullReportResultAction implements Action
{
    
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings( "unused" )
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    // ---------------------------------------------------------------
    // Input Parameters
    // ---------------------------------------------------------------

    private String dsId;

    public void setDsId( String dsId )
    {
        this.dsId = dsId;
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
    
    private int periodId;
    
    public void setPeriodId( int periodId )
    {
        this.periodId = periodId;
    }

    private String ouId;
    
    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }
    
    String dataElementInfo;
    
    private Map<String, String> deMapForName;
    
    
    public Map<String, String> getDeMapForName()
    {
        return deMapForName;
    }
    
    private Map<String, String> deMapForValue;
    
    public Map<String, String> getDeMapForValue()
    {
        return deMapForValue;
    }

    private List<String> deFinalList;
    
    public List<String> getDeFinalList()
    {
        return deFinalList;
    }
    
    private String dataSetName;
    
    public String getDataSetName()
    {
        return dataSetName;
    }
    
    private int dataElementCount;
    
    public int getDataElementCount()
    {
        return dataElementCount;
    }
    
    private int nullValuDeCount;
    
    public int getNullValuDeCount()
    {
        return nullValuDeCount;
    }

    private int zeroValueDeCount;
    
    public int getZeroValueDeCount()
    {
        return zeroValueDeCount;
    }
    
    private int notZeroValueDeCount;
    
    public int getNotZeroValueDeCount()
    {
        return notZeroValueDeCount;
    }
    
    private String orgUnitName;
    
    public String getOrgUnitName()
    {
        return orgUnitName;
    }

    private String periodName;
    
    public String getPeriodName()
    {
        return periodName;
    }
    
    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------

 

    public String execute() throws Exception
    {
        
        deMapForName = new HashMap<String, String>();
        deMapForValue = new HashMap<String, String>();
        deFinalList = new ArrayList<String>();
        
        // Period Related Info
        Period period = periodService.getPeriod( periodId );
      
        // orgUnit Related Info
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouId ) );
        orgUnitName = orgUnit.getName();
        
        // DataSet Related Info
        DataSet dataSet =  dataSetService.getDataSet( Integer.parseInt( dsId ) );
        dataSetName = dataSet.getName();
        
        PeriodType dataSetPeriodType = dataSet.getPeriodType();
        
        periodName = getPeriodNameByPeriodType( dataSetPeriodType , period );
        
        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements()  );
       
        
        System.out.println( "Period : " + periodName + ", OrgUnit Name : " + orgUnit.getName() + ", Data Set Name: " + dataSet.getName() );
        
        System.out.println( "dataSet Period Type : " + dataSetPeriodType.getName() + ", OrgUnit Name : " + orgUnit.getName() + ", dataElement List : " + dataElementList.size() );
        
        // dataElement related Information
        
        dataElementInfo = "-1";
        List<DataElement> deList = new ArrayList<DataElement>();
        for ( DataElement dataElement : dataElementList )
        {
            DataElement de1 = dataElementService.getDataElement( dataElement.getId() );
            deList.add( de1 );
            
            dataElementInfo += "," + de1.getId();
            //StringBuffer deInfo = new StringBuffer( "-1" );
            //deInfo.append( "," ).append( dataElement.getId() );
            //deInfo.toString();
        }
       
        //String query = "SELECT datavalue.value ,dataelement.name ,dataelementcategoryoption.name FROM datavalue ,dataelement,dataelementcategoryoption where datavalue.dataelementid = " + dataElement.getId() + " AND datavalue.periodid = " +  period.getId() + " AND datavalue.sourceid = " + orgUnit.getId() + " AND datavalue.dataelementid = dataelement.dataelementid AND datavalue.categoryoptioncomboid = dataelementcategoryoption.categoryoptionid";
        
        String query =  "SELECT datavalue.value, datavalue.dataelementid ,dataelement.name , categoryoptioncomboid from datavalue  INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid  where datavalue.dataelementid in (" + dataElementInfo + ") and datavalue.periodid = " + period.getId() +  " and datavalue.sourceid = " + orgUnit.getId();

        
        SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

        //List<String> tempList = new ArrayList<String>();
        
        List<String> zeroValueDeList = new ArrayList<String>();
        List<String> notZeroValueDeList = new ArrayList<String>();
        List<String> nullValueDeList = new ArrayList<String>();
        int count1 = 0;
        int count2 = 0;
        while ( rs.next() )
        {                
            String deValue =  rs.getString( 1 );
            //Double deValue =  rs.getDouble( 1 );
            Integer dataElementId = rs.getInt( 2 );
            //String dataElementName = rs.getString( 3 );
            Integer categoryoptioncomboid = rs.getInt( 4 );
            
            String deAndOptionCombo = "";
            
            if( deValue.equals("0") )
            {
                //DataElement tempDE = dataElementService.getDataElement( dataElementId );
                //DataElementCategoryCombo dataElementCategoryCombo = tempDE.getCategoryCombo();
                deAndOptionCombo = dataElementId + ":" + categoryoptioncomboid;
                zeroValueDeList.add( deAndOptionCombo );
                deMapForValue.put( deAndOptionCombo, deValue );
                count1++;
            }
            else
            {
                deAndOptionCombo = dataElementId + ":" + categoryoptioncomboid;
                notZeroValueDeList.add( deAndOptionCombo );
                //deMapForName.put( deAndOptionCombo, dataElementName );
                //deMapForValue.put( deAndOptionCombo, deValue.toString() );
                deMapForValue.put( deAndOptionCombo, deValue );
                count2++;
            }
            
            //deMapForValue.put( deAndOptionCombo, deValue );
            
        }
        System.out.println( "Zero Loop count: " + count1 + ", Not Zero loop count : " + count2 );
        for( DataElement dataElement : dataElementList )
        {
            List<DataElementCategoryOptionCombo> decocList = new ArrayList<DataElementCategoryOptionCombo>(dataElement.getCategoryCombo().getOptionCombos() );
            String deAndOptionCombo = "";
            for( DataElementCategoryOptionCombo deCom  : decocList )
            {
                if( !(zeroValueDeList.contains( dataElement.getId() + ":" + deCom.getId()) || notZeroValueDeList.contains(  dataElement.getId() + ":" + deCom.getId())) )
                {
                    deAndOptionCombo = dataElement.getId() + ":" + deCom.getId();
                    nullValueDeList.add( deAndOptionCombo );
                }
                
                deMapForName.put( dataElement.getId() + ":" + deCom.getId(), dataElement.getName()+ " - " + deCom.getName() );
            }
            
            /*  
            if( !(zeroValueDeList.contains( dataElement) || notZeroValueDeList.contains( dataElement )) )
            {
                DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
                String deAndOptionCombo = dataElement.getId() + ":" + dataElementCategoryCombo.getId();
                nullValueDeList.add( deAndOptionCombo );
            }
            //deMapForName.put( deAndOptionCombo, dataElement.getName() );
              */
             
        }
        deFinalList.addAll( nullValueDeList );
        deFinalList.addAll( zeroValueDeList );
        deFinalList.addAll( notZeroValueDeList );
        
        dataElementCount = deFinalList.size();
        nullValuDeCount = nullValueDeList.size();
        zeroValueDeCount = zeroValueDeList.size();
        notZeroValueDeCount = notZeroValueDeList.size();

        System.out.println( "Null value dataElement List : " + nullValueDeList.size() + ",Zero Value dataElement List : " + zeroValueDeList.size() + ", Valued dataElement List : " + notZeroValueDeList.size()  );   
        System.out.println( "Final dataElement List : " + deFinalList.size() );   
        return SUCCESS;
    }

    // Method for getting perion name when periodType is known
    public String getPeriodNameByPeriodType( PeriodType periodType, Period period )
    {
        SimpleDateFormat simpleDateFormat1;

        SimpleDateFormat simpleDateFormat2;

        String periodName = "";

        if ( periodType.getName().equalsIgnoreCase( "monthly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM-yyyy" );
            periodName = simpleDateFormat1.format( period.getStartDate() ) ;
        }
        else if ( periodType.getName().equalsIgnoreCase( "quarterly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM" );
            simpleDateFormat2 = new SimpleDateFormat( "MMM-yyyy" );

            periodName = simpleDateFormat1.format( period.getStartDate() ) + " - " + simpleDateFormat2.format( period.getEndDate() );
        }
        else if ( periodType.getName().equalsIgnoreCase( "yearly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy" );
            int year;
           
            year = Integer.parseInt( simpleDateFormat1.format( period.getStartDate() ) ) + 1;
            
            periodName =  simpleDateFormat1.format( period.getStartDate() ) + "-" + year ;
            
        }
        else if( periodType.getName().equalsIgnoreCase( "daily" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
         
            periodName = simpleDateFormat1.format( period.getStartDate() );
        
        }
        else
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
           
            periodName = simpleDateFormat1.format( period.getStartDate() ) + " - " + simpleDateFormat1.format( period.getEndDate() );
        }

        return periodName;
    }   
    

}
