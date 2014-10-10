package org.hisp.dhis.alert.idsp.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.alert.util.AlertUtility;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

/**
 * @author Samta Bajpai
 * 
 * @version IDSPOutbreakAction.java Jun 5, 2012 12:43:10 PM
 */

public class IDSPOutbreakAction
    implements Action
{

    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService orgUnitGroupService;

    public void setOrgUnitGroupService( OrganisationUnitGroupService orgUnitGroupService )
    {
        this.orgUnitGroupService = orgUnitGroupService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private AlertUtility alertUtility;

    public void setAlertUtility( AlertUtility alertUtility )
    {
        this.alertUtility = alertUtility;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // ---------------------------------------------------------------
    // Input & Output
    // ---------------------------------------------------------------

    private String resultString;

    public String getResultString()
    {
        return resultString;
    }

    List<OrganisationUnit> immChildrenList;

    public List<OrganisationUnit> getImmChildrenList()
    {
        return immChildrenList;
    }

    Map<String, Integer> orgUnit_ProgramMap;

    public Map<String, Integer> getOrgUnit_ProgramMap()
    {
        return orgUnit_ProgramMap;
    }

    Map<String, String> outBreakAlertMap;

    public Map<String, String> getOutBreakAlertMap()
    {
        return outBreakAlertMap;
    }

    Map<String, String> outBreakAlertColorMap;

    public Map<String, String> getOutBreakAlertColorMap()
    {
        return outBreakAlertColorMap;
    }

    Map<String, Integer> totalEnrollCountForSelDateMap;

    public Map<String, Integer> getTotalEnrollCountForSelDateMap()
    {
        return totalEnrollCountForSelDateMap;
    }

    Integer totalRegCountForSelDate = 0;

    public Integer getTotalRegCountForSelDate()
    {
        return totalRegCountForSelDate;
    }

    Integer totalRegCount = 0;

    public Integer getTotalRegCount()
    {
        return totalRegCount;
    }

    List<Integer> totalRegCountList;

    public List<Integer> getTotalRegCountList()
    {
        return totalRegCountList;
    }

    List<Integer> totalRegCountListForSelDate;

    public List<Integer> getTotalRegCountListForSelDate()
    {
        return totalRegCountListForSelDate;
    }

    List<Program> programList;

    public List<Program> getProgramList()
    {
        return programList;
    }

    String rootOrgUnitName;

    public String getRootOrgUnitName()
    {
        return rootOrgUnitName;
    }

    List<Integer> rootOrgUnitEnrollCountList;

    public List<Integer> getRootOrgUnitEnrollCountList()
    {
        return rootOrgUnitEnrollCountList;
    }

    String drillDownOrgUnitId;

    public void setDrillDownOrgUnitId( String drillDownOrgUnitId )
    {
        this.drillDownOrgUnitId = drillDownOrgUnitId;
    }

    String navigationString;

    public String getNavigationString()
    {
        return navigationString;
    }

    private String toDaysDate;

    public String getToDaysDate()
    {
        return toDaysDate;
    }

    List<String> normInfo;

    public List<String> getNormInfo()
    {
        return normInfo;
    }

    List<String> normNames;

    public List<String> getNormNames()
    {
        return normNames;
    }

    private String populationDeId;

    private Integer orgUnitGroupId;

    private String dataSetId;

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------

    public String execute()
        throws Exception
    {
        //statementManager.initialise();
        int idspFlag = 0;
        if ( currentUserService.getCurrentUser().getId() != 0 )
        {
            UserCredentials userCredentials = userService.getUserCredentialsByUsername( currentUserService
                .getCurrentUsername() );

            for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
            {
                if ( userAuthorityGroup.getAuthorities().contains( ("F_REPORT_IDSP") ) )
                {
                    idspFlag = 1;
                    break;
                }
            }
        }

        if ( idspFlag == 0 )
        {
            return "standard";
        }

        normInfo = new ArrayList<String>();
        normNames = new ArrayList<String>();
        immChildrenList = new ArrayList<OrganisationUnit>();
        programList = new ArrayList<Program>();
        rootOrgUnitEnrollCountList = new ArrayList<Integer>();
        totalRegCountList = new ArrayList<Integer>();
        totalRegCountListForSelDate = new ArrayList<Integer>();
        totalEnrollCountForSelDateMap = new HashMap<String, Integer>();
        orgUnit_ProgramMap = new HashMap<String, Integer>();
        outBreakAlertMap = new HashMap<String, String>();
        outBreakAlertColorMap = new HashMap<String, String>();

        resultString = "";

        navigationString = "IDSP Outbreak";

        String periodIdString = alertUtility.getPeriodIdForIDSPOutBreak();
        String periodId = periodIdString.split( "::" )[0];
        navigationString += " ( " + periodIdString.split( "::" )[1] + " )";

        String populationPeriodId = alertUtility.getPeriodIdForIDSPPopulation();

        normInfo = getNormInfoFromXML();

        if ( normInfo != null && normInfo.size() > 0 )
        {
            List<OrganisationUnit> rootOrgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );

            if ( drillDownOrgUnitId != null )
            {
                rootOrgUnitList.add( organisationUnitService
                    .getOrganisationUnit( Integer.parseInt( drillDownOrgUnitId ) ) );
                List<OrganisationUnit> orgUnitBrach = new ArrayList<OrganisationUnit>( organisationUnitService
                    .getOrganisationUnitBranch( Integer.parseInt( drillDownOrgUnitId ) ) );
                int flag = 1;
                for ( OrganisationUnit orgUnit : orgUnitBrach )
                {
                    if ( currentUserService.getCurrentUser().getOrganisationUnits().contains( orgUnit ) )
                    {
                        flag = 2;
                    }
                    if ( flag == 2 )
                    {
                        navigationString += " -> <a href=\"idspoutbreak.action?drillDownOrgUnitId=" + orgUnit.getId()
                            + "\">" + orgUnit.getName() + "</a>";
                    }
                }
            }
            else
            {
                rootOrgUnitList.addAll( currentUserService.getCurrentUser().getOrganisationUnits() );
            }

            for ( OrganisationUnit orgUnit : rootOrgUnitList )
            {
                rootOrgUnitName = orgUnit.getName() + ", ";
                List<OrganisationUnit> tempOuList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                Collections.sort( tempOuList, new IdentifiableObjectNameComparator() );

                immChildrenList.addAll( tempOuList );

                for ( OrganisationUnit ou : tempOuList )
                {
                    List<OrganisationUnit> childTree = new ArrayList<OrganisationUnit>( organisationUnitService
                        .getOrganisationUnitWithChildren( ou.getId() ) );
                    String orgUnitIdsByComma1 = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class,
                        childTree ) );
                    List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup
                        .getMembers() );
                    childTree.retainAll( orgUnitGroupMembers );
                    String orgUnitIdsByComma = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class,
                        childTree ) );

                    int populationData = alertUtility.getAggregatedData( orgUnitIdsByComma1, populationDeId,
                        populationPeriodId );

                    int confirmedCount = alertUtility.getConfirmedCount( orgUnitIdsByComma, dataSetId, periodId );
                    int totalSubcentreCount = childTree.size();
                    // System.out.println(confirmedCount + " : " +
                    // totalSubcentreCount );

                    for ( String norm : normInfo )
                    {
                        String normId = norm.split( "@:@" )[0];
                        String caseId = norm.split( "@:@" )[1];
                        String deathId = norm.split( "@:@" )[2];
                        String normName = norm.split( "@:@" )[3];

                        int caseData = alertUtility.getAggregatedData( orgUnitIdsByComma, caseId, periodId );

                        int deathData = alertUtility.getAggregatedData( orgUnitIdsByComma, deathId, periodId );

                        if ( deathData >= 1 )
                        {
                            outBreakAlertMap.put( normName + ":" + ou.getId(), deathData + " Deaths" );

                            if ( confirmedCount != totalSubcentreCount )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "PINK" );
                            }
                            else
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "RED" );
                            }
                        }
                        else
                        {
                            long minLimit = Math.round( populationData / 1000.0 );
                            long maxLimit = Math.round( (populationData / 1000.0) * 5 );
                            outBreakAlertMap.put( normName + ":" + ou.getId(), caseData + " Cases" );

                            if ( confirmedCount != totalSubcentreCount )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "PINK" );
                                continue;
                            }

                            if ( minLimit == 0 || maxLimit == 0 )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "WHITE" );
                                continue;
                            }

                            if ( caseData >= maxLimit )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "RED" );
                            }
                            else if ( caseData <= minLimit )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "GREEN" );
                            }
                            else
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "YELLOW" );
                            }
                        }
                    }
                }
            }
        }

        //statementManager.destroy();

        return SUCCESS;
    }

    public List<String> getNormInfoFromXML()
    {
        List<String> normInfo = new ArrayList<String>();
        String raFolderName = alertUtility.getRAFolderName();

        String newpath = "";
        try
        {
            newpath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "OutBreaks.xml";
        }
        catch ( NullPointerException npe )
        {
            System.out.println( "DHIS_HOME is not set" );
            return null;
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( newpath ) );
            if ( doc == null )
            {

                return null;
            }

            populationDeId = doc.getElementsByTagName( "population" ).item( 0 ).getFirstChild().getNodeValue();

            dataSetId = doc.getElementsByTagName( "dataset" ).item( 0 ).getFirstChild().getNodeValue();

            orgUnitGroupId = Integer.parseInt( doc.getElementsByTagName( "orgunitgroup" ).item( 0 ).getFirstChild()
                .getNodeValue() );

            NodeList listOfNorms = doc.getElementsByTagName( "norm" );
            int totalNorms = listOfNorms.getLength();

            for ( int s = 0; s < totalNorms; s++ )
            {
                Element element = (Element) listOfNorms.item( s );
                String normId = element.getAttribute( "id" );
                String caseId = element.getAttribute( "caseid" );
                String deathId = element.getAttribute( "deathid" );
                String lableName = element.getAttribute( "name" );

                if ( normId != null && caseId != null && deathId != null && lableName != null )
                {
                    normInfo.add( normId + "@:@" + caseId + "@:@" + deathId + "@:@" + lableName );
                    normNames.add( lableName );
                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
            return null;
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            return null;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            return null;
        }

        return normInfo;
    }

}
