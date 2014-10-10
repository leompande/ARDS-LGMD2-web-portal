package org.hisp.dhis.i18n.action;

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
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.i18n.I18nService;

import com.opensymphony.xwork2.Action;

/**
 * @author Oyvind Brucker
 * @version $Id$
 * @modifier Dang Duy Hieu
 * @since 2010-03-24
 */
public class I18nAction
    implements Action
{
    private String className;

    private Integer objectId;

    private String returnUrl;

    private String message;

    private Locale currentLocale;
    
    private List<Locale> availableLocales = new ArrayList<Locale>();
    
    private Map<String, String> translations = new Hashtable<String, String>();

    private Map<String, String> referenceTranslations = new Hashtable<String, String>();
    
    private List<String> propertyNames = new ArrayList<String>();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nService i18nService;

    public void setI18nService( I18nService i18nService )
    {
        this.i18nService = i18nService;
    }
    
    private IdentifiableObjectManager identifiableObjectManager;

    public void setIdentifiableObjectManager( IdentifiableObjectManager identifiableObjectManager )
    {
        this.identifiableObjectManager = identifiableObjectManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    public void setClassName( String className )
    {
        this.className = className;
    }

    public void setObjectId( Integer objectId )
    {
        this.objectId = objectId;
    }

    public void setReturnUrl( String returnUrl )
    {
        this.returnUrl = returnUrl;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public String getClassName()
    {
        return className;
    }

    public Integer getObjectId()
    {
        return objectId;
    }

    public String getReturnUrl()
    {
        return returnUrl;
    }

    public String getMessage()
    {
        return message;
    }

    public Locale getCurrentLocale()
    {
        return currentLocale;
    }

    public List<Locale> getAvailableLocales()
    {
        return availableLocales;
    }

    public Map<String, String> getReferenceTranslations()
    {
        return referenceTranslations;
    }

    public Map<String, String> getTranslations()
    {
        return translations;
    }

    public List<String> getPropertyNames()
    {
        return propertyNames;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        currentLocale = i18nService.getCurrentLocale();
        
        availableLocales = i18nService.getAvailableLocales();
        
        translations = i18nService.getTranslationsNoFallback( className, objectId );

        IdentifiableObject object = identifiableObjectManager.getObject( objectId, className );

        referenceTranslations = i18nService.getObjectPropertyValues( object );

        propertyNames = i18nService.getObjectPropertyNames( object );

        return SUCCESS;
    }
}
