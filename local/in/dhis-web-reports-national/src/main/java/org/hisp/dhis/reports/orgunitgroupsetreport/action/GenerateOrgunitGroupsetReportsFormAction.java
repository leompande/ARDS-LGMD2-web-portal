/*
 * Copyright (c) 2004-2009, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.reports.orgunitgroupsetreport.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.util.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

/**
 * @author Brajesh Murari
 * @version $Id: GenerateOrgunitGroupsetReportsFormAction.java 86 2009-06-08 11:59:30Z brajesh $
 */

public class GenerateOrgunitGroupsetReportsFormAction
    implements Action
{
  
        // -------------------------------------------------------------------------
        // Dependencies
        // -------------------------------------------------------------------------

        private OrganisationUnitGroupService organisationUnitGroupService;
        
        public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
        {
            this.organisationUnitGroupService = organisationUnitGroupService;
        }
        
        private PeriodService periodService;

        public void setPeriodService( PeriodService periodService )
        {
            this.periodService = periodService;
        }
        
        private ReportService reportService;

        public void setReportService( ReportService reportService )
        {
            this.reportService = reportService;
        }
              
        // -------------------------------------------------------------------------
        // Getter & Setter
        // -------------------------------------------------------------------------
             
        private String message;
        
        public String getMessage()
        {
            return message;
        }
        private List<Report_in> reportList;
        
        public List<Report_in> getReportList()
        {
            return reportList;
        }
          
        private Collection<Period> periods = new ArrayList<Period>();

        public Collection<Period> getPeriods()
        {
            return periods;
        }
  
        private SimpleDateFormat simpleDateFormat;

        public SimpleDateFormat getSimpleDateFormat()
        {
            return simpleDateFormat;
        }
        
        private Collection<OrganisationUnitGroupSet> organisationUnitGroupSets;

        public Collection<OrganisationUnitGroupSet> getOrganisationUnitGroupSets()
        {
            return organisationUnitGroupSets;
        }
        
        private String raFolderName;
        
      
        // -------------------------------------------------------------------------
        // Action implementation
        // -------------------------------------------------------------------------

        public String execute()
            throws Exception
        {
            /* OrganisationUnitGroupSet */
            organisationUnitGroupSets = organisationUnitGroupService.getAllOrganisationUnitGroupSets();
            
            /* For Report List*/
            
          //  reportList = new ArrayList<Report>(); 
            
            /*ra Folder Name*/
            raFolderName = reportService.getRAFolderName();       
            
            /*For date formate*/
            simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );      
            PeriodType monthlyPeriodType = periodService.getPeriodTypeByName( "Monthly" );
            periods.addAll( periodService.getPeriodsByPeriodType( monthlyPeriodType ) );      
            
            getSelectedReportList();

            return SUCCESS;
        }

        public void getSelectedReportList()
        {
            String fileName = "SpecialReportsList.xml";
            String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
                + File.separator + fileName;
            try
            {
                String newpath = System.getenv( "DHIS2_HOME" );
                if ( newpath != null )
                {
                    path = newpath + File.separator + File.separator + raFolderName + File.separator + fileName;
                }
                /*
                else
                {
                    message = "Please Check " + raFolderName + "Directorry For SpecialReportsList.xml File";
                    return "error";
                }
                */
            }
            catch ( NullPointerException npe )
            {
                // do nothing, but we might be using this somewhere without
                // USER_HOME set, which will throw a NPE
            }

            String reportId = "";
            String reportName = "";
            String reportType = "";           
            String reportModel = "";
            String reportFileName = "";
            //String reportOrgUnitGroupSet = "";
            String reportLevel = "";
            
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

                        NodeList reportLevelList = reportElement.getElementsByTagName( "level" );
                        Element reportLevelElement = (Element) reportLevelList.item( 0 );
                        NodeList textreportLevelList = reportLevelElement.getChildNodes();
                        reportLevel = ((Node) textreportLevelList.item( 0 )).getNodeValue().trim();
                        
                       // Report reportObj = new Report(reportId, reportName, reportType, reportModel, reportFileName, reportLevel);
                        
                        System.out.println("reportId :" + reportId + " : " +  "reportFileName" + reportFileName );
                       
                       //reportList.add( reportObj );  
                        
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


