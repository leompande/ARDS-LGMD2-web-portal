package org.hisp.dhis.reports.importing.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.util.DBConnection;
import org.hisp.dhis.reports.util.ReportService;

import com.opensymphony.xwork2.ActionSupport;

public class ImportingResultAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private DBConnection dbConnection;
    
    public void setDbConnection( DBConnection dbConnection )
    {
        this.dbConnection = dbConnection;
    }
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
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

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private String status;
    
    public String getStatus()
    {
        return status;
    }
    
    private Map<String, String> deMap;
    
    private Map<Integer, Integer> ouMap;
    
    private Map<Integer, Integer> periodMap;
    
    Connection newDBCon = null;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        String storedBy = "admin";

        Connection oldDBCon = dbConnection.openOLDDBConnection();
        
        newDBCon = dbConnection.openConnection();
        
        PreparedStatement pst = null;
        
        String query = "INSERT IGNORE INTO DATAVALUE (dataelementid,periodid,sourceid,categoryoptioncomboid,value,storedby) values (?,?,?,?,?,?)";
        
        pst = newDBCon.prepareStatement( query );
        
        try
        {
            deMap = new HashMap<String, String>( reportService.getDEMappingsForImporting() );
            
            ouMap = new HashMap<Integer, Integer>( reportService.getOUMappingForImporting() );
            
            periodMap = new HashMap<Integer, Integer>( reportService.getPeriodMappingForImporting() );
            
            List<Integer> ouList = new ArrayList<Integer>( ouMap.keySet() );

            for( Integer ouId : ouList )
            {
                OrganisationUnit destOrgUnit = organisationUnitService.getOrganisationUnit( ouId );
                int sourceOrgUnitId = -1;
                sourceOrgUnitId = ouMap.get( ouId );
                
                List<Integer> periodList = new ArrayList<Integer>( periodMap.keySet() );            
                for( Integer periodId : periodList )
                {
                    Period destPeriod = periodService.getPeriod( periodId );
                    int sourcePeriodId = -1;
                    sourcePeriodId = periodMap.get( periodId );
                    
                    List<String> deList = new ArrayList<String>( deMap.keySet() );
                    for( String deExp : deList )
                    {
                        System.out.println( "DataElementId : "+ deExp.substring( deExp.indexOf( "[" )+1, deExp.indexOf( "." ) ) );
                        System.out.println( "OptionComboId : "+ deExp.substring( deExp.indexOf( "." )+1, deExp.lastIndexOf( "]" ) ) );
                        
                        Integer deId = Integer.parseInt( deExp.substring( deExp.indexOf( "[" )+1, deExp.indexOf( "." ) ) );
                        Integer optComboId = Integer.parseInt( deExp.substring( deExp.indexOf( "." )+1, deExp.lastIndexOf( "]" ) ) );
                        
                        //DataElement destDataElement = dataElementService.getDataElement( deId );
                        //DataElementCategoryOptionCombo destdataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optComboId );
                                                
                        String sourceDeExp = deMap.get( deExp );
                        
                        if( sourceDeExp == null)
                        {
                            System.out.println("sourceDeExp is NULL");                            
                        }
                        else if(sourcePeriodId == -1)
                        {
                            System.out.println("sourcePeriod is NULL "+periodId);                            
                        }
                        else if(sourceOrgUnitId == -1)
                        {
                            System.out.println("sourceOrgUnit is NULL");                            
                        }
                        else if(oldDBCon == null)
                        {
                            System.out.println("oldDBCon is NULL");                            
                        }
                        
                        String resultVal = reportService.getResultDataValue( sourceDeExp, sourcePeriodId,  sourceOrgUnitId, oldDBCon );
                        
                        if(resultVal == null || resultVal.trim().equals( "" ) || resultVal.trim().equalsIgnoreCase( "0" ) )
                        {
                            continue;
                        }
                        
                        try
                        {   
                        	
                            pst.setInt( 1, deId );
                            pst.setInt( 2, periodId );
                            pst.setInt( 3, ouId );
                            pst.setInt( 4, optComboId );                            
                            pst.setString( 5, resultVal );
                            pst.setString( 6, storedBy );                            
                            
                            pst.executeUpdate();
                        }
                        catch ( Exception e )
                        {
                            System.out.println( "SQL Exception while inserting : " + e.getMessage() );
                        }
                        //DataValue dataValue = new DataValue( destDataElement, destPeriod, destOrgUnit, resultVal, storedBy, new Date(), null, destdataElementCategoryOptionCombo );
    
                        //dataValueService.addDataValue( dataValue );
    
                    }
                    
                    System.out.println("Importing for "+destPeriod.getStartDate() + " is completed");
                }
                
                System.out.println("**********************************************");
                System.out.println("Importing for "+destOrgUnit.getShortName() + " is completed");
                System.out.println("**********************************************");
            }
            
            status = "IMPORTING HAS DONE";
            
            System.out.println("IMPORTING HAS DONE");
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage() );
            status =  "Exception : "+ e.getMessage();
        }
        finally
        {
            try
            {  
            	if( pst != null ) pst.close();
            	
                if( oldDBCon != null ) oldDBCon.close();
                
                if( newDBCon != null ) newDBCon.close();
            }
            catch( Exception e )
            {
                System.out.println("Exception while closing DB Connections : "+e.getMessage());
            }
        }// finally block end
        
        return SUCCESS;
    }
   
}
