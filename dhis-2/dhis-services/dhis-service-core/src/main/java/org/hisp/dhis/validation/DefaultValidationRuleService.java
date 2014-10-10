package org.hisp.dhis.validation;

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

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsByName;
import static org.hisp.dhis.i18n.I18nUtils.i18n;
import static org.hisp.dhis.system.util.TextUtils.LN;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Margrethe Store
 * @author Lars Helge Overland
 * @author Jim Grace
 */
@Transactional
public class DefaultValidationRuleService
    implements ValidationRuleService
{
    private static final Log log = LogFactory.getLog( DefaultValidationRuleService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ValidationRuleStore validationRuleStore;

    public void setValidationRuleStore( ValidationRuleStore validationRuleStore )
    {
        this.validationRuleStore = validationRuleStore;
    }

    private GenericIdentifiableObjectStore<ValidationRuleGroup> validationRuleGroupStore;

    public void setValidationRuleGroupStore( GenericIdentifiableObjectStore<ValidationRuleGroup> validationRuleGroupStore )
    {
        this.validationRuleGroupStore = validationRuleGroupStore;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
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

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    
    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // ValidationRule business logic
    // -------------------------------------------------------------------------

    @Override
    public Collection<ValidationResult> validate( Date startDate, Date endDate, Collection<OrganisationUnit> sources, boolean sendAlerts, I18nFormat format )
    {
        return validate( startDate, endDate, sources, null, sendAlerts, format );
    }

    @Override
    public Collection<ValidationResult> validate( Date startDate, Date endDate, Collection<OrganisationUnit> sources,
        ValidationRuleGroup group, boolean sendAlerts, I18nFormat format )
    {
    	log.info( "Validate start:" + startDate + " end: " + endDate + " sources: " + sources.size() + " group: " + group );
    	
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = group != null ? group.getMembers() : getAllValidationRules();
        
        Collection<ValidationResult> results = Validator.validate( sources, periods, rules, null,
            constantService, expressionService, periodService, dataValueService );
        
        formatPeriods( results, format );
        
        if ( sendAlerts )
        {
            Set<ValidationResult> resultsToAlert = new HashSet<ValidationResult>( results );
            FilterUtils.filter( resultsToAlert, new ValidationResultToAlertFilter() );
            postAlerts( resultsToAlert, new Date() );
        }
        
        return results;
    }

    @Override
    public Collection<ValidationResult> validate( Date startDate, Date endDate, OrganisationUnit source )
    {
    	log.info( "Validate start: " + startDate + " end: " + endDate + " source: " + source.getName() );
    	
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = getAllValidationRules();
        Collection<OrganisationUnit> sources = new HashSet<OrganisationUnit>();
        sources.add( source );
        
        return Validator.validate( sources, periods, rules, null,
            constantService, expressionService, periodService, dataValueService );
    }

    @Override
    public Collection<ValidationResult> validate( DataSet dataSet, Period period, OrganisationUnit source )
    {
    	log.info( "Validate data set: " + dataSet.getName() + " period: " + period.getPeriodType().getName() + " "
            + period.getStartDate() + " " + period.getEndDate() + " source: " + source.getName() );
    	
        Collection<Period> periods = new ArrayList<Period>();
        periods.add( period );

        Collection<ValidationRule> rules = null;
        
        if ( DataSet.TYPE_CUSTOM.equals( dataSet.getDataSetType() ) )
        {
            rules = getRulesForDataSet( dataSet );
        }
        else
        {
            rules = getValidationTypeRulesForDataElements( dataSet.getDataElements() );
        }
        
        log.info( "Using validation rules: " + rules.size() );

        Collection<OrganisationUnit> sources = new HashSet<OrganisationUnit>();
        sources.add( source );
        
        return Validator.validate( sources, periods, rules, null,
            constantService, expressionService, periodService, dataValueService );
    }

    @Override
    public void scheduledRun()
    {
        log.info( "Starting scheduled monitoring task" );
        
        // Find all the rules belonging to groups that will send alerts to user roles.

        Set<ValidationRule> rules = getAlertRules();

        Collection<OrganisationUnit> sources = organisationUnitService.getAllOrganisationUnits();
        
        Set<Period> periods = getAlertPeriodsFromRules( rules );
        
        Date lastScheduledRun = (Date) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_LAST_MONITORING_RUN );
        
        // Any database changes after this moment will contribute to the next run.
        
        Date thisRun = new Date();
        
        log.info( "Scheduled monitoring run sources: " + sources.size() + ", periods: " + periods.size() + ", rules:" + rules.size()
            + ", last run: " + ( lastScheduledRun == null ? "[none]" : lastScheduledRun ) );
        
        Collection<ValidationResult> results = Validator.validate( sources, periods, rules,
            lastScheduledRun, constantService, expressionService, periodService, dataValueService );
        
        log.info( "Run results: " + results.size() );
        
        if ( !results.isEmpty() )
        {
            postAlerts( results, thisRun );
        }
        
        log.info( "Posted alerts, monitoring task done" );
        
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_LAST_MONITORING_RUN, thisRun );
    }

    // -------------------------------------------------------------------------
    // Supportive methods - scheduled run
    // -------------------------------------------------------------------------

    /**
     * Gets all the validation rules that could generate alerts.
     * 
     * @return rules that will generate alerts
     */
    private Set<ValidationRule> getAlertRules()
    {
        Set<ValidationRule> rules = new HashSet<ValidationRule>();
        
        for ( ValidationRuleGroup validationRuleGroup : getAllValidationRuleGroups() )
        {
            if ( validationRuleGroup.hasUserRolesToAlert() )
            {
                rules.addAll( validationRuleGroup.getMembers() );
            }
        }
        
        return rules;
    }

    /**
     * Gets the current and most recent periods to search, based on
     * the period types from the rules to run.
     * 
     * For each period type, return the period containing the current date
     * (if any), and the most recent previous period. Add whichever of
     * these periods actually exist in the database.
     * 
     * TODO If the last successful daily run was more than one day ago, we might 
     * add some additional periods of type DailyPeriodType not to miss any
     * alerts.
     *
     * @param rules the ValidationRules to be evaluated on this run
     * @return periods to search for new alerts
     */
    private Set<Period> getAlertPeriodsFromRules( Set<ValidationRule> rules )
    {
        Set<Period> periods = new HashSet<Period>();

        Set<PeriodType> rulePeriodTypes = getPeriodTypesFromRules( rules );

        for ( PeriodType periodType : rulePeriodTypes )
        {
            CalendarPeriodType calendarPeriodType = ( CalendarPeriodType ) periodType;
            Period currentPeriod = calendarPeriodType.createPeriod();
            Period previousPeriod = calendarPeriodType.getPreviousPeriod( currentPeriod );
            periods.addAll( periodService.getIntersectingPeriodsByPeriodType( periodType,
                previousPeriod.getStartDate(), currentPeriod.getEndDate() ) );            
        }

        return periods;
    }

    /**
     * At the end of a scheduled monitoring run, post messages to the users who
     * want to see the results.
     * 
     * Create one message for each set of users who receive the same
     * subset of results. (Not necessarily the same as the set of users who
     * receive alerts from the same subset of validation rules -- because
     * some of these rules may return no results.) This saves on message
     * storage space.
     * 
     * The message results are sorted into their natural order.
     * 
     * TODO: Internationalize the messages according to the user's
     * preferred language, and generate a message for each combination of
     * ( target language, set of results ).
     * 
     * @param validationResults the set of validation error results
     * @param scheduledRunStart the date/time when this scheduled run started
     */
    private void postAlerts( Collection<ValidationResult> validationResults, Date scheduledRunStart )
    {
        SortedSet<ValidationResult> results = new TreeSet<ValidationResult>( validationResults );

        Map<List<ValidationResult>, Set<User>> messageMap = getMessageMap( results );
        
        for ( Map.Entry<List<ValidationResult>, Set<User>> entry : messageMap.entrySet() )
        {
            Collections.sort( entry.getKey() );
            sendAlertmessage( entry.getKey(), entry.getValue(), scheduledRunStart );
        }
    }

    /**
     * Gets the Set of period types found in a set of rules.
     * 
     * Note that that we have to get periodType from periodService,
     * otherwise the ID will not be present.)
     * 
     * @param rules validation rules of interest
     * @return period types contained in those rules
     */
    private Set<PeriodType> getPeriodTypesFromRules ( Collection<ValidationRule> rules )
    {
        Set<PeriodType> rulePeriodTypes = new HashSet<PeriodType>();
        
        for ( ValidationRule rule : rules )
        {
            rulePeriodTypes.add( periodService.getPeriodTypeByName( rule.getPeriodType().getName() ) );
        }
        
        return rulePeriodTypes;
    }

    /**
     * Returns a map where the key is a sorted list of validation results
     * to assemble into a message, and the value is the set of users who
     * should receive this message.
     * 
     * @param results all the validation run results
     * @return map of result sets to users
     */
    private Map<List<ValidationResult>, Set<User>> getMessageMap( Set<ValidationResult> results )
    {
        Map<User, Set<ValidationRule>> userRulesMap = getUserRulesMap();

        Map<List<ValidationResult>, Set<User>> messageMap = new HashMap<List<ValidationResult>, Set<User>>();

        for ( User user : userRulesMap.keySet() )
        {
            // For users receiving alerts, find the subset of results from run.

            Collection<ValidationRule> userRules = userRulesMap.get( user );
            List<ValidationResult> userResults = new ArrayList<ValidationResult>();

            for ( ValidationResult result : results )
            {
                if ( userRules.contains( result.getValidationRule() ) )
                {
                    userResults.add( result );
                }
            }

            // Group this user with other users having the same result subset.

            if ( !userResults.isEmpty() )
            {
                Set<User> messageReceivers = messageMap.get( userResults );
                if ( messageReceivers == null )
                {
                    messageReceivers = new HashSet<User>();
                    messageMap.put( userResults, messageReceivers );
                }
                messageReceivers.add( user );
            }
        }
                
        return messageMap;
    }

    /**
     * Constructs a Map where the key is each user who is configured to
     * receive alerts, and the value is a list of rules they should receive
     * results for.
     * 
     * @return Map from users to sets of rules
     */
    private Map<User, Set<ValidationRule>> getUserRulesMap()
    {
        Map<User, Set<ValidationRule>> userRulesMap = new HashMap<User, Set<ValidationRule>>();

        for ( ValidationRuleGroup validationRuleGroup : getAllValidationRuleGroups() )
        {
            Collection<UserAuthorityGroup> userRolesToAlert = validationRuleGroup.getUserAuthorityGroupsToAlert();
            
            if ( userRolesToAlert != null && !userRolesToAlert.isEmpty() )
            {
                for ( UserAuthorityGroup role : userRolesToAlert )
                {
                    for ( UserCredentials userCredentials : role.getMembers() )
                    {
                        User user = userCredentials.getUser();
                        Set<ValidationRule> userRules = userRulesMap.get( user );
                        if ( userRules == null )
                        {
                            userRules = new HashSet<ValidationRule>();
                            userRulesMap.put( user, userRules );
                        }
                        userRules.addAll( validationRuleGroup.getMembers() );
                    }
                }
            }
        }
        
        return userRulesMap;
    }

    /**
     * Generate and send an alert message containing a list of validation
     * results to a set of users.
     * 
     * @param results results to put in this message
     * @param users users to receive these results
     * @param scheduledRunStart date/time when the scheduled run started
     */
    private void sendAlertmessage( List<ValidationResult> results, Set<User> users, Date scheduledRunStart )
    {
        StringBuilder builder = new StringBuilder();

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );

        Map<String, Integer> importanceCountMap = countResultsByImportanceType( results );

        String subject = "Alerts as of " + dateTimeFormatter.format( scheduledRunStart ) + ": High "
            + ( importanceCountMap.get( "high" ) == null ? 0 : importanceCountMap.get( "high" ) ) + ", Medium "
            + ( importanceCountMap.get( "medium" ) == null ? 0 : importanceCountMap.get( "medium" ) ) + ", Low "
            + ( importanceCountMap.get( "low" ) == null ? 0 : importanceCountMap.get( "low" ) );

        //TODO use velocity template for message
        
        for ( ValidationResult result : results )
        {
            ValidationRule rule = result.getValidationRule();
            
            builder.append( result.getSource().getName() ).append( " " ).append( result.getPeriod().getName() ).append( LN ).
            append( rule.getName() ).append( " (" ).append( rule.getImportance() ).append( ") " ).append( LN ).
            append( rule.getLeftSide().getDescription() ).append( ": " ).append( result.getLeftsideValue() ).append( LN ).
            append( rule.getRightSide().getDescription() ).append( ": " ).append( result.getRightsideValue() ).append( LN ).append( LN );
        }
        
        log.info( "Alerting users: " + users.size() + ", subject: " + subject );
        messageService.sendMessage( subject, builder.toString(), null, users );
    }

    // -------------------------------------------------------------------------
    // Supportive methods - monitoring
    // -------------------------------------------------------------------------

    /**
     * Counts the results of each importance type, for all the importance
     * types that are found within the results.
     * 
     * @param results results to analyze
     * @return Mapping between importance type and result counts.
     */
    private Map<String, Integer> countResultsByImportanceType ( List<ValidationResult> results )
    {
        Map<String, Integer> importanceCountMap = new HashMap<String, Integer>();
        
        for ( ValidationResult result : results )
        {
            Integer importanceCount = importanceCountMap.get( result.getValidationRule().getImportance() );
            
            importanceCountMap.put( result.getValidationRule().getImportance(), importanceCount == null ? 1
                : importanceCount + 1 );
        }
        
        return importanceCountMap;
    }
    
    /**
     * Returns all validation-type rules which have specified data elements
     * assigned to them.
     * 
     * @param dataElements the data elements to look for
     * @return all validation rules which have the data elements assigned.
     */
    private Collection<ValidationRule> getValidationTypeRulesForDataElements( Set<DataElement> dataElements )
    {
        Set<ValidationRule> rulesForDataElements = new HashSet<ValidationRule>();

        Set<DataElement> validationRuleElements = new HashSet<DataElement>();

        for ( ValidationRule validationRule : getAllValidationRules() )
        {
            if ( validationRule.getRuleType().equals( ValidationRule.RULE_TYPE_VALIDATION ) )
            {
                validationRuleElements.clear();
                validationRuleElements.addAll( validationRule.getLeftSide().getDataElementsInExpression() );
                validationRuleElements.addAll( validationRule.getRightSide().getDataElementsInExpression() );

                if ( dataElements.containsAll( validationRuleElements ) )
                {
                    rulesForDataElements.add( validationRule );
                }
            }
        }

        return rulesForDataElements;
    }

    /**
     * Returns all validation rules which have data elements assigned to them
     * which are members of the given data set.
     * 
     * @param dataSet the data set
     * @return all validation rules which have data elements assigned to them
     *         which are members of the given data set
     */
    private Collection<ValidationRule> getRulesForDataSet( DataSet dataSet )
    {
        Set<ValidationRule> rulesForDataSet = new HashSet<ValidationRule>();

        Set<DataElementOperand> operands = dataEntryFormService.getOperandsInDataEntryForm( dataSet );

        Set<DataElementOperand> validationRuleOperands = new HashSet<DataElementOperand>();

        for ( ValidationRule rule : getAllValidationRules() )
        {
            if ( rule.getRuleType().equals( ValidationRule.RULE_TYPE_VALIDATION ) )
            {
                validationRuleOperands.clear();
                validationRuleOperands.addAll( expressionService.getOperandsInExpression(
                    rule.getLeftSide().getExpression() ) );
                validationRuleOperands.addAll( expressionService.getOperandsInExpression(
                    rule.getRightSide().getExpression() ) );

                if ( operands.containsAll( validationRuleOperands ) )
                {
                    rulesForDataSet.add( rule );
                }
            }
        }

        return rulesForDataSet;
    }
    
    /**
     * Formats and sets name on the period of each result.
     * 
     * @param results the collecion of validation results.
     * @param format the i18n format.
     */
    private void formatPeriods( Collection<ValidationResult> results, I18nFormat format )
    {
        if ( format != null )
        {
            for ( ValidationResult result : results )
            {
                if ( result != null && result.getPeriod() != null )
                {
                    result.getPeriod().setName( format.formatPeriod( result.getPeriod() ) );
                }
            }
        }
    }
    
    // -------------------------------------------------------------------------
    // ValidationRule CRUD operations
    // -------------------------------------------------------------------------

    public int saveValidationRule( ValidationRule validationRule )
    {
        return validationRuleStore.save( validationRule );
    }

    public void updateValidationRule( ValidationRule validationRule )
    {
        validationRuleStore.update( validationRule );
    }

    public void deleteValidationRule( ValidationRule validationRule )
    {
        validationRuleStore.delete( validationRule );
    }

    public ValidationRule getValidationRule( int id )
    {
        return i18n( i18nService, validationRuleStore.get( id ) );
    }

    public ValidationRule getValidationRule( String uid )
    {
        return i18n( i18nService, validationRuleStore.getByUid( uid ) );
    }

    public ValidationRule getValidationRuleByName( String name )
    {
        return i18n( i18nService, validationRuleStore.getByName( name ) );
    }

    public Collection<ValidationRule> getAllValidationRules()
    {
        return i18n( i18nService, validationRuleStore.getAll() );
    }

    public Collection<ValidationRule> getValidationRules( final Collection<Integer> identifiers )
    {
        Collection<ValidationRule> objects = getAllValidationRules();

        return identifiers == null ? objects : FilterUtils.filter( objects, new Filter<ValidationRule>()
        {
            public boolean retain( ValidationRule object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public Collection<ValidationRule> getValidationRulesByName( String name )
    {
        return getObjectsByName( i18nService, validationRuleStore, name );
    }

    public Collection<ValidationRule> getValidationRulesByDataElements( Collection<DataElement> dataElements )
    {
        return i18n( i18nService, validationRuleStore.getValidationRulesByDataElements( dataElements ) );
    }

    public int getValidationRuleCount()
    {
        return validationRuleStore.getCount();
    }

    public int getValidationRuleCountByName( String name )
    {
        return getCountByName( i18nService, validationRuleStore, name );
    }

    public Collection<ValidationRule> getValidationRulesBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, validationRuleStore, first, max );
    }

    public Collection<ValidationRule> getValidationRulesBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, validationRuleStore, name, first, max );
    }

    // -------------------------------------------------------------------------
    // ValidationRuleGroup CRUD operations
    // -------------------------------------------------------------------------

    public int addValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        return validationRuleGroupStore.save( validationRuleGroup );
    }

    public void deleteValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        validationRuleGroupStore.delete( validationRuleGroup );
    }

    public void updateValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        validationRuleGroupStore.update( validationRuleGroup );
    }

    public ValidationRuleGroup getValidationRuleGroup( int id )
    {
        return i18n( i18nService, validationRuleGroupStore.get( id ) );
    }

    public ValidationRuleGroup getValidationRuleGroup( int id, boolean i18nValidationRules )
    {
        ValidationRuleGroup group = getValidationRuleGroup( id );

        if ( i18nValidationRules )
        {
            i18n( i18nService, group.getMembers() );
        }

        return group;
    }

    public ValidationRuleGroup getValidationRuleGroup( String uid )
    {
        return i18n( i18nService, validationRuleGroupStore.getByUid( uid ) );
    }

    public Collection<ValidationRuleGroup> getAllValidationRuleGroups()
    {
        return i18n( i18nService, validationRuleGroupStore.getAll() );
    }

    public ValidationRuleGroup getValidationRuleGroupByName( String name )
    {
        return i18n( i18nService, validationRuleGroupStore.getByName( name ) );
    }

    public int getValidationRuleGroupCount()
    {
        return validationRuleGroupStore.getCount();
    }

    public int getValidationRuleGroupCountByName( String name )
    {
        return getCountByName( i18nService, validationRuleGroupStore, name );
    }

    public Collection<ValidationRuleGroup> getValidationRuleGroupsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, validationRuleGroupStore, first, max );
    }

    public Collection<ValidationRuleGroup> getValidationRuleGroupsBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, validationRuleGroupStore, name, first, max );
    }
}
