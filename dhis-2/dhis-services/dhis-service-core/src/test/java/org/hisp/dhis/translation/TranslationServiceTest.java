package org.hisp.dhis.translation;

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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Ignore;

/**
 * @author Lars Helge Overland
 */
@Ignore
public class TranslationServiceTest
    extends DhisSpringTest
{
    private TranslationService translationService;

    // -------------------------------------------------------------------------
    // Testdata
    // -------------------------------------------------------------------------

    private int id1 = 0;
    private int id2 = 1;

    private String locale1 = Locale.UK.toString();
    private String locale2 = Locale.US.toString();
    private String locale3 = Locale.FRANCE.toString();

    private String className1 = OrganisationUnit.class.getName();
    private String className2 = DataElement.class.getName();

    private Translation translation1a = new Translation( className1, id1, locale1, "name", "cheers" );
    private Translation translation1b = new Translation( className1, id1, locale1, "shortName", "goodbye" );
    private Translation translation2a = new Translation( className1, id1, locale2, "name", "hello" );
    private Translation translation2b = new Translation( className2, id1, locale2, "name", "hey" );
    private Translation translation2c = new Translation( className2, id2, locale3, "name", "bonjour" );

    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        translationService = (TranslationService) getBean( TranslationService.ID );
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddGet()
    {
        translationService.addTranslation( translation1a );
        translationService.addTranslation( translation1b );
        
        assertEquals( translation1a, translationService.getTranslation( className1, id1, Locale.UK, "name" ) );
        assertEquals( translation1b, translationService.getTranslation( className1, id1, Locale.UK, "shortName" ) );
    }
    
    @Test
    public void delete()
    {
        translationService.addTranslation( translation1a );
        translationService.addTranslation( translation1b );
        
        assertNotNull( translationService.getTranslation( className1, id1, Locale.UK, "name" ) );
        assertNotNull( translationService.getTranslation( className1, id1, Locale.UK, "shortName" ) );
        
        translationService.deleteTranslation( translation1a );
        
        assertNull( translationService.getTranslation( className1, id1, Locale.UK, "name" ) );
        assertNotNull( translationService.getTranslation( className1, id1, Locale.UK, "shortName" ) );

        translationService.deleteTranslation( translation1b );

        assertNull( translationService.getTranslation( className1, id1, Locale.UK, "name" ) );
        assertNull( translationService.getTranslation( className1, id1, Locale.UK, "shortName" ) );
    }
    
    @Test
    public void testUpdateTranslation()
    {
        translationService.addTranslation( translation1a );
        
        assertEquals( translation1a, translationService.getTranslation( className1, id1, Locale.UK, "name" ) );
        
        translation1a.setValue( "regards" );
        
        translationService.updateTranslation( translation1a );

        assertEquals( "regards", translationService.getTranslation( className1, id1, Locale.UK, "name" ).getValue() );
    }

    @Ignore
    @Test
    public void testGetTranslations()
    {
        translationService.addTranslation( translation1a );
        translationService.addTranslation( translation1b );
        translationService.addTranslation( translation2a );
        translationService.addTranslation( translation2b );
        translationService.addTranslation( translation2c );
        
        assertEquals( 2, translationService.getTranslations( className1, Locale.UK ).size() );
        assertTrue( translationService.getTranslations( className1, id1, Locale.UK ).contains( translation1a ) );
        assertTrue( translationService.getTranslations( className1, id1, Locale.UK ).contains( translation1b ) );
    }
    
    @Test
    public void testGetAllTranslations()
    {
        translationService.addTranslation( translation1a );
        translationService.addTranslation( translation1b );
        translationService.addTranslation( translation2a );
        translationService.addTranslation( translation2b );
        translationService.addTranslation( translation2c );
        
        assertEquals( 5, translationService.getAllTranslations().size() );
    }
}
