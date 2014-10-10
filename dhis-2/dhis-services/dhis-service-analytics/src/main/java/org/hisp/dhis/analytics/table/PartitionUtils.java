package org.hisp.dhis.analytics.table;

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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.analytics.Partitions;
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.YearlyPeriodType;

/**
 * @author Lars Helge Overland
 */
public class PartitionUtils
{
    private static final YearlyPeriodType PERIODTYPE = new YearlyPeriodType();
    
    private static final String SEP = "_";

    public static List<Period> getPeriods( Date earliest, Date latest )
    {
        List<Period> periods = new ArrayList<Period>();
        
        Period period = PERIODTYPE.createPeriod( earliest );
        
        while ( period != null && period.getStartDate().before( latest ) )
        {
            periods.add( period );            
            period = PERIODTYPE.getNextPeriod( period );
        }
        
        return periods;
    }

    //TODO optimize by including required filter periods only
    
    public static Partitions getPartitions( Period period, String tablePrefix, String tableSuffix, List<String> validPartitions )
    {
        tablePrefix = StringUtils.trimToEmpty( tablePrefix );
        tableSuffix = StringUtils.trimToEmpty( tableSuffix );

        Partitions partitions = new Partitions();
        
        int startYear = year( period.getStartDate() );
        int endYear = year( period.getEndDate() );
        
        while ( startYear <= endYear )
        {
            String name = tablePrefix + SEP + startYear + tableSuffix;            
            partitions.add( name.toLowerCase() );
            startYear++;
        }

        return partitions.prunePartitions( validPartitions );
    }
    
    public static Partitions getPartitions( List<NameableObject> periods, String tablePrefix, String tableSuffix )
    {
        Set<String> partitions = new HashSet<String>();
        
        for ( NameableObject period : periods )
        {
            partitions.addAll( getPartitions( (Period) period, tablePrefix, tableSuffix, null ).getPartitions() );
        }
        
        return new Partitions( new ArrayList<String>( partitions ) );
    }
    
    public static ListMap<Partitions, NameableObject> getPartitionPeriodMap( List<NameableObject> periods, String tablePrefix, String tableSuffix )
    {
        ListMap<Partitions, NameableObject> map = new ListMap<Partitions, NameableObject>();
        
        for ( NameableObject period : periods )
        {
            map.putValue( getPartitions( (Period) period, tablePrefix, tableSuffix, null ), period );
        }
        
        return map;
    }

    /**
     * Creates a mapping between period type name and period for the given periods.
     */
    public static ListMap<String, NameableObject> getPeriodTypePeriodMap( Collection<NameableObject> periods )
    {
        ListMap<String, NameableObject> map = new ListMap<String, NameableObject>();
        
        for ( NameableObject period : periods )
        {
            String periodTypeName = ((Period) period).getPeriodType().getName();
            
            map.putValue( periodTypeName, period );
        }
        
        return map;
    }
    
    /**
     * Returns the year of the given date.
     */
    public static int year( Date date )
    {
        return new Cal( date ).getYear();
    }
    
    /**
     * Returns the max date within the year of the given date.
     */
    public static Date maxOfYear( Date date )
    {
        return new Cal( year( date ), 12, 31 ).time();
    }
}
