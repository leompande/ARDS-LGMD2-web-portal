package org.hisp.dhis.concept;

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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public abstract class ConceptTest
    extends DhisSpringTest
{
    protected ConceptService conceptService;

    protected DataElementCategoryOption categoryOptionA;

    protected DataElementCategoryOption categoryOptionB;

    protected DataElementCategoryOption categoryOptionC;

    protected DataElementCategory defaultCategory;
    
    protected DataElementCategory categoryA;

    protected DataElementCategory categoryB;

    protected DataElementCategory categoryC;

    protected Concept defaultConcept;

    protected List<DataElementCategoryOption> categoryOptions;

    public void setUpConceptTest()
        throws Exception
    {
        conceptService = (ConceptService) getBean( ConceptService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        // ---------------------------------------------------------------------
        // Setup Default Concept
        // ---------------------------------------------------------------------

        defaultConcept = conceptService.getConceptByName( Concept.DEFAULT_CONCEPT_NAME );

        // ---------------------------------------------------------------------
        // Setup Category Option
        // ---------------------------------------------------------------------

        categoryOptionA = new DataElementCategoryOption( "CategoryOptionA" );
        categoryOptionB = new DataElementCategoryOption( "CategoryOptionB" );
        categoryOptionC = new DataElementCategoryOption( "CategoryOptionC" );

        categoryService.addDataElementCategoryOption( categoryOptionA );
        categoryService.addDataElementCategoryOption( categoryOptionB );
        categoryService.addDataElementCategoryOption( categoryOptionC );

        categoryOptions = new ArrayList<DataElementCategoryOption>();

        categoryOptions.add( categoryOptionA );
        categoryOptions.add( categoryOptionB );
        categoryOptions.add( categoryOptionC );

        // ---------------------------------------------------------------------
        // Setup Category
        // ---------------------------------------------------------------------

        defaultCategory = categoryService.getDataElementCategoryByName( DataElementCategory.DEFAULT_NAME );
        
        categoryA = new DataElementCategory( "CategoryA", defaultConcept, categoryOptions );
        categoryB = new DataElementCategory( "CategoryB", defaultConcept, categoryOptions );
        categoryC = new DataElementCategory( "CategoryC", defaultConcept, categoryOptions );

        categoryService.addDataElementCategory( categoryA );
        categoryService.addDataElementCategory( categoryB );
        categoryService.addDataElementCategory( categoryC );

    }
}
