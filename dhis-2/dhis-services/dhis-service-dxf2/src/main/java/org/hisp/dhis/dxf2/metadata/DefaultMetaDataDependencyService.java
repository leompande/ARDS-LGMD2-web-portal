package org.hisp.dhis.dxf2.metadata;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.proxy.HibernateProxy;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.validation.ValidationRule;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
public class DefaultMetaDataDependencyService
    implements MetaDataDependencyService
{
    private static final Log log = LogFactory.getLog( DefaultMetaDataDependencyService.class );

    private final Class[] specialCases = new Class[]{ DataElement.class, Indicator.class, OrganisationUnit.class, ValidationRule.class };

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    //--------------------------------------------------------------------------
    // Get MetaData dependency Map
    //--------------------------------------------------------------------------

    public Map<String, List<IdentifiableObject>> getIdentifiableObjectMap( Map<String, Object> identifiableObjectUidMap )
    {
        Map<String, List<IdentifiableObject>> identifiableObjectMap = new HashMap<String, List<IdentifiableObject>>();

        for ( Map.Entry<String, Object> identifiableObjectUidEntry : identifiableObjectUidMap.entrySet() )
        {
            String className = identifiableObjectUidEntry.getKey();

            for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
            {
                if ( className.equals( (entry.getValue() + "_all") ) )
                {
                    Boolean o = (Boolean) identifiableObjectUidMap.get( className );

                    if ( o != null && o )
                    {
                        Class<? extends IdentifiableObject> identifiableObjectClass = entry.getKey();
                        Collection<? extends IdentifiableObject> identifiableObjects = manager.getAll( identifiableObjectClass );
                        identifiableObjectMap.put( entry.getValue(), new ArrayList<IdentifiableObject>( identifiableObjects ) );
                    }
                }
                else if ( entry.getValue().equals( className ) )
                {
                    Class<? extends IdentifiableObject> identifiableObjectClass = entry.getKey();
                    Collection<? extends IdentifiableObject> identifiableObjects = manager.getByUid( identifiableObjectClass, (Collection<String>) identifiableObjectUidEntry.getValue() );

                    identifiableObjectMap.put( entry.getValue(), new ArrayList<IdentifiableObject>( identifiableObjects ) );
                }
            }
        }

        return identifiableObjectMap;
    }

    public Map<String, List<IdentifiableObject>> getIdentifiableObjectWithDependencyMap( Map<String, Object> identifiableObjectUidMap )
    {
        Map<String, List<IdentifiableObject>> identifiableObjectMap = getIdentifiableObjectMap( identifiableObjectUidMap );
        Collection<IdentifiableObject> identifiableObjects = new HashSet<IdentifiableObject>();

        for ( Map.Entry<String, List<IdentifiableObject>> identifiableObjectEntry : identifiableObjectMap.entrySet() )
        {
            identifiableObjects.addAll( identifiableObjectEntry.getValue() );
        }

        Set<IdentifiableObject> dependencySet = getDependencySet( identifiableObjects );

        for ( IdentifiableObject dependency : dependencySet )
        {
            for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
            {
                if ( entry.getKey().equals( dependency.getClass() ) )
                {
                    if ( identifiableObjectMap.get( entry.getValue() ) != null )
                    {
                        identifiableObjectMap.get( entry.getValue() ).add( dependency );
                    }
                    else
                    {
                        List<IdentifiableObject> idObjects = new ArrayList<IdentifiableObject>();
                        idObjects.add( dependency );

                        identifiableObjectMap.put( entry.getValue(), idObjects );
                    }
                }
            }
        }

        return identifiableObjectMap;
    }

    //--------------------------------------------------------------------------
    // Get MetaData dependency Set
    //--------------------------------------------------------------------------

    @Override
    public Set<IdentifiableObject> getDependencySet( IdentifiableObject identifiableObject )
    {
        Set<IdentifiableObject> dependencySet = new HashSet<IdentifiableObject>();
        dependencySet.addAll( computeAllDependencies( identifiableObject ) );

        if ( isSpecialCase( identifiableObject ) )
        {
            dependencySet.addAll( computeSpecialDependencyCase( identifiableObject ) );
        }

        return dependencySet;
    }

    @Override
    public Set<IdentifiableObject> getDependencySet( Collection<? extends IdentifiableObject> identifiableObjects )
    {
        Set<IdentifiableObject> dependencySet = new HashSet<IdentifiableObject>();

        for ( IdentifiableObject identifiableObject : identifiableObjects )
        {
            dependencySet.addAll( getDependencySet( identifiableObject ) );
        }

        return dependencySet;
    }

    //--------------------------------------------------------------------------
    // Compute dependencies
    //--------------------------------------------------------------------------

    private List<IdentifiableObject> computeAllDependencies( IdentifiableObject identifiableObject )
    {
        List<IdentifiableObject> finalDependencies = new ArrayList<IdentifiableObject>();
        List<IdentifiableObject> dependencies = getDependencies( identifiableObject );

        if ( dependencies.isEmpty() )
        {
            return finalDependencies;
        }
        else
        {
            for ( IdentifiableObject dependency : dependencies )
            {
                log.debug( "[ COMPUTING DEPENDENCY ] : " + dependency.getName() );

                finalDependencies.add( dependency );

                List<IdentifiableObject> computedDependencies = computeAllDependencies( dependency );
                finalDependencies.addAll( computedDependencies );
            }

            return finalDependencies;
        }
    }

    private List<IdentifiableObject> getDependencies( IdentifiableObject identifiableObject )
    {
        List<IdentifiableObject> dependencies = new ArrayList<IdentifiableObject>();
        List<Field> fields = ReflectionUtils.getAllFields( identifiableObject.getClass() );

        for ( Field field : fields )
        {
            for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
            {
                if ( ReflectionUtils.isType( field, entry.getKey() ) )
                {
                    Method getterMethod = ReflectionUtils.findGetterMethod( field.getName(), identifiableObject );
                    IdentifiableObject dependencyObject = ReflectionUtils.invokeGetterMethod( field.getName(), identifiableObject );

                    if ( dependencyObject != null && isExportView( getterMethod ) )
                    {
                        log.debug( "[ DEPENDENCY OBJECT ] : " + dependencyObject.getName() );

                        if ( dependencyObject instanceof HibernateProxy )
                        {
                            Object hibernateProxyObject = ((HibernateProxy) dependencyObject).getHibernateLazyInitializer().getImplementation();
                            IdentifiableObject deProxyDependencyObject = (IdentifiableObject) hibernateProxyObject;

                            dependencies.add( deProxyDependencyObject );
                        }
                        else
                        {
                            dependencies.add( dependencyObject );
                        }
                    }
                }
                else if ( ReflectionUtils.isCollection( field.getName(), identifiableObject, entry.getKey() ) )
                {
                    Method getterMethod = ReflectionUtils.findGetterMethod( field.getName(), identifiableObject );
                    Collection<IdentifiableObject> dependencyCollection = ReflectionUtils.invokeGetterMethod( field.getName(), identifiableObject );

                    if ( dependencyCollection != null && isExportView( getterMethod ) )
                    {
                        for ( IdentifiableObject dependencyElement : dependencyCollection )
                        {
                            log.debug( "[ DEPENDENCY COLLECTION ELEMENT ] : " + dependencyElement.getName() );

                            if ( dependencyElement instanceof HibernateProxy )
                            {
                                Object hibernateProxyObject = ((HibernateProxy) dependencyElement).getHibernateLazyInitializer().getImplementation();
                                IdentifiableObject deProxyDependencyObject = (IdentifiableObject) hibernateProxyObject;

                                dependencies.add( deProxyDependencyObject );
                            }
                            else
                            {
                                dependencies.add( dependencyElement );
                            }
                        }
                    }
                }
            }
        }

        return dependencies;
    }

    //--------------------------------------------------------------------------
    // Compute special case dependencies
    //--------------------------------------------------------------------------

    private boolean isSpecialCase( IdentifiableObject identifiableObject )
    {
        for ( Class specialCase : specialCases )
        {
            if ( identifiableObject.getClass().equals( specialCase ) )
            {
                return true;
            }
        }

        return false;
    }

    private Set<IdentifiableObject> computeSpecialDependencyCase( IdentifiableObject identifiableObject )
    {
        Set<IdentifiableObject> resultSet = new HashSet<IdentifiableObject>();

        if ( identifiableObject instanceof Indicator )
        {
            List<Indicator> indicators = new ArrayList<Indicator>();
            indicators.add( (Indicator) identifiableObject );

            Set<DataElement> dataElementSet = expressionService.getDataElementsInIndicators( indicators );

            resultSet.addAll( dataElementSet );
            resultSet.addAll( getDependencySet( dataElementSet ) );

            Set<Constant> constantSet = new HashSet<Constant>();

            List<String> expressions = new ArrayList<String>();
            Collections.addAll( expressions, ((Indicator) identifiableObject).getNumerator(), ((Indicator) identifiableObject).getDenominator() );

            for ( String expression : expressions )
            {
                Matcher matcher = ExpressionService.CONSTANT_PATTERN.matcher( expression );
                while ( matcher.find() )
                {
                    String co = matcher.group( 1 );
                    constantSet.add( constantService.getConstant( co ) );
                }
            }

            resultSet.addAll( constantSet );
            resultSet.addAll( getDependencySet( constantSet ) );

            return resultSet;
        }
        else if ( identifiableObject instanceof ValidationRule )
        {
            Set<DataElement> dataElementSet = new HashSet<DataElement>();

            Expression leftSide = ReflectionUtils.invokeGetterMethod( "leftSide", identifiableObject );
            Expression rightSide = ReflectionUtils.invokeGetterMethod( "rightSide", identifiableObject );

            dataElementSet.addAll( expressionService.getDataElementsInExpression( leftSide.getExpression() ) );
            dataElementSet.addAll( expressionService.getDataElementsInExpression( rightSide.getExpression() ) );

            resultSet.addAll( dataElementSet );
            resultSet.addAll( getDependencySet( dataElementSet ) );

            Set<Constant> constantSet = new HashSet<Constant>();
            constantSet.addAll( constantService.getAllConstants() );

            resultSet.addAll( constantSet );
            resultSet.addAll( getDependencySet( constantSet ) );

            return resultSet;
        }
        else if ( identifiableObject instanceof DataElement )
        {
            Set<DataElementCategoryOptionCombo> dataElementCategoryOptionComboSet = new HashSet<DataElementCategoryOptionCombo>();
            dataElementCategoryOptionComboSet.addAll( ((DataElement) identifiableObject).getCategoryCombo().getOptionCombos() );

            resultSet.addAll( dataElementCategoryOptionComboSet );
            resultSet.addAll( getDependencySet( dataElementCategoryOptionComboSet ) );

            return resultSet;
        }
        else if ( identifiableObject instanceof OrganisationUnit )
        {
            Set<OrganisationUnitLevel> organisationUnitLevelSet = new HashSet<OrganisationUnitLevel>();
            int level = ((OrganisationUnit) identifiableObject).getOrganisationUnitLevel();

            while ( level > 0 )
            {
                organisationUnitLevelSet.add( organisationUnitService.getOrganisationUnitLevelByLevel( level ) );
                level--;
            }

            resultSet.addAll( organisationUnitLevelSet );
            resultSet.addAll( getDependencySet( organisationUnitLevelSet ) );

            return resultSet;
        }
        else
        {
            return resultSet;
        }
    }

    //--------------------------------------------------------------------------
    // Utils
    //--------------------------------------------------------------------------

    public boolean isExportView( Method method )
    {
        if ( method.isAnnotationPresent( JsonView.class ) )
        {
            Class[] viewClasses = method.getAnnotation( JsonView.class ).value();

            for ( Class viewClass : viewClasses )
            {
                if ( viewClass.equals( ExportView.class ) )
                {
                    return true;
                }
            }
        }

        return false;
    }
}
