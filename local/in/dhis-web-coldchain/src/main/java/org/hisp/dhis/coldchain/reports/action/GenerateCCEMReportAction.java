package org.hisp.dhis.coldchain.reports.action;

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

import org.hisp.dhis.coldchain.reports.CCEMReport;
import org.hisp.dhis.coldchain.reports.CCEMReportDesign;
import org.hisp.dhis.coldchain.reports.CCEMReportManager;
import org.hisp.dhis.coldchain.reports.CCEMReportOutput;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class GenerateCCEMReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CCEMReportManager ccemReportManager;

    public void setCcemReportManager( CCEMReportManager ccemReportManager )
    {
        this.ccemReportManager = ccemReportManager;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private String selReportId;

    public void setSelReportId( String selReportId )
    {
        this.selReportId = selReportId;
    }

    private List<Integer> selOrgUnitList;

    public void setSelOrgUnitList( List<Integer> selOrgUnitList )
    {
        this.selOrgUnitList = selOrgUnitList;
    }

    private List<Integer> orgunitGroupList;

    public void setOrgunitGroupList( List<Integer> orgunitGroupList )
    {
        this.orgunitGroupList = orgunitGroupList;
    }

    private CCEMReport ccemReport;

    public CCEMReport getCcemReport()
    {
        return ccemReport;
    }

    private CCEMReportOutput ccemReportOutput;

    public CCEMReportOutput getCcemReportOutput()
    {
        return ccemReportOutput;
    }

    private String periodRadio;

    public void setPeriodRadio( String periodRadio )
    {
        this.periodRadio = periodRadio;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {
        String orgUnitIdsByComma = ccemReportManager.getOrgunitIdsByComma( selOrgUnitList, orgunitGroupList );

        ccemReport = ccemReportManager.getCCEMReportByReportId( selReportId );

        Map<String, String> ccemSettingsMap = new HashMap<String, String>( ccemReportManager.getCCEMSettings() );

        List<CCEMReportDesign> reportDesignList = new ArrayList<CCEMReportDesign>( ccemReportManager
            .getCCEMReportDesign( ccemReport.getXmlTemplateName() ) );

        if ( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE ) )
        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Map<String, Integer> catalogTypeAttributeValueMap = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValue( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId ) );

            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableData = new ArrayList<List<String>>();

            tableHeadings.add( "Catalog" );
            tableHeadings.add( "Data" );

            for ( String catalogTypeAttributeValueKey : catalogTypeAttributeValueMap.keySet() )
            {
                List<String> oneTableRowData = new ArrayList<String>();
                oneTableRowData.add( catalogTypeAttributeValueKey );
                oneTableRowData.add( "" + catalogTypeAttributeValueMap.get( catalogTypeAttributeValueKey ) );
                tableData.add( oneTableRowData );               
            }

            ccemReportOutput.setOutputType( ccemReport.getOutputType() );
            ccemReportOutput.setTableData( tableData );
            ccemReportOutput.setTableHeadings( tableHeadings );
            ccemReportOutput.setReportHeading( ccemReport.getReportName() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP ) )
        {

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Map<String, Integer> catalogTypeAttributeValueMap = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValue( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId ) );

            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableData = new ArrayList<List<String>>();

            List<Map<String, Integer>> outPutMap = new ArrayList<Map<String, Integer>>();

            tableHeadings.add( "Model Name" );
            tableHeadings.add( "Total #" );

            int i = 0;
            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                i++;
                if ( i == 1 )
                    continue;

                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                if ( ccemCellContent1.split( ":" )[3].equalsIgnoreCase( "UNKNOWN" ) )
                {
                    tableHeadings.add( "Unknown" );
                    tableHeadings.add( "%" );
                }
                else if ( ccemCellContent1.split( ":" )[4].equalsIgnoreCase( "MORE" ) )
                {
                    inventoryTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    catalogTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    Integer inventoryTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                    Integer ageStart = Integer.parseInt( ccemCellContent1.split( ":" )[3] );
                    Integer ageEnd = -1;

                    tableHeadings.add( ">" + (ageStart - 1) + " Yrs" );
                    tableHeadings.add( "%" );

                    Map<String, Integer> catalogTypeAttributeValueMap1 = new HashMap<String, Integer>(
                        ccemReportManager.getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId,
                            catalogTypeAttributeId, inventoryTypeAttributeId, ageStart, ageEnd ) );
                    outPutMap.add( catalogTypeAttributeValueMap1 );
                }
                else
                {
                    inventoryTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    catalogTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    Integer inventoryTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                    Integer ageStart = Integer.parseInt( ccemCellContent1.split( ":" )[3] );
                    Integer ageEnd = Integer.parseInt( ccemCellContent1.split( ":" )[4] );

                    tableHeadings.add( ageStart + "-" + ageEnd + " Yrs" );
                    tableHeadings.add( "%" );

                    Map<String, Integer> catalogTypeAttributeValueMap1 = new HashMap<String, Integer>(
                        ccemReportManager.getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId,
                            catalogTypeAttributeId, inventoryTypeAttributeId, ageStart, ageEnd ) );
                    outPutMap.add( catalogTypeAttributeValueMap1 );
                }
            }

            Map<Integer, Integer> grandTotal = new HashMap<Integer, Integer>();
            Integer temp = 0;
            for ( String modelName : catalogTypeAttributeValueMap.keySet() )
            {
                List<String> oneTableRowData = new ArrayList<String>();
                oneTableRowData.add( modelName );

                Integer modelNameTotalCount = catalogTypeAttributeValueMap.get( modelName );

                if ( modelNameTotalCount == null )
                    modelNameTotalCount = 0;

                oneTableRowData.add( "" + modelNameTotalCount );

                Integer temp1 = grandTotal.get( 0 );
                if ( temp1 == null )
                {
                    grandTotal.put( 0, modelNameTotalCount );
                }
                else
                {
                    grandTotal.put( 0, temp1 + modelNameTotalCount );
                }

                Integer unknownCount = 0;
                int rowNo = 1;
                for ( Map<String, Integer> tempMap : outPutMap )
                {
                    temp = tempMap.get( modelName );
                    if ( temp == null )
                        temp = 0;

                    oneTableRowData.add( "" + temp );
                    try
                    {
                        double tempD = ((double) temp / modelNameTotalCount) * 100.0;
                        tempD = Math.round( tempD * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        oneTableRowData.add( "" + tempD );
                    }
                    catch ( Exception e )
                    {
                        oneTableRowData.add( "" + 0 );
                    }

                    temp1 = grandTotal.get( rowNo );
                    if ( temp1 == null )
                    {
                        grandTotal.put( rowNo, temp );
                    }
                    else
                    {
                        grandTotal.put( rowNo, temp1 + temp );
                    }

                    unknownCount += temp;
                    rowNo++;
                }

                oneTableRowData.add( "" + (modelNameTotalCount - unknownCount) );

                try
                {
                    double tempD = ((modelNameTotalCount - unknownCount) / (double) modelNameTotalCount) * 100.0;

                    tempD = Math.round( tempD * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                    oneTableRowData.add( "" + tempD );
                }
                catch ( Exception e )
                {
                    oneTableRowData.add( "" + 0 );
                }

                tableData.add( oneTableRowData );
            }

            List<String> oneTableRowData = new ArrayList<String>();

            oneTableRowData.add( "Total" );
            Integer totalCount = grandTotal.get( 0 );
            if ( totalCount == null )
                totalCount = 0;
            oneTableRowData.add( "" + totalCount );

            Integer grandTotalOfUnknown = 0;
            for ( i = 1; i < grandTotal.size(); i++ )
            {
                temp = grandTotal.get( i );

                if ( temp == null )
                {
                    temp = 0;
                }
                oneTableRowData.add( "" + temp );
                try
                {
                    double tempD = ((double) temp / totalCount) * 100.0;
                    tempD = Math.round( tempD * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    oneTableRowData.add( "" + tempD );
                }
                catch ( Exception e )
                {
                    oneTableRowData.add( "" + 0 );
                }

                grandTotalOfUnknown += temp;
            }

            oneTableRowData.add( "" + (totalCount - grandTotalOfUnknown) );
            try
            {
                double tempD = ((double) (totalCount - grandTotalOfUnknown) / totalCount) * 100.0;
                tempD = Math.round( tempD * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                oneTableRowData.add( "" + tempD );
            }
            catch ( Exception e )
            {
                oneTableRowData.add( "" + 0 );
            }

            tableData.add( oneTableRowData );
            ccemReportOutput.setOutputType( ccemReport.getOutputType() );
            ccemReportOutput.setTableData( tableData );
            ccemReportOutput.setTableHeadings( tableHeadings );
            ccemReportOutput.setReportHeading( ccemReport.getReportName() );
        }

        else if ( ccemReport.getReportType().equals( CCEMReport.ORGUNITGROUP_DATAVALUE ) )
        {
            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List<List<String>> tableData = new ArrayList<List<String>>();

            List<String> oneSubHeadingRow = new ArrayList<String>();

            Integer periodId = 0;
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date );
            String periodStartDate = "";

            if ( periodRadio.equalsIgnoreCase( CCEMReport.CURRENT_YEAR ) )
            {
                periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";
            }
            else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_YEAR ) )
            {
                periodStartDate = (calendar.get( Calendar.YEAR ) - 1) + "-01-01";
            }

            periodId = ccemReportManager.getPeriodId( periodStartDate, ccemReport.getPeriodRequire() );

            if ( periodId == 0 )
            {
                ccemReportOutput.setReportHeading( "No Period Exists" );
                return SUCCESS;
            }

            tableHeadings.add( "Facility Type" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "Total Facilities" );
            oneSubHeadingRow.add( " " );

            String dataElementIdsByComma = "-1";
            String optComboIdsByComma = "-1";
            List<String> dataElementOptions = new ArrayList<String>();

            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );

                dataElementIdsByComma += "," + dataElementId;
                optComboIdsByComma += "," + optComboId;

                tableHeadings.add( ccemReportDesign1.getDisplayheading() );

                List<String> distinctDataElementValues = new ArrayList<String>( ccemReportManager
                    .getDistinctDataElementValue( dataElementId, optComboId, periodId ) );

                for ( int i = 0; i < distinctDataElementValues.size(); i++ )
                {
                    if ( i != 0 )
                    {
                        tableHeadings.add( " " );
                    }
                    oneSubHeadingRow.add( distinctDataElementValues.get( i ).split( ":" )[2] );
                    dataElementOptions.add( distinctDataElementValues.get( i ) );
                }
            }

            tableSubHeadings.add( oneSubHeadingRow );

            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                List<Integer> orgUnitIds = ccemReportManager.getOrgunitIds( selOrgUnitList, orgUnitGroupId );

                if ( orgUnitIds == null || orgUnitIds.size() <= 0 )
                {

                }
                else
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );

                    List<String> oneTableDataRow = new ArrayList<String>();
                    String orgUnitIdsBycomma = getCommaDelimitedString( orgUnitIds );

                    oneTableDataRow.add( orgUnitGroup.getName() );

                    oneTableDataRow.add( "" + orgUnitIds.size() );

                    Map<String, Integer> dataValueCountMap = new HashMap<String, Integer>( ccemReportManager
                        .getDataValueCountforDataElements( dataElementIdsByComma, optComboIdsByComma, periodId,
                            orgUnitIdsBycomma ) );
                    for ( String dataElementOption : dataElementOptions )
                    {
                        Integer temp = dataValueCountMap.get( dataElementOption );
                        if ( temp == null )
                        {
                            temp = 0;
                        }
                        oneTableDataRow.add( "" + temp );
                    }

                    tableData.add( oneTableDataRow );
                }
            }

            ccemReportOutput.setOutputType( ccemReport.getOutputType() );
            ccemReportOutput.setTableData( tableData );
            ccemReportOutput.setTableHeadings( tableHeadings );
            ccemReportOutput.setTableSubHeadings( tableSubHeadings );
            ccemReportOutput.setReportHeading( ccemReport.getReportName() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE ) )
        {
            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List<String> oneSubHeadingRow = new ArrayList<String>();
            List<List<String>> tableData = new ArrayList<List<String>>();

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date );
            String periodStartDate = "";
            String periodEndDate = "";
            String periodIdsByComma = "";
            List<Period> periodList = null;
            PeriodType periodType = periodService.getPeriodTypeByName( ccemReport.getPeriodRequire() );
            Date sDate = null;
            Date eDate = null;

            int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

            tableHeadings.add( "OrgUnit Hierarchy" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "OrgUnit" );
            oneSubHeadingRow.add( " " );

            if ( periodRadio.equalsIgnoreCase( CCEMReport.CURRENT_YEAR ) )
            {
                periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";
                periodEndDate = calendar.get( Calendar.YEAR ) + "-12-31";
                sDate = format.parseDate( periodStartDate );
                eDate = format.parseDate( periodEndDate );
            }
            else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_YEAR ) )
            {
                periodStartDate = (calendar.get( Calendar.YEAR ) - 1) + "-01-01";
                periodEndDate = (calendar.get( Calendar.YEAR ) - 1) + "-12-31";
                sDate = format.parseDate( periodStartDate );
                eDate = format.parseDate( periodEndDate );
            }
            else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_6_MONTHS ) )
            {
                calendar.add( Calendar.MONTH, -1 );
                calendar.set( Calendar.DATE, monthDays[calendar.get( Calendar.MONTH )] );
                eDate = calendar.getTime();

                calendar.add( Calendar.MONTH, -5 );
                calendar.set( Calendar.DATE, 1 );
                sDate = calendar.getTime();
            }
            else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_3_MONTHS ) )
            {
                calendar.add( Calendar.MONTH, -1 );
                calendar.set( Calendar.DATE, monthDays[calendar.get( Calendar.MONTH )] );
                eDate = calendar.getTime();

                calendar.add( Calendar.MONTH, -2 );
                calendar.set( Calendar.DATE, 1 );
                sDate = calendar.getTime();
            }

            periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, sDate, eDate ) );
            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );
            periodIdsByComma = getCommaDelimitedString( periodIds );
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yy" );
            System.out.println( simpleDateFormat.format( sDate ) + " : " + simpleDateFormat.format( eDate ) );

            String dataElementIdsByComma = "-1";
            String optComboIdsByComma = "-1";

            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );

                dataElementIdsByComma += "," + dataElementId;
                optComboIdsByComma += "," + optComboId;

                tableHeadings.add( ccemReportDesign1.getDisplayheading() );
                int i = 0;
                for ( Period period : periodList )
                {
                    oneSubHeadingRow.add( simpleDateFormat.format( period.getStartDate() ) );
                    if ( i != 0 )
                        tableHeadings.add( " " );
                    i++;
                }
            }

            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();

            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                    .getOrganisationUnitGroup( orgUnitGroupId );
                orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }

            orgUnitList.retainAll( orgUnitGroupMembers );
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            Map<String, Integer> equipmentDataValueMap = new HashMap<String, Integer>( ccemReportManager
                .getFacilityWiseEquipmentRoutineData( orgUnitIdsByComma, periodIdsByComma, dataElementIdsByComma,
                    optComboIdsByComma ) );

            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                List<String> oneTableDataRow = new ArrayList<String>();
                String orgUnitBranch = "";
                if ( orgUnit.getParent() != null )
                {
                    orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                }
                else
                {
                    orgUnitBranch = " ";
                }

                oneTableDataRow.add( orgUnitBranch );
                oneTableDataRow.add( orgUnit.getName() );

                for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
                {
                    String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                    Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );

                    for ( Period period : periodList )
                    {
                        Integer temp = equipmentDataValueMap.get( orgUnit.getId() + ":" + dataElementId + ":"
                            + period.getId() );
                        if ( temp == null )
                        {
                            oneTableDataRow.add( " " );
                        }
                        else
                        {
                            oneTableDataRow.add( "" + temp );
                        }
                    }
                }

                tableData.add( oneTableDataRow );
            }

            tableSubHeadings.add( oneSubHeadingRow );
            ccemReportOutput.setOutputType( ccemReport.getOutputType() );
            ccemReportOutput.setTableData( tableData );
            ccemReportOutput.setTableHeadings( tableHeadings );
            ccemReportOutput.setTableSubHeadings( tableSubHeadings );
            ccemReportOutput.setReportHeading( ccemReport.getReportName() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.VACCINE_STORAGE_CAPACITY ) )
        {
            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List<String> oneSubHeadingRow = new ArrayList<String>();
            List<List<String>> tableData = new ArrayList<List<String>>();

            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();

            String orgUnitGroupIdsByComma = "-1";

            Integer periodId = 0;
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date );
            String periodStartDate = "";

            periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";

            periodId = ccemReportManager.getPeriodId( periodStartDate, "Yearly" );

            if ( periodId == 0 )
            {
                ccemReportOutput.setReportHeading( "No Period Exists" );
                return SUCCESS;
            }

            tableHeadings.add( "OrgUnit Hierarchy" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "OrgUnit" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "OrgUnit Code" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "OrgUnit Type" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "Net Storage" );
            oneSubHeadingRow.add( "Actual" );
            tableHeadings.add( " " );
            oneSubHeadingRow.add( "Required" );
            tableHeadings.add( " " );
            oneSubHeadingRow.add( "Difference" );
            tableHeadings.add( "Surplus" );
            oneSubHeadingRow.add( ">30%" );
            tableHeadings.add( " " );
            oneSubHeadingRow.add( "10-30%" );
            tableHeadings.add( "Match" );
            oneSubHeadingRow.add( "+/- 10%" );
            tableHeadings.add( "Shortage" );
            oneSubHeadingRow.add( "10-30%" );
            tableHeadings.add( " " );
            oneSubHeadingRow.add( ">30%" );

            tableSubHeadings.add( oneSubHeadingRow );

            CCEMReportDesign ccemReportDesign1 = reportDesignList.get( 0 );
            String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            if ( ccemCellContent1.equals( "ALL" ) )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                    orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                }
            }
            else
            {
                String orgUnitGroupIds[] = ccemReportDesign1.getContent().split( "," );

                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    int flag = 0;
                    for ( String ouGroupId : orgUnitGroupIds )
                    {
                        if ( Integer.parseInt( ouGroupId ) == orgUnitGroupId )
                        {
                            orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                            flag = 1;
                            break;
                        }
                    }

                    if ( flag == 0 )
                        continue;

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                }
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }

            orgUnitList.retainAll( orgUnitGroupMembers );
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            // Calculations for Actual Column
            ccemReportDesign1 = reportDesignList.get( 1 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );

            String[] partsOfCellContent = ccemCellContent1.split( "-" );
            Integer vscrActualInventoryTypeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[0] );
            Integer vscrActualInventoryTypeAttributeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[1] );
            Double factor = Double.parseDouble( partsOfCellContent[0].split( ":" )[2] );
            ;

            Map<Integer, Double> equipmentSumByInventoryTypeMap = new HashMap<Integer, Double>( ccemReportManager
                .getSumOfEquipmentDatabyInventoryType( orgUnitIdsByComma, vscrActualInventoryTypeId,
                    vscrActualInventoryTypeAttributeId, factor ) );

            String[] partsOfVSRActualCellContent = partsOfCellContent[1].split( ":" );
            Integer vsrActualInventoryTypeId = Integer.parseInt( partsOfVSRActualCellContent[0] );
            Integer vsrActualCatalogTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[1] );
            Integer vsrActualInventoryTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[2] );
            String vsrActualEquipmentValue = partsOfVSRActualCellContent[3];

            Map<Integer, Double> catalogSumByEquipmentDataMap = new HashMap<Integer, Double>( ccemReportManager
                .getCatalogDataSumByEquipmentData( orgUnitIdsByComma, vsrActualInventoryTypeId,
                    vsrActualCatalogTypeAttributeId, vsrActualInventoryTypeAttributeId, vsrActualEquipmentValue ) );

            // Calculations for Required Column
            ccemReportDesign1 = reportDesignList.get( 2 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            partsOfCellContent = ccemCellContent1.split( "--" );

            String[] catalogDataParts = partsOfCellContent[0].split( ":" );
            Integer vsReqCatalogTypeId = Integer.parseInt( catalogDataParts[0] );
            Integer vsReqStorageTempId = Integer.parseInt( catalogDataParts[1] );
            String vsReqStorageTemp = catalogDataParts[2];
            Integer vsReqNationalSupplyId = Integer.parseInt( catalogDataParts[3] );
            String vsReqNationalSupply = catalogDataParts[4];
            String vsReqCatalogAttribIds = catalogDataParts[5];

            Integer vsReqPackedVol = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[0] );
            Integer vsReqDiluentVol = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[1] );
            Integer vsReqDoses = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[2] );
            Integer vsReqTargetPopCat = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[3] );
            Integer vsReqUsage = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[4] );
            Integer vsReqWastage = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[5] );

            List<Integer> catalogIdsForRequirement = new ArrayList<Integer>( ccemReportManager
                .getCatalogIdsForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply ) );

            Map<String, String> catalogDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getCatalogDataForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply, vsReqCatalogAttribIds ) );

            Integer vsReqStaticDel = Integer.parseInt( partsOfCellContent[3].split( "," )[0] );
            Integer vsReqOutReachDel = Integer.parseInt( partsOfCellContent[3].split( "," )[1] );

            String catalogOption_DataelementIds = vsReqStaticDel + "," + vsReqOutReachDel;

            String[] dataelementDataParts = partsOfCellContent[1].split( "," );
            Map<String, Integer> catalogOption_DataelementMap = new HashMap<String, Integer>();

            for ( String de_catalogOption : dataelementDataParts )
            {
                catalogOption_DataelementMap.put( de_catalogOption.split( ":" )[1], Integer.parseInt( de_catalogOption
                    .split( ":" )[0] ) );
                catalogOption_DataelementIds += "," + Integer.parseInt( de_catalogOption.split( ":" )[0] );
            }

            Map<String, String> dataElementDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getDataElementDataForCatalogOptionsForRequirement( orgUnitIdsByComma, catalogOption_DataelementIds,
                    periodId ) );

            String orgUnitGroupAttribIds = partsOfCellContent[2];
            Integer vsReqSupplyInterval = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[0] );
            Integer vsReqReserveStock = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[1] );

            Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getOrgUnitGroupAttribDataForRequirement( orgUnitGroupIdsByComma, orgUnitGroupAttribIds ) );

            Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>( ccemReportManager
                .getOrgunitAndOrgUnitGroupMap( orgUnitGroupIdsByComma, orgUnitIdsByComma ) );

            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                List<String> oneTableDataRow = new ArrayList<String>();
                String orgUnitBranch = "";
                if ( orgUnit.getParent() != null )
                {
                    orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                }
                else
                {
                    orgUnitBranch = " ";
                }

                oneTableDataRow.add( orgUnitBranch );
                oneTableDataRow.add( orgUnit.getName() );
                oneTableDataRow.add( orgUnit.getCode() );
                String orgUnitGroupName = orgUnitGroupMap.get( orgUnit.getId() );
                if ( orgUnitGroupName == null )
                {
                    oneTableDataRow.add( " " );
                }
                else
                {
                    oneTableDataRow.add( orgUnitGroupName );
                }

                Double vsrActualValue = catalogSumByEquipmentDataMap.get( orgUnit.getId() );
                if ( vsrActualValue == null )
                    vsrActualValue = 0.0;

                Double vscrActualValue = equipmentSumByInventoryTypeMap.get( orgUnit.getId() );
                if ( vscrActualValue == null )
                    vscrActualValue = 0.0;

                Double vaccineActualValue = vsrActualValue + vscrActualValue;
                vaccineActualValue = Math.round( vaccineActualValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                oneTableDataRow.add( "" + vaccineActualValue );

                // Calculation for Requirement Column
                String tempStr = null;
                Double vaccineRequirement = 0.0;
                for ( Integer catalogId : catalogIdsForRequirement )
                {
                    Double vsReqUsageData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqUsage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqUsageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqUsageData = 0.0;
                        }
                    }

                    Double vsReqTargetPopData = 0.0;
                    String vsReqTargetPopCatData = catalogDataForRequirement.get( catalogId + ":" + vsReqTargetPopCat );
                    if ( vsReqTargetPopCatData != null )
                    {
                        Integer deId = catalogOption_DataelementMap.get( vsReqTargetPopCatData );
                        tempStr = dataElementDataForRequirement.get( deId + ":" + periodId + ":" + orgUnit.getId() );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqTargetPopData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqTargetPopData = 0.0;
                            }
                        }
                    }

                    Double vsReqDosesData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqDoses );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqDosesData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqDosesData = 0.0;
                        }
                    }

                    Double vsReqPackedVolData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqPackedVol );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqPackedVolData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqPackedVolData = 0.0;
                        }
                    }

                    String tempStr1 = dataElementDataForRequirement.get( vsReqStaticDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    String tempStr2 = dataElementDataForRequirement.get( vsReqOutReachDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    if ( (tempStr1 != null && tempStr1.equalsIgnoreCase( "true" ))
                        || (tempStr2 != null && tempStr2.equalsIgnoreCase( "true" )) )
                    {
                        Double vsReqDiluentVolData = 0.0;
                        tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqDiluentVol );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqDiluentVolData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqDiluentVolData = 0.0;
                            }
                        }

                        vsReqPackedVolData += vsReqDiluentVolData;
                    }

                    Double vsReqWastageData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqWastage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqWastageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqWastageData = 0.0;
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

                    // Formula for calculating Requirement for individual
                    // vaccine
                    Double individualVaccineRequirement = 0.0;
                    try
                    {
                        individualVaccineRequirement = ((vsReqUsageData * vsReqTargetPopData) / 100) * vsReqDosesData
                            * vsReqPackedVolData * (1 / (1 - (vsReqWastageData / 100)))
                            * (((vsReqSupplyIntervalData + vsReqReserveStockData) / 52) / 1000);
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception while calculating individualVaccineRequirement" );
                        individualVaccineRequirement = 0.0;
                    }

                    // System.out.println( vsReqUsageData
                    // +":"+vsReqTargetPopData +":"+vsReqDosesData
                    // +":"+vsReqPackedVolData +":"+ vsReqWastageData +":"+
                    // vsReqSupplyIntervalData +":"+ vsReqReserveStockData );

                    vaccineRequirement += individualVaccineRequirement;

                }

                vaccineRequirement = Math.round( vaccineRequirement * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                oneTableDataRow.add( "" + vaccineRequirement );

                Double diffVaccineReq = vaccineActualValue - vaccineRequirement;
                diffVaccineReq = Math.round( diffVaccineReq * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                oneTableDataRow.add( "" + diffVaccineReq );

                Double diffPercentage = (diffVaccineReq / vaccineActualValue) * 100;
                if ( diffPercentage < -30.0 )
                {
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "1" );
                }
                else if ( diffPercentage >= -30.0 && diffPercentage < -10.0 )
                {
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "1" );
                    oneTableDataRow.add( "0" );
                }
                else if ( diffPercentage >= -10.0 && diffPercentage < 10.0 )
                {
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "1" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                }
                else if ( diffPercentage >= 10.0 && diffPercentage < 30.0 )
                {
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "1" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                }
                else
                {
                    oneTableDataRow.add( "1" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                    oneTableDataRow.add( "0" );
                }

                tableData.add( oneTableDataRow );
            }

            ccemReportOutput.setOutputType( ccemReport.getOutputType() );
            ccemReportOutput.setTableData( tableData );
            ccemReportOutput.setTableHeadings( tableHeadings );
            ccemReportOutput.setTableSubHeadings( tableSubHeadings );
            ccemReportOutput.setReportHeading( ccemReport.getReportName() );
        }

        return SUCCESS;
    }

    private String getOrgunitBranch( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " -> " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
}
