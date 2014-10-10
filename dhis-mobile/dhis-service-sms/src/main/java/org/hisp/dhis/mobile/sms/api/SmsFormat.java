package org.hisp.dhis.mobile.sms.api;

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
/**
 *
 * @author Saptarshi
 */
public class SmsFormat
{

    String version;

    String formId;

    String periodTypeId;

    String periodText;

    String[] dataValues;

    public SmsFormat( SmsInbound sms )
    {
        String info = sms.getText();
        String[] text = info.split( "#" );
        version = text[0];
        text = text[1].split( "\\*" );
        formId = text[0];
        text = text[1].split( "\\?" );
        periodTypeId = text[0];
        text = text[1].split( "\\$" );
        periodText = text[0];
        dataValues = text[1].split( "\\|", 1000 );
    }

    public String[] getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( String[] dataValues )
    {
        this.dataValues = dataValues;
    }

    public String getFormId()
    {
        return formId;
    }

    public void setFormId( String formId )
    {
        this.formId = formId;
    }

    public String getPeriodText()
    {
        return periodText;
    }

    public void setPeriodText( String periodText )
    {
        this.periodText = periodText;
    }

    public String getPeriodTypeId()
    {
        return periodTypeId;
    }

    public void setPeriodTypeId( String periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }
}
