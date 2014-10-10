package org.hisp.dhis;

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

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregatedOrgUnitDataValueService;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.expression.Operator;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportObjectStatus;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.mapping.MapLegend;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.resourcetable.ResourceTableService;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.validation.ValidationCriteria;
import org.hisp.dhis.validation.ValidationCriteriaService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.xml.sax.InputSource;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public abstract class DhisConvenienceTest
{
    protected static final Log log = LogFactory.getLog( DhisConvenienceTest.class );

    protected static final String BASE_UID = "123456789a";

    protected static final String BASE_IN_UID = "inabcdefgh";

    protected static final String BASE_DE_UID = "deabcdefgh";

    protected static final String BASE_DS_UID = "dsabcdefgh";

    protected static final String BASE_OU_UID = "ouabcdefgh";

    private static final String EXT_TEST_DIR = System.getProperty( "user.home" ) + File.separator + "dhis2_test_dir";

    private static Date date;

    protected static final double DELTA = 0.01;

    // -------------------------------------------------------------------------
    // Service references
    // -------------------------------------------------------------------------

    protected DataElementService dataElementService;

    protected DataElementCategoryService categoryService;

    protected DataDictionaryService dataDictionaryService;

    protected IndicatorService indicatorService;

    protected DataSetService dataSetService;

    protected SectionService sectionService;

    protected CompleteDataSetRegistrationService completeDataSetRegistrationService;

    protected OrganisationUnitService organisationUnitService;

    protected OrganisationUnitGroupService organisationUnitGroupService;

    protected AggregatedDataValueService aggregatedDataValueService;

    protected AggregatedOrgUnitDataValueService aggregatedOrgUnitDataValueService;

    protected PeriodService periodService;

    protected ConstantService constantService;

    protected ValidationRuleService validationRuleService;

    protected ValidationCriteriaService validationCriteriaService;

    protected ExpressionService expressionService;

    protected DataValueService dataValueService;

    protected ResourceTableService resourceTableService;

    protected MappingService mappingService;

    protected ProgramStageService programStageService;

    protected DataEntryFormService dataEntryFormService;

    protected UserService userService;

    protected MessageService messageService;
    
    protected IdentifiableObjectManager identifiableObjectManager;

    static
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set( 1970, Calendar.JANUARY, 1 );

        date = calendar.getTime();
    }

    // -------------------------------------------------------------------------
    // Convenience methods
    // -------------------------------------------------------------------------

    /**
     * Creates a date.
     * 
     * @param year the year.
     * @param month the month.
     * @param day the day of month.
     * @return a date.
     */
    public static Date getDate( int year, int month, int day )
    {
        final Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.MONTH, month - 1 );
        calendar.set( Calendar.DAY_OF_MONTH, day );

        return calendar.getTime();
    }

    /**
     * Creates a date.
     * 
     * @param day the day of the year.
     * @return a date.
     */
    public Date getDay( int day )
    {
        final Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set( Calendar.DAY_OF_YEAR, day );

        return calendar.getTime();
    }

    /**
     * Compares two collections for equality. This method does not check for the
     * implementation type of the collection in contrast to the native equals
     * method. This is useful for black-box testing where one will not know the
     * implementation type of the returned collection for a method.
     * 
     * @param actual the actual collection to check.
     * @param reference the reference objects to check against.
     * @return true if the collections are equal, false otherwise.
     */
    public static boolean equals( Collection<?> actual, Object... reference )
    {
        final Collection<Object> collection = new HashSet<Object>();

        Collections.addAll( collection, reference );

        if ( actual == collection )
        {
            return true;
        }

        if ( actual == null )
        {
            return false;
        }

        if ( actual.size() != collection.size() )
        {
            log.warn( "Actual collection has different size compared to reference collection: " + actual.size() + " / "
                + collection.size() );
            return false;
        }

        for ( Object object : actual )
        {
            if ( !collection.contains( object ) )
            {
                log.warn( "Object in actual collection not part of reference collection: " + object );
                return false;
            }
        }

        for ( Object object : collection )
        {
            if ( !actual.contains( object ) )
            {
                log.warn( "Object in reference collection not part of actual collection: " + object );
                return false;
            }
        }

        return true;
    }

    public static String message( Object expected )
    {
        return "Expected was: " + ((expected != null) ? "[" + expected.toString() + "]" : "[null]");
    }

    public static String message( Object expected, Object actual )
    {
        return message( expected ) + " Actual was: " + ((actual != null) ? "[" + actual.toString() + "]" : "[null]");
    }

    // -------------------------------------------------------------------------
    // Dependency injection methods
    // -------------------------------------------------------------------------

    /**
     * Sets a dependency on the target service. This method can be used to set
     * mock implementations of dependencies on services for testing purposes.
     * The advantage of using this method over setting the services directly is
     * that the test can still be executed against the interface type of the
     * service; making the test unaware of the implementation and thus
     * re-usable. A weakness is that the field name of the dependency must be
     * assumed.
     * 
     * @param targetService the target service.
     * @param fieldName the name of the dependency field in the target service.
     * @param dependency the dependency.
     */
    protected void setDependency( Object targetService, String fieldName, Object dependency )
    {
        Class<?> clazz = dependency.getClass().getInterfaces()[0];

        setDependency( targetService, fieldName, dependency, clazz );
    }

    /**
     * Sets a dependency on the target service. This method can be used to set
     * mock implementations of dependencies on services for testing purposes.
     * The advantage of using this method over setting the services directly is
     * that the test can still be executed against the interface type of the
     * service; making the test unaware of the implementation and thus
     * re-usable. A weakness is that the field name of the dependency must be
     * assumed.
     * 
     * @param targetService the target service.
     * @param fieldName the name of the dependency field in the target service.
     * @param dependency the dependency.
     * @param clazz the class type of the dependency.
     */
    protected void setDependency( Object targetService, String fieldName, Object dependency, Class<?> clazz )
    {
        try
        {
            targetService = getRealObject( targetService );

            String setMethodName = "set" + fieldName.substring( 0, 1 ).toUpperCase()
                + fieldName.substring( 1, fieldName.length() );

            Class<?>[] argumentClass = new Class<?>[] { clazz };

            Method method = targetService.getClass().getMethod( setMethodName, argumentClass );

            method.invoke( targetService, dependency );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to set dependency '" + fieldName + "' on service", ex );
        }
    }

    /**
     * If the given class is advised by Spring AOP it will return the target
     * class, i.e. the advised class. If not the given class is returned
     * unchanged.
     * 
     * @param object the object.
     */
    @SuppressWarnings( "unchecked" )
    private <T> T getRealObject( T object )
        throws Exception
    {
        if ( AopUtils.isAopProxy( object ) )
        {
            return (T) ((Advised) object).getTargetSource().getTarget();
        }

        return object;
    }

    // -------------------------------------------------------------------------
    // Create object methods
    // -------------------------------------------------------------------------

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static DataElement createDataElement( char uniqueCharacter )
    {
        DataElement dataElement = new DataElement();

        dataElement.setUid( BASE_DE_UID + uniqueCharacter );
        dataElement.setName( "DataElement" + uniqueCharacter );
        dataElement.setShortName( "DataElementShort" + uniqueCharacter );
        dataElement.setCode( "DataElementCode" + uniqueCharacter );
        dataElement.setDescription( "DataElementDescription" + uniqueCharacter );
        dataElement.setActive( true );
        dataElement.setType( DataElement.VALUE_TYPE_INT );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        dataElement.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );

        return dataElement;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param categoryCombo The category combo.
     */
    public static DataElement createDataElement( char uniqueCharacter, DataElementCategoryCombo categoryCombo )
    {
        DataElement dataElement = createDataElement( uniqueCharacter );

        dataElement.setCategoryCombo( categoryCombo );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );

        return dataElement;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param type The value type.
     * @param aggregationOperator The aggregation operator.
     */
    public static DataElement createDataElement( char uniqueCharacter, String type, String aggregationOperator )
    {
        DataElement dataElement = createDataElement( uniqueCharacter );
        dataElement.setType( type );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        dataElement.setAggregationOperator( aggregationOperator );

        return dataElement;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param type The value type.
     * @param aggregationOperator The aggregation operator.
     * @param categoryCombo The category combo.
     */
    public static DataElement createDataElement( char uniqueCharacter, String type, String aggregationOperator,
        DataElementCategoryCombo categoryCombo )
    {
        DataElement dataElement = createDataElement( uniqueCharacter );
        dataElement.setType( type );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        dataElement.setAggregationOperator( aggregationOperator );
        dataElement.setCategoryCombo( categoryCombo );

        return dataElement;
    }

    /**
     * @param categoryComboUniqueIdentifier A unique character to identify the
     *        category combo.
     * @param categoryOptionUniqueIdentifiers Unique characters to identify the
     *        category options.
     * @return DataElementCategoryOptionCombo
     */
    public static DataElementCategoryOptionCombo createCategoryOptionCombo( char categoryComboUniqueIdentifier,
        char... categoryOptionUniqueIdentifiers )
    {
        DataElementCategoryOptionCombo categoryOptionCombo = new DataElementCategoryOptionCombo();

        categoryOptionCombo.setCategoryCombo( new DataElementCategoryCombo( "CategoryCombo"
            + categoryComboUniqueIdentifier ) );

        for ( char identifier : categoryOptionUniqueIdentifiers )
        {
            categoryOptionCombo.getCategoryOptions()
                .add( new DataElementCategoryOption( "CategoryOption" + identifier ) );
        }

        return categoryOptionCombo;
    }

    /**
     * @param categoryCombo the category combo.
     * @param categoryOptions the category options.
     * @return DataElementCategoryOptionCombo
     */
    public static DataElementCategoryOptionCombo createCategoryOptionCombo( DataElementCategoryCombo categoryCombo,
        DataElementCategoryOption... categoryOptions )
    {
        DataElementCategoryOptionCombo categoryOptionCombo = new DataElementCategoryOptionCombo();

        categoryOptionCombo.setCategoryCombo( categoryCombo );

        for ( DataElementCategoryOption categoryOption : categoryOptions )
        {
            categoryOptionCombo.getCategoryOptions().add( categoryOption );
        }

        return categoryOptionCombo;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static DataElementGroup createDataElementGroup( char uniqueCharacter )
    {
        DataElementGroup group = new DataElementGroup();

        group.setUid( BASE_UID + uniqueCharacter );
        group.setName( "DataElementGroup" + uniqueCharacter );
        group.setShortName( "DataElementGroup" + uniqueCharacter );
        group.setCode( "DataElementCode" + uniqueCharacter );

        return group;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static DataElementGroupSet createDataElementGroupSet( char uniqueCharacter )
    {
        DataElementGroupSet groupSet = new DataElementGroupSet();

        groupSet.setUid( BASE_UID + uniqueCharacter );
        groupSet.setName( "DataElementGroupSet" + uniqueCharacter );

        return groupSet;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static DataDictionary createDataDictionary( char uniqueCharacter )
    {
        DataDictionary dictionary = new DataDictionary();

        dictionary.setName( "DataDictionary" + uniqueCharacter );
        dictionary.setDescription( "Description" + uniqueCharacter );
        dictionary.setRegion( "Region" + uniqueCharacter );

        return dictionary;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static IndicatorType createIndicatorType( char uniqueCharacter )
    {
        IndicatorType type = new IndicatorType();

        type.setName( "IndicatorType" + uniqueCharacter );
        type.setFactor( 100 );

        return type;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param type The type.
     */
    public static Indicator createIndicator( char uniqueCharacter, IndicatorType type )
    {
        Indicator indicator = new Indicator();

        indicator.setUid( BASE_IN_UID + uniqueCharacter );
        indicator.setName( "Indicator" + uniqueCharacter );
        indicator.setShortName( "IndicatorShort" + uniqueCharacter );
        indicator.setCode( "IndicatorCode" + uniqueCharacter );
        indicator.setDescription( "IndicatorDescription" + uniqueCharacter );
        indicator.setAnnualized( false );
        indicator.setIndicatorType( type );
        indicator.setNumerator( "Numerator" );
        indicator.setNumeratorDescription( "NumeratorDescription" );
        indicator.setDenominator( "Denominator" );
        indicator.setDenominatorDescription( "DenominatorDescription" );

        return indicator;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static IndicatorGroup createIndicatorGroup( char uniqueCharacter )
    {
        IndicatorGroup group = new IndicatorGroup();

        group.setUid( BASE_UID + uniqueCharacter );
        group.setName( "IndicatorGroup" + uniqueCharacter );

        return group;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static IndicatorGroupSet createIndicatorGroupSet( char uniqueCharacter )
    {
        IndicatorGroupSet groupSet = new IndicatorGroupSet();

        groupSet.setUid( BASE_UID + uniqueCharacter );
        groupSet.setName( "IndicatorGroupSet" + uniqueCharacter );

        return groupSet;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param periodType The period type.
     */
    public static DataSet createDataSet( char uniqueCharacter, PeriodType periodType )
    {
        DataSet dataSet = new DataSet();

        dataSet.setUid( BASE_DS_UID + uniqueCharacter );
        dataSet.setName( "DataSet" + uniqueCharacter );
        dataSet.setShortName( "DataSetShort" + uniqueCharacter );
        dataSet.setCode( "DataSetCode" + uniqueCharacter );
        dataSet.setPeriodType( periodType );

        return dataSet;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static OrganisationUnit createOrganisationUnit( char uniqueCharacter )
    {
        OrganisationUnit unit = new OrganisationUnit();

        unit.setUid( BASE_OU_UID + uniqueCharacter );
        unit.setName( "OrganisationUnit" + uniqueCharacter );
        unit.setShortName( "OrganisationUnitShort" + uniqueCharacter );
        unit.setCode( "OrganisationUnitCode" + uniqueCharacter );
        unit.setOpeningDate( date );
        unit.setClosedDate( date );
        unit.setActive( true );
        unit.setComment( "Comment" + uniqueCharacter );
        unit.setGeoCode( "GeoCode" );

        return unit;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param parent The parent.
     */
    public static OrganisationUnit createOrganisationUnit( char uniqueCharacter, OrganisationUnit parent )
    {
        OrganisationUnit unit = createOrganisationUnit( uniqueCharacter );

        unit.setParent( parent );
        parent.getChildren().add( unit );

        return unit;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static OrganisationUnitGroup createOrganisationUnitGroup( char uniqueCharacter )
    {
        OrganisationUnitGroup group = new OrganisationUnitGroup();

        group.setUid( BASE_UID + uniqueCharacter );
        group.setName( "OrganisationUnitGroup" + uniqueCharacter );
        group.setShortName( "OrganisationUnitGroupShort" + uniqueCharacter );
        group.setCode( "OrganisationUnitGroupCode" + uniqueCharacter );

        return group;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static OrganisationUnitGroupSet createOrganisationUnitGroupSet( char uniqueCharacter )
    {
        OrganisationUnitGroupSet groupSet = new OrganisationUnitGroupSet();

        groupSet.setName( "OrganisationUnitGroupSet" + uniqueCharacter );
        groupSet.setDescription( "Description" + uniqueCharacter );
        groupSet.setCompulsory( true );

        return groupSet;
    }

    /**
     * @param type The PeriodType.
     * @param startDate The start date.
     * @param endDate The end date.
     */
    public static Period createPeriod( PeriodType type, Date startDate, Date endDate )
    {
        Period period = new Period();

        period.setPeriodType( type );
        period.setStartDate( startDate );
        period.setEndDate( endDate );

        return period;
    }

    /**
     * @param isoPeriod the ISO period string.
     */
    public static Period createPeriod( String isoPeriod )
    {
        return PeriodType.getPeriodFromIsoString( isoPeriod );
    }

    /**
     * @param startDate The start date.
     * @param endDate The end date.
     */
    public static Period createPeriod( Date startDate, Date endDate )
    {
        Period period = new Period();

        period.setPeriodType( new MonthlyPeriodType() );
        period.setStartDate( startDate );
        period.setEndDate( endDate );

        return period;
    }

    /**
     * Uses the given category option combo also as attribute option combo.
     * 
     * @param dataElement The data element.
     * @param period The period.
     * @param source The source.
     * @param value The value.
     * @param categoryOptionCombo The category (and attribute) option combo.
     */
    public static DataValue createDataValue( DataElement dataElement, Period period, OrganisationUnit source,
        String value, DataElementCategoryOptionCombo categoryOptionCombo )
    {
        DataValue dataValue = new DataValue();

        dataValue.setDataElement( dataElement );
        dataValue.setPeriod( period );
        dataValue.setSource( source );
        dataValue.setCategoryOptionCombo( categoryOptionCombo );
        dataValue.setAttributeOptionCombo( categoryOptionCombo );
        dataValue.setValue( value );
        dataValue.setComment( "Comment" );
        dataValue.setStoredBy( "StoredBy" );
        dataValue.setTimestamp( date );

        return dataValue;
    }

    /**
     * @param dataElement The data element.
     * @param period The period.
     * @param source The source.
     * @param value The value.
     * @param categoryOptionCombo The category option combo.
     * @param attributeOptionCombo The attribute option combo.
     */
    public static DataValue createDataValue( DataElement dataElement, Period period, OrganisationUnit source,
        String value, DataElementCategoryOptionCombo categoryOptionCombo, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        DataValue dataValue = new DataValue();

        dataValue.setDataElement( dataElement );
        dataValue.setPeriod( period );
        dataValue.setSource( source );
        dataValue.setCategoryOptionCombo( categoryOptionCombo );
        dataValue.setAttributeOptionCombo( attributeOptionCombo );
        dataValue.setValue( value );
        dataValue.setComment( "Comment" );
        dataValue.setStoredBy( "StoredBy" );
        dataValue.setTimestamp( date );

        return dataValue;
    }

    /**
     * @param dataElement The data element.
     * @param period The period.
     * @param source The source.
     * @param value The value.
     * @param lastupdated The date.value.
     * @param categoryOptionCombo The category option combo.
     * @param attributeOptionCombo The attribute option combo.
     */
    public static DataValue createDataValue( DataElement dataElement, Period period, OrganisationUnit source,
        String value, Date lastupdated, DataElementCategoryOptionCombo categoryOptionCombo, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        DataValue dataValue = new DataValue();

        dataValue.setDataElement( dataElement );
        dataValue.setPeriod( period );
        dataValue.setSource( source );
        dataValue.setCategoryOptionCombo( categoryOptionCombo );
        dataValue.setAttributeOptionCombo( attributeOptionCombo );
        dataValue.setValue( value );
        dataValue.setComment( "Comment" );
        dataValue.setStoredBy( "StoredBy" );
        dataValue.setTimestamp( lastupdated );

        return dataValue;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param operator The operator.
     * @param leftSide The left side expression.
     * @param rightSide The right side expression.
     * @param periodType The period-type.
     */
    public static ValidationRule createValidationRule( char uniqueCharacter, Operator operator, Expression leftSide,
        Expression rightSide, PeriodType periodType )
    {
        ValidationRule validationRule = new ValidationRule();

        validationRule.setName( "ValidationRule" + uniqueCharacter );
        validationRule.setDescription( "Description" + uniqueCharacter );
        validationRule.setType( ValidationRule.TYPE_ABSOLUTE );
        validationRule.setOperator( operator );
        validationRule.setLeftSide( leftSide );
        validationRule.setRightSide( rightSide );
        validationRule.setPeriodType( periodType );

        return validationRule;
    }

    /**
     * Creates a ValidationRule of RULE_TYPE_MONITORING
     * 
     * @param uniqueCharacter A unique character to identify the object.
     * @param operator The operator.
     * @param leftSide The left side expression.
     * @param rightSide The right side expression.
     * @param periodType The period-type.
     * @param organisationUnitLevel The unit level of organisations to be
     *        evaluated by this rule.
     * @param sequentialSampleCount How many sequential past periods to sample.
     * @param annualSampleCount How many years of past periods to sample.
     * @param highOutliers How many high outlying past samples to discard before
     *        averaging.
     * @param lowOutliers How many low outlying past samples to discard before
     *        averaging.
     */
    public static ValidationRule createMonitoringRule( char uniqueCharacter, Operator operator, Expression leftSide,
        Expression rightSide, PeriodType periodType, int organisationUnitLevel, int sequentialSampleCount,
        int annualSampleCount, int highOutliers, int lowOutliers )
    {
        ValidationRule validationRule = new ValidationRule();

        validationRule.setName( "MonitoringRule" + uniqueCharacter );
        validationRule.setDescription( "Description" + uniqueCharacter );
        validationRule.setType( ValidationRule.TYPE_ABSOLUTE );
        validationRule.setRuleType( ValidationRule.RULE_TYPE_SURVEILLANCE );
        validationRule.setOperator( operator );
        validationRule.setLeftSide( leftSide );
        validationRule.setRightSide( rightSide );
        validationRule.setPeriodType( periodType );
        validationRule.setOrganisationUnitLevel( organisationUnitLevel );
        validationRule.setSequentialSampleCount( sequentialSampleCount );
        validationRule.setAnnualSampleCount( annualSampleCount );
        validationRule.setHighOutliers( highOutliers );
        validationRule.setLowOutliers( lowOutliers );

        return validationRule;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return ValidationRuleGroup
     */
    public static ValidationRuleGroup createValidationRuleGroup( char uniqueCharacter )
    {
        ValidationRuleGroup group = new ValidationRuleGroup();

        group.setName( "ValidationRuleGroup" + uniqueCharacter );
        group.setDescription( "Description" + uniqueCharacter );

        return group;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param expressionString The expression string.
     * @param dataElementsInExpression A collection of the data elements
     *        entering into the expression.
     */
    public static Expression createExpression( char uniqueCharacter, String expressionString,
        Set<DataElement> dataElementsInExpression, Set<DataElementCategoryOptionCombo> optionCombosInExpression )
    {
        Expression expression = new Expression();

        expression.setExpression( expressionString );
        expression.setDescription( "Description" + uniqueCharacter );
        expression.setDataElementsInExpression( dataElementsInExpression );
        expression.setOptionCombosInExpression( optionCombosInExpression );

        return expression;
    }

    /**
     * @param dataElementId The data element identifier.
     * @param categoryOptionComboId The data element category option combo
     *        identifier.
     * @param periodId The period identifier.
     * @param sourceId The source identifier.
     * @param status The status.
     */
    public static ImportDataValue createImportDataValue( int dataElementId, int categoryOptionComboId, int periodId,
        int sourceId, ImportObjectStatus status )
    {
        ImportDataValue importDataValue = new ImportDataValue();

        importDataValue.setDataElementId( dataElementId );
        importDataValue.setCategoryOptionComboId( categoryOptionComboId );
        importDataValue.setPeriodId( periodId );
        importDataValue.setSourceId( sourceId );
        importDataValue.setValue( String.valueOf( 10 ) );
        importDataValue.setStoredBy( "StoredBy" );
        importDataValue.setTimestamp( new Date() );
        importDataValue.setComment( "Comment" );
        importDataValue.setStatus( status.name() );

        return importDataValue;
    }

    public static MapLegend createMapLegend( char uniqueCharacter, Double startValue, Double endValue )
    {
        MapLegend legend = new MapLegend();

        legend.setName( "MapLegend" + uniqueCharacter );
        legend.setStartValue( startValue );
        legend.setEndValue( endValue );
        legend.setColor( "Color" + uniqueCharacter );

        return legend;
    }

    public static MapLegendSet createMapLegendSet( char uniqueCharacter )
    {
        MapLegendSet legendSet = new MapLegendSet();

        legendSet.setName( "MapLegendSet" + uniqueCharacter );

        return legendSet;
    }

    public static Chart createChart( char uniqueCharacter, List<Indicator> indicators, List<Period> periods,
        List<OrganisationUnit> units )
    {
        Chart chart = new Chart();

        chart.setName( "Chart" + uniqueCharacter );
        chart.setIndicators( indicators );
        chart.setPeriods( periods );
        chart.setOrganisationUnits( units );
        chart.setDimensions( DimensionalObject.DATA_X_DIM_ID, DimensionalObject.PERIOD_DIM_ID,
            DimensionalObject.ORGUNIT_DIM_ID );

        return chart;
    }

    public static User createUser( char uniqueCharacter )
    {
        User user = new User();

        user.setFirstName( "FirstName" + uniqueCharacter );
        user.setSurname( "Surname" + uniqueCharacter );
        user.setEmail( "Email" + uniqueCharacter );
        user.setPhoneNumber( "PhoneNumber" + uniqueCharacter );

        UserCredentials credentials = new UserCredentials();
        credentials.setUsername( "username" );
        credentials.setPassword( "password" );

        user.setUserCredentials( credentials );

        return user;
    }

    public static UserGroup createUserGroup( char uniqueCharacter, Set<User> users )
    {
        UserGroup userGroup = new UserGroup();

        userGroup.setName( "UserGroup" + uniqueCharacter );
        userGroup.setMembers( users );

        return userGroup;
    }

    protected static Program createProgram( char uniqueCharacter, Set<ProgramStage> programStages,
        OrganisationUnit organisationUnit )
    {
        Program program = new Program();

        program.setName( "Program" + uniqueCharacter );
        program.setDescription( "Description" + uniqueCharacter );
        program.setDateOfEnrollmentDescription( "DateOfEnrollmentDescription" );
        program.setDateOfIncidentDescription( "DateOfIncidentDescription" );
        program.setProgramStages( programStages );
        program.setType( Program.MULTIPLE_EVENTS_WITH_REGISTRATION );

        for ( ProgramStage programStage : programStages )
        {
            programStage.setProgram( program );
        }

        program.getOrganisationUnits().add( organisationUnit );

        return program;
    }

    public static ProgramStage createProgramStage( char uniqueCharacter, int minDays )
    {
        return createProgramStage( uniqueCharacter, minDays, false );
    }

    public static ProgramStage createProgramStage( char uniqueCharacter, int minDays, boolean irregular )
    {
        ProgramStage programStage = new ProgramStage();

        programStage.setName( "ProgramStage" + uniqueCharacter );
        programStage.setDescription( "description" + uniqueCharacter );
        programStage.setMinDaysFromStart( minDays );
        programStage.setIrregular( irregular );

        return programStage;
    }

    public static Patient createPatient( char uniqueChar, OrganisationUnit organisationUnit )
    {
        Patient patient = new Patient();
        patient.setAutoFields();
        patient.setOrganisationUnit( organisationUnit );

        return patient;
    }

    public static Patient createPatient( char uniqueChar, OrganisationUnit organisationUnit,
        PatientIdentifierType patientIdentifierType )
    {
        Patient patient = new Patient();
        patient.setAutoFields();
        patient.setOrganisationUnit( organisationUnit );

        PatientIdentifier pIdentifier = new PatientIdentifier();
        pIdentifier.setIdentifierType( patientIdentifierType );
        pIdentifier.setPatient( patient );
        pIdentifier.setIdentifier( "Identifier" + uniqueChar );
        patient.getIdentifiers().add( pIdentifier );

        return patient;
    }

    public static PatientAttributeValue createPatientAttributeValue( char uniqueChar, Patient patient,
        PatientAttribute patientAttribute )
    {
        PatientAttributeValue attributeValue = new PatientAttributeValue();
        attributeValue.setPatient( patient );
        attributeValue.setPatientAttribute( patientAttribute );
        attributeValue.setValue( "Attribute" + uniqueChar );

        return attributeValue;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return PatientAttribute
     */
    public static PatientAttribute createPatientAttribute( char uniqueChar )
    {
        PatientAttribute patientAttribute = new PatientAttribute();

        patientAttribute.setName( "Attribute" + uniqueChar );
        patientAttribute.setDescription( "Attribute" + uniqueChar );
        patientAttribute.setValueType( PatientAttribute.TYPE_STRING );

        return patientAttribute;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return PatientAttribute
     */
    public static PatientAttribute createPatientAttribute( char uniqueChar, String type )
    {
        PatientAttribute patientAttribute = new PatientAttribute();

        patientAttribute.setName( "Attribute" + uniqueChar );
        patientAttribute.setDescription( "Attribute" + uniqueChar );
        patientAttribute.setValueType( type );

        return patientAttribute;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return PatientAttributeOption
     */
    public static PatientAttributeOption createPatientAttributeOption( char uniqueChar,
        PatientAttribute patientAttribute )
    {
        PatientAttributeOption patientAttributeOption = new PatientAttributeOption();

        patientAttributeOption.setName( "AttributeOption" + uniqueChar );
        patientAttributeOption.setPatientAttribute( patientAttribute );

        return patientAttributeOption;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return PatientAttributeGroup
     */
    public static PatientAttributeGroup createPatientAttributeGroup( char uniqueChar, List<PatientAttribute> attributes )
    {
        PatientAttributeGroup patientAttributeGroup = new PatientAttributeGroup();

        patientAttributeGroup.setName( "PatientAttributeGroup" + uniqueChar );
        patientAttributeGroup.setDescription( "PatientAttributeGroup" + uniqueChar );
        patientAttributeGroup.setAttributes( attributes );

        return patientAttributeGroup;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return PatientIdentifierType
     */
    public static PatientIdentifierType createPatientIdentifierType( char uniqueChar )
    {
        PatientIdentifierType identifierType = new PatientIdentifierType();

        identifierType.setName( "IdentifierType" + uniqueChar );
        identifierType.setDescription( "IdentifierType" + uniqueChar );
        identifierType.setType( PatientIdentifierType.VALUE_TYPE_TEXT );

        return identifierType;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return ValidationCriteria
     */
    public static ValidationCriteria createValidationCriteria( char uniqueCharacter, String property, int operator,
        String value )
    {
        ValidationCriteria validationCriteria = new ValidationCriteria();

        validationCriteria.setName( "ValidationCriteria" + uniqueCharacter );
        validationCriteria.setDescription( "Description" + uniqueCharacter );
        validationCriteria.setProperty( property );
        validationCriteria.setOperator( operator );
        validationCriteria.setValue( value );

        return validationCriteria;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return RelationshipType
     */
    public static RelationshipType createRelationshipType( char uniqueChar )
    {
        RelationshipType relationshipType = new RelationshipType();

        relationshipType.setaIsToB( "aIsToB" );
        relationshipType.setbIsToA( "bIsToA" );
        relationshipType.setName( "RelationshipType" + uniqueChar );

        return relationshipType;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param sql A query statement to retreive record/data from database.
     * @return a sqlView instance
     */
    protected static SqlView createSqlView( char uniqueCharacter, String sql )
    {
        SqlView sqlView = new SqlView();

        sqlView.setName( "SqlView" + uniqueCharacter );
        sqlView.setDescription( "Description" + uniqueCharacter );
        sqlView.setSqlQuery( sql );

        return sqlView;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return a concept instance
     */
    protected static Concept createConcept( char uniqueCharacter )
    {
        Concept concept = new Concept();

        concept.setName( "Concept" + uniqueCharacter );

        return concept;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param value The value for constant
     * @return a constant instance
     */
    protected static Constant createConstant( char uniqueCharacter, double value )
    {
        Constant constant = new Constant();

        constant.setName( "Constant" + uniqueCharacter );
        constant.setValue( value );

        return constant;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Injects the externalDir property of LocationManager to
     * user.home/dhis2_test_dir. LocationManager dependency must be retrieved
     * from the context up front.
     * 
     * @param locationManager The LocationManager to be injected with the
     *        external directory.
     */
    public void setExternalTestDir( LocationManager locationManager )
    {
        setDependency( locationManager, "externalDir", EXT_TEST_DIR, String.class );
    }

    /**
     * Attempts to remove the external test directory.
     */
    public void removeExternalTestDir()
    {
        deleteDir( new File( EXT_TEST_DIR ) );
    }

    private boolean deleteDir( File dir )
    {
        if ( dir.isDirectory() )
        {
            String[] children = dir.list();

            for ( int i = 0; i < children.length; i++ )
            {
                boolean success = deleteDir( new File( dir, children[i] ) );

                if ( !success )
                {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    // -------------------------------------------------------------------------
    // Allow xpath testing of DXF2
    // -------------------------------------------------------------------------

    protected String xpathTest( String xpathString, String xml )
        throws XPathExpressionException
    {
        InputSource source = new InputSource( new StringReader( xml ) );
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( new Dxf2NamespaceResolver() );

        return xpath.evaluate( xpathString, source );
    }

    protected class Dxf2NamespaceResolver
        implements NamespaceContext
    {
        @Override
        public String getNamespaceURI( String prefix )
        {
            if ( prefix == null )
            {
                throw new IllegalArgumentException( "No prefix provided!" );
            }
            else
            {
                if ( prefix.equals( "d" ) )
                {
                    return "http://dhis2.org/schema/dxf/2.0";
                }
                else
                {
                    return XMLConstants.NULL_NS_URI;
                }
            }
        }

        @Override
        public String getPrefix( String namespaceURI )
        {
            return null;
        }

        @Override
        public Iterator<?> getPrefixes( String namespaceURI )
        {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Allow xpath testing of DXF2
    // -------------------------------------------------------------------------

    /**
     * Creates a user and injects into the security context with username 
     * "username". Requires <code>identifiableObjectManager</code> and 
     * <code>userService</code> to be injected into the test.
     * 
     * @param allAuth whether to grant ALL authority to user.
     * @param auths authorities to grant to user.
     * @return the user.
     */
    public User createUserAndInjectSecurityContext( boolean allAuth, String... auths )
    {
        return createUserAndInjectSecurityContext( null, allAuth, auths );
    }
    
    /**
     * Creates a user and injects into the security context with username 
     * "username". Requires <code>identifiableObjectManager</code> and 
     * <code>userService</code> to be injected into the test.
     * 
     * @param organisationUnits the organisation units of the user.
     * @param allAuth whether to grant the ALL authority to user.
     * @param auths authorities to grant to user.
     * @return the user.
     */
    public User createUserAndInjectSecurityContext( Set<OrganisationUnit> organisationUnits, boolean allAuth, String... auths )
    {
        Assert.notNull( identifiableObjectManager, "IdentifiableObjectManager must be injected in test" );
        Assert.notNull( userService, "UserService must be injected in test" );
        
        UserAuthorityGroup userAuthorityGroup = new UserAuthorityGroup();
        userAuthorityGroup.setName( "Superuser" );
        
        if ( allAuth )
        {
            userAuthorityGroup.getAuthorities().add( "ALL" );
        }
        
        if ( auths != null )
        {
            for ( String auth : auths )
            {
                userAuthorityGroup.getAuthorities().add( auth );
            }
        }
        
        identifiableObjectManager.save( userAuthorityGroup );

        User user = createUser( 'A' );
        
        if ( organisationUnits != null )
        {
            user.setOrganisationUnits( organisationUnits );
        }
        
        user.getUserCredentials().getUserAuthorityGroups().add( userAuthorityGroup );
        userService.addUser( user );
        user.getUserCredentials().setUser( user );
        userService.addUserCredentials( user.getUserCredentials() );

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( new SimpleGrantedAuthority( "ALL" ) );

        UserDetails userDetails = new org.springframework.security.core.userdetails.User( "username", "password", authorities );

        Authentication authentication = new UsernamePasswordAuthenticationToken( userDetails, "", authorities );
        SecurityContextHolder.getContext().setAuthentication( authentication );

        return user;
    }
}
