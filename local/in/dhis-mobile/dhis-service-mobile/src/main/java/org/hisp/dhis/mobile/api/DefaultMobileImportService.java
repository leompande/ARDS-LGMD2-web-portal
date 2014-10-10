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
package org.hisp.dhis.mobile.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;



import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.functions.Today;
import org.hibernate.Session;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.mobile.SmsService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.UserStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultMobileImportService
    implements MobileImportService
{

    private static final Log LOG = LogFactory.getLog( DefaultMobileImportService.class );

	public static final String ORGUNITAUTOFORMAT = "ORGUNITAUTOFORMAT";
	public static final String YES = "0";
	public static final String NO = "1";
	



	private static final int ADDCASE = 1;
	private static final int ERRORCASE = 2;
	private static final int UPDATECASE = 3;
	

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SendSMSService sendSMSService;

    public void setSendSMSService( SendSMSService sendSMSService )
    {
        this.sendSMSService = sendSMSService;
    }

    private ReceiveSMSService receiveSMSService;

    public void setReceiveSMSService( ReceiveSMSService receiveSMSService )
    {
        this.receiveSMSService = receiveSMSService;
    }

    SmsService smsService;

    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    private UserService userService;

    public void setUserService(UserService userService) 
    {
	this.userService = userService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService(ConstantService constantService) {
  		this.constantService = constantService;
  	}
    
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private ProgramService programService;
    
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private ProgramInstanceService programInstanceService;
    
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private ProgramStageInstanceService programStageInstanceService;
    
    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private PatientDataValueService patientDataValueService;
    
    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }
        
    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService(
			PatientAttributeValueService patientAttributeValueService) {
		this.patientAttributeValueService = patientAttributeValueService;
	}

    
    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private String storedBy;

    // -------------------------------------------------------------------------
    // Services
    // -------------------------------------------------------------------------

    @Override
    public void readAllMessages()
    {
        smsService.readAllMessages();
        System.out.println( "Message reading done" );
    }

    @Override
    public User getUserInfo( String mobileNumber )
    {
        Collection<User> userList = userStore.getUsersByPhoneNumber( mobileNumber );

        User selectedUser = null;

        if ( userList != null && userList.size() > 0 )
        {
            selectedUser = userList.iterator().next();
        }

        return selectedUser;
    }

    @Override
    public Period getPeriodInfo( String startDate, String periodType ) throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        List<Period> periods = null;
        PeriodType pt = null;
        if ( periodType.equals( "3" ) )
        {
            pt = new MonthlyPeriodType();
            periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
        }
        else if ( periodType.equals( "1" ) )
        {
            pt = new DailyPeriodType();
            periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
        }
        else if ( periodType.equals( "6" ) )
        {
            pt = new YearlyPeriodType();
            periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
        }
        else if ( periodType.equals( "2" ) )
        {
            pt = new WeeklyPeriodType();
            periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
        }

        for ( Period period : periods )
        {
            String tempDate = dateFormat.format( period.getStartDate() );
            if ( tempDate.equalsIgnoreCase( startDate ) )
            {
                return period;
            }
        }

        Period period = pt.createPeriod( dateFormat.parse( startDate ) );
        period = reloadPeriodForceAdd( period );
        periodService.addPeriod( period );

        return period;
    }

    private final Period reloadPeriod( Period period )
    {
        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

    @Override
    public MobileImportParameters getParametersFromXML( String fileName )
        throws Exception
    {
        File importFile = locationManager.getFileForReading( fileName, "mi", "pending" );

        String mobileNumber;
        String smsTime;
        String startDate;
        String periodType;
        String formType;
        String anmName;
        String anmQuery;

        String tempDeid;
        String tempDataValue;

        Map<String, String> dataValues = new HashMap<String, String>();

        MobileImportParameters mobileImportParameters = new MobileImportParameters();

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( importFile );
            if ( doc == null )
            {
                return null;
            }

            // To get Mobile Number
            NodeList sourceInfo = doc.getElementsByTagName( "source" );
            Element sourceInfoElement = (Element) sourceInfo.item( 0 );
            NodeList textsourceInfoNameList = sourceInfoElement.getChildNodes();
            mobileNumber = textsourceInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setMobileNumber( mobileNumber );

            // To get Period
            NodeList periodInfo = doc.getElementsByTagName( "period" );
            Element periodInfoElement = (Element) periodInfo.item( 0 );
            NodeList textperiodInfoNameList = periodInfoElement.getChildNodes();
            startDate = textperiodInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setStartDate( startDate );

            // To get TimeStamp
            NodeList timeStampInfo = doc.getElementsByTagName( "timeStamp" );
            Element timeStampInfoElement = (Element) timeStampInfo.item( 0 );
            NodeList texttimeStampInfoNameList = timeStampInfoElement.getChildNodes();
            smsTime = texttimeStampInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setSmsTime( smsTime );

            // To get PeriodType
            NodeList periodTypeInfo = doc.getElementsByTagName( "periodType" );
            Element periodTypeInfoElement = (Element) periodTypeInfo.item( 0 );
            NodeList textPeriodTypeInfoNameList = periodTypeInfoElement.getChildNodes();
            periodType = textPeriodTypeInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setPeriodType( periodType );

            // To get FormType
            NodeList formTypeInfo = doc.getElementsByTagName( "formtype" );
            Element formTypeInfoElement = (Element) formTypeInfo.item( 0 );
            NodeList formTypeInfoNameList = formTypeInfoElement.getChildNodes();
            formType = formTypeInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setFormType( formType );

            if ( formType.equalsIgnoreCase( MobileImportParameters.FORM_TYPE_DATAFORM ) )
            {
                NodeList listOfDataValues = doc.getElementsByTagName( "dataValue" );
                int totalDataValues = listOfDataValues.getLength();
                for ( int s = 0; s < totalDataValues; s++ )
                {
                    Node dataValueNode = listOfDataValues.item( s );
                    if ( dataValueNode.getNodeType() == Node.ELEMENT_NODE )
                    {
                        Element dataValueElement = (Element) dataValueNode;

                        NodeList dataElementIdList = dataValueElement.getElementsByTagName( "dataElement" );
                        Element dataElementElement = (Element) dataElementIdList.item( 0 );
                        NodeList textdataElementIdList = dataElementElement.getChildNodes();
                        tempDeid = textdataElementIdList.item( 0 ).getNodeValue().trim();

                        NodeList valueList = dataValueElement.getElementsByTagName( "value" );
                        Element valueElement = (Element) valueList.item( 0 );
                        NodeList textValueElementList = valueElement.getChildNodes();
                        tempDataValue = textValueElementList.item( 0 ).getNodeValue();

                        String tempDeID = tempDeid;
                        // Integer tempDV = Integer.parseInt( tempDataValue );

                        dataValues.put( tempDeID, tempDataValue );
                    }
                }// end of for loop with s var

                mobileImportParameters.setDataValues( dataValues );
            }
            else if ( formType.equalsIgnoreCase( MobileImportParameters.FORM_TYPE_ANMREGFORM ) )
            {
                // To get ANM Name
                NodeList anmNameInfo = doc.getElementsByTagName( "anmname" );
                Element anmNameInfoElement = (Element) anmNameInfo.item( 0 );
                NodeList anmNameInfoNameList = anmNameInfoElement.getChildNodes();
                anmName = anmNameInfoNameList.item( 0 ).getNodeValue().trim();

                mobileImportParameters.setAnmName( anmName );
            }
            else if ( formType.equalsIgnoreCase( MobileImportParameters.FORM_TYPE_ANMQUERYFORM ) )
            {
                // To get ANM Query
                NodeList anmQueryInfo = doc.getElementsByTagName( "anmquery" );
                Element anmQueryInfoElement = (Element) anmQueryInfo.item( 0 );
                NodeList anmQueryInfoNameList = anmQueryInfoElement.getChildNodes();
                anmQuery = anmQueryInfoNameList.item( 0 ).getNodeValue().trim();

                mobileImportParameters.setAnmQuery( anmQuery );
            }
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

        return mobileImportParameters;

    }// getParametersFromXML end

    @Override
    public List<String> getImportFiles()
    {
        List<String> fileNames = new ArrayList<String>();

        try
        {
            String importFolderPath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
                + "mi" + File.separator + "pending";

            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                importFolderPath = newpath + File.separator + "mi" + File.separator + "pending";
            }

            File dir = new File( importFolderPath );

            String[] files = dir.list();

            fileNames = Arrays.asList( files );
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }

        return fileNames;
    }

    public int moveFile( File source, File dest )
        throws IOException
    {

        if ( !dest.exists() )
        {
            dest.createNewFile();
        }

        InputStream in = null;

        OutputStream out = null;

        try
        {
            in = new FileInputStream( source );

            out = new FileOutputStream( dest );

            byte[] buf = new byte[1024];

            int len;

            while ( (len = in.read( buf )) > 0 )
            {
                out.write( buf, 0, len );
            }
        }
        catch ( Exception e )
        {
            return -1;
        }
        finally
        {
            in.close();

            out.close();
        }

        return 1;

    }

    @Override
    public void moveImportedFile( String fileName )
    {
        try
        {
            String sourceFilePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "mi"
                + File.separator + "pending" + File.separator + fileName;

            String destFilePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "mi"
                + File.separator + "completed" + File.separator + fileName;

            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                sourceFilePath = newpath + File.separator + "mi" + File.separator + "pending" + File.separator
                    + fileName;

                destFilePath = newpath + File.separator + "mi" + File.separator + "completed" + File.separator
                    + fileName;
            }

            File sourceFile = new File( sourceFilePath );

            File destFile = new File( destFilePath );

            int status = moveFile( sourceFile, destFile );

            if ( status == 1 )
            {
                sourceFile.delete();
            }
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }

    @Override
    public void moveFailedFile( String fileName )
    {
        try
        {
            String sourceFilePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "mi"
                + File.separator + "pending" + File.separator + fileName;

            String destFilePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "mi"
                + File.separator + "bounced" + File.separator + fileName;

            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                sourceFilePath = newpath + File.separator + "mi" + File.separator + "pending" + File.separator
                    + fileName;

                destFilePath = newpath + File.separator + "mi" + File.separator + "bounced" + File.separator + fileName;
            }

            File sourceFile = new File( sourceFilePath );

            File destFile = new File( destFilePath );

            int status = moveFile( sourceFile, destFile );

            if ( status == 1 )
            {
                sourceFile.delete();
            }
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }

    @Override
    @Transactional
    public void importPendingFiles()
    {
        try
        {
            List<String> fileNames = new ArrayList<String>( getImportFiles() );

            for ( String importFile : fileNames )
            {
                String statusMsg = importXMLFile( importFile );
                String senderInfo = importFile.replace( ".xml", "" );
                SendSMS sendSMS = sendSMSService.getSendSMS( senderInfo );
                
                if( sendSMS == null )
                {
                    sendSMS = new SendSMS( senderInfo, statusMsg );    
                    sendSMSService.addSendSMS( sendSMS );
                }
                else
                {
                    sendSMS.setSendingMessage( statusMsg );
                    sendSMSService.updateSendSMS( sendSMS );
                }
            }
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }

    @Transactional
    public String importANMRegData( String importFile, MobileImportParameters mobImportParameters )
    {
        String importStatus = "";

        try
        {
            User curUser = getUserInfo( mobImportParameters.getMobileNumber() );

            if ( curUser != null )
            {
              //  UserCredentials userCredentials = userStore.getUserCredentials( curUser );
            	  UserCredentials userCredentials = userService.getUserCredentials( curUser );
            	
                if ( (userCredentials != null)
                    && (mobImportParameters.getMobileNumber().equals( curUser.getPhoneNumber() )) )
                {
                }
                else
                {
                    LOG.error( " Import File Contains Unrecognised Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );
                    return "Phone number is not registered to any facility. Please contact admin";
                }

                List<OrganisationUnit> sources = new ArrayList<OrganisationUnit>( curUser.getOrganisationUnits() );

                if ( sources == null || sources.size() <= 0 )
                {
                    LOG.error( " No User Exists with corresponding Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );

                    return "Phone number is not registered to any facility. Please contact admin";
                }

                OrganisationUnit source = sources.get( 0 );
                String anmName = mobImportParameters.getAnmName();

                if ( source == null || anmName == null || anmName.trim().equalsIgnoreCase( "" ) )
                {
                    LOG.error( importFile + " Import File is not Properly Formated" );
                    moveFailedFile( importFile );

                    return "Data not Received Properly, Please send again";
                }
                source.setComment( anmName );
                organisationUnitService.updateOrganisationUnit( source );

                moveImportedFile( importFile );

                importStatus = "YOUR NAME IS REGISTERD SUCCESSFULLY";
            }
            else
            {
                LOG.error( importFile + " Phone number not found... Sending to Bounced" );
                importStatus = "Phone number is not registered to any facility. Please contact admin";
                moveFailedFile( importFile );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LOG.error( e.getMessage() );
            LOG.error( "Exception caused in importing... Moving to Bounced" );

            importStatus = "Data not Received Properly, Please send again";
            moveFailedFile( importFile );
        }
        finally
        {
        }

        return importStatus;
    }

    @Transactional
    public String importANMQueryData( String importFile, MobileImportParameters mobImportParameters )
    {
        String importStatus = "";

        try
        {
            User curUser = getUserInfo( mobImportParameters.getMobileNumber() );

            if ( curUser != null )
            {
                //UserCredentials userCredentials = userStore.getUserCredentials( curUser );
                UserCredentials userCredentials = userService.getUserCredentials( curUser );
                
                if ( (userCredentials != null)
                    && (mobImportParameters.getMobileNumber().equals( curUser.getPhoneNumber() )) )
                {
                }
                else
                {
                    LOG.error( " Import File Contains Unrecognised Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );
                    return "Phone number is not registered to any facility. Please contact admin";
                }

                List<OrganisationUnit> sources = new ArrayList<OrganisationUnit>( curUser.getOrganisationUnits() );

                if ( sources == null || sources.size() <= 0 )
                {
                    LOG.error( " No User Exists with corresponding Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );

                    return "Phone number is not registered to any facility. Please contact admin";
                }

                String anmQuery = mobImportParameters.getAnmQuery();

                if ( anmQuery == null || anmQuery.trim().equalsIgnoreCase( "" ) )
                {
                    LOG.error( importFile + " Import File is not Properly Formated" );
                    moveFailedFile( importFile );

                    return "Data not Received Properly, Please send again";
                }

                ReceiveSMS receiveSMS = new ReceiveSMS( importFile, anmQuery );
                receiveSMSService.addReceiveSMS( receiveSMS );

                moveImportedFile( importFile );

                importStatus = "YOUR Query IS REGISTERD SUCCESSFULLY";
            }
            else
            {
                LOG.error( importFile + " Phone number not found... Sending to Bounced" );
                importStatus = "Phone number is not registered to any facility. Please contact admin";
                moveFailedFile( importFile );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LOG.error( e.getMessage() );
            LOG.error( "Exception caused in importing... Moving to Bounced" );

            importStatus = "Data not Received Properly, Please send again";
            moveFailedFile( importFile );
        }
        finally
        {
        }

        return importStatus;
    }

    @Transactional
    private OrganisationUnit getOrganisationUnitByPhone( String phoneNumber )
    {
        try
        {
            String query = "SELECT organisationunitid FROM organisationunit WHERE phoneNumber LIKE '" + phoneNumber
                + "'";
            SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
            if ( sqlResultSet != null && sqlResultSet.next() )
            {
                Integer orgUnitId = sqlResultSet.getInt( 1 );
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
                if ( orgUnit != null )
                    return orgUnit;
            }

            return null;
        }
        catch ( Exception e )
        {
            System.out.println( "Exception occurred while getting OrganisationUnit by phone number" );

            return null;
        }
    }

    @Transactional
    private User getUserbyOrgUnit( int orgUnitId )
    {
        try
        {
            String query = "SELECT userinfoid FROM usermembership WHERE organisationunitid =" + orgUnitId;

            SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
            if ( sqlResultSet != null && sqlResultSet.next() )
            {
                Integer userId = sqlResultSet.getInt( 1 );
              //  User user = userStore.getUser( userId );
                User user = userService.getUser(userId);
                if ( user != null )
                    return user;
            }
            
            return null;
        }
        catch( Exception e )
        {
            System.out.println( "Exception occurred while getting User by orgunit id" );
            System.out.println( e.getMessage() );
            return null;
        }
    }

    @Override
    @Transactional
    public String importXMLFile( String importFile )
    {
        int insertFlag = 1;
        String insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";
        String importStatus = "";

        try
        {
            MobileImportParameters mobImportParameters = getParametersFromXML( importFile );

            if ( mobImportParameters == null )
            {
                LOG.error( importFile + " Import File is not Properly Formatted" );

                moveFailedFile( importFile );

                return "Data not Received Properly, Please send again";
            }

            // Checking for FormType, if formtype is ANMREG
            if ( mobImportParameters.getFormType().equalsIgnoreCase( MobileImportParameters.FORM_TYPE_ANMREGFORM ) )
            {
                importStatus = importANMRegData( importFile, mobImportParameters );
                return importStatus;
            }
            else if ( mobImportParameters.getFormType().equalsIgnoreCase( MobileImportParameters.FORM_TYPE_ANMQUERYFORM ) )
            {
                importStatus = importANMQueryData( importFile, mobImportParameters );
                return importStatus;
            }

            OrganisationUnit source = getOrganisationUnitByPhone( mobImportParameters.getMobileNumber() );

            if ( source == null )
            {
                LOG.error( " No Faciliy Exists with corresponding Phone Number : "+ mobImportParameters.getMobileNumber() );
                moveFailedFile( importFile );
                return "Phone number is not registered to any facility. Please contact admin";
            }

            User curUser = getUserbyOrgUnit( source.getId() );

            if ( curUser == null )
            {
                LOG.error( " No User Exists with corresponding Facility : " + mobImportParameters.getMobileNumber() );
                storedBy = "[unknown]-" + mobImportParameters.getMobileNumber();
            }
            else
            {
                //UserCredentials userCredentials = userStore.getUserCredentials( curUser );
                UserCredentials userCredentials = userService.getUserCredentials( curUser );
            	
                storedBy = userCredentials.getUsername();
            }

            Period period = getPeriodInfo( mobImportParameters.getStartDate(), mobImportParameters.getPeriodType() );

            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            SimpleDateFormat monthFormat = new SimpleDateFormat( "MMM-yy" );

            Date timeStamp = dateFormat.parse( mobImportParameters.getSmsTime() );

            long t;
            if ( timeStamp == null )
            {
                Date d = new Date();
                t = d.getTime();
            }
            else
            {
                t = timeStamp.getTime();
            }

            java.sql.Date lastUpdatedDate = new java.sql.Date( t );

            Map<String, String> dataValueMap = new HashMap<String, String>( mobImportParameters.getDataValues() );

            if ( dataValueMap == null || dataValueMap.size() <= 0 )
            {
                LOG.error( "dataValue map is null" );
            }
            else if ( source == null )
            {
                LOG.error( "source is null" );
            }
            else if ( period == null )
            {
                LOG.error( "period is null" );
            }
            else if ( timeStamp == null )
            {
                LOG.error( "timeStamp is null" );
            }

            if ( source == null || period == null || timeStamp == null || dataValueMap == null || dataValueMap.size() <= 0 )
            {
                LOG.error( importFile + " Import File is not Properly Formated" );
                moveFailedFile( importFile );
                return "Data not Received Properly, Please send again";
            }

            Set<String> keys = dataValueMap.keySet();

            for ( String key : keys )
            {
                String parts[] = key.split( "\\." );

                String deStr = parts[0];

                String optStr = parts[1];

                String value = String.valueOf( dataValueMap.get( key ) );

                DataElement dataElement = dataElementService.getDataElement( Integer.valueOf( deStr ) );

                DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

                optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.valueOf( optStr ) );

                DataValue dataValue = dataValueService.getDataValue( source, dataElement, period, optionCombo );

                if ( value.trim().equalsIgnoreCase( "" ) )
                {
                    value = null;
                }

                if ( dataValue == null )
                {
                    if ( value != null )
                    {
                        insertQuery += "( " + dataElement.getId() + ", " + period.getId() + ", " + source.getId()
                            + ", " + optionCombo.getId() + ", '" + value + "', '" + storedBy + "', '" + lastUpdatedDate
                            + "' ), ";
                        insertFlag = 2;
                    }
                }
                else
                {
                    /*
                    dataValue.setValue( value );
                    dataValue.setTimestamp( timeStamp );
                    dataValue.setStoredBy( storedBy );
                    dataValueService.updateDataValue( dataValue );
                    */
                    String updateQuery = "UPDATE datavalue SET value = '"+ value +"', storedby = '"+ storedBy +"', lastupdated = '"+ lastUpdatedDate +"' " +
                                            "WHERE dataelementid = "+ dataElement.getId() +
                                                " AND periodid = "+ period.getId() +
                                                " AND sourceid = "+ source.getId() +
                                                " AND categoryoptioncomboid = "+optionCombo.getId();
                    
                    jdbcTemplate.update( updateQuery );
                }
            }

            if ( insertFlag != 1 )
            {
                insertQuery = insertQuery.substring( 0, insertQuery.length() - 2 );

                jdbcTemplate.update( insertQuery );
            }

            moveImportedFile( importFile );

            if ( period.getPeriodType().getName().equalsIgnoreCase( "monthly" ) )
            {
                importStatus = "THANK YOU FOR SENDING MONTHLY REPORT FOR " + monthFormat.format( period.getStartDate() );
            }
            else if ( period.getPeriodType().getName().equalsIgnoreCase( "daily" ) )
            {
                importStatus = "THANK YOU FOR SENDING DAILY REPORT FOR " + dateFormat.format( period.getStartDate() );
            }
            else
            {
                importStatus = "THANK YOU FOR SENDING REPORT FOR " + dateFormat.format( period.getStartDate() ) + " : "
                    + dateFormat.format( period.getEndDate() );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LOG.error( e.getMessage() );
            LOG.error( "Exception caused in importing... Moving to Bounced" );

            importStatus = "Data not Received Properly, Please send again";
            moveFailedFile( importFile );
        }
        finally
        {
        }

        return importStatus;
    }

    public void importInteractionMessage( String smsText, String sender, Date sendTime )
    {

        String insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";

        try
        {
            String[] smstext = smsText.split( "#" );

            System.out.println( "original text: " + smsText );

            String dataelementid = smstext[1];
            String periodid = smstext[2];
            String comboid = smstext[3];
            String value = smstext[4];

            OrganisationUnit source = getOrganisationUnitByPhone( sender );

            System.out.println( "-----------------source--------------" + source );

            User curUser = getUserbyOrgUnit( source.getId() );

            // User curUser = userStore.getUser(1);

            // User curUser = null;

            if ( curUser == null )
            {
                LOG.error( " No User Exists with corresponding Facility : " + sender );

                storedBy = "[unknown]-" + sender;
            }
            else
            {
//                UserCredentials userCredentials = userStore.getUserCredentials( curUser );
            	  UserCredentials userCredentials = userService.getUserCredentials( curUser );
              	
                storedBy = userCredentials.getUsername();
            }
            DataElement dataElement = dataElementService.getDataElement( dataelementid );

            DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

            optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( comboid );

            Period period = periodService.getPeriod( Integer.parseInt( periodid ) );

            DataValue dataValue = dataValueService.getDataValue( source, dataElement, period, optionCombo );

            /*
             * if( value.trim().equalsIgnoreCase("") ) { value = null; }
             */

            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            SimpleDateFormat monthFormat = new SimpleDateFormat( "MMM-yy" );

            int date = sendTime.getDate();
            int month = sendTime.getMonth() + 1;
            int year = sendTime.getYear() + 1900;
            int hour = sendTime.getHours();
            int minutes = sendTime.getMinutes();
            int seconds = sendTime.getSeconds();

            String sendtime = "";

            sendtime += "" + year;

            sendtime += "-";

            if ( month < 10 )
            {
                sendtime += "0" + month;
            }
            else
            {
                sendtime += "" + month;
            }

            sendtime += "-";

            if ( date < 10 )
            {
                sendtime += "0" + date;
            }
            else
            {
                sendtime += "" + date;
            }

            sendtime += "_";

            if ( hour < 10 )
            {
                sendtime += "0" + hour;
            }
            else
            {
                sendtime += "" + hour;
            }

            sendtime += "-";

            if ( minutes < 10 )
            {
                sendtime += "0" + minutes;
            }
            else
            {
                sendtime += "" + minutes;
            }

            sendtime += "-";

            if ( seconds < 10 )
            {
                sendtime += "0" + seconds;
            }
            else
            {
                sendtime += "" + seconds;
            }

            System.out.println( "Time: " + sendtime );
            Date timeStamp = dateFormat.parse( sendtime );

            long t;
            if ( timeStamp == null )
            {
                Date d = new Date();
                t = d.getTime();
            }
            else
            {
                t = timeStamp.getTime();
            }

            java.sql.Date lastUpdatedDate = new java.sql.Date( t );

            System.out.println( "( " + Integer.parseInt( dataelementid ) + ", " + period.getId() + ", "
                + source.getId() + ", " + Integer.parseInt( comboid ) + ", '" + value + "', '" + storedBy + "', '"
                + lastUpdatedDate + "' ) " );
            if ( dataValue == null )
            {
                if ( value != null )
                {
                    insertQuery += "( " + Integer.parseInt( dataelementid ) + ", " + period.getId() + ", "
                        + source.getId() + ", " + Integer.parseInt( comboid ) + ", '" + value + "', '" + storedBy
                        + "', '" + lastUpdatedDate + "' ) ";
                    jdbcTemplate.update( insertQuery );
                }
            }
            else
            {
                dataValue.setValue( value );

                dataValue.setTimestamp( timeStamp );

                dataValue.setStoredBy( storedBy );

                dataValueService.updateDataValue( dataValue );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LOG.error( e.getMessage() );
            LOG.error( "Interactive message not processed" );
        }

    }

	@Override
	@Transactional
	public void registerPatientData(String unCompressedText, String sender, Date sendTime ) {
	
		//	SN#ebc:sc:sname:fname:21032013:F:5:5665222222
		//		SU#UID#ebc:sc:sname:fname:21032013:F:5:5665222222
		
		String statusMessage=null;
		String stage;
		Date registrationDate;
		 String fullName = "";
         String firstName = "";
         String middleName = "";
         String lastName = "";
         String gender = null;
         boolean isUpdateCase = false;
         String UID = null;
     	String data[];
     	Patient patient;
         
    	
         int fatherNamePatientAttributeId = (int) constantService.getConstantByName("PatientAttributeFatherNameId").getValue();
         int schoolCodePatientAttributeId = (int) constantService.getConstantByName("PatientAttributeSchoolCodeId").getValue();
    		
         
         List<PatientAttributeValue> patientAttributeValues = new ArrayList<PatientAttributeValue>();
         
		String smsData[] = unCompressedText.split("#");
		stage = smsData[0];
		
		if (stage.substring(1, 2).equalsIgnoreCase("U")){
			isUpdateCase = true;
			UID = smsData[1].trim();
			 data = smsData[2].split(":");
		}else {
			 data = smsData[1].split(":");
		}
		
		registrationDate = Calendar.getInstance().getTime();
		
		
		
		String path = System.getenv( "DHIS2_HOME" ) +  File.separator + "mi" + File.separator + "formIDLayout.csv";
        Properties props = new Properties();
        try {
			props.load( new FileReader( path ) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String mappingString = props.getProperty("SN");
		String[] elementIds = mappingString.split("\\,");
		
		for (int i=0;i<elementIds.length;i++){
			System.out.print(elementIds[i] + " ");
		}
	
		Collection<User> users = userService.getUsersByPhoneNumber(sender);
		User user = users.iterator().next();
		System.out.println("user info-" + user.getDisplayName() + "gender" + user.getGender());
		
		OrganisationUnit userOrgUnit = user.getOrganisationUnit();
		
		if (isUpdateCase){
		patient = patientIdentifierService.getPatient(patientIdentifierTypeService.getPatientIdentifierType(ORGUNITAUTOFORMAT), UID);	
		}else {
		 patient = new Patient();
		}

		for (int i=0;i<elementIds.length;i++){
			
			if (elementIds[i].equalsIgnoreCase("NAME")){
			
				// Set FirstName, MiddleName, LastName by FullName
                fullName = data[i].trim();

                int startIndex = fullName.indexOf( ' ' );
                int endIndex = fullName.lastIndexOf( ' ' );

                firstName = fullName.toString();
                middleName = "";
                lastName = "";

                if ( fullName.indexOf( ' ' ) != -1 )
                {
                    firstName = fullName.substring( 0, startIndex );
                    if ( startIndex == endIndex )
                    {
                        middleName = "";
                        lastName = fullName.substring( startIndex + 1, fullName.length() );
                    }
                    else
                    {
                        middleName = fullName.substring( startIndex + 1, endIndex );
                        lastName = fullName.substring( endIndex + 1, fullName.length() );
                    }
                }

                patient.setFirstName( firstName );
                patient.setMiddleName( middleName );
                patient.setLastName( lastName );
                patient.setIsDead( false );
                //patient.setUnderAge( false );
                patient.setOrganisationUnit( userOrgUnit);
              
                patient.setRegistrationDate( registrationDate );
			}
			
			 else if( elementIds[i].equalsIgnoreCase( "GENDER" ) && data[i] != null )
             {
            
                if( gender == null )
                     gender = data[i];
                 patient.setGender( gender );
             }
             else if( elementIds[i].equalsIgnoreCase( "PHONE" ) && data[i] != null )
             {
                 patient.setPhoneNumber( data[i] );
             }
             else if( elementIds[i].equalsIgnoreCase( "DOB" ) && data[i] != null )
             {
            	  
            		  System.out.println(""+data[i]);
            		
            		  patient.setBirthDateFromAge(Integer.parseInt(data[i].trim()), 'Y');
            		 // patient.setBirthDate(birthDate);
				
            	  
             }
             
             else if( elementIds[i].substring( 0, 2 ).equalsIgnoreCase( "PA" ) && data[i] != null )
             {
                 PatientAttributeValue attributeValue = null;
                 PatientAttribute attribute = patientAttributeService.getPatientAttribute( Integer.parseInt( elementIds[i].split( ":" )[1] ) );
                 
                 String value = data[i];
                 //String value = props1.getProperty( elementIds[i]+":"+patientData[i] );
                 
               PatientAttributeValue patientAttributeValue = new PatientAttributeValue();
               patientAttributeValue.setPatient(patient);
               patientAttributeValue.setPatientAttribute(attribute);
               patientAttributeValue.setValue(value);
               
               patientAttributeValues.add(patientAttributeValue);
             }
			
		}
		
			if (!isUpdateCase){
		
			  // Creating new Patient 
            Integer id = patientService.createPatient( patient, null, null, patientAttributeValues );
                   
                // create identifier
       		 PatientIdentifierType patientIdentifierType = patientIdentifierTypeService.getPatientIdentifierType( ORGUNITAUTOFORMAT );
                int threeDigitRunningNo = 1;
                String threeDigitRunningNumber = "001";
              
                PatientAttribute schoolCodePatientAttribute = patientAttributeService.getPatientAttribute(schoolCodePatientAttributeId);
                PatientAttributeValue schoolCodeValue = patientAttributeValueService.getPatientAttributeValue(patient, schoolCodePatientAttribute);
                PatientAttribute fatherNamePatientAttribute = patientAttributeService.getPatientAttribute(fatherNamePatientAttributeId);
                PatientAttributeValue fatherNameAttributeValue = patientAttributeValueService.getPatientAttributeValue(patient, fatherNamePatientAttribute);
                
                String orgUnitAutoIdentifierData = schoolCodeValue.getValue()+fullName.substring(0,3)+patient.getIntegerValueOfAge()+patient.getGender()+fatherNameAttributeValue.getValue().substring(0, 2) + threeDigitRunningNumber;
              
                PatientIdentifier orgUnitAutoIdentifier = patientIdentifierService.get( patientIdentifierType, orgUnitAutoIdentifierData );
                
                while ( orgUnitAutoIdentifier != null )
                {
                    threeDigitRunningNo++;
                    if( threeDigitRunningNo <= 9 )
                    {
                        threeDigitRunningNumber = "00" + threeDigitRunningNo;
                    }
                    else if( threeDigitRunningNo >= 10 && threeDigitRunningNo <= 99 )
                    {
                        threeDigitRunningNumber = "0" + threeDigitRunningNo;
                    }
                   
                    orgUnitAutoIdentifierData = schoolCodeValue.getValue()+fullName.substring(0,3)+patient.getIntegerValueOfAge()+patient.getGender()+fatherNameAttributeValue.getValue().substring(0, 2) + threeDigitRunningNumber;
                       orgUnitAutoIdentifier = patientIdentifierService.get( patientIdentifierType, orgUnitAutoIdentifierData );
                }
                orgUnitAutoIdentifier = new PatientIdentifier();
                orgUnitAutoIdentifier.setIdentifierType( patientIdentifierType );
                orgUnitAutoIdentifier.setIdentifier( orgUnitAutoIdentifierData );
                orgUnitAutoIdentifier.setPatient( patient );

                patient.getIdentifiers().add( orgUnitAutoIdentifier );
               
                         statusMessage = fullName+" IS SUCCESSFULLY REGISTERED,UNIQUE-ID:"+orgUnitAutoIdentifierData;
			}
			else {
				patientService.updatePatient(patient, null, null, new ArrayList<PatientAttributeValue>(), patientAttributeValues, new ArrayList<PatientAttributeValue>());
				
				  statusMessage = fullName+" IS SUCCESSFULLY UPDATED";
			}  
               addReturnMessage(statusMessage, sendTime, sender);      
	}
            
          

	@Override
	@Transactional
	public void registerDataByUID(String unCompressedText, String sender,
			Date sendTime) {
		
		//S2N#UID#0:1:0
		//S2U#UID#1:1:1
		
		String stage = "";
		int stageNo = 0;
		int updateFlag = 0;
		int followUpNo = -1;
		String registrationDate = "";
		String UID="";
		String programName = "Rheumatic Heart Disease";
		String statusMessage=null;
		boolean addCase = false;
		Date dateToday = Calendar.getInstance().getTime();
		Date dueDate;
		
		int[][][] addUpdateErrorCaseMatrix = {
				{
					{ADDCASE,ADDCASE,ERRORCASE,ERRORCASE,ADDCASE},
					{ERRORCASE,ERRORCASE,ADDCASE,ERRORCASE,ADDCASE},
					{ERRORCASE,ERRORCASE,ERRORCASE,ADDCASE,ADDCASE},
					{ERRORCASE,ERRORCASE,ERRORCASE,ERRORCASE,ADDCASE},
									
				},
				{
					{ERRORCASE,ERRORCASE,ERRORCASE,ERRORCASE,ERRORCASE},
					{UPDATECASE,UPDATECASE,ERRORCASE,ERRORCASE,UPDATECASE},
					{UPDATECASE,UPDATECASE,UPDATECASE,ERRORCASE,UPDATECASE},
					{UPDATECASE,UPDATECASE,UPDATECASE,UPDATECASE,UPDATECASE}
				}
			};
		
		String smsData[] = unCompressedText.split("#");
		stage = smsData[0];
		
		if (stage.substring(2, 3).equalsIgnoreCase("U")){
		updateFlag = 1;
			
		}
		
		
		stageNo = Integer.parseInt(stage.substring(1, 2));
		UID = smsData[1];
		String data[] = smsData[2].split(":");
		
		
		if (stageNo == 1){
			programName = "Registration and Symptom detection";
		}else if (stageNo == 5){
			programName = "Rheumatic Heart Disease";
		}else{
			programName = "Rheumatic Fever";
		}
	
		
		System.out.println("StageNo = " + stageNo + "programName = " + programName);
	
		String path = System.getenv( "DHIS2_HOME" ) +  File.separator + "mi" + File.separator + "formIDLayout.csv";
        Properties props = new Properties();
        try {
			props.load( new FileReader( path ) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
   
        String mappingString = props.getProperty("S"+stageNo);
		String[] elementIds = mappingString.split("\\,");
		
		
		PatientIdentifierType patientIdentifierType = patientIdentifierTypeService.getPatientIdentifierType( ORGUNITAUTOFORMAT );
	  	Patient patient = patientIdentifierService.getPatient(patientIdentifierType, UID.trim());
		
		if (stageNo!=1){
			// check if stage 1 has been entered or not
			Program stage1Program = programService.getProgramByName("Registration and Symptom detection");
			if (!patient.getPrograms().contains(stage1Program)){
				 statusMessage = "1Data for invalid stage Sent.UID:"+UID;
				  addReturnMessage(statusMessage, sendTime, sender);
				  return;
			}
	
			Program stage2Program = programService.getProgramByName("Rheumatic Fever");
			
			if (stageNo == 3 || stageNo == 4){
				if (!patient.getPrograms().contains(stage2Program)){
					 statusMessage = "1Data for invalid stage Sent.UID:"+UID;
					  addReturnMessage(statusMessage, sendTime, sender);
					  return;
				}
				
			}
		}
	
		
		Program program = programService.getProgramByName(programName);
		System.out.println("program = "+program);
		ProgramInstance programInstance =null;
		Collection<Program> patientPrograms = patient.getPrograms();
		System.out.println("program = "+program.getDisplayName() + "patient programs size = " + patientPrograms.size());
		
		if (!patientPrograms.contains(program)){
		// add program to patient for initial case
			patient.getPrograms().add(program);
			  programInstance = new ProgramInstance();
              programInstance.setEnrollmentDate(dateToday );
              programInstance.setDateOfIncident( dateToday);
              programInstance.setProgram( program );
              programInstance.setCompleted( false );

              programInstance.setPatient( patient );
            
              patientService.updatePatient( patient );

              programInstanceService.addProgramInstance( programInstance );
              
			
		}
		
		Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances(patient, program);
		programInstance = programInstances.iterator().next();
		Collection<ProgramStageInstance> progStageInstances = programInstance.getProgramStageInstances();
		int noOfStages = progStageInstances.size();
		ProgramStageInstance progStageInstance = null;
		
		System.out.println("no of Stage = " + noOfStages + "updateFlag = "+updateFlag + "stage no="+stageNo);
		System.out.println("------->"+addUpdateErrorCaseMatrix[updateFlag][noOfStages][stageNo-1]);
		// write logic to get whether update case or add case or error
		if (addUpdateErrorCaseMatrix[updateFlag][noOfStages][stageNo-1] == ADDCASE){
			
			
			progStageInstance = new ProgramStageInstance();
			ProgramStage programStage = program.getProgramStages().iterator().next();
		   
	        progStageInstance.setProgramInstance( programInstance );
	        progStageInstance.setProgramStage( programStage );
	        
	       // programStageInstance.setStageInProgram( programStage.getStageInProgram() );
	        progStageInstance.setDueDate( dateToday );
	        progStageInstance.setExecutionDate(dateToday );
	        progStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );

	        int psInstanceId = programStageInstanceService.addProgramStageInstance( progStageInstance );
	        
			
		}else if (addUpdateErrorCaseMatrix[updateFlag][noOfStages][stageNo-1] == UPDATECASE){
			
			int updateIndex;
			if (stageNo == 2 || stageNo == 3 ||stageNo == 4){
				updateIndex = stageNo-2;
			}else {
				updateIndex = noOfStages-1;
			}
			 progStageInstance = (ProgramStageInstance) progStageInstances.toArray()[updateIndex];
			 
		}else if (addUpdateErrorCaseMatrix[updateFlag][noOfStages][stageNo-1] == ERRORCASE){
			  statusMessage = "Data for invalid stage Sent.UID:"+UID;
			 
			  addReturnMessage(statusMessage, sendTime, sender);
			  return;
		}
		
		System.out.println("elementId size= "+elementIds.length + "data.length = " + data.length);
		for (int k = 0; k<elementIds.length;k++){
			System.out.print(elementIds[k] + " ");
		}
		System.out.println();
		for (int k = 0; k<data.length;k++){
			System.out.print(data[k] + " ");
		}
		
		for (int i=0;i<elementIds.length;i++){
			
			 if( elementIds[i].substring( 0, 2 ).equalsIgnoreCase( "DV" ) && data[i] != null ){
                 DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( elementIds[i].split( ":" )[1] ) );
                 PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( progStageInstance, dataElement );
                 
                 
                 String value = data[i];
                 
                 if (value.trim().equalsIgnoreCase(YES)){
            		 value = "true";
            	 }else if (value.trim().equalsIgnoreCase(NO)){
            		 value = "false";
            	 }
                 //String value = props1.getProperty( elementIds[i]+":"+patientData[i] );
               
                 
                 if ( value != null && value.trim().length() == 0 )
                 {
                     value = null;
                 }
                 
                 if ( progStageInstance.getExecutionDate() == null )
                 {
                     progStageInstance.setExecutionDate( new Date() );
                     programStageInstanceService.updateProgramStageInstance( progStageInstance );
                 }
                 
                 if ( patientDataValue == null && value != null )
                 {
                	
                     LOG.debug( "Adding PatientDataValue, value added" );

                     patientDataValue = new PatientDataValue( progStageInstance, dataElement, new Date(), value );
                     patientDataValue.setStoredBy( storedBy );
                     patientDataValue.setProvidedElsewhere( false );

                     patientDataValueService.savePatientDataValue( patientDataValue );
                 }
                 if( patientDataValue != null && value == null )
                 {
                     patientDataValueService.deletePatientDataValue( patientDataValue );
                 }
                 else if( patientDataValue != null && value != null )
                 {
                     LOG.debug( "Updating PatientDataValue, value added/changed" );

                     patientDataValue.setValue( value );
                     patientDataValue.setTimestamp( new Date() );
                     patientDataValue.setProvidedElsewhere( false );
                     patientDataValue.setStoredBy( storedBy );
                     
                     patientDataValueService.updatePatientDataValue( patientDataValue );
                     
                   
                 }
				 
			}
		}
		  statusMessage = "Stage"+stageNo+" added/updated for student with UID:"+UID;
	
		addReturnMessage(statusMessage, sendTime, sender);
		
		return ;
		
	}

	
	
@Transactional
	private void addReturnMessage(String statusMessage, Date sendTime,
			String sender) {
		// TODO Auto-generated method stub
		System.out.println("StatusMsg: "+ statusMessage );
         
      SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
         String timeStamp = dateFormat.format( sendTime );
         String senderInfo = sender + "_" +timeStamp;
         SendSMS sendSMS = sendSMSService.getSendSMS( senderInfo );
             
         if( sendSMS == null )
         {
             sendSMS = new SendSMS( senderInfo, statusMessage );    
             sendSMSService.addSendSMS( sendSMS );
         }
         else
         {
             sendSMS.setSendingMessage( statusMessage );
             sendSMSService.updateSendSMS( sendSMS );
         }
      
        
	}

	@Override
	public void registerProgram1Data(String unCompressedText, String sender,
			Date sendTime) {
		//S2#UID#0:1:0
		//S2U#UID#1:1:1
		
		String stage = "";
		int stageNo;
		int updateFlag = 0;
		int followUpNo = -1;
		String registrationDate = "";
		String UID="";
		String programName = "Registration and Symptom detection";
		String statusMessage=null;
		boolean addCase = false;
		
		int[][][] addUpdateErrorCaseMatrix = {
				{
					{ADDCASE,ADDCASE,ERRORCASE,ERRORCASE,ADDCASE},
					{ERRORCASE,ERRORCASE,ADDCASE,ERRORCASE,ADDCASE},
					{ERRORCASE,ERRORCASE,ERRORCASE,ADDCASE,ADDCASE},
					{ERRORCASE,ERRORCASE,ERRORCASE,ERRORCASE,ADDCASE}
				},
				{
					{ERRORCASE,ERRORCASE,ERRORCASE,ERRORCASE,ERRORCASE},
					{UPDATECASE,UPDATECASE,ERRORCASE,ERRORCASE,UPDATECASE},
					{UPDATECASE,UPDATECASE,UPDATECASE,ERRORCASE,UPDATECASE},
					{UPDATECASE,UPDATECASE,UPDATECASE,UPDATECASE,UPDATECASE}
				}
			};
		
		
		String smsData[] = unCompressedText.split("#");
		stage = smsData[0];
		stageNo = Integer.parseInt(stage.substring(1, 2));
		
		
		
		registrationDate = smsData[1];
		UID = smsData[2];
		String data[] = smsData[3].split(":");
		
		String path = System.getenv( "DHIS2_HOME" ) +  File.separator + "mi" + File.separator + "formIDLayout.csv";
        Properties props = new Properties();
        try {
			props.load( new FileReader( path ) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String mappingString = props.getProperty("S1");
		String[] elementIds = mappingString.split("\\,");
		
		
		PatientIdentifierType patientIdentifierType = patientIdentifierTypeService.getPatientIdentifierType( ORGUNITAUTOFORMAT );
	        
		
		Patient patient = patientIdentifierService.getPatient(patientIdentifierType, UID.trim());
		
		Program program = programService.getProgram(programName);
		ProgramInstance programInstance =null;
		Collection<Program> patientPrograms = patient.getPrograms();
		
		if (!patientPrograms.contains(program)){
		// add program to patient for initial case
			patient.getPrograms().add(program);
			  programInstance = new ProgramInstance();
              programInstance.setEnrollmentDate( patient.getRegistrationDate() );
              programInstance.setDateOfIncident( patient.getRegistrationDate() );
              programInstance.setProgram( program );
              programInstance.setCompleted( false );

              programInstance.setPatient( patient );
            
              patientService.updatePatient( patient );

              programInstanceService.addProgramInstance( programInstance );
              
			
		}
		
		Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances(patient, program);
		programInstance = programInstances.iterator().next();
		Collection<ProgramStageInstance> progStageInstances = programInstance.getProgramStageInstances();
		int noOfStages = progStageInstances.size();
		ProgramStageInstance progStageInstance = null;
		
		// write logic to get whether update case or add case or error
		if (addCase){
			
			
			progStageInstance = new ProgramStageInstance();
			ProgramStage programStage = new ProgramStage();
		   
	        progStageInstance.setProgramInstance( programInstance );
	        progStageInstance.setProgramStage( programStage );
	        
	       // programStageInstance.setStageInProgram( programStage.getStageInProgram() );
	        progStageInstance.setDueDate( patient.getRegistrationDate() );
	        progStageInstance.setExecutionDate(patient.getRegistrationDate() );
	        progStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );

	        int psInstanceId = programStageInstanceService.addProgramStageInstance( progStageInstance );
	        
			
		}else {
			 progStageInstance = (ProgramStageInstance) progStageInstances.toArray()[0];
			 
		}
		
		
		for (int i=0;i<elementIds.length;i++){
			
			 if( elementIds[i].substring( 0, 2 ).equalsIgnoreCase( "DV" ) && data[i] != null ){
                 DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( elementIds[i].split( ":" )[1] ) );
                 PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( progStageInstance, dataElement );
                 
                 
                 String value = data[i];
                 //String value = props1.getProperty( elementIds[i]+":"+patientData[i] );
               
                 
                 if ( value != null && value.trim().length() == 0 )
                 {
                     value = null;
                 }
                 
                 if ( progStageInstance.getExecutionDate() == null )
                 {
                     progStageInstance.setExecutionDate( new Date() );
                     programStageInstanceService.updateProgramStageInstance( progStageInstance );
                 }
                 
                 if ( patientDataValue == null && value != null )
                 {
                     LOG.debug( "Adding PatientDataValue, value added" );

                     patientDataValue = new PatientDataValue( progStageInstance, dataElement, new Date(), value );
                     patientDataValue.setStoredBy( storedBy );
                     patientDataValue.setProvidedElsewhere( false );

                     patientDataValueService.savePatientDataValue( patientDataValue );
                 }
                 if( patientDataValue != null && value == null )
                 {
                     patientDataValueService.deletePatientDataValue( patientDataValue );
                 }
                 else if( patientDataValue != null && value != null )
                 {
                     LOG.debug( "Updating PatientDataValue, value added/changed" );

                     patientDataValue.setValue( value );
                     patientDataValue.setTimestamp( new Date() );
                     patientDataValue.setProvidedElsewhere( false );
                     patientDataValue.setStoredBy( storedBy );
                     
                     patientDataValueService.updatePatientDataValue( patientDataValue );
                 }
				 
			}
		}
		
	
		addReturnMessage(statusMessage, sendTime, sender);
		
		return ;
	
	}
		
	}


