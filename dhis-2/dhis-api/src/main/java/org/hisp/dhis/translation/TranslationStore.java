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

import java.util.Locale;
import java.util.Collection;

/**
 * @author Oyvind Brucker
 */
public interface TranslationStore
{
    String ID = TranslationStore.class.getName();

    /**
     * Adds a Translation.
     * 
     * @param translation the Translation.
     */
    void addTranslation( Translation translation );

    /**
     * Updates a Translation.
     * 
     * @param translation the Translation.
     */
    void updateTranslation( Translation translation );

    /**
     * Retrieves a Translation.
     * 
     * @param className the class name.
     * @param id the id.
     * @param locale the locale.
     * @param property the property.
     * @return a Translation.
     */
    Translation getTranslation( String className, int id, Locale locale, String property );

    /**
     * Retrieves a Translation. Only exact matches on the given
     * Locale will be returned.
     * 
     * @param className the class name.
     * @param id the id.
     * @param locale the locale.
     * @param property the property.
     * @return a Translation.
     */
    Translation getTranslationNoFallback( String className, int id, Locale locale, String property  );

    /**
     * Retrieves a Collection of Translations.
     * 
     * @param className the class name.
     * @param id the id.
     * @param locale the locale.
     * @return a Collection of Translations.
     */
    Collection<Translation> getTranslations( String className, int id, Locale locale );

    /**
     * Retrieves a Collection of Translations. Only exact matches on the given
     * Locale will be returned.
     * 
     * @param className the class name.
     * @param id the id.
     * @param locale the locale.
     * @return a Collection of Translations.
     */
    Collection<Translation> getTranslationsNoFallback( String className, int id, Locale locale );
    
    /**
     * Retrieves a Collection of Translations.
     * 
     * @param className the class name.
     * @param locale the locale.
     * @return a Collection of Translations.
     */
    Collection<Translation> getTranslations( String className, Locale locale );

    /**
     * Retrieves a Collection of Translations.
     * 
     * @param locale the locale.
     * @return a Collection of Translations.
     */
    Collection<Translation> getTranslations( Locale locale );

    /**
     * Retrieves a Collection of all Translations.
     * 
     * @return a Collection of all Translations.
     */
    Collection<Translation> getAllTranslations();

    /**
     * Deletes a Translation.
     * 
     * @param translation the Translation.
     */
    void deleteTranslation( Translation translation );

    /**
     * Deletes Translations.
     * 
     * @param className the class name.
     * @param id the id.
     */
    void deleteTranslations( String className, int id );
}