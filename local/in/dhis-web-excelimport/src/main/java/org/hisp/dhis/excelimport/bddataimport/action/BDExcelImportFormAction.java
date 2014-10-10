package org.hisp.dhis.excelimport.bddataimport.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.excelimport.util.BDImportSheet;
import org.hisp.dhis.excelimport.util.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BDExcelImportFormAction implements Action
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
    
    private List<BDImportSheet> excelImportSheetList;

    public List<BDImportSheet> getExcelImportSheetList()
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
        
        excelImportSheetList = new ArrayList<BDImportSheet>();
        
        getExcelImportSheetList( "BDDataImportSheetList.xml" );
        
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
        String checkerTemplateName;
        String checkerRangeForHeader;
        String checkerRangeForData;

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

            NodeList listOfReports = doc.getElementsByTagName( "BDImportSheet" );
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

                    nodeList = reportElement.getElementsByTagName( "periodType" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    periodicity = ((Node) nodeList.item( 0 )).getNodeValue().trim();

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

                    BDImportSheet bdImportSheet = new BDImportSheet( xmlTemplateName, displayName, periodicity,checkerTemplateName, checkerRangeForHeader, checkerRangeForData);
                    
                    excelImportSheetList.add( count, bdImportSheet );

                    count++;
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
    }

}
