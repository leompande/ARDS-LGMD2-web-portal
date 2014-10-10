package org.hisp.dhis.hrentry.state;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.hr.Attribute;

/**
 * @author Ismail Koleleni
 */
public interface SelectedStateManager
{
    final String CUSTOM_FORM = "customform";
    final String SECTION_FORM = "sectionform";
    final String DEFAULT_FORM = "defaultform";
    
    final List<String> ALLOWED_FORM_TYPES = Arrays.asList( CUSTOM_FORM, SECTION_FORM, DEFAULT_FORM );

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------
    
    OrganisationUnit getSelectedOrganisationUnit();  

    void clearSelectedOrganisationUnits();
    
    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------
    
    void setSelectedHrDataSet( HrDataSet hrDataSet );

    HrDataSet getSelectedHrDataSet();

    void clearSelectedHrDataSet();
    
    Collection<Attribute> getAttribute();    

    // -------------------------------------------------------------------------
    // Attribute
    // -------------------------------------------------------------------------
    
    void setSelectedAttribute( Integer index );

    Integer getSelectedAttributeIndex();

    Attribute getSelectedAttribute();

    void clearSelectedAttribute();

    List<Attribute> getAttributeList();

    //void nextPeriodSpan();

    //void previousPeriodSpan();
    
    Attribute reloadAttribute();

    // -------------------------------------------------------------------------
    // DisplayMode
    // -------------------------------------------------------------------------
    
    void setSelectedDisplayMode( String displayMode );
    
    String getSelectedDisplayMode();
    
    void clearSelectedDisplayMode();
    
    boolean displayModeIsValid( String displayMode );
    
    String getDisplayMode();
}
