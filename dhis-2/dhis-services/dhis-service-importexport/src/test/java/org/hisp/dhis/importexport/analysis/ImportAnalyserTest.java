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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ImportAnalyserTest
    extends DhisTest
{
    private ExpressionService expressionService;
    
    private ImportAnalyser analyser;
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        expressionService = (ExpressionService) getBean( ExpressionService.ID );
        
        analyser = new DefaultImportAnalyser( expressionService );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testUniqueConstraintViolations()
    {
        analyser.addObject( createDataElement( 'A' ) );
        analyser.addObject( createDataElement( 'B' ) );
        analyser.addObject( createDataElement( 'C' ) );
        analyser.addObject( createDataElement( 'A' ) );
        analyser.addObject( createDataElement( 'A' ) );
        analyser.addObject( null );

        analyser.addObject( createIndicator( 'A', new IndicatorType() ) );
        analyser.addObject( createIndicator( 'B', new IndicatorType() ) );
        analyser.addObject( createIndicator( 'A', new IndicatorType() ) );
        analyser.addObject( createIndicator( 'C', new IndicatorType() ) );
        analyser.addObject( createIndicator( 'A', new IndicatorType() ) );
        analyser.addObject( null );
        
        analyser.addObject( createOrganisationUnit( 'A' ) );
        analyser.addObject( createOrganisationUnit( 'B' ) );
        analyser.addObject( createOrganisationUnit( 'A' ) );
        analyser.addObject( null );
        analyser.addObject( null );
        analyser.addObject( null );
        
        ImportAnalysis analysis = analyser.getImportAnalysis();
        List<EntityPropertyValue> violations = analysis.getUniqueConstraintViolations();
        
        assertNotNull( violations );
        assertEquals( 9, violations.size() );
        
        assertTrue( violations.contains( new EntityPropertyValue( DataElement.class, "name", "DataElementA" ) ) );
        assertTrue( violations.contains( new EntityPropertyValue( DataElement.class, "shortname", "DataElementShortA" ) ) );
        assertTrue( violations.contains( new EntityPropertyValue( DataElement.class, "code", "DataElementCodeA" ) ) );
        
        assertTrue( violations.contains( new EntityPropertyValue( Indicator.class, "name", "IndicatorA" ) ) );
        assertTrue( violations.contains( new EntityPropertyValue( Indicator.class, "shortname", "IndicatorShortA" ) ) );
        assertTrue( violations.contains( new EntityPropertyValue( Indicator.class, "code", "IndicatorCodeA" ) ) );
        
        assertTrue( violations.contains( new EntityPropertyValue( OrganisationUnit.class, "name", "OrganisationUnitA" ) ) );
        assertTrue( violations.contains( new EntityPropertyValue( OrganisationUnit.class, "shortname", "OrganisationUnitShortA" ) ) );
        assertTrue( violations.contains( new EntityPropertyValue( OrganisationUnit.class, "code", "OrganisationUnitCodeA" ) ) );
    }
}
