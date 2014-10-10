package org.hisp.dhis.patient.startup;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.hisp.dhis.system.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version TableAlteror.java Sep 9, 2010 10:22:29 PM
 */
public class TableAlteror
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( TableAlteror.class );

    final Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );

    final Pattern IDENTIFIER_PATTERN_FIELD = Pattern.compile( "id=\"(\\d+)-(\\d+)-val\"" );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StatementBuilder statementBuilder;

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void execute()
        throws Exception
    {
        executeSql( "ALTER TABLE relationshiptype RENAME description TO name" );

        executeSql( "ALTER TABLE programstage_dataelements DROP COLUMN showOnReport" );

        executeSql( "ALTER TABLE patientdatavalue DROP COLUMN categoryoptioncomboid" );
        executeSql( "DROP TABLE patientchart" );

        executeSql( "ALTER TABLE program DROP COLUMN hidedateofincident" );

        executeSql( "UPDATE program SET type=2 where singleevent=true" );
        executeSql( "UPDATE program SET type=3 where anonymous=true" );
        executeSql( "ALTER TABLE program DROP COLUMN singleevent" );
        executeSql( "ALTER TABLE program DROP COLUMN anonymous" );
        executeSql( "UPDATE program SET type=1 where type is null" );

        executeSql( "UPDATE programstage SET irregular=false WHERE irregular is null" );

        executeSql( "DROP TABLE programattributevalue" );
        executeSql( "DROP TABLE programinstance_attributes" );
        executeSql( "DROP TABLE programattributeoption" );
        executeSql( "DROP TABLE programattribute" );
        executeSql( "DROP TABLE patientdatavaluearchive" );

        executeSql( "ALTER TABLE patientattribute DROP COLUMN noChars" );
        executeSql( "ALTER TABLE programstageinstance ALTER executiondate TYPE date" );

        executeSql( "ALTER TABLE patientidentifier ALTER COLUMN patientid DROP NOT NULL" );
        executeSql( "ALTER TABLE patient DROP COLUMN bloodgroup" );
        executeSql( "ALTER TABLE patientmobilesetting DROP COLUMN bloodGroup" );

        executeSql( "ALTER TABLE caseaggregationcondition RENAME description TO name" );

        executeSql( "UPDATE programstage_dataelements SET allowProvidedElsewhere=false WHERE allowProvidedElsewhere is null" );
        executeSql( "UPDATE patientdatavalue SET providedElsewhere=false WHERE providedElsewhere is null" );
        executeSql( "ALTER TABLE programstageinstance DROP COLUMN providedbyanotherfacility" );

        executeSql( "ALTER TABLE patientattribute DROP COLUMN inheritable" );
        executeSql( "ALTER TABLE programstageinstance DROP COLUMN stageInProgram" );

        executeSql( "UPDATE programstage SET reportDateDescription='Report date' WHERE reportDateDescription is null" );

        executeSql( "CREATE INDEX programstageinstance_executiondate ON programstageinstance (executiondate)" );

        executeSql( "UPDATE programstage SET autoGenerateEvent=true WHERE autoGenerateEvent is null" );

        executeSql( "UPDATE program SET generatedByEnrollmentDate=false WHERE generatedByEnrollmentDate is null" );

        executeSql( "ALTER TABLE programstage DROP COLUMN stageinprogram" );

        executeSql( "CREATE INDEX index_patientdatavalue ON patientdatavalue( programstageinstanceid, dataelementid, value, timestamp )" );

        executeSql( "CREATE INDEX index_programinstance ON programinstance( programinstanceid )" );

        executeSql( "ALTER TABLE program DROP COLUMN maxDaysAllowedInputData" );

        executeSql( "ALTER TABLE period modify periodid int AUTO_INCREMENT" );
        executeSql( "CREATE SEQUENCE period_periodid_seq" );
        executeSql( "ALTER TABLE period ALTER COLUMN periodid SET DEFAULT NEXTVAL('period_periodid_seq')" );

        executeSql( "UPDATE program SET programstage_dataelements=false WHERE displayInReports is null" );

        executeSql( "ALTER TABLE programvalidation DROP COLUMN leftside" );
        executeSql( "ALTER TABLE programvalidation DROP COLUMN rightside" );
        executeSql( "ALTER TABLE programvalidation DROP COLUMN dateType" );

        executeSql( "UPDATE programstage SET validCompleteOnly=false WHERE validCompleteOnly is null" );
        executeSql( "UPDATE program SET ignoreOverdueEvents=false WHERE ignoreOverdueEvents is null" );

        executeSql( "UPDATE programstage SET displayGenerateEventBox=true WHERE displayGenerateEventBox is null" );
        executeSql( "ALTER TABLE patientidentifier DROP COLUMN preferred" );

        executeSql( "UPDATE patientidentifiertype SET personDisplayName=false WHERE personDisplayName is null" );

        executeSql( "ALTER TABLE programvalidation RENAME description TO name" );

        executeSql( "UPDATE program SET blockEntryForm=false WHERE blockEntryForm is null" );
        executeSql( "ALTER TABLE dataset DROP CONSTRAINT program_name_key" );
        executeSql( "UPDATE userroleauthorities SET authority='F_PROGRAM_PUBLIC_ADD' WHERE authority='F_PROGRAM_ADD'" );

        executeSql( "UPDATE patientaudit SET accessedModule='patient_dashboard' WHERE accessedModule is null" );
        executeSql( "UPDATE patienttabularreport SET userOrganisationUnit=false WHERE userOrganisationUnit is null" );
        executeSql( "UPDATE patienttabularreport SET userOrganisationUnitChildren=false WHERE userOrganisationUnitChildren is null" );
        executeSql( "UPDATE patientattribute SET valueType='date' WHERE valueType='DATE'" );
        executeSql( "UPDATE patientattribute SET valueType='string' WHERE valueType='TEXT'" );
        executeSql( "UPDATE patientattribute SET valueType='number' WHERE valueType='NUMBER'" );
        executeSql( "UPDATE patientattribute SET valueType='bool' WHERE valueType='YES/NO'" );
        executeSql( "UPDATE patientattribute SET valueType='combo' WHERE valueType='COMBO'" );
        executeSql( "UPDATE patientidentifiertype SET type='string' WHERE type='text'" );

        executeSql( "UPDATE program SET onlyEnrollOnce='false' WHERE onlyEnrollOnce is null" );
        executeSql( "UPDATE programStage SET captureCoordinates=false WHERE captureCoordinates is null" );

        executeSql( "update caseaggregationcondition set \"operator\"='times' where \"operator\"='SUM'" );

        executeSql( "update prorgam set \"operator\"='times' where \"operator\"='SUM'" );

        executeSql( "update program set remindCompleted=false where remindCompleted is null" );
        executeSql( "update patientreminder set dateToCompare='duedate' where programstageid is not null" );
        executeSql( "UPDATE programinstance SET followup=false where followup is null" );
        executeSql( "UPDATE patientreminder SET sendTo=1 where sendTo is null" );

        updateUid();

        updateUidInDataEntryFrom();

        updateProgramInstanceStatus();

        executeSql( "ALTER TABLE program DROP COLUMN disableRegistrationFields" );
        executeSql( "ALTER TABLE program ALTER COLUMN dateofincidentdescription DROP NOT NULL" );
        executeSql( "ALTER TABLE patient ALTER COLUMN birthdate DROP NOT NULL" );
        executeSql( "ALTER TABLE patient ALTER COLUMN gender DROP NOT NULL" );
        executeSql( "ALTER TABLE patient ALTER COLUMN underage DROP NOT NULL" );
        executeSql( "ALTER TABLE program ALTER COLUMN dateofenrollmentdescription DROP NOT NULL" );
        executeSql( "UPDATE program SET displayOnAllOrgunit=true where displayOnAllOrgunit is null" );
        executeSql( "UPDATE program SET useFormNameDataElement=true where useFormNameDataElement is null" );
        executeSql( "ALTER TABLE caseaggregationcondition ALTER COLUMN aggregationexpression TYPE varchar(1000)" );
        executeSql( "update patientattribute set displayonvisitschedule = false where displayonvisitschedule is null" );
        executeSql( "update program set useBirthDateAsIncidentDate = false where useBirthDateAsIncidentDate is null" );
        executeSql( "update program set useBirthDateAsEnrollmentDate = false where useBirthDateAsEnrollmentDate is null" );
        executeSql( "update program set selectEnrollmentDatesInFuture = false where selectEnrollmentDatesInFuture is null" );
        executeSql( "update program set selectIncidentDatesInFuture = false where selectIncidentDatesInFuture is null" );
        executeSql( "update validationcriteria set description = name where description is null or description='' " );
        executeSql( "update programstage set generatedByEnrollmentDate = false where generatedByEnrollmentDate is null " );
        executeSql( "update programstage set blockEntryForm = false where blockEntryForm is null " );
        executeSql( "update programstage set remindCompleted = false where remindCompleted is null " );
        executeSql( "ALTER TABLE program DROP COLUMN generatedByEnrollmentDate" );
        executeSql( "ALTER TABLE program DROP COLUMN blockEntryForm" );
        executeSql( "ALTER TABLE program DROP COLUMN remindCompleted" );
        executeSql( "ALTER TABLE program DROP COLUMN displayProvidedOtherFacility" );
        executeSql( "UPDATE program SET dataEntryMethod=false WHERE dataEntryMethod is null" );
        executeSql( "UPDATE patientreminder SET messageType=1 WHERE messageType is null" );
        executeSql( "UPDATE programstage SET allowGenerateNextVisit=false WHERE allowGenerateNextVisit is null" );

        executeSql( "update patient set name=concat_ws(' ', trim(firstname), trim(middlename), trim(lastname))" );
        executeSql( "alter table patient drop column firstname" );
        executeSql( "alter table patient drop column middlename" );
        executeSql( "alter table patient drop column lastname" );

        executeSql( "DROP TABLE patient_programs" );
        executeSql( "DROP TABLE patient_attributes" );

        executeSql( "update programstage set openAfterEnrollment=false where openAfterEnrollment is null" );
        executeSql( "update programstage set reportDateToUse=false where reportDateToUse is null" );

        executeSql( "update patientidentifiertype set orgunitScope=false where orgunitScope is null" );
        executeSql( "update patientidentifiertype set programScope=false where programScope is null" );

        executeSql( "update programstageinstance set status=0 where status is null" );
        executeSql( "ALTER TABLE patienttabularreport RENAME level TO ouMode" );
        executeSql( "ALTER TABLE program DROP COLUMN facilityLB" );
        executeSql( "DROP TABLE patienttabularreport_attributes" );
        executeSql( "DROP TABLE patienttabularreport_filtervalues" );
        executeSql( "DROP TABLE patienttabularreport_fixedattribute" );
        executeSql( "DROP TABLE patienttabularreport_programstagedataelements" );
        executeSql( "DROP TABLE patienttabularreport_organisationunits" );
        executeSql( "DROP TABLE patienttabularreport_identifiers" );
        executeSql( "DROP TABLE patienttabularreport_organisationUnits" );
        executeSql( "DROP TABLE patientaggregatereport_enddates" );
        executeSql( "DROP TABLE patientaggregatereport_filtervalues" );
        executeSql( "DROP TABLE patientaggregatereport_fixedperiods" );
        executeSql( "DROP TABLE patientaggregatereport_organisationunits" );
        executeSql( "DROP TABLE patientaggregatereport_relativeperiods" );
        executeSql( "DROP TABLE patientaggregatereport_startdates" );
        executeSql( "ALTER TABLE patientaggregatereport RENAME level TO ouMode" );
        executeSql( "ALTER TABLE patientaggregatereport DROP COLUMN facilityLB" );
        executeSql( "update programstage_dataelements set allowDateInFuture=false where allowDateInFuture is null" );
        executeSql( "update programstage set autoGenerateEvent=true where programid in ( select programid from program where type=2 )" );
        executeSql( "alter table patient alter column organisationunitid set not null" );

        executeSql( "ALTER TABLE patientdatavalue ALTER COLUMN timestamp TYPE timestamp" );
        executeSql( "ALTER TABLE programstageinstance ALTER COLUMN executiondate TYPE timestamp" );

        updateCoordinatesProgramStageInstance();

        addPatientAttributes();

        executeSql( "ALTER TABLE program DROP COLUMN useBirthDateAsIncidentDate" );
        executeSql( "ALTER TABLE program DROP COLUMN useBirthDateAsEnrollmentDate" );

        executeSql( "UPDATE patientattribute SET displayinlistnoprogram=false WHERE displayinlistnoprogram is null" );
        executeSql( "UPDATE patientidentifiertype SET displayinlistnoprogram=false WHERE displayinlistnoprogram is null" );

        executeSql( "ALTER TABLE patientidentifiertype DROP COLUMN persondisplayname" );

        updateProgramAttributes();
        executeSql( "update datavalue set storedby='aggregated_from_tracker' where storedby='DHIS-System'");
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private void updateProgramAttributes()
    {
        String autoIncrVal = statementBuilder.getAutoIncrementValue();

        String attributeSql = "INSERT INTO program_attributes (programattributeid, attributeid, sort_order, displayinlist, programid) "
            + "SELECT "
            + autoIncrVal
            + ", pp.patientattributeid, pp.sort_order, false, pp.programid "
            + "FROM program_patientattributes pp";
        executeSql( attributeSql );

        log.info( "Inserted data into program_attributes table." );

        String identifierSql = "INSERT INTO program_identifiertypes (programidentifiertypeid, identifiertypeid, sort_order, displayinlist, programid) "
            + "SELECT "
            + autoIncrVal
            + ", pp.patientidentifiertypeid, pp.sort_order, false, pp.programid "
            + "FROM program_patientidentifiertypes pp";
        executeSql( identifierSql );

        log.info( "Inserted data into program_identifiertypes table." );

        executeSql( "DROP TABLE program_patientattributes." );
        log.info( "Dropped program_patientattributes table." );

        executeSql( "DROP TABLE program_patientidentifiertypes." );
        log.info( "Dropped program_patientidentifiertypes table." );

    }

    private void updateUid()
    {
        updateUidColumn( "patientattribute" );
        updateUidColumn( "patientattributegroup" );
        updateUidColumn( "patientidentifiertype" );
        updateUidColumn( "program" );
        updateUidColumn( "patientattribute" );
        updateUidColumn( "programstage" );
        updateUidColumn( "programstagesection" );
        updateUidColumn( "programvalidation" );
        updateUidColumn( "caseaggregatecondition" );
    }

    private void updateUidColumn( String tableName )
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement.executeQuery( "SELECT " + tableName + "id FROM " + tableName
                + " where uid is null" );

            while ( resultSet.next() )
            {
                String uid = CodeGenerator.generateCode();

                executeSql( "UPDATE " + tableName + " SET uid='" + uid + "'  WHERE " + tableName + "id="
                    + resultSet.getInt( 1 ) );
            }
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private void updateUidInDataEntryFrom()
    {
        Collection<ProgramStage> programStages = programStageService.getAllProgramStages();

        for ( ProgramStage programStage : programStages )
        {
            DataEntryForm dataEntryForm = programStage.getDataEntryForm();
            if ( dataEntryForm != null && dataEntryForm.getFormat() != DataEntryForm.CURRENT_FORMAT )
            {
                String programStageUid = programStage.getUid();
                String htmlCode = programStage.getDataEntryForm().getHtmlCode();

                // ---------------------------------------------------------------------
                // Metadata code to add to HTML before outputting
                // ---------------------------------------------------------------------

                StringBuffer sb = new StringBuffer();

                // ---------------------------------------------------------------------
                // Pattern to match data elements in the HTML code
                // ---------------------------------------------------------------------

                Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

                // ---------------------------------------------------------------------
                // Iterate through all matching data element fields
                // ---------------------------------------------------------------------

                while ( inputMatcher.find() )
                {
                    String inputHTML = inputMatcher.group();

                    // -----------------------------------------------------------------
                    // Get HTML input field code
                    // -----------------------------------------------------------------

                    String dataElementCode = inputMatcher.group( 1 );

                    Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( dataElementCode );

                    if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
                    {
                        // -------------------------------------------------------------
                        // Get data element ID of data element
                        // -------------------------------------------------------------

                        int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );
                        DataElement dataElement = dataElementService.getDataElement( dataElementId );

                        if ( dataElement != null )
                        {
                            inputHTML = inputHTML.replaceFirst( identifierMatcher.group( 1 ), programStageUid );
                            inputHTML = inputHTML.replaceFirst( identifierMatcher.group( 2 ), dataElement.getUid() );
                            inputMatcher.appendReplacement( sb, inputHTML );
                        }

                    }
                }

                inputMatcher.appendTail( sb );

                htmlCode = (sb.toString().isEmpty()) ? htmlCode : sb.toString();
                dataEntryForm.setHtmlCode( htmlCode );
                dataEntryForm.setFormat( DataEntryForm.CURRENT_FORMAT );
                dataEntryFormService.updateDataEntryForm( dataEntryForm );
            }
        }
    }

    private void updateProgramInstanceStatus()
    {
        // Set active status for events
        executeSql( "UPDATE programinstance SET status=0 WHERE completed=false" );

        // Set un-completed status for events
        executeSql( "UPDATE programinstance SET status=2 WHERE programinstanceid in "
            + "( select psi.programinstanceid from programinstance pi join programstageinstance psi "
            + "on psi.programinstanceid = psi.programstageinstanceid "
            + "where pi.completed=true and psi.completed = false )" );

        // Set completed status for events
        executeSql( "UPDATE programinstance SET status=1 WHERE status is null" );

        // Drop the column with name as completed
        executeSql( "ALTER TABLE programinstance DROP COLUMN completed" );
    }

    private void updateCoordinatesProgramStageInstance()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement
                .executeQuery( "SELECT programstageinstanceid, coordinates FROM programstageinstance where coordinates is not null" );

            while ( resultSet.next() )
            {
                String coordinates = resultSet.getString( "coordinates" );
                String longitude = ValidationUtils.getLongitude( coordinates );
                String latitude = ValidationUtils.getLatitude( coordinates );
                executeSql( "UPDATE programstageinstance SET longitude='" + longitude + "', latitude='" + latitude
                    + "'  WHERE programstageinstanceid=" + resultSet.getInt( "programstageinstanceid" ) );
            }

            executeSql( "ALTER TABLE programstageinstance DROP COLUMN coordinates" );
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private void addPatientAttributes()
    {
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            Statement statement = holder.getStatement();
            ResultSet resultSet = statement
                .executeQuery( "select patientattributeid, uid from patientattribute where name='Gender'" );
            if ( resultSet.next() )
            {
                int id = resultSet.getInt( "patientattributeid" );
                String uid = resultSet.getString( "uid" );

                // Update Case Aggregate Query Builder
                String source = "[CP" + CaseAggregationCondition.SEPARATOR_OBJECT + "gender]";
                String target = "[" + CaseAggregationCondition.OBJECT_PATIENT_ATTRIBUTE
                    + CaseAggregationCondition.SEPARATOR_OBJECT + id + "]";
                updateFixedAttributeInCaseAggregate( source, target );

                log.info( "Updated case aggregate query build formulas which have gender criteria" );

                // Update custom entry form && validation criteria
                removeFixedAttributeInCustomRegistrationForm( "gender", uid );
                executeSql( "UPDATE validationcriteria SET property='" + uid + "' WHERE property='gender' " );
            }

            // ---------------------------------------------------------------------
            // Death date
            // ---------------------------------------------------------------------

            resultSet = statement.executeQuery( "select uid from patientattribute where name='Death date'" );
            if ( resultSet.next() )
            {
                String uid = resultSet.getString( "uid" );

                // Update custom entry form && Validation criteria
                removeFixedAttributeInCustomRegistrationForm( "deathDate", uid );
                executeSql( "UPDATE validationcriteria SET property='" + uid + "' WHERE property='deathDate' " );
            }
            // ---------------------------------------------------------------------
            // registrationDate
            // ---------------------------------------------------------------------

            resultSet = statement.executeQuery( "select uid from patientattribute where name='Registration date'" );
            if ( resultSet.next() )
            {
                String uid = resultSet.getString( "uid" );

                // Update custom entry form && validation criteria
                removeFixedAttributeInCustomRegistrationForm( "registrationDate", uid );
                executeSql( "UPDATE validationcriteria SET property='" + uid + "' WHERE property='registrationDate' " );
            }

            // ---------------------------------------------------------------------
            // isDead
            // ---------------------------------------------------------------------

            resultSet = statement.executeQuery( "select uid from patientattribute where name='Is Dead'" );
            if ( resultSet.next() )
            {
                String uid = resultSet.getString( "uid" );

                // Update custom entry form && validation criteria
                removeFixedAttributeInCustomRegistrationForm( "isDead", uid );
                executeSql( "UPDATE validationcriteria SET property='" + uid + "' WHERE property='isDead' " );
            }

            // ---------------------------------------------------------------------
            // underAge
            // ---------------------------------------------------------------------

            resultSet = statement.executeQuery( "select uid from patientattribute where name='Is under age'" );
            if ( resultSet.next() )
            {
                String uid = resultSet.getString( "uid" );

                // Update custom entry form && validation criteria
                removeFixedAttributeInCustomRegistrationForm( "underAge", uid );
                executeSql( "UPDATE validationcriteria SET property='" + uid + "' WHERE property='underAge' " );
            }

            // ---------------------------------------------------------------------
            // DobType
            // ---------------------------------------------------------------------

            resultSet = statement
                .executeQuery( "select patientattributeid,uid from patientattribute where name='DOB type'" );
            if ( resultSet.next() )
            {
                int id = resultSet.getInt( "patientattributeid" );
                String uid = resultSet.getString( "uid" );

                // Update Case Aggregate Query Builder
                String source = "[CP" + CaseAggregationCondition.SEPARATOR_OBJECT + "dobType]";
                String target = "[" + CaseAggregationCondition.OBJECT_PATIENT_ATTRIBUTE
                    + CaseAggregationCondition.SEPARATOR_OBJECT + id + "]";
                updateFixedAttributeInCaseAggregate( source, target );

                // Update custom entry form && validation criteria
                removeFixedAttributeInCustomRegistrationForm( "dobType", uid );
                executeSql( "UPDATE validationcriteria SET property='" + uid + "' WHERE property='dobType' " );
            }

            // -------------------------------------------------------------
            // Birthdate
            // -------------------------------------------------------------

            resultSet = statement.executeQuery( "select uid from patientattribute where name='Birth date'" );
            if ( resultSet.next() )
            {
                String uid = resultSet.getString( "uid" );

                // Update custom entry form && validation criteria
                removeFixedAttributeInCustomRegistrationForm( "birthDate", uid );
                executeSql( "UPDATE validationcriteria SET property='" + uid + "' WHERE property='birthDate' " );
            }

            // -------------------------------------------------------------
            // Age
            // -------------------------------------------------------------

            resultSet = statement
                .executeQuery( "select patientattributeid, uid from patientattribute where name='Age'" );
            if ( resultSet.next() )
            {
                int id = resultSet.getInt( "patientattributeid" );
                String uid = resultSet.getString( "uid" );

                // Update custom entry form && validation criteria
                removeFixedAttributeInCustomRegistrationForm( "age", uid );
                executeSql( "UPDATE validationcriteria SET property='" + uid + "' WHERE property='age' " );

                String source = "[CP" + CaseAggregationCondition.SEPARATOR_OBJECT + "age]";
                String target = "[" + CaseAggregationCondition.OBJECT_PATIENT_ATTRIBUTE
                    + CaseAggregationCondition.SEPARATOR_OBJECT + id + ".age]";
                updateFixedAttributeInCaseAggregate( source, target );
            }

            // -------------------------------------------------------------
            // Phone number
            // -------------------------------------------------------------

            resultSet = statement.executeQuery( "select uid from patientattribute where name='Phone number'" );
            if ( resultSet.next() )
            {
                String uid = resultSet.getString( "uid" );

                // Update custom entry form
                removeFixedAttributeInCustomRegistrationForm( "phoneNumber", uid );
            }

            // -------------------------------------------------------------
            // Patient full name
            // -------------------------------------------------------------

            resultSet = statement.executeQuery( "select uid from patientattribute where name='Full name'" );
            if ( resultSet.next() )
            {
                String uid = resultSet.getString( "uid" );

                // Update custom entry form
                removeFixedAttributeInCustomRegistrationForm( "fullName", uid );

                // update template messsages
                updateFixedAttributeInTemplateMessage( uid );
            }

            // -------------------------------------------------------------
            // User Associate
            // -------------------------------------------------------------

            resultSet = statement.executeQuery( "select uid from patientattribute where name='Staff'" );
            if ( resultSet.next() )
            {
                String uid = resultSet.getString( "uid" );

                // Update custom entry form
                removeFixedAttributeInCustomRegistrationForm( "associate", uid );
            }

        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }

    private void updateFixedAttributeInCaseAggregate( String source, String target )
    {
        StatementHolder holder = statementManager.getHolder();
        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement
                .executeQuery( "SELECT caseaggregationconditionid, aggregationExpression FROM caseaggregationcondition where aggregationExpression like '%"
                    + source + "%'" );

            while ( resultSet.next() )
            {
                String id = resultSet.getString( "caseaggregationconditionid" );
                String expression = resultSet.getString( "aggregationExpression" );

                expression = expression.replaceAll( source, target );
                executeSql( "UPDATE caseaggregationcondition SET aggregationExpression='" + expression
                    + "'  WHERE caseaggregationconditionid=" + id );
            }
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private void updateFixedAttributeInTemplateMessage( String uid )
    {
        StatementHolder holder = statementManager.getHolder();
        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement
                .executeQuery( "SELECT patientreminderid, templatemessage FROM patientreminder where templatemessage like '%{patient-name}%'" );

            while ( resultSet.next() )
            {
                String id = resultSet.getString( "patientreminderid" );
                String expression = resultSet.getString( "templatemessage" );

                expression = expression.replaceAll( "{patient-name}", "{attributeid=" + uid + "}" );
                executeSql( "UPDATE patientreminder SET templatemessage='" + expression + "'  WHERE patientreminderid="
                    + id );
            }
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            holder.close();
        }
    }

    public void removeFixedAttributeInCustomRegistrationForm( String property, String uid )
    {
        StatementHolder holder = statementManager.getHolder();
        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement
                .executeQuery( "SELECT df.dataentryformid, df.htmlcode FROM dataentryform df INNER JOIN patientregistrationform pf on df.dataentryformid=pf.dataentryform" );

            while ( resultSet.next() )
            {
                String htmlCode = resultSet.getString( "htmlcode" );

                htmlCode = htmlCode.replace( "fixedattributeid=\"" + property + "\"", "attributeid=\"" + uid + "\"" );

                executeSql( "UPDATE dataentryform SET htmlcode='" + htmlCode + "' WHERE dataentryformid="
                    + resultSet.getInt( "dataentryformid" ) );
            }
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private int executeSql( String sql )
    {
        try
        {
            return statementManager.getHolder().executeUpdate( sql );
        }
        catch ( Exception ex )
        {
            log.debug( ex );
            return -1;
        }
    }
}
