package org.hisp.dhis.minmax;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.junit.Test;

/**
 * @author Kristian Nordal
 * @version $Id: MinMaxDataElementStoreTest.java 5012 2008-04-24 21:14:40Z larshelg $
 */
public class MinMaxDataElementStoreTest
    extends DhisSpringTest
{
    private MinMaxDataElementStore minMaxDataElementStore;

    @Override
    public void setUpTest()
        throws Exception
    {
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        minMaxDataElementStore = (MinMaxDataElementStore) getBean( MinMaxDataElementStore.ID );
    }

    @Test
    public void testBasic()
        throws Exception
    {
        OrganisationUnit source1 = createOrganisationUnit( 'A' );
        OrganisationUnit source2 = createOrganisationUnit( 'B' );

        organisationUnitService.addOrganisationUnit( source1 );
        organisationUnitService.addOrganisationUnit( source2 );

        DataElement dataElement1 = new DataElement();
        dataElement1.setName( "DE1name" );
        dataElement1.setShortName( "DE1sname" );
        dataElement1.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement1.setType( DataElement.VALUE_TYPE_INT );
        dataElement1.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        
        DataElement dataElement2 = new DataElement();
        dataElement2.setName( "DE2name" );
        dataElement2.setShortName( "DE2sname" );
        dataElement2.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement2.setType( DataElement.VALUE_TYPE_INT );
        dataElement2.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );

        DataElement dataElement3 = new DataElement();
        dataElement3.setName( "DE3name" );
        dataElement3.setShortName( "DE3sname" );
        dataElement3.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement3.setType( DataElement.VALUE_TYPE_INT );
        dataElement3.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        
        DataElement dataElement4 = new DataElement();
        dataElement4.setName( "DE4name" );
        dataElement4.setShortName( "DE4sname" );
        dataElement4.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement4.setType( DataElement.VALUE_TYPE_INT );
        dataElement4.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        
        dataElementService.addDataElement( dataElement1 );
        dataElementService.addDataElement( dataElement2 );
        dataElementService.addDataElement( dataElement3 );
        dataElementService.addDataElement( dataElement4 );
        
        
        DataElementCategoryOptionCombo optionCombo1 = new DataElementCategoryOptionCombo();        
        categoryService.addDataElementCategoryOptionCombo( optionCombo1 );
        
        DataElementCategoryOptionCombo optionCombo2 = new DataElementCategoryOptionCombo();        
        categoryService.addDataElementCategoryOptionCombo( optionCombo2 );

        MinMaxDataElement minMaxDataElement1 = new MinMaxDataElement( source1, dataElement1, optionCombo1, 0, 100, false );
        MinMaxDataElement minMaxDataElement2 = new MinMaxDataElement( source2, dataElement2, optionCombo1, 0, 100, false );
        MinMaxDataElement minMaxDataElement3 = new MinMaxDataElement( source2, dataElement3, optionCombo1, 0, 100, false );
        MinMaxDataElement minMaxDataElement4 = new MinMaxDataElement( source2, dataElement4, optionCombo1, 0, 100, false );
        
        MinMaxDataElement minMaxDataElement5 = new MinMaxDataElement( source1, dataElement1, optionCombo2, 0, 100, false );

        int mmdeid1 = minMaxDataElementStore.save( minMaxDataElement1 );
        minMaxDataElementStore.save( minMaxDataElement2 );
        minMaxDataElementStore.save( minMaxDataElement3 );
        minMaxDataElementStore.save( minMaxDataElement4 );
        minMaxDataElementStore.save( minMaxDataElement5 );

        // ----------------------------------------------------------------------
        // Assertions
        // ----------------------------------------------------------------------

        assertNotNull( minMaxDataElementStore.get( mmdeid1 ) );

        assertTrue( minMaxDataElementStore.get( mmdeid1 ).getMax() == 100 );

        minMaxDataElement1.setMax( 200 );
        minMaxDataElementStore.update( minMaxDataElement1 );
        assertTrue( minMaxDataElementStore.get( mmdeid1 ).getMax() == 200 );

        Collection<DataElement> dataElements1 = new HashSet<DataElement>();
        dataElements1.add( dataElement1 );

        Collection<DataElement> dataElements2 = new HashSet<DataElement>();
        dataElements2.add( dataElement2 );
        dataElements2.add( dataElement3 );
        dataElements2.add( dataElement4 );

        assertNotNull( minMaxDataElementStore.get( source1, dataElement1, optionCombo1 ) );
        assertNull( minMaxDataElementStore.get( source2, dataElement1, optionCombo1 ) );

        assertTrue( minMaxDataElementStore.get( source1, dataElements1 ).size() == 2 );
        assertTrue( minMaxDataElementStore.get( source2, dataElements2 ).size() == 3 );       

        minMaxDataElementStore.delete( minMaxDataElement1 );

        assertNull( minMaxDataElementStore.get( mmdeid1 ) );
    }
}
