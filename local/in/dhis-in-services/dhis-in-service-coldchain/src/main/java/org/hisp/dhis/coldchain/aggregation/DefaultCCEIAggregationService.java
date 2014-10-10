package org.hisp.dhis.coldchain.aggregation;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.coldchain.reports.CCEMReport;
import org.hisp.dhis.coldchain.reports.CCEMReportDesign;
import org.hisp.dhis.coldchain.reports.CCEMReportManager;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultCCEIAggregationService
    implements CCEIAggregationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private CCEMReportManager ccemReportManager;

    public void setCcemReportManager( CCEMReportManager ccemReportManager )
    {
        this.ccemReportManager = ccemReportManager;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
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

    private DataElementCategoryService dataElementCategoryService;
    
    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    
    public String getQueryForRefrigeratorWorkingStatus( Integer equipmentTypeId, Integer modelTypeAttributeId, String modelName, String workingStatus )
    {
        String query = "SELECT equipment.organisationunitid,COUNT(*) FROM modelattributevalue " +
                            " INNER JOIN equipment ON modelattributevalue.modelid = equipment.modelid " +
                            " INNER JOIN equipmentattributevalue ON equipmentattributevalue.equipmentid = equipment.equipmentid " +
                            " WHERE " + 
                                " equipment.equipmenttypeid = " + equipmentTypeId + " AND " +
                                " modelattributevalue.value = " + modelName + " AND " + 
                                " modelattributevalue.modeltypeattributeid = " + modelTypeAttributeId +" AND " +
                                " equipment.organisationunitid IN ( " + Lookup.ORGUNITID_BY_COMMA + " ) AND " +
                                " equipmentattributevalue.value IN ( " + workingStatus + " ) AND " +
                                " equipment.registrationdate <= '" + Lookup.CURRENT_PERIOD_ENDDATE +"'" +
                            " GROUP BY equipment.organisationunitid";
        return query;
    }

    public String getQueryForRefrigeratorUtilization( Integer equipmentTypeId, Integer modelTypeAttributeId, String modelName, String utilization )
    {
        String query = "SELECT equipment.organisationunitid,COUNT(*) FROM modelattributevalue " +
                            " INNER JOIN equipment ON modelattributevalue.modelid = equipment.modelid " +
                            " INNER JOIN equipmentattributevalue ON equipmentattributevalue.equipmentid = equipment.equipmentid " +
                            " WHERE " + 
                                " equipment.equipmenttypeid = " + equipmentTypeId + " AND " +
                                " modelattributevalue.value = " + modelName + " AND " + 
                                " modelattributevalue.modeltypeattributeid = " + modelTypeAttributeId +" AND " +
                                " equipment.organisationunitid IN ( " + Lookup.ORGUNITID_BY_COMMA + " ) AND " +
                                " equipmentattributevalue.value IN ( " + utilization + " ) AND " + 
                                " equipment.registrationdate <= '" + Lookup.CURRENT_PERIOD_ENDDATE +"'" +
                            " GROUP BY equipment.organisationunitid";
        return query;
    }

    public Map<String, Integer> calculateRefrigeratorWorkingStatus( Period period, DataElement dataElement, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            query = query.replace( Lookup.ORGUNITID_BY_COMMA, orgUnitIdsByComma );
            
            query = query.replace( Lookup.CURRENT_PERIOD_ENDDATE, simpleDateFormat.format( period.getEndDate() ) );
            
            System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println("Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }

    public Map<String, Integer> calculateRefrigeratorUtilization( Period period, DataElement dataElement, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( Lookup.CURRENT_PERIOD_ENDDATE, simpleDateFormat.format( period.getEndDate() ) );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println("Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }

    public Map<String, Integer> calculateStorageCapacityData( Period period, DataElement dataElement, Set<OrganisationUnit> orgUnits, Set<OrganisationUnitGroup> orgUnitGroups )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();

        /**
         * TODO need to get all parameters from lookup
         */
        CCEMReport ccemReport = ccemReportManager.getCCEMReportByReportId( "22" );
        Map<String, String> ccemSettingsMap = new HashMap<String, String>( ccemReportManager.getCCEMSettings() );
        List<CCEMReportDesign> reportDesignList = new ArrayList<CCEMReportDesign>( ccemReportManager.getCCEMReportDesign( ccemReport.getXmlTemplateName() ) );

        Calendar calendar = Calendar.getInstance();
        calendar.setTime( new Date() );
        String periodStartDate = period.getStartDateString();

        String periodEndDate = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy" );
        Period period1 = PeriodType.getPeriodFromIsoString( simpleDateFormat.format( period.getStartDate() ) );
        period1 = periodService.reloadPeriod( period1 );
        Integer periodId = period1.getId();
        // Integer periodId = ccemReportManager.getPeriodId( periodStartDate,
        // "Yearly" );

        CCEMReportDesign ccemReportDesign1 = reportDesignList.get( 0 );
        String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );

        ccemReportDesign1 = reportDesignList.get( 1 );
        ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
        String[] partsOfCellContent = ccemCellContent1.split( "-" );
        Integer vscrActualInventoryTypeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[0] );
        Integer vscrActualInventoryTypeAttributeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[1] );
        Double factor = Double.parseDouble( partsOfCellContent[0].split( ":" )[2] );

        Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
        String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

        Collection<Integer> orgUnitGroupIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnitGroup.class, orgUnitGroups ) );
        String orgUnitGroupIdsByComma = getCommaDelimitedString( orgUnitGroupIds );

        Map<Integer, Double> equipmentSumByInventoryTypeMap = new HashMap<Integer, Double>(
            ccemReportManager.getSumOfEquipmentDatabyInventoryType( orgUnitIdsByComma, vscrActualInventoryTypeId, vscrActualInventoryTypeAttributeId, factor ) );

        String[] partsOfVSRActualCellContent = partsOfCellContent[1].split( ":" );
        Integer vsrActualInventoryTypeId = Integer.parseInt( partsOfVSRActualCellContent[0] );
        Integer vsrActualCatalogTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[1] );
        Integer vsrActualInventoryTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[2] );
        String vsrActualEquipmentValue = partsOfVSRActualCellContent[3];

        Map<Integer, Double> catalogSumByEquipmentDataMap = new HashMap<Integer, Double>( ccemReportManager.getCatalogDataSumByEquipmentData( orgUnitIdsByComma, vsrActualInventoryTypeId, vsrActualCatalogTypeAttributeId, vsrActualInventoryTypeAttributeId, vsrActualEquipmentValue ) );

        // Calculations for Required Column
        ccemReportDesign1 = reportDesignList.get( 2 );
        ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
        partsOfCellContent = ccemCellContent1.split( "--" );

        // PART 1 - 4:47:+4C:48:Yes
        String[] catalogDataParts = partsOfCellContent[0].split( ":" );
        Integer vsReqCatalogTypeId = Integer.parseInt( catalogDataParts[0] );
        Integer vsReqStorageTempId = Integer.parseInt( catalogDataParts[1] );
        String vsReqStorageTemp = catalogDataParts[2];
        Integer vsReqNationalSupplyId = Integer.parseInt( catalogDataParts[3] );
        String vsReqNationalSupply = catalogDataParts[4];

        List<Integer> catalogIdsForRequirement = new ArrayList<Integer>( ccemReportManager.getCatalogIdsForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp, vsReqNationalSupplyId, vsReqNationalSupply ) );

        // PART 2 - 5
        String dataelementIds = "" + partsOfCellContent[1];
        Integer vsReqLiveBirthDeId = Integer.parseInt( dataelementIds.split( "," )[0] );
        Map<String, String> dataElementDataForRequirement = new HashMap<String, String>( ccemReportManager.getDataElementDataForCatalogOptionsForRequirement( orgUnitIdsByComma, dataelementIds, periodId ) );

        // PART 3 - VaccineVolumePerChild
        String vvpcConstantName = "" + partsOfCellContent[2];
        Constant constant = constantService.getConstantByName( vvpcConstantName );
        Double vsReqVaccineVolumePerChildData = constant.getValue();

        // PART 4 - 3,4
        String orgUnitGroupAttribIds = partsOfCellContent[3];
        Integer vsReqSupplyInterval = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[0] );
        Integer vsReqReserveStock = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[1] );

        Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>( ccemReportManager.getOrgUnitGroupAttribDataForRequirement( orgUnitGroupIdsByComma, orgUnitGroupAttribIds ) );

        Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>( ccemReportManager.getOrgunitAndOrgUnitGroupMap( orgUnitGroupIdsByComma, orgUnitIdsByComma ) );

        for ( OrganisationUnit orgUnit : orgUnits )
        {
            Map<String, String> numberOfData = new HashMap<String, String>();

            Double vsrActualValue = catalogSumByEquipmentDataMap.get( orgUnit.getId() );
            if ( vsrActualValue == null )
                vsrActualValue = 0.0;

            Double vscrActualValue = equipmentSumByInventoryTypeMap.get( orgUnit.getId() );
            if ( vscrActualValue == null )
                vscrActualValue = 0.0;

            Double vaccineActualValue = vsrActualValue + vscrActualValue;
            vaccineActualValue = Math.round( vaccineActualValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
            numberOfData.put( "Actual", vaccineActualValue + "" );

            // Calculation for Requirement Column
            String tempStr = null;
            Double vaccineRequirement = 0.0;
            Double vsReqLiveBirthData = 0.0;
            tempStr = dataElementDataForRequirement.get( vsReqLiveBirthDeId + ":" + periodId + ":" + orgUnit.getId() );
            if ( tempStr != null )
            {
                try
                {
                    vsReqLiveBirthData = Double.parseDouble( tempStr );
                    constant = constantService.getConstantByName( "LiveBirthsPerThousand" );                    
                    Double liveBirthPerThousand = 40.0;
                    if( constant != null )
                    {
                        liveBirthPerThousand = constant.getValue();
                    }
                    vsReqLiveBirthData = ( vsReqLiveBirthData * liveBirthPerThousand ) / 1000;                    
                }
                catch ( Exception e )
                {
                    vsReqLiveBirthData = 0.0;
                }
            }

            Double vsReqSupplyIntervalData = 0.0;
            tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqSupplyInterval );
            if ( tempStr != null )
            {
                try
                {
                    vsReqSupplyIntervalData = Double.parseDouble( tempStr );
                }
                catch ( Exception e )
                {
                    vsReqSupplyIntervalData = 0.0;
                }
            }

            Double vsReqReserveStockData = 0.0;
            tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqReserveStock );
            if ( tempStr != null )
            {
                try
                {
                    vsReqReserveStockData = Double.parseDouble( tempStr );
                }
                catch ( Exception e )
                {
                    vsReqReserveStockData = 0.0;
                }
            }

            System.out.println( vsReqLiveBirthData + " : " + vsReqVaccineVolumePerChildData + " : "
                + vsReqSupplyIntervalData + " : " + vsReqReserveStockData );
            // Formula for calculating Requirement
            try
            {
                vaccineRequirement = vsReqLiveBirthData * vsReqVaccineVolumePerChildData
                    * ((vsReqSupplyIntervalData + vsReqReserveStockData) / 52);
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while calculating individualVaccineRequirement" );
                vaccineRequirement = 0.0;
            }

            vaccineRequirement = Math.round( vaccineRequirement * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
            numberOfData.put( "Required", vaccineRequirement + "" );

            Double diffVaccineReq = vaccineActualValue - vaccineRequirement;
            diffVaccineReq = Math.round( diffVaccineReq * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
            numberOfData.put( "Difference", "" + diffVaccineReq );

            Double diffPercentage = 0.0;
            if ( vaccineActualValue == 0 && diffVaccineReq == 0 )
            {
                diffPercentage = 0.0;
            }
            else
            {
                diffPercentage = (diffVaccineReq / vaccineActualValue) * 100;
            }

            if ( diffPercentage < -30.0 )
            {
                aggregationResultMap.put( orgUnit.getId() + ":" + dataElement.getId(), 1 );
            }
            else if ( diffPercentage >= -30.0 && diffPercentage < -10.0 )
            {
                aggregationResultMap.put( orgUnit.getId() + ":" + dataElement.getId(), 1 );
            }
            else if ( diffPercentage >= -10.0 && diffPercentage < 10.0 )
            {
                //aggregationResultMap.put( orgUnit.getId() + ":" + dataElement.getId(), 0 );
            }
            else if ( diffPercentage >= 10.0 && diffPercentage < 30.0 )
            {
                //aggregationResultMap.put( orgUnit.getId() + ":" + dataElement.getId(), 0 );
            }
            else
            {
                //aggregationResultMap.put( orgUnit.getId() + ":" + dataElement.getId(), 0 );
            }
        }

        return aggregationResultMap;
    }

    public String importData( Map<String, Integer> aggregationResultMap, Period period )
    {
        String importStatus = "";

        Integer updateCount = 0;
        Integer insertCount = 0;

        String storedBy = currentUserService.getCurrentUsername();
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        long t;
        Date d = new Date();
        t = d.getTime();
        java.sql.Date lastUpdatedDate = new java.sql.Date( t );

        String query = "";
        int insertFlag = 1;
        String insertQuery = "INSERT INTO datavalue ( dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated, attributeoptioncomboid ) VALUES ";

        try
        {
            int count = 1;
            for ( String cellKey : aggregationResultMap.keySet() )
            {
                // Orgunit
                String[] oneRow = cellKey.split( ":" );
                Integer orgUnitId = Integer.parseInt( oneRow[0] );
                Integer deId = Integer.parseInt( oneRow[1] );
                Integer deCOCId = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo().getId();
                Integer periodId = period.getId();
                String value = aggregationResultMap.get( cellKey ) + "";

                query = "SELECT value FROM datavalue WHERE dataelementid = " + deId + " AND categoryoptioncomboid = " + deCOCId + " AND periodid = " + periodId + " AND sourceid = " + orgUnitId;
                SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
                if ( sqlResultSet1 != null && sqlResultSet1.next() )
                {
                    String updateQuery = "UPDATE datavalue SET value = '" + value + "', storedby = '" + storedBy + "',lastupdated='" + lastUpdatedDate + "' WHERE dataelementid = " + deId + " AND periodid = "
                        + periodId + " AND sourceid = " + orgUnitId + " AND categoryoptioncomboid = " + deCOCId;

                    jdbcTemplate.update( updateQuery );
                    updateCount++;
                }
                else
                {
                    if ( value != null && !value.trim().equals( "" ) )
                    {
                        insertQuery += "( " + deId + ", " + periodId + ", " + orgUnitId + ", " + deCOCId + ", '" + value + "', '" + storedBy + "', '" + lastUpdatedDate + "'," + deCOCId + "), ";
                        insertFlag = 2;
                        insertCount++;
                    }
                }

                if ( count == 1000 )
                {
                    count = 1;

                    if ( insertFlag != 1 )
                    {
                        insertQuery = insertQuery.substring( 0, insertQuery.length() - 2 );
                        jdbcTemplate.update( insertQuery );
                    }

                    insertFlag = 1;

                    insertQuery = "INSERT INTO datavalue ( dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated, attributeoptioncomboid ) VALUES ";
                }

                count++;
            }

            if ( insertFlag != 1 )
            {
                insertQuery = insertQuery.substring( 0, insertQuery.length() - 2 );
                jdbcTemplate.update( insertQuery );
            }

            importStatus = "Successfully populated aggregated data for the period : " + period.getStartDateString();
            importStatus += "<br/> Total new records : " + insertCount;
            importStatus += "<br/> Total updated records : " + updateCount;

        }
        catch ( Exception e )
        {
            importStatus = "Exception occured while import, please check log for more details" + e.getMessage();
        }

        return importStatus;
    }

}
