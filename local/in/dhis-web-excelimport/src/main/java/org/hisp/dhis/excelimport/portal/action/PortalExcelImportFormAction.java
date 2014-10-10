package org.hisp.dhis.excelimport.portal.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.excelimport.util.PortalImportSheet;
import org.hisp.dhis.excelimport.util.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class PortalExcelImportFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
    
    private List<PortalImportSheet> excelImportSheetList;

    public List<PortalImportSheet> getExcelImportSheetList()
    {
        return excelImportSheetList;
    }

    private String raFolderName;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        raFolderName = reportService.getRAFolderName();
        
        excelImportSheetList = new ArrayList<PortalImportSheet>();
        
        getExcelImportSheetList( "portalDataImportSheetList.xml" );
        
        return SUCCESS;
    }
    
    
    public void getExcelImportSheetList( String reportListFileName )
    {
        String fileName = reportListFileName;

        String excelImportFolderName = "excelimport";

        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + raFolderName + File.separator + excelImportFolderName + File.separator + fileName;

        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println("DHIS2_HOME is not set");
        }

        String xmlTemplateName;
        String displayName;
        String periodicity;
        String proforma;
        String checkerTemplateName;
        String checkerRangeForHeader;
        String checkerRangeForData;
        String datasetId;
        String orgunitGroupId;
        String facilityStart;

        int count = 0;

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at DHIS HOME" );
                return;
            }

            NodeList listOfReports = doc.getElementsByTagName( "PortalImportSheet" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;
                    
                    NodeList nodeList = reportElement.getElementsByTagName( "xmlTemplateName" );
                    Element element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    xmlTemplateName = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "displayName" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    displayName = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "periodicity" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    periodicity = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "proforma" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    proforma = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "checkerTemplateName" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    checkerTemplateName = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "checkerRangeForHeader" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    checkerRangeForHeader = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "checkerRangeForData" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    checkerRangeForData = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "dataset" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    datasetId = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "orgunitgroup" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    orgunitGroupId = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "facilityStart" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    facilityStart = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    PortalImportSheet portalImportSheet = new PortalImportSheet( xmlTemplateName, displayName, periodicity, proforma, checkerTemplateName, checkerRangeForHeader, checkerRangeForData, datasetId, orgunitGroupId, facilityStart );
                    
                    excelImportSheetList.add( count, portalImportSheet );

                    count++;
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
    }

}
