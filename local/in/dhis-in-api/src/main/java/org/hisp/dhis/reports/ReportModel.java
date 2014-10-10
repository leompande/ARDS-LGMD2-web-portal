package org.hisp.dhis.reports;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in element and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of element code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import java.util.ArrayList;
import java.util.List;

public class ReportModel 
{

    public final static String RM_STATIC = "static";
        
    public final static String RM_DYNAMIC_WITH_ROOT = "dynamicwithrootfacility";
        
    public final static String RM_DYNAMIC_WITHOUT_ROOT = "dynamicwithoutroot";
    
    public final static String RM_STATIC_DATAELEMENTS = "STATIC-DATAELEMENTS";
    
    public final static String RM_STATIC_FINANCIAL = "STATIC-FINANCIAL";
    
    public final static String RM_DYNAMIC_DATAELEMENT = "DYNAMIC-DATAELEMENT";
    
    public final static String RM_INDICATOR_FOR_FEEDBACK = "INDICATOR-FOR-FEEDBACK";
    
    public final static String INDICATOR_AGAINST_PARENT = "INDICATOR-AGAINST-PARENT";
    
    public final static String RM_INDICATOR_AGAINST_SIBLINGS = "INDICATOR-AGAINST-SIBLINGS";
    
    public final static String RM_PROGRESSIVE_ORGUNIT = "PROGRESSIVE-ORGUNIT";
    
    public final static String RM_PROGRESSIVE_PERIOD = "PROGRESSIVE-PERIOD";
    
        
    public static List<String> getReportModels()
    {
        List<String> reportModels = new ArrayList<String>();

        reportModels.add( RM_STATIC );
        
        reportModels.add( RM_STATIC_DATAELEMENTS );
        
        reportModels.add( RM_STATIC_FINANCIAL );
        
        reportModels.add( RM_DYNAMIC_WITH_ROOT );

        reportModels.add( RM_DYNAMIC_WITHOUT_ROOT );

        reportModels.add( RM_DYNAMIC_DATAELEMENT );
        
        reportModels.add( RM_INDICATOR_FOR_FEEDBACK );
        
        reportModels.add( INDICATOR_AGAINST_PARENT );
        
        reportModels.add( RM_INDICATOR_AGAINST_SIBLINGS );
        
        reportModels.add( RM_PROGRESSIVE_ORGUNIT );
        
        reportModels.add( RM_PROGRESSIVE_PERIOD );

        return reportModels;            
    }
        
}
