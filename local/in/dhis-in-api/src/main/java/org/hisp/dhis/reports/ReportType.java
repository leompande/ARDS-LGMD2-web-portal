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

public class ReportType
{
    public final static String RT_ROUTINE = "Routine";

    public final static String RT_LINELIST = "Linelisting";
    
    public final static String RT_LINELIST_WEB_PORTAL = "Linelisting Web Portal";
    
    public final static String RT_GOI = "GOI";
    
    public final static String RT_FEEDBACK = "Feedback";
    
    public final static String RT_AGGREGATION = "Aggregation";
    
    public final static String RT_PERIODWISEPROGRESS = "PeriodWiseProgress";
    
    public final static String RT_ORGUNITWISEPROGRESS = "OrgUnitWiseProgress";
    
    public final static String RT_CSREVIEW = "CS Review";

    public final static String RT_PHYSICAL_OUTPUT = "PhysicalOutput";
    
    public final static String RT_ADVANCED_REPORT = "Advanced Reports";      
    
    public final static String RT_BULK_REPORT = "Bulk Reports";
    
    public final static String RT_IDSP_REPORT = "IDSP Reports";
    
    public final static String RT_LINELIST_BULK_REPORT = "Linelisting Bulk Reports";
    
    public final static String RT_COLDCHAIN_REPORT = "Cold Chain Reports";
    
    public final static String RT_MD_REPORT = "MD Reports";
    
    public final static String RT_RANKING_REPORT = "Ranking Reports";

    public final static String RT_FEEDBACK_TEMPLATE = "Feedback Template";

    public static List<String> getReportTypes()
    {
        List<String> reportTypes = new ArrayList<String>();

        reportTypes.add( RT_AGGREGATION );
        
        reportTypes.add( RT_FEEDBACK );
        
        reportTypes.add( RT_GOI );
        
        reportTypes.add( RT_LINELIST );
        
        reportTypes.add( RT_ORGUNITWISEPROGRESS );
        
        reportTypes.add( RT_PERIODWISEPROGRESS );
        
        reportTypes.add( RT_ROUTINE );
        
        reportTypes.add( RT_CSREVIEW );

        reportTypes.add( RT_PHYSICAL_OUTPUT );
        
        reportTypes.add( RT_ADVANCED_REPORT );
        
        reportTypes.add( RT_BULK_REPORT );
        
        reportTypes.add( RT_LINELIST_WEB_PORTAL );
        
        reportTypes.add( RT_IDSP_REPORT );
        
        reportTypes.add( RT_LINELIST_BULK_REPORT );
        
        reportTypes.add( RT_COLDCHAIN_REPORT );
        
        reportTypes.add( RT_MD_REPORT );
        
        reportTypes.add( RT_RANKING_REPORT);

        reportTypes.add(RT_FEEDBACK_TEMPLATE);
        
        return reportTypes;
    }
}
