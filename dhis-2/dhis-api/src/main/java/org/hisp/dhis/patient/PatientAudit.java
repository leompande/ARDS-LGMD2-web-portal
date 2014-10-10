package org.hisp.dhis.patient;

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

import java.util.Date;

/**
 * @author Chau Thu Tran
 * 
 * @version PatientAudit.java 8:56:01 AM Sep 26, 2012 $
 */
public class PatientAudit
{
    public static final String MODULE_PATIENT_DASHBOARD = "patient_dashboard";

    public static final String MODULE_TABULAR_REPORT = "tabular_report";

    private int id;

    private Patient patient;

    private String visitor;

    private Date date;

    private String accessedModule;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public PatientAudit()
    {

    }

    public PatientAudit( Patient patient, String visitor, Date date, String accessedModule )
    {
        this.patient = patient;
        this.visitor = visitor;
        this.date = date;
        this.accessedModule = accessedModule;
    }

    //TODO implement hashCode and equals
    
    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate( Date date )
    {
        this.date = date;
    }

    public String getVisitor()
    {
        return visitor;
    }

    public void setVisitor( String visitor )
    {
        this.visitor = visitor;
    }

    public String getAccessedModule()
    {
        return accessedModule;
    }

    public void setAccessedModule( String accessedModule )
    {
        this.accessedModule = accessedModule;
    }
}
