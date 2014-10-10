/*
 * Copyright (c) 2004-2007, University of Oslo
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
package org.hisp.dhis.mobile.api;


import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class MobileImportParameters implements Serializable
{

    public static final String FORM_TYPE_ANMREGFORM = "anmregform";
    public static final String FORM_TYPE_ANMQUERYFORM = "anmqueryform";
    public static final String FORM_TYPE_DATAFORM = "dataform";
    
    public static final String ANMREG_FORM_ID = "ANMRF";
    public static final String ANMQUERY_FORM_ID = "ANMQUERY";
    
    
    private String formType;
    
    private String mobileNumber;

    private String startDate;

    private String smsTime;

    private String periodType;

    private Map<String, String> dataValues;

    private String anmName;
    
    private String anmQuery;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public MobileImportParameters()
    {
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    public String getMobileNumber()
    {
        return mobileNumber;
    }

    public void setMobileNumber( String mobileNumber )
    {
        this.mobileNumber = mobileNumber;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public String getSmsTime()
    {
        return smsTime;
    }

    public void setSmsTime( String smsTime )
    {
        this.smsTime = smsTime;
    }

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    public String getPeriodType()
    {
        return periodType;
    }

    public Map<String, String> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( Map<String, String> dataValues )
    {
        this.dataValues = dataValues;
    }

    public String getFormType()
    {
        return formType;
    }

    public void setFormType( String formType )
    {
        this.formType = formType;
    }

    public String getAnmName()
    {
        return anmName;
    }

    public void setAnmName( String anmName )
    {
        this.anmName = anmName;
    }

    public String getAnmQuery()
    {
        return anmQuery;
    }

    public void setAnmQuery( String anmQuery )
    {
        this.anmQuery = anmQuery;
    }
    
    
    
}
