package org.hisp.dhis.excelimport.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.excelimport.api.Report;
import org.hisp.dhis.excelimport.util.ReportService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class GetReportsAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private String periodType;

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    private String ouId;

    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }

    private String reportListFileName;

    public void setReportListFileName( String reportListFileName )
    {
        this.reportListFileName = reportListFileName;
    }

    private List<Report> reportList;

    public List<Report> getReportList()
    {
        return reportList;
    }

    private String ouName;

    public String getOuName()
    {
        return ouName;
    }

    private String autogenrep;

    public void setAutogenrep( String autogenrep )
    {
        this.autogenrep = autogenrep;
    }

   // private String orgUnitLevel;

    private String raFolderName;
    
    OrganisationUnit orgUnit;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        reportList = new ArrayList<Report>();
        raFolderName = reportService.getRAFolderName();
        if ( ouId != null )
        {
            try
            {
                orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouId ) );

                //int ouLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
                int ouLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() );

                if ( autogenrep.equalsIgnoreCase( "1" ) )
                    ouLevel++;

                //orgUnitLevel = "" + ouLevel;
                ouName = orgUnit.getShortName();
                System.out.println( ouName );
                getSelectedReportList( reportListFileName );
                //System.out.println("\n\n Report File Name:" + reportListFileName );
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while getting Reports List : " + e.getMessage() );
            }
        }

        return SUCCESS;
    }

    public void getSelectedReportList( String reportListFileName )
    {
        String fileName = reportListFileName;

        String excelImportFolderName = "excelimport";

        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + raFolderName + File.separator
            + excelImportFolderName + File.separator + fileName;

        // String path = System.getProperty( "user.home" ) + File.separator +
        // "dhis" + File.separator + excelImportFolderName
        // + File.separator + fileName;

        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + excelImportFolderName
                    + File.separator + fileName;

                // path = newpath + File.separator + excelImportFolderName +
                // File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        String reportId = "";
        String reportName = "";
        String reportType = "";
        String dataSetId = "";
        String reportModel = "";
        String reportFileName = "";
        // String checkerFileName = "";
        int count = 0;

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return;
            }

            NodeList listOfReports = doc.getElementsByTagName( "excelImportReport" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;
                    reportId = reportElement.getAttribute( "id" );

                    NodeList reportNameList = reportElement.getElementsByTagName( "name" );
                    Element reportNameElement = (Element) reportNameList.item( 0 );
                    NodeList textreportNameList = reportNameElement.getChildNodes();
                    reportName = ((Node) textreportNameList.item( 0 )).getNodeValue().trim();

                    NodeList reportTypeList = reportElement.getElementsByTagName( "type" );
                    Element reportTypeElement = (Element) reportTypeList.item( 0 );
                    NodeList textreportTypeList = reportTypeElement.getChildNodes();
                    reportType = ((Node) textreportTypeList.item( 0 )).getNodeValue().trim();

                    NodeList reportModelList = reportElement.getElementsByTagName( "model" );
                    Element reportModelElement = (Element) reportModelList.item( 0 );
                    NodeList textreportModelList = reportModelElement.getChildNodes();
                    reportModel = ((Node) textreportModelList.item( 0 )).getNodeValue().trim();

                    NodeList reportFileNameList = reportElement.getElementsByTagName( "filename" );
                    Element reportFileNameElement = (Element) reportFileNameList.item( 0 );
                    NodeList textreportFileNameList = reportFileNameElement.getChildNodes();
                    reportFileName = ((Node) textreportFileNameList.item( 0 )).getNodeValue().trim();


                    NodeList reportDatasetList = reportElement.getElementsByTagName( "dataset" );
                    Element reportDatasetElement = (Element) reportDatasetList.item( 0 );
                    NodeList textreportDatasetList = reportDatasetElement.getChildNodes();
                    dataSetId = ((Node) textreportDatasetList.item( 0 )).getNodeValue().trim();

                    DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
                    
                    if ( reportType.equals( periodType ) && dataSet.getSources().contains( orgUnit ) )
                    {
                        Report reportObj = new Report( reportId, reportName, reportType, reportModel, reportFileName, dataSetId );
                        reportList.add( count, reportObj );
                        count++;
                        System.out.println( reportName + " : " + reportId );
                    }
                }
            }// end of for loop with s var
        }// try block end
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

    }// getReportList end

}
