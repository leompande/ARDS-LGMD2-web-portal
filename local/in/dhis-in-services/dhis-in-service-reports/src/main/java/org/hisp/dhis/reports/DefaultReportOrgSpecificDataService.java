package org.hisp.dhis.reports;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.hisp.dhis.system.util.MathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;

/**
 * <gaurav>,Date: 7/10/12, Time: 6:50 PM
 */

public class DefaultReportOrgSpecificDataService
    implements ReportOrgSpecificDataService
{

    // ------------------------------------------------------------------------------------------
    // Dependencies
    // ------------------------------------------------------------------------------------------

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

    private DatabaseInfoProvider databaseInfoProvider;

    public void setDatabaseInfoProvider( DatabaseInfoProvider databaseInfoProvider )
    {
        this.databaseInfoProvider = databaseInfoProvider;
    }

    // ------------------------------------------------------------------------------------------
    // Implementation
    // ------------------------------------------------------------------------------------------

    public String getExpressionValue( String expression, String periodIdsByComma, String OrgunitIDsByComma )
    {
        final String NULL_REPLACEMENT = "0";

        DatabaseInfo dataBaseInfo = databaseInfoProvider.getDatabaseInfo();
        String query = "";

        try
        {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString
                    .length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                if ( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
                {
                    query = "SELECT SUM( cast( value as numeric) ) FROM aggregateddatavalue WHERE dataelementid = "
                        + dataElementId + " AND categoryoptioncomboid = " + optionComboId + " AND periodid IN ("
                        + periodIdsByComma + ")" + " AND organisationunitid IN(" + OrgunitIDsByComma + ")";
                }
                else if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
                {

                    query = "SELECT SUM(value) FROM aggregateddatavalue WHERE dataelementid = " + dataElementId
                        + " AND categoryoptioncomboid = " + optionComboId + " AND periodid IN (" + periodIdsByComma
                        + ")" + " AND organisationunitid IN(" + OrgunitIDsByComma + ")";
                }

                SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

                Double aggregatedValue = null;
                if ( rs.next() )
                {
                    aggregatedValue = rs.getDouble( 1 );
                }

                if ( aggregatedValue == null )
                {
                    replaceString = NULL_REPLACEMENT;
                }
                else
                {
                    replaceString = String.valueOf( aggregatedValue );
                }

                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );

            double expValue = 0.0;
            try
            {
                expValue = MathUtils.calculateExpression( buffer.toString() );

                expValue = Math.round( expValue );

            }
            catch ( Exception e )
            {

                expValue = 0.0;
                resultValue = "";
            }

            resultValue = "" + (double) expValue;

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    public Map<String, String> getOrgSpecificData( OrganisationUnit District, Date sDate, Date eDate, Date aggSdata,
        PeriodType periodType, String XmlFileName )
    {

        final String RA_FOLDER = System.getenv( "DHIS2_HOME" ) + File.separator
            + configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();

        Map<String, String> DataPositionMap = new HashMap<String, String>();

        String DistrictFeedBackXmlPath = RA_FOLDER + File.separator + XmlFileName;

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;

        File newdir = new File( outputReportPath );

        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( DistrictFeedBackXmlPath ) );
            if ( doc == null )
            {
                System.out.println( "District feedback XML file not found" );
            }

            NodeList listOfDeFeed = doc.getElementsByTagName( "org-code" );
            int totalDeFeeds = listOfDeFeed.getLength();

            for ( int s = 0; s < totalDeFeeds; s++ )
            {
                Element deFeedElement = (Element) listOfDeFeed.item( s );

                int oucode = new Integer( deFeedElement.getAttribute( "oucode" ) );
                String ptype = deFeedElement.getAttribute( "type" );
                int sheetno = new Integer( deFeedElement.getAttribute( "sheetno" ) );
                int rowno = new Integer( deFeedElement.getAttribute( "rowno" ) );
                int colno = new Integer( deFeedElement.getAttribute( "colno" ) );
                String expression = new String( deFeedElement.getAttribute( "expression" ) );

                List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( District.getChildren() );

                if ( organisationUnitGroupService == null )
                {
                    System.out.println( "* ERROR !! OrganisationUnitGroupService is NULL" );
                }

                List<OrganisationUnit> orgUnitGroupList = new ArrayList<OrganisationUnit>( organisationUnitGroupService
                    .getOrganisationUnitGroup( oucode ).getMembers() );
                orgUnitList.retainAll( orgUnitGroupList );

                Collection<Period> IntermediatePeriods = periodService.getPeriodsBetweenDates( sDate, eDate );

                Collection<Period> IntermediateAggPeriods = periodService.getPeriodsBetweenDates( aggSdata, eDate );

                String aggregatedValue = "0";

                if ( orgUnitList.size() > 0 && IntermediatePeriods.size() > 0 )
                {
                    String OrgUnitIDsByComma = "";
                    String PeriodIDsByComma = "";

                    for ( OrganisationUnit orgUnit : orgUnitList )
                    {

                        OrgUnitIDsByComma = OrgUnitIDsByComma.concat( "," + orgUnit.getId() );
                    }

                    if ( ptype.equalsIgnoreCase( "CMCY" ) )
                    {
                        for ( Period period : IntermediatePeriods )
                        {
                            PeriodIDsByComma = PeriodIDsByComma.concat( "," + period.getId() );
                        }
                    }

                    else
                    {
                        for ( Period period : IntermediateAggPeriods )
                        {

                            PeriodIDsByComma = PeriodIDsByComma.concat( "," + period.getId() );

                        }
                    }

                    aggregatedValue = getExpressionValue( expression, PeriodIDsByComma.substring( 1 ),
                        OrgUnitIDsByComma.substring( 1 ) );

                }

                int tempColNo = colno;
                int tempRowNo = rowno;

                String tempStr = aggregatedValue;

                String cellPos = String.valueOf( tempColNo ) + ":" + String.valueOf( tempRowNo );

                DataPositionMap.put( cellPos, tempStr );

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

        return DataPositionMap;
    }

}
