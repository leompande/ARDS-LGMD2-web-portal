package org.hisp.dhis.sqlview;

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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dang Duy Hieu
 * @version $Id ResourceTableNameMap.java Aug 10, 2010$
 */
public class ResourceTableNameMap
{
    private static Map<String, String> resourceNameMap;
    
    private static Map<String, String> ignoredNameMap;

    static
    {
        resourceNameMap = new HashMap<String, String>();
        ignoredNameMap = new HashMap<String, String>();

        resourceNameMap.put( "_cocn", "_categoryoptioncomboname" );
        resourceNameMap.put( "_cs", "_categorystructure" );
        resourceNameMap.put( "_degss", "_dataelementgroupsetstructure" );
        resourceNameMap.put( "_icgss", "_indicatorgroupsetstructure" );
        resourceNameMap.put( "_ous", "_orgunitstructure" );
        resourceNameMap.put( "_ougss", "_orgunitgroupsetstructure" );
        resourceNameMap.put( "_oustgss", "_organisationunitgroupsetstructure" );

        ignoredNameMap.put( "_users", "users" );
        ignoredNameMap.put( "_uinfo", "userinfo" );
        ignoredNameMap.put( "_patient", "patient" );
        ignoredNameMap.put( "_patientid", "patientidentifier" );
        ignoredNameMap.put( "_patientattr", "patientattribute" );
        ignoredNameMap.put( "_relationship", "relationship.*" );
        ignoredNameMap.put( "_caseaggrcondition", "caseaggregationcondition" );
    }

    public static String getResourceNameByAlias( String alias )
    {
        return resourceNameMap.get( alias );
    }
    
    public static String getIgnoredNameByAlias( String alias )
    {
        return ignoredNameMap.get( alias );
    }

    public static Map<String, String> getIgnoredNameMap()
    {
        return ignoredNameMap;
    }

}
