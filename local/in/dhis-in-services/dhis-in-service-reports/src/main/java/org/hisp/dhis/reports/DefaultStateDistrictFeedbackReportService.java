package org.hisp.dhis.reports;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

/**
 * <gaurav>,Date: 7/4/12, Time: 12:47 PM
 */
public class DefaultStateDistrictFeedbackReportService
    implements StateDistrictFeedbackReportService
{

    // ---------------------------------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------------------------------------------------------------------------------------
    // Implementation
    // ---------------------------------------------------------------------------------------

    public Map<String, String> getDistrictFeedbackData( OrganisationUnit District, Date sDate, Date eDate,
        String XmlFileName )
    {
        final String RA_FOLDER = System.getenv( "DHIS2_HOME" ) + File.separator
            + configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();

        String DistrictFeedBackXmlPath = RA_FOLDER + File.separator + XmlFileName;

        Map<String, String> feedbackTemplateMap = new HashMap<String, String>();

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( DistrictFeedBackXmlPath ) );
            if ( doc == null )
            {
                System.out.println( "District feedback XML file not found" );
            }

            NodeList excelFiles = doc.getElementsByTagName( "excel" );
            int numFile = excelFiles.getLength();

            NodeList listOfDeFeed = doc.getElementsByTagName( "dfeed" );
            int totalDeFeeds = listOfDeFeed.getLength();

            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnitWithChildren( District.getId() ) );

            for ( int s = 0; s < totalDeFeeds; s++ )
            {
                Element deFeedElement = (Element) listOfDeFeed.item( s );

                int fCode = new Integer( deFeedElement.getAttribute( "fcode" ) );
                int sheetNo = new Integer( deFeedElement.getAttribute( "sheetno" ) );
                int rowNo = new Integer( deFeedElement.getAttribute( "rowno" ) );
                int colNo = new Integer( deFeedElement.getAttribute( "colno" ) );
                int dataSetID = new Integer( deFeedElement.getAttribute( "datasetid" ) );

                if ( organisationUnitGroupService == null )
                {
                    System.out.println( "* ERROR !! OrganisationUnitGroupService is NULL" );
                }

                List<OrganisationUnit> orgUnitGroupList = new ArrayList<OrganisationUnit>( organisationUnitGroupService
                    .getOrganisationUnitGroup( fCode ).getMembers() );

                orgUnitGroupList.retainAll( orgUnitList );

                Collection<Period> IntermediatePeriods = periodService.getPeriodsBetweenDates( sDate, eDate );

                int totalMatchedFacilities = orgUnitGroupList.size();

                int totalReported = 0;

                int totalLate = 0;

                int totalEntries = 0;

                int totalNonZero = 0;

                int dataValueCount = 0;

                for ( OrganisationUnit orgUnit : orgUnitGroupList )
                {

                    for ( Period period : IntermediatePeriods )
                    {

                        DataSet facilityDataSet = dataSetService.getDataSet( dataSetID );

                        int expiryDays = facilityDataSet.getExpiryDays();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( period.getStartDate() );
                        cal.add( Calendar.DATE, expiryDays );
                        Date expiryDate = cal.getTime();

                        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSetService
                            .getDataElements( facilityDataSet ) );

                        totalEntries = (dataElementList.size() * orgUnitGroupList.size());

                        Collection<Integer> dataElementCollection = new ArrayList<Integer>( getIdentifiers(
                            DataElement.class, dataElementList ) );

                        String dataElementIdsByComma = getCommaDelimitedString( dataElementCollection );

                        dataValueCount = jdbcTemplate.queryForInt(
                            "SELECT COUNT(0) FROM datavalue WHERE dataelementid IN(?) AND periodid=? AND sourceid=?",
                            new Object[] { new String( dataElementIdsByComma ), new Integer( period.getId() ),
                                new Integer( orgUnit.getId() ) } );

                        totalNonZero = jdbcTemplate
                            .queryForInt(
                                "SELECT COUNT(0) FROM datavalue WHERE dataelementid IN(?) AND periodid=? AND sourceid=? AND value>0 ",
                                new Object[] { new String( dataElementIdsByComma ), new Integer( period.getId() ),
                                    new Integer( orgUnit.getId() ) } );

                        totalLate = jdbcTemplate
                            .queryForInt(
                                "SELECT COUNT(0) FROM datavalue WHERE dataelementid IN(?) AND periodid=? AND sourceid=? AND lastupdated>? ",
                                new Object[] { new String( dataElementIdsByComma ), new Integer( period.getId() ),
                                    new Integer( orgUnit.getId() ), (expiryDate) } );

                    }

                    if ( dataValueCount > 0 )
                    {
                        ++totalReported;
                    }

                }

                totalNonZero = (totalNonZero / IntermediatePeriods.size());

                for ( int iNum = 1; iNum <= 9; ++iNum )
                {
                    if ( iNum == 1 )
                    {
                        int tempColNo = colNo + 1;
                        int tempRowNo = rowNo;
                        String tempStr = String.valueOf( totalMatchedFacilities );

                        String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                        feedbackTemplateMap.put( cellPos, tempStr );

                    }
                    if ( iNum == 2 )
                    {
                        int tempColNo = colNo + 3;
                        int tempRowNo = rowNo;
                        String tempStr = String.valueOf( (totalMatchedFacilities - totalReported) );

                        String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                        feedbackTemplateMap.put( cellPos, tempStr );

                    }
                    if ( iNum == 3 )
                    {
                        int tempColNo = colNo + 5;
                        int tempRowNo = rowNo;
                        String tempStr;
                        if ( totalMatchedFacilities > 0 )
                        {
                            tempStr = String.valueOf( Math.round( ((totalReported) * 100) / totalMatchedFacilities ) )
                                + " %";
                        }
                        else
                        {
                            tempStr = "0 %";
                        }

                        String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                        feedbackTemplateMap.put( cellPos, tempStr );

                    }
                    if ( iNum == 4 )
                    {
                        int tempColNo = colNo + 1;
                        int tempRowNo = rowNo + 10;
                        String tempStr;

                        if ( totalEntries > 0 )
                        {
                            tempStr = String
                                .valueOf( Math.round( ((totalEntries - totalNonZero) * 100) / totalEntries ) )
                                + " %";
                        }
                        else
                        {
                            tempStr = "0 %";
                        }

                        String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                        feedbackTemplateMap.put( cellPos, tempStr );

                    }
                    if ( iNum == 5 )
                    {
                        int tempColNo = colNo + 4;
                        int tempRowNo = rowNo + 10;
                        String tempStr;
                        if ( totalEntries > 0 )
                        {
                            tempStr = String.valueOf( Math.round( ((totalNonZero * 100) / totalEntries) ) ) + " %";
                        }
                        else
                        {
                            tempStr = "0 %";
                        }

                        String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                        feedbackTemplateMap.put( cellPos, tempStr );

                    }
                    if ( iNum == 6 )
                    {
                        int tempColNo = colNo + 1;
                        int tempRowNo = rowNo + 20;
                        String tempStr = String.valueOf( totalMatchedFacilities - totalLate );

                        String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                        feedbackTemplateMap.put( cellPos, tempStr );

                    }
                    if ( iNum == 7 )
                    {
                        int tempColNo = colNo + 3;
                        int tempRowNo = rowNo + 20;
                        String tempStr;
                        if ( totalMatchedFacilities > 0 )
                        {
                            tempStr = String.valueOf( Math.round( ((totalMatchedFacilities - totalLate) * 100)
                                / totalMatchedFacilities ) )
                                + " %";
                        }
                        else
                        {
                            tempStr = "0 %";
                        }

                        String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                        feedbackTemplateMap.put( cellPos, tempStr );

                    }
                    if ( iNum == 8 )
                    {
                        int tempColNo = colNo + 4;
                        int tempRowNo = rowNo + 20;

                        String tempStr = String.valueOf( totalLate );

                        String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                        feedbackTemplateMap.put( cellPos, tempStr );

                    }
                    if ( iNum == 9 )
                    {
                        int tempColNo = colNo + 6;
                        int tempRowNo = rowNo + 20;
                        String tempStr;
                        if ( totalMatchedFacilities > 0 )
                        {
                            tempStr = String.valueOf( Math.round( ((totalLate) * 100) / totalMatchedFacilities ) )
                                + " %";
                        }
                        else
                        {
                            tempStr = "0 %";
                        }

                        String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                        feedbackTemplateMap.put( cellPos, tempStr );
                    }

                }

            }

        }
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

        return feedbackTemplateMap;
    }

}