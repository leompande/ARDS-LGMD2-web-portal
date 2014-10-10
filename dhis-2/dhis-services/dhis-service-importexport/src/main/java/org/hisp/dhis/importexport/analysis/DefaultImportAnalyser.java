package org.hisp.dhis.importexport.analysis;

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

import static org.hisp.dhis.importexport.analysis.IndicatorFormulaIdentifier.DENOMINATOR;
import static org.hisp.dhis.importexport.analysis.IndicatorFormulaIdentifier.NUMERATOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultImportAnalyser
    implements ImportAnalyser
{
    private static final Log log = LogFactory.getLog( DefaultImportAnalyser.class );
    
    private static final String PROP_NAME = "name";
    private static final String PROP_SHORT_NAME = "shortname";
    private static final String PROP_CODE = "code";
    private static final String PROP_PRIMARY_KEY = "primaryKey";
    
    private static final String SEPARARATOR = "-";

    private ExpressionService expressionService;

    public DefaultImportAnalyser( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    // -------------------------------------------------------------------------
    // This class is stateful and should have a prototype scope
    // -------------------------------------------------------------------------

    private List<EntityPropertyValue> values = new ArrayList<EntityPropertyValue>();
    
    private List<Integer> dataElementIdentifiers = new ArrayList<Integer>();
    
    private List<Indicator> indicators = new ArrayList<Indicator>();
    
    // -------------------------------------------------------------------------
    // ImportAnalyser implementation
    // -------------------------------------------------------------------------

    public void addObject( Object object )
    {
        if ( object instanceof DataValue )
        {
            final DataValue value = (DataValue) object;
            
            values.add( new EntityPropertyValue( DataValue.class, PROP_PRIMARY_KEY, 
                value.getDataElement().getId() + SEPARARATOR +
                value.getCategoryOptionCombo().getId() + SEPARARATOR +
                value.getPeriod().getId() + SEPARARATOR +
                value.getSource().getId() ) );
        }
        else if ( object instanceof DataElement )
        {
            values.add( new EntityPropertyValue( DataElement.class, PROP_NAME, ((DataElement)object).getName() ) );
            values.add( new EntityPropertyValue( DataElement.class, PROP_SHORT_NAME, ((DataElement)object).getShortName() ) );
            values.add( new EntityPropertyValue( DataElement.class, PROP_CODE, ((DataElement)object).getCode() ) );
            
            dataElementIdentifiers.add( ((DataElement)object).getId() );
        }
        else if ( object instanceof Indicator )
        {
            values.add( new EntityPropertyValue( Indicator.class, PROP_NAME, ((Indicator)object).getName() ) );
            values.add( new EntityPropertyValue( Indicator.class, PROP_SHORT_NAME, ((Indicator)object).getShortName() ) );
            values.add( new EntityPropertyValue( Indicator.class, PROP_CODE, ((Indicator)object).getCode() ) );
            
            indicators.add( (Indicator)object );
        }
        else if ( object instanceof DataSet )
        {
            values.add( new EntityPropertyValue( DataSet.class, PROP_NAME, ((DataSet)object).getName() ) );
            values.add( new EntityPropertyValue( DataSet.class, PROP_SHORT_NAME, ((DataSet)object).getShortName() ) );
            values.add( new EntityPropertyValue( DataSet.class, PROP_CODE, ((DataSet)object).getCode() ) );            
        }
        else if ( object instanceof OrganisationUnit )
        {
            values.add( new EntityPropertyValue( OrganisationUnit.class, PROP_NAME, ((OrganisationUnit)object).getName() ) );
            values.add( new EntityPropertyValue( OrganisationUnit.class, PROP_SHORT_NAME, ((OrganisationUnit)object).getShortName() ) );
            values.add( new EntityPropertyValue( OrganisationUnit.class, PROP_CODE, ((OrganisationUnit)object).getCode() ) );            
        }
    }
    
    public ImportAnalysis getImportAnalysis()
    {
        ImportAnalysis analysis = new ImportAnalysis();
        
        analysis.getUniqueConstraintViolations().addAll( getDuplicates() );
        analysis.getNonExistingDataElementIdentifiers().addAll( getNonExistingIdentifiersInIndicators() );

        return analysis;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Sorts out non-existing data element identifiers in indicator numerator and
     * denominators.
     */
    private List<IndicatorFormulaIdentifier> getNonExistingIdentifiersInIndicators()
    {
        List<IndicatorFormulaIdentifier> identifiers = new ArrayList<IndicatorFormulaIdentifier>();
        
        for ( Indicator indicator : indicators )
        {
            Set<DataElementOperand> operands = expressionService.getOperandsInExpression( indicator.getNumerator() );
            
            for ( DataElementOperand operand : operands )
            {
                if ( !dataElementIdentifiers.contains( operand.getDataElementId() ) )
                {
                    identifiers.add( new IndicatorFormulaIdentifier( indicator.getName(), NUMERATOR, operand.getDataElementId() ) );
                }
            }
            
            operands = expressionService.getOperandsInExpression( indicator.getDenominator() );
            
            for ( DataElementOperand operand : operands )
            {
                if ( !dataElementIdentifiers.contains( operand.getDataElementId() ) )
                {
                    identifiers.add( new IndicatorFormulaIdentifier( indicator.getName(), DENOMINATOR, operand.getDataElementId() ) );
                }
            }
        }

        log.info( "Found " + identifiers.size() + " non-existing data element identifiers after searching " + indicators.size() + " indicators" );
        
        return identifiers;
    }
    
    /**
     * Sorts out duplicate entries from the given list. Returns only one instance 
     * of a duplicate independent of how many times it occurred. Null-values are 
     * not recognized as duplicates.
     */
    private List<EntityPropertyValue> getDuplicates()    
    {
        Collections.sort( values );
        
        Set<EntityPropertyValue> duplicates = new HashSet<EntityPropertyValue>();
        
        EntityPropertyValue previous = null;
        
        for ( EntityPropertyValue value : values )
        {
            if ( value.equals( previous ) && value.getValue() != null )
            {
                duplicates.add( value );
            }
            
            previous = value;
        }
        
        List<EntityPropertyValue> list = new ArrayList<EntityPropertyValue>( duplicates );
        
        Collections.sort( list );
        
        return list;
    }
}
