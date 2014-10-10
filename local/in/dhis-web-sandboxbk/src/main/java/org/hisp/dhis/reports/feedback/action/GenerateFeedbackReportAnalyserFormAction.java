package org.hisp.dhis.reports.feedback.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.api.Report;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.ActionSupport;

public class GenerateFeedbackReportAnalyserFormAction
    extends ActionSupport
    {

        // -------------------------------------------------------------------------
        // Dependencies
        // -------------------------------------------------------------------------

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

        public OrganisationUnitService getOrganisationUnitService()
        {
            return organisationUnitService;
        }

        // -------------------------------------------------------------------------
        // Constants
        // -------------------------------------------------------------------------

        private final int ALL = 0;

        public int getALL()
        {
            return ALL;
        }

        // -------------------------------------------------------------------------
        // Properties
        // -------------------------------------------------------------------------

        private Collection<OrganisationUnit> organisationUnits;

        public Collection<OrganisationUnit> getOrganisationUnits()
        {
            return organisationUnits;
        }

        private Collection<Period> periods = new ArrayList<Period>();

        public Collection<Period> getPeriods()
        {
            return periods;
        }

        private Collection<PeriodType> periodTypes;

        public Collection<PeriodType> getPeriodTypes()
        {
            return periodTypes;
        }

        // -------------------------------------------------------------------------
        // Action implementation
        // -------------------------------------------------------------------------

        public String execute()
            throws Exception
        {
            /* OrganisationUnit */
            organisationUnits = organisationUnitService.getAllOrganisationUnits();

            /* Period Info */
            periodTypes = periodService.getAllPeriodTypes();

            for ( PeriodType type : periodTypes )
            {
                periods.addAll( periodService.getPeriodsByPeriodType( type ) );
            }

            return SUCCESS;
        }

        public void getReportList()
        {
            String fileName = "nationalReportsList.xml";
            String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"
                + File.separator + fileName;
            try
            {
                String newpath = System.getenv( "USER_HOME" );
                if ( newpath != null )
                {
                    path = newpath + File.separator + "dhis" + File.separator + "ra_national" + File.separator + fileName;
                }
            }
            catch ( NullPointerException npe )
            {
                // do nothing, but we might be using this somewhere without
                // USER_HOME set, which will throw a NPE
            }

            String reportId = "";
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

                NodeList listOfReports = doc.getElementsByTagName( "report" );
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

                        NodeList reportTypeList = reportElement.getElementsByTagName( "type" );
                        Element reportTypeElement = (Element) reportTypeList.item( 0 );
                        NodeList textreportTypeList = reportTypeElement.getChildNodes();

                        NodeList reportModelList = reportElement.getElementsByTagName( "model" );
                        Element reportModelElement = (Element) reportModelList.item( 0 );
                        NodeList textreportModelList = reportModelElement.getChildNodes();

                        NodeList reportLevelList = reportElement.getElementsByTagName( "level" );
                        Element reportLevelElement = (Element) reportLevelList.item( 0 );
                        NodeList textreportLevelList = reportLevelElement.getChildNodes();
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
