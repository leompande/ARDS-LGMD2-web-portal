package org.hisp.dhis.importexport.action.util;

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

import static org.hisp.dhis.common.Objects.DATADICTIONARY;
import static org.hisp.dhis.common.Objects.DATAELEMENT;
import static org.hisp.dhis.common.Objects.DATAELEMENTGROUP;
import static org.hisp.dhis.common.Objects.DATAELEMENTGROUPSET;
import static org.hisp.dhis.common.Objects.DATASET;
import static org.hisp.dhis.common.Objects.DATAVALUE;
import static org.hisp.dhis.common.Objects.INDICATOR;
import static org.hisp.dhis.common.Objects.INDICATORGROUP;
import static org.hisp.dhis.common.Objects.INDICATORGROUPSET;
import static org.hisp.dhis.common.Objects.INDICATORTYPE;
import static org.hisp.dhis.common.Objects.ORGANISATIONUNIT;
import static org.hisp.dhis.common.Objects.ORGANISATIONUNITGROUP;
import static org.hisp.dhis.common.Objects.ORGANISATIONUNITGROUPSET;
import static org.hisp.dhis.common.Objects.ORGANISATIONUNITLEVEL;
import static org.hisp.dhis.common.Objects.REPORTTABLE;
import static org.hisp.dhis.common.Objects.VALIDATIONRULE;

//import static org.hisp.dhis.common.Objects.ATTRIBUTE;
//import static org.hisp.dhis.common.Objects.ATTRIBUTEGROUP;
//import static org.hisp.dhis.common.Objects.ATTRIBUTEOPTIONGROUP;
//import static org.hisp.dhis.common.Objects.ATTRIBUTEOPTIONS;
//import static org.hisp.dhis.common.Objects.HRDATASET;
//import static org.hisp.dhis.common.Objects.DATAVALUES;
//import static org.hisp.dhis.common.Objects.TRAINING;
//import static org.hisp.dhis.common.Objects.HISTORY;
//import static org.hisp.dhis.common.Objects.INPUTTYPE;
//import static org.hisp.dhis.common.Objects.DATATYPE;
//import static org.hisp.dhis.common.Objects.PERSON;
import static org.hisp.dhis.common.Objects.valueOf;

import java.util.HashMap;
import java.util.Map;

import org.h2.value.DataType;
import org.hisp.dhis.common.Objects;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.History;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.InputType;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.Training;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.validation.ValidationRule;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class ClassMapUtil
{
    private static Map<Objects, Class<?>> classMap;
    
    static
    {
        classMap = new HashMap<Objects, Class<?>>();
        
        classMap.put( DATAELEMENT, DataElement.class );
        classMap.put( DATAELEMENTGROUP, DataElementGroup.class );
        classMap.put( DATAELEMENTGROUPSET, DataElementGroupSet.class );
        classMap.put( INDICATORTYPE, IndicatorType.class );
        classMap.put( INDICATOR, Indicator.class );
        classMap.put( INDICATORGROUP, IndicatorGroup.class );
        classMap.put( INDICATORGROUPSET, IndicatorGroupSet.class );
        classMap.put( DATADICTIONARY, DataDictionary.class );
        classMap.put( DATASET, DataSet.class );
        classMap.put( ORGANISATIONUNIT, OrganisationUnit.class );
        classMap.put( ORGANISATIONUNITGROUP, OrganisationUnitGroup.class );
        classMap.put( ORGANISATIONUNITGROUPSET, OrganisationUnitGroupSet.class );
        classMap.put( ORGANISATIONUNITLEVEL, OrganisationUnitLevel.class );
        classMap.put( VALIDATIONRULE, ValidationRule.class );
        classMap.put( REPORTTABLE, ReportTable.class );
        
        classMap.put( DATAVALUE, DataValue.class );
//        classMap.put( ATTRIBUTE, Attribute.class);
//        classMap.put( ATTRIBUTEOPTIONS, AttributeOptions.class);
//        classMap.put( ATTRIBUTEGROUP, AttributeGroup.class);
//        classMap.put( ATTRIBUTEOPTIONGROUP, AttributeOptionGroup.class);
//        classMap.put( HRDATASET, HrDataSet.class);
//        classMap.put( DATAVALUES, DataValues.class);
//        classMap.put( TRAINING, Training.class);
//        classMap.put( HISTORY, History.class);
//        classMap.put( INPUTTYPE, InputType.class);
//        classMap.put( DATATYPE, DataType.class);
//        classMap.put( PERSON, Person.class);
    }
    
    public static Class<?> getClass( String type )
    {        
        try
        {            
            return classMap.get( valueOf( type ) );
        }
        catch ( IllegalArgumentException ex )
        {
            return null;
        }
    }
}
