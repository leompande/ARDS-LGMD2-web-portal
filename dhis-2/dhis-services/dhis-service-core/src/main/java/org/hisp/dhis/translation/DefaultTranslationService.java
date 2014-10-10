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

import java.util.Collection;
import java.util.Locale;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public class DefaultTranslationService
    implements TranslationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TranslationStore translationStore;

    public void setTranslationStore( TranslationStore translationStore )
    {
        this.translationStore = translationStore;
    }

    // -------------------------------------------------------------------------
    // Translation
    // -------------------------------------------------------------------------

    public void addTranslation( Translation translation )
    {
        translationStore.addTranslation( translation );
    }

    public void updateTranslation( Translation translation )
    {
        translationStore.updateTranslation( translation );
    }

    public Translation getTranslation( String className, int id, Locale locale, String property )
    {
        return translationStore.getTranslation( className, id, locale, property );
    }

    public Translation getTranslationNoFallback( String className, int id, Locale locale, String property )
    {
        return translationStore.getTranslationNoFallback( className, id, locale, property );
    }

    public Collection<Translation> getTranslations( String className, int id, Locale locale )
    {
        return translationStore.getTranslations( className, id, locale );
    }

    public Collection<Translation> getTranslationsNoFallback( String className, int id, Locale locale )
    {
        return translationStore.getTranslationsNoFallback( className, id, locale );
    }

    public Collection<Translation> getTranslations( String className, Locale locale )
    {
        return translationStore.getTranslations( className, locale );
    }

    public Collection<Translation> getTranslations( Locale locale )
    {
        return translationStore.getTranslations( locale );
    }

    public Collection<Translation> getAllTranslations()
    {
        return translationStore.getAllTranslations();
    }

    public void deleteTranslation( Translation translation )
    {
        translationStore.deleteTranslation( translation );
    }

    public void deleteTranslations( String className, int id )
    {
        translationStore.deleteTranslations( className, id );
    }
}
